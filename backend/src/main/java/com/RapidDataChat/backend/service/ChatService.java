package com.RapidDataChat.backend.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.RapidDataChat.backend.model.Message;
import com.RapidDataChat.backend.model.Model;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ChatService {

    private final OkHttpClient client = new OkHttpClient();
    private final Map<String, SseEmitter> userEmitters = new ConcurrentHashMap<>();

    // 创建聊天流
    public SseEmitter createChatStreambyModel(String sessionId, Model model) {
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        userEmitters.put(sessionId, sseEmitter);

        try {
            validateModel(model);
            Request request = buildRequest(model, true);

            EventSources.createFactory(client).newEventSource(request, new SimpleEventSourceListener(sseEmitter));
        } catch (Exception e) {
            handleException(sseEmitter, sessionId, e);
        }

        return sseEmitter;
    }

    // 停止聊天流
    public void stopChatStream(String sessionId) {
        SseEmitter sseEmitter = userEmitters.remove(sessionId);
        if (sseEmitter != null) {
            sseEmitter.complete();
        }
    }

    // 创建单次聊天
    public Message createChatbyModel(Model model) {
        validateModel(model);

        try {
            String response = HttpRequest.post(combineUrl(model.getBaseUrl(), "v1/chat/completions"))
                    .header("Authorization", "Bearer " + model.getApiKey())
                    .header("Content-Type", "application/json")
                    .body(JSONUtil.toJsonStr(buildRequestBody(model, false)))
                    .execute().body();

            return parseResponse(response);

        } catch (Exception e) {
            throw new RuntimeException("创建聊天时发生错误: " + e.getMessage(), e);
        }
    }

    // 构建请求
    private Request buildRequest(Model model, boolean stream) {
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                JSONUtil.toJsonStr(buildRequestBody(model, stream))
        );

        return new Request.Builder()
                .url(combineUrl(model.getBaseUrl(), "v1/chat/completions"))
                .header("Authorization", "Bearer " + model.getApiKey())
                .post(body)
                .build();
    }

    // 构建请求体
    private Map<String, Object> buildRequestBody(Model model, boolean stream) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model.getModel());
        requestBody.put("messages", model.getMessages());
        requestBody.put("stream", stream);
        return requestBody;
    }

    // 合并URL
    private String combineUrl(String baseUrl, String endpoint) {
        return baseUrl.endsWith("/") ? baseUrl + endpoint : baseUrl + "/" + endpoint;
    }

    // 验证模型
    private void validateModel(Model model) {
        if (model.getApiKey() == null || model.getBaseUrl() == null || model.getModel() == null) {
            throw new IllegalArgumentException("必须提供 API 密钥、基础 URL 和模型");
        }
    }

    // 解析响应
    private Message parseResponse(String response) {
        Map<String, Object> responseMap = JSONUtil.toBean(response, Map.class);

        // 处理错误响应
        if (responseMap.containsKey("error")) {
            Map<String, Object> errorMap = (Map<String, Object>) responseMap.get("error");
            String errorMessage = (String) errorMap.get("message");
            throw new RuntimeException("API返回错误: " + errorMessage);
        }

        Map<String, Object> messageMap = (Map<String, Object>) ((List<Map<String, Object>>) responseMap.get("choices")).get(0).get("message");

        return Message.builder()
                .role((String) messageMap.get("role"))
                .content((String) messageMap.get("content"))
                .build();
    }

    // 处理异常
    private void handleException(SseEmitter sseEmitter, String sessionId, Exception e) {
        try {
            sseEmitter.send(new Message("assistant", "发生错误" + e.getMessage()));
        } catch (IOException ioException) {
            log.error("Error handling exception", ioException);
        } finally {
            sseEmitter.complete();
            userEmitters.remove(sessionId);
        }
    }

    // 内部类: 简单的 EventSourceListener 实现
    private class SimpleEventSourceListener extends EventSourceListener {
        private final SseEmitter sseEmitter;

        SimpleEventSourceListener(SseEmitter sseEmitter) {
            this.sseEmitter = sseEmitter;
        }

        @Override
        public void onOpen(EventSource eventSource, Response response) {
            // 连接打开事件处理
        }

        @Override
        public void onEvent(EventSource eventSource, String id, String type, String data) {
            if ("[DONE]".equals(data)) {
                completeSseEmitter(sseEmitter);
                return;
            }

            try {
                Map<String, Object> responseMap = JSONUtil.toBean(data, Map.class);
                Map<String, Object> delta = (Map<String, Object>) ((List<Map<String, Object>>) responseMap.get("choices")).get(0).get("delta");

                String role = (String) delta.get("role");
                String content = (String) delta.get("content");

                if (content != null) {
                    sseEmitter.send(new Message(role, content));
                }
            } catch (IOException e) {
                log.error("Error sending message", e);
            }
        }

        @Override
        public void onClosed(EventSource eventSource) {
            completeSseEmitter(sseEmitter);
        }

        @Override
        @SneakyThrows
        public void onFailure(EventSource eventSource, Throwable t, Response response) {
            try {
                if (response != null && response.body() != null) {
                    sseEmitter.send(new Message("assistant", response.body().string()));
                }
            } catch (Exception e) {
                log.error("Error sending failure message", e);
                sseEmitter.send(new Message("assistant", "出错了请重新尝试"));
            }
            eventSource.cancel();
            completeSseEmitter(sseEmitter);
        }

        private void completeSseEmitter(SseEmitter sseEmitter) {
            if (sseEmitter != null) {
                try {
                    sseEmitter.complete();
                } catch (Exception e) {
                    log.error("Error completing SSE emitter", e);
                }
            }
        }
    }
}