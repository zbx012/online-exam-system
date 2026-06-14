package com.example.project.service;

import com.example.project.entity.User;
import com.example.project.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UserService 单元测试
 * 使用 Mockito 模拟数据库层，验证注册和登录的业务逻辑
 */
class UserServiceTest {

    private UserMapper userMapper;       // Mock 对象
    private UserService userService;     // 被测试对象

    @BeforeEach
    void setUp() {
        userMapper = mock(UserMapper.class);
        userService = new UserService();
        // 通过反射注入 mock 的 UserMapper
        try {
            var field = UserService.class.getDeclaredField("userMapper");
            field.setAccessible(true);
            field.set(userService, userMapper);
        } catch (Exception e) {
            fail("注入 Mock 失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("注册成功 — 用户名不存在")
    void testRegisterSuccess() {
        // 准备：数据库里没有这个用户
        when(userMapper.selectByUsername("newUser")).thenReturn(null);
        when(userMapper.insertUser(any(User.class))).thenReturn(1);

        boolean result = userService.register("newUser", "123456", "test@qq.com", "student");

        assertTrue(result);

        // 验证：密码确实被 BCrypt 加密了（不是明文）
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).insertUser(captor.capture());
        User savedUser = captor.getValue();
        assertNotNull(savedUser.getPassword());
        assertNotEquals("123456", savedUser.getPassword()); // 已加密
        assertTrue(savedUser.getPassword().startsWith("$2a$")); // BCrypt 特征前缀
    }

    @Test
    @DisplayName("注册失败 — 用户名已存在")
    void testRegisterDuplicateUsername() {
        User existingUser = new User();
        existingUser.setUsername("test01");
        when(userMapper.selectByUsername("test01")).thenReturn(existingUser);

        boolean result = userService.register("test01", "123456", "test@qq.com", "student");

        assertFalse(result);
        verify(userMapper, never()).insertUser(any()); // 没调用插入
    }

    @Test
    @DisplayName("注册失败 — 无效的角色类型")
    void testRegisterInvalidType() {
        when(userMapper.selectByUsername("newUser")).thenReturn(null);

        boolean result = userService.register("newUser", "123456", "test@qq.com", "hacker");

        assertFalse(result);
    }

    @Test
    @DisplayName("登录成功 — 正确的用户名和密码")
    void testLoginSuccess() {
        // 准备一个数据库中已有的用户（密码是 BCrypt 加密的 "123456"）
        User dbUser = new User();
        dbUser.setUsername("test01");
        // 生成一个真实的 BCrypt hash
        String bcryptHash = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder()
                .encode("123456");
        dbUser.setPassword(bcryptHash);
        when(userMapper.selectByUsername("test01")).thenReturn(dbUser);

        String token = userService.login("test01", "123456");

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("登录失败 — 密码错误")
    void testLoginWrongPassword() {
        User dbUser = new User();
        dbUser.setUsername("test01");
        dbUser.setPassword(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder()
                .encode("123456"));
        when(userMapper.selectByUsername("test01")).thenReturn(dbUser);

        String token = userService.login("test01", "wrongpassword");

        assertNull(token);
    }

    @Test
    @DisplayName("登录失败 — 用户不存在")
    void testLoginUserNotFound() {
        when(userMapper.selectByUsername("nobody")).thenReturn(null);

        String token = userService.login("nobody", "123456");

        assertNull(token);
    }
}
