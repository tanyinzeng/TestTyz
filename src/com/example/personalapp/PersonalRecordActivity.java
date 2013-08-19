package com.example.personalapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.taptwo.android.widget.CircleFlowIndicator;
import org.taptwo.android.widget.ViewFlow;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.adapter.NoteSubmitPhotoAdapter;
import com.example.adapter.PersonRecordAdapter;
import com.example.adapter.TestAdapter;
import com.example.adapter.ViewFlowAdapter4;
import com.example.constants.AsyncBitmapLoader;
import com.example.constants.AsyncBitmapLoader.ImageCallBack;
import com.example.constants.Constants;
import com.example.constants.DeviceUtil;
import com.example.constants.FTPUtil;
import com.example.constants.ImageTools;
import com.example.constants.LogUtil;
import com.example.constants.SharedPreferencemanager;
import com.example.constants.StringUtil;
import com.example.entity.PersonRecordEntity;
import com.example.logic.MediaCenter;
import com.example.view.SLCustomListView;
import com.example.view.SLCustomListView.OnRefreshListener;

public class PersonalRecordActivity extends BaseActivity implements
		OnClickListener, OnRefreshListener {

	private RelativeLayout twoLayout, thirdLayout, topLayout;
	private LinearLayout firstLayout;
	private FrameLayout fourlayout;
	private Button btnAdd, btnSure, btnBack;
	private String phone, localMainPath, localRecord, remotePath;
	private List<String> imgUrls;
	private Context context;
	private LinearLayout progressLayout;
	private GridView recordgidView;
	private SLCustomListView listView;
	private PersonRecordAdapter recordAdapter;
	private AsyncBitmapLoader loader;

	// note four
	private ViewFlow mViewFlow;
	private CircleFlowIndicator mIndicator = null;
	private TextView tvTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.personal_record_list_layout);
		MediaCenter.getIns().clearRecords();
		topLayout = (RelativeLayout) findViewById(R.id.top_title);
		context = this;
		loader = new AsyncBitmapLoader(this,
				Constants.FTP_STATUS.WORKSPACE_TRACK_INFO);
		phone = SharedPreferencemanager.getPhone(getApplicationContext());
		localMainPath = Environment.getExternalStorageDirectory()
				+ Constants.FTP_STATUS.WORKSPACE_PERSONAL_INFO + phone;
		localRecord = localMainPath + Constants.FTP_STATUS.WORKSPACE_TRACK_INFO;
		remotePath = "/" + phone + Constants.FTP_STATUS.WORKSPACE_TRACK_INFO;
		progressLayout = (LinearLayout) findViewById(R.id.progress_layout);
		new Thread(new Runnable() {
			@Override
			public void run() {
				File file = new File(localRecord);
				if (!file.exists()) {
					file.mkdirs();
				}
				File[] fileList = file.listFiles();
				if (fileList.length > 0) {
					xmlParser(fileList);
				} else {
					if (FTPUtil.ftpDownload(remotePath, localRecord,
							Constants.FTP_STATUS.TRACK_TXT_NAME)) {
						file = new File(localRecord);
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
								initData();
							}
						});
					}
				}
			}
		}).start();
		initView();
		initSecondLayout();
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

	private void xmlParser(File[] fileList) {
		for (int i = 0; i < fileList.length; i++) {
			String str = fileList[i].toString();
			if (str.contains(Constants.FTP_STATUS.TRACK_TXT_NAME)) {
				try {
					List<PersonRecordEntity> noteBars = SharedPreferencemanager
							.pullPersonRecordFromFile(context, localRecord
									+ Constants.FTP_STATUS.TRACK_TXT_NAME);
					for (int j = 0; j < noteBars.size(); j++) {
						MediaCenter.getIns().addRecordEntity(noteBars.get(j));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				progressLayout.setVisibility(View.GONE);
				initData();
			}
		});
	}

	private void xmlParserOnline(File[] fileList) {
		MediaCenter.getIns().clearRecords();
		for (int i = 0; i < fileList.length; i++) {
			String str = fileList[i].toString();
			if (str.contains(Constants.FTP_STATUS.TRACK_TXT_NAME)) {
				try {
					List<PersonRecordEntity> noteBars = SharedPreferencemanager
							.pullPersonRecordFromFile(context, localRecord
									+ Constants.FTP_STATUS.TRACK_TXT_NAME);
					for (int j = 0; j < noteBars.size(); j++) {
						PersonRecordEntity entity = noteBars.get(j);
						entity.setOnline(true);
						MediaCenter.getIns().addRecordEntity(entity);
					}
					SharedPreferencemanager.pushPersonalRecordToFile(
							MediaCenter.getIns().getRecords(), context,
							localRecord + Constants.FTP_STATUS.TRACK_TXT_NAME);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				progressLayout.setVisibility(View.GONE);
				initData();
			}
		});
	}

	private void initData() {

		List<PersonRecordEntity> entitys = MediaCenter.getIns().getRecords();
		recordgidView = (GridView) findViewById(R.id.my_pic_gridView);
		listView = (SLCustomListView) findViewById(R.id.my_pic_list);
		List<String> items = new ArrayList<String>();
		items.add("aa");
		TestAdapter testAdapter = new TestAdapter(this, items);
		listView.setAdapter(testAdapter);
		listView.setonRefreshListener(this);
		recordAdapter = new PersonRecordAdapter(this, entitys);
		recordgidView.setAdapter(recordAdapter);
		recordgidView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				recordEntity = MediaCenter.getIns().getRecords().get(arg2);
				firstLayout.setVisibility(View.GONE);
				btnAdd.setVisibility(View.GONE);
				thirdLayout.setVisibility(View.VISIBLE);
				btnSure.setVisibility(View.VISIBLE);
				btnSure.setText(R.string.edit);
				imgName = recordEntity.getImgUrls().get(0) + ".png";
				LogUtil.log("imgName = " + imgName);
				if (recordEntity.isOnline()) {
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
				tvNoteDesc.setText(recordEntity.getTitle());
				tvNoteTime.setText(recordEntity.getTime());
			}
		});
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

	private void initNoteFourLayout() {
		fourlayout = (FrameLayout) findViewById(R.id.notebook_fourlayout);
		mViewFlow = (ViewFlow) findViewById(R.id.viewflow);

		mIndicator = (CircleFlowIndicator) findViewById(R.id.viewflowindic);
		mViewFlow.setFlowIndicator(mIndicator);
	}

	private PersonRecordEntity recordEntity;
	private String imgName;

	private EditText etTitle;
	private Button btnSubmit;
	private GridView gridView;
	private NoteSubmitPhotoAdapter photoAdapter;

	private void initSecondLayout() {
		etTitle = (EditText) findViewById(R.id.cost_title_et);
		btnSubmit = (Button) findViewById(R.id.submit_photo_et);
		gridView = (GridView) findViewById(R.id.notebook_gridView);
		btnSubmit.setOnClickListener(this);
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
						ImageTools.savePhotoToSDCard(smallBitmap, localRecord,
								imgName);
						imgUrls.add(localRecord + imgName);
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
					ImageTools.savePhotoToSDCard(newBitmap, localRecord,
							imgName);
					imgUrls.add(localRecord + imgName);
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

	private void initView() {
		firstLayout = (LinearLayout) findViewById(R.id.first_layout);
		twoLayout = (RelativeLayout) findViewById(R.id.second_layout);
		btnBack = (Button) findViewById(R.id.back);
		btnAdd = (Button) findViewById(R.id.add);
		btnSure = (Button) findViewById(R.id.sure);
		tvTitle = (TextView) findViewById(R.id.title);
		btnBack.setOnClickListener(this);
		btnAdd.setOnClickListener(this);
		btnSure.setOnClickListener(this);

	}

	private boolean isEdit = false;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			if (twoLayout.getVisibility() == View.VISIBLE) {
				if (isEdit) {
					isEdit = false;
					twoLayout.setVisibility(View.GONE);
					thirdLayout.setVisibility(View.VISIBLE);
					btnSure.setText(R.string.edit);
					tvTitle.setText("个人记录");
				} else {
					twoLayout.setVisibility(View.GONE);
					btnSure.setVisibility(View.GONE);
					btnAdd.setVisibility(View.VISIBLE);
					firstLayout.setVisibility(View.VISIBLE);
					tvTitle.setText("个人记录");
				}
			} else if (thirdLayout.getVisibility() == View.VISIBLE) {
				thirdLayout.setVisibility(View.GONE);
				btnAdd.setVisibility(View.VISIBLE);
				firstLayout.setVisibility(View.VISIBLE);
				btnSure.setVisibility(View.GONE);
			} else {
				finish();
			}
			break;
		case R.id.add:
			MediaCenter.getIns().clearPhotos();
			firstLayout.setVisibility(View.GONE);
			twoLayout.setVisibility(View.VISIBLE);
			btnAdd.setVisibility(View.GONE);
			btnSure.setVisibility(View.VISIBLE);
			imgUrls = new ArrayList<String>();
			photoAdapter = new NoteSubmitPhotoAdapter(context, false, imgUrls);
			gridView.setAdapter(photoAdapter);
			etTitle.setText("");
			tvTitle.setText("添加个人记录");
			break;
		case R.id.sure:
			if (btnSure.getText().toString()
					.equals(getResources().getString(R.string.edit))) {
				isEdit = true;
				thirdLayout.setVisibility(View.GONE);
				twoLayout.setVisibility(View.VISIBLE);
				btnSure.setText(R.string.sure);
				etTitle.setText(recordEntity.getTitle());
				imgUrls = new ArrayList<String>();
				photoAdapter = new NoteSubmitPhotoAdapter(this,
						recordEntity.isOnline(), recordEntity.getImgUrls());
				gridView.setAdapter(photoAdapter);
				tvTitle.setText("编辑个人记录");
			} else {
				String title = etTitle.getText().toString();
				if (StringUtil.isEmpty(title)) {
					Toast.makeText(this, "标题不能为空", Toast.LENGTH_LONG).show();
					etTitle.requestFocus();
					return;
				}
				if (isEdit) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					recordEntity.setTime(sdf.format(new Date()));
					if (imgUrls != null) {
						if (imgUrls.size() > 0) {
							recordEntity.setOnline(false);
							recordEntity.setImgUrls(imgUrls);
						}
					}
					recordEntity.setTitle(etTitle.getText().toString());

				} else {
					if (imgUrls.size() == 0) {
						Toast.makeText(this, "请添加图片", Toast.LENGTH_LONG).show();
						return;
					}
					PersonRecordEntity record = new PersonRecordEntity();
					record.setTitle(etTitle.getText().toString());
					record.setImgUrls(imgUrls);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					record.setTime(sdf.format(new Date()));
					MediaCenter.getIns().addRecordEntity(record);
				}
				SharedPreferencemanager.pushPersonalRecordToFile(MediaCenter
						.getIns().getRecords(), context, localRecord
						+ Constants.FTP_STATUS.TRACK_TXT_NAME);
				new Thread(new Runnable() {
					@Override
					public void run() {
						FTPUtil.ftpUpload(remotePath, localRecord,
								Constants.FTP_STATUS.TRACK_TXT_NAME);
					}
				}).start();
				twoLayout.setVisibility(View.GONE);
				btnSure.setVisibility(View.GONE);
				btnAdd.setVisibility(View.VISIBLE);
				firstLayout.setVisibility(View.VISIBLE);
				recordAdapter.notifyDataSetChanged();
				tvTitle.setText("个人记录");
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
											+ Constants.FTP_STATUS.WORKSPACE_TRACK_INFO,
									localRecord, strs[strs.length - 1] + ".png");
						}
					}
				}).start();
			} else {
				Toast.makeText(PersonalRecordActivity.this, "is not wifi",
						Toast.LENGTH_SHORT).show();
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
			thirdLayout.setVisibility(View.GONE);
			fourlayout.setVisibility(View.VISIBLE);
			topLayout.setVisibility(View.GONE);
			mViewFlow.setAdapter(new ViewFlowAdapter4(this, recordEntity), 0);
			break;
		}
	}

	@Override
	public void onBackPressed() {
		if (twoLayout.getVisibility() == View.VISIBLE) {
			if (isEdit) {
				isEdit = false;
				twoLayout.setVisibility(View.GONE);
				thirdLayout.setVisibility(View.VISIBLE);
				btnSure.setText(R.string.edit);
				tvTitle.setText("个人记录");
			} else {
				twoLayout.setVisibility(View.GONE);
				btnSure.setVisibility(View.GONE);
				btnAdd.setVisibility(View.VISIBLE);
				firstLayout.setVisibility(View.VISIBLE);
				tvTitle.setText("个人记录");
			}
		} else if (thirdLayout.getVisibility() == View.VISIBLE) {
			thirdLayout.setVisibility(View.GONE);
			btnAdd.setVisibility(View.VISIBLE);
			firstLayout.setVisibility(View.VISIBLE);
			btnSure.setVisibility(View.GONE);
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
					if (FTPUtil.ftpDownload(remotePath, localRecord,
							Constants.FTP_STATUS.TRACK_TXT_NAME)) {
						File file = new File(localRecord);
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
							listView.onRefreshComplete();
						}
					});

				}
			}).start();
		} else {
			listView.onRefreshComplete();
		}
	}

}
