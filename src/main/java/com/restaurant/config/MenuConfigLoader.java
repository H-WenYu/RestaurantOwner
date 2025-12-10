package com.restaurant.config;

import com.restaurant.model.Dish;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 简单的菜单配置加载器，从 resources/menu.csv 读取菜品定义。
 */
public class MenuConfigLoader {
    private MenuConfigLoader() {
    }

    public static List<Dish> loadMenuFromResource() {
        List<Dish> dishes = new ArrayList<>();
        try (InputStream in = MenuConfigLoader.class.getClassLoader().getResourceAsStream("menu.csv")) {
            if (in == null) {
                return dishes;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                reader.lines()
                        .map(String::trim)
                        .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                        .forEach(line -> parseLine(line, dishes));
            }
        } catch (IOException ignored) {
            // 读取失败时返回已解析的部分，保持游戏可运行
        }
        return dishes;
    }

    private static void parseLine(String line, List<Dish> dishes) {
        String[] parts = line.split(",");
        if (parts.length < 7) {
            return;
        }
        try {
            String name = parts[0].trim();
            int price = Integer.parseInt(parts[1].trim());
            int cookTime = Integer.parseInt(parts[2].trim());
            int exp = Integer.parseInt(parts[3].trim());
            int satisfaction = Integer.parseInt(parts[4].trim());
            int level = Integer.parseInt(parts[5].trim());
            int stamina = Integer.parseInt(parts[6].trim());
            int servings = parts.length > 7 ? Integer.parseInt(parts[7].trim()) : 1;
            // 新增字段：成本、店铺经验、厨师经验
            int cost = parts.length > 8 ? Integer.parseInt(parts[8].trim()) : price / 3;
            int shopExp = parts.length > 9 ? Integer.parseInt(parts[9].trim()) : exp;
            int chefExp = parts.length > 10 ? Integer.parseInt(parts[10].trim()) : exp / 2;
            dishes.add(new Dish(name, price, cookTime, exp, satisfaction, level, stamina, servings, cost, shopExp,
                    chefExp));
        } catch (NumberFormatException ignored) {
            // 略过格式错误的行
        }
    }
}
