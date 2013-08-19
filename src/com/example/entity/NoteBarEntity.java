package com.example.entity;

import java.io.Serializable;

public class NoteBarEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1195121292912173775L;
	private String barName;
	private String dateComplete;
	private int forwardDay;

	public String getBarName() {
		return barName;
	}

	public void setBarName(String barName) {
		this.barName = barName;
	}

	public String getDateComplete() {
		return dateComplete;
	}

	public void setDateComplete(String dateComplete) {
		this.dateComplete = dateComplete;
	}

	public int getForwardDay() {
		return forwardDay;
	}

	public void setForwardDay(int forwardDay) {
		this.forwardDay = forwardDay;
	}
}
