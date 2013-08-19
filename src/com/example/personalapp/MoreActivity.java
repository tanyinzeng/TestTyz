package com.example.personalapp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.taptwo.android.widget.CircleFlowIndicator;
import org.taptwo.android.widget.ViewFlow;
import org.taptwo.android.widget.ViewFlow.ViewSwitchListener;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.adapter.MoreAppAdapter;
import com.example.adapter.ViewFlowAdapter1;
import com.example.constants.AlertUtil;
import com.example.constants.Constants;
import com.example.constants.LogUtil;
import com.example.constants.Md5Encode;
import com.example.constants.MyHttpUtil;
import com.example.constants.SharedPreferencemanager;
import com.example.entity.MoreAppEntity;
import com.example.entity.UserInfo;
import com.example.logic.MediaCenter;

public class MoreActivity extends BaseActivity implements OnTouchListener,
		OnClickListener {
	private RelativeLayout aboutLayout, suggLayout, friendLayout,
			moreAppLayout, warnLayout, checkLayout;
	private Button btnBack, btnSure;

	// about
	private RelativeLayout firstLayout, aboutSecondLayout, suggSecondLayout,
			warnSecondLayout, moreAppSecondLayout, imgLayout;
	private ViewFlow mViewFlow;
	private CircleFlowIndicator mIndicator = null;
	private List<String> mImages;
	private ViewFlowAdapter1 viewFlowAdapter;
	private TextView tvAboutTv, tvWarn;
	private ListView moreLv;
	private MoreAppAdapter moreAppAdapter;

	private EditText suggEt;
	private static Handler mHandler;
	private UserInfo userInfo;

	private String phone, localMainPath, localImg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.more_layout);
		PersonalApplication app = (PersonalApplication) getApplication();
		userInfo = app.getUserInfo();
		phone = SharedPreferencemanager.getPhone(getApplicationContext());
		localMainPath = Environment.getExternalStorageDirectory()
				+ Constants.FTP_STATUS.WORKSPACE_PERSONAL_INFO + phone;
		localImg = localMainPath + Constants.FTP_STATUS.WORKSPACE_MYPIC_INFO;
		File file = new File(localImg);
		if (!file.exists()) {
			file.mkdirs();
		}
		initView();
		initAboutSecondLayout();
		initSuggSecondLayout();
		initWarnSecondLayout();
		initMoreAppSecondLayout();
	}

	private void initMoreAppSecondLayout() {
		moreAppSecondLayout = (RelativeLayout) findViewById(R.id.more_app_second_layout);
		moreLv = (ListView) findViewById(R.id.more_app_listview);
		moreAppAdapter = new MoreAppAdapter(this, MediaCenter.getIns()
				.getMoreApps());
		moreLv.setAdapter(moreAppAdapter);
		moreLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Uri uri = Uri.parse(MediaCenter.getIns().getMoreApps()
						.get(arg2).getLink());
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			}
		});
	}

	private void initWarnSecondLayout() {
		warnSecondLayout = (RelativeLayout) findViewById(R.id.warning_second_layout);
		tvWarn = (TextView) findViewById(R.id.warning_tv);
	}

	private void initAboutSecondLayout() {
		aboutSecondLayout = (RelativeLayout) findViewById(R.id.second_layout);
		tvAboutTv = (TextView) findViewById(R.id.about_app_content);
		mViewFlow = (ViewFlow) findViewById(R.id.viewflow);
		mImages = new ArrayList<String>();
		viewFlowAdapter = new ViewFlowAdapter1(this, mImages);
		mViewFlow.setAdapter(viewFlowAdapter, 0);
		mViewFlow.setOnViewSwitchListener(new ViewSwitchListener() {
			@Override
			public void onSwitched(View view, int position) {
				Toast.makeText(MoreActivity.this, "position = " + position,
						Toast.LENGTH_LONG).show();
			}
		});
		mIndicator = (CircleFlowIndicator) findViewById(R.id.viewflowindic);
		mViewFlow.setFlowIndicator(mIndicator);
	}

	private void initView() {
		firstLayout = (RelativeLayout) findViewById(R.id.first_layout);
		imgLayout = (RelativeLayout) findViewById(R.id.person_img_layout);
		imgLayout.setOnTouchListener(this);
		aboutLayout = (RelativeLayout) findViewById(R.id.person_about_layout);
		aboutLayout.setOnTouchListener(this);
		suggLayout = (RelativeLayout) findViewById(R.id.person_feedback_layout);
		suggLayout.setOnTouchListener(this);
		friendLayout = (RelativeLayout) findViewById(R.id.person_tell_friend_layout);
		friendLayout.setOnTouchListener(this);
		moreAppLayout = (RelativeLayout) findViewById(R.id.person_more_app_layout);
		moreAppLayout.setOnTouchListener(this);
		warnLayout = (RelativeLayout) findViewById(R.id.person_warning_app_layout);
		warnLayout.setOnTouchListener(this);
		checkLayout = (RelativeLayout) findViewById(R.id.person_checking_app_layout);
		checkLayout.setOnTouchListener(this);
		btnBack = (Button) findViewById(R.id.back);
		btnSure = (Button) findViewById(R.id.sure);
		btnBack.setOnClickListener(this);
		btnSure.setOnClickListener(this);
		initHandler();
	}

	private void initSuggSecondLayout() {
		suggSecondLayout = (RelativeLayout) findViewById(R.id.sugg_second_layout);
		suggEt = (EditText) findViewById(R.id.sugg_et);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			v.setPressed(true);
			return true;
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			if (v.isPressed()) {
				switch (v.getId()) {
				case R.id.person_img_layout:
					Intent intent = new Intent(
							MoreActivity.this,
							PersonMyPicActivity.class);
					startActivity(intent);
					break;
				case R.id.person_about_layout:
					firstLayout.setVisibility(View.GONE);
					aboutSecondLayout.setVisibility(View.VISIBLE);
					String url = Constants.GET_QUEST_URI.ABOUT_URI + "id="
							+ userInfo.getCityId() + "&phone="
							+ userInfo.getPhone() + "&password="
							+ Md5Encode.MD5Encode(userInfo.getPwd());
					MyHttpUtil.sendMoreActivityRequest(url,
							Constants.USER_STATUS.ABOUT_URI_STATUS);
					break;
				case R.id.person_feedback_layout:
					firstLayout.setVisibility(View.GONE);
					suggSecondLayout.setVisibility(View.VISIBLE);
					btnSure.setVisibility(View.VISIBLE);
					break;
				case R.id.person_tell_friend_layout:
					url = Constants.GET_QUEST_URI.SEND_FRIEND_URI + "phone="
							+ userInfo.getPhone() + "&password="
							+ Md5Encode.MD5Encode(userInfo.getPwd());
					MyHttpUtil.sendMoreActivityRequest(url,
							Constants.USER_STATUS.SEND_FRIEND_URI_STATUS);
					break;
				case R.id.person_more_app_layout:
					firstLayout.setVisibility(View.GONE);
					moreAppSecondLayout.setVisibility(View.VISIBLE);
					url = Constants.GET_QUEST_URI.MORE_APP_URI
							+ "page=1&size=10&id=" + userInfo.getId()
							+ "&phone=" + userInfo.getPhone() + "&password="
							+ Md5Encode.MD5Encode(userInfo.getPwd());
					MediaCenter.getIns().clearMoreApps();
					MyHttpUtil.sendMoreActivityRequest(url,
							Constants.USER_STATUS.MORE_APP_URI_STATUS);
					break;
				case R.id.person_warning_app_layout:
					firstLayout.setVisibility(View.GONE);
					warnSecondLayout.setVisibility(View.VISIBLE);
					MyHttpUtil.sendMoreActivityRequest(
							Constants.GET_QUEST_URI.WARNING_URI,
							Constants.USER_STATUS.WARNING_URI_STATUS);
					break;
				case R.id.person_checking_app_layout:
					break;
				}
			}
			v.setPressed(false);
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			Rect rect = new Rect();
			v.getDrawingRect(rect);
			float x = event.getX();
			float y = event.getY();
			if (!rect.contains((int) x, (int) y)) {
				v.setPressed(false);
			}

		} else {
			v.setPressed(false);
		}

		return false;
	}

	@Override
	public void onBackPressed() {
		if (firstLayout.getVisibility() == View.VISIBLE) {
			finish();
		} else if (aboutSecondLayout.getVisibility() == View.VISIBLE) {
			aboutSecondLayout.setVisibility(View.GONE);
			firstLayout.setVisibility(View.VISIBLE);
		} else if (suggSecondLayout.getVisibility() == View.VISIBLE) {
			suggSecondLayout.setVisibility(View.GONE);
			firstLayout.setVisibility(View.VISIBLE);
			btnSure.setVisibility(View.GONE);
		} else if (warnSecondLayout.getVisibility() == View.VISIBLE) {
			warnSecondLayout.setVisibility(View.GONE);
			firstLayout.setVisibility(View.VISIBLE);
		} else if (moreAppSecondLayout.getVisibility() == View.VISIBLE) {
			moreAppSecondLayout.setVisibility(View.GONE);
			firstLayout.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			if (firstLayout.getVisibility() == View.VISIBLE) {
				finish();
			} else if (aboutSecondLayout.getVisibility() == View.VISIBLE) {
				aboutSecondLayout.setVisibility(View.GONE);
				firstLayout.setVisibility(View.VISIBLE);
			} else if (suggSecondLayout.getVisibility() == View.VISIBLE) {
				suggSecondLayout.setVisibility(View.GONE);
				firstLayout.setVisibility(View.VISIBLE);
				btnSure.setVisibility(View.GONE);
			} else if (warnSecondLayout.getVisibility() == View.VISIBLE) {
				warnSecondLayout.setVisibility(View.GONE);
				firstLayout.setVisibility(View.VISIBLE);
			} else if (moreAppSecondLayout.getVisibility() == View.VISIBLE) {
				moreAppSecondLayout.setVisibility(View.GONE);
				firstLayout.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.sure:
			if (suggSecondLayout.getVisibility() == View.VISIBLE) {
				String suggest = suggEt.getText().toString();
				if (suggest != null && suggest.length() > 0) {
					String url = Constants.GET_QUEST_URI.FEED_BACK_URI
							+ "content=" + suggest;
					MyHttpUtil.sendMoreActivityRequest(url,
							Constants.USER_STATUS.FEED_BACK_STATUS);
				} else {
					Toast.makeText(this, "投诉建议不能为空", Toast.LENGTH_SHORT).show();
				}

			}
			break;
		}
	}

	private void initHandler() {
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Constants.USER_STATUS.NOT_NETWORK:
					AlertUtil.disProgress();
					AlertUtil.showDialog(MoreActivity.this, "无网络");
					break;
				case Constants.USER_STATUS.MSG_SEND_REQUEST:
					AlertUtil.disProgress();
					AlertUtil.showProgress(MoreActivity.this);
					break;
				case Constants.USER_STATUS.MSG_SEND_FALIURE:
					AlertUtil.disProgress();
					AlertUtil.showDialog(MoreActivity.this, "请求失败");
					break;
				case Constants.USER_STATUS.FEED_BACK_STATUS:
					String resultStr = (String) msg.obj;
					if ("success".equalsIgnoreCase(resultStr)) {
						Toast.makeText(MoreActivity.this, "请求成功",
								Toast.LENGTH_SHORT).show();
						suggSecondLayout.setVisibility(View.GONE);
						firstLayout.setVisibility(View.VISIBLE);
						btnSure.setVisibility(View.GONE);
					} else {
						Toast.makeText(MoreActivity.this, "请求失败",
								Toast.LENGTH_SHORT).show();
					}

					AlertUtil.disProgress();
					break;
				case Constants.USER_STATUS.ABOUT_URI_STATUS:
					try {
						resultStr = (String) msg.obj;
						JSONArray jsonArray = new JSONArray(resultStr);
						JSONObject jsonObject = jsonArray.getJSONObject(0);
						LogUtil.log("jsonObject = " + jsonObject.toString());
						tvAboutTv.setText(jsonObject.getString("content"));
						JSONArray jsonItems = jsonObject
								.getJSONArray("picture");
						mImages = new ArrayList<String>();
						for (int i = 0; i < jsonItems.length(); i++) {
							JSONObject jsonItem = jsonItems.getJSONObject(i);
							mImages.add(jsonItem.getString("url"));
						}
						viewFlowAdapter = new ViewFlowAdapter1(
								MoreActivity.this, mImages);
						mViewFlow.setAdapter(viewFlowAdapter);
						viewFlowAdapter.notifyDataSetChanged();
					} catch (Exception e) {
					}
					AlertUtil.disProgress();
					break;
				case Constants.USER_STATUS.WARNING_URI_STATUS:
					try {
						resultStr = (String) msg.obj;
						JSONArray jsonArray = new JSONArray(resultStr);
						JSONObject jsonObject = jsonArray.getJSONObject(0);
						LogUtil.log("jsonObject = " + jsonObject.toString());
						tvWarn.setText(jsonObject.getString("content"));
					} catch (Exception e) {
					}
					AlertUtil.disProgress();
					break;
				case Constants.USER_STATUS.MORE_APP_URI_STATUS:
					try {
						resultStr = (String) msg.obj;
						JSONArray jsonArray = new JSONArray(resultStr);
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonItem = jsonArray.getJSONObject(i);
							MoreAppEntity entity = new MoreAppEntity();
							entity.setId(jsonItem.getString("id"));
							entity.setContent(jsonItem.getString("content"));
							entity.setImg(jsonItem.getString("img"));
							entity.setLink(jsonItem.getString("link"));
							entity.setTitle(jsonItem.getString("title"));
							MediaCenter.getIns().addMoreApp(entity);
						}
					} catch (Exception e) {
					}
					moreAppAdapter.notifyDataSetChanged();
					AlertUtil.disProgress();
					break;
				case Constants.USER_STATUS.SEND_FRIEND_URI_STATUS:
					try {
						resultStr = (String) msg.obj;
						JSONArray jsonArray = new JSONArray(resultStr);
						JSONObject jsonObject = jsonArray.getJSONObject(0);
						LogUtil.log("jsonObject = " + jsonObject.toString());
						Uri uri = Uri.parse("smsto:");
						Intent it = new Intent(Intent.ACTION_SENDTO, uri);
						it.putExtra("sms_body", jsonObject.getString("content"));
						startActivity(it);
					} catch (Exception e) {
					}
					AlertUtil.disProgress();
					break;
				case Constants.USER_STATUS.PHOTO_STATUS:
					AlertUtil.disProgress();
					try {
						resultStr = (String) msg.obj;
						JSONArray jsonArray = new JSONArray(resultStr);
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonItem = jsonArray.getJSONObject(i);
							String item = jsonItem.getString("url");
							if (item.contains(Constants.FTP_STATUS.WORKSPACE_MYPIC_INFO)
									&& !item.contains(".xml")) {
								MediaCenter.getIns().addPic(item);
							}
						}
						// imgAdapter.notifyDataSetChanged();
					} catch (Exception e) {
					}
					break;
				}
			};
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
