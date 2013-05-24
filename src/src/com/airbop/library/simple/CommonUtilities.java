/*
 * Copyright 2012 Indigo Rose Software Design Corporation
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.airbop.library.simple;


//import static com.airbop.library.simple.CommonUtilities.displayMessage;

import java.util.Set;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Helper class providing methods and constants common to other classes in the
 * app.
 */
public final class CommonUtilities {

    /**
     * Base URL of the Demo Server (such as http://my_host:8080/gcm-demo)
     */ 
    static final String SERVER_URL = "http://www.airbop.com/api/v1/";

    /**
     * Google Project Number registered to use GCM (from your Google API Console).
     */
    //static final String GOOGLE_PROJECT_NUMBER = <<REPLACE_ME>>;
    
    /**
     * AirBop App key to identify this app
     */
    //static final String AIRBOP_APP_KEY = <<REPLACE_ME>>;
        
    /**
     * AIRBOP_APP_SECRET App key to identify this app shhhh
     */
    //static final String AIRBOP_APP_SECRET = <<REPLACE_ME>>;
           
    /** Should we send the location to the AirBopServer
     * If you set this value to true, you also need to uncomment the following manifest permissions:
     * android.permission.ACCESS_FINE_LOCATION
     * android.permission.ACCESS_COARSE_LOCATION
     */
   // static final boolean USE_LOCATION = false;
    
    /**
     * Should we use the IntentService or the AsyncTask
     */
    static final boolean USE_SERVICE = true;
    
    /**
     * Tag used on log messages.
     */
    static final String TAG = "AirBop-Simple-Library";

    /**
     * Intent used to display a message in the screen.
     */
    public static final String DISPLAY_MESSAGE_ACTION =
            "com.airbop.library.simple.DISPLAY_MESSAGE";

    /**
     * Intent's extra that contains the message to be displayed.
     */
    public static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
    
    static void displayMessageFromIntent(Context context, Intent intent) {
       if (intent != null) {
    	   Bundle bundle = intent.getExtras();
    	   if (bundle != null) {
    		   Set<String> keys = bundle.keySet();
    		   if (keys != null) {
    			   for (String key : keys) { 
    				   Object o = bundle.get(key);
    				   if (o != null) {
    					   displayMessage(context, "Key: " + key + " value: " + o);
    				   }
    			   }
    		   }
    	   } else {
    		   displayMessage(context, "Extras are null");
    	   }
       } else {
    	   displayMessage(context, "Intent is null");
       }
    }
    
    /************************
     * Language helpers
     */
    
    /**
     * Simple helper that gets the location criteria that we want. 
     * @return
     */
    public static Criteria getCriteria() {
    	if (true) {
	    	Criteria criteria = new Criteria();
		    criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		    criteria.setPowerRequirement(Criteria.POWER_LOW);
		    criteria.setAltitudeRequired(false);
		    criteria.setBearingRequired(false);
		    criteria.setSpeedRequired(false);
		    criteria.setCostAllowed(true);
		    
		    return criteria;
    	} 
    	return null;
    }
    
    /**
     * Get the last location from the LocationManager, if it's available, if not
     * return null.
     * @param appContext
     * @return
     */
    public static Location getLastLocation(final Context appContext) {
    	Location location = null;
    	if (true) {
	    	Criteria criteria = getCriteria();
	    	LocationManager locationManager = (LocationManager)appContext.getSystemService(Context.LOCATION_SERVICE);
		    if (locationManager != null) {
		    	String provider = locationManager.getBestProvider(criteria, true);
			    location = locationManager.getLastKnownLocation(provider); 
			    
			    if (location != null) {
			    	displayMessage(appContext
			    			, String.format(AirBopStrings.airbop_got_last_location
			    					, location.getLatitude()
			    					, location.getLongitude()));
			    }
	    	}
    	}
	    return location;
    }
    
    /** 
     * Get the current location from the location manager, and when we get it
     * post that information to the Airbop servers
     * @param appContext
     * @param regId
     * @return
     */
    public static boolean getCurrentLocation(LocationListener locationListener
    		, final Context appContext
    		) {
    	if (true) {
	    	Criteria criteria = getCriteria();
	    	LocationManager locationManager = (LocationManager)appContext.getSystemService(Context.LOCATION_SERVICE);
		    if (locationManager != null) {
		    	String provider = locationManager.getBestProvider(criteria, true);
		    	
	    		locationManager.requestLocationUpdates(provider, 2000, 10,
	                    locationListener);    
	    		// We've posted so let the caller know
	    		return true;
		    }
    	}
	    // We couldn't get the location manager so let the caller know
	    return false;
    }
    
    public static class AirBopManifestSettings {
    	public String mGoogleProjectNumber = "";
    	public String mAirBopAppkey = "";
    	public String mAirBopAppSecret = "";
    	public boolean mUseLocation = false;
    	public String mDefaultNotificationTitle = "";
    	public int mNotificationIcon = 0;
    	public String mDefaultNotificationClass = "";
    }
    
