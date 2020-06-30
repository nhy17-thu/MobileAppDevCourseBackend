package com.mobilecourse.backend.model;

import java.sql.Timestamp;

// 此类中的类型必须和数据库中table的列类型一一对应，否则会出现问题
public class Order {
    private int oid;                    // 订单id
    private int issuerId;               // 发布方uid
    private Integer receiverId;         // 接单方uid（注意要用Integer，否则赋值为null时会报错！）
    private String title;               // 订单标题
    private String content;             // 详细描述订单的具体内容，细节要求等
    private Integer price;              // 订单奖励的金额
    private Timestamp updateTime;       // 订单状态上一次更新的时间
    private String status;              // 订单状态分四种：published/received/receiverComplete/confirmed
    private String receiverLongitude;   // 接单方当前经度
    private String receiverLatitude;    // 接单方当前纬度
    private String startPos;            // 订单开始位置
    private String endPos;              // 订单目的地

    @Override
    public String toString() {
        return "Order{" +
                "oid=" + oid +
                ", issuerId=" + issuerId +
                ", receiverId=" + receiverId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", price=" + price +
                ", updateTime=" + updateTime +
                ", status='" + status + '\'' +
                ", receiverLongitude='" + receiverLongitude + '\'' +
                ", receiverLatitude='" + receiverLatitude + '\'' +
                ", startPos='" + startPos + '\'' +
                ", endPos='" + endPos + '\'' +
                '}';
    }

    public int getOid() {
        return oid;
    }

    public void setOid(int oid) {
        this.oid = oid;
    }

    public int getIssuerId() {
        return issuerId;
    }

    public void setIssuerId(int issuerId) {
        this.issuerId = issuerId;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReceiverLongitude() {
        return receiverLongitude;
    }

    public void setReceiverLongitude(String receiverLongitude) {
        this.receiverLongitude = receiverLongitude;
    }

    public String getReceiverLatitude() {
        return receiverLatitude;
    }

    public void setReceiverLatitude(String receiverLatitude) {
        this.receiverLatitude = receiverLatitude;
    }

    public String getStartPos() {
        return startPos;
    }

    public void setStartPos(String startPos) {
        this.startPos = startPos;
    }

    public String getEndPos() {
        return endPos;
    }

    public void setEndPos(String endPos) {
        this.endPos = endPos;
    }
}
