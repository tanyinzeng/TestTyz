package com.example.constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.text.format.DateFormat;
import android.util.Log;

public class CommUtil {
	public static String getStrFromStringList(List<String> strList) {
		StringBuffer sb = new StringBuffer();
		for (String str : strList) {
			sb.append(str).append(",");
		}
		return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : sb
				.toString();
	}

	/**
	 * 
	 * @author coolgo(winerdaxian@163.com)
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String getStrFromDate(Date date, String format) {
		if (format == null || "".equals(format.trim())) {
			format = "kk:mm MM月dd日 EEE"; // 24小时制
		}
		return DateFormat.format(format, date).toString();
	}

	/**
	 * 判断字符串是否为空
	 * 
	 * @author coolgo(winerdaxian@163.com)
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str) {
		if (str == null)
			return true;
		if ("".equals(str.trim())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 存放自拍自传文件的路径
	 * 
	 * @author coolgo(winerdaxian@163.com)
	 * 
	 * @return
	 */
	public static String getBasePhotoDirToPhone() {
		File dir = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/DCIM/iclass-photos");
		if (!dir.exists()) {
			dir.mkdir();
		}

		return dir.getAbsolutePath();
	}

	/**
	 * 处理上传图片压缩的问题
	 * 
	 * @author coolgo(winerdaxian@163.com)
	 * 
	 * @param originFile
	 * @param maxSize
	 * @return
	 */
	public static byte[] compressFileToJPEGOfMaxSize(File originFile,
			int maxSize) {
		byte[] result = null;

		int inSampleSize = 1;
		long fileLength = originFile.length();
		if (fileLength > maxSize) {
			double temp = Math.sqrt(fileLength / maxSize);
			double tempSize = Math.ceil(temp);
			inSampleSize += (int) tempSize;
		}
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		Bitmap originBitmap = null;
		try {
			Options opts = new Options();
			opts.inSampleSize = inSampleSize;
			originBitmap = BitmapFactory.decodeFile(
					originFile.getAbsolutePath(), opts);
			int degree = PictureEdit.readPictureDegree(originFile
					.getAbsolutePath());
			if (degree == 0) { // 未旋转的情况
				originBitmap.compress(CompressFormat.JPEG, 100,
						byteArrayOutputStream);
			} else {
				Matrix matrix = new Matrix();
				matrix.setRotate(degree);
				Bitmap destinationBitmap = Bitmap.createBitmap(originBitmap, 0,
						0, originBitmap.getWidth(), originBitmap.getHeight(),
						matrix, true);
				destinationBitmap.compress(CompressFormat.JPEG, 100,
						byteArrayOutputStream);
			}
			result = byteArrayOutputStream.toByteArray();
			byteArrayOutputStream.close();
		} catch (Exception e) {
			Log.e("compressFileToJPEGOfMaxSize", e.getMessage(), e);
		}
		return result;
	}

	/**
	 * 从图库中获取uri的信息
	 * 
	 * @author coolgo(winerdaxian@163.com)
	 * 
	 * @param activity
	 * @param uri
	 * @return
	 */
	public static String getImagePathFromMediaStore(Activity activity, Uri uri) {
		String[] proj = { Media.DATA, Media.ORIENTATION };
		Cursor cursor = activity.managedQuery(uri, proj, // Which columns to
															// return
				null, // WHERE clause; which rows to return (all rows)
				null, // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)
		int dataIndex = cursor.getColumnIndexOrThrow(Media.DATA);
		cursor.moveToFirst();
		String picUriPath = cursor.getString(dataIndex);
		return picUriPath;
	}

	/**
	 * 获取md5码
	 * 
	 * @author coolgo(winerdaxian@163.com)
	 * 
	 * @param val
	 * @return
	 */
	public static String getMD5(String val) {
		String str_md5 = "";
		try {
			byte[] m;
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(val.getBytes());
			m = md5.digest();
			str_md5 = getString(m);
		} catch (NoSuchAlgorithmException e) {
			Log.e("getMd5", e.getLocalizedMessage(), e);
		}
		return str_md5;
	}

	private static String getString(byte[] b) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			sb.append(b[i]);
		}
		return sb.toString();
	}
}
