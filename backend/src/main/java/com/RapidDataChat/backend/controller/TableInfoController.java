package com.RapidDataChat.backend.controller;

import com.RapidDataChat.backend.model.TableInfo;
import com.RapidDataChat.backend.service.TableInfoService;
import com.RapidDataChat.backend.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tables") // RESTful API 推荐使用复数形式
public class TableInfoController {

    @Autowired
    private TableInfoService tableInfoService;

    /**
     * 根据ID获取单个表信息。
     *
     * @param tableInfo 表信息ID
     * @return ApiResponse 包含单个表信息和操作结果
     */
    @PostMapping("/getById")
    public ApiResponse<TableInfo> getTableById(@RequestBody TableInfo tableInfo) {
        TableInfo newtableInfo = tableInfoService.findById(tableInfo.getTableInfoId());
        if (newtableInfo == null) {
            return new ApiResponse<>(null, "未找到表信息", true);
        }
        return new ApiResponse<>(newtableInfo, "获取表信息", true);
    }


    /**
     * 更新现有表信息。
     *
     * @param tableInfo 包含要更新的表信息ID和表信息对象
     * @return ApiResponse 包含更新后的表信息和操作结果
     */
    @PostMapping("/update")
    public ApiResponse<TableInfo> updateTableInfo(@RequestBody TableInfo tableInfo) {
        boolean isUpdated = tableInfoService.update(tableInfo, tableInfo.getTableInfoId());
        if (!isUpdated) {
            return new ApiResponse<>(null, "未找到要更新的表信息", true);
        }
        return new ApiResponse<>(tableInfo, "表信息已更新", true);
    }

    /**
     * 删除表信息。
     *
     * @param tableInfo 要删除的表信息ID
     * @return ApiResponse 包含删除操作的结果消息
     */
    @PostMapping("/delete")
    public ApiResponse<String> deleteTableInfo(@RequestBody TableInfo tableInfo) {
        boolean isDeleted = tableInfoService.delete(tableInfo.getTableInfoId());
        if (!isDeleted) {
            return new ApiResponse<>("", "删除表信息时出错，ID：" + tableInfo.getTableInfoId(), false);
        }
        return new ApiResponse<>("表信息已成功删除", "表信息已删除", true);
    }

    /**
     * 存储所有表信息到table_info表
     */
    @PostMapping("/saveAll")
    public ResponseEntity<ApiResponse<Void>> saveTableInfo(@RequestBody List<TableInfo> tableInfo) {
        try {
            tableInfoService.saveAll(tableInfo);
            return ResponseEntity.ok().body(new ApiResponse<>(null, "表信息已添加", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(null, "保存表信息时发生错误：" + e.getMessage(), false));
        }
    }

    /**
     * 根据 databaseInfoId 获取所有表信息。
     *
     * @param tableInfo 包含 databaseInfoId 的请求体
     * @return ApiResponse 包含所有表信息列表和操作结果
     */
    @PostMapping("/getAllByDatabaseId")
    public ApiResponse<List<TableInfo>> getAllByDatabaseId(@RequestBody TableInfo tableInfo) {
        List<TableInfo> tableInfos = tableInfoService.findByDatabaseInfoId(tableInfo.getDatabaseInfoId());
        return new ApiResponse<>(tableInfos, "获取所有表信息", true);
    }
}