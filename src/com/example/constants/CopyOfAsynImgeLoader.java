package com.example.constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;

import com.example.logic.MediaCenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

public class CopyOfAsynImgeLoader {
	private HashMap<String, SoftReference<Drawable>> imageCache;
	private static Context context;

	public CopyOfAsynImgeLoader(Context context) {
		imageCache = new HashMap<String, SoftReference<Drawable>>();
		this.context = context;
	}

	public Drawable loadDrawable(final ImageView iv,final String imageUrl,
			final ImageCallback imageCallback) {
		if (imageCache.containsKey(imageUrl)) {
			SoftReference<Drawable> softReference = imageCache.get(imageUrl);
			Drawable drawable = softReference.get();
			if (drawable != null) {
				return drawable;
			}
		}
		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				imageCallback.imageLoaded((Drawable) message.obj, iv);
			}
		};
		new Thread() {
			@Override
			public void run() {
				Drawable drawable = loadImageFromUrl(imageUrl);
				imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
				Message message = handler.obtainMessage(0, drawable);
				handler.sendMessage(message);
			}
		}.start();
		return null;
	}

	public static Drawable loadImageFromUrl( String url) {

		Drawable drawable;
		try {

			InputStream inputStream = new FileInputStream(url);
			Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
			File file = new File(url);
			int degree = PictureEdit.readPictureDegree(file.getAbsolutePath());
			if (degree == 0) { // 未旋转的情况

			} else {
				Matrix matrix = new Matrix();
				matrix.setRotate(degree);
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
						bitmap.getHeight(), matrix, true);
			}

			String[] paths = url.split("/");
			String localMyPic = "";
			
			drawable = new BitmapDrawable(bitmap);
			return drawable;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public interface ImageCallback {
		public void imageLoaded(Drawable imageDrawable, ImageView iv);

	}
}
