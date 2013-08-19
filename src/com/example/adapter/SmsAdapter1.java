package com.example.adapter;

import java.util.List;
import com.example.constants.AsyncBitmapLoader;
import com.example.constants.Constants;
import com.example.constants.AsyncBitmapLoader.ImageCallBack;
import com.example.entity.SmsOnlineEntity;
import com.example.personalapp.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SmsAdapter1 extends BaseAdapter {
	private List<SmsOnlineEntity> smsEntitys;
	private LayoutInflater inflater;
	private AsyncBitmapLoader asynLoader;

	public SmsAdapter1(Context context, List<SmsOnlineEntity> smsEntitys) {
		inflater = LayoutInflater.from(context);
		this.smsEntitys = smsEntitys;
		asynLoader = new AsyncBitmapLoader(context,"sms");
	}

	@Override
	public int getCount() {
		return smsEntitys.size();
	}

	@Override
	public Object getItem(int arg0) {
		return smsEntitys.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int pos, View contertView, ViewGroup arg2) {
		ViewHolder holder;
		if (contertView == null) {
			holder = new ViewHolder();
			contertView = inflater.inflate(R.layout.sms_list_item1, null);
			holder.ivHead = (ImageView) contertView
					.findViewById(R.id.sms_iv_head);
			holder.tvName = (TextView) contertView.findViewById(R.id.sms_name);
			holder.tvDate = (TextView) contertView.findViewById(R.id.sms_time);
			holder.tvContent = (TextView) contertView
					.findViewById(R.id.sms_content);
			contertView.setTag(holder);
		} else {
			holder = (ViewHolder) contertView.getTag();
		}
		SmsOnlineEntity entity = smsEntitys.get(pos);
		if (entity.getIcon() != null && entity.getIcon().length() > 0) {
			Bitmap bmp = asynLoader.loadBitmap(holder.ivHead,
					Constants.GET_QUEST_URI.ROOT_IMG
					+ entity.getIcon() , 
					new ImageCallBack() {
						@Override
						public void imageLoad(ImageView imageView, Bitmap bitmap) {
							imageView.setImageBitmap(bitmap);
						}
					});
			if(bmp != null){
				holder.ivHead.setImageBitmap(bmp);
			}
		}
		holder.tvName.setText(entity.getName());
		holder.tvDate.setText(entity.getDate());
		holder.tvContent.setText(entity.getContent());
		return contertView;
	}

	private class ViewHolder {
		private ImageView ivHead;
		private TextView tvName, tvDate, tvContent;
	}

}
