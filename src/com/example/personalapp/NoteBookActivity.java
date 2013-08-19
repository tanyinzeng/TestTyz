package com.example.personalapp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.taptwo.android.widget.CircleFlowIndicator;
import org.taptwo.android.widget.ViewFlow;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import com.example.adapter.NoteBookAdapter;
import com.example.adapter.NoteSubmitPhotoAdapter;
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

public class NoteBookActivity extends BaseActivity implements OnClickListener,
		OnRefreshListener {
	private ListView noteBarLv;
	private SLCustomListView noteContentLv;
	private Button btnBack, btnWrite, btnSure, btnShare;
	private NoteBarAdapter noteBarAdapter;
	private NoteBookAdapter noteBookAdapter;
	private int level = 0;// 星级
	private List<ImageButton> stars = new ArrayList<ImageButton>();
	private ImageButton star1, star2, star3, star4, star5;
	private ImageView noteImg;
	private GridView gridView;
	private RelativeLayout firstLayout, secondLayout, topLayout,
			noteThirdLayout;
	private FrameLayout noteFourLayout;
	private TextView tvTitle, tvNoteDesc, tvNoteTitle, tvNoteTime;
	private int currentItem = 0;

	// note four
	private ViewFlow mViewFlow;
	private CircleFlowIndicator mIndicator = null;
	private String imgName;
	private NoteSubmitPhotoAdapter photoAdapter;
	// private XmlTool xmlTool;
	private String localMainPath, localNote, phone, remotePath;
	private char[] ch = { '一', '二', '三', '四', '五', '六', '七', '八', '九' };
	private NoteBookEntity noteBookEntity;
	private List<String> imgUrls;
	private Context context;

	private LinearLayout progressLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.notebook_layout);
		context = this;
		phone = SharedPreferencemanager.getPhone(getApplicationContext());
		MediaCenter.getIns().clearNoteBars();
		MediaCenter.getIns().clearMapNotes();
		tvTitle = (TextView) findViewById(R.id.title);
		localMainPath = Environment.getExternalStorageDirectory()
				+ Constants.FTP_STATUS.WORKSPACE_PERSONAL_INFO + phone;
		tvTitle.setText(R.string.notebook_title);
		remotePath = "/" + phone + Constants.FTP_STATUS.WORKSPACE_NOTE_INFO;
		localNote = localMainPath + Constants.FTP_STATUS.WORKSPACE_NOTE_INFO;
		noteBars = new ArrayList<NoteBarEntity>();
		maps = new HashMap<String, List<NoteBookEntity>>();
		progressLayout = (LinearLayout) findViewById(R.id.progress_layout);
		new Thread(new Runnable() {
			@Override
			public void run() {
				File file = new File(localNote);
				if (!file.exists()) {
					file.mkdirs();
				}
				File[] fileList = file.listFiles();
				if (fileList.length > 0) {
					xmlParser(fileList);
				} else {
					if (FTPUtil.ftpDownload(remotePath, localNote,
							Constants.FTP_STATUS.NOTE_BAR_TXT_NAME)) {
						FTPUtil.ftpDownload(remotePath, localNote,
								Constants.FTP_STATUS.NOTE_TXT_NAME);
						file = new File(localNote);
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
		initNoteThirdLayout();
		initNoteFourLayout();
	}

	private List<NoteBarEntity> noteBars;
	private Map<String, List<NoteBookEntity>> maps;

	public void xmlParserOnline(File[] fileList) {
		MediaCenter.getIns().clearNoteBars();
		MediaCenter.getIns().clearMapNotes();
		for (int i = 0; i < fileList.length; i++) {
			String str = fileList[i].toString();
			if (str.contains(Constants.FTP_STATUS.NOTE_BAR_TXT_NAME)) {
				try {
					noteBars = SharedPreferencemanager.pullNoteBarsFromFile(
							this, str);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (str.contains(Constants.FTP_STATUS.NOTE_TXT_NAME)) {
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
						.getIns().getMapsNote(), context, localNote
						+ Constants.FTP_STATUS.NOTE_TXT_NAME);
				if (noteBarAdapter != null) {
					noteBarAdapter.notifyDataSetChanged();
				}
				if(noteBars != null && currentItem<noteBars.size()){
					String key = noteBars.get(currentItem).getBarName();
					noteBookAdapter = new NoteBookAdapter(NoteBookActivity.this,
							MediaCenter.getIns().getMapsNoteBookEntitys(key));
					noteContentLv.setAdapter(noteBookAdapter);
				}
			}
		});
	}

	public void xmlParser(File[] fileList) {
		for (int i = 0; i < fileList.length; i++) {
			String str = fileList[i].toString();
			if (str.contains(Constants.FTP_STATUS.NOTE_BAR_TXT_NAME)) {
				try {
					noteBars = SharedPreferencemanager.pullNoteBarsFromFile(
							this, str);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (str.contains(Constants.FTP_STATUS.NOTE_TXT_NAME)) {
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
					noteBookAdapter = new NoteBookAdapter(NoteBookActivity.this,
							MediaCenter.getIns().getMapsNoteBookEntitys(key));
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

	private void initView() {
		topLayout = (RelativeLayout) findViewById(R.id.top);
		noteBarLv = (ListView) findViewById(R.id.tabBar_listview);

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
					btnSure.setVisibility(View.VISIBLE);
					tvTitle.setText("添加记事本");
					photoAdapter = new NoteSubmitPhotoAdapter(context, false,
							imgUrls);
					gridView.setAdapter(photoAdapter);
					setStar(0);
					level = 0;
					etNoteContent.setText("");
					etNoteTitle.setText("");
				} else {
					noteBarAdapter.notifyDataSetChanged();
					noteBookAdapter.notifyDataSetChanged();
					String key = MediaCenter.getIns().getNoteBars()
							.get(currentItem).getBarName();
					noteBookAdapter = new NoteBookAdapter(
							NoteBookActivity.this, MediaCenter.getIns()
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
				LogUtil.log("currentItem = "
						+ currentItem
						+ " , size = "
						+ MediaCenter.getIns().getNoteBars().size()
						+ " , arg2 = "
						+ arg2
						+ " , size = "
						+ MediaCenter
								.getIns()
								.getMapsNoteBookEntitys(
										MediaCenter.getIns().getNoteBars()
												.get(currentItem).getBarName())
								.size());
				noteBookEntity = MediaCenter
						.getIns()
						.getMapsNoteBookEntitys(
								MediaCenter.getIns().getNoteBars()
										.get(currentItem).getBarName())
						.get(arg2 - 1);
				if (noteBookEntity.getImgUrl().size() > 0) {
					firstLayout.setVisibility(View.GONE);
					btnWrite.setVisibility(View.GONE);
					btnSure.setVisibility(View.VISIBLE);
					btnSure.setText(R.string.edit);
					noteThirdLayout.setVisibility(View.VISIBLE);

					imgName = noteBookEntity.getImgUrl().get(0);
					LogUtil.log("imgName = " + imgName);
					if (noteBookEntity.isOnline()) {
						new LoadImagesUtil(noteImg)
								.execute(new String[] { Constants.GET_QUEST_URI.GET_PICTURE_URI
										+ imgName + ".png" });
					} else {
						noteImg.setImageBitmap(ImageTools.decodeUriAsBitmap(imgName));
					}
					tvNoteDesc.setText(noteBookEntity.getNoteContent());
					tvNoteTitle.setText(noteBookEntity.getNoteTitle());
					tvNoteTime.setText(noteBookEntity.getNoteDate());
				} else {
					Toast.makeText(context, "图片没有上传成功", Toast.LENGTH_LONG)
							.show();
				}

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

	

	String key = "";
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
					key = "第" + ch[currentItem] + "阶段";
					List<NoteBookEntity> entitys = MediaCenter.getIns()
							.getMapsNoteBookEntitys(key);
					if (entitys == null) {
						currentItem--;
					}
					firstLayout.setVisibility(View.VISIBLE);
					secondLayout.setVisibility(View.GONE);
					btnWrite.setVisibility(View.VISIBLE);
					btnSure.setVisibility(View.GONE);
					tvTitle.setText("记事本");
				}
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
			MediaCenter.getIns().clearPhotos();
			firstLayout.setVisibility(View.GONE);
			secondLayout.setVisibility(View.VISIBLE);
			btnWrite.setVisibility(View.GONE);
			btnSure.setVisibility(View.VISIBLE);
			tvTitle.setText("添加记事本");
			photoAdapter = new NoteSubmitPhotoAdapter(context, false, imgUrls);
			gridView.setAdapter(photoAdapter);
			setStar(0);
			level = 0;
			etNoteContent.setText("");
			etNoteTitle.setText("");
			break;
		case R.id.sure:
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
				noteBookEntity.isOnline();
				photoAdapter = new NoteSubmitPhotoAdapter(this,
						noteBookEntity.isOnline(), noteBookEntity.getImgUrl());
				gridView.setAdapter(photoAdapter);
			} else {
				if (StringUtil.isEmpty(etNoteTitle.getText().toString())) {
					Toast.makeText(this, "标题不能为空", Toast.LENGTH_SHORT).show();
					etNoteTitle.requestFocus();
					return;
				}
				if (StringUtil.isEmpty(etNoteContent.getText().toString())) {
					Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();
					etNoteContent.requestFocus();
					return;
				}
				if (isEdit) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					noteBookEntity.setNoteDate(sdf.format(new Date()));
					if (imgUrls.size() > 0) {
						LogUtil.log("imgUrls = " + imgUrls.get(0));
						noteBookEntity.setOnline(false);
						noteBookEntity.setImgUrl(imgUrls);
					}
					noteBookEntity.setNoteContent(etNoteContent.getText()
							.toString());
					noteBookEntity.setNoteTitle(etNoteTitle.getText()
							.toString());
					noteBookEntity.setStarTag(level);
				} else {
					if (imgUrls != null && imgUrls.size() == 0) {
						Toast.makeText(this, "请添加图片", Toast.LENGTH_LONG).show();
						return;
					}
					key = "第" + ch[currentItem] + "阶段";
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
					entity.setNoteContent(etNoteContent.getText().toString());
					entity.setStarTag(level);
					Date date = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					entity.setNoteDate(sdf.format(date));
					entity.setImgUrl(imgUrls);
					entity.setOnline(false);
					entitys.add(entity);
					MediaCenter.getIns().addMapBookEntity(key, entitys);
					noteBookAdapter = new NoteBookAdapter(
							NoteBookActivity.this, MediaCenter.getIns()
									.getMapsNoteBookEntitys(key));
					noteContentLv.setAdapter(noteBookAdapter);
				}
				SharedPreferencemanager.pushNoteBarsToFile(MediaCenter.getIns()
						.getNoteBars(), context, localNote
						+ Constants.FTP_STATUS.NOTE_BAR_TXT_NAME);
				SharedPreferencemanager.pushNoteBookEntityToFile(MediaCenter
						.getIns().getMapsNote(), context, localNote
						+ Constants.FTP_STATUS.NOTE_TXT_NAME);
				new Thread(new Runnable() {
					@Override
					public void run() {
						FTPUtil.ftpUpload(remotePath, localNote,
								Constants.FTP_STATUS.NOTE_BAR_TXT_NAME);
						FTPUtil.ftpUpload(remotePath, localNote,
								Constants.FTP_STATUS.NOTE_TXT_NAME);
					}
				}).start();

				noteBookAdapter.notifyDataSetChanged();
				firstLayout.setVisibility(View.VISIBLE);
				secondLayout.setVisibility(View.GONE);
				btnWrite.setVisibility(View.VISIBLE);
				btnSure.setVisibility(View.GONE);
				isEdit = false;
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
					Constants.FTP_STATUS.WORKSPACE_NOTE_INFO), 0);
			break;
		case R.id.submit_photo_et:
			if (DeviceUtil.is3GorWifi()) {
				new Thread(new Runnable() {
					@Override
					public void run() {

						for (int i = 0; i < imgUrls.size(); i++) {
							String[] strs = imgUrls.get(i).split("/");
							LogUtil.log("ftpUpload = " + strs[strs.length - 1]);
							FTPUtil.ftpUpload("/" + phone
									+ Constants.FTP_STATUS.WORKSPACE_NOTE_INFO,
									localNote, strs[strs.length - 1] + ".png");
						}
					}
				}).start();
			} else {
				Toast.makeText(NoteBookActivity.this, "is not wifi",
						Toast.LENGTH_SHORT).show();
			}
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
				MediaCenter.getIns().clearPhotos();
				ImageTools.ShowPickDialog(NoteBookActivity.this);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LogUtil.log("resultCode = " + resultCode);
		if (resultCode != Activity.RESULT_OK) {// result is not correct
			return;
		} else {
			switch (requestCode) {
			case Constants.USER_STATUS.PHOTO_PICKED_WITH_DATA:
				// 照片的原始资源地址
				Uri originalUri = data.getData();
				try {
//					// 使用ContentProvider通过URI获取原始图片
					Bitmap smallBitmap = ImageTools.dealWithPhotoPicked(context, originalUri);
					if(smallBitmap != null){
						String imgName = String.valueOf(System
								.currentTimeMillis());
						ImageTools.savePhotoToSDCard(smallBitmap, localNote,
								imgName);
						imgUrls.add(localNote + imgName);
						photoAdapter = new NoteSubmitPhotoAdapter(this, false,
								imgUrls);
						gridView.setAdapter(photoAdapter);
						photoAdapter.notifyDataSetChanged();
					}
						
//					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case Constants.USER_STATUS.CAMERA_WITH_DATA:
				try {
					Bitmap newBitmap = ImageTools.dealWithCarmeaData();

					// 将处理过的图片显示在界面上，并保存到本地
					String imgName = String.valueOf(System.currentTimeMillis());
					ImageTools.savePhotoToSDCard(newBitmap, localNote, imgName);
					imgUrls.add(localNote + imgName);
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

				holder.tvTitle.setText(tab.substring(0, 2) + "\n"
						+ tab.substring(2));
				holder.tvTitle.setVisibility(View.VISIBLE);
				holder.ivAdd.setVisibility(View.GONE);
			} else {
				holder.tvTitle.setVisibility(View.GONE);
				holder.ivAdd.setVisibility(View.VISIBLE);
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
				key = "第" + ch[currentItem] + "阶段";
				List<NoteBookEntity> entitys = MediaCenter.getIns()
						.getMapsNoteBookEntitys(key);
				if (entitys == null) {
					currentItem--;
				}
				firstLayout.setVisibility(View.VISIBLE);
				secondLayout.setVisibility(View.GONE);
				btnWrite.setVisibility(View.VISIBLE);
				btnSure.setVisibility(View.GONE);
				tvTitle.setText("记事本");
			}
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
					if (FTPUtil.ftpDownload(remotePath, localNote,
							Constants.FTP_STATUS.NOTE_BAR_TXT_NAME)) {
						FTPUtil.ftpDownload(remotePath, localNote,
								Constants.FTP_STATUS.NOTE_TXT_NAME);
						File file = new File(localNote);
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
