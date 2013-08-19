package com.example.adapter;

import java.util.List;

import com.example.entity.ScoreContentEntity;
import com.example.personalapp.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ScoreContentAdapter extends BaseAdapter {
	private List<ScoreContentEntity> entitys;
	private LayoutInflater inflater;

	public ScoreContentAdapter(Context context, List<ScoreContentEntity> entitys) {
		this.entitys = entitys;
		inflater = LayoutInflater.from(context);
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
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.score_content_list_item,
					null);
			holder.tvName = (TextView) convertView
					.findViewById(R.id.score_name);
			holder.tvTime = (TextView) convertView
					.findViewById(R.id.score_time);
			holder.tvScore = (TextView) convertView
					.findViewById(R.id.score_score);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ScoreContentEntity entity = entitys.get(position);
		holder.tvName.setText(entity.getName());
		holder.tvTime.setText(entity.getTime());
		holder.tvScore.setText(entity.getScore() + "·Ö");
		return convertView;
	}

	private class ViewHolder {
		private TextView tvName;
		private TextView tvTime;
		private TextView tvScore;
	}

}
