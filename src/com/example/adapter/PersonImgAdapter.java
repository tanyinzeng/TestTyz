package com.example.adapter;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.constants.Constants;
import com.example.constants.LogUtil;
import com.example.constants.SharedPreferencemanager;
import com.example.personalapp.R;

public class PersonImgAdapter extends BaseAdapter {
	private List<String> imgUrls;
	private LayoutInflater inflater;
	private Context context;

	public PersonImgAdapter(Context context, List<String> imgUrls) {
		this.context = context;
		this.imgUrls = imgUrls;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		if(imgUrls.size()<5){
			return imgUrls.size() + 1;
		}else{
			return imgUrls.size();
		}
		
	}

	@Override
	public Object getItem(int position) {
		return imgUrls.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private int mCount = 0;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LogUtil.log("NoteSubmitPhotoAdapter pos = " + position + " mCount = "
				+ mCount);
		if (position == 0) {
			mCount++;
		} else {
			mCount = 0;
		}

		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.upload_photo_layout, null);
			viewHolder.ivPhoto = (ImageView) convertView
					.findViewById(R.id.upload_img);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (mCount > 1) {
			return convertView;
		}

		if (position != 0) {
			mCount = 0;
		}

		if (imgUrls.size() > 0 ) {
			try {
				InputStream inputStream = new FileInputStream(
						Environment
						.getExternalStorageDirectory()
						+ Constants.FTP_STATUS.WORKSPACE_PERSONAL_INFO
						+ SharedPreferencemanager.getPhone(context)
						+ Constants.FTP_STATUS.WORKSPACE_PERSONAL_INFO+
						imgUrls.get(position) + ".png");
				Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
				viewHolder.ivPhoto.setImageBitmap(bitmap);
			} catch (Exception e) {
				LogUtil.log("getMessage = " + e.getMessage());
			}
		}

		return convertView;
	}

	private class ViewHolder {
		private ImageView ivPhoto;
	}

}
