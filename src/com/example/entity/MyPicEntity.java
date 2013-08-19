package com.example.entity;

import java.io.Serializable;
import java.util.List;

public class MyPicEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7536990721012747434L;
	private List<String> imgUrls;
	private boolean isOnline;
	
	public boolean isOnline() {
		return isOnline;
	}
	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}
	public List<String> getImgUrls() {
		return imgUrls;
	}
	public void setImgUrls(List<String> imgUrls) {
		this.imgUrls = imgUrls;
	}
	
}
