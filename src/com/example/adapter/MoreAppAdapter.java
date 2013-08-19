package com.example.adapter;

import java.util.List;

import com.example.constants.AsyncBitmapLoader;
import com.example.constants.Constants;
import com.example.constants.LoadImagesUtil;
import com.example.constants.LogUtil;
import com.example.constants.AsyncBitmapLoader.ImageCallBack;
import com.example.entity.MoreAppEntity;
import com.example.personalapp.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MoreAppAdapter extends BaseAdapter {
	private List<MoreAppEntity> entitys;
	private LayoutInflater inflater;
	private AsyncBitmapLoader asynLoader;

	public MoreAppAdapter(Context context, List<MoreAppEntity> entitys) {
		this.entitys = entitys;
		inflater = LayoutInflater.from(context);
		asynLoader = new AsyncBitmapLoader(context,"/more/");
	}

	@Override
	public int getCount() {
		return entitys.size();
	}

	@Override
	public Object getItem(int position) {
		return entitys.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LogUtil.log("MoreAppAdapter = " + position);
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.more_app_listitem, null);
			holder.ivIcon = (ImageView) convertView
					.findViewById(R.id.more_icon);
			holder.tvTitle = (TextView) convertView
					.findViewById(R.id.more_title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		MoreAppEntity entity = entitys.get(position);
//		new LoadImagesUtil(holder.ivIcon)
//				.execute(new String[] { Constants.GET_QUEST_URI.ROOT_IMG
//						+ entity.getImg() });
		Bitmap bmp = asynLoader.loadBitmap(holder.ivIcon,
				Constants.GET_QUEST_URI.ROOT_IMG
				+ entity.getImg() , 
				new ImageCallBack() {
					@Override
					public void imageLoad(ImageView imageView, Bitmap bitmap) {
						imageView.setImageBitmap(bitmap);
					}
				});
		if(bmp != null){
			holder.ivIcon.setImageBitmap(bmp);
		}
		holder.tvTitle.setText(entity.getTitle());
		return convertView;
	}

	private class ViewHolder {
		private ImageView ivIcon;
		private TextView tvTitle;
	}

}
