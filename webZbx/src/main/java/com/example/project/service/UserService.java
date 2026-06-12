package com.example.project.service;

import com.example.project.entity.User;
import com.example.project.mapper.UserMapper;
import com.example.project.utils.JwtUtils;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public boolean register(String username, String password, String email, String type) {
        // 1. 检查用户名是否已存在
        User existingUser = userMapper.selectByUsername(username);
        if (existingUser != null) {
            return false;  // 用户名已存在
        }

        // 2. 使用 BCrypt 加密密码
        String encryptedPassword = passwordEncoder.encode(password);

        // 3. 创建用户对象
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(encryptedPassword);
        newUser.setEmail(email);
        if (type.equals("admin") || type.equals("teacher") || type.equals("student")) {
            newUser.setType(type);
        } else {
            return false;
        }

        // 4. 保存到数据库
        return userMapper.insertUser(newUser) > 0;
    }

    public String login(String username, String password) {
        // 1. 根据用户名查询用户
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            return null;  // 用户不存在
        }

        // 2. 使用 BCrypt 验证密码
        if (passwordEncoder.matches(password, user.getPassword())) {
            // 登录成功，生成并返回 JWT token
            return JwtUtils.generateToken(username);
        }
        return null;
    }

    public User getUserByUserName(String userName) {
        return userMapper.selectByUsername(userName);
    }

    public String validateToken(String token) {
        return JwtUtils.validateToken(token);
    }

    public User updateUser(User user) {
        try {
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                String password = user.getPassword();
                // 使用 BCrypt 加密新密码
                String encryptedPassword = passwordEncoder.encode(password);
                user.setPassword(encryptedPassword);
            }
            userMapper.updateUser(user);
            return userMapper.selectByUsername(user.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public boolean deleteUser(String username){
        return userMapper.deleteByUserName(username)>0;
    }

    public ArrayList<User> getAllUsers(){
        return userMapper.selectAll();
    }
}
