package com.RapidDataChat.backend.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import java.util.Date;

@Data
@TableName("table_info")  // MyBatis Plus 注解
public class TableInfo {

    @TableId(value = "table_info_id", type = IdType.AUTO)
    private Integer tableInfoId;

    @TableField("database_info_id")
    private Integer databaseInfoId;

    @TableField("table_name")
    private String tableName;

    @TableField("table_comment")
    private String tableComment;

    @TableField("table_description")
    private String tableDescription;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField(exist = false)
    private DatabaseInfo databaseInfo;  // 加载关联的 DatabaseInfo 信息
}

