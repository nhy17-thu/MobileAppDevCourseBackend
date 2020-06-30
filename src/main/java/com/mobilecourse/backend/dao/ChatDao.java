package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.Chat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatDao {
	// 函数的名称要和对应的Mapper文件中的id完全对应

	// 查找和给定uid相关（可能是发送方/接收方）的聊天记录，按时间倒序排列
	List<Chat> getSortedChatRecordsById(int uid);

	// 根据发送方id查找所有聊天记录
	List<Chat> getChatRecordsBySenderId(int senderId);

	// 根据接收方id查找所有聊天记录
	List<Chat> getChatRecordsByReceiverId(int receiverId);

	// 根据发送方和接收方id查找所有聊天记录
	List<Chat> getChatRecordsBySenderAndReceiverId(int senderId, int receiverId);

	// 向数据库添加一条聊天记录
	void addChatRecord(Chat chat);
}