    static final String AIRBOP_GOOGLE_PROJECT_NUMBER = "AIRBOP_GOOGLE_PROJECT_NUMBER";
	static final String AIRBOP_APP_KEY = "AIRBOP_APP_KEY";
	static final String AIRBOP_APP_SECRET = "AIRBOP_APP_SECRET";
	static final String AIRBOP_USE_LOCATION = "AIRBOP_USE_LOCATION";
	static final String AIRBOP_DEFAULT_NOTIFICATION_TITLE = "AIRBOP_DEFAULT_NOTIFICATION_TITLE";
	static final String AIRBOP_NOTIFICATION_ICON = "AIRBOP_NOTIFICATION_ICON";
	static final String AIRBOP_DEFAULT_NOTIFICATION_CLASS = "AIRBOP_DEFAULT_NOTIFICATION_CLASS";
	
    public static AirBopManifestSettings loadDataFromManifest(Context application_context) {
    	AirBopManifestSettings airbop_settings = new AirBopManifestSettings();
    	
		if (application_context != null) {
			Log.v(TAG, "loadDataFromManifest: application_context != null");
			ApplicationInfo ai;
			try {
				ai = application_context.getPackageManager().getApplicationInfo(
						application_context.getPackageName(), PackageManager.GET_META_DATA);
			
				if (ai != null) {
					Log.v(TAG, "loadDataFromManifest: ai != null");
					Log.v(TAG, "application_context.getPackageName(): "
							+ application_context.getPackageName());
					Bundle app_bundle=ai.metaData;
					if (app_bundle != null) {
						Log.v(TAG, "app_bundle: "+ app_bundle);
						if (airbop_settings != null){
							
							Log.v(TAG, "app_bundle: airbop_settings != null");
							
							airbop_settings.mGoogleProjectNumber = app_bundle.getString(AIRBOP_GOOGLE_PROJECT_NUMBER);
							Log.v(TAG, "mGoogleProjectNumber: " + airbop_settings.mGoogleProjectNumber);
							
							airbop_settings.mAirBopAppkey = 
								app_bundle.getString(AIRBOP_APP_KEY);
							Log.v(TAG, "mAirBopAppkey: " + airbop_settings.mAirBopAppkey);
							
							airbop_settings.mAirBopAppSecret = 
								app_bundle.getString(AIRBOP_APP_SECRET);
							Log.v(TAG, "mAirBopAppSecret: " + airbop_settings.mAirBopAppSecret);
							
							airbop_settings.mUseLocation = 
								app_bundle.getBoolean(AIRBOP_USE_LOCATION, false);
							Log.v(TAG, "mUseLocation: " + airbop_settings.mUseLocation);
							
							airbop_settings.mDefaultNotificationTitle= 
								app_bundle.getString(AIRBOP_DEFAULT_NOTIFICATION_TITLE);
							Log.v(TAG, "mDefaultNotificationTitle: " + airbop_settings.mDefaultNotificationTitle);
							
							airbop_settings.mNotificationIcon= 
								app_bundle.getInt(AIRBOP_NOTIFICATION_ICON);
							Log.v(TAG, "mNotificationIcon: " + airbop_settings.mNotificationIcon);
							
							airbop_settings.mDefaultNotificationClass= 
								app_bundle.getString(AIRBOP_DEFAULT_NOTIFICATION_CLASS);
							Log.v(TAG, "mDefaultNotificationClass: " + airbop_settings.mDefaultNotificationClass);
							
						}						
					}
				}
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return airbop_settings;
	}
    
    public static class AirBopStrings {
    	static final String airbop_getting_location = "Going to get current location from the location manager.";
    	static final String airbop_gcm_register_attempt = "Attempting to register with the GCM servers.";
    	static final String airbop_already_registered = "Device is already registered on AirBop server.";
    	static final String airbop_reg_thread_running = "WARNING: Registration thread already running.";
    	static final String airbop_service_registration_complete = "The AirBopIntentService has finished registering.";
    	static final String airbop_service_unregistration_complete = "The AirBopIntentService has finished unregistering.";
    	static final String airbop_got_current_location = "Got current location. Latitude : %1$s Longitude : %2$s";
    	static final String airbop_got_last_location = "Got last location. Latitude : %1$s Longitude : %2$s";
    	static final String airbop_gcm_registered = "GCM: device successfully registered.";
    	static final String airbop_gcm_unregistered = "GCM: device successfully unregistered.";
    	static final String airbop_message = "AirBop: You\'ve got a message.";
    	static final String airbop_gcm_deleted = "GCM server: server deleted %1$d pending messages.";
    	static final String airbop_gcm_error = "GCM: error (%1$s).";
    	static final String airbop_gcm_recoverable_error = "GCM: recoverable error (%1$s).";
    	static final String airbop_unreg_thread_running = "WARNING: Unregistration thread already running.";
    	static final String airbop_server_registering = "Trying (attempt %1$d/%2$d) to register device on AirBop Server.";
    	static final String airbop_server_registered = "AirBop: The device has been successfully registered.";
    	static final String airbop_request_error = "ERROR: request could not be processed by AirBop: %1$s - %2$s";
    	static final String airbop_server_reg_failed = "Failed to register: Post failed with error code: %1$s %2$s";
    	static final String airbop_server_reg_failed_401 = "Failed to register, server error message: %1$s";
    	static final String airbop_server_reg_failed_timeout = "Failed to register, connection timeout, server error message: %1$s";
    	static final String airbop_server_register_error = "Could not register device on AirBop Server after %1$d attempts.";
    	static final String airbop_unregister_device = "Attempting to unregister device from AirBop server.";
    	static final String airbop_server_unregistered = "AirBop: successfully unregistered and removed device.";
    	static final String airbop_server_unregister_error = "Could not unregister device on AirBop Server (%1$s).";
    }
 
}
