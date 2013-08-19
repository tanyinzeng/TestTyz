package com.example.adapter;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.constants.AsyncBitmapLoader;
import com.example.constants.Constants;
import com.example.constants.LogUtil;
import com.example.constants.AsyncBitmapLoader.ImageCallBack;
import com.example.personalapp.R;

public class NoteSubmitPhotoAdapter extends BaseAdapter {
	private List<String> imgUrls;
	private LayoutInflater inflater;
	private boolean isOnline;
	private AsyncBitmapLoader asynLoader;

	public NoteSubmitPhotoAdapter(Context context,boolean isOnline, List<String> imgUrls) {
		this.imgUrls = imgUrls;
		inflater = LayoutInflater.from(context);
		this.isOnline = isOnline;
		asynLoader = new AsyncBitmapLoader(context,
				Constants.FTP_STATUS.WORKSPACE_NOTE_INFO);
	}

	@Override
	public int getCount() {
		LogUtil.log("size = " + imgUrls.size());
		return imgUrls.size() + 1;
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

		if (imgUrls.size() > 0 && position < imgUrls.size()) {
			if(isOnline){
				String[] img = imgUrls.get(position).split("/");
				String imgUrl = imgUrls.get(position);
				if (img.length > 3) {
					LogUtil.log("length = " + img.length);
					imgUrl = img[4] + "/" + img[5] + "/"
							+ img[6];
				}
				Bitmap bmp = asynLoader.loadBitmap(viewHolder.ivPhoto,
						Constants.GET_QUEST_URI.GET_PICTURE_URI
								+ imgUrl + ".png",
						new ImageCallBack() {
							@Override
							public void imageLoad(ImageView imageView,
									Bitmap bitmap) {
								imageView.setImageBitmap(bitmap);
							}
						});
				if (bmp != null) {
					viewHolder.ivPhoto.setImageBitmap(bmp);
				}
				
			}else{
				try {
					InputStream inputStream = new FileInputStream(
							imgUrls.get(position) + ".png");
					Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
					viewHolder.ivPhoto.setImageBitmap(bitmap);
				} catch (Exception e) {
					LogUtil.log("getMessage = " + e.getMessage());
				}
			}
		}

		return convertView;
	}

	private class ViewHolder {
		private ImageView ivPhoto;
	}

}
