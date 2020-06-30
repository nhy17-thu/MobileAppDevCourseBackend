package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserDao {
	// 函数的名称要和对应的Mapper文件中的id完全对应

	// 查找
	List<User> selectAll();

	// 统计
	int userCnt();

	// 用户注册
	void register(User user);

	// 用户删除：还需要删除所有相关订单
	// void deleteUser(String name);

	// 取得用户信息，用于登陆验证等场景
	User getUserByName(String name);
	User getUserByID(int uid);
	User getUserByEmail(String email);
	User getUserByCode(String code);    // 查找指定验证码对应的用户

	// 将对象中的内容更新入库（对象中有值时更新字段，没有值的属性不修改）
	void updateUserSelectiveByID(User user);

}
