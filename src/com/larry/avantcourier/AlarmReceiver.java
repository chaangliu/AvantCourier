package com.larry.avantcourier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			Intent newIntent = new Intent(context, Rock.class);
			newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(newIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
