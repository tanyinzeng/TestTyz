package com.example.entity;

import java.io.Serializable;

public class CostSpendEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8055783522271945185L;
	private String title;
	private String money;
	private String time;
	private String memo;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}
}
