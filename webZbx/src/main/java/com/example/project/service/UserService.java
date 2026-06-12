package com.example.project.service;

import com.example.project.entity.User;
import com.example.project.mapper.UserMapper;
import com.example.project.utils.JwtUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@Service
public class UserService {
    @Autowired  // 自动注入UserMapper
    private UserMapper userMapper;
    public boolean register(String username, String password, String email,String type) {
        // 1. 检查用户名是否已存在
        User existingUser = userMapper.selectByUsername(username);
        if (existingUser != null) {
            return false;  // 用名已存在
        }

        // 2. 对密码进行MD5加密
        String encryptedPassword = DigestUtils.md5DigestAsHex(
                password.getBytes(StandardCharsets.UTF_8)
        );

        // 3. 创建用户对象
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(encryptedPassword);
        newUser.setEmail(email);
        if (type.equals("admin") || type.equals("teacher")|| type.equals("student")){
            newUser.setType(type);
        }else{
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

        // 2. 对输入的密码进行MD5加密
        String encryptedPassword = DigestUtils.md5DigestAsHex(
                password.getBytes(StandardCharsets.UTF_8)
        );

        // 3. 比较加密后的密码
        if (encryptedPassword.equals(user.getPassword())) {
            // 登录成功，生成并返回 JWT token
            return JwtUtils.generateToken(username);
        }
        return null;  //
    }

    public User getUserByUserName(String userName){
        return userMapper.selectByUsername(userName);
    }


    public String validateToken(String token) {
        return JwtUtils.validateToken(token);
    }

    public User updateUser(User user) {
        try {
            if (user.getPassword()!=null) {
                String password=user.getPassword();
                String encryptedPassword = DigestUtils.md5DigestAsHex(
                        password.getBytes(StandardCharsets.UTF_8)
                );
                user.setPassword(encryptedPassword);
            }
            userMapper.updateUser(user);
            return userMapper.selectByUsername(user.getUsername());
        }catch (Exception e){
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
