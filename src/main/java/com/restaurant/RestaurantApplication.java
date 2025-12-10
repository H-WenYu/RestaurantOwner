package com.restaurant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 餐厅经营游戏 - Spring Boot 启动类
 */
@SpringBootApplication
@EnableScheduling
public class RestaurantApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestaurantApplication.class, args);
        System.out.println("🍽️ 餐厅游戏后端已启动！");
        System.out.println("📡 API地址: http://localhost:9090/api");
    }
}
