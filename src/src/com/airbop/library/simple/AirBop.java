package com.airbop.library.simple;

import static com.airbop.library.simple.CommonUtilities.SERVER_URL;
import static com.airbop.library.simple.CommonUtilities.USE_SERVICE;
import static com.airbop.library.simple.CommonUtilities.GCM_MESSAGE_ACTION;
import static com.airbop.library.simple.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.airbop.library.simple.CommonUtilities.displayMessage;

import java.lang.ref.WeakReference;

import android.support.v4.content.LocalBroadcastManager;
import com.airbop.library.simple.CommonUtilities.AirBopManifestSettings;
import com.airbop.library.simple.CommonUtilities.AirBopStrings;
import com.google.android.gcm.GCMRegistrar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class AirBop implements AirBopRegisterTask.RegTaskCompleteListener, LocationListener {
	
	/*
	 	<meta-data android:name="AIRBOP_GOOGLE_PROJECT_NUMBER" android:value="998229458551" />
    	<meta-data android:name="AIRBOP_APP_KEY" android:value="7b483fb1-cc4c-4bab-b36c-5019424423d5" />
    	<meta-data android:name="AIRBOP_APP_SECRET" android:value="8c52db2822ca2f5579a71f5c64d29c68c1fca8927cc83c9f9bfe5478e56d1412" />
        <meta-data android:name="AIRBOP_USE_LOCATION" android:value="false" />
	 */
	
	//21 days in milliseconds
	public static final long AIRBOP_DEFAULT_ON_SERVER_LIFESPAN_MS =
            1000L * 3600L * 24L * 21L;
	
	private static final String TAG = "AirBop";
	
	private AirBopManifestSettings mAirBopSettings = null;
	private AirBopServerUtilities mServerData = null;
	private AirBopRegisterTask  mRegisterTask = null;
	private boolean mServiceRunning = false;
	private AirBopRegisterReceiver mRegisterReceiver = null;
	private AsyncTask<Void, Void, Void> mUnRegisterTask = null;
	
	
	private WeakReference<Context> mAppContext = null;
	
	private Context getContext() {
		Context application_context = null;
		if (mAppContext != null) {
			application_context = mAppContext.get();
		}
		return application_context;
	}
	
	public static void registerAirBopMessageReceiver(Context application_context
			, BroadcastReceiver gcm_receiver) {
		LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(application_context);
        if (lbm != null) {
        	lbm.registerReceiver(gcm_receiver
        			, new IntentFilter(GCM_MESSAGE_ACTION));
        }
	}
	
	public static void registerAirBopLogReceiver(Context application_context
			, BroadcastReceiver log_receiver) {
		LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(application_context);
        if (lbm != null) {
        	lbm.registerReceiver(log_receiver
        			, new IntentFilter(DISPLAY_MESSAGE_ACTION));
        }
	}
	
	public static void unregisterAirBopMessageReceiver(Context application_context
			, BroadcastReceiver gcm_receiver) {
		LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(application_context);
        if (lbm != null) {
        	lbm.unregisterReceiver(gcm_receiver);
        }
	}
	
	public static void unregisterAirBopLogReceiver(Context application_context
			, BroadcastReceiver log_receiver) {
		LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(application_context);
        if (lbm != null) {
        	lbm.unregisterReceiver(log_receiver);
        }
	}
	
	public void register(Context application_context) {
		
		if (application_context != null) {
			mAppContext = new WeakReference<Context>(application_context);
		}
		
		initialize(application_context);
		if (mAirBopSettings.mUseLocation) {
    		
    		Location location = CommonUtilities.getLastLocation(application_context);
    		if (location == null) {
    			// We didn't get the location
    			displayMessage(application_context, AirBopStrings.airbop_getting_location);
    			// We have to query the Location Manager for the location
    			// and get the result in onLocationChanged
    			CommonUtilities.getCurrentLocation(this
	    				, application_context);
    		} else {    			
    			//Save the data to the prefs
    			mServerData.mLocation = location;
    			mServerData.saveCurrentDataToPrefs(application_context);
    			//register
        		internalRegister(application_context);
    		}
    	} else {
    		//Remove any old location data
    		AirBopServerUtilities.clearLocationPrefs(application_context);
    		//Save the label if it's there
    		mServerData.saveCurrentDataToPrefs(application_context);
    		//register
    		internalRegister(application_context);
    	}
	}
	
	
    
    private  void internalRegister(Context appContext) {	
     	
    	final String regId = GCMRegistrar.getRegistrationId(appContext);
    	Log.v(TAG,"internalRegister regId: " + regId);
    	if (regId.equals("")) {
        	// We don't have a REG ID from GCM so let's ask for one. 
        	// The response from the GCM server will trigger: 
        	// GCMIntentService.onRegistered() where we will then register with
        	// AiRBop's servers.
    		displayMessage(appContext, AirBopStrings.airbop_gcm_register_attempt);
    		Log.v(TAG,"mGoogleProjectNumber: " + mAirBopSettings.mGoogleProjectNumber);
    		GCMRegistrar.register(appContext, mAirBopSettings.mGoogleProjectNumber);
        } else {
        	
            // We have a regID from the GCM, now check to see if
        	// we have successfully registered with AirBop's servers:
            if (GCMRegistrar.isRegisteredOnServer(appContext)) {
                // This device is already registered with GCM and with the AirBop
            	// servers, nothing to do here.
            	displayMessage(appContext, AirBopStrings.airbop_already_registered);
            
            	return;
            } else if (USE_SERVICE) {
            	//Register with a service
            	internalRegisterService(regId, appContext);
            } else if (mRegisterTask == null){
            	//Register with 
            	internalRegisterAsyncTask(regId, appContext);	    	 	
            } else {
            	displayMessage(appContext, AirBopStrings.airbop_reg_thread_running);
            }
        }
    }
    
    private  void internalRegisterService(final String regId, Context appContext) {	
    	if (appContext != null) {
	    	if (mServiceRunning == false) {
	        	// We have previously gotten the regID from the GCM server, but
	        	// when we tried to register with AirBop in the
	        	// GCMIntentService.onRegistered() callback it failed, so let's try again
	        	
	            // This will be done outside of the GUI thread.
	        	
	            if (mRegisterReceiver == null) {
	    	 		// Register receiver
					registerAirBopRegisterReceiver(appContext);
	    	 	}
	    	 	
	    	 	
		 		Intent intent = new Intent(appContext, AirBopIntentService.class);
				intent.putExtra(AirBopIntentService.BUNDLE_REG_ID, regId);
				intent.putExtra(AirBopIntentService.BUNDLE_REGISTRATION_TASK, true);
									
				mServiceRunning = true;
				// Start Service		
				appContext.startService(intent);
	    	 	
	        } else {
	        	displayMessage(appContext, AirBopStrings.airbop_reg_thread_running);
	        }
    	}
    }
    
    private  void internalRegisterAsyncTask(final String regId, Context appContext) {	
    	if (appContext != null) {
	    	if (mRegisterTask == null){
	            
	        	// We have previously gotten the regID from the GCM server, but
	        	// when we tried to register with AirBop in the
	        	// GCMIntentService.onRegistered() callback it failed, so let's try again
	        	
	            // This will be done outside of the GUI thread.
	        	
	            // It's also necessary to cancel the thread onDestroy(),
	            // hence the use of AsyncTask instead of a raw thread.        	
	    		mServerData.mRegId = regId;
	    		mRegisterTask = new AirBopRegisterTask(this
	    			 , appContext
	    			 , regId
	    			 , mServerData);
	    	 	mRegisterTask.execute(null, null, null);
	
	    	 	
	        } else {
	        	displayMessage(appContext, AirBopStrings.airbop_reg_thread_running);
	        }
    	}
    }
    
    public void unRegister (Context application_context) {
		
		if (application_context != null) {
			mAppContext = new WeakReference<Context>(application_context);
		}
		
		initialize(application_context);
    	
    	if (false) {
    		//Use the Service
    		internalUnRegisterService(application_context);
    	} else {
    		//use the ASYNC TASK
    		internalUnRegisterAsyncTask(application_context);
    	}
    }
    
    private void internalUnRegisterService(Context application_context) {
    	
    	if (application_context != null) {
			//Use the Service
	    	if (mServiceRunning == false) {	
	    		final String regId = GCMRegistrar.getRegistrationId(application_context);
		    	// Only bother if we actually have a regID from GCM, otherwise
		    	// there is nothing to unregister
		        if (!regId.equals("")) {
		    		if (mRegisterReceiver == null) {
		    	 		// Register receiver
						registerAirBopRegisterReceiver(application_context);
		    	 	}
		    	 		    	 	
			 		Intent intent = new Intent(application_context, AirBopIntentService.class);
					intent.putExtra(AirBopIntentService.BUNDLE_REG_ID, regId);
					intent.putExtra(AirBopIntentService.BUNDLE_REGISTRATION_TASK, false);
										
					mServiceRunning = true;
					
					// Start Service		
					application_context.startService(intent);
		        }
	    	} else {
	    		displayMessage(application_context
	    				, AirBopStrings.airbop_unreg_thread_running);
	    	}
    	}
    }
    
    private void internalUnRegisterAsyncTask(final Context application_context) {
    	// Try to unregister, but not in the UI thread.
        // It's also necessary to cancel the thread onDestroy(),
        // hence the use of AsyncTask instead of a raw thread.
    	
    	if (application_context != null) {
			//use the ASYNC TASK
	    	if (mUnRegisterTask == null) {
		    	final String regId = GCMRegistrar.getRegistrationId(application_context);
		    	// Only bother if we actually have a regID from GCM, otherwise
		    	// there is nothing to unregister
		        if (!regId.equals("")) {
			    	//final Context context = this;
			        mUnRegisterTask = new AsyncTask<Void, Void, Void>() {
			
			            @Override
			            protected Void doInBackground(Void... params) {
			            	
			                boolean unregistered = AirBopServerUtilities.unregister(application_context
			                        		, regId);
			                // If this worked unregister from the GCM servers
			                if (unregistered) {
			                    GCMRegistrar.unregister(application_context);
			                }
			                return null;
			            }
			
			            @Override
			            protected void onPostExecute(Void result) {
			            	mUnRegisterTask = null;
			            }
			
			        };
			        mUnRegisterTask.execute(null, null, null);
		        }
	    	} else {
	    		displayMessage(application_context
	    				, AirBopStrings.airbop_unreg_thread_running);
	    	}
    	}
    }
	
	//PRIVATE
	
	
	private void initialize(Context application_context) {
		if (application_context != null) {
			mAirBopSettings = CommonUtilities.loadDataFromManifest(application_context);

			checkNotNull(mAirBopSettings.mGoogleProjectNumber
					, CommonUtilities.AIRBOP_GOOGLE_PROJECT_NUMBER);
			checkNotNull(mAirBopSettings.mAirBopAppkey
					, CommonUtilities.AIRBOP_APP_KEY);
			checkNotNull(mAirBopSettings.mAirBopAppSecret
					, CommonUtilities.AIRBOP_APP_SECRET);
			
			// Make sure the device has the proper dependencies.
	        GCMRegistrar.checkDevice(application_context);
	        
	        
	        // Make sure the manifest was properly set - comment out this line
	        // while developing the app, then uncomment it when it's ready.
	        GCMRegistrar.checkManifest(application_context);  
	        
	        //Ser the AirBop registration lifespan to be 21 days. This means that after 14 days
	        //this client will attempt to reregister with AirBop. 
	        //Make sure that this value is positive, otherwise you will run into issues
	        GCMRegistrar.setRegisterOnServerLifespan(application_context, AIRBOP_DEFAULT_ON_SERVER_LIFESPAN_MS);
	        
	        mServerData = AirBopServerUtilities.fillDefaults("");
	        mServerData.mLabel = "AirBop Sample";
		}
	}

	private void checkNotNull(Object reference, String name) {
        if (reference == null) {
            throw new NullPointerException(
                   "Please set the %1$s value in the manifest and recompile the app.".format(name));
        }
    }
	
private void registerAirBopRegisterReceiver(Context application_context) {
		
		if ((application_context != null) && (mRegisterReceiver == null)) {
			//Receiver
			IntentFilter filter = new IntentFilter(AirBopIntentService.ACTION_REGISTRATION_PROCESSED);

			filter.addCategory(Intent.CATEGORY_DEFAULT);
			mRegisterReceiver = new AirBopRegisterReceiver();
			application_context.registerReceiver(mRegisterReceiver, filter);
		}
	}

	protected void unregisterAirBopRegisterReceiver(Context application_context) {
		if ((application_context != null) && (mRegisterReceiver != null)) {
			
			application_context.unregisterReceiver(mRegisterReceiver);
			mRegisterReceiver = null;
		}
	}
	
	public class AirBopRegisterReceiver extends BroadcastReceiver {
		

		@Override
		public void onReceive(Context context, Intent intent) {
			
			mServiceRunning = false;
			
			boolean registration_task = intent.getBooleanExtra(AirBopIntentService.BUNDLE_REGISTRATION_TASK, true);
			if (registration_task) {
				displayMessage(context, AirBopStrings.airbop_service_registration_complete);
			} else {
				displayMessage(context, AirBopStrings.airbop_service_unregistration_complete);
			}
			
		}
	}
	
	private void unregisterFromLocationManager() {
		Context appContext = getContext();
		if (appContext != null) {
			LocationManager locationManager = (LocationManager)appContext.getSystemService(
					Context.LOCATION_SERVICE);
		    if (locationManager != null) {
		    	locationManager.removeUpdates(this);
		    }
		}
	}
	
	//*****************************************************
	// Location Listener
	public void onLocationChanged(Location location) {
		// Unregister from location manager
		unregisterFromLocationManager();
		

		displayMessage(getContext()
				, String.format(AirBopStrings.airbop_got_current_location
						, location.getLatitude()
						, location.getLongitude()));
			
		// Set the location and save the data so that the intent services can read
		// it
		mServerData.mLocation = location;
		mServerData.saveCurrentDataToPrefs(getContext());
		//register
		internalRegister(getContext());
		
	}



	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub	
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub	
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	//**************************************************************


	public void onTaskComplete() {
		// We have registered
		mRegisterTask = null;		
		
	}
} 
