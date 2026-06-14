package com.example.project.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JwtUtils 单元测试
 * 验证 Token 的生成和校验逻辑
 */
class JwtUtilsTest {

    @Test
    @DisplayName("生成 Token 不为空")
    void testGenerateToken() {
        String token = JwtUtils.generateToken("test01");
        assertNotNull(token);
        assertFalse(token.isEmpty());
        System.out.println("生成的 Token: " + token.substring(0, 30) + "...");
    }

    @Test
    @DisplayName("有效 Token 能正确解析出用户名")
    void testValidateValidToken() {
        String token = JwtUtils.generateToken("test01");
        String username = JwtUtils.validateToken(token);
        assertEquals("test01", username);
    }

    @Test
    @DisplayName("篡改的 Token 返回 null")
    void testValidateTamperedToken() {
        String token = JwtUtils.generateToken("test01");
        // 在末尾加一个字符，模拟篡改
        String tampered = token + "x";
        String username = JwtUtils.validateToken(tampered);
        assertNull(username);
    }

    @Test
    @DisplayName("空 Token 返回 null")
    void testValidateEmptyToken() {
        assertNull(JwtUtils.validateToken(""));
    }

    @Test
    @DisplayName("两个不同用户生成的 Token 解析结果不同")
    void testDifferentUsersHaveDifferentTokens() {
        String token1 = JwtUtils.generateToken("student");
        String token2 = JwtUtils.generateToken("teacher");
        assertNotEquals(token1, token2);
        assertEquals("student", JwtUtils.validateToken(token1));
        assertEquals("teacher", JwtUtils.validateToken(token2));
    }
}
