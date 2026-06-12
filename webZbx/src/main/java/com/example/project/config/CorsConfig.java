package com.example.project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 开发环境配置（允许所有来源）
        registry.addMapping("/**")  // 所有接口
                .allowedOriginPatterns("*")  // 允许所有来源，生产环境要修改
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH","OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)  // 允许携带cookie
                .maxAge(3600);  // 预检请求缓存时间（秒）

        System.out.println("✅ CORS配置已加载：允许所有来源访问");
    }
}