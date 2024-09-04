package com.RapidDataChat.backend.controller;

import com.RapidDataChat.backend.model.ColumnInfo;
import com.RapidDataChat.backend.service.ColumnInfoService;
import com.RapidDataChat.backend.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/columns") // 使用复数形式的RESTful URL是一种好习惯
public class ColumnInfoController {

    @Autowired
    private ColumnInfoService columnInfoService;

    /**
     * 获取所有列信息，并包括关联的表信息。
     * @return ApiResponse，包含所有列信息列表和操作结果
     */
    @PostMapping("/getAll")
    public ApiResponse<List<ColumnInfo>> getAllColumns() {
        List<ColumnInfo> columns = columnInfoService.getAllColumnInfoWithTableInfo();
        return new ApiResponse<>(columns, "获取所有列信息和关联的表信息", true);
    }

    /**
     * 根据数据库ID获取所有列信息，并包括关联的表信息。
     * @param columnInfo 数据库ID
     * @return ApiResponse，包含所有列信息列表和操作结果
     */
    @PostMapping("/getAllByDatabaseInfoId")
    public ApiResponse<List<ColumnInfo>> getAllByDatabaseInfoId(@RequestBody ColumnInfo columnInfo) {
        List<ColumnInfo> columns = columnInfoService.getAllByDatabaseInfoId(columnInfo.getDatabaseInfoId());
        return new ApiResponse<>(columns, "获取指定数据库的所有列信息和关联的表信息", true);
    }

    /**
     * 获取指定表的所有列信息。
     * @param columnInfo 要查询的表信息ID包含在columnInfo对象中
     * @return ApiResponse，包含指定表的列信息列表和操作结果
     */
    @PostMapping("/getAllByTableId")
    public ApiResponse<List<ColumnInfo>> getAllColumnsByTableId(@RequestBody ColumnInfo columnInfo) {
        List<ColumnInfo> columns = columnInfoService.findByTableInfoId(columnInfo.getTableInfoId());
        return new ApiResponse<>(columns, "获取指定表的所有列信息", true);
    }

    /**
     * 根据ID获取列信息。
     * @param columnInfo 要查询的列的ID
     * @return ApiResponse，包含特定列信息和操作结果
     */
    @PostMapping("/getById")
    public ApiResponse<ColumnInfo> getColumnById(@RequestBody ColumnInfo columnInfo) {
        ColumnInfo newColumnInfo = columnInfoService.findById(columnInfo.getColumnInfoId());
        if (newColumnInfo == null) {
            return new ApiResponse<>(null, "未找到指定的列信息", true);
        }
        return new ApiResponse<>(newColumnInfo, "获取列信息", true);
    }

    /**
     * 添加新的列信息。
     * @param columnInfo 要添加的列信息对象
     * @return ApiResponse，包含被添加的列信息和操作结果
     */
    @PostMapping("/add")
    public ApiResponse<ColumnInfo> addColumnInfo(@RequestBody ColumnInfo columnInfo) {
        ColumnInfo savedColumnInfo = columnInfoService.save(columnInfo);
        return new ApiResponse<>(savedColumnInfo, "列信息已添加", true);
    }

    /**
     * 更新现有的列信息。
     * @param columnInfo 更新后的列信息对象，包含要更新的列的ID
     * @return ApiResponse，包含更新后的列信息和操作结果
     */
    @PostMapping("/update")
    public ApiResponse<ColumnInfo> updateColumnInfo(@RequestBody ColumnInfo columnInfo) {
        boolean isUpdated = columnInfoService.update(columnInfo, columnInfo.getColumnInfoId());
        if (!isUpdated) {
            return new ApiResponse<>(null, "未找到要更新的列信息", true);
        }
        return new ApiResponse<>(columnInfo, "列信息已更新", true);
    }

    /**
     * 删除指定ID的列信息。
     * @param columnInfo 要删除的列的ID
     * @return ApiResponse，包含删除操作的结果消息
     */
    @PostMapping("/delete")
    public ApiResponse<String> deleteColumnInfo(@RequestBody ColumnInfo columnInfo) {
        boolean isDeleted = columnInfoService.delete(columnInfo.getColumnInfoId());
        if (!isDeleted) {
            return new ApiResponse<>("", "删除列信息时出错，ID：" + columnInfo.getColumnInfoId(), false);
        }
        return new ApiResponse<>("列信息已成功删除", "列信息已删除", true);
    }
}