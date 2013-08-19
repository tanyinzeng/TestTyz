package com.example.personalapp;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;

public class AlarmAlert extends Activity {
	private MediaPlayer mp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			mp = new MediaPlayer();
			mp.setDataSource(this, RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
			AlertDialog dialog = new AlertDialog.Builder(this)
					.setTitle(this.getResources().getString(R.string.app_name))
					.setMessage("项目时间到")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									mp.stop();
									AlarmAlert.this.finish();
								}
							}).setCancelable(true).create();
			dialog.show();
			mp.prepare();
			mp.start();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
