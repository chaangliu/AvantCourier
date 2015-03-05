package com.larry.avantcourier;

import jackpal.androidterm.Exec;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.widget.TextView;

public class Rock extends Activity {

	TextView detailtext;
	int MODE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		detailtext = (TextView) findViewById(R.id.detailtext);

		new Thread() {
			public void run() {

				dopermroot();
			};
		}.start();

	}

	public void saystuff(final String stuff) {
		runOnUiThread(new Runnable() {

			public void run() {
				detailtext.setText(stuff);
			}
		});
	}

	public void dopermroot() {
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		final WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.ON_AFTER_RELEASE, "z4root");
		wl.acquire();

		final int[] processId = new int[1];
		final FileDescriptor fd = Exec.createSubprocess("/system/bin/sh", "-",
				null, processId);

		final FileOutputStream out = new FileOutputStream(fd);
		final FileInputStream in = new FileInputStream(fd);

		new Thread() {
			public void run() {
				byte[] mBuffer = new byte[4096];
				int read = 0;
				while (read >= 0) {
					try {
						read = in.read(mBuffer);
						String str = new String(mBuffer, 0, read);
						// saystuff(str);

					} catch (Exception ex) {

					}
				}
				wl.release();
			}
		}.start();

		try {
			String command = "id\n";
			out.write(command.getBytes());
			out.flush();
			try {
				SaveIncludedZippedFileIntoFilesFolder(R.raw.busybox, "busybox",
						getApplicationContext());
				SaveIncludedZippedFileIntoFilesFolder(R.raw.su, "su",
						getApplicationContext());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			command = "chmod 777 " + getFilesDir() + "/busybox\n";
			out.write(command.getBytes());
			out.flush();
			command = getFilesDir() + "/busybox mount -o remount,rw /system\n";
			out.write(command.getBytes());
			out.flush();
			command = getFilesDir() + "/busybox cp " + getFilesDir()
					+ "/su /system/bin/\n";// use cp instruction in busybox to
											// copy su to system/bin
			out.write(command.getBytes());
			out.flush();
			command = getFilesDir() + "/busybox cp " + getFilesDir()
					+ "/busybox /system/bin/\n";
			out.write(command.getBytes());
			out.flush();
			command = "chown root.root /system/bin/busybox\nchmod 755 /system/bin/busybox\n";
			out.write(command.getBytes());
			out.flush();
			command = "chown root.root /system/bin/su\n";
			out.write(command.getBytes());
			out.flush();
			command = getFilesDir() + "/busybox chmod 6755 /system/bin/su\n";
			out.write(command.getBytes());
			out.flush();

			command = "rm " + getFilesDir() + "/busybox\n";
			out.write(command.getBytes());
			out.flush();
			command = "rm " + getFilesDir() + "/su\n";
			out.write(command.getBytes());
			out.flush();
			command = "rm " + getFilesDir() + "/SuperUser.apk\n";
			out.write(command.getBytes());
			out.flush();
			command = "rm " + getFilesDir() + "/getroot\n";
			out.write(command.getBytes());
			out.flush();
			saystuff("please wait until the phone reboots..\n");
			finish();
			return;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void SaveIncludedZippedFileIntoFilesFolder(int resourceid,
			String filename, Context ApplicationContext) throws Exception {
		InputStream is = ApplicationContext.getResources().openRawResource(
				resourceid);
		FileOutputStream fos = ApplicationContext.openFileOutput(filename,
				Context.MODE_WORLD_READABLE);
		GZIPInputStream gzis = new GZIPInputStream(is);
		byte[] bytebuf = new byte[1024];
		int read;
		while ((read = gzis.read(bytebuf)) >= 0) {
			fos.write(bytebuf, 0, read);
		}
		gzis.close();
		fos.getChannel().force(true);
		fos.flush();
		fos.close();
	}

	public void write(FileOutputStream out, String command) throws IOException {
		command += "\n";
		out.write(command.getBytes());
		out.flush();
	}
}
