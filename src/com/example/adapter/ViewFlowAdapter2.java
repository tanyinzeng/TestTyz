package com.example.adapter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import com.example.constants.AsyncBitmapLoader;
import com.example.constants.Constants;
import com.example.constants.LogUtil;
import com.example.constants.SharedPreferencemanager;
import com.example.constants.AsyncBitmapLoader.ImageCallBack;
import com.example.personalapp.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ViewFlowAdapter2 extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater = null;
	private List<String> imgUrl;
	private String fromWhere;
	private AsyncBitmapLoader asynLoader;

	public ViewFlowAdapter2(Context ctx, List<String> imgUrl, String fromWhere) {
		this.mContext = ctx;
		this.mInflater = LayoutInflater.from(mContext);
		this.imgUrl = imgUrl;
		this.fromWhere = fromWhere;
		asynLoader = new AsyncBitmapLoader(ctx,fromWhere);
	}

	@Override
	public int getCount() {
		return imgUrl.size();
	}

	@Override
	public String getItem(int position) {
		return imgUrl.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.circle_viewflow_item_layout, null);
			holder = new ViewHolder();
			holder.iv = (ImageView) convertView.findViewById(R.id.img_view);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
			if (Constants.FTP_STATUS.WORKSPACE_ALBUM_INFO
					.equals(fromWhere)) {
				if (SharedPreferencemanager.getPhotoOnline(mContext)) {
					Bitmap bmp = asynLoader.loadBitmap(holder.iv,
							Constants.GET_QUEST_URI.GET_PICTURE_URI
							+ SharedPreferencemanager.getPhone(mContext)
							+ imgUrl.get(position) , 
							new ImageCallBack() {
								@Override
								public void imageLoad(ImageView imageView, Bitmap bitmap) {
									imageView.setImageBitmap(bitmap);
								}
							});
					if(bmp != null){
						holder.iv.setImageBitmap(bmp);
					}
				} else {
					InputStream inputStream = new FileInputStream(
							imgUrl.get(position));
					Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
					holder.iv.setImageBitmap(bitmap);
				}
			} 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return convertView;
	}

	private class ViewHolder {
		private ImageView iv;
	}


}
