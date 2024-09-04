package com.RapidDataChat.backend.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.RapidDataChat.backend.model.Query;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QueryRepository extends BaseMapper<Query> {

    /**
     * 根据用户ID查找查询记录并按创建时间倒序排列。
     * @param userId 用户ID
     * @return 按创建时间倒序排列的对应用户ID的查询记录列表
     */
    @Select("SELECT * FROM queries WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Query> findByUserIdOrderByCreateTimeDesc(int userId);

    /**
     * 根据会话ID查找查询记录。
     * @param sessionId 会话ID
     * @return 对应会话ID的查询记录列表
     */
    @Select("SELECT * FROM queries WHERE session_id = #{sessionId}")
    List<Query> findBySessionId(int sessionId);
}
