package com.example.adapter;

import java.io.File;
import java.util.List;
import com.example.constants.AsynImgeLoader;
import com.example.constants.AsynImgeLoader.ImageCallback;
import com.example.constants.AsyncBitmapLoader.ImageCallBack;
import com.example.constants.AsyncBitmapLoader;
import com.example.constants.Constants;
import com.example.constants.LogUtil;
import com.example.constants.SharedPreferencemanager;
import com.example.personalapp.PersonMyPicActivity;
import com.example.personalapp.R;
import com.example.personalapp.SlidingMenuActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MyPicAdapter extends BaseAdapter {
	private List<String> imgUrls;
	private LayoutInflater inflater;
	private Context context;
	private int mCount = 0;
	private AsynImgeLoader asyncImageLoader;
	private String fromWhere;
	private AsyncBitmapLoader asynLoader;

	public MyPicAdapter(Context context, List<String> imgUrls, String fromWhere) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.imgUrls = imgUrls;
		asyncImageLoader = new AsynImgeLoader(context, fromWhere);
		this.fromWhere = fromWhere;
		asynLoader = new AsyncBitmapLoader(context,
				fromWhere);
	}

	@Override
	public int getCount() {
		return imgUrls.size();
	}

	@Override
	public Object getItem(int position) {
		return imgUrls.get(position);
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
		LogUtil.log("getView after  = " + position);
		vh.btnShare.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (SharedPreferencemanager.getPhotoOnline(context)) {
					Intent intent = new Intent(Intent.ACTION_SEND);
					intent.setType("text/plain");
					intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
					// ∑÷œÌºƒ”Ô
					intent.putExtra(Intent.EXTRA_TEXT, "œ≤ª∂≤ª");

					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(Intent.createChooser(intent, context
							.getResources().getString(R.string.app_name)));
				} else {
					Intent intent = new Intent(Intent.ACTION_SEND);
					try {
						intent.putExtra(Intent.EXTRA_STREAM,
								Uri.fromFile(new File(imgUrls.get(position))));
					} catch (Exception e) {
						Toast.makeText(context, "∑÷œÌÕº∆¨¥ÌŒÛ", Toast.LENGTH_SHORT)
								.show();
						return;
					}
					intent.setType("image/*");
					intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
					// ∑÷œÌºƒ”Ô
					intent.putExtra(Intent.EXTRA_TEXT, "œ≤ª∂≤ª");

					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(Intent.createChooser(intent, context
							.getResources().getString(R.string.app_name)));
				}
			}
		});

		if (Constants.FTP_STATUS.WORKSPACE_ALBUM_INFO.equals(fromWhere)) {
			if (SharedPreferencemanager.getPhotoOnline(context)) {
				Bitmap bmp = asynLoader.loadBitmap(vh.iv,
						Constants.GET_QUEST_URI.GET_PICTURE_URI
								+ SharedPreferencemanager.getPhone(context)
								+ imgUrls.get(position), new ImageCallBack() {
							@Override
							public void imageLoad(ImageView imageView,
									Bitmap bitmap) {
								imageView.setImageBitmap(bitmap);
							}
						});
				if (bmp != null) {
					vh.iv.setImageBitmap(bmp);
				}
			} else {
				try {
					LogUtil.log("imgUrl = " + imgUrls.get(position));
					Drawable cachedImage = asyncImageLoader.loadDrawable(vh.iv,
							imgUrls.get(position), new ImageCallback() {

								@Override
								public void imageLoaded(Drawable imageDrawable,
										ImageView iv) {
									iv.setImageDrawable(imageDrawable);
								}
							});
					if (cachedImage != null) {
						vh.iv.setImageDrawable(cachedImage);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LogUtil.log("setOnClickListener");
				if (Constants.FTP_STATUS.WORKSPACE_ALBUM_INFO.equals(fromWhere)) {
					SlidingMenuActivity.sendHandlerMessage(
							Constants.USER_STATUS.MYPIC_ADAPTER_TO_PIC,
							position);
				} else {
					PersonMyPicActivity.sendHandlerMessage(
							Constants.USER_STATUS.MYPIC_ADAPTER_TO_PIC,
							position);
				}
			}
		});

		return convertView;
	}

}
