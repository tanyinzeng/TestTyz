package com.example.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.entity.CostSpendEntity;
import com.example.personalapp.R;

/**
 * 
 * @author tyz
 * 
 */
public class ExpandableListViewaAdapter extends BaseExpandableListAdapter {
	private Context context;
	private List<CostSpendEntity> groupArray;// 组列表
	private List<List<CostSpendEntity>> childArray;// 子列表

	public ExpandableListViewaAdapter(Context context,
			List<CostSpendEntity> groupArray,
			List<List<CostSpendEntity>> childArray) {
		this.context = context;
		this.groupArray = groupArray;
		this.childArray = childArray;
	}

	/*-----------------Child */
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		Log.i("info", "getChild");
		return childArray.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		Log.i("info", "getChildId");
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		childHolder holder;
		if (convertView == null) {
			holder = new childHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.test_child_view, null);
			holder.tvTitle = (TextView) convertView
					.findViewById(R.id.group_title);
			holder.tvMoney = (TextView) convertView
					.findViewById(R.id.group_money);
			convertView.setTag(holder);
		} else {
			holder = (childHolder) convertView.getTag();
		}
		Log.i("info", "getChildView");
		CostSpendEntity entity = childArray.get(groupPosition).get(
				childPosition);
		holder.tvTitle.setText(entity.getTitle());
		holder.tvMoney.setText("￥" + entity.getMoney());
		return convertView;
	}

	private class childHolder {
		private TextView tvTitle, tvMoney;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		Log.i("info", "getChildrenCount");
		if (childArray.size() > 0 && childArray.size() > groupPosition) {
			return childArray.get(groupPosition).size();
		} else {
			return 0;
		}
	}

	/* ----------------------------Group */
	@Override
	public Object getGroup(int groupPosition) {
		Log.i("info", "getGroup");
		return getGroup(groupPosition);
	}

	@Override
	public int getGroupCount() {
		Log.i("info", "getGroupCount");
		return groupArray.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		Log.i("info", "getGroupId");
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		final ViewHolderGroup group;
		if (convertView == null) {
			group = new ViewHolderGroup();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.test_group_view, null);
			group.ivGroup = (ImageView) convertView
					.findViewById(R.id.group_img);
			group.tvTitle = (TextView) convertView
					.findViewById(R.id.group_title);
			group.tvMoney = (TextView) convertView
					.findViewById(R.id.group_money);
			convertView.setTag(group);
		} else {
			group = (ViewHolderGroup) convertView.getTag();
		}
		if (isExpanded) {
			group.ivGroup.setBackgroundResource(R.drawable.down);
		} else {
			group.ivGroup.setBackgroundResource(R.drawable.right);
		}
		String title = groupArray.get(groupPosition).getTitle();
		if(title.length() > 5){
			title = title.substring(0, 5)+"...";
		}
		group.tvTitle.setText(title);
		group.tvMoney.setText("￥" + groupArray.get(groupPosition).getMoney());
		return convertView;
	}

	private class ViewHolderGroup {
		private ImageView ivGroup;
		private TextView tvTitle, tvMoney;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		Log.i("info", "isChildSelectable");
		return true;
	}

	private TextView getGenericView(String string) {
		AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		TextView textView = new TextView(context);
		textView.setLayoutParams(layoutParams);
		textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		textView.setPadding(40, 40, 40, 40);
		textView.setText(string);
		return textView;
	}
}