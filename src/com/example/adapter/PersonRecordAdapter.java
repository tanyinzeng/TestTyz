package com.example.adapter;

import java.util.List;
import com.example.constants.Constants;
import com.example.constants.CopyOfAsynImgeLoader;
import com.example.constants.AsyncBitmapLoader.ImageCallBack;
import com.example.constants.CopyOfAsynImgeLoader.ImageCallback;
import com.example.constants.AsyncBitmapLoader;
import com.example.constants.LogUtil;
import com.example.entity.PersonRecordEntity;
import com.example.personalapp.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PersonRecordAdapter extends BaseAdapter {
	private List<PersonRecordEntity> entitys;
	private Context context;
	private CopyOfAsynImgeLoader asyncImageLoader;
	private AsyncBitmapLoader asynLoader;

	public PersonRecordAdapter(Context context, List<PersonRecordEntity> entitys) {
		this.context = context;
		this.entitys = entitys;
		asyncImageLoader = new CopyOfAsynImgeLoader(context);
		asynLoader = new AsyncBitmapLoader(context,Constants.FTP_STATUS.WORKSPACE_TRACK_INFO);
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
		ViewItem viewItem;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.personal_record_list_item, null);
			viewItem = new ViewItem();
			viewItem.iv = (ImageView) convertView
					.findViewById(R.id.record_list_img);
			viewItem.tvTitle = (TextView) convertView
					.findViewById(R.id.record_title);
			viewItem.tvTime = (TextView) convertView
					.findViewById(R.id.record_time);
			convertView.setTag(viewItem);
		} else {
			viewItem = (ViewItem) convertView.getTag();
		}
		PersonRecordEntity entity = entitys.get(position);
		if (entity.isOnline()) {
			for (int i = 0; i < entity.getImgUrls().size(); i++) {
				LogUtil.log("imgUrl = " + entity.getImgUrls().get(i));
				String[] img = entity.getImgUrls().get(i).split("/");
				LogUtil.log("length = " + img.length);
				if (img.length > 3) {
					String imgUrl = img[4] + "/" + img[5] + "/" + img[6];
					LogUtil.log("imgUrl = " + imgUrl);
					entity.getImgUrls().set(i, imgUrl);
				}
				Bitmap bmp = asynLoader.loadBitmap(viewItem.iv,
						Constants.GET_QUEST_URI.GET_PICTURE_URI
						+ entity.getImgUrls().get(0) + ".png" , 
						new ImageCallBack() {
							@Override
							public void imageLoad(ImageView imageView, Bitmap bitmap) {
								imageView.setImageBitmap(bitmap);
							}
						});
				if(bmp != null){
					viewItem.iv.setImageBitmap(bmp);
				}
			}
		} else {
			LogUtil.log("imgUrl = " + entity.getImgUrls().get(0));
			try {
				Drawable cachedImage = asyncImageLoader.loadDrawable(viewItem.iv,
						entity.getImgUrls().get(0)+".png", new ImageCallback() {

							@Override
							public void imageLoaded(Drawable imageDrawable,
									ImageView iv) {
								iv.setImageDrawable(imageDrawable);
							}
						});
				if (cachedImage != null) {
					viewItem.iv.setImageDrawable(cachedImage);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		viewItem.tvTitle.setText(entity.getTitle());
		viewItem.tvTime.setText(entity.getTime());
		return convertView;
	}

	private class ViewItem {
		private ImageView iv;
		private TextView tvTitle, tvTime;
	}

}
