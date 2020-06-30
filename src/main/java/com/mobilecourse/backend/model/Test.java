package com.mobilecourse.backend.model;

import java.sql.Timestamp;

// 此类中的类型必须和数据库中table的列类型一一对应，否则会出现问题
public class Test {
    // getter和setter可以使用idea自带的功能生成，右键点击->Generate->Getter and Setter即可自动生成
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    private int id;
    private String content;
    private Timestamp createTime;
}
