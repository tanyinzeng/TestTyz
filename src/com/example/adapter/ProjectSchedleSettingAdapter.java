package com.example.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.entity.NoteBarEntity;
import com.example.personalapp.R;

public class ProjectSchedleSettingAdapter extends BaseAdapter {
	private List<NoteBarEntity> tabs;
	private LayoutInflater inflater;

	public ProjectSchedleSettingAdapter(Context context,
			List<NoteBarEntity> tabs) {
		this.tabs = tabs;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return tabs.size();
	}

	@Override
	public Object getItem(int arg0) {
		return tabs.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(
					R.layout.project_schedle_setting_item, null);
			holder.tvTitle = (TextView) convertView
					.findViewById(R.id.setting_item_title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		NoteBarEntity entity = tabs.get(position);
		holder.tvTitle.setText(entity.getBarName());
		return convertView;
	}

	private class ViewHolder {
		private TextView tvTitle;
	}

}
