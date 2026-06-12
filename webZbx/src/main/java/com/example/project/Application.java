package com.example.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController  // 添加这个注解，确保它是个Web应用
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

    }

}

