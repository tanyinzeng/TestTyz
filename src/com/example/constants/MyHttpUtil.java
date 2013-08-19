package com.example.constants;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import com.example.personalapp.LoginActivity;
import com.example.personalapp.MoreActivity;
import com.example.personalapp.PersonMyPicActivity;
import com.example.personalapp.PersonalAppActivity1;
import com.example.personalapp.PersonalApplication;
import com.example.personalapp.SlidingMenuActivity;
import com.example.personalapp.SmsActivity;
import com.example.personalapp.TestActivity;

public class MyHttpUtil {
	public static void sendLoginRequest(String uri) {
		if (!DeviceUtil.isNetworkConnected(PersonalApplication.getIns())) {
			LoginActivity.sendHandlerMessage(Constants.USER_STATUS.NOT_NETWORK,
					null);
			return;
		}
		LoginActivity.sendHandlerMessage(
				Constants.USER_STATUS.MSG_SEND_REQUEST, null);
		try {
			final String url = uri.replaceAll(" ", "%20");

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						HttpClient client = new DefaultHttpClient();

						HttpGet httpGet = new HttpGet(url);
						// 向服务器发送post请求
						HttpResponse httpResponse = client.execute(httpGet);
						if (httpResponse.getStatusLine().getStatusCode() == 200) {
							String result = EntityUtils.toString(httpResponse
									.getEntity());
							LogUtil.log("result = " + result);
							String resultStr = DataParseUtil.xmlParse(result);
							LogUtil.log("str = " + resultStr);
							LoginActivity.sendHandlerMessage(
									Constants.USER_STATUS.LOGIN_SUCCESS,
									resultStr);
						} else {
							LogUtil.log("getStatusCode = "
									+ httpResponse.getStatusLine()
											.getStatusCode());
							LoginActivity.sendHandlerMessage(
									Constants.USER_STATUS.MSG_SEND_FALIURE,
									httpResponse.getStatusLine()
											.getStatusCode());
						}
					} catch (Exception e) {
						LogUtil.log("e.getMessage() = " + e.getMessage());
						LoginActivity.sendHandlerMessage(
								Constants.USER_STATUS.MSG_REQUEST_TIMEOUT, "");
						e.printStackTrace();
					}
				}
			}).start();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static void sendSmsRequest(String uri, final int type) {
		if (!DeviceUtil.isNetworkConnected(PersonalApplication.getIns())) {
			SmsActivity.sendHandlerMessage(Constants.USER_STATUS.NOT_NETWORK,
					null);
			return;
		}
		SmsActivity.sendHandlerMessage(Constants.USER_STATUS.MSG_SEND_REQUEST,
				null);
		try {
			final String url = uri.replaceAll(" ", "%20");

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						HttpClient client = new DefaultHttpClient();

						HttpGet httpGet = new HttpGet(url);
						// 向服务器发送post请求
						HttpResponse httpResponse = client.execute(httpGet);
						if (httpResponse.getStatusLine().getStatusCode() == 200) {
							String result = EntityUtils.toString(httpResponse
									.getEntity());
							LogUtil.log("result = " + result);
							String resultStr = DataParseUtil.xmlParse(result);
							LogUtil.log("str = " + resultStr);
							SmsActivity.sendHandlerMessage(type, resultStr);
						} else {
							LogUtil.log("getStatusCode = "
									+ httpResponse.getStatusLine()
											.getStatusCode());
							SmsActivity.sendHandlerMessage(
									Constants.USER_STATUS.MSG_SEND_FALIURE,
									httpResponse.getStatusLine()
											.getStatusCode());
						}
					} catch (Exception e) {
						LogUtil.log("e.getMessage() = " + e.getMessage());
						SmsActivity.sendHandlerMessage(
								Constants.USER_STATUS.MSG_REQUEST_TIMEOUT, "");
						e.printStackTrace();
					}
				}
			}).start();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static void sendMoreActivityRequest(String uri, final int type) {
		if (!DeviceUtil.isNetworkConnected(PersonalApplication.getIns())) {
			MoreActivity.sendHandlerMessage(Constants.USER_STATUS.NOT_NETWORK,
					null);
			return;
		}
		LogUtil.log("uri = " + uri);
		MoreActivity.sendHandlerMessage(Constants.USER_STATUS.MSG_SEND_REQUEST,
				null);
		try {
			final String url = uri.replaceAll(" ", "%20");

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						HttpClient client = new DefaultHttpClient();

						HttpGet httpGet = new HttpGet(url);
						// 向服务器发送post请求
						HttpResponse httpResponse = client.execute(httpGet);
						if (httpResponse.getStatusLine().getStatusCode() == 200) {
							String result = EntityUtils.toString(httpResponse
									.getEntity());
							LogUtil.log("result = " + result);
							String resultStr = DataParseUtil.xmlParse(result);
							LogUtil.log("str = " + resultStr);
							MoreActivity.sendHandlerMessage(type, resultStr);
						} else {
							LogUtil.log("getStatusCode = "
									+ httpResponse.getStatusLine()
											.getStatusCode());
							MoreActivity.sendHandlerMessage(
									Constants.USER_STATUS.MSG_SEND_FALIURE,
									httpResponse.getStatusLine()
											.getStatusCode());
						}
					} catch (Exception e) {
						LogUtil.log("e.getMessage() = " + e.getMessage());
						MoreActivity.sendHandlerMessage(
								Constants.USER_STATUS.MSG_REQUEST_TIMEOUT, "");
						e.printStackTrace();
					}
				}
			}).start();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static void sendMyPicRequest(String uri, final int type) {
		if (!DeviceUtil.isNetworkConnected(PersonalApplication.getIns())) {
			PersonMyPicActivity.sendHandlerMessage(
					Constants.USER_STATUS.NOT_NETWORK, null);
			return;
		}
		LogUtil.log("uri = " + uri);
		PersonMyPicActivity.sendHandlerMessage(
				Constants.USER_STATUS.MSG_SEND_REQUEST, null);
		try {
			final String url = uri.replaceAll(" ", "%20");

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						HttpClient client = new DefaultHttpClient();

						HttpGet httpGet = new HttpGet(url);
						// 向服务器发送post请求
						HttpResponse httpResponse = client.execute(httpGet);
						if (httpResponse.getStatusLine().getStatusCode() == 200) {
							String result = EntityUtils.toString(httpResponse
									.getEntity());
							LogUtil.log("result = " + result);
							String resultStr = DataParseUtil.xmlParse(result);
							LogUtil.log("str = " + resultStr);
							PersonMyPicActivity.sendHandlerMessage(type,
									resultStr);
						} else {
							LogUtil.log("getStatusCode = "
									+ httpResponse.getStatusLine()
											.getStatusCode());
							PersonMyPicActivity.sendHandlerMessage(
									Constants.USER_STATUS.MSG_SEND_FALIURE,
									httpResponse.getStatusLine()
											.getStatusCode());
						}
					} catch (Exception e) {
						LogUtil.log("e.getMessage() = " + e.getMessage());
						PersonMyPicActivity.sendHandlerMessage(
								Constants.USER_STATUS.MSG_REQUEST_TIMEOUT, "");
						e.printStackTrace();
					}
				}
			}).start();
		} catch (Exception e) {
		}
	}

	public static void sendPhotoRequest(String uri, final int type) {
		if (!DeviceUtil.isNetworkConnected(PersonalApplication.getIns())) {
			SlidingMenuActivity.sendHandlerMessage(
					Constants.USER_STATUS.NOT_NETWORK, null);
			return;
		}
		LogUtil.log("uri = " + uri);
		SlidingMenuActivity.sendHandlerMessage(
				Constants.USER_STATUS.MSG_SEND_REQUEST, null);
		try {
			final String url = uri.replaceAll(" ", "%20");

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						HttpClient client = new DefaultHttpClient();

						HttpGet httpGet = new HttpGet(url);
						// 向服务器发送post请求
						HttpResponse httpResponse = client.execute(httpGet);
						if (httpResponse.getStatusLine().getStatusCode() == 200) {
							String result = EntityUtils.toString(httpResponse
									.getEntity());
							LogUtil.log("result = " + result);
							String resultStr = DataParseUtil.xmlParse(result);
							LogUtil.log("str = " + resultStr);
							SlidingMenuActivity.sendHandlerMessage(type,
									resultStr);
						} else {
							LogUtil.log("getStatusCode = "
									+ httpResponse.getStatusLine()
											.getStatusCode());
							SlidingMenuActivity.sendHandlerMessage(
									Constants.USER_STATUS.MSG_SEND_FALIURE,
									httpResponse.getStatusLine()
											.getStatusCode());
						}
					} catch (Exception e) {
						LogUtil.log("e.getMessage() = " + e.getMessage());
						SlidingMenuActivity.sendHandlerMessage(
								Constants.USER_STATUS.MSG_REQUEST_TIMEOUT, "");
						e.printStackTrace();
					}
				}
			}).start();
		} catch (Exception e) {
		}
	}


	public static void sendAppCategory(String uri, final int type) {
		if (!DeviceUtil.isNetworkConnected(PersonalApplication.getIns())) {
			PersonalAppActivity1.sendHandlerMessage(
					Constants.USER_STATUS.NOT_NETWORK, null);
			return;
		}
		LogUtil.log("uri = " + uri);
		PersonalAppActivity1.sendHandlerMessage(
				Constants.USER_STATUS.MSG_SEND_REQUEST, null);
		try {
			final String url = uri.replaceAll(" ", "%20");

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						HttpClient client = new DefaultHttpClient();

						HttpGet httpGet = new HttpGet(url);
						// 向服务器发送post请求
						HttpResponse httpResponse = client.execute(httpGet);
						if (httpResponse.getStatusLine().getStatusCode() == 200) {
							String result = EntityUtils.toString(httpResponse
									.getEntity());
							LogUtil.log("result = " + result);
							String resultStr = DataParseUtil.xmlParse(result);
							LogUtil.log("str = " + resultStr);
							PersonalAppActivity1.sendHandlerMessage(type,
									resultStr);
						} else {
							LogUtil.log("getStatusCode = "
									+ httpResponse.getStatusLine()
											.getStatusCode());
							PersonalAppActivity1.sendHandlerMessage(
									Constants.USER_STATUS.MSG_SEND_FALIURE,
									httpResponse.getStatusLine()
											.getStatusCode());
						}
					} catch (Exception e) {
						LogUtil.log("e.getMessage() = " + e.getMessage());
						PersonalAppActivity1.sendHandlerMessage(
								Constants.USER_STATUS.MSG_REQUEST_TIMEOUT, "");
						e.printStackTrace();
					}
				}
			}).start();
		} catch (Exception e) {
		}
	}

	public static void sendAppList(String uri, final int type) {
		if (!DeviceUtil.isNetworkConnected(PersonalApplication.getIns())) {
			PersonalAppActivity1.sendHandlerMessage(
					Constants.USER_STATUS.NOT_NETWORK, null);
			return;
		}
		LogUtil.log("uri = " + uri);
		PersonalAppActivity1.sendHandlerMessage(
				Constants.USER_STATUS.MSG_SEND_REQUEST, null);
		try {
			final String url = uri.replaceAll(" ", "%20");

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						HttpClient client = new DefaultHttpClient();

						HttpGet httpGet = new HttpGet(url);
						// 向服务器发送post请求
						HttpResponse httpResponse = client.execute(httpGet);
						if (httpResponse.getStatusLine().getStatusCode() == 200) {
							String result = EntityUtils.toString(httpResponse
									.getEntity());
							LogUtil.log("result = " + result);
							String resultStr = DataParseUtil.xmlParse(result);
							LogUtil.log("str = " + resultStr);
							PersonalAppActivity1.sendHandlerMessage(type,
									resultStr);
						} else {
							LogUtil.log("getStatusCode = "
									+ httpResponse.getStatusLine()
											.getStatusCode());
							PersonalAppActivity1.sendHandlerMessage(
									Constants.USER_STATUS.MSG_SEND_FALIURE,
									httpResponse.getStatusLine()
											.getStatusCode());
						}
					} catch (Exception e) {
						LogUtil.log("e.getMessage() = " + e.getMessage());
						PersonalAppActivity1.sendHandlerMessage(
								Constants.USER_STATUS.MSG_REQUEST_TIMEOUT, "");
						e.printStackTrace();
					}
				}
			}).start();
		} catch (Exception e) {
		}
	}

}
