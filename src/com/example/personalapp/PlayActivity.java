package com.example.personalapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class PlayActivity extends BaseActivity {
	private VideoView videoView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_view);
		Intent intent = getIntent();
		String uri = intent.getStringExtra("uri");
		videoView = (VideoView) findViewById(R.id.videoView);
		videoView.setVideoURI(Uri.parse(uri));
		MediaController mediaController = new MediaController(PlayActivity.this);
		videoView.setMediaController(mediaController);
		videoView.start();
	}
}
