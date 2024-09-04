package com.RapidDataChat.backend.controller;

import com.RapidDataChat.backend.model.User;
import com.RapidDataChat.backend.service.UserService;
import com.RapidDataChat.backend.util.ApiResponse;
import cn.hutool.jwt.JWT;
import cn.hutool.crypto.digest.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    private static final String JWT_SECRET = "your_secret_key_here";

    @PostMapping("/login")
    public ApiResponse<User> login(@RequestBody User user) {
        User foundUser = userService.findByUsername(user.getUsername());
        if (foundUser == null) {
            // 用户不存在时的提示
            return new ApiResponse<>(null, "用户不存在，请注册", false);
        }
        if (BCrypt.checkpw(user.getPassword(), foundUser.getPasswordHash())) {
            // 正确的 JWT 创建方式
            String token = JWT.create()
                    .setPayload("id", foundUser.getUserId())
                    .setPayload("username", foundUser.getUsername())
                    .setKey(JWT_SECRET.getBytes())
                    .sign();
            foundUser.setToken(token);
            return new ApiResponse<>(foundUser, "登录成功", true);
        }
        return new ApiResponse<>(null, "用户名或密码错误", false);
    }

    @PostMapping("/register")
    public ApiResponse<User> register(@RequestBody User user) {
        // 首先检查用户名是否已存在
        if (userService.findByUsername(user.getUsername()) != null) {
            return new ApiResponse<>(null, "用户名已存在", false);
        }
        // 检查邮箱是否已被注册
        if (userService.findByEmail(user.getEmail()) != null) {
            return new ApiResponse<>(null, "邮箱已被注册，请使用其他邮箱或找回密码", false);
        }
        // 对密码进行哈希处理
        user.setPasswordHash(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        // 保存用户
        User savedUser = userService.save(user);
        return new ApiResponse<>(savedUser, "注册成功", true);
    }

}
