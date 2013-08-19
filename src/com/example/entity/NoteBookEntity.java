package com.example.entity;

import java.io.Serializable;
import java.util.List;

public class NoteBookEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3085089183551892483L;
	private String noteTitle;
	private String noteDate;
	private String noteContent;
	private int starTag;
	private List<String> imgUrl;
	private boolean isOnline = false;
	

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

	public int getStarTag() {
		return starTag;
	}

	public void setStarTag(int starTag) {
		this.starTag = starTag;
	}

	public String getNoteTitle() {
		return noteTitle;
	}

	public void setNoteTitle(String noteTitle) {
		this.noteTitle = noteTitle;
	}

	public String getNoteDate() {
		return noteDate;
	}

	public void setNoteDate(String noteDate) {
		this.noteDate = noteDate;
	}

	public String getNoteContent() {
		return noteContent;
	}

	public void setNoteContent(String noteContent) {
		this.noteContent = noteContent;
	}

}
