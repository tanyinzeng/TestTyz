package com.example.adapter;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import com.example.constants.AsyncBitmapLoader;
import com.example.constants.Constants;
import com.example.constants.LogUtil;
import com.example.constants.AsyncBitmapLoader.ImageCallBack;
import com.example.entity.FeelingContentEntity;
import com.example.personalapp.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FeelingContentAdapter extends BaseAdapter {
	private List<FeelingContentEntity> entitys;
	private LayoutInflater inflater;
	private Context context;
	private AsyncBitmapLoader asynLoader;

	public FeelingContentAdapter(Context context,
			List<FeelingContentEntity> entitys) {
		inflater = LayoutInflater.from(context);
		this.entitys = entitys;
		this.context = context;
		asynLoader = new AsyncBitmapLoader(context,
				Constants.FTP_STATUS.WORKSPACE_CHENGJI_INFO);
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
		List<ImageView> stars = new ArrayList<ImageView>();
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.feeling_content_list_item,
					null);
			holder.ivFeel = (ImageView) convertView
					.findViewById(R.id.feeling_img);
			holder.tvContent = (TextView) convertView
					.findViewById(R.id.feeling_content);
			holder.tvTime = (TextView) convertView
					.findViewById(R.id.feeling_time);
			holder.star1 = (ImageView) convertView
					.findViewById(R.id.first_star);
			holder.star2 = (ImageView) convertView
					.findViewById(R.id.second_star);
			holder.star3 = (ImageView) convertView
					.findViewById(R.id.third_star);
			holder.star4 = (ImageView) convertView.findViewById(R.id.four_star);
			holder.star5 = (ImageView) convertView.findViewById(R.id.five_star);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		stars.add(holder.star1);
		stars.add(holder.star2);
		stars.add(holder.star3);
		stars.add(holder.star4);
		stars.add(holder.star5);
		FeelingContentEntity entity = entitys.get(position);
		holder.tvContent.setText(entity.getContent());
		holder.tvTime.setText(entity.getTime());
		for (int i = 0; i < stars.size(); i++) {
			if (i < entity.getStars()) {
				stars.get(i)
						.setImageDrawable(
								context.getResources().getDrawable(
										R.drawable.star_sel));
			} else {
				stars.get(i).setImageDrawable(
						context.getResources().getDrawable(R.drawable.star));
			}
		}
		if (entity.getImgUrl().size() > 0) {
			try {
				if (entity.isOnline()) {
					for (int i = 0; i < entity.getImgUrl().size(); i++) {

						String[] img = entity.getImgUrl().get(i).split("/");
						if (img.length > 3) {
							LogUtil.log("length = " + img.length);
							String imgUrl = img[4] + "/" + img[5] + "/"
									+ img[6];
							LogUtil.log("imgUrl = " + imgUrl);
							entity.getImgUrl().set(i, imgUrl);
						}
						Bitmap bmp = asynLoader.loadBitmap(holder.ivFeel,
								Constants.GET_QUEST_URI.GET_PICTURE_URI
										+ entity.getImgUrl().get(0) + ".png",
								new ImageCallBack() {
									@Override
									public void imageLoad(ImageView imageView,
											Bitmap bitmap) {
										imageView.setImageBitmap(bitmap);
									}
								});
						if (bmp != null) {
							holder.ivFeel.setImageBitmap(bmp);
						}
					}
				} else {
					InputStream inputStream = new FileInputStream(entity
							.getImgUrl().get(0) + ".png");
					Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
					holder.ivFeel.setImageBitmap(bitmap);
				}
			} catch (Exception e) {
				LogUtil.log("getMessage = " + e.getMessage());
			}
		}
		return convertView;
	}

	private class ViewHolder {
		private ImageView ivFeel;
		private TextView tvContent;
		private TextView tvTime;
		private ImageView star1, star2, star3, star4, star5;
	}
}
