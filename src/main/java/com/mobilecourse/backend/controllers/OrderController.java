package com.mobilecourse.backend.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.dao.OrderDao;
import com.mobilecourse.backend.WebSocketServer;
import com.mobilecourse.backend.model.Order;
import org.apache.tomcat.jni.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Hashtable;
import java.util.List;

@RestController
@EnableAutoConfiguration
//设置此参数可以给这个类的所有接口都加上前缀
@RequestMapping("/order")
public class OrderController extends CommonController {
    @Autowired
    private OrderDao orderMapper;

    // 此Controller所有API均经过测试可用

    // 用来给普通类调用mapper
    public static OrderController orderController;

    @PostConstruct
    public void init() {
        orderController = this;
        // 用来给普通类调用mapper
        orderController.orderMapper = this.orderMapper;
    }

    // 根据oid找到指定订单
    public Order getOrderByOid(int oid) {
        try {
            return orderController.orderMapper.getOrderByOid(oid);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 根据oid查找指定订单
    @RequestMapping(value = "/getOrders/{oid}", method = {RequestMethod.GET})
    public String getOrdersByOid(@PathVariable int oid) {
        try {
            Order order = orderController.orderMapper.getOrderByOid(oid);
            if (order == null) return wrapperMsg(403, "没有找到对应的订单！");
            JSONObject jsonObject = getJsonOrderObject(order);
            return wrapperMsg(200, jsonObject.toJSONString());
        } catch (Exception e) {
            return wrapperMsg(500, e.toString());
        }
    }

    // 根据issuerId查找所有订单，时间降序（最新的在最前）
    @RequestMapping(value = "/getOrders/issuer", method = {RequestMethod.GET})
    public String getOrdersByIssuerId(@RequestParam(value = "uid")int issuerId) {
        try {
            JSONArray jsonArray = new JSONArray();
            List<Order> list = orderController.orderMapper.getOrdersByIssuerId(issuerId);
            if (list.isEmpty()) return wrapperMsg(403, "这名用户暂未发布任何订单！");
            for (Order o : list) {
                jsonArray.add(getJsonOrderObject(o));
            }
            return wrapperMsg(200, jsonArray.toJSONString());
        } catch (Exception e) {
            return wrapperMsg(500, e.toString());
        }
    }

    // 根据receiverId查找所有订单，时间降序（最新的在最前）
    @RequestMapping(value = "/getOrders/receiver", method = {RequestMethod.GET})
    public String getOrdersByReceiverId(@RequestParam(value = "uid")int receiverId) {
        try {
            JSONArray jsonArray = new JSONArray();
            List<Order> list = orderController.orderMapper.getOrdersByReceiverId(receiverId);
            if (list.isEmpty()) return wrapperMsg(403, "这名用户暂未接收任何订单！");
            for (Order o : list) {
                jsonArray.add(getJsonOrderObject(o));
            }
            return wrapperMsg(200, jsonArray.toJSONString());
        } catch (Exception e) {
            return wrapperMsg(500, e.toString());
        }
    }

    // 根据issuerId查找所有已经完成的订单，时间降序（最新的在最前）
    @RequestMapping(value = "/getCompletedOrders/issuer", method = {RequestMethod.GET})
    public String getCompletedOrdersByIssuerId(@RequestParam(value = "uid")int issuerId) {
        try {
            JSONArray jsonArray = new JSONArray();
            List<Order> list = orderController.orderMapper.getCompletedOrdersByIssuerId(issuerId);
            if (list.isEmpty()) return wrapperMsg(403, "这名用户暂无已完成的发布订单！");
            for (Order o : list) {
                jsonArray.add(getJsonOrderObject(o));
            }
            return wrapperMsg(200, jsonArray.toJSONString());
        } catch (Exception e) {
            return wrapperMsg(500, e.toString());
        }
    }

    // 根据receiverId查找所有已经完成的订单，时间降序（最新的在最前）
    @RequestMapping(value = "/getCompletedOrders/receiver", method = {RequestMethod.GET})
    public String getCompletedOrdersByReceiverId(@RequestParam(value = "uid")int receiverId) {
        try {
            JSONArray jsonArray = new JSONArray();
            List<Order> list = orderController.orderMapper.getCompletedOrdersByReceiverId(receiverId);
            if (list.isEmpty()) return wrapperMsg(403, "这名用户暂无已完成的接收¥订单！");
            for (Order o : list) {
                jsonArray.add(getJsonOrderObject(o));
            }
            return wrapperMsg(200, jsonArray.toJSONString());
        } catch (Exception e) {
            return wrapperMsg(500, e.toString());
        }
    }

    // 根据标题，查找所有标题中出现给定关键字的订单，时间降序（最新的在最前）
    @RequestMapping(value = "/getOrders/title", method = {RequestMethod.GET})
    public String getOrdersByTitleKeyword(@RequestParam(value = "keyword")String keyword) {
        try {
            JSONArray jsonArray = new JSONArray();
            List<Order> list = orderController.orderMapper.getOrdersByTitleKeyword("%" + keyword + "%");
            if (list.isEmpty()) return wrapperMsg(403, "无相关搜索结果！");
            for (Order o : list) {
                jsonArray.add(getJsonOrderObject(o));
            }
            return wrapperMsg(200, jsonArray.toJSONString());
        } catch (Exception e) {
            return wrapperMsg(500, e.toString());
        }
    }

    // 根据正文，查找所有正文中出现给定关键字的订单，时间降序（最新的在最前）
    @RequestMapping(value = "/getOrders/content", method = {RequestMethod.GET})
    public String getOrdersByContentKeyword(@RequestParam(value = "keyword")String keyword) {
        try {
            JSONArray jsonArray = new JSONArray();
            List<Order> list = orderController.orderMapper.getOrdersByContentKeyword("%" + keyword + "%");
            if (list.isEmpty()) return wrapperMsg(403, "无相关搜索结果！");
            for (Order o : list) {
                jsonArray.add(getJsonOrderObject(o));
            }
            return wrapperMsg(200, jsonArray.toJSONString());
        } catch (Exception e) {
            return wrapperMsg(500, e.toString());
        }
    }

    // 根据价格，查找所有价格在[lowerBound, upperBound]之间的订单，时间降序（最新的在最前）
    @RequestMapping(value = "/getOrders/priceRange", method = {RequestMethod.GET})
    public String getOrdersByPriceRange(@RequestParam(value = "lowerBound")int lowerBound,
                                        @RequestParam(value = "upperBound")int upperBound) {
        try {
            JSONArray jsonArray = new JSONArray();
            List<Order> list = orderController.orderMapper.getOrdersByPriceRange(lowerBound, upperBound);
            if (list.isEmpty()) return wrapperMsg(403, "无相关搜索结果！");
            for (Order o : list) {
                jsonArray.add(getJsonOrderObject(o));
            }
            return wrapperMsg(200, jsonArray.toJSONString());
        } catch (Exception e) {
            return wrapperMsg(500, e.toString());
        }
    }

    // 根据出发位置，查找所有出发位置中出现给定关键字的订单，时间降序（最新的在最前）
    @RequestMapping(value = "/getOrders/startPos", method = {RequestMethod.GET})
    public String getOrdersByStartPosKeyword(@RequestParam(value = "keyword")String keyword) {
        try {
            JSONArray jsonArray = new JSONArray();
            List<Order> list = orderController.orderMapper.getOrdersByStartPosKeyword("%" + keyword + "%");
            if (list.isEmpty()) return wrapperMsg(403, "无相关搜索结果！");
            for (Order o : list) {
                jsonArray.add(getJsonOrderObject(o));
            }
            return wrapperMsg(200, jsonArray.toJSONString());
        } catch (Exception e) {
            return wrapperMsg(500, e.toString());
        }
    }

    // 根据目的地，查找所有目的地中出现给定关键字的订单，时间降序（最新的在最前）
    @RequestMapping(value = "/getOrders/endPos", method = {RequestMethod.GET})
    public String getOrdersByEndPosKeyword(@RequestParam(value = "keyword")String keyword) {
        try {
            JSONArray jsonArray = new JSONArray();
            List<Order> list = orderController.orderMapper.getOrdersByEndPosKeyword("%" + keyword + "%");
            if (list.isEmpty()) return wrapperMsg(403, "无相关搜索结果！");
            for (Order o : list) {
                jsonArray.add(getJsonOrderObject(o));
            }
            return wrapperMsg(200, jsonArray.toJSONString());
        } catch (Exception e) {
            return wrapperMsg(500, e.toString());
        }
    }

    // 添加新订单到数据库
    // 这里只需要issuerId（从session获取）, title, content, price, startPos, endPos
    // oid, updateTime, status为自动生成的默认值
    // receiverId, receiverLongitude, receiverLatitude暂时为NULL
    @RequestMapping(value = "/add", method = {RequestMethod.PUT})
    public String addOrder(HttpServletRequest request,
                           @RequestParam(value = "title")String title,
                           @RequestParam(value = "content")String content,
                           @RequestParam(value = "price")Integer price,
                           @RequestParam(value = "startPos")String startPos,
                           @RequestParam(value = "endPos")String endPos) {
        try {
            Order o = new Order();
            o.setIssuerId((int) request.getSession().getAttribute("uid"));
            o.setTitle(title);
            o.setContent(content);
            o.setPrice(price);
            o.setStartPos(startPos);
            o.setEndPos(endPos);
            orderController.orderMapper.addOrder(o);

            JSONObject wrapperMsg = new JSONObject();
            wrapperMsg.put("code", 201);
            wrapperMsg.put("msg", o.getTitle() + " has been successfully published!");
            wrapperMsg.put("oid", o.getOid());
            return wrapperMsg.toJSONString();
        } catch (Exception e) {
            return wrapperMsg(500, e.toString());
        }
    }

    // 更新给定订单的信息：receiverId,title,content,price,status,receiverLongitude,receiverLatitude,startPos,endPos
    @RequestMapping(value = "/update/{oid}", method = {RequestMethod.POST})
    public String updateOrderInfo(HttpServletRequest request,
                                  @PathVariable int oid,
                                  @RequestParam(value = "receiverId", required = false)Integer receiverId,
                                  @RequestParam(value = "title", required = false)String title,
                                  @RequestParam(value = "content", required = false)String content,
                                  @RequestParam(value = "price", required = false)Integer price,
                                  @RequestParam(value = "status", required = false)String status,
                                  @RequestParam(value = "receiverLongitude", required = false)String receiverLongitude,
                                  @RequestParam(value = "receiverLatitude", required = false)String receiverLatitude,
                                  @RequestParam(value = "startPos", required = false)String startPos,
                                  @RequestParam(value = "endPos", required = false)String endPos) {
        // 先检查当前用户是否有权限更新oid对应订单：为发布方或接单方？
        int uid = (int) request.getSession().getAttribute("uid");
        Order target = orderController.orderMapper.getOrderByOid(oid);
        if (target == null) return wrapperMsg(403, "所给订单不存在，请检查后重试！");

        Order o = new Order();
        o.setOid(oid);
        if (target.getIssuerId() == uid) {
            // 如果为发布方，可以更新除接单方信息外的所有信息
            // 没有接收到的参数均为null，在SQL语句中会被忽略
            o.setTitle(title);
            o.setContent(content);
            o.setPrice(price);
            o.setStatus(status);
            o.setStartPos(startPos);
            o.setEndPos(endPos);
        } else if (target.getReceiverId() == null || target.getReceiverId() == uid) {
            // 如果还没有接单方或当前用户为接单方，可以更新接单方信息
            o.setReceiverId(receiverId);
            // 订单状态分四种：published/received/receiverComplete/confirmed
            o.setStatus(status);
            o.setReceiverLongitude(receiverLongitude);
            o.setReceiverLatitude(receiverLatitude);
        } else {
            // 否则无权限更新信息
            return wrapperMsg(403, "当前用户无权限更新这个订单的信息！");
        }
        orderController.orderMapper.updateOrderSelectiveByOID(o);
        return wrapperMsg(200, "成功更新oid: " + oid + "订单的信息");
    }

    // 用于接收订单的专用接口，可以后续再更新坐标
    @RequestMapping(value = "/receive/{oid}", method = {RequestMethod.GET})
    public String receiveOrder(HttpServletRequest request,
                               @PathVariable int oid) {
        int uid = (int) request.getSession().getAttribute("uid");
        Order target = orderController.orderMapper.getOrderByOid(oid);
        if (target.getReceiverId() != null) return wrapperMsg(403, "所给订单已被接单，请检查后重试！");

        target.setReceiverId(uid);
        target.setStatus("received");   // 订单状态分四种：published/received/receiverComplete/confirmed
        // 设置默认经纬度：清华大学（浮点数表示，东经/北纬为正）
        target.setReceiverLongitude("116.32");
        target.setReceiverLatitude("40.00");
        orderController.orderMapper.updateOrderSelectiveByOID(target);
        return wrapperMsg(200, "oid: " + oid + "已经由uid: " + uid + "接单成功。");
    }

    // 用于接收订单的专用接口，可以后续再更新坐标
    @RequestMapping(value = "/complete/{oid}", method = {RequestMethod.GET})
    public String completeOrder(HttpServletRequest request,
                               @PathVariable int oid) {
        int uid = (int) request.getSession().getAttribute("uid");
        Order target = orderController.orderMapper.getOrderByOid(oid);
        if (target.getIssuerId() != uid) return wrapperMsg(403, "当前用户不是订单发起人，无法确认订单完成！");

        target.setStatus("confirmed");  // 订单状态分四种：published/received/receiverComplete/confirmed
        orderController.orderMapper.updateOrderSelectiveByOID(target);
        return wrapperMsg(200, "oid: " + oid + "已经由发起人确认完成。");
    }


    private static JSONObject getJsonOrderObject(Order o) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("oid", o.getOid());
        jsonObject.put("issuerId", o.getIssuerId());
        jsonObject.put("receiverId", o.getReceiverId());
        jsonObject.put("title", o.getTitle());
        jsonObject.put("content", o.getContent());
        jsonObject.put("price", o.getPrice());
        jsonObject.put("updateTime", o.getUpdateTime());
        jsonObject.put("status", o.getStatus());
        jsonObject.put("startPos", o.getStartPos());
        jsonObject.put("endPos", o.getEndPos());
        jsonObject.put("receiverLongitude", o.getReceiverLongitude());
        jsonObject.put("receiverLatitude", o.getReceiverLatitude());
        return jsonObject;
    }

}
