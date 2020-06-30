package com.mobilecourse.backend.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.sql.Timestamp;

// 此类中的类型必须和数据库中table的列类型一一对应，否则会出现问题
public class User {
	private int uid;

	@NotEmpty(message = "昵称不能为空")
	@NotBlank(message = "昵称不能为空")
	private String name;

	@Email(message = "请输入有效的邮箱地址")
	private String email;

	private String password;    // 密码使用SHA-256算法加密后存储

	private Timestamp createTime;

	private String avatar;

	private String status;      // 标示用户是否激活：未激活用户为随机uuid，已激活用户为name

	@Override
	public String toString() {
		return "User{" +
				"uid=" + uid +
				", name='" + name + '\'' +
				", email='" + email + '\'' +
				", password='" + password + '\'' +
				", createTime=" + createTime +
				", avatar='" + avatar + '\'' +
				", status='" + status + '\'' +
				'}';
	}

	// getter和setter可以使用idea自带的功能生成，右键点击->Generate->Getter and Setter即可自动生成
	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
