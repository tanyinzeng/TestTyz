package com.example.personalapp;

import com.example.constants.SharedPreferencemanager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class SplashActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		if (SharedPreferencemanager.getIsLogin(this)) {
			new Handler().postDelayed(new Runnable() {

				public void run() {
					Intent intent = new Intent(SplashActivity.this,
							TestActivity.class);
					startActivity(intent);
					finish();
				}

			}, 2500);
		} else {
			new Handler().postDelayed(new Runnable() {

				public void run() {
					Intent intent = new Intent(SplashActivity.this,
							LoginActivity.class);
					startActivity(intent);
					finish();
				}

			}, 2500);
		}
	}
}
