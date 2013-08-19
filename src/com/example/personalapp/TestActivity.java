package com.example.personalapp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.example.adapter.PersonImgAdapter;
import com.example.adapter.WelcomePagerAdapter;
import com.example.constants.AsyncBitmapLoader;
import com.example.constants.AsyncBitmapLoader.ImageCallBack;
import com.example.constants.Constants;
import com.example.constants.FTPUtil;
import com.example.constants.ImageTools;
import com.example.constants.LogUtil;
import com.example.constants.SharedPreferencemanager;
import com.example.constants.StringUtil;
import com.example.entity.UserInfo;
import com.example.view.HomeGuangGaoView;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TestActivity extends Activity implements OnTouchListener,
		OnClickListener {
	private ViewPager mViewpager;
	private List<View> mListViews;
	private WelcomePagerAdapter mPagerAdapter;
	private RadioGroup mRadioGroup;
	private RadioButton mRadioBtn;
	private RelativeLayout photoLayout, smsLayout, noteLayout, projectLayout,
			recordLayout, checkLayout, spendLayout, thingLayout, appLayout,
			moreLayout, modifySignLayout, firstLayout;
	private ImageView ivEdit, ivchangHead, ivHead;
	private Button btnSure, btnBack;
	private EditText etSign;
	private String remotePath, localMainPath, localPersonPath;
	private boolean isHead = false;
	private UserInfo userInfo;
	private TextView tvName, tvSign, tvTitle;
	private LinearLayout progressLayout;
	private AsyncBitmapLoader loader;
	private PersonalApplication app;
	private GridView imgGridView;
	private PersonImgAdapter imgAdapter;
	private List<String> imgUrls;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		tvTitle = (TextView) findViewById(R.id.title);
		app = (PersonalApplication) getApplication();
		loader = new AsyncBitmapLoader(this,
				Constants.FTP_STATUS.WORKSPACE_PERSONAL_INFO);
		progressLayout = (LinearLayout) findViewById(R.id.map_layout);
		localMainPath = Environment.getExternalStorageDirectory()
				+ Constants.FTP_STATUS.WORKSPACE_PERSONAL_INFO
				+ SharedPreferencemanager.getPhone(getApplicationContext());
		localPersonPath = localMainPath
				+ Constants.FTP_STATUS.WORKSPACE_PERSONAL_INFO;
		remotePath = "/" + SharedPreferencemanager.getPhone(this);
		initView();
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (FTPUtil.ftpDownload(remotePath
						+ Constants.FTP_STATUS.WORKSPACE_PERSONAL_INFO,
						localPersonPath, Constants.FTP_STATUS.PERSON_TXT_NAME)) {
					String filePath = localPersonPath
							+ Constants.FTP_STATUS.PERSON_TXT_NAME;
					userInfo = SharedPreferencemanager.pullUserInfoFromFile(
							TestActivity.this, filePath);
					app.setUserInfo(userInfo);
					app.saveUserInfo();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							progressLayout.setVisibility(View.GONE);
							if (userInfo != null) {
								tvName.setText(userInfo.getName());
								tvSign.setText(userInfo.getSign());
								if (userInfo.getImgName() != null
										&& userInfo.getImgName().length() > 0) {
									File headFile = new File(localPersonPath
											+ userInfo.getImgName() + ".png");
									if (headFile.exists()) {
										Bitmap bmp = BitmapFactory
												.decodeFile(localPersonPath
														+ userInfo.getImgName()
														+ ".png");
										ivHead.setImageBitmap(bmp);
										ivchangHead.setImageBitmap(bmp);
									} else {
										LogUtil.log("head img = "
												+ Constants.GET_QUEST_URI.GET_PICTURE_URI
												+ userInfo.getPhone()
												+ "/person/"
												+ userInfo.getImgName()
												+ ".png");
										Bitmap bmp = loader
												.loadBitmap(
														ivHead,
														Constants.GET_QUEST_URI.GET_PICTURE_URI
																+ userInfo
																		.getPhone()
																+ "/person/"
																+ userInfo
																		.getImgName()
																+ ".png",
														new ImageCallBack() {
															@Override
															public void imageLoad(
																	ImageView imageView,
																	Bitmap bitmap) {
																imageView
																		.setImageBitmap(bitmap);
															}
														});
										if (bmp != null) {
											ivHead.setImageBitmap(bmp);
											ivchangHead.setImageBitmap(bmp);
										}
									}
								}
								if (userInfo.getBackgroundName() != null
										&& userInfo.getBackgroundName().size() > 0) {
									File headFile = new File(localPersonPath
											+ userInfo.getBackgroundName().get(
													0) + ".png");
									if (headFile.exists()) {
										HomeGuangGaoView
												.sendHandlerMessage(
														Constants.USER_STATUS.BACKGROUND_FROM_LOCAL,
														userInfo.getBackgroundName());
									} else {
										HomeGuangGaoView
												.sendHandlerMessage(
														Constants.USER_STATUS.BACKGROUND_FROM_NET,
														userInfo.getBackgroundName());
									}
								}
							}
						}
					});
				} else {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							String filePath = localPersonPath
									+ Constants.FTP_STATUS.PERSON_TXT_NAME;
							userInfo = SharedPreferencemanager
									.pullUserInfoFromFile(TestActivity.this,
											filePath);
							app.setUserInfo(userInfo);
							app.saveUserInfo();
							LogUtil.log("getBackgroundName = "
									+ userInfo.getBackgroundName()
									+ " , getImgName = "
									+ userInfo.getImgName());
							progressLayout.setVisibility(View.GONE);
							if (userInfo != null) {
								tvName.setText(userInfo.getName());
								tvSign.setText(userInfo.getSign());
								if (userInfo.getImgName() != null
										&& userInfo.getImgName().length() > 0) {
									File headFile = new File(localPersonPath
											+ userInfo.getImgName() + ".png");
									if (headFile.exists()) {
										Bitmap bmp = BitmapFactory
												.decodeFile(localPersonPath
														+ userInfo.getImgName()
														+ ".png");
										ivHead.setImageBitmap(bmp);
									} else {
										LogUtil.log("head img = "
												+ Constants.GET_QUEST_URI.GET_PICTURE_URI
												+ userInfo.getPhone()
												+ "/person/"
												+ userInfo.getImgName()
												+ ".png");
										Bitmap bmp = loader
												.loadBitmap(
														ivHead,
														Constants.GET_QUEST_URI.GET_PICTURE_URI
																+ userInfo
																		.getPhone()
																+ "/person/"
																+ userInfo
																		.getImgName()
																+ ".png",
														new ImageCallBack() {
															@Override
															public void imageLoad(
																	ImageView imageView,
																	Bitmap bitmap) {
																imageView
																		.setImageBitmap(bitmap);
															}
														});
										if (bmp != null) {
											ivHead.setImageBitmap(bmp);
										}
									}
								}
								if (userInfo.getBackgroundName() != null
										&& userInfo.getBackgroundName().size() > 0) {
									File headFile = new File(localPersonPath
											+ userInfo.getBackgroundName().get(
													0) + ".png");
									if (headFile.exists()) {
										HomeGuangGaoView
												.sendHandlerMessage(
														Constants.USER_STATUS.BACKGROUND_FROM_LOCAL,
														userInfo.getBackgroundName());
									} else {
										HomeGuangGaoView
												.sendHandlerMessage(
														Constants.USER_STATUS.BACKGROUND_FROM_NET,
														userInfo.getBackgroundName());
									}
								}
							}
						}
					});
					FTPUtil.ftpUpload(
							"/"
									+ SharedPreferencemanager
											.getPhone(TestActivity.this)
									+ Constants.FTP_STATUS.WORKSPACE_PERSONAL_INFO,
							Environment.getExternalStorageDirectory()
									+ Constants.FTP_STATUS.WORKSPACE_PERSONAL_INFO
									+ SharedPreferencemanager
											.getPhone(TestActivity.this)
									+ Constants.FTP_STATUS.WORKSPACE_PERSONAL_INFO,
							Constants.FTP_STATUS.PERSON_TXT_NAME);
				}
			}
		}).start();

		String localAlbum = localMainPath
				+ Constants.FTP_STATUS.WORKSPACE_ALBUM_INFO;
		File file = new File(localAlbum);
		if (!file.exists()) {
			file.mkdirs();
		}
		String localVideo = localMainPath
				+ Constants.FTP_STATUS.WORKSAPCE_VIDEO_INFO;
		file = new File(localVideo);
		if (!file.exists()) {
			file.mkdirs();
		}
		String localSms = localMainPath
				+ Constants.FTP_STATUS.WORKSPACE_MESSAGE_INFO;
		file = new File(localSms);
		if (!file.exists()) {
			file.mkdirs();
		}
		String localNote = localMainPath
				+ Constants.FTP_STATUS.WORKSPACE_NOTE_INFO;
		file = new File(localNote);
		if (!file.exists()) {
			file.mkdirs();
		}

		Intent service = new Intent(TestActivity.this, UploadService.class);
		startService(service);
	}

	private void initView() {
		initSecondView();
		initViewPager();
	}

	@Override
	protected void onStart() {
		homeView.onStart();
		super.onStart();
	}

	@Override
	protected void onStop() {
		homeView.onStop();
		super.onStop();
	}

	private void initSecondView() {
		modifySignLayout = (RelativeLayout) findViewById(R.id.modify_sign_layout);
		ivEdit = (ImageView) findViewById(R.id.person_head_edit);
		ivEdit.setOnClickListener(this);
		btnSure = (Button) findViewById(R.id.sure);
		btnSure.setOnClickListener(this);
		etSign = (EditText) findViewById(R.id.modify_sign);
		ivchangHead = (ImageView) findViewById(R.id.img_head);
		ivchangHead.setOnClickListener(this);
		imgUrls = new ArrayList<String>();
		imgGridView = (GridView) findViewById(R.id.img_gridView);
		imgAdapter = new PersonImgAdapter(this, imgUrls);
		imgGridView.setAdapter(imgAdapter);
		imgGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				isHead = false;
				ImageTools.ShowPickDialog(TestActivity.this);
			}
		});
	}

	private HomeGuangGaoView homeView;

	private void initViewPager() {
		firstLayout = (RelativeLayout) findViewById(R.id.main_layout);
		ivHead = (ImageView) findViewById(R.id.person_head_iv);
		btnBack = (Button) findViewById(R.id.back);
		btnBack.setOnClickListener(this);
		tvName = (TextView) findViewById(R.id.person_name);
		tvSign = (TextView) findViewById(R.id.person_sign);
		homeView = (HomeGuangGaoView) findViewById(R.id.guanggao_view);
		mListViews = new ArrayList<View>();
		View firstView = LayoutInflater.from(this).inflate(
				R.layout.first_activity, null);
		photoLayout = (RelativeLayout) firstView
				.findViewById(R.id.person_photo_video);
		photoLayout.setOnTouchListener(this);
		smsLayout = (RelativeLayout) firstView.findViewById(R.id.person_sms);
		smsLayout.setOnTouchListener(this);
		noteLayout = (RelativeLayout) firstView
				.findViewById(R.id.person_notebook);
		noteLayout.setOnTouchListener(this);
		projectLayout = (RelativeLayout) firstView
				.findViewById(R.id.person_project_schedle);
		projectLayout.setOnTouchListener(this);
		View twoView = LayoutInflater.from(this).inflate(R.layout.two_activity,
				null);

		recordLayout = (RelativeLayout) twoView
				.findViewById(R.id.person_record);
		recordLayout.setOnTouchListener(this);
		checkLayout = (RelativeLayout) twoView
				.findViewById(R.id.person_check_feeling);
		checkLayout.setOnTouchListener(this);
		spendLayout = (RelativeLayout) twoView
				.findViewById(R.id.person_cost_spending);
		spendLayout.setOnTouchListener(this);
		thingLayout = (RelativeLayout) twoView
				.findViewById(R.id.person_thing_schedle);
		thingLayout.setOnTouchListener(this);
		View threeView = LayoutInflater.from(this).inflate(
				R.layout.three_activity, null);

		appLayout = (RelativeLayout) threeView.findViewById(R.id.person_app);
		appLayout.setOnTouchListener(this);
		moreLayout = (RelativeLayout) threeView.findViewById(R.id.person_more);
		moreLayout.setOnTouchListener(this);

		mListViews.add(firstView);
		mListViews.add(twoView);
		mListViews.add(threeView);
		mViewpager = (ViewPager) findViewById(R.id.welcomePager);
		mPagerAdapter = new WelcomePagerAdapter(mListViews);
		mViewpager.setAdapter(mPagerAdapter);
		mViewpager.setCurrentItem(0);

		mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		int size = mListViews.size();
		for (int i = 0; i < size; i++) {
			mRadioBtn = new RadioButton(this);
			mRadioBtn.setBackgroundResource(R.drawable.page_indicator_bg);
			mRadioBtn.setButtonDrawable(android.R.color.transparent);
			mRadioBtn.setPadding(10, 10, 10, 10);
			mRadioGroup.addView(mRadioBtn, new LayoutParams(22, 22));
		}
		mRadioGroup.check(mRadioGroup.getChildAt(0).getId());

		mViewpager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				mRadioGroup.check(mRadioGroup.getChildAt(position).getId());
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Intent intent;
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			v.setPressed(true);
			return true;
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			if (v.isPressed()) {
				switch (v.getId()) {
				case R.id.person_photo_video:
					intent = new Intent(TestActivity.this,
							SlidingMenuActivity.class);
					startActivity(intent);
					break;
				case R.id.person_sms:
					intent = new Intent(TestActivity.this, SmsActivity.class);
					startActivity(intent);
					break;
				case R.id.person_notebook:
					intent = new Intent(TestActivity.this,
							NoteBookActivity.class);
					startActivity(intent);
					break;
				case R.id.person_project_schedle:
					intent = new Intent(TestActivity.this,
							ProjectArrgantActivity.class);
					startActivity(intent);
					break;
				case R.id.person_record:
					intent = new Intent(TestActivity.this,
							PersonalRecordActivity.class);
					startActivity(intent);
					break;
				case R.id.person_check_feeling:
					intent = new Intent(TestActivity.this,
							ScoreFeelingActivity.class);
					startActivity(intent);
					break;
				case R.id.person_cost_spending:
					intent = new Intent(TestActivity.this,
							CostSpendingActivity.class);
					intent.putExtra(Constants.USER_FLAG.MAIN_TO_FLAG,
							Constants.USER_FLAG.MAIN_TO_COST);
					startActivity(intent);
					break;
				case R.id.person_thing_schedle:
					intent = new Intent(TestActivity.this,
							CostSpendingActivity.class);
					intent.putExtra(Constants.USER_FLAG.MAIN_TO_FLAG,
							Constants.USER_FLAG.MAIN_TO_THING);
					startActivity(intent);
					break;
				case R.id.person_app:
					intent = new Intent(TestActivity.this,
							PersonalAppActivity1.class);
					startActivity(intent);
					break;
				case R.id.person_more:
					intent = new Intent(TestActivity.this, MoreActivity.class);
					startActivity(intent);
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
		if (modifySignLayout.getVisibility() == View.VISIBLE) {
			firstLayout.setVisibility(View.VISIBLE);
			modifySignLayout.setVisibility(View.GONE);
			btnSure.setVisibility(View.GONE);
			btnBack.setVisibility(View.GONE);
			tvTitle.setText(R.string.app_name);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.person_head_edit:
			firstLayout.setVisibility(View.GONE);
			modifySignLayout.setVisibility(View.VISIBLE);
			btnSure.setVisibility(View.VISIBLE);
			btnBack.setVisibility(View.VISIBLE);
			if (userInfo != null) {
				etSign.setText(userInfo.getSign());
				if (userInfo.getBackgroundName() != null) {
					imgAdapter = new PersonImgAdapter(this,
							userInfo.getBackgroundName());
					imgGridView.setAdapter(imgAdapter);
				}
			}
			tvTitle.setText("编辑个人资料");
			break;
		case R.id.back:
			firstLayout.setVisibility(View.VISIBLE);
			modifySignLayout.setVisibility(View.GONE);
			btnSure.setVisibility(View.GONE);
			btnBack.setVisibility(View.GONE);
			tvTitle.setText(R.string.app_name);
			break;
		case R.id.sure:
			if (StringUtil.isEmpty(etSign.getText().toString())) {
				Toast.makeText(TestActivity.this, "签名不能为空", Toast.LENGTH_LONG)
						.show();
				etSign.requestFocus();
				return;
			}
			firstLayout.setVisibility(View.VISIBLE);
			modifySignLayout.setVisibility(View.GONE);
			btnSure.setVisibility(View.GONE);
			btnBack.setVisibility(View.GONE);
			if (imgUrls.size() > 0) {
				userInfo.setBackgroundName(imgUrls);
				HomeGuangGaoView.sendHandlerMessage(
						Constants.USER_STATUS.BACKGROUND_FROM_LOCAL, imgUrls);
			}

			userInfo.setSign(etSign.getText().toString());
			tvSign.setText(userInfo.getSign());
			String filePath = localMainPath
					+ Constants.FTP_STATUS.WORKSPACE_PERSONAL_INFO
					+ Constants.FTP_STATUS.PERSON_TXT_NAME;
			SharedPreferencemanager.pushUserInfoToFile(userInfo,
					TestActivity.this, filePath);
			tvTitle.setText(R.string.app_name);
			new Thread(new Runnable() {
				@Override
				public void run() {
					FTPUtil.ftpUpload(
							"/"
									+ SharedPreferencemanager
											.getPhone(TestActivity.this)
									+ Constants.FTP_STATUS.WORKSPACE_PERSONAL_INFO,
							Environment.getExternalStorageDirectory()
									+ Constants.FTP_STATUS.WORKSPACE_PERSONAL_INFO
									+ SharedPreferencemanager
											.getPhone(TestActivity.this)
									+ Constants.FTP_STATUS.WORKSPACE_PERSONAL_INFO,
							Constants.FTP_STATUS.PERSON_TXT_NAME);
				}
			}).start();
			break;
		case R.id.img_head:
			isHead = true;
			ImageTools.ShowPickDialog(this);
			break;
		case R.id.img_background:
			isHead = false;
			ImageTools.ShowPickDialog(this);
			break;
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LogUtil.log("resultCode = " + resultCode);
		if (resultCode != Activity.RESULT_OK) {// result is not correct
			LogUtil.log("requestCode = " + requestCode);
			LogUtil.log("resultCode = " + resultCode);
			LogUtil.log("data = " + data);
			return;
		} else {
			switch (requestCode) {
			case Constants.USER_STATUS.PHOTO_PICKED_WITH_DATA:
				// 照片的原始资源地址
				Uri originalUri = data.getData();
				try {
					Bitmap smallBitmap = ImageTools.dealWithPhotoPicked(
							TestActivity.this, originalUri);
					if (smallBitmap != null) {
						String imgName = String.valueOf(System
								.currentTimeMillis());
						if (isHead) {
							ivchangHead.setImageBitmap(smallBitmap);
							ivHead.setImageBitmap(smallBitmap);
							userInfo.setImgName("head");
							ImageTools.savePhotoToSDCard(smallBitmap,
									localPersonPath, "head");
						} else {
							imgUrls.add(imgName);
							imgAdapter = new PersonImgAdapter(
									TestActivity.this, imgUrls);
							imgGridView.setAdapter(imgAdapter);
							ImageTools.savePhotoToSDCard(smallBitmap,
									localPersonPath, imgName);
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case Constants.USER_STATUS.CAMERA_WITH_DATA:
				try {
					Bitmap newBitmap = ImageTools.dealWithCarmeaData();

					// 将处理过的图片显示在界面上，并保存到本地
					String imgName = String.valueOf(System.currentTimeMillis());
					if (isHead) {
						ivchangHead.setImageBitmap(newBitmap);
						ivHead.setImageBitmap(newBitmap);
						userInfo.setImgName("head");
						ImageTools.savePhotoToSDCard(newBitmap,
								localPersonPath, "head");
					} else {
						imgUrls.add(imgName);
						imgAdapter = new PersonImgAdapter(TestActivity.this,
								imgUrls);
						imgGridView.setAdapter(imgAdapter);
						// userInfo.setBackgroundName("background");
						ImageTools.savePhotoToSDCard(newBitmap,
								localPersonPath, imgName);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}


}
