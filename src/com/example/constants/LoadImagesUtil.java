package com.example.constants;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class LoadImagesUtil extends AsyncTask<String[], Void, Drawable[]> {
	private Drawable[] drawables;
	ImageView imageView;

	public LoadImagesUtil(ImageView imageView) {
		this.imageView = imageView;
	}

	@Override
	protected Drawable[] doInBackground(String[]... params) {
		drawables = new Drawable[params[0].length];
		for (int i = 0; i < params[0].length; i++) {
			URL url;
			try {
				url = new URL(params[0][i]);
				LogUtil.log("url = " + url);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setDoInput(true);
				conn.connect();
				InputStream inputStream = conn.getInputStream();
				BitmapFactory.Options opts2 = new BitmapFactory.Options();
				opts2.inJustDecodeBounds = false;
				opts2.inInputShareable = true;
				opts2.inPurgeable = true;
				opts2.inPreferredConfig = Bitmap.Config.RGB_565;

				Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null,
						opts2);
				BitmapDrawable bd = new BitmapDrawable(bitmap);
				drawables[i] = bd;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return drawables;
	}

	@Override
	protected void onPostExecute(Drawable[] result) {
		super.onPostExecute(result);
		if (result != null && result.length > 0) {
			imageView.setImageDrawable(result[0]);
			imageView.setScaleType(ScaleType.FIT_XY);
		}
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

}
