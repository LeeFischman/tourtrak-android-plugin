tourtrak-android-plugin
=======================

The android location transmitter plugin for TourTrak

###Description
This plugin is for use with the TourTrak mobile application and is used to transmit a rider's location
in the background during the tour. It can be installed as a regular plugin or run as a standalone
application to aid contributing/making future changes.

###Automatic Installation for TourTrak Mobile Application

Navigate to the cordova folder in the TourTrak mobile application project and run the following command:
`cordova plugin add https://github.com/tofferrosen/tourtrak-android-plugin.git`. For additional information, please refer to Cordova or Phonegap documentation.

###Contributing/Developing as a Standalone Application
Simply download the whole repository and import it to Eclipse as an Android project. 
Under `assets/www` exists index.html, which you can use to test calling methods implemented in in the `CDVInterface.java` file under `src/edu/rit/se` directory. 

###How does it work?
The CDVInterface class defines the native methods availble to the cordova interface as exposed in assets/js/CDVInterface.js. It implements the following methods:

* start
* pauseTracking
* resumeTracking

Start tracking takes in the data collection service server URL, the start time of the tour (in GMT unix time, which is seconds since epoch), start time of the beta tour, end time of the tour, end time of the beta tour, the tour id, rider id, and finally the callback context. 

#### Automatic Start/End Tracking
It uses the Android Alarm Manager and sets the wakeup time at the appropriate time with the action to end tracking or start tracking. These events are handled by broadcast receivers (EndTrackingAlarm, StartTrackingAlarm, EndTrackingAlarmBeta, StartTrackingAlarmBeta).

#### Acting as a Location Transmitter in the background
We also use the Android Alarm Manager to set repeating alarms for requesting locations and delivering them to the server. The times are set by the geolocation polling rate and server polling rates given by the TourTrak Server. These are returned by the server to this plugin each time we deliver a location.

#### Issues
We inherited much of this code as part of an Android application from the SE cycl-ops team during 2012-2013, which we converted into a stand-alone Cordova plugin. We fixed some bugs and added some features, but there are still some concerns that we could not fit into our schedule:

* If a user force closes or swipes the application away, the automatic/ending of tracking fails to work.
* We didn't sufficiently test the Beta alarms to our satisfaction, and we recommend adding some unit tests or similar to ensure that the start/end automatically tracking works as expected for both the real and beta alarms. 
* Some code is never being used, and can be cleaned up.

That said, there is absolutely room to refactor as well as adding tests.
