package com.example.entity;

public class SmsOnlineEntity {
	/*
	 * ([{"id":"53","icon":"/img/ble/ppc002.jpg","name":"huqin123",
	 * "date":"2013/7/24 17:35:34","content":"艾丝凡按时发生地 "},
	 * {"id":"52","icon":"/img/ble/ppc002.jpg","name":"11111",
	 * "date":"2013/7/22 15:15:48","content":"艾丝凡按时发生地 "}]
	 */
	private String id;
	private String icon;
	private String name;
	private String date;
	private String content;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
