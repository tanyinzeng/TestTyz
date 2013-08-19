package com.example.adapter;

import java.util.List;
import com.example.constants.AsyncBitmapLoader;
import com.example.constants.Constants;
import com.example.constants.LogUtil;
import com.example.constants.AsyncBitmapLoader.ImageCallBack;
import com.example.personalapp.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ViewFlowAdapter1 extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater = null;
	private List<String> imgUrl;
	private AsyncBitmapLoader asynLoader;

	public ViewFlowAdapter1(Context ctx, List<String> imgUrl) {
		mContext = ctx;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.imgUrl = imgUrl;
		asynLoader = new AsyncBitmapLoader(ctx,"vir");
	}

	@Override
	public int getCount() {
		return imgUrl.size();
	}

	@Override
	public String getItem(int position) {
		return imgUrl.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.circle_viewflow_item_layout, null);
			holder = new ViewHolder();
			holder.iv = (ImageView) convertView.findViewById(R.id.img_view);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		LogUtil.log("pos = " + Constants.GET_QUEST_URI.ROOT_IMG
				+ imgUrl.get(position));
		Bitmap bmp = asynLoader.loadBitmap(holder.iv,
				Constants.GET_QUEST_URI.ROOT_IMG
				+ imgUrl.get(position) , 
				new ImageCallBack() {
					@Override
					public void imageLoad(ImageView imageView, Bitmap bitmap) {
						imageView.setImageBitmap(bitmap);
					}
				});
		if(bmp != null){
			holder.iv.setImageBitmap(bmp);
		}
		return convertView;
	}

	private class ViewHolder {
		private ImageView iv;
	}

}
