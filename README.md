AirBop-simple-jar BETA
=================

WARNING: This is in the BETA stage so use at your own risk. There will be bugs.

This is a simple jar to make adding AirBop to your app much easier.

## How to add the simple JAR to your app

### 1. Get the JARS that you need

In order to use the AirBop simple jar you need to add the airbop.jar and gcm.jar to you app. These two jars can be found in the jars folder of our [GitHub repository](https://github.com/indigorose/airbop-simple-jar/tree/master/jars).

Download both of the jars and then put them in the `libs` folder of your project.

### 2. Add the GCM requirements to your Android.xml manifest

Next you need to add the requirements to your manifest file. This is described in detail in the [Adding AirBop to Your App](http://airbop.com/tutorials/adding-airbop-to-your-app) tutorial, but in a nutshell the following needs to be added and PACKAGE needs to be replaced with your applications package:

* In the `manifest` tag:

            <!-- GCM requires Android SDK version 2.2 (API level 8) or above. -->
            <!-- The targetSdkVersion is optional, but it's always a good practice
                 to target higher versions. -->
            <uses-sdk android:minSdkVersion="8" android:targetSdkVersion="16"/>
        
            <!-- GCM connects to Google Services. -->
            <uses-permission android:name="android.permission.INTERNET" />
        
            <!-- GCM requires a Google account. -->
            <uses-permission android:name="android.permission.GET_ACCOUNTS" />
        
            <!-- Keeps the processor from sleeping when a message is received. -->
            <uses-permission android:name="android.permission.WAKE_LOCK" />
            
            <!--
             Creates a custom permission so only this app can receive its messages.
        
             NOTE: the permission *must* be called PACKAGE.permission.C2D_MESSAGE,
                   where PACKAGE is the application's package name.
            -->
            <permission
                android:name="PACKAGE.permission.C2D_MESSAGE"
                android:protectionLevel="signature" />
            <uses-permission
                android:name="PACKAGE.permission.C2D_MESSAGE" />
        
            <!-- This app has permission to register and receive data message. -->
            <uses-permission
                android:name="com.google.android.c2dm.permission.RECEIVE" />
            
* Within the `manifest\application` tag the following needs to be added:

                <!--
                  BroadcastReceiver that will receive intents from GCM
                  services and handle them to the custom IntentService.
        
                  The com.google.android.c2dm.permission.SEND permission is necessary
                  so only GCM services can send data messages for the app.
                -->
                <receiver
                    android:name="com.airbop.library.simple.AirBopGCMBroadcastReceiver"
                    android:permission="com.google.android.c2dm.permission.SEND" >
                    <intent-filter>
                        <!-- Receives the actual messages. -->
                        <action android:name="com.google.android.c2dm.intent.RECEIVE" />
                        <!-- Receives the registration id. -->
                        <action android:name="com.google.android.c2dm.intent.REGISTRATION" />
                        <category android:name="PACKAGE" />
                    </intent-filter>
                </receiver>
        
                <!--
                  Application-specific subclass of GCMBaseIntentService that will
                  handle received messages.
        
                  By default, it must be named .GCMIntentService, unless the
                  application uses a custom BroadcastReceiver that redefines its name.
                -->
                <service android:name="com.airbop.library.simple.AirBopGCMIntentService" />
                <service android:name="com.airbop.library.simple.AirBopIntentService"></service>
                
You will notice if you have worked with GCM or AirBop without using the simple JAR that there are three lines that are specific to the simple jar:

                    android:name="com.airbop.library.simple.AirBopGCMBroadcastReceiver"
                <service android:name="com.airbop.library.simple.AirBopGCMIntentService" />
                <service android:name="com.airbop.library.simple.AirBopIntentService"></service>
                
These lines tell GCM to use the airbop.jar for some of the basic GCM task, rather than looking for the hooks within your app.

### 3. Add the AirBop simple jar meta-data to your Android.xml manifest

The next step is to add all of the data that the simple jar needs to your manifest. This will be added within the `manifest\application`

    <!--  AirBop META DATA -->
    <meta-data android:name="AIRBOP_GOOGLE_PROJECT_NUMBER" android:value="@string/gpn" />
    <meta-data android:name="AIRBOP_APP_KEY" android:value="<<YOUR_AIRBOP_APP_KEY>>" />
    <meta-data android:name="AIRBOP_APP_SECRET" android:value="<<YOUR_AIRBOP_APP_SECRET>>" />
    <meta-data android:name="AIRBOP_USE_LOCATION" android:value="false" />
    <meta-data android:name="AIRBOP_DEFAULT_NOTIFICATION_TITLE" android:value="AirBop Client" />
    <meta-data android:name="AIRBOP_NOTIFICATION_ICON" android:resource="@drawable/ic_stat_gcm"/>
    <meta-data android:name="AIRBOP_DEFAULT_NOTIFICATION_CLASS" android:value="<<YOURPACKAGE>>.<<YOURCLASS>>"/>
    
Here are the items in greater detail:

* `AIRBOP_GOOGLE_PROJECT_NUMBER` (Required) Your Google Project Number. This should be a string resource, and within the string resource you should specify the actual number. e.g.

		<meta-data android:name="AIRBOP_GOOGLE_PROJECT_NUMBER" android:value="@string/gpn" />
		
Which refers to the following string resource in a separete xml file:

        <string name="gpn">##########</string>
        
* `AIRBOP_APP_KEY` (Required) Your AirBop app key, found in airbop.com account.

* `AIRBOP_APP_SECRET` (Required) Your AirBop app secret, found in your airbop.com account.

* `AIRBOP_NOTIFICATION_ICON` (Required) This is the resource id of the icon that will be used in the notification.

* `AIRBOP_DEFAULT_NOTIFICATION_CLASS` (Optional) If no URL is specified this is the activity that will be shown when the end-user clicks on the notification. E.g.:

        <meta-data android:name="AIRBOP_DEFAULT_NOTIFICATION_CLASS" android:value="com.airbop.client.DemoActivity"/>

* `AIRBOP_USE_LOCATION` (Optional) value controls whether or not you want the users location data sent to AirBop. The default is false. If you set this to false AirBop will look up the devices location based on IP. If you set this to "true" the following permissions must be added to you manifest as well:

    	<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/> 
    	<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/> 
    	
* `AIRBOP_DEFAULT_NOTIFICATION_TITLE` (Optional) Controls the default title for notifications if none is passed in within the message. This is optional and will default to a blank string.

* `AIRBOP_DEFAULT_NOTIFICATION_HANDLING` (Optional) Whether or not the airbop.jar should handle the notification creation. This defaults to true. If false no notification will be displayed when a message from AirBop is received. You will have to create your own handler to perform any actions. More information on this can be found in the *Custom Message Handling* section.
   
### 4. Register with AirBop and GCM
  
The next step is to register with GCM. A good place to do this would be in the `onCreate()` method of your main activity:

    AirBop mAirBop = new AirBop();
    mAirBop.register(getApplicationContext());
        
### 5. Unregister with AirBop and GCM

Finally you should allow people to opt-out of your notification, if that event you will need to call the following code:

    AirBop mAirBop = new AirBop();
    mAirBop.unRegister(getApplicationContext());
    
### 6. Add the jar to your Proguard setup

If you are proguarding your App (and you should be) add the following to your configuration file:

    -libraryjars .\libs\airbop.jar
    -keep class com.airbop.library.** { *; }
    
### 7. See the sample for more information

If you are confused at all please see the sample location within the `example/AirBopJarDemo` directory of our GitHub repository. It is a working example of how to use the airbop.jar.

## Custom Message Handling

You can let the simple JAR handle all of the messaging and notification for you, or you block the default handling and do everything manually. Here is how you do it:

*    Block the default message handling via the Android.xml manifest file:
 
         <meta-data android:name="AIRBOP_DEFAULT_NOTIFICATION_HANDLING" android:value="false"/>
         
*    Create a `BroadcastReceiver` that will handle the message sent from the AirBop servers. E.g.:

            public class DemoAirBopMessageReceiver extends BroadcastReceiver {
            	 private static final String TAG = "DemoAirBopMessageReceiver";
            	@Override
            	public void onReceive(Context context, Intent intent) {
            		
            		Log.v(TAG, "onReceive");
            		if (intent != null) {  
               		 Intent i =  new Intent(context, DemoActivity.class);
               		 i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               		 
            	    	//Check the bundle for the pay load body and title
            	        Bundle bundle = intent.getExtras();
            	        i.putExtras(bundle);
            	        context.startActivity(i);
            	    }
            	}
            }

*    Register your receiver with AirBop's jar to receive notifications in your manifest:

            <receiver android:name=".DemoAirBopMessageReceiver">
            		<intent-filter>
            				<action android:name="com.airbop.library.simple.GCM_MESSAGE" />
            		</intent-filter>
            </receiver>

*    That's it.

### Getting debug information

If you want to have access to the error and status messages created by the simple jar you will need to register a BroadcastReceiver with the jar, and then process the message in `OnReceive`. This is a rather simple procedure that can be done from your main activity:

    private final BroadcastReceiver mHandleLogMessageReceiver =
            new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	if (intent != null) {
        		String newMessage = intent.getStringExtra(EXTRA_MESSAGE);
        		Log.v(TAG, newMessage);
        		mDisplay.append(newMessage + "\n");
        	}
        }
    };
    
