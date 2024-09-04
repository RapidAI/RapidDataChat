package com.RapidDataChat.backend.controller;

import com.RapidDataChat.backend.model.UserInputRecord;
import com.RapidDataChat.backend.service.UserInputRecordService;
import com.RapidDataChat.backend.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/userInputRecords")
public class UserInputRecordController {

    @Autowired
    private UserInputRecordService userInputRecordService;

    /**
     * 获取所有用户输入记录。
     * @return ApiResponse 包含所有用户输入记录列表和操作结果
     */
    @GetMapping("/getAll")
    public ApiResponse<List<UserInputRecord>> getAllUserInputRecords() {
        List<UserInputRecord> records = userInputRecordService.findAll();
        return new ApiResponse<>(records, "获取所有用户输入记录", true);
    }

    /**
     * 根据用户ID获取用户的所有输入记录。
     * @param record 用户ID
     * @return ApiResponse 包含用户的所有输入记录和操作结果
     */
    @PostMapping("/getByUserId")
    public ApiResponse<List<UserInputRecord>> getUserInputRecordsByUserId(@RequestBody UserInputRecord record) {
        List<UserInputRecord> records = userInputRecordService.findByUserId(record.getUserId());
        return new ApiResponse<>(records, "获取用户的所有输入记录", true);
    }

    /**
     * 添加新的用户输入记录。
     * @param record 要添加的用户输入记录
     * @return ApiResponse 包含被添加的用户输入记录和操作结果
     */
    @PostMapping("/add")
    public ApiResponse<UserInputRecord> addUserInputRecord(@RequestBody UserInputRecord record) {
        UserInputRecord savedRecord = userInputRecordService.save(record);
        return new ApiResponse<>(savedRecord, "用户输入记录已添加", true);
    }

    /**
     * 更新现有用户输入记录。
     * @param record 包含输入ID和更新后的用户输入记录的对象
     * @return ApiResponse 包含更新后的用户输入记录和操作结果
     */
    @PostMapping("/update")
    public ApiResponse<UserInputRecord> updateUserInputRecord(@RequestBody UserInputRecord record) {
        UserInputRecord updatedRecord = userInputRecordService.update(record, record.getInputId());
        if (updatedRecord == null) {
            return new ApiResponse<>(null, "未找到用户输入记录", true);
        }
        return new ApiResponse<>(updatedRecord, "用户输入记录已更新", true);
    }

    /**
     * 删除用户输入记录。
     * @param inputId 输入ID
     * @return ApiResponse 包含删除操作的结果消息
     */
    @PostMapping("/delete/{inputId}")
    public ApiResponse<String> deleteUserInputRecord(@PathVariable int inputId) {
        boolean isDeleted = userInputRecordService.delete(inputId);
        if (!isDeleted) {
            return new ApiResponse<>("", "删除用户输入记录时出错，输入ID：" + inputId, false);
        }
        return new ApiResponse<>("用户输入记录已成功删除", "用户输入记录已删除", true);
    }
}