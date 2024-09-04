package com.RapidDataChat.backend.model;

import com.baomidou.mybatisplus.annotation.*;
import com.pgvector.PGvector;
import lombok.Data;

import java.util.Date;

@Data
@TableName("query_vector")  // MyBatis Plus 注解，指定数据库表名
public class QueryVector {

    @TableId(value = "query_vector_id", type = IdType.AUTO)
    private Integer queryVectorId;  // 向量ID，唯一标识每个向量

    @TableField("database_info_id")
    private Integer databaseInfoId;  // 关联的数据库信息ID，引用数据库信息表的ID

    @TableField("session_id")
    private Integer sessionId;  // 会话ID，引用会话表的ID

    @TableField("user_id")
    private Integer userId;  // 用户ID，引用用户表的ID

    @TableField("query_text")
    private String queryText;  // 自然语言查询文本，用户输入的查询请求文本

    @TableField("embedding_vector")
    private PGvector embeddingVector;  // 用来检索的 query_text 向量

    @TableField("result_text")
    private String resultText;  // 生成的SQL语句，系统根据自然语言查询生成的SQL代码

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;  // 创建时间，记录查询的创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;  // 更新时间，查询记录的最后更新时间

    @TableField("success")
    private Boolean success;  // 执行结果，指示查询是否成功
}
