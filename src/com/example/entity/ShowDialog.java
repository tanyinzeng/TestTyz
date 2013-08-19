package com.example.entity;

import android.graphics.drawable.Drawable;

public class ShowDialog {
	String showContent;
	Drawable ivDrawable;

	public String getShowContent() {
		return showContent;
	}

	public Drawable getIvDrawable() {
		return ivDrawable;
	}

	public void setIvDrawable(Drawable ivDrawable) {
		this.ivDrawable = ivDrawable;
	}

	public void setShowContent(String showContent) {
		this.showContent = showContent;
	}
}
