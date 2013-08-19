/*
 * Copyright (C) 2012 yueyueniao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.view;

import com.example.personalapp.R;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

public class SlidingMenu extends RelativeLayout {

	private View mSlidingView;
	private View mRightView;
	private RelativeLayout bgShade;
	// private LinearLayout lintool;
	private int screenWidth;
	private int screenHeight;
	private Context mContext;
	private Scroller mScroller;
	private boolean hasClickRight = false;
	public GestureDetector detector;

	public SlidingMenu(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {

		mContext = context;
		bgShade = new RelativeLayout(context);

		mScroller = new Scroller(getContext());
		WindowManager windowManager = ((Activity) context).getWindow()
				.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		screenWidth = display.getWidth();

		screenHeight = display.getHeight();
		LayoutParams bgParams = new LayoutParams(screenWidth, screenHeight);
		bgParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		bgShade.setLayoutParams(bgParams);
		detector = new GestureDetector(new MyGesListener());

	}

	public SlidingMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SlidingMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public void setRightView(View view) {
		LayoutParams behindParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.FILL_PARENT);
		behindParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		addView(view, behindParams);
		mRightView = view;

	}

	public void setCenterView(View view) {
		LayoutParams aboveParams = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);

		LayoutParams bgParams = new LayoutParams(screenWidth, screenHeight);
		bgParams.addRule(RelativeLayout.CENTER_IN_PARENT);

		// View bgShadeContent = new View(mContext);
		// bgShadeContent.setBackgroundDrawable(getResources().getDrawable(
		// R.drawable.shade_bg));
		// bgShade.addView(bgShadeContent, bgParams);

		addView(bgShade, bgParams);

		addView(view, aboveParams);
		mSlidingView = view;
		mSlidingView.bringToFront();
		mSlidingView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return detector.onTouchEvent(event);
			}
		});

	}

	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
		postInvalidate();
	}

	@Override
	public void computeScroll() {
		if (!mScroller.isFinished()) {
			if (mScroller.computeScrollOffset()) {
				int oldX = mSlidingView.getScrollX();
				int oldY = mSlidingView.getScrollY();
				int x = mScroller.getCurrX();
				int y = mScroller.getCurrY();
				if (oldX != x || oldY != y) {
					if (mSlidingView != null) {
						mSlidingView.scrollTo(x, y);
						if (x < 0) {
							bgShade.scrollTo(x + 20, y);
						} else {
							bgShade.scrollTo(x - 50, y);
						}
					}
				}
				invalidate();
			}
		}
	}

	void smoothScrollTo(int dx) {
		int duration = 500;
		int oldScrollX = mSlidingView.getScrollX();
		mScroller.startScroll(oldScrollX, mSlidingView.getScrollY(), dx,
				mSlidingView.getScrollY(), duration);
		invalidate();
	}

	public void showRightView() {
		int menuWidth = mRightView.getWidth();
		int oldScrollX = mSlidingView.getScrollX();
		if (oldScrollX == 0) {
			// mRightView.setVisibility(View.VISIBLE);
			smoothScrollTo(menuWidth);
			// lintool.setVisibility(View.VISIBLE);
			hasClickRight = true;
		} else if (oldScrollX == menuWidth) {
			smoothScrollTo(-menuWidth);
			// lintool.setVisibility(View.GONE);
			if (hasClickRight) {
				hasClickRight = false;
			}
		}
	}

	public class MyGesListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (e1.getX() - e2.getX() > 0) {

			} else if (e2.getX() - e1.getX() > 50) {
				// 从左向右
				Log.d("info", "从左向右");
				if (hasClickRight) {
					showRightView();
				}
			}
			return super.onFling(e1, e2, velocityX, velocityY);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		detector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	public void setLintool(LinearLayout lintool) {
		// this.lintool = lintool;
	}

}
