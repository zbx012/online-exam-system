package com.example.project.controller;

import com.example.project.dto.LoginResponse;
import com.example.project.entity.User;
import com.example.project.service.TokenBlacklistService;
import com.example.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

@RestController  // 声明这是一个REST API控制器
@RequestMapping("/api/auth")  // 所有api的前缀
public class AutoController {
    @Autowired
    private UserService userService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;


    //用户注册api
    @PostMapping("/register")
    public String register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "student") String type){

        boolean success = userService.register(username, password, email,type);
        if (success) {
            return "注册成功！";
        } else {
            return "注册失败：用户名已存在";
        }
    }

    //用户登录api
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestParam String username,
            @RequestParam String password) {
        try {
            String token = userService.login(username, password);
            if (token != null) {
                User user = userService.getUserByUserName(username);
                LoginResponse response = new LoginResponse(true, "登录成功", token, user);
                return ResponseEntity.ok(response);
            } else {
                LoginResponse response = new LoginResponse(false, "登录失败：用户名或密码错误", null, null);
                return ResponseEntity.status(401).body(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/info")
    public ResponseEntity<User> info(@RequestParam String userName){
        try {
            User user = userService.getUserByUserName(userName);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/update")
    public ResponseEntity<User> update(
        @RequestBody User user){
        try {
            //String token = userService.validateToken(user.getUsername());
            //if (token != null) {
                User updateUser= userService.updateUser(user);
                if (updateUser!=null) {
                    return ResponseEntity.ok(updateUser);
                } else {
                    return ResponseEntity.status(500).build();
                }
            //}
            //return ResponseEntity.status(401).body("更新失败：用户未登录");
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam String userName){
        try {
           // String token = userService.validateToken(userName);
            //if (token != null) {
                boolean success = userService.deleteUser(userName);
                if (success) {
                    return ResponseEntity.ok("删除成功！");
                } else {
                    return ResponseEntity.status(500).body("删除失败：用户名不存在");
                }
            //}
            //return ResponseEntity.status(401).body("删除失败：用户未登录");
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/getAllUser")
    public ResponseEntity<ArrayList<User>> getAllUser(){
        try {
            return ResponseEntity.ok(userService.getAllUsers());
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 用户登出 — 将 Token 加入 Redis 黑名单
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> result = new java.util.HashMap<>();
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                tokenBlacklistService.blacklist(token);
                result.put("success", true);
                result.put("message", "已退出登录");
                return ResponseEntity.ok(result);
            }
            result.put("success", false);
            result.put("message", "未找到有效的 Token");
            return ResponseEntity.badRequest().body(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "登出失败");
            return ResponseEntity.internalServerError().body(result);
        }
    }


}
