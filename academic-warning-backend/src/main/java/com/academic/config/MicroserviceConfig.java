package com.academic.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * 微服务配置类
 */
@Configuration
@EnableFeignClients(basePackages = "com.academic.feign")
public class MicroserviceConfig {
}
