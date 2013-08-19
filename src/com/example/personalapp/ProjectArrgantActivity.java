package com.example.personalapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.taptwo.android.widget.CircleFlowIndicator;
import org.taptwo.android.widget.ViewFlow;
import com.example.adapter.NoteBookAdapter;
import com.example.adapter.NoteSubmitPhotoAdapter;
import com.example.adapter.ProjectSchedleSettingAdapter;
import com.example.adapter.ViewFlowAdapter3;
import com.example.constants.Constants;
import com.example.constants.DeviceUtil;
import com.example.constants.FTPUtil;
import com.example.constants.ImageTools;
import com.example.constants.LoadImagesUtil;
import com.example.constants.LogUtil;
import com.example.constants.SharedPreferencemanager;
import com.example.constants.StringUtil;
import com.example.entity.NoteBarEntity;
import com.example.entity.NoteBookEntity;
import com.example.logic.MediaCenter;
import com.example.view.SLCustomListView;
import com.example.view.SLCustomListView.OnRefreshListener;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ProjectArrgantActivity extends Activity implements
		OnClickListener, OnRefreshListener {
	private String localMainPath, phone, remotePath, localProject;
	private ListView noteBarLv;
	private SLCustomListView noteContentLv;
	private Button btnBack, btnWrite, btnSure, btnShare;
	private NoteBarAdapter noteBarAdapter;
	private NoteBookAdapter noteBookAdapter;
	private int level;// 星级
	private List<ImageButton> stars = new ArrayList<ImageButton>();
	private ImageButton star1, star2, star3, star4, star5;
	private ImageView noteImg;
	private GridView gridView;
	private RelativeLayout tabSetting, firstLayout, secondLayout, thirdLayout,
			forthLayout, topLayout, noteThirdLayout;
	private FrameLayout noteFourLayout;
	private TextView tvTitle, tvNoteDesc, tvNoteTitle, tvNoteTime;
	private int currentItem = 0;
	// 设置
	private ListView proLv;
	private ProjectSchedleSettingAdapter proAdapter;

	// note four
	private ViewFlow mViewFlow;
	private CircleFlowIndicator mIndicator = null;
	private String imgName;
	private NoteSubmitPhotoAdapter photoAdapter;
	private char[] en = { 'A', 'B', 'C', 'D', 'E', 'F' };
	private NoteBookEntity noteBookEntity;
	private List<String> imgUrls;
	private Context context;

	private LinearLayout progressLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.project_arrangent_layout);
		context = this;
		phone = SharedPreferencemanager.getPhone(getApplicationContext());
		MediaCenter.getIns().clearNoteBars();
		MediaCenter.getIns().clearMapNotes();
		localMainPath = Environment.getExternalStorageDirectory()
				+ Constants.FTP_STATUS.WORKSPACE_PERSONAL_INFO + phone;
		remotePath = "/" + phone + Constants.FTP_STATUS.WORKSPACE_PROJECT_INFO;
		localProject = localMainPath
				+ Constants.FTP_STATUS.WORKSPACE_PROJECT_INFO;
		noteBars = new ArrayList<NoteBarEntity>();
		maps = new HashMap<String, List<NoteBookEntity>>();
		progressLayout = (LinearLayout) findViewById(R.id.progress_layout);
		new Thread(new Runnable() {
			@Override
			public void run() {
				File file = new File(localProject);
				if (!file.exists()) {
					file.mkdirs();
				}
				File[] fileList = file.listFiles();
				if (fileList.length > 0) {
					xmlParser(fileList);
				} else {
					if (FTPUtil.ftpDownload(remotePath, localProject,
							Constants.FTP_STATUS.PROJECT_TXT_NAME)) {
						FTPUtil.ftpDownload(remotePath, localProject,
								Constants.FTP_STATUS.PROJECT_BAR_TXT_NAME);
						file = new File(localProject);
						if (!file.exists()) {
							file.mkdirs();
						}
						fileList = file.listFiles();
						if (fileList.length > 0) {
							xmlParserOnline(fileList);
						}
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
		initSecondView();
		initSetting();
		initNoteThirdLayout();
		initNoteFourLayout();
	}

	private List<NoteBarEntity> noteBars;
	private Map<String, List<NoteBookEntity>> maps;

	public void xmlParser(File[] fileList) {
		for (int i = 0; i < fileList.length; i++) {
			String str = fileList[i].toString();
			if (str.contains(Constants.FTP_STATUS.PROJECT_BAR_TXT_NAME)) {
				try {
					noteBars = SharedPreferencemanager.pullNoteBarsFromFile(
							this, str);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (str.contains(Constants.FTP_STATUS.PROJECT_TXT_NAME)) {
				try {
					maps = SharedPreferencemanager.pullNoteEntityFromFile(this,
							str);
				} catch (Exception e) {
				}
			}
		}
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				progressLayout.setVisibility(View.GONE);
				for (int j = 0; j < noteBars.size(); j++) {
					MediaCenter.getIns().addNoteBars(noteBars.get(j));
					String key = noteBars.get(j).getBarName();
					MediaCenter.getIns().addMapBookEntity(key, maps.get(key));
				}
				if (noteBarAdapter != null) {
					noteBarAdapter.notifyDataSetChanged();
				}
				if(noteBars != null && currentItem<noteBars.size()){
					String key = noteBars.get(currentItem).getBarName();
					noteBookAdapter = new NoteBookAdapter(context, MediaCenter
							.getIns().getMapsNoteBookEntitys(key));
					noteContentLv.setAdapter(noteBookAdapter);
				}
			}
		});
	}

	public void xmlParserOnline(File[] fileList) {
		MediaCenter.getIns().clearNoteBars();
		MediaCenter.getIns().clearMapNotes();
		for (int i = 0; i < fileList.length; i++) {
			String str = fileList[i].toString();
			if (str.contains(Constants.FTP_STATUS.PROJECT_BAR_TXT_NAME)) {
				try {
					noteBars = SharedPreferencemanager.pullNoteBarsFromFile(
							this, str);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (str.contains(Constants.FTP_STATUS.PROJECT_TXT_NAME)) {
				try {
					maps = SharedPreferencemanager.pullNoteEntityFromFile(this,
							str);
				} catch (Exception e) {
				}
			}
		}
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				progressLayout.setVisibility(View.GONE);
				for (int j = 0; j < noteBars.size(); j++) {
					MediaCenter.getIns().addNoteBars(noteBars.get(j));
					String key = noteBars.get(j).getBarName();
					List<NoteBookEntity> entitys = maps.get(key);
					for (int i = 0; i < entitys.size(); i++) {
						NoteBookEntity entity = entitys.get(i);
						LogUtil.log("getImgUrl = " + entity.getImgUrl().get(0));
						entity.setOnline(true);
					}
					MediaCenter.getIns().addMapBookEntity(key, maps.get(key));
				}
				SharedPreferencemanager.pushNoteBookEntityToFile(MediaCenter
						.getIns().getMapsNote(), context, localProject
						+ Constants.FTP_STATUS.PROJECT_TXT_NAME);
				if (noteBarAdapter != null) {
					noteBarAdapter.notifyDataSetChanged();
				}
				if(noteBars != null && currentItem<noteBars.size()){
					String key = noteBars.get(currentItem).getBarName();
					noteBookAdapter = new NoteBookAdapter(context, MediaCenter
							.getIns().getMapsNoteBookEntitys(key));
					noteContentLv.setAdapter(noteBookAdapter);
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	private void initNoteFourLayout() {
		noteFourLayout = (FrameLayout) findViewById(R.id.notebook_fourlayout);
		mViewFlow = (ViewFlow) findViewById(R.id.viewflow);
		mIndicator = (CircleFlowIndicator) findViewById(R.id.viewflowindic);
		mViewFlow.setFlowIndicator(mIndicator);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mViewFlow.onConfigurationChanged(newConfig);
	}

	private void initNoteThirdLayout() {
		noteThirdLayout = (RelativeLayout) findViewById(R.id.note_thirdLayout);
		noteImg = (ImageView) findViewById(R.id.note_detail);
		noteImg.setOnClickListener(this);
		btnShare = (Button) findViewById(R.id.note_share);
		tvNoteDesc = (TextView) findViewById(R.id.note_desc);
		btnShare.setOnClickListener(this);
		tvNoteTitle = (TextView) findViewById(R.id.note_title);
		tvNoteTime = (TextView) findViewById(R.id.note_time);
	}

	private EditText etProTitle, etForwardDate;
	private Button btnDate, btnSing;
	private NoteBarEntity barEntity;
	private int barPos = 0;
	private Button btnSubmit;

	private void initSetting() {
		tabSetting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				firstLayout.setVisibility(View.GONE);
				thirdLayout.setVisibility(View.VISIBLE);
				btnWrite.setVisibility(View.GONE);
			}
		});
		thirdLayout = (RelativeLayout) findViewById(R.id.third_layout);
		forthLayout = (RelativeLayout) findViewById(R.id.four_layout);
		btnSubmit = (Button) findViewById(R.id.submit_photo_et);
		btnSubmit.setOnClickListener(this);
		etProTitle = (EditText) findViewById(R.id.project_schedle_title_et);
		btnDate = (Button) findViewById(R.id.select_calendar_et);
		etForwardDate = (EditText) findViewById(R.id.forward_day_warn_et);
		btnSing = (Button) findViewById(R.id.select_sing_et);
		btnSing.setOnClickListener(this);
		btnDate.setOnClickListener(this);
		proLv = (ListView) findViewById(R.id.project_schedle_listview_setting);
		proAdapter = new ProjectSchedleSettingAdapter(this, MediaCenter
				.getIns().getNoteBars());
		proLv.setAdapter(proAdapter);
		proLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				thirdLayout.setVisibility(View.GONE);
				tvTitle.setText("修改项目安排");
				forthLayout.setVisibility(View.VISIBLE);
				btnWrite.setVisibility(View.GONE);
				btnSure.setVisibility(View.VISIBLE);
				barEntity = MediaCenter.getIns().getNoteBars().get(arg2);
				etProTitle.setText(barEntity.getBarName());
				barPos = arg2;
			}
		});
	}

	private void initView() {
		topLayout = (RelativeLayout) findViewById(R.id.top);
		tvTitle = (TextView) findViewById(R.id.title);
		noteBarLv = (ListView) findViewById(R.id.tabBar_listview);
		tabSetting = (RelativeLayout) findViewById(R.id.setting_project_schedle);
		noteBarAdapter = new NoteBarAdapter(this, MediaCenter.getIns()
				.getNoteBars());
		noteBarLv.setAdapter(noteBarAdapter);
		noteBarLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				currentItem = arg2;
				if (arg2 == MediaCenter.getIns().getNoteBars().size()) {
					imgUrls = new ArrayList<String>();
					firstLayout.setVisibility(View.GONE);
					secondLayout.setVisibility(View.VISIBLE);
					btnWrite.setVisibility(View.GONE);
					setStar(0);
					etNoteContent.setText("");
					etNoteTitle.setText("");
					MediaCenter.getIns().clearPhotos();
					photoAdapter = new NoteSubmitPhotoAdapter(context, false,
							MediaCenter.getIns().getPhotos());
					gridView.setAdapter(photoAdapter);
					btnSure.setVisibility(View.VISIBLE);
					tvTitle.setText("添加项目安排");
					level = 0;
				} else {
					noteBarAdapter.notifyDataSetChanged();
					noteBookAdapter.notifyDataSetChanged();
					String key = MediaCenter.getIns().getNoteBars()
							.get(currentItem).getBarName();
					noteBookAdapter = new NoteBookAdapter(
							ProjectArrgantActivity.this, MediaCenter.getIns()
									.getMapsNoteBookEntitys(key));
					noteContentLv.setAdapter(noteBookAdapter);
				}
			}
		});
		noteContentLv = (SLCustomListView) findViewById(R.id.notebook_listview);
		noteContentLv.setonRefreshListener(this);
		List<NoteBarEntity> noteBars = MediaCenter.getIns().getNoteBars();
		if (noteBars.size() > 0 && currentItem < noteBars.size()) {
			String key = noteBars.get(currentItem).getBarName();
			noteBookAdapter = new NoteBookAdapter(this, MediaCenter.getIns()
					.getMapsNoteBookEntitys(key));
			noteContentLv.setAdapter(noteBookAdapter);
		}
		noteContentLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				firstLayout.setVisibility(View.GONE);
				btnWrite.setVisibility(View.GONE);
				noteThirdLayout.setVisibility(View.VISIBLE);
				noteBookEntity = MediaCenter
						.getIns()
						.getMapsNoteBookEntitys(
								MediaCenter.getIns().getNoteBars()
										.get(currentItem).getBarName())
						.get(arg2 - 1);
				imgName = noteBookEntity.getImgUrl().get(0);
				btnSure.setVisibility(View.VISIBLE);
				btnSure.setText(R.string.edit);
				if (noteBookEntity.isOnline()) {
					new LoadImagesUtil(noteImg)
							.execute(new String[] { Constants.GET_QUEST_URI.GET_PICTURE_URI
									+ imgName + ".png" });
				} else {
					noteImg.setImageBitmap(decodeUriAsBitmap(imgName));
				}
				tvNoteDesc.setText(noteBookEntity.getNoteContent());
				tvNoteTitle.setText(noteBookEntity.getNoteTitle());
				tvNoteTime.setText(noteBookEntity.getNoteDate());
			}
		});
		firstLayout = (RelativeLayout) findViewById(R.id.first_layout);
		secondLayout = (RelativeLayout) findViewById(R.id.second_layout);
		btnBack = (Button) findViewById(R.id.back);
		btnBack.setOnClickListener(this);
		noteContentLv.setSelection(0);
		btnWrite = (Button) findViewById(R.id.write);
		btnWrite.setOnClickListener(this);
		btnSure = (Button) findViewById(R.id.sure);
		btnSure.setOnClickListener(this);
	}

	private Bitmap decodeUriAsBitmap(String imgUrl) {
		Bitmap bitmap = null;
		try {
			InputStream inputStream = new FileInputStream(imgUrl + ".png");
			bitmap = BitmapFactory.decodeStream(inputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

	private String key = "";
	private boolean isEdit = false;

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
		case R.id.back:
			if (secondLayout.getVisibility() == View.VISIBLE) {
				if (isEdit) {
					isEdit = false;
					secondLayout.setVisibility(View.GONE);
					noteThirdLayout.setVisibility(View.VISIBLE);
					btnSure.setText(R.string.edit);
				} else {
					key = "第" + en[currentItem] + "项目";
					List<NoteBookEntity> entitys = MediaCenter.getIns()
							.getMapsNoteBookEntitys(key);
					if (entitys == null) {
						currentItem--;
					}
					firstLayout.setVisibility(View.VISIBLE);
					secondLayout.setVisibility(View.GONE);
					btnWrite.setVisibility(View.VISIBLE);
					btnSure.setVisibility(View.GONE);
					tvTitle.setText("项目安排");
				}
			} else if (thirdLayout.getVisibility() == View.VISIBLE) {
				thirdLayout.setVisibility(View.GONE);
				firstLayout.setVisibility(View.VISIBLE);
				btnWrite.setVisibility(View.VISIBLE);
				btnSure.setVisibility(View.GONE);
			} else if (forthLayout.getVisibility() == View.VISIBLE) {
				forthLayout.setVisibility(View.GONE);
				thirdLayout.setVisibility(View.VISIBLE);
				btnWrite.setVisibility(View.GONE);
				btnSure.setVisibility(View.GONE);
				tvTitle.setText("项目安排");
			} else if (noteThirdLayout.getVisibility() == View.VISIBLE) {
				noteThirdLayout.setVisibility(View.GONE);
				firstLayout.setVisibility(View.VISIBLE);
				btnWrite.setVisibility(View.VISIBLE);
				btnSure.setVisibility(View.GONE);
			} else if (noteFourLayout.getVisibility() == View.VISIBLE) {
				noteFourLayout.setVisibility(View.GONE);
				topLayout.setVisibility(View.VISIBLE);
				noteThirdLayout.setVisibility(View.VISIBLE);
			} else {
				finish();
			}
			break;
		case R.id.write:
			imgUrls = new ArrayList<String>();
			firstLayout.setVisibility(View.GONE);
			secondLayout.setVisibility(View.VISIBLE);
			btnWrite.setVisibility(View.GONE);
			btnSure.setVisibility(View.VISIBLE);
			tvTitle.setText("添加项目安排");
			photoAdapter = new NoteSubmitPhotoAdapter(this, false, imgUrls);
			gridView.setAdapter(photoAdapter);
			setStar(0);
			etNoteContent.setText("");
			etNoteTitle.setText("");
			level = 0;
			MediaCenter.getIns().clearPhotos();
			break;
		case R.id.sure:
			if (forthLayout.getVisibility() == View.GONE) {
				if (btnSure.getText().equals(
						getResources().getString(R.string.edit))) {
					isEdit = true;
					noteThirdLayout.setVisibility(View.GONE);
					secondLayout.setVisibility(View.VISIBLE);
					btnSure.setText(R.string.sure);
					etNoteTitle.setText(noteBookEntity.getNoteTitle());
					etNoteContent.setText(noteBookEntity.getNoteContent());
					setStar(noteBookEntity.getStarTag());
					imgUrls = new ArrayList<String>();
					photoAdapter = new NoteSubmitPhotoAdapter(this,
							noteBookEntity.isOnline(),
							noteBookEntity.getImgUrl());
					gridView.setAdapter(photoAdapter);
				} else {
					if (StringUtil.isEmpty(etNoteTitle.getText().toString())) {
						Toast.makeText(this, "标题不能为空", Toast.LENGTH_SHORT)
								.show();
						etNoteTitle.requestFocus();
						return;
					}
					if (StringUtil.isEmpty(etNoteContent.getText().toString())) {
						Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT)
								.show();
						etNoteContent.requestFocus();
						return;
					}
					if (isEdit) {
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd");
						noteBookEntity.setNoteDate(sdf.format(new Date()));
						if (imgUrls != null) {
							if (imgUrls.size() > 0) {
								noteBookEntity.setOnline(false);
								noteBookEntity.setImgUrl(imgUrls);
							}
						}
						noteBookEntity.setNoteContent(etNoteContent.getText()
								.toString());
						noteBookEntity.setNoteTitle(etNoteTitle.getText()
								.toString());
						noteBookEntity.setStarTag(level);

					} else {
						if (imgUrls.size() == 0) {
							Toast.makeText(this, "请添加图片", Toast.LENGTH_LONG)
									.show();
							return;
						}
						key = "第" + en[currentItem] + "项目";
						List<NoteBookEntity> entitys = MediaCenter.getIns()
								.getMapsNoteBookEntitys(key);
						if (entitys == null) {
							entitys = new ArrayList<NoteBookEntity>();
							NoteBarEntity bar = new NoteBarEntity();
							bar.setBarName(key);
							bar.setDateComplete("");
							bar.setForwardDay(0);
							MediaCenter.getIns().addNoteBars(bar);
						}
						NoteBookEntity entity = new NoteBookEntity();
						entity.setNoteTitle(etNoteTitle.getText().toString());
						entity.setNoteContent(etNoteContent.getText()
								.toString());
						entity.setStarTag(level);
						Date date = new Date();
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd");
						entity.setNoteDate(sdf.format(date));
						entity.setImgUrl(imgUrls);
						entitys.add(entity);
						MediaCenter.getIns().addMapBookEntity(key, entitys);
						noteBookAdapter = new NoteBookAdapter(
								ProjectArrgantActivity.this, MediaCenter
										.getIns().getMapsNoteBookEntitys(key));
						noteContentLv.setAdapter(noteBookAdapter);
					}
					SharedPreferencemanager.pushNoteBarsToFile(MediaCenter
							.getIns().getNoteBars(), context, localProject
							+ Constants.FTP_STATUS.PROJECT_BAR_TXT_NAME);
					SharedPreferencemanager.pushNoteBookEntityToFile(
							MediaCenter.getIns().getMapsNote(), context,
							localProject
									+ Constants.FTP_STATUS.PROJECT_TXT_NAME);
					new Thread(new Runnable() {
						@Override
						public void run() {
							FTPUtil.ftpUpload(remotePath, localProject,
									Constants.FTP_STATUS.PROJECT_BAR_TXT_NAME);
							FTPUtil.ftpUpload(remotePath, localProject,
									Constants.FTP_STATUS.PROJECT_TXT_NAME);
						}
					}).start();

					noteBookAdapter.notifyDataSetChanged();
					firstLayout.setVisibility(View.VISIBLE);
					secondLayout.setVisibility(View.GONE);
					btnWrite.setVisibility(View.VISIBLE);
					btnSure.setVisibility(View.GONE);
				}

			} else if (forthLayout.getVisibility() == View.VISIBLE) {
				if (StringUtil.isEmpty(etProTitle.getText().toString())) {
					Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show();
					etProTitle.requestFocus();
					return;
				}
				if (StringUtil.isEmpty(etForwardDate.getText().toString())) {
					Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show();
					etForwardDate.requestFocus();
					return;
				}
				barEntity.setBarName(etProTitle.getText().toString());
				barEntity.setDateComplete(choiceDate);
				int forwardDay = Integer.parseInt(etForwardDate.getText()
						.toString());
				barEntity.setForwardDay(forwardDay);
				List<NoteBarEntity> entitys = MediaCenter.getIns()
						.getNoteBars();
				entitys.remove(barPos);
				entitys.add(barPos, barEntity);
				btnSure.setVisibility(View.GONE);
				forthLayout.setVisibility(View.GONE);
				thirdLayout.setVisibility(View.VISIBLE);
				calendar.setTimeInMillis(System.currentTimeMillis());
				calendar.set(Calendar.YEAR, choiceYear);
				calendar.set(Calendar.MONTH, choiceMonth);
				int day = choiceDay - forwardDay;
				calendar.set(Calendar.DAY_OF_MONTH, day > 0 ? day : day + 30);
				Intent intent = new Intent(ProjectArrgantActivity.this,
						CallAlarm.class);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						ProjectArrgantActivity.this, 0, intent, 0);
				// 获取闹钟管理器
				AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
				// 设置闹钟
				alarmManager.set(AlarmManager.RTC_WAKEUP,
						calendar.getTimeInMillis(), pendingIntent);
				SharedPreferencemanager.pushNoteBarsToFile(MediaCenter.getIns()
						.getNoteBars(), context, localProject
						+ Constants.FTP_STATUS.PROJECT_BAR_TXT_NAME);
			}
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
			topLayout.setVisibility(View.GONE);
			noteThirdLayout.setVisibility(View.GONE);
			noteFourLayout.setVisibility(View.VISIBLE);
			mViewFlow.setAdapter(new ViewFlowAdapter3(this, noteBookEntity,
					Constants.FTP_STATUS.WORKSPACE_PROJECT_INFO), 0);
			break;
		case R.id.select_sing_et:
			doPickRingtone();
			break;
		case R.id.select_calendar_et:
			new DatePickerDialog(this, onDateSetListener1, Calendar
					.getInstance().get(Calendar.YEAR), Calendar.getInstance()
					.get(Calendar.MONTH), Calendar.getInstance().get(
					Calendar.DAY_OF_MONTH)).show();
			// 获取日历实例
			calendar = Calendar.getInstance();
			break;
		case R.id.submit_photo_et:
			if (DeviceUtil.is3GorWifi()) {
				new Thread(new Runnable() {
					@Override
					public void run() {

						for (int i = 0; i < imgUrls.size(); i++) {
							LogUtil.log("imgUrls = " + imgUrls.get(i));
							String[] strs = imgUrls.get(i).split("/");

							FTPUtil.ftpUpload(
									"/"
											+ phone
											+ Constants.FTP_STATUS.WORKSPACE_PROJECT_INFO,
									localProject, strs[strs.length - 1]
											+ ".png");
						}

					}
				}).start();
			} else {
				Toast.makeText(ProjectArrgantActivity.this, "is not wifi",
						Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}

	private void doPickRingtone() {
		Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
				RingtoneManager.TYPE_RINGTONE);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);

		Uri ringtoneUri;
		if (mRingtoneUri != null) {
			ringtoneUri = Uri.parse(mRingtoneUri);
		} else {
			ringtoneUri = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
		}
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
				ringtoneUri);
		startActivityForResult(intent, REQUEST_CODE_PICK_RINGTONE);
	}

	private Calendar calendar;
	private String choiceDate = "";
	private int choiceYear, choiceMonth, choiceDay;
	// 用于选择铃声后作相应的判断标记
	private static final int REQUEST_CODE_PICK_RINGTONE = 1;
	// 保存铃声的Uri的字符串形式
	private String mRingtoneUri = null;
	private MediaPlayer mp;
	DatePickerDialog.OnDateSetListener onDateSetListener1 = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			choiceYear = year;
			choiceMonth = monthOfYear;
			choiceDay = dayOfMonth;
			choiceDate = (year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
		}
	};

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

	private EditText etNoteTitle, etNoteContent;
	private Button btnNoteSubmitPic;

	private void initSecondView() {
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
		btnNoteSubmitPic = (Button) findViewById(R.id.submit_photo_et);
		btnNoteSubmitPic.setOnClickListener(this);
		etNoteTitle = (EditText) findViewById(R.id.item_title_et);
		etNoteContent = (EditText) findViewById(R.id.item_content_et);
		gridView = (GridView) findViewById(R.id.notebook_gridView);
		photoAdapter = new NoteSubmitPhotoAdapter(this, false, MediaCenter
				.getIns().getPhotos());
		gridView.setAdapter(photoAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				ShowPickDialog();
			}
		});
	}

	private void ShowPickDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("图片来源");
		builder.setNegativeButton("取消", null);
		builder.setItems(new String[] { "拍照", "相册" },
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							Intent openCameraIntent = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							Uri imageUri = Uri.fromFile(new File(Environment
									.getExternalStorageDirectory(), "image.jpg"));
							// 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
							openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
									imageUri);
							startActivityForResult(openCameraIntent,
									Constants.USER_STATUS.CAMERA_WITH_DATA);
							break;

						case 1:
							Intent openAlbumIntent = new Intent(
									Intent.ACTION_GET_CONTENT);
							openAlbumIntent.setType("image/*");
							startActivityForResult(
									openAlbumIntent,
									Constants.USER_STATUS.PHOTO_PICKED_WITH_DATA);
							break;

						default:
							break;
						}
					}
				});
		builder.create().show();
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
					// 使用ContentProvider通过URI获取原始图片
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 2;
					options.inJustDecodeBounds = false;
					options.inInputShareable = true;
					options.inPurgeable = true;
					options.inPreferredConfig = Bitmap.Config.RGB_565;
					Bitmap photo = BitmapFactory.decodeStream(
							getContentResolver().openInputStream(originalUri),
							null, options);
					if (photo != null) {
						// 为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
						Bitmap smallBitmap = ImageTools
								.zoomBitmap(photo, photo.getWidth()
										/ Constants.USER_STATUS.SCALE,
										photo.getHeight()
												/ Constants.USER_STATUS.SCALE);
						// 释放原始图片占用的内存，防止out of memory异常发生
						photo.recycle();
						String imgName = String.valueOf(System
								.currentTimeMillis());
						ImageTools.savePhotoToSDCard(smallBitmap, localProject,
								imgName);
						imgUrls.add(localProject + imgName);
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
					Bitmap bitmap = BitmapFactory.decodeFile(Environment
							.getExternalStorageDirectory() + "/image.jpg");
					Bitmap newBitmap = ImageTools.zoomBitmap(bitmap,
							bitmap.getWidth() / Constants.USER_STATUS.SCALE,
							bitmap.getHeight() / Constants.USER_STATUS.SCALE);
					// 由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
					bitmap.recycle();

					// 将处理过的图片显示在界面上，并保存到本地
					String imgName = String.valueOf(System.currentTimeMillis());
					ImageTools.savePhotoToSDCard(newBitmap, localProject,
							imgName);
					imgUrls.add(localProject + imgName);
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

	class NoteBarAdapter extends BaseAdapter {
		private List<NoteBarEntity> strs;
		private LayoutInflater inflater;

		public NoteBarAdapter(Context context, List<NoteBarEntity> strs) {
			inflater = LayoutInflater.from(context);
			this.strs = strs;
		}

		@Override
		public int getCount() {
			return strs.size() + 1;
		}

		@Override
		public Object getItem(int position) {
			return strs.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			try {
				ViewHolder holder;
				if (convertView == null) {
					holder = new ViewHolder();
					convertView = inflater.inflate(
							R.layout.notebook_tabbar_listitem, null);
					holder.tvTitle = (TextView) convertView
							.findViewById(R.id.notebar_title);
					holder.ivAdd = (ImageView) convertView
							.findViewById(R.id.note_add);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				if (position == currentItem) {
					convertView.setBackgroundResource(R.drawable.note_btn_sel);
				} else {
					convertView.setBackgroundResource(R.drawable.note_btn);
				}
				if (position < strs.size()) {
					NoteBarEntity entity = strs.get(position);
					String tab = entity.getBarName();
					if (entity.getDateComplete().length() > 0) {
						String name = tab.substring(1);
						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
						long to = df.parse(entity.getDateComplete()).getTime();
						Date date = new Date();
						long from = df.parse(df.format(date)).getTime();
						System.out.println((to - from) / (1000 * 60 * 60 * 24));
						holder.tvTitle.setText("离" + name + "结束（" + (to - from)
								/ (1000 * 60 * 60 * 24) + "天）");
					} else {
						holder.tvTitle.setText(tab.substring(0, 2) + "\n"
								+ tab.substring(2));
					}
					holder.tvTitle.setVisibility(View.VISIBLE);
					holder.ivAdd.setVisibility(View.GONE);
				} else {
					holder.tvTitle.setVisibility(View.GONE);
					holder.ivAdd.setVisibility(View.VISIBLE);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return convertView;
		}

		private class ViewHolder {
			private TextView tvTitle;
			private ImageView ivAdd;
		}
	}

	@Override
	public void onBackPressed() {
		if (secondLayout.getVisibility() == View.VISIBLE) {
			if (isEdit) {
				isEdit = false;
				secondLayout.setVisibility(View.GONE);
				noteThirdLayout.setVisibility(View.VISIBLE);
				btnSure.setText(R.string.edit);
			} else {
				key = "第" + en[currentItem] + "项目";
				List<NoteBookEntity> entitys = MediaCenter.getIns()
						.getMapsNoteBookEntitys(key);
				if (entitys == null) {
					currentItem--;
				}
				firstLayout.setVisibility(View.VISIBLE);
				secondLayout.setVisibility(View.GONE);
				btnWrite.setVisibility(View.VISIBLE);
				btnSure.setVisibility(View.GONE);
				tvTitle.setText("项目安排");
			}
		} else if (thirdLayout.getVisibility() == View.VISIBLE) {
			thirdLayout.setVisibility(View.GONE);
			firstLayout.setVisibility(View.VISIBLE);
			btnWrite.setVisibility(View.VISIBLE);
			btnSure.setVisibility(View.GONE);
		} else if (forthLayout.getVisibility() == View.VISIBLE) {
			forthLayout.setVisibility(View.GONE);
			thirdLayout.setVisibility(View.VISIBLE);
			btnWrite.setVisibility(View.GONE);
			btnSure.setVisibility(View.GONE);
			tvTitle.setText("项目安排");
		} else if (noteThirdLayout.getVisibility() == View.VISIBLE) {
			noteThirdLayout.setVisibility(View.GONE);
			firstLayout.setVisibility(View.VISIBLE);
			btnWrite.setVisibility(View.VISIBLE);
			btnSure.setVisibility(View.GONE);
		} else if (noteFourLayout.getVisibility() == View.VISIBLE) {
			noteFourLayout.setVisibility(View.GONE);
			topLayout.setVisibility(View.VISIBLE);
			noteThirdLayout.setVisibility(View.VISIBLE);
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
					if (FTPUtil.ftpDownload(remotePath, localProject,
							Constants.FTP_STATUS.PROJECT_TXT_NAME)) {
						FTPUtil.ftpDownload(remotePath, localProject,
								Constants.FTP_STATUS.PROJECT_BAR_TXT_NAME);
						File file = new File(localProject);
						if (!file.exists()) {
							file.mkdirs();
						}
						File[] fileList = file.listFiles();
						if (fileList.length > 0) {
							xmlParserOnline(fileList);
						}
					}
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							noteContentLv.onRefreshComplete();
						}
					});
				}
			}).start();
		} else {
			Toast.makeText(context, "当前网络不是wifi", Toast.LENGTH_LONG).show();
			noteContentLv.onRefreshComplete();
		}

	}
}
