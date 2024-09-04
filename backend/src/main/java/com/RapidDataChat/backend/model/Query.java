package com.RapidDataChat.backend.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("queries")  // MyBatis Plus 注解
public class Query {

    @TableId(value = "query_id", type = IdType.AUTO)
    private Integer queryId;

    @TableField("database_info_id")
    private Integer databaseInfoId;

    @TableField("session_id")
    private Integer sessionId;

    @TableField("user_id")
    private Integer userId;

    @TableField("query_text")
    private String queryText;

    @TableField("sql_text")
    private String sqlText;

    @TableField("response_text")
    private String responseText;

    @TableField("execution_time")
    private Float executionTime;

    @TableField("success")
    private Boolean success;

    @TableField("executed_at")
    private Date executedAt;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
