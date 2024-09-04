package com.RapidDataChat.backend.controller;

import com.RapidDataChat.backend.model.Session;
import com.RapidDataChat.backend.service.SessionService;
import com.RapidDataChat.backend.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    @Autowired
    private SessionService sessionService;

    /**
     * 获取所有会话。
     * @return ApiResponse，包含所有会话的列表和操作结果
     */
    @PostMapping("/getAll")
    public ApiResponse<List<Session>> getAllSessions() {
        List<Session> sessions = sessionService.findAll();
        return new ApiResponse<>(sessions, "获取所有会话", true);
    }

    /**
     * 根据用户ID获取会话。
     * @param session 用户ID
     * @return ApiResponse，包含对应用户的会话列表和操作结果
     */
    @PostMapping("/getByUserId")
    public ApiResponse<List<Session>> getSessionsByUserId(@RequestBody Session session) {
        List<Session> sessions = sessionService.getSessionsByUserId(session.getUserId());
        if (sessions.isEmpty()) {
            return new ApiResponse<>(null, "未找到用户会话", true);
        }
        return new ApiResponse<>(sessions, "获取用户会话", true);
    }


    /**
     * 添加新的会话。
     * @param session 要添加的会话信息
     * @return ApiResponse，包含被添加的会话信息和操作结果
     */
    @PostMapping("/add")
    public ApiResponse<Session> addSession(@RequestBody Session session) {
        Session savedSession = sessionService.save(session);
        return new ApiResponse<>(savedSession, "会话已添加", true);
    }

    /**
     * 更新现有的会话信息。
     * @param session 包含会话ID和更新后的会话信息的对象
     * @return ApiResponse，包含更新后的会话信息和操作结果
     */
    @PostMapping("/update")
    public ApiResponse<Session> updateSession(@RequestBody Session session) {
        boolean isUpdated = sessionService.update(session, session.getSessionId());
        if (!isUpdated) {
            return new ApiResponse<>(null, "未找到要更新的会话", true);
        }
        return new ApiResponse<>(session, "会话已更新", true);
    }

    /**
     * 删除会话。
     * @param session 要删除的会话ID
     * @return ApiResponse，包含删除操作的结果消息
     */
    @PostMapping("/delete")
    public ApiResponse<String> deleteSession(@RequestBody  Session session) {
        boolean isDeleted = sessionService.delete(session.getSessionId());
        if (!isDeleted) {
            return new ApiResponse<>("", "删除会话时出错，会话ID：" + session.getSessionId(), false);
        }
        return new ApiResponse<>("会话已成功删除", "会话已删除", true);
    }
}
