package com.example.adapter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import com.example.constants.AsyncBitmapLoader;
import com.example.constants.AsyncBitmapLoader.ImageCallBack;
import com.example.constants.Constants;
import com.example.constants.LoadImagesUtil;
import com.example.constants.LogUtil;
import com.example.constants.SharedPreferencemanager;
import com.example.entity.NoteBookEntity;
import com.example.personalapp.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NoteBookAdapter extends BaseAdapter {
	private List<NoteBookEntity> entitys;
	private LayoutInflater inflater;
	private Context context;
	private AsyncBitmapLoader asynLoader;

	public NoteBookAdapter(Context context, List<NoteBookEntity> entitys) {
		this.entitys = entitys;
		inflater = LayoutInflater.from(context);
		this.context = context;
		asynLoader = new AsyncBitmapLoader(context,
				Constants.FTP_STATUS.WORKSPACE_NOTE_INFO);
	}

	@Override
	public int getCount() {
		return entitys.size();
	}

	@Override
	public Object getItem(int arg0) {
		return entitys.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int pos, View conterView, ViewGroup arg2) {
		ViewHolder holder;
		if (conterView == null) {
			holder = new ViewHolder();
			conterView = inflater.inflate(R.layout.notebook_listitem, null);
			holder.ivHead = (ImageView) conterView
					.findViewById(R.id.note_img_head);
			holder.tvTitle = (TextView) conterView
					.findViewById(R.id.notebook_item_title);
			holder.tvTime = (TextView) conterView
					.findViewById(R.id.notebook_item_time);
			holder.tvContent = (TextView) conterView
					.findViewById(R.id.notebook_item_content);
			conterView.setTag(holder);
		} else {
			holder = (ViewHolder) conterView.getTag();
		}
		NoteBookEntity entity = entitys.get(pos);
		if (entity.getImgUrl().size() > 0) {
			try {
				LogUtil.log("getImgUrl = " + entity.getImgUrl().get(0)
						+ " online = " + entity.isOnline());
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
						Bitmap bmp = asynLoader.loadBitmap(holder.ivHead,
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
							holder.ivHead.setImageBitmap(bmp);
						}
					}
				} else {
					InputStream inputStream = new FileInputStream(entity
							.getImgUrl().get(0) + ".png");
					Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
					holder.ivHead.setImageBitmap(bitmap);
				}
			} catch (Exception e) {
				LogUtil.log("getMessage = " + e.getMessage());
			}

		}
		holder.tvTitle.setText(entity.getNoteTitle());
		holder.tvTime.setText(entity.getNoteDate());
		String content = entity.getNoteContent();
		if (content.length() > 7) {
			content = content.substring(0, 7) + "...";
		}
		holder.tvContent.setText(content);
		return conterView;
	}

	private class ViewHolder {
		private ImageView ivHead;
		private TextView tvTitle;
		private TextView tvTime;
		private TextView tvContent;
	}

	private Bitmap decodeUriAsBitmap(Uri uri) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(context.getContentResolver()
					.openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}
}
