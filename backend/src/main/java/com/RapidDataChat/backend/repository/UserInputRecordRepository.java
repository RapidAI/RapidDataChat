package com.RapidDataChat.backend.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.RapidDataChat.backend.model.UserInputRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserInputRecordRepository extends BaseMapper<UserInputRecord> {

    /**
     * 根据用户ID查找用户的所有输入记录，并按创建时间倒序排列。
     * @param userId 用户ID
     * @return 按创建时间倒序排列的用户的所有输入记录列表
     */
    @Select("SELECT * FROM user_input_records WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<UserInputRecord> findByUserId(int userId);
}