package com.example.adapter;

import java.util.List;

import com.example.personalapp.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NoteBarAdapter extends BaseAdapter {
	private List<String> strs;
	private LayoutInflater inflater;

	public NoteBarAdapter(Context context, List<String> strs) {
		inflater = LayoutInflater.from(context);
		this.strs = strs;
	}

	@Override
	public int getCount() {
		return strs.size();
	}

	@Override
	public Object getItem(int position) {
		return strs.get(position);
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
			convertView = inflater.inflate(R.layout.notebook_tabbar_listitem,
					null);
			holder.tvTitle = (TextView) convertView
					.findViewById(R.id.notebar_title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// if(position == 0){
		// convertView.setBackgroundResource(R.drawable.note_btn_sel);
		// }else{
		// convertView.setBackgroundResource(R.drawable.note_btn);
		// }
		holder.tvTitle.setText(strs.get(position));
		return convertView;
	}

	private class ViewHolder {
		private TextView tvTitle;
	}
}
