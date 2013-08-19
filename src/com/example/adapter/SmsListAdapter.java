package com.example.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.constants.LogUtil;
import com.example.entity.SmsEntity;
import com.example.personalapp.R;

public class SmsListAdapter extends BaseAdapter {
	private ArrayList<SmsEntity> entitys;
	private LayoutInflater inflater;

	public SmsListAdapter(Context context, ArrayList<SmsEntity> entitys) {
		inflater = LayoutInflater.from(context);
		this.entitys = entitys;
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
		LogUtil.log("SmsListAdapter = " + position);
		ViewHolder holder;
		SmsEntity entity = entitys.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			if (entity.getType() == 1) {
				convertView = inflater.inflate(R.layout.sms_item_chat_from,
						null);
			} else if (entity.getType() == 2) {
				convertView = inflater.inflate(R.layout.sms_item_chat_to, null);
			}
			holder.tvContent = (TextView) convertView
					.findViewById(R.id.chat_tv);
			holder.tvTime = (TextView) convertView.findViewById(R.id.chat_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tvContent.setText(entity.getSmsbody());
		holder.tvTime.setText(entity.getDate());
		return convertView;
	}

	private class ViewHolder {
		private TextView tvContent;
		private TextView tvTime;
	}
}
