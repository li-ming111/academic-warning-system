package com.academic.service;

import java.util.Map;

public interface AIService {

    /**
     * 获取AI回复
     * @param userId 用户ID
     * @param message 用户消息
     * @return AI回复内容
     */
    Map<String, Object> getResponse(String userId, String message);

    /**
     * 获取AI建议
     * @param userId 用户ID
     * @return 建议列表
     */
    Map<String, Object> getSuggestions(String userId);
}