Then in your Activities `onCreate` call `AirBop.registerAirBopLogReceiver` and then register your receiver:

    AirBop.registerAirBopLogReceiver(this, mHandleLogMessageReceiver);
    
Finally in `onDestroy` you will need to unregister your receiver:

    @Override
    protected void onDestroy() {
    	AirBop.unregisterAirBopLogReceiver(this, mHandleLogMessageReceiver);
    	super.onDestroy();
    }
    
### Supported JSON variables

The simple jar supports the following JSON data:

* title - The title for your message, which will be displayed in the notification area. 
* message - The main text that will appear in the notification area.
* url - The URL that you want to display to your end users when they click on the notification.
* image_url - The URL of the image that will be downloaded and used in the notification, as described here: [Tutorial: Using AirBop to Push Images for BigPictureStyle Notifications](http://blog.andromo.com/2012/tutorial-using-airbop-to-push-images-for-bigpicturestyle-notifications/).
* large_icon - A Base64 encoded image that will serve as the large icon for the notification, as described here: [Tutorial: Using AirBop to Send Images in the Message Payload](http://blog.andromo.com/2012/tutorial-using-airbop-to-send-images-in-the-message-payload/).

#### Large Icon Example JSON

    {
        "title": "AirBop",
        "message": "Mark has sent you a message from AirBop",
        "url": "http://www.airbop.com",
        "large_icon" : "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"
    }

#### Image URL Example JSON

    {
        "title": "AirBop",
        "message": "Here is your BigPictureStyle notification",
        "url": "http://www.airbop.com",
        "image_url" : "http://blog.andromo.com/wp-content/uploads/2012/12/a.1003-small-size.jpg"
    }

### Building the jar from source.

If you want to modify the jar and recompile it feel free. Grab the source from our [GitHub repository](https://github.com/indigorose/airbop-simple-jar/tree/master/jars) and have fun with it. 

If you are fixing a bug in the 

To build the jar if you want:

cd into: AirBop-simple-jar/src

    >android update project -p .
    >ant jar

airbop.jar will be built into:

    AirBop-simple-jar\src\bin

