package com.example.adapter;

import java.util.List;

import com.example.constants.AsyncBitmapLoader;
import com.example.constants.Constants;
import com.example.constants.LoadImagesUtil;
import com.example.constants.LogUtil;
import com.example.constants.SharedPreferencemanager;
import com.example.constants.AsyncBitmapLoader.ImageCallBack;
import com.example.entity.AppListCategory;
import com.example.personalapp.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PersonalAppCategoryItemAdapter extends BaseAdapter {
	private List<AppListCategory> categorys;
	private LayoutInflater inflater;
	private AsyncBitmapLoader asynLoader;

	public PersonalAppCategoryItemAdapter(Context context,
			List<AppListCategory> categorys,String path) {
		this.categorys = categorys;
		inflater = LayoutInflater.from(context);
		asynLoader = new AsyncBitmapLoader(context,Constants.FTP_STATUS.ZUO_PIN);
	}

	@Override
	public int getCount() {
		return categorys.size();
	}

	@Override
	public Object getItem(int arg0) {
		return categorys.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LogUtil.log("PersonalAppCategoryItemAdapter  = " + position);
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.personal_app_category_item,
					null);
			holder.ivCategory = (ImageView) convertView
					.findViewById(R.id.personal_category_item);
			holder.tvName = (TextView)convertView.findViewById(R.id.category_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		AppListCategory cate = categorys.get(position);
		holder.tvName.setText(cate.getName());
		Bitmap bmp = asynLoader.loadBitmap(holder.ivCategory,
				 Constants.GET_QUEST_URI.ROOT_IMG +cate.getPt_img(), 
				new ImageCallBack() {
					@Override
					public void imageLoad(ImageView imageView, Bitmap bitmap) {
						imageView.setImageBitmap(bitmap);
					}
				});
		if(bmp != null){
			holder.ivCategory.setImageBitmap(bmp);
		}
		return convertView;
	}

	private class ViewHolder {
		private ImageView ivCategory;
		private TextView tvName;
	}

}
