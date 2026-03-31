package com.academic.feign;

import com.academic.dto.LoginRequest;
import com.academic.dto.LoginResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 用户服务Feign客户端
 */
@FeignClient(name = "user-service", url = "${feign.client.url.user-service:http://localhost:8082}")
public interface UserServiceFeignClient {
    
    @PostMapping("/api/users/login")
    LoginResponse login(@RequestBody LoginRequest request);
    
    @PostMapping("/api/users/register")
    void register(@RequestBody Object request);
}
