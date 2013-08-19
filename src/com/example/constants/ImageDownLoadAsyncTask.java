package com.example.constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * 异步下载图片
 * 
 * @author sy
 * 
 */
public class ImageDownLoadAsyncTask extends AsyncTask<Void, Void, Bitmap> {
	private String imagePath, localPath;
	private ImageView imageView;
	private int Image_width;// 显示图片的宽�?
	private ProgressBar progressbar;

	/**
	 * 构�?方法
	 * 
	 * @param context
	 * @param imagePath
	 * @param imageView
	 */
	public ImageDownLoadAsyncTask(String imagePath, ImageView imageView,
			int Image_width, String localPath) {
		this.imagePath = imagePath;
		this.imageView = imageView;
		this.Image_width = Image_width;
		this.localPath = localPath;
	}

	public void setProgressbar(ProgressBar progressbar) {
		this.progressbar = progressbar;
	}

	@Override
	protected Bitmap doInBackground(Void... params) {

		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 8;
			options.inJustDecodeBounds = false;
			options.inInputShareable = true;
			options.inPurgeable = true;
			options.inPreferredConfig = Bitmap.Config.RGB_565;

			InputStream inputStream = new FileInputStream(imagePath);
			Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null,
					options);
			File file = new File(imagePath);
			int degree = PictureEdit.readPictureDegree(file.getAbsolutePath());
			if (degree == 0) { // δ��ת�����

			} else {
				Matrix matrix = new Matrix();
				matrix.setRotate(degree);
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
						bitmap.getHeight(), matrix, true);
			}

			String[] paths = imagePath.split("/");
			LogUtil.log("localPath = " + localPath);
			String filePath = localPath + "pic/" + paths[paths.length - 1];
			File file2 = new File(localPath + "pic/");
			if (!file2.exists()) {
				file2.mkdirs();
			}
			File file1 = new File(filePath);
			if (!file1.exists()) {
				file1.createNewFile();
			}
			try {
				FileOutputStream fos = new FileOutputStream(file1);
				if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)) {
					fos.flush();
					fos.close();
				}
			} catch (Exception e) {
				LogUtil.log("" + e.getMessage());
			}
			return bitmap;
		} catch (IOException e) {
			LogUtil.log("getMessage = " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(Bitmap drawable) {
		// TODO Auto-generated method stub
		super.onPostExecute(drawable);
		if (drawable != null) {
			LayoutParams layoutParams = imageView.getLayoutParams();
			int height = drawable.getHeight();// 获取图片的高�?
			int width = drawable.getWidth();// 获取图片的宽�?
			layoutParams.height = (height * Image_width) / width;
			imageView.setLayoutParams(layoutParams);
			imageView.setImageBitmap(drawable);
			if (drawable.isRecycled()) {
				drawable.recycle();
				System.gc();
			}
		}
		if (progressbar.isShown()) {
			progressbar.setVisibility(View.GONE);
		}

	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

	}
}