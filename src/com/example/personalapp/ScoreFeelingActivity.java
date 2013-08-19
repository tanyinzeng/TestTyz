package com.example.personalapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.taptwo.android.widget.CircleFlowIndicator;
import org.taptwo.android.widget.ViewFlow;
import com.example.adapter.FeelingContentAdapter;
import com.example.adapter.NoteSubmitPhotoAdapter;
import com.example.adapter.ScoreContentAdapter;
import com.example.adapter.ViewFlowAdapter7;
import com.example.constants.AsyncBitmapLoader;
import com.example.constants.AsyncBitmapLoader.ImageCallBack;
import com.example.constants.Constants;
import com.example.constants.DeviceUtil;
import com.example.constants.FTPUtil;
import com.example.constants.ImageTools;
import com.example.constants.LogUtil;
import com.example.constants.SharedPreferencemanager;
import com.example.constants.StringUtil;
import com.example.entity.FeelingContentEntity;
import com.example.entity.ScoreContentEntity;
import com.example.logic.MediaCenter;
import com.example.view.SLCustomListView;
import com.example.view.SLCustomListView.OnRefreshListener;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ScoreFeelingActivity extends BaseActivity implements
		OnClickListener, OnRefreshListener {
	private Button btnScore, btnAdd, btnSure, btnBack, btnFeeling;
	private SLCustomListView scoreLv, feelLv;
	private ScoreContentAdapter scoreAdapter;
	private FeelingContentAdapter feelAdapter;
	private TextView tvTitle;
	private RelativeLayout firstLayout, twoLayout, scoreSecondLayout,
			thirdLayout, topLayout;
	private String phone, localMainPath, localScorePath, remotePath, imgName;
	private int level;// 星级
	private List<ImageButton> stars = new ArrayList<ImageButton>();
	private ImageButton star1, star2, star3, star4, star5;
	private NoteSubmitPhotoAdapter photoAdapter;
	private Button btnSubmit;
	private List<String> imgUrls;

	private Context context;

	private LinearLayout progressLayout;
	private AsyncBitmapLoader loader;

	// note four
	private FrameLayout fourlayout;
	private ViewFlow mViewFlow;
	private CircleFlowIndicator mIndicator = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.score_feeling_layout);
		topLayout = (RelativeLayout) findViewById(R.id.top);
		context = this;
		loader = new AsyncBitmapLoader(this,
				Constants.FTP_STATUS.WORKSPACE_CHENGJI_INFO);
		phone = SharedPreferencemanager.getPhone(getApplicationContext());
		localMainPath = Environment.getExternalStorageDirectory()
				+ Constants.FTP_STATUS.WORKSPACE_PERSONAL_INFO
				+ SharedPreferencemanager.getPhone(getApplicationContext());
		localScorePath = localMainPath
				+ Constants.FTP_STATUS.WORKSPACE_CHENGJI_INFO;
		remotePath = "/" + phone + Constants.FTP_STATUS.WORKSPACE_CHENGJI_INFO;
		progressLayout = (LinearLayout) findViewById(R.id.progress_layout);
		MediaCenter.getIns().clearScores();
		MediaCenter.getIns().clearFeels();
		new Thread(new Runnable() {
			@Override
			public void run() {
				File file = new File(localScorePath);
				if (!file.exists()) {
					file.mkdirs();
				}
				File[] fileList = file.listFiles();
				if (fileList.length > 0) {
					xmlParser(fileList);
				} else {
					if (FTPUtil.ftpDownload(remotePath, localScorePath,
							Constants.FTP_STATUS.CHENGJI_TXT_NAME)) {
						FTPUtil.ftpDownload(remotePath, localScorePath,
								Constants.FTP_STATUS.FEELING_TXT_NAME);
						file = new File(localScorePath);
						if (!file.exists()) {
							file.mkdirs();
						}
						fileList = file.listFiles();
						xmlParserOnline(fileList);
					} else {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								progressLayout.setVisibility(View.GONE);
							}
						});
					}
				}
			}
		}).start();

		initView();
		initFeelTwoView();
		initScoreTwoView();
		initThirdLayout();
		initNoteFourLayout();
	}

	private ImageView noteImg;
	private Button btnShare;
	private TextView tvNoteDesc, tvNoteTime;

	private void initThirdLayout() {
		thirdLayout = (RelativeLayout) findViewById(R.id.note_thirdLayout);
		noteImg = (ImageView) findViewById(R.id.note_detail);
		noteImg.setOnClickListener(this);
		btnShare = (Button) findViewById(R.id.note_share);
		tvNoteDesc = (TextView) findViewById(R.id.note_desc);
		tvNoteTime = (TextView) findViewById(R.id.note_time);
		btnShare.setOnClickListener(this);
	}

	private void initNoteFourLayout() {
		fourlayout = (FrameLayout) findViewById(R.id.notebook_fourlayout);
		mViewFlow = (ViewFlow) findViewById(R.id.viewflow);

		mIndicator = (CircleFlowIndicator) findViewById(R.id.viewflowindic);
		mViewFlow.setFlowIndicator(mIndicator);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void xmlParserOnline(File[] fileList) {
		MediaCenter.getIns().clearScores();
		MediaCenter.getIns().clearFeels();
		for (int i = 0; i < fileList.length; i++) {
			String str = fileList[i].toString();
			if (str.contains(Constants.FTP_STATUS.CHENGJI_TXT_NAME)) {
				try {
					List<ScoreContentEntity> noteBars = SharedPreferencemanager
							.pullScoreContentFromFile(context, str);
					for (int j = 0; j < noteBars.size(); j++) {
						MediaCenter.getIns().addScoreContent(noteBars.get(j));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (str.contains(Constants.FTP_STATUS.FEELING_TXT_NAME)) {
				try {
					List<FeelingContentEntity> noteBars = SharedPreferencemanager
							.pullFeelContentFromFile(context, str);
					for (int j = 0; j < noteBars.size(); j++) {
						FeelingContentEntity entity = noteBars.get(j);
						entity.setOnline(true);
						MediaCenter.getIns().addFeelContent(noteBars.get(j));
					}
					SharedPreferencemanager.pushFeelContentToFile(MediaCenter
							.getIns().getFeels(), context, localScorePath
							+ Constants.FTP_STATUS.FEELING_TXT_NAME);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				progressLayout.setVisibility(View.GONE);
				scoreAdapter.notifyDataSetChanged();
				feelAdapter.notifyDataSetChanged();
			}
		});
	}

	private void xmlParser(File[] fileList) {
		for (int i = 0; i < fileList.length; i++) {
			String str = fileList[i].toString();
			if (str.contains(Constants.FTP_STATUS.CHENGJI_TXT_NAME)) {
				try {
					List<ScoreContentEntity> noteBars = SharedPreferencemanager
							.pullScoreContentFromFile(context, str);
					for (int j = 0; j < noteBars.size(); j++) {
						MediaCenter.getIns().addScoreContent(noteBars.get(j));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (str.contains(Constants.FTP_STATUS.FEELING_TXT_NAME)) {
				try {
					List<FeelingContentEntity> noteBars = SharedPreferencemanager
							.pullFeelContentFromFile(context, str);
					for (int j = 0; j < noteBars.size(); j++) {
						MediaCenter.getIns().addFeelContent(noteBars.get(j));
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				progressLayout.setVisibility(View.GONE);
				scoreAdapter.notifyDataSetChanged();
				feelAdapter.notifyDataSetChanged();
			}
		});
	}

	private void initScoreTwoView() {
		etScoreName = (EditText) findViewById(R.id.score_title_et);
		etScore = (EditText) findViewById(R.id.score_score_et);
		etScoreTime = (TextView) findViewById(R.id.score_scoretime_et);
		etScoreTime.setOnClickListener(this);
	}

	private EditText etFeelTitle, etScoreName, etScore;
	private TextView etScoreTime;
	private GridView gridView;

	private void initFeelTwoView() {
		star1 = (ImageButton) findViewById(R.id.first_star);
		star2 = (ImageButton) findViewById(R.id.second_star);
		star3 = (ImageButton) findViewById(R.id.third_star);
		star4 = (ImageButton) findViewById(R.id.four_star);
		star5 = (ImageButton) findViewById(R.id.five_star);
		star1.setOnClickListener(this);
		star2.setOnClickListener(this);
		star3.setOnClickListener(this);
		star4.setOnClickListener(this);
		star5.setOnClickListener(this);
		stars.add(star1);
		stars.add(star2);
		stars.add(star3);
		stars.add(star4);
		stars.add(star5);
		etFeelTitle = (EditText) findViewById(R.id.item_title_et);
		gridView = (GridView) findViewById(R.id.notebook_gridView);
		photoAdapter = new NoteSubmitPhotoAdapter(this, false, MediaCenter
				.getIns().getPhotos());
		gridView.setAdapter(photoAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				MediaCenter.getIns().clearPhotos();
				ImageTools.ShowPickDialog(ScoreFeelingActivity.this);
			}
		});
		btnSubmit = (Button) findViewById(R.id.submit_photo_et);
		btnSubmit.setOnClickListener(this);
	}

	// private void ShowPickDialog() {
	// AlertDialog.Builder builder = new AlertDialog.Builder(this);
	// builder.setTitle("图片来源");
	// builder.setNegativeButton("取消", null);
	// builder.setItems(new String[] { "拍照", "相册" },
	// new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// switch (which) {
	// case 0:
	// Intent openCameraIntent = new Intent(
	// MediaStore.ACTION_IMAGE_CAPTURE);
	// Uri imageUri = Uri.fromFile(new File(Environment
	// .getExternalStorageDirectory(), "image.jpg"));
	// // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
	// openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
	// imageUri);
	// startActivityForResult(openCameraIntent,
	// Constants.USER_STATUS.CAMERA_WITH_DATA);
	// break;
	//
	// case 1:
	// Intent openAlbumIntent = new Intent(
	// Intent.ACTION_GET_CONTENT);
	// openAlbumIntent.setType("image/*");
	// startActivityForResult(
	// openAlbumIntent,
	// Constants.USER_STATUS.PHOTO_PICKED_WITH_DATA);
	// break;
	//
	// default:
	// break;
	// }
	// }
	// });
	// builder.create().show();
	// }

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
					// 使用ContentProvider通过URI获取原始图片
					Bitmap smallBitmap = ImageTools.dealWithPhotoPicked(
							context, originalUri);
					if (smallBitmap != null) {
						String imgName = String.valueOf(System
								.currentTimeMillis());
						ImageTools.savePhotoToSDCard(smallBitmap,
								localScorePath, imgName);
						imgUrls.add(localScorePath + imgName);
						photoAdapter = new NoteSubmitPhotoAdapter(this, false,
								imgUrls);
						gridView.setAdapter(photoAdapter);
						photoAdapter.notifyDataSetChanged();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case Constants.USER_STATUS.CAMERA_WITH_DATA:
				try {
					// 将保存在本地的图片取出并缩小后显示在界面上
					Bitmap newBitmap = ImageTools.dealWithCarmeaData();
					// 将处理过的图片显示在界面上，并保存到本地
					String imgName = String.valueOf(System.currentTimeMillis());
					ImageTools.savePhotoToSDCard(newBitmap, localScorePath,
							imgName);
					imgUrls.add(localScorePath + imgName);
					photoAdapter = new NoteSubmitPhotoAdapter(this, false,
							imgUrls);
					gridView.setAdapter(photoAdapter);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}

	private FeelingContentEntity feelContent;
	private ScoreContentEntity scoreContent;
	private boolean isScoreEdit = false, isFeelEdit = false;

	private void initView() {
		tvTitle = (TextView) findViewById(R.id.title);
		btnAdd = (Button) findViewById(R.id.write);
		btnSure = (Button) findViewById(R.id.sure);
		btnBack = (Button) findViewById(R.id.back);
		btnScore = (Button) findViewById(R.id.score_btn);
		btnFeeling = (Button) findViewById(R.id.feeling_btn);
		btnAdd.setOnClickListener(this);
		btnSure.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		btnScore.setOnClickListener(this);
		btnFeeling.setOnClickListener(this);
		btnScore.setSelected(true);
		scoreLv = (SLCustomListView) findViewById(R.id.score_listview);
		feelLv = (SLCustomListView) findViewById(R.id.feeling_listview);
		scoreLv.setonRefreshListener(this);
		feelLv.setonRefreshListener(this);
		scoreLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				isScoreEdit = true;
				scoreContent = MediaCenter.getIns().getScores().get(arg2 - 1);
				tvTitle.setText("编辑成绩");
				firstLayout.setVisibility(View.GONE);
				scoreSecondLayout.setVisibility(View.VISIBLE);
				btnAdd.setVisibility(View.GONE);
				btnSure.setVisibility(View.VISIBLE);
				etScoreName.setText(scoreContent.getName());
				etScore.setText(scoreContent.getScore() + "");
				etScoreTime.setText(scoreContent.getTime());
			}
		});
		feelLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				feelContent = MediaCenter.getIns().getFeels().get(arg2 - 1);
				firstLayout.setVisibility(View.GONE);
				btnAdd.setVisibility(View.GONE);
				btnSure.setVisibility(View.VISIBLE);
				btnSure.setText(R.string.edit);
				thirdLayout.setVisibility(View.VISIBLE);
				imgName = feelContent.getImgUrl().get(0) + ".png";
				LogUtil.log("imgName = " + imgName);
				if (feelContent.isOnline()) {
					Bitmap bmp = loader.loadBitmap(noteImg,
							Constants.GET_QUEST_URI.GET_PICTURE_URI + imgName,
							new ImageCallBack() {
								@Override
								public void imageLoad(ImageView imageView,
										Bitmap bitmap) {
									imageView.setImageBitmap(bitmap);
								}
							});
					if (bmp != null) {
						noteImg.setImageBitmap(bmp);
					}
				} else {
					noteImg.setImageBitmap(decodeUriAsBitmap(imgName));
				}
				tvNoteDesc.setText(feelContent.getContent());
				tvNoteTime.setText(feelContent.getTime());
			}
		});

		scoreAdapter = new ScoreContentAdapter(this, MediaCenter.getIns()
				.getScores());
		scoreLv.setAdapter(scoreAdapter);
		feelAdapter = new FeelingContentAdapter(this, MediaCenter.getIns()
				.getFeels());
		feelLv.setAdapter(feelAdapter);
		firstLayout = (RelativeLayout) findViewById(R.id.first_layout);
		twoLayout = (RelativeLayout) findViewById(R.id.second_layout);
		scoreSecondLayout = (RelativeLayout) findViewById(R.id.chengji_second_layout);
	}

	private Bitmap decodeUriAsBitmap(String imgUrl) {
		Bitmap bitmap = null;
		try {
			InputStream inputStream = new FileInputStream(imgUrl);
			bitmap = BitmapFactory.decodeStream(inputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

	DatePickerDialog.OnDateSetListener onDateSetListener3 = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			etScoreTime
					.setText((year + "-" + (monthOfYear + 1) + "-" + dayOfMonth));
		}
	};

	@Override
	public void onClick(View v) {
		if (star1.isPressed()) {
			level = 1;
		} else if (star2.isPressed()) {
			level = 2;
		} else if (star3.isPressed()) {
			level = 3;
		} else if (star4.isPressed()) {
			level = 4;
		} else if (star5.isPressed()) {
			level = 5;
		}
		setStar(level);
		switch (v.getId()) {
		case R.id.score_scoretime_et:
			new DatePickerDialog(this, onDateSetListener3, Calendar
					.getInstance().get(Calendar.YEAR), Calendar.getInstance()
					.get(Calendar.MONTH), Calendar.getInstance().get(
					Calendar.DAY_OF_MONTH)).show();
			break;
		case R.id.back:
			if (twoLayout.getVisibility() == View.VISIBLE) {
				if (isFeelEdit) {
					isFeelEdit = false;
					twoLayout.setVisibility(View.GONE);
					thirdLayout.setVisibility(View.VISIBLE);
				} else {
					isScoreEdit = false;
					firstLayout.setVisibility(View.VISIBLE);
					twoLayout.setVisibility(View.GONE);
					btnAdd.setVisibility(View.VISIBLE);
					btnSure.setVisibility(View.GONE);
					btnFeeling.setSelected(true);
					tvTitle.setText("考核、心情");
					btnScore.setSelected(false);
				}
			} else if (scoreSecondLayout.getVisibility() == View.VISIBLE) {
				firstLayout.setVisibility(View.VISIBLE);
				scoreSecondLayout.setVisibility(View.GONE);
				btnScore.setSelected(true);
				btnFeeling.setSelected(false);
				btnAdd.setVisibility(View.VISIBLE);
				btnSure.setVisibility(View.GONE);
				tvTitle.setText("考核、心情");
			} else if (thirdLayout.getVisibility() == View.VISIBLE) {
				thirdLayout.setVisibility(View.GONE);
				firstLayout.setVisibility(View.VISIBLE);
				btnAdd.setVisibility(View.VISIBLE);
				btnFeeling.setSelected(true);
				btnScore.setSelected(false);
				btnSure.setVisibility(View.GONE);
				tvTitle.setText("考核、心情");
				btnSure.setVisibility(View.GONE);
			} else if (fourlayout.getVisibility() == View.VISIBLE) {
				fourlayout.setVisibility(View.GONE);
				thirdLayout.setVisibility(View.VISIBLE);
				topLayout.setVisibility(View.VISIBLE);
			} else {
				finish();
			}
			break;
		case R.id.sure:
			if (twoLayout.getVisibility() == View.VISIBLE) {
				if (StringUtil.isEmpty(etFeelTitle.getText().toString())) {
					Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show();
					etFeelTitle.requestFocus();
					return;
				}
				if (isFeelEdit) {
					if (imgUrls.size() > 0) {
						feelContent.setImgUrl(imgUrls);
						feelContent.setOnline(false);
					}
					feelContent.setContent(etFeelTitle.getText().toString());
					feelContent.setStars(level);
					Date date = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					feelContent.setTime(sdf.format(date));
				} else {
					if (imgUrls.size() == 0) {
						Toast.makeText(this, "请添加图片", Toast.LENGTH_LONG).show();
						return;
					}
					FeelingContentEntity feel = new FeelingContentEntity();
					feel.setContent(etFeelTitle.getText().toString());
					feel.setImgUrl(imgUrls);
					feel.setStars(level);
					// feel.setBmps(MediaCenter.getIns().getPhotos());
					Date date = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					feel.setTime(sdf.format(date));
					MediaCenter.getIns().addFeelContent(feel);
				}
				SharedPreferencemanager.pushFeelContentToFile(MediaCenter
						.getIns().getFeels(), context, localScorePath
						+ Constants.FTP_STATUS.FEELING_TXT_NAME);
				new Thread(new Runnable() {
					@Override
					public void run() {
						FTPUtil.ftpUpload(remotePath, localScorePath,
								Constants.FTP_STATUS.FEELING_TXT_NAME);
					}
				}).start();
				btnFeeling.setSelected(true);
				btnScore.setSelected(false);
				scoreLv.setVisibility(View.GONE);
				feelLv.setVisibility(View.VISIBLE);
				feelAdapter.notifyDataSetChanged();
				twoLayout.setVisibility(View.GONE);
				firstLayout.setVisibility(View.VISIBLE);
				btnSure.setVisibility(View.GONE);
				btnAdd.setVisibility(View.VISIBLE);
				isFeelEdit = false;
			} else if (thirdLayout.getVisibility() == View.VISIBLE) {
				thirdLayout.setVisibility(View.GONE);
				twoLayout.setVisibility(View.VISIBLE);
				isFeelEdit = true;
				imgUrls = new ArrayList<String>();
				btnSure.setText(R.string.sure);
				setStar(feelContent.getStars());
				etFeelTitle.setText(feelContent.getContent());
				photoAdapter = new NoteSubmitPhotoAdapter(this,
						feelContent.isOnline(), feelContent.getImgUrl());
				gridView.setAdapter(photoAdapter);
			} else {
				if (StringUtil.isEmpty(etScoreName.getText().toString())) {
					Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show();
					etScoreName.requestFocus();
					return;
				}
				if (StringUtil.isEmpty(etScore.getText().toString())) {
					Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show();
					etScore.requestFocus();
					return;
				}
				if (isScoreEdit) {
					scoreContent.setName(etScoreName.getText().toString());
					scoreContent.setScore(Integer.parseInt(etScore.getText()
							.toString()));
					scoreContent.setTime(etScoreTime.getText().toString());
				} else {
					ScoreContentEntity score = new ScoreContentEntity();
					score.setName(etScoreName.getText().toString());
					score.setScore(Integer.parseInt(etScore.getText()
							.toString()));
					score.setTime(etScoreTime.getText().toString());
					MediaCenter.getIns().addScoreContent(score);
				}

				SharedPreferencemanager.pushScoreContentToFile(MediaCenter
						.getIns().getScores(), context, localScorePath
						+ Constants.FTP_STATUS.CHENGJI_TXT_NAME);
				new Thread(new Runnable() {
					@Override
					public void run() {
						FTPUtil.ftpUpload(remotePath, localScorePath,
								Constants.FTP_STATUS.CHENGJI_TXT_NAME);
					}
				}).start();
				btnScore.setSelected(true);
				btnFeeling.setSelected(false);
				scoreLv.setVisibility(View.VISIBLE);
				feelLv.setVisibility(View.GONE);
				scoreAdapter.notifyDataSetChanged();
				scoreSecondLayout.setVisibility(View.GONE);
				firstLayout.setVisibility(View.VISIBLE);
				btnSure.setVisibility(View.GONE);
				btnAdd.setVisibility(View.VISIBLE);
				isScoreEdit = false;
			}
			tvTitle.setText("考核、心情");
			break;
		case R.id.write:
			imgUrls = new ArrayList<String>();
			MediaCenter.getIns().clearPhotos();
			photoAdapter = new NoteSubmitPhotoAdapter(context, false, imgUrls);
			gridView.setAdapter(photoAdapter);
			setStar(0);
			etFeelTitle.setText("");
			etScore.setText("");
			etScoreName.setText("");
			etScoreTime.setText("");
			if (btnFeeling.isSelected()) {
				tvTitle.setText("添加心情");
				firstLayout.setVisibility(View.GONE);
				twoLayout.setVisibility(View.VISIBLE);
				btnAdd.setVisibility(View.GONE);
				btnSure.setVisibility(View.VISIBLE);
			} else {
				tvTitle.setText("添加成绩");
				firstLayout.setVisibility(View.GONE);
				scoreSecondLayout.setVisibility(View.VISIBLE);
				btnAdd.setVisibility(View.GONE);
				btnSure.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.submit_photo_et:
			if (DeviceUtil.is3GorWifi()) {
				new Thread(new Runnable() {
					@Override
					public void run() {

						for (int i = 0; i < imgUrls.size(); i++) {
							String[] strs = imgUrls.get(i).split("/");
							FTPUtil.ftpUpload(
									"/"
											+ phone
											+ Constants.FTP_STATUS.WORKSPACE_CHENGJI_INFO,
									localScorePath, strs[strs.length - 1]
											+ ".png");
						}

					}
				}).start();
			} else {
				Toast.makeText(ScoreFeelingActivity.this, "is not wifi",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.score_btn:
			btnScore.setSelected(true);
			btnFeeling.setSelected(false);
			scoreLv.setVisibility(View.VISIBLE);
			feelLv.setVisibility(View.GONE);
			scoreAdapter.notifyDataSetChanged();
			break;
		case R.id.feeling_btn:
			btnFeeling.setSelected(true);
			btnScore.setSelected(false);
			scoreLv.setVisibility(View.GONE);
			feelLv.setVisibility(View.VISIBLE);
			feelAdapter.notifyDataSetChanged();
			break;
		case R.id.note_share:
			Intent intent = new Intent(Intent.ACTION_SEND);
			try {
				intent.putExtra(Intent.EXTRA_STREAM,
						Uri.fromFile(new File(imgName + ".png")));
			} catch (Exception e) {
				Toast.makeText(this, "分享图片错误", Toast.LENGTH_SHORT).show();
				return;
			}
			intent.setType("image/*");
			intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
			// 分享寄语
			intent.putExtra(Intent.EXTRA_TEXT, "喜欢不");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(Intent.createChooser(intent, getResources()
					.getString(R.string.app_name)));
			break;
		case R.id.note_detail:
			thirdLayout.setVisibility(View.GONE);
			topLayout.setVisibility(View.GONE);
			fourlayout.setVisibility(View.VISIBLE);
			mViewFlow.setAdapter(new ViewFlowAdapter7(this, feelContent,
					Constants.FTP_STATUS.WORKSPACE_CHENGJI_INFO), 0);
			break;
		}
	}

	private void setStar(int pos) {
		for (int i = 0; i < stars.size(); i++) {
			if (i < pos) {
				stars.get(i).setImageDrawable(
						getResources().getDrawable(R.drawable.star_sel));
			} else {
				stars.get(i).setImageDrawable(
						getResources().getDrawable(R.drawable.star));
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (twoLayout.getVisibility() == View.VISIBLE) {
			if (isFeelEdit) {
				isFeelEdit = false;
				twoLayout.setVisibility(View.GONE);
				thirdLayout.setVisibility(View.VISIBLE);
			} else {
				isScoreEdit = false;
				firstLayout.setVisibility(View.VISIBLE);
				twoLayout.setVisibility(View.GONE);
				btnAdd.setVisibility(View.VISIBLE);
				btnSure.setVisibility(View.GONE);
				btnFeeling.setSelected(true);
				tvTitle.setText("考核、心情");
				btnScore.setSelected(false);
			}
		} else if (scoreSecondLayout.getVisibility() == View.VISIBLE) {
			firstLayout.setVisibility(View.VISIBLE);
			scoreSecondLayout.setVisibility(View.GONE);
			btnScore.setSelected(true);
			btnFeeling.setSelected(false);
			btnAdd.setVisibility(View.VISIBLE);
			btnSure.setVisibility(View.GONE);
			tvTitle.setText("考核、心情");
		} else if (thirdLayout.getVisibility() == View.VISIBLE) {
			thirdLayout.setVisibility(View.GONE);
			firstLayout.setVisibility(View.VISIBLE);
			btnAdd.setVisibility(View.VISIBLE);
			btnFeeling.setSelected(true);
			btnScore.setSelected(false);
			btnSure.setVisibility(View.GONE);
			tvTitle.setText("考核、心情");
			btnSure.setVisibility(View.GONE);
		} else if (fourlayout.getVisibility() == View.VISIBLE) {
			fourlayout.setVisibility(View.GONE);
			thirdLayout.setVisibility(View.VISIBLE);
			topLayout.setVisibility(View.VISIBLE);
		} else {
			finish();
		}
	}

	@Override
	public void onRefresh() {
		if (DeviceUtil.is3GorWifi()) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					if (FTPUtil.ftpDownload(remotePath, localScorePath,
							Constants.FTP_STATUS.CHENGJI_TXT_NAME)) {
						FTPUtil.ftpDownload(remotePath, localScorePath,
								Constants.FTP_STATUS.FEELING_TXT_NAME);
						File file = new File(localScorePath);
						if (!file.exists()) {
							file.mkdirs();
						}
						File[] fileList = file.listFiles();
						xmlParserOnline(fileList);
					}
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							feelLv.onRefreshComplete();
							scoreLv.onRefreshComplete();
						}
					});
				}
			}).start();
		} else {
			Toast.makeText(this, "不是wifi", Toast.LENGTH_LONG).show();
			feelLv.onRefreshComplete();
			scoreLv.onRefreshComplete();
		}
	}

}
