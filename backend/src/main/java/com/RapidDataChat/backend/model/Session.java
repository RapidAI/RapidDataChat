package com.RapidDataChat.backend.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("sessions")  // MyBatis Plus 注解
public class Session {

    @TableId(value = "session_id", type = IdType.AUTO)
    private Integer sessionId;

    @TableField("database_info_id")
    private Integer databaseInfoId;

    @TableField("database_info")
    private String databaseInfo;

    @TableField("database_info_cheked")
    private String databaseInfoCheked;

    @TableField("user_id")
    private Integer userId;

    @TableField("model_id")
    private Integer modelId;

    @TableField("title")
    private String title;

    @TableField("messages")
    private String messages;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
