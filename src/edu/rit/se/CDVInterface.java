package edu.rit.se;

//import org.apache.cordova.CordovaPlugin;
//import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import org.apache.cordova.*;

import android.content.res.Resources;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import edu.rit.se.BuildConfig; 
import edu.rit.se.R;

import edu.rit.se.trafficanalysis.TourConfig;
import edu.rit.se.trafficanalysis.TourConfig.TourConfigData;
import edu.rit.se.trafficanalysis.api.Messages;
import edu.rit.se.trafficanalysis.tracking.LocationReceiver;
import edu.rit.se.trafficanalysis.tracking.StateBroadcaster;
import edu.rit.se.trafficanalysis.tracking.TrackingService;

/**
 * This is the Tour-Trak Android Java cordova plugin. 
 * 
 * Acts as a location transmitter in the background of the device,
 * sending location updates of the rider as he or she rides through 
 * the tour to the Data Collection Server. 
 * 
 * @author Christoffer Rosen (cbr4830@rit.edu)
 * @author Ian Graves 
 *
 */

public class CDVInterface extends CordovaPlugin {

	private final static String TAG = CDVInterface.class.getSimpleName();
	private final static String DCS_URL = "http://devcycle.se.rit.edu/";

	private String mHelloTo = "World";
	private Messages.LocationUpdate loc = null;

	private boolean locationInit = false;
	private LocationListener locationListener = null;
	private TrackingService trackingService = null;
	private LocationReceiver test = null;
	private StateBroadcaster stateCaster= null;

	// Acquire a reference to the system Location Manager
	LocationManager locationManager;

	/**
	 * JavaScript will fire off a plugin request to the native side (HERE) and 
	 * will be passed to this method.
	 * 
	 * This does not run on the UI Thread but on the WebCore thread.
	 *
	 * @param action        The action to execute.
	 * @param args          JSONArry of arguments for the plugin.
	 * @param callbackId    The callback id used when calling back into JavaScript.
	 * @return              A PluginResult object with a status and message.
	 */
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		Log.d("HI!", "HI!");
		if (action.equals("echo")) {
			String message = args.getString(0); 
			Log.d("ECHO ", message);
			this.echo(message, callbackContext);
			return true;
		}
		return false;
	}

	private void echo(String message, CallbackContext callbackContext) {
		if (message != null && message.length() > 0) { 
			callbackContext.success(message);
		} else {
			callbackContext.error("Expected one non-empty string argument.");
		}
	}

	/** 
	 * Set up the default configuration:
	 * I wonder how necessary this really is.
	 * @param cfg	TourConfig configuration
	 */
	public void setupDefaultConfig(TourConfig cfg) {
		if (!cfg.isTourConfigured()) {
			TourConfigData tour = new TourConfigData();

			// Get a handle to the system's resources
			Resources res = this.cordova.getActivity().getApplicationContext().getResources();

			tour.tour_id = res.getString(R.string.defaultConfigRaceId);
			tour.tour_name = res.getString(R.string.defaultConfigRaceName);
			tour.tour_organization = res.getString(R.string.defaultConfigTourOwner);
			tour.tour_logo = res.getString(R.string.defaultConfigTourLogo);
			tour.tour_url = res.getString(R.string.defaultConfigTourUrl);
			tour.dcs_url = res.getString(R.string.defaultConfigServerUrl);
			tour.gcm_sender_id = res.getString(R.string.defaultConfigGcmSenderId);

			if (BuildConfig.DEBUG) {
				tour.dcs_url = DCS_URL;
				tour.start_time = System.currentTimeMillis() + 60000 * 3;
				tour.max_tour_time = 30000000;
			} else {
				tour.start_time = Long.parseLong(res.getString(
						R.string.defaultConfigRaceStartTime));
				tour.max_tour_time = Long.parseLong(res.getString(
						R.string.defaultConfigRaceMaxTime));
			}

			cfg.setNewTourConfig(tour);
		}
	}
}
