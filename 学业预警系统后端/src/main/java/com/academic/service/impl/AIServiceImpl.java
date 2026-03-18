package com.academic.service.impl;

import com.academic.service.AIService;
import com.academic.service.StudentService;
import com.academic.service.ScoreService;
import com.academic.service.WarningService;
import com.academic.service.AssistancePlanService;
import com.academic.entity.StudentProfile;
import com.academic.entity.Score;
import com.academic.entity.AcademicWarning;
import com.academic.entity.AssistancePlan;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AIServiceImpl implements AIService {

    @Autowired
    private StudentService studentService;

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private WarningService warningService;

    @Autowired
    private AssistancePlanService assistancePlanService;

    // 智谱API配置
    private static final String ZHIPU_API_KEY = "8ce12362b1254ca69d90ca1ba02d2493.MPKnQNiL3vBMgvfD";
    private static final String ZHIPU_API_URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> getResponse(String userId, String message) {
        Map<String, Object> response = new HashMap<>();
        String content = generateResponse(userId, message);
        response.put("content", content);
        return response;
    }

    @Override
    public Map<String, Object> getSuggestions(String userId) {
        Map<String, Object> response = new HashMap<>();
        List<String> suggestions = new ArrayList<>();
        suggestions.add("我的GPA是多少？");
        suggestions.add("我有哪些预警？");
        suggestions.add("如何提高成绩？");
        suggestions.add("我的课程表是什么？");
        response.put("suggestions", suggestions);
        return response;
    }

    private String generateResponse(String userId, String message) {
        try {
            Long userIdLong = Long.parseLong(userId);
            StudentProfile student = studentService.getByUserId(userIdLong);
            if (student == null) {
                return "我无法找到你的学生信息，请稍后再试。";
            }

            // 构建相关数据 - 简化版
            String relevantData = "学生基本信息：姓名：" + student.getName() + "，学号：" + student.getStudentId() + "\n";
            
            // 所有问题都调用智谱API
            try {
                String aiResponse = callZhipuAPI(message, student, relevantData);
                return aiResponse != null ? aiResponse : "我理解你的问题，但目前无法提供详细回答。";
            } catch (Exception e) {
                System.out.println("智谱API调用失败：" + e.getMessage());
                e.printStackTrace();
                // 如果API调用失败，返回默认回复
                return "我理解你的问题，但目前无法提供详细回答。请稍后再试。";
            }

        } catch (NumberFormatException e) {
            return "用户ID格式错误，请稍后再试。";
        } catch (Exception e) {
            System.out.println("处理请求失败：" + e.getMessage());
            e.printStackTrace();
            return "系统暂时无法处理你的请求，请稍后再试。";
        }
    }

    // 调用智谱API
    private String callZhipuAPI(String message, StudentProfile student, String relevantData) throws IOException, InterruptedException {
        System.out.println("开始调用智谱API...");
        
        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "glm-4");
        
        List<Map<String, String>> messages = new ArrayList<>();
        
        // 系统提示 - 增强版，提供更详细的指令和上下文，包含实际数据
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "你是学业预警系统的AI助手，专门负责回答学生的学业相关问题。请严格按照以下要求回答：\n" +
                "1. 只回答与学生学业相关的问题，包括但不限于：成绩查询、预警信息、课程安排、GPA计算、帮扶计划等。\n" +
                "2. 对于非学业相关的问题，礼貌地告知用户你只能回答学业相关问题。\n" +
                "3. 回答必须与用户的问题直接相关，避免无关内容。\n" +
                "4. 回答要准确、简洁、友好，避免使用专业术语，确保学生能够理解。\n" +
                "5. 如果无法回答用户的问题，要明确告知，并提供合理的建议。\n" +
                "6. 学生信息：姓名：" + student.getName() + "，学号：" + student.getStudentId() + "\n" +
                "7. 相关数据：" + relevantData + "\n" +
                "8. 请严格根据用户的问题内容和提供的相关数据生成回复，确保回复与问题高度相关且准确反映实际情况。");


        messages.add(systemMessage);
        
        // 用户问题
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", message);
        messages.add(userMessage);
        
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.3); // 降低温度，减少随机性
        requestBody.put("max_tokens", 500);

        System.out.println("请求体：" + objectMapper.writeValueAsString(requestBody));

        // 构建HTTP请求
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ZHIPU_API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + ZHIPU_API_KEY)
                .timeout(java.time.Duration.ofSeconds(30)) // 设置30秒超时
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody)))
                .build();

        // 发送请求并获取响应
        System.out.println("发送请求到智谱API...");
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("智谱API响应状态码：" + response.statusCode());
        System.out.println("智谱API响应内容：" + response.body());

        // 解析响应
        if (response.statusCode() == 200) {
            Map<?, ?> responseMap = objectMapper.readValue(response.body(), Map.class);
            List<?> choices = (List<?>) responseMap.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<?, ?> choice = (Map<?, ?>) choices.get(0);
                Map<?, ?> messageMap = (Map<?, ?>) choice.get("message");
                String content = (String) messageMap.get("content");
                System.out.println("智谱API返回内容：" + content);
                
                // 验证回复是否与问题相关
                if (isResponseRelevant(message, content)) {
                    return content;
                } else {
                    System.out.println("AI回复与问题无关，返回兜底回答");
                    return "抱歉，我无法理解你的问题。请尝试重新表述，或询问与学业相关的问题。";
                }
            }
        }

        return null;
    }
    
    // 验证AI回复是否与问题相关
    private boolean isResponseRelevant(String question, String response) {
        // 简单的相关性检查
        if (response == null || response.isEmpty()) {
            return false;
        }
        
        // 检查回复是否包含明显的无关内容
        String lowerQuestion = question.toLowerCase();
        String lowerResponse = response.toLowerCase();
        
        // 检查是否是标准的无关回复
        if (lowerResponse.contains("无法回答") || lowerResponse.contains("不理解") || lowerResponse.contains("不清楚")) {
            return true; // 这些是有效的回复
        }
        
        // 检查回复是否与学业相关
        String[] academicKeywords = {"成绩", "预警", "gpa", "课程", "帮扶", "计划", "学分", "考试", "学期"};
        boolean hasAcademicKeyword = false;
        for (String keyword : academicKeywords) {
            if (lowerResponse.contains(keyword)) {
                hasAcademicKeyword = true;
                break;
            }
        }
        
        // 如果问题包含学业关键词，回复也应该包含
        for (String keyword : academicKeywords) {
            if (lowerQuestion.contains(keyword) && !hasAcademicKeyword) {
                return false;
            }
        }
        
        return true;
    }
}
