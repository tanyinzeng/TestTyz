package com.example.personalapp;

import java.util.List;
import org.taptwo.android.widget.CircleFlowIndicator;
import org.taptwo.android.widget.ViewFlow;
import com.example.adapter.ViewFlowAdapter2;
import com.example.constants.Constants;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class ShowPhotoItemActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.show_photo_item_layout);
		Intent intent = getIntent();
		List<String> imgUrls = intent.getStringArrayListExtra("imgUrls");
		int pos = intent.getIntExtra("pos", 0);
		mViewFlow = (ViewFlow) findViewById(R.id.viewflow);
		mIndicator = (CircleFlowIndicator) findViewById(R.id.viewflowindic);
		mViewFlow.setFlowIndicator(mIndicator);
		mViewFlow.setAdapter(new ViewFlowAdapter2(this, imgUrls,
				Constants.FTP_STATUS.WORKSPACE_ALBUM_INFO), pos);
	}

	// note four
	private ViewFlow mViewFlow;
	private CircleFlowIndicator mIndicator = null;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mViewFlow.onConfigurationChanged(newConfig);
	}
}
