package com.larry.avantcourier;

import jackpal.androidterm.Exec;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity {
	TextView t;
	WakeLock wl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		t = (TextView) findViewById(R.id.t);
		int apiCode = Build.VERSION.SDK_INT;
		if (apiCode == 17) {
			new Thread() {
				public void run() {
					rockIt();
				};
			}.start();

		} else {

		}
		// Utilities.runCommand("adb");
	}

	public void rockIt() {

		try {
			SaveIncludedFileIntoFilesFolder(R.raw.getroot, "getroot",
					getApplicationContext());
			SaveIncludedFileIntoFilesFolder(R.raw.su,// larry added
					"su", getApplicationContext());

			SaveIncludedFileIntoFilesFolder(R.raw.busybox,// make mount
															// available
					"busybox", getApplicationContext());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("SAVE FILE ERROR", e.getMessage());
		}

		final int[] processId = new int[1];
		final FileDescriptor fd = Exec.createSubprocess("/system/bin/sh", "-",
				null, processId);

		final FileOutputStream out = new FileOutputStream(fd);
		final FileInputStream in = new FileInputStream(fd);
		new Thread() {
			public void run() {
				byte[] mBuffer = new byte[4096];
				// byte[] mBuffer_t = new byte[4096];
				int read = 0;
				while (read >= 0) {
					try {
						read = in.read(mBuffer);// int temp = ..
						String str = new String(mBuffer, 0, read);
						Log.d("SHELL", str);
						if (str.contains("Done")) {
							saystuff("part 2...");

							Intent intent = new Intent(getApplicationContext(),
									AlarmReceiver.class);
							PendingIntent sender = PendingIntent.getBroadcast(
									getApplicationContext(), 0, intent, 0);

							// Get the AlarmManager service
							AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

							Calendar cal = Calendar.getInstance();
							cal.add(Calendar.SECOND, 5);
							am.set(AlarmManager.RTC_WAKEUP,
									cal.getTimeInMillis(), sender);

							saystuff("part 3...");
							wl.release();
							Thread.sleep(20000);
							finish();
							return;
						}
						if (str.contains("Cannot find adb")) {
							runOnUiThread(new Runnable() {

								public void run() {
									Log.d("ERROR", "cannot find adb");
								}
							});
						}
					} catch (Exception e) {
						read = -1;
						e.printStackTrace();
					}
				}
			};
		}.start();

		try {

			// run ¡¸chmod 777 getroot¡¹ and then run¡¸getroot¡¹immediately

			// question is , after runing this,how to start phase2 and copy su
			// to system/bin? May 23,Larry
			Log.d("CCC", getFilesDir() + "");
			String command = "chmod 777 " + getFilesDir() + "/getroot\n";
			out.write(command.getBytes());
			out.flush();
			command = getFilesDir() + "/getroot\n";
			out.write(command.getBytes());
			out.flush();
			saystuff("part 4...");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void SaveIncludedFileIntoFilesFolder(int resourceid,
			String filename, Context ApplicationContext) throws Exception {
		InputStream is = ApplicationContext.getResources().openRawResource(
				resourceid);
		FileOutputStream fos = ApplicationContext.openFileOutput(filename,
				Context.MODE_WORLD_READABLE);
		// file saved in /data/data/<package name>/files/<filename>
		byte[] bytebuf = new byte[1024];
		int read;
		while ((read = is.read(bytebuf)) >= 0) {
			fos.write(bytebuf, 0, read);
		}
		is.close();
		fos.getChannel().force(true);
		fos.flush();
		fos.close();
	}

	public void saystuff(final String stuff) {
		runOnUiThread(new Runnable() {

			public void run() {
				t.setText(stuff);
			}
		});
	}
}
