package com.example.project.config;

import com.example.project.service.TokenBlacklistService;
import com.example.project.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JWT 认证拦截器
 * 对所有 /api/** 请求进行 Token 校验 + 黑名单检查
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 OPTIONS 预检请求（CORS）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String requestURI = request.getRequestURI();

        // 放行登录和注册接口
        if (requestURI.contains("/api/auth/login") || requestURI.contains("/api/auth/register")) {
            return true;
        }

        // 获取请求头中的 Token
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 校验 Token
        if (token == null || token.isEmpty()) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(401);
            response.getWriter().write("{\"success\":false,\"message\":\"未登录，请先登录\"}");
            return false;
        }

        // 检查 Token 是否在黑名单中（已登出）
        if (tokenBlacklistService.isBlacklisted(token)) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(401);
            response.getWriter().write("{\"success\":false,\"message\":\"Token 已失效，请重新登录\"}");
            return false;
        }

        String username = JwtUtils.validateToken(token);
        if (username == null) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(401);
            response.getWriter().write("{\"success\":false,\"message\":\"Token 无效或已过期，请重新登录\"}");
            return false;
        }

        // Token 有效，将用户名存入 request 属性供后续使用
        request.setAttribute("currentUsername", username);
        return true;
    }
}
