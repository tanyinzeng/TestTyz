package com.example.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import com.example.constants.AsynImgeLoader;
import com.example.constants.AsyncBitmapLoader.ImageCallBack;
import com.example.constants.AsyncBitmapLoader;
import com.example.constants.Constants;
import com.example.constants.LogUtil;
import com.example.constants.SharedPreferencemanager;
import com.example.entity.MyPicEntity;
import com.example.personalapp.PersonMyPicActivity;
import com.example.personalapp.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class CopyOfMyPicAdapter extends BaseAdapter {
	private List<MyPicEntity> entitys;
	private LayoutInflater inflater;
	private Context context;
	private int mCount = 0;
	private AsynImgeLoader asyncImageLoader;
	private AsyncBitmapLoader asynLoader;

	public CopyOfMyPicAdapter(Context context, List<MyPicEntity> entitys,
			String fromWhere) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.entitys = entitys;
		asyncImageLoader = new AsynImgeLoader(context, fromWhere);
		asynLoader = new AsyncBitmapLoader(context, fromWhere);
	}

	@Override
	public int getCount() {
		if (entitys == null) {
			return 0;
		} else {
			return entitys.size();
		}
	}

	@Override
	public Object getItem(int position) {
		return entitys.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class ViewHold {
		private ImageView iv;
		private Button btnShare;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LogUtil.log("getView = " + position);
		if (position == 0) {
			mCount++;
		} else {
			mCount = 0;
		}

		ViewHold vh = null;
		if (convertView == null) {
			vh = new ViewHold();
			convertView = inflater.inflate(R.layout.my_pic_layout, null);
			vh.iv = (ImageView) convertView.findViewById(R.id.record_list_img);
			vh.btnShare = (Button) convertView.findViewById(R.id.btn_share);
			convertView.setTag(vh);
		} else {
			vh = (ViewHold) convertView.getTag();
		}

		if (mCount > 1) {
			return convertView;
		}

		if (position != 0) {
			mCount = 0;
		}
		final MyPicEntity entity = entitys.get(position);
		LogUtil.log("getView after  = " + position);
		vh.btnShare.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (entity.isOnline()) {
					Intent intent = new Intent(Intent.ACTION_SEND);
					intent.setType("text/plain");
					intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
					// ·ÖÏí¼ÄÓï
					intent.putExtra(Intent.EXTRA_TEXT, "Ï²»¶²»");

					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(Intent.createChooser(intent, context
							.getResources().getString(R.string.app_name)));
				} else {
					Intent intent = new Intent(Intent.ACTION_SEND);
					try {
						intent.putExtra(Intent.EXTRA_STREAM,
								Uri.fromFile(new File(entity.getImgUrls().get(0))));
					} catch (Exception e) {
						Toast.makeText(context, "·ÖÏíÍ¼Æ¬´íÎó", Toast.LENGTH_SHORT)
								.show();
						return;
					}
					intent.setType("image/*");
					intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
					// ·ÖÏí¼ÄÓï
					intent.putExtra(Intent.EXTRA_TEXT, "Ï²»¶²»");

					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(Intent.createChooser(intent, context
							.getResources().getString(R.string.app_name)));
				}
			}
		});

		if (entity.isOnline()) {
			String[] img = entity.getImgUrls().get(0).split("/");
			LogUtil.log("length = " + img.length);
			if (img.length > 3) {
				String imgUrl = img[4] + "/" + img[5] + "/" + img[6];
				LogUtil.log("imgUrl = " + imgUrl);
				entity.getImgUrls().set(0,imgUrl);
			}
			Bitmap bmp = asynLoader.loadBitmap(vh.iv,
					Constants.GET_QUEST_URI.GET_PICTURE_URI
							+ SharedPreferencemanager.getPhone(context)
							+ entity.getImgUrls().get(0) + ".png", new ImageCallBack() {
						@Override
						public void imageLoad(ImageView imageView, Bitmap bitmap) {
							imageView.setImageBitmap(bitmap);
						}
					});
			if (bmp != null) {
				vh.iv.setImageBitmap(bmp);
			}
		} else {
			try {
				LogUtil.log("imgUrl = " + entity.getImgUrls().get(0));
				// Drawable cachedImage = asyncImageLoader.loadDrawable(vh.iv,
				// entity.getImgUrl()+".png", new ImageCallback() {
				//
				// @Override
				// public void imageLoaded(Drawable imageDrawable,
				// ImageView iv) {
				// iv.setImageDrawable(imageDrawable);
				// }
				// });
				// if (cachedImage != null) {
				// vh.iv.setImageDrawable(cachedImage);
				// }
				InputStream inputStream = new FileInputStream(
						entity.getImgUrls().get(0) + ".png");
				Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
				vh.iv.setImageBitmap(bitmap);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LogUtil.log("setOnClickListener");
				PersonMyPicActivity.sendHandlerMessage(
						Constants.USER_STATUS.MYPIC_ADAPTER_TO_PIC, position);
			}
		});

		return convertView;
	}

}
