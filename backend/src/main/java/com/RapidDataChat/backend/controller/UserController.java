package com.RapidDataChat.backend.controller;

import com.RapidDataChat.backend.model.User;
import com.RapidDataChat.backend.service.UserService;
import com.RapidDataChat.backend.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users") // RESTful API 的标准实践是使用复数形式
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取所有用户信息。
     * @return ApiResponse 包含所有用户信息列表和操作结果
     */
    @PostMapping("/getAll")
    public ApiResponse<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return new ApiResponse<>(users, "获取所有用户信息", true);
    }

    /**
     * 根据用户ID获取单个用户信息。
     * @param user 用户ID
     * @return ApiResponse 包含单个用户信息和操作结果
     */
    @PostMapping("/getById")
    public ApiResponse<User> getUserById(@RequestBody User user) {
        User newuser = userService.findById(user.getUserId());
        if (newuser == null) {
            return new ApiResponse<>(null, "未找到用户信息", true);
        }
        return new ApiResponse<>(newuser, "获取用户信息", true);
    }

    /**
     * 添加新的用户。
     * @param user 要添加的用户信息
     * @return ApiResponse 包含被添加的用户信息和操作结果
     */
    @PostMapping("/add")
    public ApiResponse<User> addUser(@RequestBody User user) {
        User savedUser = userService.save(user);
        return new ApiResponse<>(savedUser, "用户已添加", true);
    }

    /**
     * 更新现有用户信息。
     * @param user 包含用户ID和更新后的用户信息的对象
     * @return ApiResponse 包含更新后的用户信息和操作结果
     */
    @PostMapping("/update")
    public ApiResponse<User> updateUser(@RequestBody User user) {
        User updatedUser = userService.update(user, user.getUserId());
        if (updatedUser == null) {
            return new ApiResponse<>(null, "未找到用户信息", true);
        }
        return new ApiResponse<>(updatedUser, "用户信息已更新", true);
    }

    /**
     * 删除用户。
     * @param user 用户ID
     * @return ApiResponse 包含删除操作的结果消息
     */
    @PostMapping("/delete")
    public ApiResponse<String> deleteUser(@RequestBody User user) {
        boolean isDeleted = userService.delete(user.getUserId());
        if (!isDeleted) {
            return new ApiResponse<>("", "删除用户信息时出错，用户ID：" + user.getUserId(), false);
        }
        return new ApiResponse<>("用户已成功删除", "用户已删除", true);
    }
}
