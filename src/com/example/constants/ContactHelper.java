package com.example.constants;

import java.io.InputStream;

import com.example.personalapp.R;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

public class ContactHelper {
	private static String IVRNUMBERKEY = "ivrNumberKey";
	private static String IVRCONTACTIDKEY = "ivrContactIdKey";

	public static String getContactId(String number, Context context) {
		String id = "";
		try {
			String[] projection = { ContactsContract.CommonDataKinds.Phone.CONTACT_ID };

			Cursor phoneCursor = context.getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					projection,
					ContactsContract.CommonDataKinds.Phone.NUMBER + " = '"
							+ number + "'", null, null);
			Log.d("info", "phoneCursor.getCount() = " + phoneCursor.getCount());
			while (phoneCursor.moveToNext()) {
				id = phoneCursor.getString(0);
				break;
			}
			phoneCursor.close();
		} catch (Exception e) {
		}
		return id;
	}

	public static BitmapDrawable getPic(String id, Context context) {
		Uri uri = ContentUris.withAppendedId(
				ContactsContract.Contacts.CONTENT_URI, Integer.parseInt(id));
		InputStream inputStream = ContactsContract.Contacts
				.openContactPhotoInputStream(context.getContentResolver(), uri);
		if (null != inputStream) {
			Bitmap bmp = BitmapFactory.decodeStream(inputStream);
			BitmapDrawable bmpDraw = new BitmapDrawable(bmp);
			return bmpDraw;
		}
		return null;
	}

	public static BitmapDrawable getPicWithNumber(String number, Context context) {
		try {
			String id = getContactId(number, context);
			return getPic(id, context);
		} catch (Exception e) {
			return null;
		}
	}

	public static String getNameWithNumber(String number, Context context) {
		String nameString = null;
		try {
			if (number == null || number.length() == 0) {
				return "";
			}
			// ContactsContract.CommonDataKinds.Phone.TYPE = INTEGER
			String[] projection = { "display_name" };
			Cursor phoneCursor = context.getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					projection,
					ContactsContract.CommonDataKinds.Phone.NUMBER + " = "
							+ number, null, null);

			while (phoneCursor.moveToNext()) {
				nameString = phoneCursor.getString(0);
				break;
			}
			phoneCursor.close();
		} catch (Exception e) {
		}
		return nameString;
	}

}
