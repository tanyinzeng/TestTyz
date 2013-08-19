package com.example.personalapp;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import com.example.constants.Constants;
import com.example.constants.DeviceUtil;
import com.example.constants.FTPUtil;
import com.example.constants.LogUtil;
import com.example.constants.SharedPreferencemanager;
import com.example.entity.FeelingContentEntity;
import com.example.entity.NoteBarEntity;
import com.example.entity.NoteBookEntity;
import com.example.entity.PersonRecordEntity;
import com.example.entity.UserInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;

public class UploadService extends Service {
	private String localMainPath, localPersonPath, remotePath, localNotePath,
			localProject, localRecord, localScorePath, localMyPic;
	private Context context;

	// 定时任务,定时发送message
	private class myTimerTask extends TimerTask {
		@Override
		public void run() {
			localMainPath = Environment.getExternalStorageDirectory()
					+ Constants.FTP_STATUS.WORKSPACE_PERSONAL_INFO
					+ SharedPreferencemanager.getPhone(context);
			localPersonPath = localMainPath
					+ Constants.FTP_STATUS.WORKSPACE_PERSONAL_INFO;
			remotePath = "/" + SharedPreferencemanager.getPhone(context);
			localNotePath = localMainPath
					+ Constants.FTP_STATUS.WORKSPACE_NOTE_INFO;
			localRecord = localMainPath
					+ Constants.FTP_STATUS.WORKSPACE_TRACK_INFO;
			localScorePath = localMainPath
					+ Constants.FTP_STATUS.WORKSPACE_CHENGJI_INFO;
			localMyPic = localMainPath
					+ Constants.FTP_STATUS.WORKSPACE_MYPIC_INFO;
			if (DeviceUtil.is3GorWifi()) {
				try {

					File file = new File(localPersonPath
							+ Constants.FTP_STATUS.PERSON_TXT_NAME);
					if (file.exists()) {
						UserInfo userInfo = SharedPreferencemanager
								.pullUserInfoFromFile(context, localPersonPath
										+ Constants.FTP_STATUS.PERSON_TXT_NAME);
						LogUtil.log("getImgName = " + userInfo.getImgName());
						if (userInfo.getImgName() != null
								&& userInfo.getImgName().length() > 0) {
							if ((new File(localPersonPath
									+ userInfo.getImgName() + ".png").exists())) {
								FTPUtil.ftpUpload(
										remotePath
												+ Constants.FTP_STATUS.WORKSPACE_PERSONAL_INFO,
										localPersonPath, userInfo.getImgName()
												+ ".png");
							}
						}
						if (userInfo.getBackgroundName() != null
								&& userInfo.getBackgroundName().size() > 0) {
							for(int i = 0;i<userInfo.getBackgroundName().size();i++){
								String imgUrl = userInfo.getBackgroundName().get(i);
								if ((new File(localPersonPath
										+ imgUrl + ".png")
										.exists())) {
									FTPUtil.ftpUpload(
											remotePath
													+ Constants.FTP_STATUS.WORKSPACE_PERSONAL_INFO,
											localPersonPath,
											imgUrl + ".png");
								}
							}
						}
						file = new File(localNotePath
								+ Constants.FTP_STATUS.NOTE_BAR_TXT_NAME);
						if (file.exists()) {
							List<NoteBarEntity> barEntitys = SharedPreferencemanager
									.pullNoteBarsFromFile(
											context,
											localNotePath
													+ Constants.FTP_STATUS.NOTE_BAR_TXT_NAME);
							Map<String, List<NoteBookEntity>> maps = SharedPreferencemanager
									.pullNoteEntityFromFile(
											context,
											localNotePath
													+ Constants.FTP_STATUS.NOTE_TXT_NAME);
							for (int i = 0; i < barEntitys.size(); i++) {
								NoteBarEntity barEntity = barEntitys.get(i);
								List<NoteBookEntity> noteBooks = maps
										.get(barEntity.getBarName());
								for (int j = 0; j < noteBooks.size(); j++) {
									NoteBookEntity entity = noteBooks.get(j);
									List<String> imgUrls = entity.getImgUrl();
									for (int t = 0; t < imgUrls.size(); t++) {
										String[] strs = imgUrls.get(t).split(
												"/");
										FTPUtil.ftpUpload(
												remotePath
														+ Constants.FTP_STATUS.WORKSPACE_NOTE_INFO,
												localNotePath,
												strs[strs.length - 1] + ".png");
									}
								}
							}
						}
						localProject = localMainPath
								+ Constants.FTP_STATUS.WORKSPACE_PROJECT_INFO;
						file = new File(localProject
								+ Constants.FTP_STATUS.PROJECT_BAR_TXT_NAME);
						if (file.exists()) {
							List<NoteBarEntity> barEntitys = SharedPreferencemanager
									.pullNoteBarsFromFile(
											context,
											localProject
													+ Constants.FTP_STATUS.PROJECT_BAR_TXT_NAME);
							Map<String, List<NoteBookEntity>> maps = SharedPreferencemanager
									.pullNoteEntityFromFile(
											context,
											localProject
													+ Constants.FTP_STATUS.PROJECT_TXT_NAME);
							for (int i = 0; i < barEntitys.size(); i++) {
								NoteBarEntity barEntity = barEntitys.get(i);
								List<NoteBookEntity> noteBooks = maps
										.get(barEntity.getBarName());
								for (int j = 0; j < noteBooks.size(); j++) {
									NoteBookEntity entity = noteBooks.get(j);
									List<String> imgUrls = entity.getImgUrl();
									for (int t = 0; t < imgUrls.size(); t++) {
										String[] strs = imgUrls.get(t).split(
												"/");
										FTPUtil.ftpUpload(
												remotePath
														+ Constants.FTP_STATUS.WORKSPACE_PROJECT_INFO,
												localProject,
												strs[strs.length - 1] + ".png");
									}
								}
							}
						}
						if ((new File(localRecord
								+ Constants.FTP_STATUS.TRACK_TXT_NAME))
								.exists()) {
							List<PersonRecordEntity> records = SharedPreferencemanager
									.pullPersonRecordFromFile(
											context,
											localRecord
													+ Constants.FTP_STATUS.TRACK_TXT_NAME);
							for (int j = 0; j < records.size(); j++) {
								PersonRecordEntity entity = records.get(j);
								List<String> imgUrls = entity.getImgUrls();
								for (int t = 0; t < imgUrls.size(); t++) {
									String[] strs = imgUrls.get(t).split("/");
									FTPUtil.ftpUpload(
											remotePath
													+ Constants.FTP_STATUS.WORKSPACE_TRACK_INFO,
											localRecord, strs[strs.length - 1]
													+ ".png");
								}
							}
						}
						if ((new File(localScorePath
								+ Constants.FTP_STATUS.FEELING_TXT_NAME))
								.exists()) {
							List<FeelingContentEntity> feels = SharedPreferencemanager
									.pullFeelContentFromFile(
											context,
											localScorePath
													+ Constants.FTP_STATUS.FEELING_TXT_NAME);
							for (int i = 0; i < feels.size(); i++) {
								FeelingContentEntity entity = feels.get(i);
								List<String> imgUrls = entity.getImgUrl();
								for (int t = 0; t < imgUrls.size(); t++) {
									String[] strs = imgUrls.get(t).split("/");
									FTPUtil.ftpUpload(
											remotePath
													+ Constants.FTP_STATUS.WORKSPACE_TRACK_INFO,
											localScorePath,
											strs[strs.length - 1] + ".png");
								}
							}
						}
						file = (new File(localMyPic));
						File[] listFiles = file.listFiles();
						if (listFiles.length > 0) {
							for(int i = 0;i<listFiles.length;i++){
								String imgName = listFiles[i].toString();
								String[] strs = imgName.split(
										"/");
								FTPUtil.ftpUpload(remotePath
												+ Constants.FTP_STATUS.WORKSPACE_MYPIC_INFO,
										localMyPic + "pic/",
										strs[strs.length - 1]);
							}
						}
						
					}
				} catch (Exception e) {
					LogUtil.log("ftp客户端出错");
				}
			}
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		context = this;
		myTimerTask timerTask = new myTimerTask();
		Timer timer = new Timer(true);
		timer.schedule(timerTask, 0, 50000);// 定时10分钟执行一次
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public void onDestroy() {
		Intent localIntent = new Intent();
		localIntent.setClass(this, UploadService.class); // 销毁时重新启动Service
		this.startService(localIntent);
		super.onDestroy();
	}
}
