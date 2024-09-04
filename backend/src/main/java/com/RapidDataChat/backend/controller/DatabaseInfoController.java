package com.RapidDataChat.backend.controller;

import com.RapidDataChat.backend.model.DatabaseInfo;
import com.RapidDataChat.backend.service.DatabaseInfoService;
import com.RapidDataChat.backend.service.TableInfoService;
import com.RapidDataChat.backend.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/databases")
public class DatabaseInfoController {

    @Autowired
    private DatabaseInfoService databaseInfoService;

    @Autowired
    private TableInfoService tableInfoService;

    @PostMapping("/getAll")
    public ApiResponse<List<DatabaseInfo>> getAllDatabaseInfos() {
        List<DatabaseInfo> databases = databaseInfoService.findAll();
        return new ApiResponse<>(databases, "获取所有数据库连接信息", true);
    }

    @PostMapping("/getById")
    public ApiResponse<DatabaseInfo> getDatabaseInfoById(@RequestBody DatabaseInfo databaseInfo) {
        DatabaseInfo newDatabaseInfo = databaseInfoService.findById(databaseInfo.getDatabaseInfoId());
        if (newDatabaseInfo == null) {
            return new ApiResponse<>(null, "未找到指定的数据库信息", true);
        }
        return new ApiResponse<>(newDatabaseInfo, "获取数据库连接信息", true);
    }

    @PostMapping("/add")
    public ApiResponse<DatabaseInfo> addDatabaseInfo(@RequestBody DatabaseInfo databaseInfo) {
        String validationError = databaseInfoService.validateDatabaseConnection(databaseInfo);
        if (validationError != null) {
            return new ApiResponse<>(null, "无法连接到数据库：" + databaseInfo.getHost() + "，错误信息：" + validationError, false);
        }
        DatabaseInfo savedDatabaseInfo = databaseInfoService.save(databaseInfo);
        return new ApiResponse<>(savedDatabaseInfo, "数据库连接信息已添加", true);
    }


    @PostMapping("/update")
    public ApiResponse<DatabaseInfo> updateDatabaseInfo(@RequestBody DatabaseInfo databaseInfo) {
        String validationError = databaseInfoService.validateDatabaseConnection(databaseInfo);
        if (validationError != null) {
            return new ApiResponse<>(null, "无法连接到数据库：" + databaseInfo.getHost() + "，错误信息：" + validationError, false);
        }

        boolean isUpdated = databaseInfoService.update(databaseInfo, databaseInfo.getDatabaseInfoId());
        if (!isUpdated) {
            return new ApiResponse<>(null, "未找到要更新的数据库信息", true);
        }
        return new ApiResponse<>(databaseInfo, "数据库连接信息已更新", true);
    }

    @PostMapping("/delete")
    public ApiResponse<String> deleteDatabaseInfo(@RequestBody DatabaseInfo databaseInfo) {
        boolean isDeleted = databaseInfoService.delete(databaseInfo.getDatabaseInfoId());
        if (!isDeleted) {
            return new ApiResponse<>("", "删除数据库信息时出错，ID：" + databaseInfo.getDatabaseInfoId(), false);
        }
        return new ApiResponse<>("数据库信息已成功删除", "数据库信息已删除", true);
    }

    @PostMapping("/sync")
    public ApiResponse<String> syncDatabaseInfo(@RequestBody DatabaseInfo databaseInfo) {
        boolean isSynced = databaseInfoService.sync(databaseInfo);
        if (!isSynced) {
            return new ApiResponse<>(null, "同步数据库信息时出错，ID：" + databaseInfo.getDatabaseInfoId(), false);
        }
        return new ApiResponse<>("数据库信息已成功同步", "数据库信息已同步", true);
    }

}