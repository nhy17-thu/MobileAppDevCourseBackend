package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderDao {
    // 函数的名称要和对应的Mapper文件中的id完全对应

    // 如下查询中的关键词，前后要用%包裹，如'%keyword%'
    // 订单状态分四种：published/received/receiverComplete/confirmed
    // 根据oid查找订单
    Order getOrderByOid(int oid);
    // 根据发布方uid查找所有订单，时间降序（最新的在最前）
    List<Order> getOrdersByIssuerId(int uid);
    // 根据接单方uid查找所有订单，时间降序（最新的在最前）
    List<Order> getOrdersByReceiverId(int uid);
    // 根据发布方uid查找所有已完成的订单，时间降序（最新的在最前）
    List<Order> getCompletedOrdersByIssuerId(int uid);
    // 根据接单方uid查找所有已完成的订单，时间降序（最新的在最前）
    List<Order> getCompletedOrdersByReceiverId(int uid);
    // 根据标题，查找所有标题中出现给定关键字的订单，时间降序（最新的在最前）
    List<Order> getOrdersByTitleKeyword(String keyword);
    // 根据正文，查找所有正文中出现给定关键字的订单，时间降序（最新的在最前）
    List<Order> getOrdersByContentKeyword(String keyword);
    // 根据价格，查找所有价格在[lowerBound, upperBound]之间的订单，时间降序（最新的在最前）
    List<Order> getOrdersByPriceRange(int lowerBound, int upperBound);
    // 根据出发位置，查找所有出发位置中出现给定关键字的订单，时间降序（最新的在最前）
    List<Order> getOrdersByStartPosKeyword(String keyword);
    // 根据目的地，查找所有目的地中出现给定关键字的订单，时间降序（最新的在最前）
    List<Order> getOrdersByEndPosKeyword(String keyword);

    // 添加新订单到数据库
    // 这里只需要issuerId, title, content, price, startPos, endPos
    // oid, updateTime, status为自动生成的默认值
    // receiverId, receiverLongitude, receiverLatitude暂时为NULL
    void addOrder(Order order);

    // 将对象中的内容更新入库（对象中有值时更新字段，没有值的属性不修改）
    void updateOrderSelectiveByOID(Order order);
}
