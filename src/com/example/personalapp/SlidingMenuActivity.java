package com.example.personalapp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.example.adapter.MyPicAdapter;
import com.example.adapter.VideoAdapter;
import com.example.constants.AlertUtil;
import com.example.constants.Constants;
import com.example.constants.LogUtil;
import com.example.constants.SharedPreferencemanager;
import com.example.logic.MediaCenter;
import com.example.view.SlidingMenu;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;

public class SlidingMenuActivity extends FragmentActivity implements
		OnClickListener {
	private SlidingMenu slidingMenu;
	private View centerView, rightView;
	
	// photo
	private Button btnBack, btnVideo;
	private List<String> image_filenames; // 图片集合
	private String localMainPath, localAlbum, localVideo;
	private String phone = "";
	private static Handler mHandler;
	private GridView gridView;
	private MyPicAdapter picAdapter;
	private LinearLayout progressLayout;

	// video
	private ListView listView;
	private List<String> image_video; // 图片集合

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sliding_activity);
		phone = SharedPreferencemanager.getPhone(getApplicationContext());
		localMainPath = Environment.getExternalStorageDirectory()
				+ Constants.FTP_STATUS.WORKSPACE_PERSONAL_INFO + phone;
		slidingMenu = (SlidingMenu) findViewById(R.id.sliding_menu);
		LayoutInflater inflater = LayoutInflater.from(this);

		rightView = inflater.inflate(R.layout.video_test_layout, null);
		slidingMenu.setRightView(rightView);

		centerView = inflater.inflate(R.layout.photo_layout, null);
		btnVideo = (Button) centerView.findViewById(R.id.video);

		slidingMenu.setCenterView(centerView);
		initPhotoView();
		initVideoView();
		initData();
		initHandler();
	}

	private void initPhotoView() {
		btnBack = (Button) centerView.findViewById(R.id.back);
		btnBack.setOnClickListener(this);
		btnVideo.setOnClickListener(this);
		
	}

	private void initVideoView() {
		listView = (ListView) rightView.findViewById(R.id.listView);
	}

	private void initData() {
		MediaCenter.getIns().clearMyPhoto();
		progressLayout =(LinearLayout) centerView.findViewById(R.id.progress_layout);
		gridView = (GridView) centerView.findViewById(R.id.my_pic_gridView);
		image_video = new ArrayList<String>();
		image_filenames = new ArrayList<String>();
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			localVideo = localMainPath
					+ Constants.FTP_STATUS.WORKSAPCE_VIDEO_INFO;
			localAlbum = localMainPath
					+ Constants.FTP_STATUS.WORKSPACE_ALBUM_INFO;
			File file = new File(localAlbum);
			File[] listFiles = file.listFiles();
			if (listFiles.length > 0) {
				for (int i = 0; i < listFiles.length; i++) {
					String imgName = listFiles[i].toString();
					LogUtil.log("imgName = "+imgName);
					if(!imgName.endsWith("pic")){
						LogUtil.log("imgName = "+imgName);
						image_filenames.add(imgName);
					}
				}
				progressLayout.setVisibility(View.GONE);
				picAdapter = new MyPicAdapter(this, image_filenames,
						Constants.FTP_STATUS.WORKSPACE_ALBUM_INFO);
				gridView.setAdapter(picAdapter);
			} 
			file = new File(localVideo);
			listFiles = file.listFiles();
			if (listFiles.length > 0) {
				for (int i = 0; i < listFiles.length; i++) {
					image_video.add(listFiles[i].toString());
				}
				// 第一次加载
				VideoAdapter adapter = new VideoAdapter(this, image_video);
				listView.setAdapter(adapter);
			} 
		}
		if ( image_filenames.size() == 0) {
			AlertUtil
					.showAlert(
							SlidingMenuActivity.this,
							R.string.app_name,
							"请将图片保存在"
									+ Constants.FTP_STATUS.WORKSPACE_PERSONAL_INFO
									+ SharedPreferencemanager
											.getPhone(getApplicationContext())
									+ Constants.FTP_STATUS.WORKSPACE_ALBUM_INFO
									,
							"确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(
										DialogInterface dialog,
										int which) {
									dialog.dismiss();
									finish();
								}
							});
		}
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(SlidingMenuActivity.this,
						ShowPhotoItemActivity.class);
				intent.putStringArrayListExtra("imgUrls",
						(ArrayList<String>) MediaCenter.getIns().getMyPhoto());
				intent.putExtra("pos", arg2);
				startActivity(intent);
			}
		});
	}

	private void initHandler() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Constants.USER_STATUS.MYPIC_ADAPTER_TO_PIC:
					int pos = (Integer)msg.obj;
					Intent intent = new Intent(SlidingMenuActivity.this,
							ShowPhotoItemActivity.class);
					intent.putStringArrayListExtra("imgUrls",
							(ArrayList<String>) MediaCenter.getIns().getMyPhoto());
					intent.putExtra("pos", pos);
					startActivity(intent);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.video:
			if(image_video.size()>0){
				AlertUtil
				.showAlert(
						SlidingMenuActivity.this,
						R.string.app_name,
						"请将视频保存在"
								+ Constants.FTP_STATUS.WORKSPACE_PERSONAL_INFO
								+ SharedPreferencemanager
										.getPhone(getApplicationContext())
								+ Constants.FTP_STATUS.WORKSAPCE_VIDEO_INFO
								,
						"确定",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(
									DialogInterface dialog,
									int which) {
								dialog.dismiss();
								finish();
							}
						});
				return;
			}
			slidingMenu.showRightView();
			break;
		case R.id.back:
			finish();
			break;
		}
	}

	

	
}
