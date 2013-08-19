package com.example.adapter;

import java.util.List;

import com.example.personalapp.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TestAdapter extends BaseAdapter {
	private List<String> tests;
	private Context context;

	public TestAdapter(Context context, List<String> tests) {
		this.context = context;
		this.tests = tests;
	}

	@Override
	public int getCount() {
		return tests.size();
	}

	@Override
	public Object getItem(int position) {
		return tests.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewItem item;
		if(convertView == null){
			item = new ViewItem();
			convertView = LayoutInflater.from(context).inflate(R.layout.test_item, null);
			item.tvItem = (TextView)convertView.findViewById(R.id.test_item);
			convertView.setTag(item);
		}else{
			item = (ViewItem)convertView.getTag();
		}
		return convertView;
	}
	
	private class ViewItem{
		private TextView tvItem;
	}

}
