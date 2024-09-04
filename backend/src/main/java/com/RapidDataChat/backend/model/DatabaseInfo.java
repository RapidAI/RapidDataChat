package com.RapidDataChat.backend.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("database_info")  // MyBatis Plus 注解
public class DatabaseInfo {

    @TableId(value = "database_info_id", type = IdType.AUTO)
    private Integer databaseInfoId;

    @TableField("database_name")
    private String databaseName;

    @TableField("host")
    private String host;

    @TableField("port")
    private Integer port;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("auth_method")
    private Integer authMethod;

    @TableField("database_type")
    private String databaseType;

    @TableField("database_description")
    private String databaseDescription;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField("sync_time")
    private Date syncTime;

    @TableField("analyze_time")
    private Date analyzeTime;

    @TableField(exist = false) // 方便AI解析同步使用
    private Model model;
}
