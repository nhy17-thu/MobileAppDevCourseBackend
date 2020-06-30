package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.OrderImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderImageDao {
    // 函数的名称要和对应的Mapper文件中的id完全对应

    // 查找给定iid对应的图片
    OrderImage getImageByImageId(int iid);
    // 查找一个订单oid对应的所有图片信息，按上传时间升序（最早上传的在最前）
    List<OrderImage> getImagesByOrderId(int oid);

    // 图片信息保存到数据库
    void addOrderImage(OrderImage orderImage);
}
