package com.RapidDataChat.backend.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("column_info")  // MyBatis Plus 注解
public class ColumnInfo {

    @TableId(value = "column_info_id", type = IdType.AUTO)
    private Integer columnInfoId;

    @TableField("table_info_id")
    private Integer tableInfoId;

    @TableField("database_info_id")
    private Integer databaseInfoId;

    @TableField("column_name")
    private String columnName;

    @TableField("column_comment")
    private String columnComment;

    @TableField("column_description")
    private String columnDescription;

    @TableField("data_type")
    private String dataType;

    @TableField("data_type_comment")
    private String dataTypeComment;

    @TableField("is_current")
    private Boolean isCurrent;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField(exist = false)
    private TableInfo tableInfo;

}
