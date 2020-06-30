package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.Test;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TestDao {
    // 函数的名称要和对应的Mapper文件中的id完全对应

    // 查找
    List<Test> selectAll();

    // 统计
    int testCnt();

    // 插入，可以指定类为输入的参数
    void insert(Test test);

    // 删除
    void delete(int id);

    // 更新, 可以使用param对参数进行重新命名，则mapper解析按照重新命名以后的参数名进行
    int update(@Param("id")int idff, String content);
}
