package com.RapidDataChat.backend.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data  // 使用 Lombok 自动生成常见方法
@TableName("model")  // MyBatis Plus 注解，指定表名
public class Model {

    @TableId(value = "model_id", type = IdType.AUTO)
    private int modelId;

    @TableField("model_name")
    private String modelName;

    @TableField("api_key")
    private String apiKey;

    @TableField("use_proxy")
    private boolean useProxy;

    @TableField("proxy_host")
    private String proxyHost;

    @TableField("proxy_port")
    private Integer proxyPort;

    @TableField("base_url")
    private String baseUrl;

    @TableField("model")
    private String model;

    @TableField("description")
    private String description;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField(exist = false)
    private List<Message> messages;
}

