package com.RapidDataChat.backend.controller;

import com.RapidDataChat.backend.model.Query;
import com.RapidDataChat.backend.service.QueryService;
import com.RapidDataChat.backend.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/queries")
public class QueryController {

    @Autowired
    private QueryService queryService;

    /**
     * 执行查询记录中的SQL语句。
     * @param query 包含要执行的SQL语句的查询记录
     * @return ApiResponse，包含执行结果和操作结果
     */
    @PostMapping("/execute")
    public ApiResponse<Query> executeQuery(@RequestBody Query query) {
        return queryService.executeQuery(query);
    }

    /**
     * 获取所有查询记录。
     * @return ApiResponse，包含所有查询记录列表和操作结果
     */
    @PostMapping("/getAll")
    public ApiResponse<List<Query>> getAllQueries() {
        List<Query> queries = queryService.findAll();
        return new ApiResponse<>(queries, "获取所有查询记录", true);
    }

    /**
     * 根据用户ID获取会话。
     * @param query 用户ID
     * @return ApiResponse，包含对应用户的查询
     */
    @PostMapping("/getByUserId")
    public ApiResponse<List<Query>> getQuerysByUserId(@RequestBody Query query) {
        List<Query> querys = queryService.getQuerysByUserId(query.getUserId());
        if (querys.isEmpty()) {
            return new ApiResponse<>(null, "未找到用户查询", true);
        }
        return new ApiResponse<>(querys, "获取用户查询", true);
    }


    /**
     * 根据ID获取单个查询记录。
     * @param query 查询记录的ID
     * @return ApiResponse，包含特定查询记录和操作结果
     */
    @PostMapping("/getById")
    public ApiResponse<Query> getQueryById(@RequestBody Query query) {
        Query newquery = queryService.findById(query.getQueryId());
        if (newquery == null) {
            return new ApiResponse<>(null, "未找到查询记录", true);
        }
        return new ApiResponse<>(newquery, "获取查询记录", true);
    }

    /**
     * 添加新的查询记录。
     * @param query 要添加的查询记录信息
     * @return ApiResponse，包含被添加的查询记录和操作结果
     */
    @PostMapping("/add")
    public ApiResponse<Query> addQuery(@RequestBody Query query) {
        Query savedQuery = queryService.save(query);
        return new ApiResponse<>(savedQuery, "查询记录已添加", true);
    }

    /**
     * 更新现有的查询记录。
     * @param query 包含查询记录ID和更新后的查询记录信息的对象
     * @return ApiResponse，包含更新后的查询记录和操作结果
     */
    @PostMapping("/update")
    public ApiResponse<Query> updateQuery(@RequestBody Query query) {
        boolean isUpdated = queryService.update(query, query.getQueryId());
        if (!isUpdated) {
            return new ApiResponse<>(null, "未找到要更新的查询记录", true);
        }
        return new ApiResponse<>(query, "查询记录已更新", true);
    }

    /**
     * 删除查询记录。
     * @param query 要删除的查询记录ID
     * @return ApiResponse，包含删除操作的结果消息
     */
    @PostMapping("/delete")
    public ApiResponse<String> deleteQuery(@RequestBody Query query) {
        boolean isDeleted = queryService.delete(query.getQueryId());
        if (!isDeleted) {
            return new ApiResponse<>("", "删除查询记录时出错，ID：" + query.getQueryId(), false);
        }
        return new ApiResponse<>("查询记录已成功删除", "查询记录已删除", true);
    }
}