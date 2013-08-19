package com.example.view;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.example.constants.LogUtil;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MyLetterListView extends View {

	OnTouchingLetterChangedListener onTouchingLetterChangedListener;
	int choose = -1;
	Paint paint = new Paint();
	boolean showBkg = false;
	MyAsyncQueryHandler asyncQuery;
	Context context;
	List<String> list;
	boolean canDrayAlpha;
	boolean touched;
	int screenWidth;
	int screenHeight;

	public MyLetterListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		canDrayAlpha = false;
		asyncQuery = new MyAsyncQueryHandler(context.getContentResolver());
		queryAlpha();
		touched = false;
	}

	public MyLetterListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		canDrayAlpha = false;
		touched = false;
		asyncQuery = new MyAsyncQueryHandler(context.getContentResolver());
		queryAlpha();
	}

	public MyLetterListView(Context context) {
		super(context);
		this.context = context;
		canDrayAlpha = false;
		asyncQuery = new MyAsyncQueryHandler(context.getContentResolver());
		queryAlpha();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (showBkg) {
			canvas.drawColor(Color.parseColor("#40000000"));
		}
		int height = getHeight();
		int width = getWidth();
		LogUtil.log("width = " + width + " , height = " + height);
		if (canDrayAlpha) {
			canvas.drawColor(Color.parseColor("#00ffffff"));
			String alpha = "";
			int singleHeight = height / (list.size() > 0 ? list.size() : 1);
			for (int i = 0; i < list.size(); i++) {
				paint.setColor(Color.parseColor("#ff8191a7"));
				paint.setTextSize((height / width) + 3);
				paint.setTypeface(Typeface.DEFAULT_BOLD);
				paint.setAntiAlias(true);
				alpha = list.get(i);
				float textWidth = paint.measureText(alpha);
				float xPos = width / 2 - textWidth / 2;
				float yPos = singleHeight * i + singleHeight / 2 + textWidth
						/ 2;
				canvas.drawText(alpha, xPos, yPos, paint);
				// paint.setColor(getResources().getColor(R.color.lightgrey));
				// canvas.drawLine(2, yPos+singleHeight/2-textWidth/2, width,
				// yPos+singleHeight/2-textWidth/2, paint);

				paint.reset();
			}
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float y = event.getY();
		final int oldChoose = choose;
		final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
		final int c = (int) (y / getHeight() * list.size());

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			showBkg = true;
			if (oldChoose != c && listener != null) {
				if (c > 0 && c < list.size()) {

					listener.onTouchingLetterChanged(list.get(c));
					choose = c;
					touched = true;
					invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (oldChoose != c && listener != null) {
				if (c > 0 && c < list.size()) {
					listener.onTouchingLetterChanged(list.get(c));
					choose = c;
					invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			showBkg = false;
			choose = -1;
			touched = false;
			invalidate();
			break;
		}
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	public void setOnTouchingLetterChangedListener(
			OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	public interface OnTouchingLetterChangedListener {
		public void onTouchingLetterChanged(String s);
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

	public void queryAlpha() {
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		String[] projection = { "sort_key" };
		asyncQuery.startQuery(0, null, uri, projection, null, null,
				"sort_key COLLATE LOCALIZED asc");
	}

	private class MyAsyncQueryHandler extends AsyncQueryHandler {

		public MyAsyncQueryHandler(ContentResolver cr) {
			super(cr);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if (cursor != null && cursor.getCount() > 0) {
				list = new ArrayList<String>();
				cursor.moveToFirst();
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					String sortKey = cursor.getString(0);
					String alpha = getAlpha(sortKey);
					if (i == 0) {
						list.add(alpha);
					} else {
						String lastAlpha = list.get(list.size() - 1);
						if (lastAlpha == null) {
							list.add(alpha);
						} else if (!alpha.equals(lastAlpha)) {
							list.add(alpha);
						}
					}
				}
				if (list.size() > 0) {
					invalidate();// 重新画屏�? canDrayAlpha = true;
				}
			}
		}
	}
}
