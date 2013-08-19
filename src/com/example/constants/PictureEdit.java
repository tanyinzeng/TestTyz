package com.example.constants;

import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class PictureEdit {
	public static void toRoundCorner(Drawable d, int pixels,
			ImageView imageView, Rect dst) {
		if (d != null) {
			Bitmap bitmap = ((BitmapDrawable) d).getBitmap();

			// Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
			// bitmap.getHeight(), Config.ARGB_8888);
			// Canvas canvas = new Canvas(output);
			//
			// final int color = 0xff424242;
			// final Paint paint = new Paint();
			// final Rect rect = new Rect(0, 0, bitmap.getWidth(),
			// bitmap.getHeight());
			// final RectF rectF = new RectF(rect);
			// final float roundPx = pixels;
			//
			// paint.setAntiAlias(true);
			// canvas.drawARGB(0, 0, 0, 0);
			// paint.setColor(color);
			// canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
			//
			// paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			// canvas.drawBitmap(bitmap, rect, rect, paint);
			imageView.setImageBitmap(toRoundCornerBitmap(bitmap, pixels, dst));
		}
	}

	public static Bitmap toRoundCornerBitmap(Bitmap bitmap, int pixels, Rect dst) {
		Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		if (dst == null) {
			dst = src;
		}
		Bitmap output = Bitmap.createBitmap(dst.width(), dst.height(),
				Config.ARGB_8888);
		final int color = 0xff424242;
		final RectF dstF = new RectF(dst);
		final float roundPx = pixels;

		final Paint paint = new Paint();

		if (!dst.equals(src)) {
			paint.setAntiAlias(true);
			paint.setDither(true);
		}
		Canvas canvas = new Canvas(output);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(dstF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}

	public static LayoutParams getWidthAndHeight(Context content, int width,
			int height, int dp, ImageView imageView) {
		int previewWidth;
		int previewHeight;
		LayoutParams para = imageView.getLayoutParams();
		float density = content.getResources().getDisplayMetrics().density;
		previewWidth = (int) (dp * density);
		previewHeight = previewWidth * height / width;
		if (previewHeight >= 10)
			para.height = previewHeight;
		else
			para.height = 10;
		para.width = previewWidth;
		return para;
	}

	/*
	 * * 读取图片属性：旋转的角度
	 * 
	 * @param path 图片绝对路径
	 * 
	 * @return degree旋转的角度
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}
}
