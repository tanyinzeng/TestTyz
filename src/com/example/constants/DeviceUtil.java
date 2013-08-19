package com.example.constants;

import com.example.personalapp.PersonalApplication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class DeviceUtil {

	public static boolean is3GorWifi() {
		try {
			ConnectivityManager mConnectivity = (ConnectivityManager) PersonalApplication
					.getIns().getSystemService(Context.CONNECTIVITY_SERVICE);
			TelephonyManager mTelephony = (TelephonyManager) PersonalApplication
					.getIns().getSystemService(Context.TELEPHONY_SERVICE);

			NetworkInfo info = mConnectivity.getActiveNetworkInfo();

			int netType = info.getType();
			int netSubtype = info.getSubtype();

			if (netType == ConnectivityManager.TYPE_WIFI) {
				return info.isConnected();
			} else if (netType == ConnectivityManager.TYPE_MOBILE
					&& netSubtype == TelephonyManager.NETWORK_TYPE_UMTS
					&& !mTelephony.isNetworkRoaming()) {
				return info.isConnected();
			} else {
				return false;
			}
		} catch (Exception e) {
			System.out.println("is3GorWifi()  e " + e.toString());
		}

		return false;

		// return isNetworkConnected(BorFloorApplication.getIns());
	}

	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}
}
