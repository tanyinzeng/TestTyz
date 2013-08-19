package com.example.entity;

import java.io.Serializable;

public class ScoreContentEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4092871606594176879L;
	private String name;
	private String time;
	private int score;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
}
