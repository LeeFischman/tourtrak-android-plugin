package edu.rit.se.trafficanalysis.tracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import edu.rit.se.trafficanalysis.TourConfig;
import edu.rit.se.trafficanalysis.util.AlarmUtil;

/**
 * Class receives broadcasts for when the tracking
 * service should be stopped if it is running.
 *
 */
public class EndTrackingAlarm extends BroadcastReceiver {

	private static final String END_TRACKING_ACTION = "edu.rit.se.trafficanalysis.endTracking";
	private static final String END_TRACKING_ACTION_BETA = "edu.rit.se.trafficanalysis.endTrackingBeta";

	private static long startTime = 0;
	private static long startTimeBeta = 0;
	private static long endTime = 0;
	private static long endTimeBeta = 0;
	
	
	public static void storeTimes(long newStartTime, long newStartTimeBeta, long newEndTime, long newEndTimeBeta) {
		startTime = newStartTime;
		startTimeBeta = newStartTimeBeta;
		endTime = newEndTime;
		endTimeBeta = newEndTimeBeta;
	}
	
	/**
	 * Schedules an alarm that signifies that the tour
	 * is over and tracking should be stopped.
	 * 
	 * @param context Application context.
	 * @param endTourTime - The unix time in MS (GMT) since epoch when tour is finished/ended
	 */
	public static void setAlarm(Context context, long endTourTime, boolean beta) {
		AlarmUtil.setAlarm(context, END_TRACKING_ACTION, endTourTime);
	}

	public static void cancelAlarm(Context context) {
		AlarmUtil.cancelAlarm(context, END_TRACKING_ACTION);
		
		if(System.currentTimeMillis() < endTime*1000) {
			StartTrackingAlarm.setAlarm(context, startTime, false);
			EndTrackingAlarm.setAlarm(context, endTime, false);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("END TRACKING", "In on receive");
		TrackingService.pauseTracking(context);
		context.stopService(new Intent(context, TrackingService.class));
	}
}
