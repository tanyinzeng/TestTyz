package com.example.adapter;

import java.util.List;
import com.example.constants.LogUtil;
import com.example.personalapp.PlayActivity;
import com.example.personalapp.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore.Images;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

public class VideoAdapter extends BaseAdapter {
	private List<String> imges;
	private LayoutInflater inflater;
	private Context context;

	public VideoAdapter(Context context, List<String> images) {
		this.imges = images;
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return imges.size();
	}

	@Override
	public Object getItem(int arg0) {
		return imges.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LogUtil.log("videoAdapter = " + position);
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.video_layout, null);
			holder.ivVideo = (ImageView) convertView
					.findViewById(R.id.video_img);
			holder.btnPlay = (Button) convertView.findViewById(R.id.video_play);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(
				imges.get(position), Images.Thumbnails.MINI_KIND);
		holder.ivVideo.setImageBitmap(bitmap);
		holder.ivVideo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, PlayActivity.class);
				intent.putExtra("uri", imges.get(position));
				context.startActivity(intent);
			}
		});
		holder.btnPlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, PlayActivity.class);
				intent.putExtra("uri", imges.get(position));
				context.startActivity(intent);
			}
		});
		return convertView;
	}

	private class ViewHolder {
		private ImageView ivVideo;
		private Button btnPlay;
	}

}
