package com.example.personalapp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.taptwo.android.widget.CircleFlowIndicator;
import org.taptwo.android.widget.ViewFlow;
import com.example.adapter.CopyOfMyPicAdapter;
import com.example.adapter.NoteSubmitPhotoAdapter;
import com.example.adapter.TestAdapter;
import com.example.adapter.ViewFlowAdapter6;
import com.example.constants.AlertUtil;
import com.example.constants.Constants;
import com.example.constants.DeviceUtil;
import com.example.constants.FTPUtil;
import com.example.constants.ImageTools;
import com.example.constants.LogUtil;
import com.example.constants.SharedPreferencemanager;
import com.example.entity.MyPicEntity;
import com.example.logic.MediaCenter;
import com.example.view.SLCustomListView;
import com.example.view.SLCustomListView.OnRefreshListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PersonMyPicActivity extends BaseActivity implements
		OnClickListener, OnRefreshListener {
	private Button btnBack, btnAdd, btnSure, btnSubmit;
	private LinearLayout firstLayout, progressLayout,topTitle;
	private RelativeLayout twoLayout;

	private SLCustomListView listView;
	private GridView gridView;
	private CopyOfMyPicAdapter picAdapter;

	private String localMainPath, localMyPic, localPicPath, remotePath;
	private String phone = "";
	private static Handler mHandler;
	private String imgUrl = "";
	private TextView tvTitle;
	private GridView gridViewPhoto;
	private NoteSubmitPhotoAdapter photoAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mypic_layout);
		phone = SharedPreferencemanager.getPhone(getApplicationContext());
		localMainPath = Environment.getExternalStorageDirectory()
				+ Constants.FTP_STATUS.WORKSPACE_PERSONAL_INFO + phone;
		firstLayout = (LinearLayout) findViewById(R.id.first_layout);
		progressLayout = (LinearLayout) findViewById(R.id.progress_layout);
		MediaCenter.getIns().clearMyPics();
		initView();
		initData();
		initHandler();
		initNoteFourLayout();
		initTwoLayout();
	}

	private void initTwoLayout() {
		topTitle = (LinearLayout)findViewById(R.id.top_title);
		listView = (SLCustomListView) findViewById(R.id.my_pic_list);
		List<String> items = new ArrayList<String>();
		items.add("aa");
		TestAdapter adapter = new TestAdapter(this, items);
		listView.setAdapter(adapter);
		listView.setonRefreshListener(this);
		tvTitle = (TextView) findViewById(R.id.title);
		btnAdd = (Button) findViewById(R.id.write);
		btnSure = (Button) findViewById(R.id.sure);
		twoLayout = (RelativeLayout) findViewById(R.id.second_layout);
		btnAdd.setOnClickListener(this);
		btnSure.setOnClickListener(this);
		btnSubmit = (Button) findViewById(R.id.submit_photo_et);
		gridViewPhoto = (GridView)(GridView) findViewById(R.id.notebook_gridView);
		imgUrls = new ArrayList<String>();
		photoAdapter = new NoteSubmitPhotoAdapter(this, false, imgUrls);
		gridViewPhoto.setAdapter(photoAdapter);
		gridViewPhoto.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				ShowPickDialog();
			}
		});
		btnSubmit.setOnClickListener(this);
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
						ImageTools.savePhotoToSDCard(smallBitmap, localMyPic,
								imgName);
						imgUrl = localMyPic + imgName;
						imgUrls.add(imgUrl);
						photoAdapter = new NoteSubmitPhotoAdapter(this, false,
								imgUrls);
						gridViewPhoto.setAdapter(photoAdapter);
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
					ImageTools
							.savePhotoToSDCard(newBitmap, localMyPic, imgName);
					imgUrl = localMyPic + imgName;
					imgUrls.add(imgUrl);
					photoAdapter = new NoteSubmitPhotoAdapter(this, false,
							imgUrls);
					gridViewPhoto.setAdapter(photoAdapter);
					photoAdapter.notifyDataSetChanged();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}

	private FrameLayout fourlayout;
	// note four
	private ViewFlow mViewFlow;
	private CircleFlowIndicator mIndicator = null;

	private void initNoteFourLayout() {
		fourlayout = (FrameLayout) findViewById(R.id.notebook_fourlayout);
		mViewFlow = (ViewFlow) findViewById(R.id.viewflow);

		mIndicator = (CircleFlowIndicator) findViewById(R.id.viewflowindic);
		mViewFlow.setFlowIndicator(mIndicator);
	}

	private void initView() {
		btnBack = (Button) findViewById(R.id.back);
		btnBack.setOnClickListener(this);
	}

	private void initData() {
		gridView = (GridView) findViewById(R.id.my_pic_gridView);
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			localMyPic = localMainPath
					+ Constants.FTP_STATUS.WORKSPACE_MYPIC_INFO;
			localPicPath = localMainPath
					+ Constants.FTP_STATUS.WORKSPACE_MYPIC_INFO
					+ Constants.FTP_STATUS.MYPIC_TXT_NAME;
			remotePath = "/" + phone
					+ Constants.FTP_STATUS.WORKSPACE_MYPIC_INFO;
			new Thread(new Runnable() {
				@Override
				public void run() {
					File file = new File(localPicPath);
					if (file.exists()) {
						xmlParse();
					} else {
						if (FTPUtil.ftpDownload(remotePath, localMyPic,
								Constants.FTP_STATUS.MYPIC_TXT_NAME)) {
							file = new File(localMyPic);
							if (!file.exists()) {
								file.mkdirs();
							}
							xmlParse();
						} else {
							runOnUiThread(new Runnable() {
								public void run() {
									picAdapter = new CopyOfMyPicAdapter(
											PersonMyPicActivity.this,
											MediaCenter.getIns()
													.getPicEntitys(),
											Constants.FTP_STATUS.WORKSPACE_MYPIC_INFO);
									gridView.setAdapter(picAdapter);
									progressLayout.setVisibility(View.GONE);
								}
							});
						}
					}
				}
			}).start();

		}
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				LogUtil.log("onItemClick");
				firstLayout.setVisibility(View.GONE);
				fourlayout.setVisibility(View.VISIBLE);
				mViewFlow.setAdapter(new ViewFlowAdapter6(
						PersonMyPicActivity.this, MediaCenter.getIns()
						.getPicEntitys().get(arg2),
				Constants.FTP_STATUS.WORKSPACE_MYPIC_INFO), arg2);
			}
		});
	}

	private void xmlParse() {
		MediaCenter.getIns().clearPicEntity();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				List<MyPicEntity> entitys = SharedPreferencemanager
						.pullMyPicFromFile(PersonMyPicActivity.this,
								localPicPath);
				for(int i = 0;i<entitys.size();i++){
					MediaCenter.getIns().addPicEntity(entitys.get(i));
				}
				picAdapter = new CopyOfMyPicAdapter(PersonMyPicActivity.this,
						MediaCenter.getIns().getPicEntitys(), Constants.FTP_STATUS.WORKSPACE_MYPIC_INFO);
				gridView.setAdapter(picAdapter);
				progressLayout.setVisibility(View.GONE);
			}
		});
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mViewFlow.onConfigurationChanged(newConfig);
	}

	private void initHandler() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Constants.USER_STATUS.NOT_NETWORK:
					AlertUtil.showDialog(PersonMyPicActivity.this, "无网络");
					AlertUtil.disProgress();
					break;
				case Constants.USER_STATUS.MSG_SEND_REQUEST:
					AlertUtil.showProgress(PersonMyPicActivity.this);
					AlertUtil.disProgress();
					break;
				case Constants.USER_STATUS.MSG_SEND_FALIURE:
					AlertUtil.showDialog(PersonMyPicActivity.this, "请求失败");
					AlertUtil.disProgress();
					break;
				case Constants.USER_STATUS.MYPIC_ADAPTER_TO_PIC:
					int pos = (Integer) msg.obj;
					firstLayout.setVisibility(View.GONE);
					fourlayout.setVisibility(View.VISIBLE);
					topTitle.setVisibility(View.GONE);
					mViewFlow.setAdapter(new ViewFlowAdapter6(
							PersonMyPicActivity.this, MediaCenter.getIns()
									.getPicEntitys().get(pos),
							Constants.FTP_STATUS.WORKSPACE_MYPIC_INFO), pos);
					break;
				}
			}
		};
	}
	private List<String> imgUrls ;
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			if (twoLayout.getVisibility() == View.VISIBLE) {
				firstLayout.setVisibility(View.VISIBLE);
				btnAdd.setVisibility(View.VISIBLE);
				btnSure.setVisibility(View.GONE);
				twoLayout.setVisibility(View.GONE);
				tvTitle.setText("个人图片");
			} else {
				finish();
			}
			break;
		case R.id.write:
			imgUrls = new ArrayList<String>();
			firstLayout.setVisibility(View.GONE);
			twoLayout.setVisibility(View.VISIBLE);
			btnAdd.setVisibility(View.GONE);
			btnSure.setVisibility(View.VISIBLE);
			tvTitle.setText("添加个人图片");
			photoAdapter = new NoteSubmitPhotoAdapter(this, false, imgUrls);
			gridViewPhoto.setAdapter(photoAdapter);
			break;
		case R.id.sure:
			if (imgUrl.length() == 0) {
				Toast.makeText(this, "请选择图片", Toast.LENGTH_LONG).show();
				return;
			}
			MyPicEntity entity = new MyPicEntity();
			entity.setImgUrls(imgUrls);
			entity.setOnline(false);
			MediaCenter.getIns().addPicEntity(entity);
			firstLayout.setVisibility(View.VISIBLE);
			btnAdd.setVisibility(View.VISIBLE);
			btnSure.setVisibility(View.GONE);
			twoLayout.setVisibility(View.GONE);
			tvTitle.setText("个人图片");
			picAdapter.notifyDataSetChanged();
			SharedPreferencemanager.pushMyPicToFile(MediaCenter.getIns()
					.getPicEntitys(), PersonMyPicActivity.this, localPicPath);
			new Thread(new Runnable() {
				@Override
				public void run() {
					FTPUtil.ftpUpload(remotePath, localMyPic,
							Constants.FTP_STATUS.MYPIC_TXT_NAME);
				}
			}).start();
			break;
		case R.id.submit_photo_et:
			if (DeviceUtil.is3GorWifi()) {
				new Thread(new Runnable() {
					@Override
					public void run() {
							for(int i = 0;i<imgUrls.size();i++){
								String imgUrl = imgUrls.get(i);
								String[] strs = imgUrl.split("/");
								FTPUtil.ftpUpload(
										"/"
												+ phone
												+ Constants.FTP_STATUS.WORKSPACE_MYPIC_INFO,
										localMyPic, strs[strs.length - 1] + ".png");
							}
					}
				}).start();
			} else {
				Toast.makeText(PersonMyPicActivity.this, "is not wifi",
						Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}

	@Override
	public void onBackPressed() {
		if (fourlayout.getVisibility() == View.VISIBLE) {
			firstLayout.setVisibility(View.VISIBLE);
			fourlayout.setVisibility(View.GONE);
			topTitle.setVisibility(View.VISIBLE);
		} else if (twoLayout.getVisibility() == View.VISIBLE) {
			firstLayout.setVisibility(View.VISIBLE);
			btnAdd.setVisibility(View.VISIBLE);
			btnSure.setVisibility(View.GONE);
			twoLayout.setVisibility(View.GONE);
		} else {
			finish();
		}
	}

	public static void sendHandlerMessage(int what, Object object) {
		if (mHandler == null) {
			return;
		}
		Message msg = mHandler.obtainMessage(what, object);
		mHandler.sendMessage(msg);
	}

	@Override
	public void onRefresh() {
		if (DeviceUtil.is3GorWifi()) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					if (FTPUtil.ftpDownload(remotePath, localMyPic,
							Constants.FTP_STATUS.MYPIC_TXT_NAME)) {
						File file = new File(localMyPic);
						if (!file.exists()) {
							file.mkdirs();
						}
						xmlParse();
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
			Toast.makeText(this, "is not wifi", Toast.LENGTH_LONG).show();
			listView.onRefreshComplete();
		}
	}
}
