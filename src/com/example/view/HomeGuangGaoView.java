package com.example.view;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.example.constants.AsyncBitmapLoader;
import com.example.constants.Constants;
import com.example.constants.SharedPreferencemanager;
import com.example.constants.AsyncBitmapLoader.ImageCallBack;
import com.example.personalapp.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

/**
 * 广告页面
 * 
 */
public class HomeGuangGaoView extends RelativeLayout {
	private ViewPager viewPager; // android-support-v4中的滑动组件
	private List<ImageView> imageViews; // 滑动的图片集合
	private int currentItem = 0; // 当前图片的索引号
	private ScheduledExecutorService scheduledExecutorService;

	private Context context;
	private static Handler mHandler;
	private AsyncBitmapLoader loader;

	private void initHandler() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Constants.USER_STATUS.BACKGROUND_FROM_LOCAL:
					imageViews = new ArrayList<ImageView>();
					List<String> imgUrls = (List<String>) msg.obj;
					for (int i = 0; i < imgUrls.size(); i++) {
						ImageView imageView = new ImageView(context);
						RelativeLayout.LayoutParams params = new LayoutParams(
								LayoutParams.FILL_PARENT,
								LayoutParams.FILL_PARENT);
						imageView.setLayoutParams(params);
						imageView.setScaleType(ScaleType.FIT_XY);
						String imgUrl = imgUrls.get(i);
						Bitmap bmp = BitmapFactory.decodeFile(Environment
								.getExternalStorageDirectory()
								+ Constants.FTP_STATUS.WORKSPACE_PERSONAL_INFO
								+ SharedPreferencemanager.getPhone(context)
								+ Constants.FTP_STATUS.WORKSPACE_PERSONAL_INFO
								+ imgUrl + ".png");
						imageView.setImageBitmap(bmp);
						imageViews.add(imageView);
					}
					viewPager.setAdapter(new MyAdapter());// 设置填充ViewPager页面的适配器
					// 设置一个监听器，当ViewPager中的页面改变时调用
					if (imageViews.size() > 1) {
						viewPager
								.setOnPageChangeListener(new MyPageChangeListener());
					}
					break;
				case Constants.USER_STATUS.BACKGROUND_FROM_NET:
					imageViews = new ArrayList<ImageView>();
					imgUrls = (List<String>) msg.obj;
					for (int i = 0; i < imgUrls.size(); i++) {
						ImageView imageView = new ImageView(context);
						RelativeLayout.LayoutParams params = new LayoutParams(
								LayoutParams.FILL_PARENT,
								LayoutParams.FILL_PARENT);
						imageView.setLayoutParams(params);
						imageView.setScaleType(ScaleType.FIT_XY);
						String imgUrl = imgUrls.get(i);
						Bitmap bmp = loader.loadBitmap(
								imageView,
								Constants.GET_QUEST_URI.GET_PICTURE_URI
										+ SharedPreferencemanager
												.getPhone(context) + "/person/"
										+ imgUrl + ".png", new ImageCallBack() {
									@Override
									public void imageLoad(ImageView imageView,
											Bitmap bitmap) {
										imageView.setImageBitmap(bitmap);
									}
								});
						if (bmp != null) {
							imageView.setImageBitmap(bmp);
						}
						imageViews.add(imageView);
					}
					viewPager.setAdapter(new MyAdapter());// 设置填充ViewPager页面的适配器
					// 设置一个监听器，当ViewPager中的页面改变时调用
					if (imageViews.size() > 1) {
						viewPager
								.setOnPageChangeListener(new MyPageChangeListener());
					}
					break;
				}

				super.handleMessage(msg);
			}

		};
	}

	public static void sendHandlerMessage(int what, Object object) {
		if (mHandler == null) {
			return;
		}
		Message msg = mHandler.obtainMessage(what, object);
		mHandler.sendMessage(msg);
	}

	public HomeGuangGaoView(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.context = context;

		LayoutInflater.from(context).inflate(R.layout.guanggao_layout, this);
		loader = new AsyncBitmapLoader(context,
				Constants.FTP_STATUS.WORKSPACE_PERSONAL_INFO);
		initScrollImg();
		initHandler();
	}

	// 切换当前显示的图片
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			viewPager.setCurrentItem(currentItem);// 切换当前显示的图片
		};
	};

	/**
	 * 初始化滚动图片
	 */
	private void initScrollImg() {
		viewPager = (ViewPager) findViewById(R.id.vp);
	}

	public void onStart() {
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		// 当Activity显示出来后，每两秒钟切换一次图片显示
		scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 3,
				TimeUnit.SECONDS);
	}

	public void onStop() {
		// 当Activity不可见的时候停止切换
		scheduledExecutorService.shutdown();
	}

	/**
	 * 换行切换任务
	 * 
	 * @author Administrator
	 */
	private class ScrollTask implements Runnable {

		public void run() {
			synchronized (viewPager) {
				currentItem = (currentItem + 1) % imageViews.size();
				handler.obtainMessage().sendToTarget(); // 通过Handler切换图片
			}
		}
	}

	/**
	 * 当ViewPager中页面的状态发生改变时调用
	 * 
	 * @author Administrator
	 */
	private class MyPageChangeListener implements OnPageChangeListener {
		private int oldPosition = 0;

		/**
		 * This method will be invoked when a new page becomes selected.
		 * position: Position index of the new selected page.
		 */
		public void onPageSelected(final int position) {
			currentItem = position;
			oldPosition = position;
			imageViews.get(position).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

				}
			});
		}

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}

	/**
	 * 填充ViewPager页面的适配器
	 * 
	 * @author Administrator
	 */
	private class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return imageViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, final int arg1) {
			((ViewPager) arg0).addView(imageViews.get(arg1));
			return imageViews.get(arg1);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}
	}
}
