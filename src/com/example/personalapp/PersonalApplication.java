package com.example.personalapp;

import com.example.constants.SharedPreferencemanager;
import com.example.entity.UserInfo;
import android.app.Application;

public class PersonalApplication extends Application {
	private static PersonalApplication ins;
	private UserInfo userInfo;

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public void saveUserInfo() {
		SharedPreferencemanager.setCityId(userInfo.getCityId(),
				getApplicationContext());
		SharedPreferencemanager.setUserId(userInfo.getId(),
				getApplicationContext());
		SharedPreferencemanager.setUserPhone(userInfo.getPhone(),
				getApplicationContext());
		SharedPreferencemanager.setUserPwd(userInfo.getPwd(),
				getApplicationContext());
	}

	public void loadUserInfo() {
		UserInfo info = new UserInfo();
		info.setId(SharedPreferencemanager.getUserId(getApplicationContext()));
		info.setPhone(SharedPreferencemanager.getPhone(getApplicationContext()));
		info.setPwd(SharedPreferencemanager.getPwd(getApplicationContext()));
		info.setCityId(SharedPreferencemanager
				.getCityId(getApplicationContext()));
		setUserInfo(info);
	}

	public static PersonalApplication getIns() {
		return ins;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		ins = this;
	}
}
