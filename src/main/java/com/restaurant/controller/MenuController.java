package com.restaurant.controller;

import com.restaurant.dto.ApiResponse;
import com.restaurant.dto.DishDTO;
import com.restaurant.game.Hotel;
import com.restaurant.model.DishInstance;
import com.restaurant.session.GameSession;
import com.restaurant.session.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 菜单管理控制器
 */
@RestController
@RequestMapping("/api/menu")
public class MenuController {

    @Autowired
    private SessionManager sessionManager;

    /**
     * 获取菜单列表（已解锁菜品 + 上架状态）
     * GET /api/menu/list?openId=xxx
     */
    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> getMenuList(@RequestParam String openId) {
        GameSession session = sessionManager.getSession(openId);
        if (session == null || !session.hasGame()) {
            return ApiResponse.error(1002, "请先登录");
        }

        Hotel hotel = session.getHotel();

        List<DishDTO> unlocked = hotel.getUnlockedDishes().values().stream()
                .map(DishDTO::fromDishInstance)
                .collect(Collectors.toList());

        List<DishDTO> active = hotel.getActiveMenu().stream()
                .map(DishDTO::fromDishInstance)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("unlocked", unlocked);
        result.put("active", active);
        result.put("maxActive", hotel.getMaxActiveMenu());
        result.put("activeCount", active.size());

        return ApiResponse.success(result);
    }

    /**
     * 上架菜品
     * POST /api/menu/activate
     * Body: { "openId": "xxx", "dishName": "蛋炒饭" }
     */
    @PostMapping("/activate")
    public ApiResponse<Map<String, Object>> activateDish(@RequestBody Map<String, String> params) {
        String openId = params.get("openId");
        String dishName = params.get("dishName");

        GameSession session = sessionManager.getSession(openId);
        if (session == null || !session.hasGame()) {
            return ApiResponse.error(1002, "请先登录");
        }

        Hotel hotel = session.getHotel();
        boolean success = hotel.activateDish(dishName);

        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("dishName", dishName);
        result.put("activeCount", hotel.getActiveMenuNames().size());

        return success ? ApiResponse.success("上架成功", result)
                : ApiResponse.error(1004, "上架失败");
    }

    /**
     * 下架菜品
     * POST /api/menu/deactivate
     * Body: { "openId": "xxx", "dishName": "蛋炒饭" }
     */
    @PostMapping("/deactivate")
    public ApiResponse<Map<String, Object>> deactivateDish(@RequestBody Map<String, String> params) {
        String openId = params.get("openId");
        String dishName = params.get("dishName");

        GameSession session = sessionManager.getSession(openId);
        if (session == null || !session.hasGame()) {
            return ApiResponse.error(1002, "请先登录");
        }

        Hotel hotel = session.getHotel();
        boolean success = hotel.deactivateDish(dishName);

        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("dishName", dishName);
        result.put("activeCount", hotel.getActiveMenuNames().size());

        return success ? ApiResponse.success("下架成功", result)
                : ApiResponse.error(1005, "下架失败");
    }
}
