package com.RapidDataChat.backend.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.RapidDataChat.backend.model.QueryVector;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QueryVectorRepository extends BaseMapper<QueryVector> {
    // 自定义查询方法，根据 databaseInfoId 查询向量
    @Select("SELECT * FROM query_vector WHERE database_info_id = #{databaseInfoId}")
    List<QueryVector> selectByDatabaseInfoId(@Param("databaseInfoId") Integer databaseInfoId);

    // 自定义向量检索方法，根据 QueryVector 对象中的 embeddingVector 和 databaseInfoId 检索
    @Select("<script>" +
            "SELECT * FROM query_vector " +
            "<where> " +
            "<if test='queryVector.databaseInfoId != null'>" +
            "AND database_info_id = #{queryVector.databaseInfoId} " +
            "</if>" +
            "AND (1 - (embedding_vector &lt;=&gt; #{queryVector.embeddingVector})) &gt; 0.8 " +
            "</where> " +
            "ORDER BY embedding_vector &lt;=&gt; #{queryVector.embeddingVector} " +
            "LIMIT #{limit}" +
            "</script>")
    List<QueryVector> searchByVectorAndDatabaseInfoId(@Param("queryVector") QueryVector queryVector, @Param("limit") int limit);
    /**
     * 批量插入向量信息。
     *
     * @param queryVectors 向量信息列表
     * @return 插入的记录数
     */
    @Insert("<script>" +
            "INSERT INTO query_vector (database_info_id, session_id, user_id, query_text, embedding_vector, result_text, success, create_time, update_time) VALUES " +
            "<foreach collection='queryVectors' item='item' index='index' separator=','>" +
            "(#{item.databaseInfoId}, #{item.sessionId}, #{item.userId}, #{item.queryText}, #{item.embeddingVector}, #{item.resultText}, #{item.success}, #{item.createTime}, #{item.updateTime})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("queryVectors") List<QueryVector> queryVectors);

}