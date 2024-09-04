package com.RapidDataChat.backend.controller;

import com.RapidDataChat.backend.model.Model;
import com.RapidDataChat.backend.service.ChatService;
import com.RapidDataChat.backend.util.ApiResponse;
import com.RapidDataChat.backend.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


/**
 * 聊天控制器，用于处理与 AI 聊天客户端的交互
 */
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;



    @PostMapping(path = "/stopStream")
    public ApiResponse<String> stopStream(@RequestParam String sessionId) {
        chatService.stopChatStream(sessionId);
        return new ApiResponse<>("流式请求已终止", "操作成功", true);
    }


    @PostMapping(path = "/generateStreambyModel", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChatbyModel(@RequestParam String sessionId, @RequestBody Model model) {
        return chatService.createChatStreambyModel(sessionId, model);
    }

    @PostMapping(path = "/generatebyModel")
    public ApiResponse<Message> ChatbyModel(@RequestBody Model model) {
        Message responseMessage = chatService.createChatbyModel(model);
        if (responseMessage == null) {
            return new ApiResponse<>(null, "生成回复失败", false);
        }
        return new ApiResponse<>(responseMessage, "生成回复成功", true);
    }
}
