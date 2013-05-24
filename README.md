airbop-simple-jar ALPHA
=================

WARNING: This is in the ALPHA stage so use at your own risk. There will be bugs.

This is a simple jar to make adding airbop to your app much easier.

To build the jar if you want:
cd into: airbop-simple-jar/src

    >android update project -p .
    >ant jar

airbop.jar will be built in:

    airbop-simple-jar\src\bin

All right I got the jar sort of working. You need to include:

    airbop.jar
    gcm.jar

Add the following to your manifest:

 <!--  AirBop META DATA -->
    	<meta-data android:name="AIRBOP_GOOGLE_PROJECT_NUMBER" android:value="@string/gpn" />
    	<meta-data android:name="AIRBOP_APP_KEY" android:value="<<YOUR_AIRBOP_APP_KEY>>" />
    	<meta-data android:name="AIRBOP_APP_SECRET" android:value="<<YOUR_AIRBOP_APP_SECRET>>" />
        <meta-data android:name="AIRBOP_USE_LOCATION" android:value="false" />
        <meta-data android:name="AIRBOP_DEFAULT_NOTIFICATION_TITLE" android:value="AirBop Client" />
        <meta-data android:name="AIRBOP_NOTIFICATION_ICON" android:resource="@drawable/ic_stat_gcm"/>
        <meta-data android:name="AIRBOP_DEFAULT_NOTIFICATION_CLASS" android:value="<<YOURPACKAGE>>"/>
        
        <receiver
            android:name="com.airbop.library.simple.AirBopGCMBroadcastReceiver"
            android:permission="com.google.android.c2dm.permission.SEND" >
            <intent-filter>
                <!-- Receives the actual messages. -->
                <action android:name="com.google.android.c2dm.intent.RECEIVE" />
                <!-- Receives the registration id. -->
                <action android:name="com.google.android.c2dm.intent.REGISTRATION" />
                <category android:name="<<YOUR PACKAGE HERE>>" />
            </intent-filter>
        </receiver>
        
        <service android:name="com.airbop.library.simple.AirBopGCMIntentService" />
        <service android:name="com.airbop.library.simple.AirBopIntentService"></service>
        
        
Then call the following from your Activity:

    AirBop mAirBop = new AirBop();
    mAirBop.register(getApplicationContext());
    
and:

    AirBop mAirBop = new AirBop();
    mAirBop.unRegister(getApplicationContext());
    
