package com.example.entity;

import java.io.Serializable;

public class ProjectEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2864323125100251209L;
	private String startTime;
	private String endTime;
	private String proType;
	private String proName;
	private String proImport;
	private String proMemo;

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getProType() {
		return proType;
	}

	public void setProType(String proType) {
		this.proType = proType;
	}

	public String getProName() {
		return proName;
	}

	public void setProName(String proName) {
		this.proName = proName;
	}

	public String getProImport() {
		return proImport;
	}

	public void setProImport(String proImport) {
		this.proImport = proImport;
	}

	public String getProMemo() {
		return proMemo;
	}

	public void setProMemo(String proMemo) {
		this.proMemo = proMemo;
	}
}
