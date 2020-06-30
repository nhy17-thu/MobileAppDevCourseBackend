package com.mobilecourse.backend.model;

import java.sql.Time;
import java.sql.Timestamp;

// 此类中的类型必须和数据库中table的列类型一一对应，否则会出现问题
public class Chat {
	// getter和setter可以使用idea自带的功能生成，右键点击->Generate->Getter and Setter即可自动生成
	private int id;
	private int senderId;
	private int receiverId;
	private String content;
	private Timestamp sendTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSenderId() {
		return senderId;
	}

	public void setSenderId(int senderId) {
		this.senderId = senderId;
	}

	public int getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(int receiverId) {
		this.receiverId = receiverId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Timestamp getSendTime() {
		return sendTime;
	}

	public void setSendTime(Timestamp sendTime) {
		this.sendTime = sendTime;
	}

	@Override
	public String toString() {
		return "Chat{" +
				"id=" + id +
				", senderId=" + senderId +
				", receiverId=" + receiverId +
				", content='" + content + '\'' +
				", sendTime=" + sendTime +
				'}';
	}
}
