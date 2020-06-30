package com.mobilecourse.backend.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.dao.ChatDao;
import com.mobilecourse.backend.model.Chat;
import com.mobilecourse.backend.WebSocketServer;
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
@RequestMapping("/chat")
public class ChatController extends CommonController {
	@Autowired
	private ChatDao chatMapper;

	// 用来给普通类调用mapper
	public static ChatController chatController;

	@PostConstruct
	public void init() {
		chatController = this;
		// 用来给普通类调用mapper
		chatController.chatMapper = this.chatMapper;
	}

	// 带路径参数的GET请求，查询所有和当前session uid相关的聊天记录
	@RequestMapping(value = "/records", method = {RequestMethod.GET})
	public String getUserChat(HttpServletRequest request) {
		try {
			// 获取当前session的uid
			int uid = (int) request.getSession().getAttribute("uid");

			JSONArray jsonArray = new JSONArray();
			List<Chat> list = chatController.chatMapper.getSortedChatRecordsById(uid);
			if (list.size() == 0)
				return wrapperMsg(201, "没有找到对应的数据！");
			for (Chat c : list) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("senderId", c.getSenderId());
				jsonObject.put("receiverId", c.getReceiverId());
				jsonObject.put("content", c.getContent());
				jsonObject.put("sendTime", c.getSendTime());
				jsonArray.add(jsonObject);
			}
			return wrapperMsg(200, jsonArray.toJSONString());
		} catch (Exception e) {
			e.printStackTrace();
			return wrapperMsg(500, e.toString());
		}
	}

	// 向数据库中添加聊天记录
	public void addChatMessage(int senderId, int receiverId, String content) {
		try {
			Chat c = new Chat();
			c.setSenderId(senderId);
			c.setReceiverId(receiverId);
			c.setContent(content);
			c.setSendTime(Timestamp.valueOf(LocalDateTime.now()));
			System.out.println(c.toString());

			chatController.chatMapper.addChatRecord(c);
		} catch (Exception e) {
			System.out.println("Failed adding chat message to database!\n");
			e.printStackTrace();
		}
	}

}
