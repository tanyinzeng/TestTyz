package com.example.entity;

import java.io.Serializable;
import java.util.List;

public class FeelingContentEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4482492749659374272L;
	private List<String> imgUrl;
	private String content;
	private String time;
	private int stars;
	private boolean isOnline;

	public boolean isOnline() {
		return isOnline;
	}

	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

	public List<String> getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(List<String> imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getStars() {
		return stars;
	}

	public void setStars(int stars) {
		this.stars = stars;
	}
}
