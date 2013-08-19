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

	/** ���ڱ�֤һ��������touch�¼������ */
	private boolean mIsMarked;

	/** ʵ�ʵ�padding�ľ����������ƫ�ƾ���ı��� */
	private final static int RATIO = 2;

	private int footContentHeight;

	/**
	 * touch�¼���ʼʱY����
	 */
	private int mStartY;
	private int firstItemIndex;
	private int mRefreshState = DONE;
	private boolean isBack;
	private OnRefreshListener refreshListener;

	/**
	 * �Ƿ����������ˢ��
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

	/** �����ؼ��ߴ� */
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
	 * ��ʶ���ײ��ˣ���������ˢ��
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
					// ��֤������padding�Ĺ����У���ǰ��λ��һֱ����head��
					// ����������б�����Ļ�Ļ����������Ƶ�ʱ���б��ͬʱ���й���
					// ��������ȥˢ����
					if (mRefreshState == RELEASE_TO_REFRESH) {
						// �������ˣ��Ƶ�����Ļ�㹻�ڸ�head�ĳ̶ȣ����ǻ�û���Ƶ�ȫ���ڸǵĵز�
						if (((mStartY - tempY) / RATIO < footContentHeight)
								&& (mStartY - tempY) > 0) {
							mRefreshState = PULL_TO_REFRESH;
							changeFooterViewBymRefreshState();
						} else if (mStartY - tempY <= 0) {
							mRefreshState = DONE;
							changeFooterViewBymRefreshState();
						}
					}
					// ��û�е�����ʾ�ɿ�ˢ�µ�ʱ��,DONE������PULL_TO_REFRESH״̬
					if (mRefreshState == PULL_TO_REFRESH && isLastIndex) {
						// ���������Խ���RELEASE_TO_REFRESH��״̬
						if ((mStartY - tempY) / RATIO >= footContentHeight) {
							mRefreshState = RELEASE_TO_REFRESH;
							isBack = true;
							changeFooterViewBymRefreshState();
						} else if (mStartY - tempY <= 0) {
							mRefreshState = DONE;
							changeFooterViewBymRefreshState();
						}
					}
					// done״̬��
					if (mRefreshState == DONE) {
						if (mStartY - tempY > 0) {
							mRefreshState = PULL_TO_REFRESH;
							changeFooterViewBymRefreshState();
						}
					}
					if (isLastIndex) {
						// ����footView��size
						if (mRefreshState == PULL_TO_REFRESH) {
							footView.setPadding(0, 0, 0, -1 * footContentHeight
									+ (mStartY - tempY) / RATIO);

						}

						// ����footView��paddingTop
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

	// ����ˢ��״̬�ı�ʱ�򣬵��ø÷������Ը��½���
	private void changeFooterViewBymRefreshState() {
		switch (mRefreshState) {
		case RELEASE_TO_REFRESH:
			progressLayout.setVisibility(View.GONE);
			showMore.setVisibility(View.VISIBLE);
			break;
		case PULL_TO_REFRESH:
			// ����ˢ��
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
	 * ˢ�»ص��ӿ�
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
	 * ˢ�����
	 */
	public void onRefreshComplete() {
		mRefreshState = DONE;
		changeFooterViewBymRefreshState();
		// ˢ����ɺ�ԭ����������ʶλ�Լ��Ƿ���listview�ײ���ʶ
		isLastIndex = false;
	}

	/**
	 * �ص�����ˢ��
	 */
	private void onPullUpRefresh() {
		if (refreshListener != null) {
			refreshListener.onPullUpRefresh();
		}
	}

	/**
	 * ��������ԴAdapter
	 * 
	 * @param adapter
	 */
	public void setAdapter(BaseAdapter adapter) {
		super.setAdapter(adapter);
	}

}
