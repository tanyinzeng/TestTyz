package com.example.personalapp;

import java.io.File;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.constants.AlertUtil;
import com.example.constants.Constants;
import com.example.constants.LogUtil;
import com.example.constants.Md5Encode;
import com.example.constants.MyHttpUtil;
import com.example.constants.SharedPreferencemanager;
import com.example.entity.UserInfo;

public class LoginActivity extends BaseActivity {
	private EditText m_phoneNumEditText, m_licenesEditText;
	private Button m_loginButton;
	private static Handler mHandler;
	private UserInfo userInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_activity);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		int width = dm.widthPixels;
		int height = dm.heightPixels;
		LogUtil.log("width = " + width + " , height = " + height);
		m_phoneNumEditText = (EditText) this
				.findViewById(R.id.edittext_phonenum);
		m_licenesEditText = (EditText) this
				.findViewById(R.id.edittext_licenese);
		m_loginButton = (Button) this.findViewById(R.id.button_login);
		m_loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (m_phoneNumEditText.getText().toString().length() == 0) {
					Toast.makeText(LoginActivity.this, "手机号不能为空！",
							Toast.LENGTH_SHORT).show();
				} else if (m_licenesEditText.getText().toString().length() == 0) {
					Toast.makeText(LoginActivity.this, "激活码不能为空！",
							Toast.LENGTH_SHORT).show();
				} else {
					String phone = m_phoneNumEditText.getText().toString();
					String pwd = m_licenesEditText.getText().toString();
					String pass = Md5Encode.MD5Encode(pwd);
					LogUtil.log("pass = " + pass);
					String url = Constants.GET_QUEST_URI.LOGIN_URI + "phone="
							+ phone + "&PassWord=" + pass;
					MyHttpUtil.sendLoginRequest(url);
				}
			}
		});
		initHandler();
	}

	private void initHandler() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Constants.USER_STATUS.NOT_NETWORK:
					AlertUtil.showDialog(LoginActivity.this, "无网络");
					break;
				case Constants.USER_STATUS.MSG_SEND_REQUEST:
					AlertUtil.showProgress(LoginActivity.this);
					break;
				case Constants.USER_STATUS.MSG_SEND_FALIURE:
					AlertUtil.showDialog(LoginActivity.this, "请求失败");
					break;
				case Constants.USER_STATUS.LOGIN_SUCCESS:
					try {
						String resultStr = (String) msg.obj;
						JSONArray jsonArray = new JSONArray(resultStr);
						JSONObject jsonObject = jsonArray.getJSONObject(0);
						LogUtil.log("jsonObject = " + jsonObject.toString());
						String returnResult = jsonObject.getString("return");
						if (returnResult.equals("true")) {

							userInfo = new UserInfo();
							userInfo.setPhone(m_phoneNumEditText.getText()
									.toString());
							userInfo.setPwd(m_licenesEditText.getText()
									.toString());
							userInfo.setId(Integer.parseInt(jsonObject
									.getString("id")));
							userInfo.setCityId(jsonObject.getString("cityid"));
							userInfo.setImgName("");
							userInfo.setSign("欢迎使用个人App");
							userInfo.setName(jsonObject.getString("username"));
							String filePath = Environment
									.getExternalStorageDirectory()
									+ Constants.FTP_STATUS.WORKSPACE_PERSONAL_INFO
									+ userInfo.getPhone()
									+ Constants.FTP_STATUS.WORKSPACE_PERSONAL_INFO;
							File file = new File(filePath);
							if (!file.exists()) {
								file.mkdirs();
							}
							SharedPreferencemanager
									.pushUserInfoToFile(
											userInfo,
											LoginActivity.this,
											filePath
													+ Constants.FTP_STATUS.PERSON_TXT_NAME);
							PersonalApplication.getIns().setUserInfo(userInfo);
							PersonalApplication.getIns().saveUserInfo();
							Intent intent = new Intent(LoginActivity.this,
									TestActivity.class);
							startActivity(intent);
							LoginActivity.this.finish();
						}
						SharedPreferencemanager.setIsLogin(true,
								getApplicationContext());
						Toast.makeText(LoginActivity.this,
								jsonObject.getString("message"),
								Toast.LENGTH_SHORT).show();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					AlertUtil.disProgress();
					break;
				}
			}
		};
	}

	public static void sendHandlerMessage(int what, Object object) {
		if (mHandler == null) {
			return;
		}
		Message msg = mHandler.obtainMessage(what, object);
		mHandler.sendMessage(msg);
	}

}
