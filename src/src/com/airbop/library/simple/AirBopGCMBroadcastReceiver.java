package com.airbop.library.simple;

import android.content.Context;

import com.google.android.gcm.GCMBroadcastReceiver;

public class AirBopGCMBroadcastReceiver extends GCMBroadcastReceiver {
	
	protected String getGCMIntentServiceClassName(Context contest) {
		//return "com.airbop.library.simple.AirBopGCMIntentService";
		return AirBopGCMIntentService.class.getName();
	}
	
}
