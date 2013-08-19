package com.example.constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

public class AsyncBitmapLoader {
	/**
	 * 内存图片软引用缓冲
	 */
	private HashMap<String, SoftReference<Bitmap>> imageCache = null;
	private String fromWhere;
	/**
	 * 图片缓存时间
	 */
	private static final int DELETE_TIME_INTERVAL = 1000 * 60 * 60 * 24 * 10;

	private static File cacheDir;
	private Context context;

	public AsyncBitmapLoader(Context context, String fromWhere) {
		this.fromWhere = fromWhere;
		imageCache = new HashMap<String, SoftReference<Bitmap>>();
		this.context = context;
	}

	public void clear() {
		cacheDir = null;
		if (imageCache != null) {
			try {

				releaseBitmapCache();
			} catch (Exception e) {
			}

			imageCache.clear();
			System.gc();
		}
	}

	public void releaseBitmapCache() {
		if (imageCache != null) {
			for (Entry<String, SoftReference<Bitmap>> entry : imageCache
					.entrySet()) {
				Bitmap bitmap = entry.getValue().get();
				if (bitmap != null) {
					bitmap = null;
				}
			}

		}
	}

	public Bitmap loadBitmap(final ImageView imageView, final String imageURL,
			final ImageCallBack imageCallBack) {
		LogUtil.log("imageURL = " + imageURL);
		// 在内存缓存中，则返回Bitmap对象
		if (imageCache.containsKey(imageURL)) {
			SoftReference<Bitmap> reference = imageCache.get(imageURL);
			Bitmap bitmap = reference.get();
			if (bitmap != null) {
				return bitmap;
			}
		} else {
			/**
			 * 加上一个对本地缓存的查找
			 */
			String bitmapName = imageURL
					.substring(imageURL.lastIndexOf("/") + 1);
			File cacheDir = new File("/mnt/sdcard/person/"
					+ SharedPreferencemanager.getPhone(context)+fromWhere);
			File[] cacheFiles = cacheDir.listFiles();
			int i = 0;
			if (null != cacheFiles) {
				for (; i < cacheFiles.length; i++) {
					if (bitmapName.equals(cacheFiles[i].getName())) {
						break;
					}
				}
				if (i < cacheFiles.length) {
					Bitmap bitmap = BitmapFactory
							.decodeFile("/mnt/sdcard/person/"
									+ SharedPreferencemanager.getPhone(context)+fromWhere
									+ bitmapName);

					return bitmap;
				}
			}
		}

		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				imageCallBack.imageLoad(imageView, (Bitmap) msg.obj);
			}
		};

		// 如果不在内存缓存中，也不在本地（被jvm回收掉），则开启线程下载图片
		new Thread() {
			@Override
			public void run() {
				InputStream bitmapIs = getStreamFromURL(imageURL);
				Bitmap bitmap;
				if (fromWhere.equals(Constants.FTP_STATUS.ZUO_PIN)) {
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 2;
					options.inJustDecodeBounds = false;
					options.inInputShareable = true;
					options.inPurgeable = true;
					options.inPreferredConfig = Bitmap.Config.RGB_565;
					bitmap = BitmapFactory
							.decodeStream(bitmapIs, null, options);
				} else {
					bitmap = BitmapFactory.decodeStream(bitmapIs);
				}

				if (bitmap == null) {
					return;
				}
				imageCache.put(imageURL, new SoftReference<Bitmap>(bitmap));
				Message msg = handler.obtainMessage(0, bitmap);
				handler.sendMessage(msg);

				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) { // sdcard
					cacheDir = new File("/mnt/sdcard/person/"
							+ SharedPreferencemanager.getPhone(context)+fromWhere);
				}
				if (cacheDir != null && !cacheDir.exists()) {
					cacheDir.mkdirs();
				}

				File bitmapFile = new File("/mnt/sdcard/person/"
						+ SharedPreferencemanager.getPhone(context)+fromWhere
						+ imageURL.substring(imageURL.lastIndexOf("/") + 1));
				if (!bitmapFile.exists()) {
					try {
						bitmapFile.createNewFile();
					} catch (Exception e) {
					}
				}
				FileOutputStream fos;
				try {
					fos = new FileOutputStream(bitmapFile);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
					fos.close();
				} catch (Exception e) {
				}
			}
		}.start();

		return null;
	}

	public InputStream getStreamFromURL(String imageURL) {
		InputStream in = null;
		try {
			URL url = new URL(imageURL);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			in = connection.getInputStream();

		} catch (Exception e) {
		}
		return in;

	}

	public interface ImageCallBack {
		public void imageLoad(ImageView imageView, Bitmap bitmap);
	}

	public void checkImageCache() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) { // sdcard
			cacheDir = new File("/mnt/sdcard/person/"
					+ SharedPreferencemanager.getPhone(context)+fromWhere);
		}
		if (cacheDir != null && (cacheDir.isDirectory())) {
			clearSdcardCache();// 清除缓存数据
		}
	}

	/**
	 * 清除图片
	 */
	private static void clearSdcardCache() {
		File[] files = cacheDir.listFiles();
		long currentTime = System.currentTimeMillis();
		if (files == null) {
			return;
		}
		for (File file : files) {
			if (currentTime - file.lastModified() > DELETE_TIME_INTERVAL) {
				file.delete();
				continue;
			}
		}
	}
}