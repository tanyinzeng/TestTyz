package com.example.personalapp;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import com.example.adapter.SmsAdapter1;
import com.example.constants.AlertUtil;
import com.example.constants.Constants;
import com.example.constants.LogUtil;
import com.example.constants.Md5Encode;
import com.example.constants.MyHttpUtil;
import com.example.entity.SmsOnlineEntity;
import com.example.entity.UserInfo;
import com.example.view.SLCustomListView;
import com.example.view.SLCustomListView.OnRefreshListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class SmsActivity extends Activity implements OnRefreshListener,
		OnClickListener {
	private SLCustomListView lvSms;
	private SmsAdapter1 smsAdapter;
	private static Handler mHandler;
	private List<SmsOnlineEntity> entitys;
	private SmsOnlineEntity entity;
	private int currentPage = 0, pageSize = 10;
	private PersonalApplication app;
	private UserInfo userInfo;
	private RelativeLayout firstLayout, twoLayout;
	// 写短信界面
	private Button  btnWrite, btnBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sms_layout);
		app = (PersonalApplication) getApplication();
		app.loadUserInfo();
		userInfo = app.getUserInfo();
		initView();
		initHandler();
		initData();
	}

	private void initHandler() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Constants.USER_STATUS.SMS_ALL_STATUS:
					try {
						String resultStr = (String) msg.obj;
						JSONArray jsonArray = new JSONArray(resultStr);
						if (currentPage == 1) {
							entitys = new ArrayList<SmsOnlineEntity>();
						}
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonItem = jsonArray.getJSONObject(i);
							SmsOnlineEntity entity = new SmsOnlineEntity();
							entity.setId(jsonItem.getString("id"));
							entity.setIcon(jsonItem.getString("icon"));
							entity.setName(jsonItem.getString("name"));
							entity.setDate(jsonItem.getString("date"));
							entity.setContent(jsonItem.getString("content"));
							entitys.add(entity);
						}
					} catch (Exception e) {
					}
					smsAdapter = new SmsAdapter1(SmsActivity.this, entitys);
					lvSms.setAdapter(smsAdapter);
					lvSms.onRefreshComplete();
					break;
				case Constants.USER_STATUS.SMS_DELETE_STATUS:
					try {
						String resultStr = (String) msg.obj;
						if ("success".equals(resultStr)) {
							entitys.remove(entity);
							smsAdapter.notifyDataSetChanged();
						} else {
							Toast.makeText(SmsActivity.this, resultStr,
									Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
					}
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

	private void initView() {
		firstLayout = (RelativeLayout) findViewById(R.id.first_layout);
		twoLayout = (RelativeLayout) findViewById(R.id.second_layout);
		lvSms = (SLCustomListView) findViewById(R.id.sms_listview);
		btnBack = (Button) findViewById(R.id.back);
		btnBack.setOnClickListener(this);
		entitys = new ArrayList<SmsOnlineEntity>();
		smsAdapter = new SmsAdapter1(this, entitys);
		btnWrite = (Button)findViewById(R.id.write);
		btnWrite.setOnClickListener(this);
		lvSms.setAdapter(smsAdapter);
		lvSms.setonRefreshListener(this);
		lvSms.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				AlertUtil.showAlert(SmsActivity.this, R.string.app_name,
						"确定删除本条消息吗", "确定",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								entity = entitys.get(arg2 - 1);
								String url = Constants.GET_QUEST_URI.SMS_DELETE_URI
										+ "bleid="
										+ entity.getId()
										+ "&id="
										+ userInfo.getId()
										+ "&username="
										+ userInfo.getPhone()
										+ "&PassWord="
										+ Md5Encode.MD5Encode(userInfo.getPwd());
								LogUtil.log("url = " + url);
								MyHttpUtil
										.sendSmsRequest(
												url,
												Constants.USER_STATUS.SMS_DELETE_STATUS);
							}
						}, "取消", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
			}
		});
	}


	@Override
	public void onRefresh() {
		initData();
	}

	private void initData() {
		currentPage++;
		String url = Constants.GET_QUEST_URI.SMS_ALL_URI + "page="
				+ currentPage + "&size=" + pageSize + "&id=" + userInfo.getId()
				+ "&Uname=" + userInfo.getPhone() + "&PassWord="
				+ Md5Encode.MD5Encode(userInfo.getPwd());
		LogUtil.log("url = " + url);
		MyHttpUtil.sendSmsRequest(url, Constants.USER_STATUS.SMS_ALL_STATUS);
	}

	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			if (twoLayout.getVisibility() == View.VISIBLE) {
				twoLayout.setVisibility(View.GONE);
				firstLayout.setVisibility(View.VISIBLE);
				btnWrite.setVisibility(View.VISIBLE);
			} else if (firstLayout.getVisibility() == View.VISIBLE) {
				finish();
			}
			break;
		case R.id.write:
			Uri uri = Uri.parse("smsto:");
			Intent it = new Intent(Intent.ACTION_SENDTO, uri);
			it.putExtra("sms_body", getResources().getString(R.string.sms_text));
			startActivity(it);
			break;
		
		}
	}


	private ProgressDialog progressDialog;

	public void showProgress(Context ctx) {
		try {
			if (progressDialog == null) {
				progressDialog = new ProgressDialog(ctx);
			}

			progressDialog.setTitle("提示");
			progressDialog.setMessage("正在发送请求…");
			progressDialog.setCancelable(true);
			progressDialog.show();

		} catch (Exception e) {
		}
	}

	public void disProgress() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	

	@Override
	public void onBackPressed() {
		if (twoLayout.getVisibility() == View.VISIBLE) {
			twoLayout.setVisibility(View.GONE);
			firstLayout.setVisibility(View.VISIBLE);
			btnWrite.setVisibility(View.VISIBLE);
		} else if (firstLayout.getVisibility() == View.VISIBLE) {
			finish();
		}
	}

}
