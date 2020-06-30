package com.mobilecourse.backend.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.dao.OrderImageDao;
import com.mobilecourse.backend.model.Order;
import com.mobilecourse.backend.model.OrderImage;
import com.mobilecourse.backend.model.User;
import org.apache.tomcat.jni.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

/*
 * 上传多张图片：/orderImage/upload，POST请求（一次传一张，多次上传）
 *      参数: int oid, MultipartFile image
 * 下载多张图片：
 * 1. 给定oid，获取对应订单的所有图片信息
 *      /orderImage/getImages，GET请求
 *      参数：int oid
 *      返回值：json格式字符串，包含iid,oid,imagePath,uploadTime
 * 2. 根据步骤1中返回的所有图片iid，逐张下载：
 *      /orderImage/download/{iid}
 *      返回值：以流的形式返回这张图片（类似于下载头像）
 */

@RestController
@EnableAutoConfiguration
//设置此参数可以给这个类的所有接口都加上前缀
@RequestMapping("/orderImage")
public class OrderImageController extends CommonController {
    @Autowired
    private OrderImageDao orderImageMapper;

    // 此Controller三个API均经测试可用

    // 上传订单的图片，每次上传一张
    @RequestMapping(value = "/upload", method = {RequestMethod.POST})
    public String uploadOrderImages(HttpServletRequest request,
                                    @RequestParam(value = "oid")int oid,
                                    @RequestParam(value = "image")MultipartFile image) {
        int uid = (int) request.getSession().getAttribute("uid");
        OrderController orderController = new OrderController();
        // 根据oid找找对应订单，检查订单的发布者是否和uid对应：否则报错！
        Order order = orderController.getOrderByOid(oid);
        if (order == null) return wrapperMsg(403, "所给订单不存在，请重新检查后上传！");
        if (order.getIssuerId() != uid) return wrapperMsg(403, "当前用户不是所给订单的发布方，请重新检查后上传！");

        OrderImage i = new OrderImage();
        // 图片存储路径
        File imagePathFile = new File(Constants.IMAGE_PATH);
        if (!imagePathFile.exists()) {
            imagePathFile.mkdirs();
        }

        if (image == null) return wrapperMsg(403, "请上传文件");
        // 原始文件名
        String originalImageName = image.getOriginalFilename();
        // 获取图片后缀
        assert originalImageName != null;
        String suffix = originalImageName.substring(originalImageName.lastIndexOf("."));
        // 生成图片存储的名称，UUID 避免相同图片名冲突，并加上图片后缀
        String imageName = UUID.randomUUID().toString() + suffix;
        try {
            // 构建真实的文件路径（这里的绝对路径是相当于当前项目的路径而不是“容器”路径）
            File saveFile = new File(imagePathFile.getAbsolutePath() + File.separator + imageName);
            // 将上传的文件保存到服务器文件系统
            image.transferTo(saveFile);
            // 将图片名称记录到数据库中
            i.setOid(oid);
            i.setImagePath(imageName);
            i.setUploadTime(Timestamp.valueOf(LocalDateTime.now()));
            orderImageMapper.addOrderImage(i);

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(image.getOriginalFilename() + "已存储到" + imagePathFile.getAbsolutePath() + File.separator + imageName);
        return wrapperMsg(200, image.getOriginalFilename() + " Uploaded");
    }

    // 获取一个订单oid的所有图片信息
    @RequestMapping(value = "/getImages", method = {RequestMethod.GET})
    public String getOrderImages(@RequestParam(value = "oid")int oid) {
        JSONArray jsonArray = new JSONArray();
        List<OrderImage> images = orderImageMapper.getImagesByOrderId(oid);
        if (images.isEmpty()) return wrapperMsg(403, "所给订单暂无图片！");
        for (OrderImage i : images) {
            jsonArray.add(getJsonOrderImageObject(i));
        }
        return wrapperMsg(200, jsonArray.toJSONString());
    }

    // 下载指定iid的图片
    @RequestMapping(value = "/download/{iid}")
    public void downloadOrderImageByIid(HttpServletResponse response,
                                        @PathVariable int iid) {
        OrderImage image = orderImageMapper.getImageByImageId(iid);
        if (image == null) {
            System.out.println("要求下载的图片（iid: " + iid + "）不存在！");
            return;
        }
        File imageFile = new File(Constants.IMAGE_PATH + File.separator + image.getImagePath());
        responseFile(response, imageFile);
    }

    private static JSONObject getJsonOrderImageObject(OrderImage i) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("iid", i.getIid());
        jsonObject.put("oid", i.getOid());
        jsonObject.put("imagePath", i.getImagePath());
        jsonObject.put("uploadTime", i.getUploadTime());
        return jsonObject;
    }
}
