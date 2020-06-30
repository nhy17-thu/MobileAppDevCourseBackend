package com.mobilecourse.backend.model;

import java.sql.Timestamp;

public class OrderImage {
    private int iid;                    // 图片id
    private int oid;                    // 图片所属订单id
    private String imagePath;           // 图片路径
    private Timestamp uploadTime;       // 图片上传时间

    @Override
    public String toString() {
        return "OrderImage{" +
                "iid=" + iid +
                ", oid=" + oid +
                ", imagePath='" + imagePath + '\'' +
                ", uploadTime=" + uploadTime +
                '}';
    }

    public int getIid() {
        return iid;
    }

    public void setIid(int iid) {
        this.iid = iid;
    }

    public int getOid() {
        return oid;
    }

    public void setOid(int oid) {
        this.oid = oid;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Timestamp getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Timestamp uploadTime) {
        this.uploadTime = uploadTime;
    }
}
