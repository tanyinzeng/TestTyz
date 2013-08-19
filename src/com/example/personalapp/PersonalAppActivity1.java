package com.example.personalapp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.taptwo.android.widget.CircleFlowIndicator;
import org.taptwo.android.widget.ViewFlow;
import org.taptwo.android.widget.ViewFlow.ViewSwitchListener;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adapter.PersonalAppCategoryItemAdapter;
import com.example.adapter.PersonalAppListAdapter;
import com.example.adapter.ViewFlowAdapter1;
import com.example.constants.AlertUtil;
import com.example.constants.Constants;
import com.example.constants.LogUtil;
import com.example.constants.Md5Encode;
import com.example.constants.MyHttpUtil;
import com.example.constants.SharedPreferencemanager;
import com.example.entity.AppListCategory;
import com.example.entity.PersonalAppEntity;
import com.example.entity.UserInfo;
import com.example.logic.MediaCenter;
import com.example.view.SlidingMenu;
import com.example.view.XListViewFoot;
import com.example.view.XListViewFoot.OnRefreshListener;

public class PersonalAppActivity1 extends FragmentActivity implements
		OnClickListener, OnRefreshListener {
	private SlidingMenu slidingMenu;
	private View centerView, rightView;

	// list
	private XListViewFoot applistView;
	private PersonalAppListAdapter listAdapter;
	private static Handler mHandler;
	private Button btnBack, btnCategory;
	private AppListCategory cate;
	private UserInfo userInfo;
	private int currentPage = 0;

	// category
	private ListView listView;
	private PersonalAppCategoryItemAdapter itemAdapter;
	private LinearLayout progresLayout;

	private Context context;
	private String path = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.personal_app_sliding_layout);
		context = this;
		slidingMenu = (SlidingMenu) findViewById(R.id.sliding_menu);
		progresLayout = (LinearLayout) findViewById(R.id.progress_layout);
		LayoutInflater inflater = LayoutInflater.from(this);
		path = Environment.getExternalStorageDirectory()
				+ Constants.FTP_STATUS.WORKSPACE_PERSONAL_INFO + 
				SharedPreferencemanager.getPhone(this)
				+Constants.FTP_STATUS.ZUO_PIN;
		File filePath = new File(path);
		if(!filePath.exists())
			filePath.mkdirs();
		rightView = inflater.inflate(R.layout.personal_category_list_layout,
				null);
		slidingMenu.setRightView(rightView);

		centerView = inflater.inflate(R.layout.personal_app_layout, null);

		slidingMenu.setCenterView(centerView);
		btnCategory = (Button) centerView.findViewById(R.id.sure);
		initCenterView();
		initCategoryView();
	}

	private void initCategoryView() {
		listView = (ListView) rightView.findViewById(R.id.listView);
		String url = Constants.GET_QUEST_URI.ROOT + "GetByProType";
		MyHttpUtil.sendAppCategory(url,
				Constants.USER_STATUS.LIST_APP_CATEGORY_STATUS);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				slidingMenu.showRightView();
				cate = MediaCenter.getIns().getCategorys().get(arg2);
				currentPage = 0;
				reqRequest(cate.getId());
				progresLayout.setVisibility(View.VISIBLE);
			}
		});
		initData();
	}

	private void initData() {
		itemAdapter = new PersonalAppCategoryItemAdapter(this, MediaCenter
				.getIns().getCategorys(),path);
		listView.setAdapter(itemAdapter);
	}

	private void initCenterView() {
		initView();
		initAboutSecondLayout();
		PersonalApplication app = (PersonalApplication) getApplication();
		app.loadUserInfo();
		userInfo = app.getUserInfo();
		initHandler();
	}

	private void reqRequest(String id) {
		currentPage++;
		String url = Constants.GET_QUEST_URI.PERSONAL_LIST_APP_URI + "id=" + id
				+ "&Uname=" + userInfo.getPhone() + "&password="
				+ Md5Encode.MD5Encode(userInfo.getPwd()) + "&page="
				+ currentPage + "&size=10";
		LogUtil.log("url = " + url);
		MyHttpUtil.sendAppList(url,
				Constants.USER_STATUS.PERSONAL_LIST_APP_STATUS);
	}

	private void initHandler() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Constants.USER_STATUS.LIST_APP_CATEGORY_STATUS:
					AlertUtil.disProgress();
					try {
						String resultStr = (String) msg.obj;
						JSONArray jsonArray = new JSONArray(resultStr);
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonItem = jsonArray.getJSONObject(i);
							AppListCategory cate = new AppListCategory();
							cate.setId(jsonItem.getString("pt_id"));
							cate.setName(jsonItem.getString("pt_name"));
							cate.setPt_img(jsonItem.getString("pt_img"));
							MediaCenter.getIns().addCategory(cate);
						}
					} catch (Exception e) {
					}
					itemAdapter.notifyDataSetChanged();
					if (currentPage == 0) {
						cate = MediaCenter.getIns().getCategorys().get(0);
						reqRequest(cate.getId());
					}
					break;
				case Constants.USER_STATUS.CATEGORY_TO_APPLIST:
					int loc = (Integer) msg.obj;
					cate = MediaCenter.getIns().getCategorys().get(loc);
					currentPage = 0;
					reqRequest(cate.getId());
					break;
				case Constants.USER_STATUS.PERSONAL_LIST_APP_STATUS:
					if (currentPage == 1) {
						MediaCenter.getIns().clearApps();
					}
					try {
						String resultStr = (String) msg.obj;
						JSONArray jsonArray = new JSONArray(resultStr);
						if (jsonArray.length() == 0) {
							applistView.setOnRefreshListener(
									PersonalAppActivity1.this, false);
						}
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonItem = jsonArray.getJSONObject(i);
							PersonalAppEntity cate = new PersonalAppEntity();
							cate.setImgUrl(jsonItem.getString("icon"));
							cate.setName(jsonItem.getString("content"));
							cate.setId(jsonItem.getString("id"));
							MediaCenter.getIns().addAppMap(cate);
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
					listAdapter.notifyDataSetChanged();
					applistView.onRefreshComplete();
					progresLayout.setVisibility(View.GONE);
					break;
				case Constants.USER_STATUS.ABOUT_URI_STATUS:
					try {
						String resultStr = (String) msg.obj;
						JSONArray jsonArray = new JSONArray(resultStr);
						/*
						 * "Imgs":[{"ico":img.jpg"},{"ico":img.jpg"}]
						 */
						mImages = new ArrayList<String>();
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonItem = jsonArray.getJSONObject(i);
							tvTitle.setText("个人作品详细");
							tvAboutTitle.setText(jsonItem.getString("title"));
							tvAboutTv.setText(jsonItem.getString("intro")
									+ ":"
									+ jsonItem.getJSONArray("content")
											.getJSONObject(0)
											.getString("content"));
							JSONArray jsonArray2 = jsonItem
									.getJSONArray("Imgs");
							for (int j = 0; j < jsonArray2.length(); j++) {
								JSONObject jsonItem2 = jsonArray2
										.getJSONObject(j);
								mImages.add(jsonItem2.getString("icon"));
							}
						}
					} catch (Exception e) {
					}
					viewFlowAdapter = new ViewFlowAdapter1(PersonalAppActivity1.this, mImages);
					mViewFlow.setAdapter(viewFlowAdapter, 0);
					viewFlowAdapter.notifyDataSetChanged();
					break;
				}
			}
		};
	}

	private RelativeLayout aboutSecondLayout, firstLayout;
	private ViewFlow mViewFlow;
	private CircleFlowIndicator mIndicator = null;
	private List<String> mImages;
	private ViewFlowAdapter1 viewFlowAdapter;
	private TextView tvAboutTv, tvTitle,tvAboutTitle;

	private void initAboutSecondLayout() {
		tvTitle = (TextView) centerView.findViewById(R.id.title);
		aboutSecondLayout = (RelativeLayout) centerView
				.findViewById(R.id.second_layout);
		tvAboutTv = (TextView) centerView.findViewById(R.id.about_app_content);
		tvAboutTitle = (TextView)centerView.findViewById(R.id.about_app_title);
		mViewFlow = (ViewFlow) centerView.findViewById(R.id.viewflow);
		mImages = new ArrayList<String>();
		viewFlowAdapter = new ViewFlowAdapter1(PersonalAppActivity1.this, mImages);
		mViewFlow.setAdapter(viewFlowAdapter, 0);
		mViewFlow.setOnViewSwitchListener(new ViewSwitchListener() {
			@Override
			public void onSwitched(View view, int position) {
			}
		});
		mIndicator = (CircleFlowIndicator) findViewById(R.id.viewflowindic);
		mViewFlow.setFlowIndicator(mIndicator);
	}

	private void initView() {
		firstLayout = (RelativeLayout) centerView
				.findViewById(R.id.listview_width);
		applistView = (XListViewFoot) centerView
				.findViewById(R.id.person_app_listview);
		applistView.setOnRefreshListener(this, true);
		listAdapter = new PersonalAppListAdapter(this, MediaCenter.getIns()
				.getApps());
		applistView.setAdapter(listAdapter);
		btnBack = (Button) centerView.findViewById(R.id.back);
		btnBack.setOnClickListener(this);
		btnCategory = (Button) centerView.findViewById(R.id.sure);
		btnCategory.setOnClickListener(this);
		applistView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				firstLayout.setVisibility(View.GONE);
				aboutSecondLayout.setVisibility(View.VISIBLE);
				btnCategory.setVisibility(View.GONE);
				String url = Constants.GET_QUEST_URI.PERSONAL_APP_DETAIL_URI
						+ "id="
						+ MediaCenter.getIns().getApps().get(arg2).getId();
				LogUtil.log("url = " + url);
				MyHttpUtil.sendAppList(url,
						Constants.USER_STATUS.ABOUT_URI_STATUS);
			}
		});
	}

	public static void sendHandlerMessage(int what, Object object) {
		if (mHandler == null) {
			return;
		}
		Message msg = mHandler.obtainMessage(what, object);
		mHandler.sendMessage(msg);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			if (aboutSecondLayout.getVisibility() == View.VISIBLE) {
				aboutSecondLayout.setVisibility(View.GONE);
				btnCategory.setVisibility(View.VISIBLE);
				firstLayout.setVisibility(View.VISIBLE);
				tvTitle.setText(R.string.personal_app_title);
			} else {
				finish();
			}
			break;
		case R.id.sure:
			slidingMenu.showRightView();
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		if (aboutSecondLayout.getVisibility() == View.VISIBLE) {
			aboutSecondLayout.setVisibility(View.GONE);
			btnCategory.setVisibility(View.VISIBLE);
			firstLayout.setVisibility(View.VISIBLE);
			tvTitle.setText(R.string.personal_app_title);
		} else {
			finish();
		}
	}

	@Override
	public void onPullUpRefresh() {
		reqRequest(cate.getId());
	}
}
