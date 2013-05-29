airbop-simple-jar ALPHA
=================

WARNING: This is in the ALPHA stage so use at your own risk. There will be bugs.

This is a simple jar to make adding airbop to your app much easier.

## How to add the simple JAR to your app

### Get the JARS that you need

In order to use the AirBop simple jar you need to add the airbop.jar and gcm.jar to you app. These two jars can be found in the jars folder of our [GitHub repository](https://github.com/indigorose/airbop-simple-jar/tree/master/jars).

Download both of the jars and then put them in the `libs` folder of your project.

 - ### Add the GCM requirements to your Android.xml manifest

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
                
You will notice if you have worked with GCM ir AirBop without using the simple JAR that there are three lines that are specific to the simple jar:

                    android:name="com.airbop.library.simple.AirBopGCMBroadcastReceiver"
                <service android:name="com.airbop.library.simple.AirBopGCMIntentService" />
                <service android:name="com.airbop.library.simple.AirBopIntentService"></service>
                
These lines tell GCM to use the airbop.jar for some of the basic GCM task, rather then looking for the hooks within your app.

 - ### Add the AirBop simple jar meta-data to your Android.xml manifest

The next step is to add all of the data that the simple jar needs to your manifext. This will be added within the `manifest\application`

    <!--  AirBop META DATA -->
    <meta-data android:name="AIRBOP_GOOGLE_PROJECT_NUMBER" android:value="@string/gpn" />
    <meta-data android:name="AIRBOP_APP_KEY" android:value="<<YOUR_AIRBOP_APP_KEY>>" />
    <meta-data android:name="AIRBOP_APP_SECRET" android:value="<<YOUR_AIRBOP_APP_SECRET>>" />
    <meta-data android:name="AIRBOP_USE_LOCATION" android:value="false" />
    <meta-data android:name="AIRBOP_DEFAULT_NOTIFICATION_TITLE" android:value="AirBop Client" />
    <meta-data android:name="AIRBOP_NOTIFICATION_ICON" android:resource="@drawable/ic_stat_gcm"/>
    <meta-data android:name="AIRBOP_DEFAULT_NOTIFICATION_CLASS" android:value="<<YOURPACKAGE>>.<<YOURCLASS>>"/>
    
Here are the items in greater detail:

* `AIRBOP_GOOGLE_PROJECT_NUMBER` - Your Goolge Project Number. This should be a string resource, and within the string resource you should specifiy the actual number. e.g.

        <string name="gpn">##########</string>
        
* `AIRBOP_APP_KEY` - Your AirBop app key, found in airbop.com account.

* `AIRBOP_APP_SECRET` = Your AirBop app secret, found in your airbop.com account.

* `AIRBOP_USE_LOCATION` value controls whether or not you want the users location data sent to Airbop. The default is false. If you set this to false AirBop will look up the devices location based on IP. If you set this to "true" the following permissions must be added to you manifest as well:

    	<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/> 
    	<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/> 
    	
* `AIRBOP_DEFAULT_NOTIFICATION_TITLE` Controls the default title for notifications if none is passed in within the messaage. This is optional and will default to a blank string.

* `AIRBOP_NOTIFICATION_ICON` This is the resource id of the icon that will be used in the notification.

* `AIRBOP_DEFAULT_NOTIFICATION_CLASS` If no URL is specificed this is the activity that will be shown when the end-user clicks on the notification. E.g:

        <meta-data android:name="AIRBOP_DEFAULT_NOTIFICATION_CLASS" android:value="com.airbop.client.DemoActivity"/>

* `AIRBOP_DEFAULT_NOTIFICATION_HANDLING` Whether or not the airbop.jar should handle the notification creation. This defaults to true. If false no notificaiton will be displayed when a message from AirBop is received. You wull have to create your own handler to perform any actions. More information on this can be found in the *Custom Message Handling* section.
   
 - ### Register with AirBop and GCM
  
The next step is to register with GCM. A good place to do this would be in the `onCreate()` method of your main activity:

    AirBop mAirBop = new AirBop();
    mAirBop.register(getApplicationContext());
        
 - ### Unregister with AirBop and GCM

Finally you should allow people to opt-out of your notification, if that event you will need to call the following code:

    AirBop mAirBop = new AirBop();
    mAirBop.unRegister(getApplicationContext());
    
 - ### See the sample for more information

If you are confused at all please see the sample location within the `example/AirBopJarDemo` diurectory of our github repository. It is a working example of how to use the airbop.jar.

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

### Building the jar from source.

If you want to modify the jar and recompile it feel free. Grab the source from our [github repository](https://github.com/indigorose/airbop-simple-jar/tree/master/jars) and have fun with it. 

If you are fixing a bug in the 

To build the jar if you want:

cd into: airbop-simple-jar/src

    >android update project -p .
    >ant jar

airbop.jar will be built intp:

    airbop-simple-jar\src\bin

