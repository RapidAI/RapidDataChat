package com.RapidDataChat.backend.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("user_input_records")  // MyBatis Plus 注解
public class UserInputRecord {

    @TableId(value = "input_id", type = IdType.AUTO)
    private Integer inputId;

    @TableField("user_id")
    private Integer userId;

    @TableField("input_text")
    private String inputText;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}