package com.RapidDataChat.backend.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.RapidDataChat.backend.model.Session;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SessionRepository extends BaseMapper<Session> {

    /**
     * 根据用户ID查找会话。
     * @param userId 用户ID
     * @return 对应用户ID的会话列表
     */
    @Select("SELECT * FROM sessions WHERE user_id = #{userId}")
    List<Session> findByUserId(int userId);

    /**
     * 根据用户ID查找会话并按创建时间倒序排列。
     * @param userId 用户ID
     * @return 按创建时间倒序排列的对应用户ID的会话列表
     */
    @Select("SELECT * FROM sessions WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Session> findByUserIdOrderByCreateTimeDesc(int userId);
}
