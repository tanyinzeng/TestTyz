package com.example.adapter;

import java.util.List;

import com.example.constants.AsyncBitmapLoader;
import com.example.constants.Constants;
import com.example.constants.AsyncBitmapLoader.ImageCallBack;
import com.example.entity.PersonalAppEntity;
import com.example.personalapp.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PersonalAppListAdapter extends BaseAdapter {
	private List<PersonalAppEntity> entitys;
	private LayoutInflater inflater;
	private AsyncBitmapLoader asynLoader;

	public PersonalAppListAdapter(Context context,
			List<PersonalAppEntity> entitys) {
		this.entitys = entitys;
		inflater = LayoutInflater.from(context);
		asynLoader = new AsyncBitmapLoader(context,"aa");
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.personal_app_list_item,
					null);
			// holder.ivLevel = (ImageView) convertView
			// .findViewById(R.id.img_level);
			holder.ivImage = (ImageView) convertView
					.findViewById(R.id.personal_img);
			holder.tvName = (TextView) convertView
					.findViewById(R.id.personal_app_title);
			holder.tvFeature = (TextView) convertView
					.findViewById(R.id.personal_app_feature);
			holder.tvFrom = (TextView) convertView
					.findViewById(R.id.personal_app_from);
			holder.btnDetail = (Button) convertView
					.findViewById(R.id.check_detail);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		PersonalAppEntity entity = entitys.get(position);
		Bitmap bmp = asynLoader.loadBitmap(holder.ivImage,
				Constants.GET_QUEST_URI.ROOT_IMG
				+ entity.getImgUrl() , 
				new ImageCallBack() {
					@Override
					public void imageLoad(ImageView imageView, Bitmap bitmap) {
						imageView.setImageBitmap(bitmap);
					}
				});
		if(bmp != null){
			holder.ivImage.setImageBitmap(bmp);
		}
		holder.tvName.setText(entity.getName());
		return convertView;
	}

	private class ViewHolder {
		private ImageView ivLevel, ivImage;
		private TextView tvName, tvFeature, tvFrom;
		private Button btnDetail;
	}
}
