package com.RapidDataChat.backend.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data  // 使用 Lombok 自动生成常见方法
@TableName("charts")  // MyBatis Plus 注解
public class Chart {

    @TableId(value = "chart_id", type = IdType.AUTO)
    private Integer chartId; // 图表ID，自增长主键

    @TableField("user_id")
    private Integer userId; // 用户ID，标识这个图表配置属于哪个用户

    @TableField("query_id")
    private Integer queryId; // 查询ID，标识这个图表配置是基于哪个查询生成的

    @TableField("chart_config")
    private String chartConfig; // 图表配置信息，以 JSON 或其他格式存储详细的图表配置参数

    @TableField(fill = FieldFill.INSERT)
    private Date createTime; // 创建时间，记录图表配置的创建时间点

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime; // 更新时间，记录图表配置的最后修改时间点

    @TableField(exist = false)
    private String extraField; // 非数据库字段
}

