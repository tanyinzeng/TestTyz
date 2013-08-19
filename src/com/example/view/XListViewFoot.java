package com.example.view;

import com.example.personalapp.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class XListViewFoot extends ListView implements OnScrollListener {
	private final static int RELEASE_TO_REFRESH = 0;
	private final static int PULL_TO_REFRESH = 1;
	private final static int REFRESHING = 2;
	private final static int DONE = 3;
	private final static int LOADING = 4;

	private LayoutInflater mInflater;
	private Context mContext;

	private LinearLayout footView;

	LinearLayout progressLayout;
	TextView showMore;

	/** 用于保证一个完整的touch事件的完成 */
	private boolean mIsMarked;

	/** 实际的padding的距离与界面上偏移距离的比例 */
	private final static int RATIO = 2;

	private int footContentHeight;

	/**
	 * touch事件开始时Y坐标
	 */
	private int mStartY;
	private int firstItemIndex;
	private int mRefreshState = DONE;
	private boolean isBack;
	private OnRefreshListener refreshListener;

	/**
	 * 是否可下拉上拉刷新
	 */
	private boolean mIsRefreshable;
	private boolean isLastIndex = false;

	public XListViewFoot(Context context) {
		super(context);
		mContext = context;
		initView();
	}

	public XListViewFoot(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}

	private void initView() {
		setCacheColorHint(mContext.getResources().getColor(R.color.transparent));
		mInflater = LayoutInflater.from(mContext);
		footView = (LinearLayout) mInflater.inflate(R.layout.footview, null);
		progressLayout = (LinearLayout) footView
				.findViewById(R.id.xlistview_footer_progressbar);
		showMore = (TextView) footView
				.findViewById(R.id.xlistview_footer_hint_textview);
		measureView(footView);
		footContentHeight = footView.getMeasuredHeight();
		footView.setPadding(0, 0, 0, -1 * footContentHeight);
		footView.invalidate();
		addFooterView(footView, null, false);

		setOnScrollListener(this);
	}

	/** 测量控件尺寸 */
	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	public void onScroll(AbsListView arg0, int firstVisiableItem, int arg2,
			int arg3) {
		firstItemIndex = firstVisiableItem;
		if (firstItemIndex == 0) {
			isLastIndex = false;
		}
	}

	/**
	 * 标识到底部了，可以上拉刷新
	 */
	public void onScrollStateChanged(AbsListView view, int arg1) {
		if (view.getLastVisiblePosition() == view.getCount() - 1) {

			isLastIndex = true;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mIsRefreshable) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!mIsMarked) {
					mIsMarked = true;
					mStartY = (int) event.getY();
				}
				break;
			case MotionEvent.ACTION_UP:
				if (mRefreshState != REFRESHING && mRefreshState != LOADING) {
					if (mRefreshState == DONE) {
						// do nothing
					}
				}
				if (mRefreshState == PULL_TO_REFRESH) {
					mRefreshState = DONE;
					changeFooterViewBymRefreshState();
				}
				if (mRefreshState == RELEASE_TO_REFRESH) {
					mRefreshState = REFRESHING;
					changeFooterViewBymRefreshState();
					onPullUpRefresh();
				}
				mIsMarked = false;
				isBack = false;
				break;
			case MotionEvent.ACTION_MOVE:
				int tempY = (int) event.getY();
				if (mRefreshState != REFRESHING && mIsMarked
						&& mRefreshState != LOADING) {
					// 保证在设置padding的过程中，当前的位置一直是在head，
					// 否则如果当列表超出屏幕的话，当在下推的时候，列表会同时进行滚动
					// 可以松手去刷新了
					if (mRefreshState == RELEASE_TO_REFRESH) {
						// 往下推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
						if (((mStartY - tempY) / RATIO < footContentHeight)
								&& (mStartY - tempY) > 0) {
							mRefreshState = PULL_TO_REFRESH;
							changeFooterViewBymRefreshState();
						} else if (mStartY - tempY <= 0) {
							mRefreshState = DONE;
							changeFooterViewBymRefreshState();
						}
					}
					// 还没有到达显示松开刷新的时候,DONE或者是PULL_TO_REFRESH状态
					if (mRefreshState == PULL_TO_REFRESH && isLastIndex) {
						// 上拉到可以进入RELEASE_TO_REFRESH的状态
						if ((mStartY - tempY) / RATIO >= footContentHeight) {
							mRefreshState = RELEASE_TO_REFRESH;
							isBack = true;
							changeFooterViewBymRefreshState();
						} else if (mStartY - tempY <= 0) {
							mRefreshState = DONE;
							changeFooterViewBymRefreshState();
						}
					}
					// done状态下
					if (mRefreshState == DONE) {
						if (mStartY - tempY > 0) {
							mRefreshState = PULL_TO_REFRESH;
							changeFooterViewBymRefreshState();
						}
					}
					if (isLastIndex) {
						// 更新footView的size
						if (mRefreshState == PULL_TO_REFRESH) {
							footView.setPadding(0, 0, 0, -1 * footContentHeight
									+ (mStartY - tempY) / RATIO);

						}

						// 更新footView的paddingTop
						if (mRefreshState == RELEASE_TO_REFRESH) {
							footView.setPadding(0, 0, 0, (mStartY - tempY)
									/ RATIO - footContentHeight);
						}
					}
				}

				break;
			default:
				break;
			}
		}
		return super.onTouchEvent(event);
	}

	// 上拉刷新状态改变时候，调用该方法，以更新界面
	private void changeFooterViewBymRefreshState() {
		switch (mRefreshState) {
		case RELEASE_TO_REFRESH:
			progressLayout.setVisibility(View.GONE);
			showMore.setVisibility(View.VISIBLE);
			break;
		case PULL_TO_REFRESH:
			// 上拉刷新
			progressLayout.setVisibility(View.GONE);
			showMore.setVisibility(View.VISIBLE);
			break;
		case REFRESHING:
			footView.setPadding(0, 0, 0, 0);
			progressLayout.setVisibility(View.VISIBLE);
			showMore.setVisibility(View.GONE);
			break;
		case DONE:
			footView.setPadding(0, 0, 0, -1 * footContentHeight);
			progressLayout.setVisibility(View.GONE);
			showMore.setVisibility(View.VISIBLE);
			break;
		}
	}

	/**
	 * 刷新回调接口
	 * 
	 * @author APPLE
	 * 
	 */
	public interface OnRefreshListener {
		public void onPullUpRefresh();
	}

	public void setOnRefreshListener(OnRefreshListener refreshListener,
			boolean mIsRefreshable) {
		this.refreshListener = refreshListener;
		this.mIsRefreshable = mIsRefreshable;
	}

	/**
	 * 刷新完成
	 */
	public void onRefreshComplete() {
		mRefreshState = DONE;
		changeFooterViewBymRefreshState();
		// 刷新完成后还原上拉下拉标识位以及是否是listview底部标识
		isLastIndex = false;
	}

	/**
	 * 回调上拉刷新
	 */
	private void onPullUpRefresh() {
		if (refreshListener != null) {
			refreshListener.onPullUpRefresh();
		}
	}

	/**
	 * 设置数据源Adapter
	 * 
	 * @param adapter
	 */
	public void setAdapter(BaseAdapter adapter) {
		super.setAdapter(adapter);
	}

}
