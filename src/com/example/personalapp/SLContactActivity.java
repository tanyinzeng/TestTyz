package com.example.personalapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import com.example.constants.LogUtil;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SLContactActivity extends BaseActivity implements
		OnScrollChangedListener {
	private BaseAdapter adapter;
	private ListView personList;
	private TextView overlay;
	// private MyLetterListView letterListView;
	private AsyncQueryHandler asyncQuery;
	private static final String NAME = "name", ID = "id",
			SORT_KEY = "sort_key";
	private HashMap<String, Integer> alphaIndexer;
	private String[] sections;
	private Handler handler;
	private OverlayThread overlayThread;

	private List<ContentValues> list;
	private Context context;
	private Button btnBack;
	private Button bt_selectall;
	private int checkNum = 0; // 记录选中的条目数量

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sl_contact);
		context = this;
		initControls();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}

	protected void onResume() {
		super.onResume();
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		String[] projection = { "_id", "display_name", "sort_key" };
		asyncQuery.startQuery(0, null, uri, projection, null, null,
				"sort_key COLLATE LOCALIZED asc");

		// letterListView.queryAlpha();
	}

	private class MyAsyncQueryHandler extends AsyncQueryHandler {

		public MyAsyncQueryHandler(ContentResolver cr) {
			super(cr);
		}

		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if (cursor != null && cursor.getCount() > 0) {
				list = new ArrayList<ContentValues>();
				cursor.moveToFirst();
				for (int i = 0; i < cursor.getCount(); i++) {
					ContentValues cv = new ContentValues();
					cursor.moveToPosition(i);
					String id = cursor.getString(0);
					String name = cursor.getString(1);
					String sortKey = cursor.getString(2);
					try {
						String[] projection = {
								ContactsContract.CommonDataKinds.Phone.TYPE,
								ContactsContract.CommonDataKinds.Phone.NUMBER };
						Cursor phoneCursor = context
								.getContentResolver()
								.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
										projection,
										ContactsContract.CommonDataKinds.Phone.CONTACT_ID
												+ " = " + id, null, null);
						while (phoneCursor.moveToNext()) {
							int phoneType = phoneCursor.getInt(0);
							int typeRes = ContactsContract.CommonDataKinds.Phone
									.getTypeLabelResource(phoneType);
							String typeDescribeString = context
									.getString(typeRes);
							cv.put(ID, id);
							cv.put(NAME, name);
							cv.put("type", typeDescribeString);
							cv.put("phone", phoneCursor.getString(1));
							cv.put(SORT_KEY, sortKey);
							list.add(cv);
						}
						phoneCursor.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				if (list.size() > 0) {
					setAdapter(list);
				}
			} else {
				list.clear();
				adapter.notifyDataSetChanged();
			}
			cursor.close();
		}
	}

	private void setAdapter(List<ContentValues> list) {
		adapter = new ListAdapter(this, list);
		personList.setAdapter(adapter);
	}

	private class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<ContentValues> list;
		private HashMap<Integer, Boolean> isSelected;

		public ListAdapter(Context context, List<ContentValues> list) {
			this.inflater = LayoutInflater.from(context);
			this.list = list;
			isSelected = new HashMap<Integer, Boolean>();
			LogUtil.log("list = " + list);
			initDate();
			alphaIndexer = new HashMap<String, Integer>();
			sections = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				String currentStr = getAlpha(list.get(i).getAsString(SORT_KEY));
				String previewStr = (i - 1) >= 0 ? getAlpha(list.get(i - 1)
						.getAsString(SORT_KEY)) : " ";
				if (!previewStr.equals(currentStr)) {
					String name = getAlpha(list.get(i).getAsString(SORT_KEY));
					alphaIndexer.put(name, i);
					sections[i] = name;
				}
			}
		}

		// 初始化isSelected的数据
		private void initDate() {
			for (int i = 0; i < list.size(); i++) {
				getIsSelected().put(i, false);
			}
		}

		public int getCount() {
			return list.size();
		}

		public Object getItem(int position) {
			return list.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (list == null)
				return null;
			ViewHolder holder;
			ContentValues cv = list.get(position);
			if (convertView == null) {
				convertView = inflater
						.inflate(R.layout.contact_list_item, null);
				holder = new ViewHolder();
				holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.phone = (TextView) convertView
						.findViewById(R.id.telphone);
				holder.cb = (CheckBox) convertView.findViewById(R.id.item_cb);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.name.setText(cv.getAsString(NAME));
			holder.phone.setText(cv.getAsString("phone"));
			String currentStr = getAlpha(list.get(position).getAsString(
					SORT_KEY));
			// 根据isSelected来设置checkbox的选中状况
			holder.cb.setChecked(getIsSelected().get(position));
			String previewStr = (position - 1) >= 0 ? getAlpha(list.get(
					position - 1).getAsString(SORT_KEY)) : " ";
			if (!previewStr.equals(currentStr)) {
				holder.alpha.setVisibility(View.VISIBLE);
				holder.alpha.setText(currentStr);
			} else {
				holder.alpha.setVisibility(View.GONE);
			}
			return convertView;
		}

		public HashMap<Integer, Boolean> getIsSelected() {
			return isSelected;
		}

		public void setIsSelected(HashMap<Integer, Boolean> isSelected) {
			this.isSelected = isSelected;
		}

	}

	private void initOverlay() {
		LayoutInflater inflater = LayoutInflater.from(this);
		overlay = (TextView) inflater.inflate(R.layout.overlay, null);
		overlay.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSLUCENT);
		WindowManager windowManager = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(overlay, lp);
	}

	private class OverlayThread implements Runnable {

		public void run() {
			overlay.setVisibility(View.GONE);
		}
	}

	private String getAlpha(String str) {
		if (str == null) {
			return "#";
		}

		if (str.trim().length() == 0) {
			return "#";
		}

		char c = str.trim().substring(0, 1).charAt(0);
		Pattern pattern = Pattern.compile("^[A-Za-z]");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase();
		} else {
			return "#";
		}
	}

	private boolean isLetter(String str) {
		return str.matches("^[A-Za-z]+$");
	}

	public String getCondition(String value) {
		String string = null;
		if ("".equals(value) && null == value) {
			return null;
		} else {
			String split;
			if (isLetter(value)) {
				split = "";
			} else {
				split = "%";
			}

			for (int i = 0; i < value.length(); i++) {
				char c = value.charAt(i);
				split = split + c + "%";
			}
			string = "sort_key like '" + split + "' ";
			Log.d("BuisynessVoice", "sql select condition:" + string);
			return string;
		}
	}

	private List<String> phones;

	private void initControls() {
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.main_contact);
		layout.requestDisallowInterceptTouchEvent(true);
		personList = (ListView) findViewById(R.id.list_view);
		asyncQuery = new MyAsyncQueryHandler(getContentResolver());
		alphaIndexer = new HashMap<String, Integer>();
		handler = new Handler();
		overlayThread = new OverlayThread();
		bt_selectall = (Button) findViewById(R.id.bt_selectall);
		initOverlay();
		phones = new ArrayList<String>();
		personList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				ViewHolder holder = (ViewHolder) arg1.getTag();
				// 改变CheckBox的状态
				holder.cb.toggle();
				// 将CheckBox的选中状况记录下来
				((ListAdapter) adapter).getIsSelected().put(arg2,
						holder.cb.isChecked());
				ContentValues cv = list.get(arg2);
				String name = (String) cv.getAsString("phone");
				// 调整选定条目
				if (holder.cb.isChecked() == true) {
					checkNum++;
					LogUtil.log("number = " + name);
					phones.add(checkNum - 1, name);
				} else {
					checkNum--;
					phones.remove(checkNum);
				}
			}
		});
		bt_selectall.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent mIntent = new Intent();
				mIntent.putStringArrayListExtra("phones",
						(ArrayList<String>) phones);
				setResult(100, mIntent);
				finish();
			}
		});

		personList.setOnScrollListener(new OnScrollListener() {

			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
		btnBack = (Button) findViewById(R.id.back);
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	public void onScrollChanged() {
		// TODO Auto-generated method stub

	}
}
