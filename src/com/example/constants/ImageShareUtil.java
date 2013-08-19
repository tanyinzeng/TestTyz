package com.example.constants;

import java.io.File;
import java.io.FileOutputStream;
import com.example.personalapp.PersonalApplication;
import com.example.personalapp.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageShareUtil {

	public static File saveOrReadLogo(String imgName) {
		File bitmapFile = new File(imgName + ".png");
		if (!bitmapFile.exists()) {
			FileOutputStream fos;
			try {
				Bitmap bitmap = BitmapFactory.decodeResource(
						PersonalApplication.getIns().getResources(),
						R.drawable.ic_launcher);

				bitmapFile.createNewFile();
				fos = new FileOutputStream(bitmapFile);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.close();
			} catch (Exception e) {
				System.out.println(" saveLogo e" + e.toString());
			}
		}

		return bitmapFile;
	}

}
