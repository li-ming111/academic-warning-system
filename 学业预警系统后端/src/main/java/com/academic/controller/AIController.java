package com.academic.controller;

import com.academic.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AIController {

    @Autowired
    private AIService aiService;

    /**
     * AI聊天接口
     * @param request 请求参数，包含userId和message
     * @return AI回复内容
     */
    @PostMapping("/chat")
    public Map<String, Object> chat(@RequestBody Map<String, Object> request) {
        String userId = request.get("userId").toString();
        String message = request.get("message").toString();
        return aiService.getResponse(userId, message);
    }

    /**
     * 获取AI建议
     * @param userId 用户ID
     * @return 建议列表
     */
    @GetMapping("/suggestions/{userId}")
    public Map<String, Object> getSuggestions(@PathVariable String userId) {
        return aiService.getSuggestions(userId);
    }
}
