package com.restaurant.controller;

import com.restaurant.dto.ApiResponse;
import com.restaurant.game.Hotel;
import com.restaurant.session.GameSession;
import com.restaurant.session.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 商店控制器 - 餐厅升级、购买桌子等
 */
@RestController
@RequestMapping("/api/shop")
public class ShopController {

    @Autowired
    private SessionManager sessionManager;

    /**
     * 获取升级信息
     * GET /api/shop/upgrade-info?openId=xxx
     */
    @GetMapping("/upgrade-info")
    public ApiResponse<Map<String, Object>> getUpgradeInfo(@RequestParam String openId) {
        GameSession session = sessionManager.getSession(openId);
        if (session == null || !session.hasGame()) {
            return ApiResponse.error(1002, "请先登录");
        }

        Hotel hotel = session.getHotel();

        Map<String, Object> result = new HashMap<>();
        result.put("hotelLevel", hotel.getHotelLevel());
        result.put("hotelExp", hotel.getHotelExp());
        result.put("expToNextLevel", hotel.getExpToNextLevel());
        result.put("money", hotel.getMoney());

        // 当前容量
        result.put("currentWaiters", hotel.getWaiters().size());
        result.put("maxWaiters", hotel.getMaxWaiters());
        result.put("currentChefs", hotel.getChefs().size());
        result.put("maxChefs", hotel.getMaxChefs());
        result.put("currentTables", hotel.getTables().size());
        result.put("maxTables", hotel.getMaxTables());

        // 购买价格
        result.put("table2Price", 200);
        result.put("table4Price", 500);
        result.put("table6Price", 1000);

        return ApiResponse.success(result);
    }

    /**
     * 购买桌子
     * POST /api/shop/buy-table
     * Body: { "openId": "xxx", "seats": 4 }
     */
    @PostMapping("/buy-table")
    public ApiResponse<Map<String, Object>> buyTable(@RequestBody Map<String, Object> params) {
        String openId = (String) params.get("openId");
        int seats = (Integer) params.get("seats");

        GameSession session = sessionManager.getSession(openId);
        if (session == null || !session.hasGame()) {
            return ApiResponse.error(1002, "请先登录");
        }

        Hotel hotel = session.getHotel();

        // 根据座位数确定价格
        int price;
        switch (seats) {
            case 2:
                price = 200;
                break;
            case 4:
                price = 500;
                break;
            case 6:
                price = 1000;
                break;
            default:
                return ApiResponse.error(1010, "无效的桌子类型");
        }

        boolean success = hotel.buyTable(seats, price);

        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("seats", seats);
        result.put("price", price);
        result.put("currentMoney", hotel.getMoney());
        result.put("tableCount", hotel.getTables().size());

        return success ? ApiResponse.success("购买成功", result)
                : ApiResponse.error(1011, "购买失败");
    }

    /**
     * 获取商店商品列表
     * GET /api/shop/items?openId=xxx
     */
    @GetMapping("/items")
    public ApiResponse<Map<String, Object>> getShopItems(@RequestParam String openId) {
        GameSession session = sessionManager.getSession(openId);
        if (session == null || !session.hasGame()) {
            return ApiResponse.error(1002, "请先登录");
        }

        Hotel hotel = session.getHotel();

        Map<String, Object> result = new HashMap<>();
        result.put("money", hotel.getMoney());

        // 桌子商品
        Map<String, Object> tables = new HashMap<>();
        tables.put("table2",
                Map.of("seats", 2, "price", 200, "available", hotel.getTables().size() < hotel.getMaxTables()));
        tables.put("table4",
                Map.of("seats", 4, "price", 500, "available", hotel.getTables().size() < hotel.getMaxTables()));
        tables.put("table6",
                Map.of("seats", 6, "price", 1000, "available", hotel.getTables().size() < hotel.getMaxTables()));
        result.put("tables", tables);

        return ApiResponse.success(result);
    }
}
