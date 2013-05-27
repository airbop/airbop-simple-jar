package com.airbop.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import static com.airbop.library.simple.CommonUtilities.displayMessage;
import android.util.Log;

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
	 	   	/*if (bundle != null) {
	 	   		//Display the standard AirBop Bundle details
	 	   		displayMessage(context, "message: " + bundle.getString("message")+ "\n");   			 	   		
	 	   		displayMessage(context, ("title: " + bundle.getString("title")+ "\n");	
	 	   		displayMessage(context, ("url: " + bundle.getString("url")+ "\n"); 	   		
	 	   		displayMessage(context, ("image_url: " + bundle.getString("image_url")+ "\n");
	 	   		displayMessage(context, ("large_icon: " + bundle.getString("large_icon")+ "\n");	
	 	   	}
	 	   	*/
	    }
	}

}
