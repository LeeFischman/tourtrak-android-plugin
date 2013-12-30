package edu.rit.se;

//import org.apache.cordova.CordovaPlugin;
//import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.apache.cordova.*;

import android.content.res.Resources;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import edu.rit.se.tourtrak.BuildConfig; 
import edu.rit.se.tourtrak.R;

import edu.rit.se.trafficanalysis.TourConfig;
import edu.rit.se.trafficanalysis.TourConfig.TourConfigData;
import edu.rit.se.trafficanalysis.api.ApiClient;
import edu.rit.se.trafficanalysis.api.DcsException;
import edu.rit.se.trafficanalysis.api.Messages;
import edu.rit.se.trafficanalysis.api.Messages.RegisterRiderResponse;
import edu.rit.se.trafficanalysis.tracking.LocationReceiver;
import edu.rit.se.trafficanalysis.tracking.StateBroadcaster;
import edu.rit.se.trafficanalysis.tracking.TrackingService;
import edu.rit.se.trafficanalysis.util.GCMHelper;

/**
 * This is the Tour-Trak Android Java Cordova Plugin. 
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
	 * will be passed to this method. Here we check for the action aka method to call
	 * 
	 * This does not run on the UI Thread but on the WebCore thread.
	 */
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

		 if (action.equals("start")) {
			
			JSONObject msgObj = args.getJSONObject(0);
			Log.d("JSON: ", msgObj.toString());
			String dcsUrl = msgObj.getString("dcsUrl");
			int startTime = msgObj.getInt("startTime");
			int endTime = msgObj.getInt("endTime");
			String tourId = msgObj.getString("tourId");
			String riderId = msgObj.getString("riderId");
			
			this.start(dcsUrl, startTime, endTime, tourId, riderId, callbackContext);
		} else if (action.equals("pauseTracking")) {
			this.pauseTracking(callbackContext);
		} else if (action.equals("resumeTracking")) {
			this.resumeTracking(callbackContext);
		}
		return false;
	}
	
	/**
	 * Will setup the tour configuration and automatically start
	 * tracking the rider at the start time and stop time of the 
	 * tour. 
	 * 
	 * TODO - get the parameters for DCS URL, etc!
	 * @param dcsUrl				Url to the Data Collection Server
	 * @param startTime				Unix time of the tour start time
	 * @param endTime				Unix time of the tour end time
	 * @param tourId				The tour identification number
	 * @param riderId				The rider's unique identification number.
	 * @param callbackContext		The callback context (called on the JS side).
	 */
	private void start(String dcsUrl, int startTime, int endTime, String tourId, 
			String riderId, CallbackContext callbackContext){
		
		Log.d("DCS URL: ", dcsUrl);
		Log.d("START TIME: ", startTime + "");
		Log.d("END TIME: ", endTime + "");
		Log.d("TOUR ID: ", tourId);
		Log.d("RIDER ID: ", riderId);
		
		if(!locationInit){
			
			/* Setup the tour configuration */
			TourConfig cfg = new TourConfig(this.cordova.getActivity().getApplicationContext());
			setupTourConfiguration(cfg, dcsUrl, startTime, endTime, tourId);
			cfg.setRiderId(riderId);
			
			trackingService = new TrackingService();
			stateCaster = new StateBroadcaster(this.cordova.getActivity().getApplicationContext());
			test = new LocationReceiver();
			
			GCMHelper.registerPush(this.cordova.getActivity().getApplicationContext());
			TrackingService.startTracking(this.cordova.getActivity().getApplicationContext());
			locationInit = true;
		}
		callbackContext.success();
	}
	
	/** 
	 * Set up the tour configuration
	 * 
	 * @param cfg			The Tour Configuration Object
	 * @param dcsUrl		Url to the data collection server
	 * @param startTime		The start time of the tour
	 * @param endTime		The end time of the tour
	 * @param tourId		The tour identification number.
	 */
	private void setupTourConfiguration(TourConfig cfg, String dcsUrl, int startTime, int endTime, String tourId) {
		
		if (!cfg.isTourConfigured()) {
			TourConfigData tour = new TourConfigData();

			tour.tour_id = tourId;
			tour.dcs_url = dcsUrl;
			//tour.gcm_sender_id = res.getString(R.string.defaultConfigGcmSenderId);
			tour.start_time = startTime;
			tour.max_tour_time = endTime;
			cfg.setNewTourConfig(tour);
		}
		
	}
	
	/**
	 * Pause tracking the rider
	 * @param callbackContext		The callback context (JS side).
	 */
	private void pauseTracking(CallbackContext callbackContext){
		Log.d("INFO: ", "PAUSE TRACKING");
	}
	
	/**
	 * Resume tracking the rider
	 * @param callbackContext		The callback context (JS side).
	 */
	private void resumeTracking(CallbackContext callbackContext){
		Log.d("RESULE: ", "RESUME");
	}
}
