package com.example.entity;

import java.io.Serializable;
import java.util.List;

public class PersonRecordEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4048810425733443985L;
	private String title;
	private List<String> imgUrls;
	private String time;
	private boolean isOnline = false;

	

	public boolean isOnline() {
		return isOnline;
	}

	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getImgUrls() {
		return imgUrls;
	}

	public void setImgUrls(List<String> imgUrls) {
		this.imgUrls = imgUrls;
	}
}
