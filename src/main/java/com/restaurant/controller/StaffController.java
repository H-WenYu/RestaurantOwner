package com.restaurant.controller;

import com.restaurant.dto.ApiResponse;
import com.restaurant.dto.EmployeeDTO;
import com.restaurant.game.Hotel;
import com.restaurant.model.Chef;
import com.restaurant.model.Waiter;
import com.restaurant.session.GameSession;
import com.restaurant.session.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 员工管理控制器
 */
@RestController
@RequestMapping("/api/staff")
public class StaffController {

    @Autowired
    private SessionManager sessionManager;

    /**
     * 获取招聘池
     * GET /api/staff/pool?openId=xxx
     */
    @GetMapping("/pool")
    public ApiResponse<Map<String, Object>> getRecruitPool(@RequestParam String openId) {
        GameSession session = sessionManager.getSession(openId);
        if (session == null || !session.hasGame()) {
            return ApiResponse.error(1002, "请先登录");
        }

        Hotel hotel = session.getHotel();

        List<EmployeeDTO> waiterPool = hotel.getWaiterPool().stream()
                .map(EmployeeDTO::fromWaiter)
                .collect(Collectors.toList());

        List<EmployeeDTO> chefPool = hotel.getChefPool().stream()
                .map(EmployeeDTO::fromChef)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("waiterPool", waiterPool);
        result.put("chefPool", chefPool);
        result.put("currentWaiters", hotel.getWaiters().size());
        result.put("maxWaiters", hotel.getMaxWaiters());
        result.put("currentChefs", hotel.getChefs().size());
        result.put("maxChefs", hotel.getMaxChefs());

        return ApiResponse.success(result);
    }

    /**
     * 招聘服务员
     * POST /api/staff/hire/waiter
     * Body: { "openId": "xxx", "index": 0 }
     */
    @PostMapping("/hire/waiter")
    public ApiResponse<Map<String, Object>> hireWaiter(@RequestBody Map<String, Object> params) {
        String openId = (String) params.get("openId");
        int index = (Integer) params.get("index");

        GameSession session = sessionManager.getSession(openId);
        if (session == null || !session.hasGame()) {
            return ApiResponse.error(1002, "请先登录");
        }

        Hotel hotel = session.getHotel();
        List<Waiter> pool = hotel.getWaiterPool();

        if (index < 0 || index >= pool.size()) {
            return ApiResponse.error(1006, "无效的招聘索引");
        }

        Waiter waiter = pool.get(index);
        int cost = waiter.getHirePrice();

        if (hotel.getMoney() < cost) {
            return ApiResponse.error(1007, "金钱不足");
        }

        boolean success = hotel.hireWaiter(waiter.getName(), cost, waiter.getEmployeeTier());

        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("name", waiter.getName());
        result.put("cost", cost);
        result.put("currentMoney", hotel.getMoney());

        return success ? ApiResponse.success("招聘成功", result)
                : ApiResponse.error(1008, "招聘失败");
    }

    /**
     * 招聘厨师
     * POST /api/staff/hire/chef
     * Body: { "openId": "xxx", "index": 0 }
     */
    @PostMapping("/hire/chef")
    public ApiResponse<Map<String, Object>> hireChef(@RequestBody Map<String, Object> params) {
        String openId = (String) params.get("openId");
        int index = (Integer) params.get("index");

        GameSession session = sessionManager.getSession(openId);
        if (session == null || !session.hasGame()) {
            return ApiResponse.error(1002, "请先登录");
        }

        Hotel hotel = session.getHotel();
        List<Chef> pool = hotel.getChefPool();

        if (index < 0 || index >= pool.size()) {
            return ApiResponse.error(1006, "无效的招聘索引");
        }

        Chef chef = pool.get(index);
        int cost = chef.getHirePrice();

        if (hotel.getMoney() < cost) {
            return ApiResponse.error(1007, "金钱不足");
        }

        boolean success = hotel.hireChef(chef.getName(), cost, chef.getEmployeeTier());

        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("name", chef.getName());
        result.put("cost", cost);
        result.put("currentMoney", hotel.getMoney());

        return success ? ApiResponse.success("招聘成功", result)
                : ApiResponse.error(1008, "招聘失败");
    }

    /**
     * 刷新招聘池
     * POST /api/staff/refresh
     */
    @PostMapping("/refresh")
    public ApiResponse<String> refreshPool(@RequestBody Map<String, String> params) {
        String openId = params.get("openId");

        GameSession session = sessionManager.getSession(openId);
        if (session == null || !session.hasGame()) {
            return ApiResponse.error(1002, "请先登录");
        }

        session.getHotel().refreshEmployeePool();
        return ApiResponse.success("刷新成功");
    }

    /**
     * 获取当前员工列表
     * GET /api/staff/list?openId=xxx
     */
    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> getStaffList(@RequestParam String openId) {
        GameSession session = sessionManager.getSession(openId);
        if (session == null || !session.hasGame()) {
            return ApiResponse.error(1002, "请先登录");
        }

        Hotel hotel = session.getHotel();

        List<EmployeeDTO> waiters = hotel.getWaiters().stream()
                .map(EmployeeDTO::fromWaiter)
                .collect(Collectors.toList());

        List<EmployeeDTO> chefs = hotel.getChefs().stream()
                .map(EmployeeDTO::fromChef)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("waiters", waiters);
        result.put("chefs", chefs);

        return ApiResponse.success(result);
    }
}
