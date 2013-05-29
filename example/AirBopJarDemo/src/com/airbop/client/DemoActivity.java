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
package com.airbop.client;

import static com.airbop.library.simple.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.airbop.library.simple.CommonUtilities.EXTRA_MESSAGE;
//import static com.airbop.library.simple.CommonUtilities.displayMessage;

//import static com.airbop.library.simple.CommonUtilities.USE_LOCATION;

//import com.airbop.library.simple.AirBopActivity;
import com.airbop.library.simple.AirBop;
//import com.google.android.gcm.GCMRegistrar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;


import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;



/**
 * Main UI for the demo app.
 */
public class DemoActivity extends Activity {

    TextView mDisplay;
    AirBop mAirBop = new AirBop();
    private static final String TAG = "DemoActivity";
    
    DemoAirBopMessageReceiver mHandleAirBopMessageReceiver = new DemoAirBopMessageReceiver();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                
        setContentView(R.layout.main);
        mDisplay = (TextView) findViewById(R.id.display);
        //registerReceiver(mHandleMessageReceiver,
        //        new IntentFilter(DISPLAY_MESSAGE_ACTION));
       
        //registerReceiver(mHandleMessageReceiver,
        //        new IntentFilter(DISPLAY_MESSAGE_ACTION));
        
        //AirBop.registerAirBopMessageReceiver(this, mHandleAirBopMessageReceiver);
        AirBop.registerAirBopLogReceiver(this, mHandleLogMessageReceiver);
        
        Intent intent = getIntent();
        if (intent != null) {
        	Bundle bundle = intent.getExtras();
        	if (bundle != null) {
	 	   		//Display the standard AirBop Bundle details
	        	mDisplay.append("message: " + bundle.getString("message")+ "\n");   			 	   		
	        	mDisplay.append("title: " + bundle.getString("title")+ "\n");	
	        	mDisplay.append("url: " + bundle.getString("url")+ "\n"); 	   		
	        	mDisplay.append("image_url: " + bundle.getString("image_url")+ "\n");
	        	mDisplay.append("large_icon: " + bundle.getString("large_icon")+ "\n");	
 	   	
        	}
        }
        // Call the register function in the AirBopActivity 
        register();  
    } 
    
    private void register() {
    	mAirBop.register(getApplicationContext());
    }
    
    private void unRegister() {
        mAirBop.unRegister(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            
            case R.id.options_register:
            	register();
                return true;
            case R.id.options_unregister:
            	unRegister();
                return true;
            case R.id.options_clear:
                mDisplay.setText(null);
                return true;
            case R.id.options_exit:
                finish();
                return true;
           /*
            case R.id.options_unregister_gcm:
            	GCMRegistrar.unregister(getApplicationContext());
            	GCMRegistrar.setRegisteredOnServer(getApplicationContext(), false);
                return true;  
            case R.id.options_unregister_airbop:
            	unRegister(false);
                return true;  
            */
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        
        //unregisterReceiver(mHandleMessageReceiver);
    	//AirBop.unregisterAirBopMessageReceiver(this, mHandleAirBopMessageReceiver);
    	AirBop.unregisterAirBopLogReceiver(this, mHandleLogMessageReceiver);
        super.onDestroy();
    }

    
    private final BroadcastReceiver mHandleLogMessageReceiver =
            new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
            mDisplay.append(newMessage + "\n");
        }
    };
    
    
}
