package com.restaurant.controller;

import com.restaurant.dto.ApiResponse;
import com.restaurant.dto.GameStateDTO;
import com.restaurant.session.GameSession;
import com.restaurant.session.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 游戏主控制器
 */
@RestController
@RequestMapping("/api/game")
public class GameController {

    @Autowired
    private SessionManager sessionManager;

    /**
     * 登录/创建游戏会话
     * POST /api/game/login
     * Body: { "openId": "xxx", "hotelName": "我的餐厅" }
     */
    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String openId = params.get("openId");
        String hotelName = params.get("hotelName");

        if (openId == null || openId.isEmpty()) {
            return ApiResponse.error(1001, "openId不能为空");
        }

        GameSession session = sessionManager.login(openId, hotelName);

        Map<String, Object> result = new HashMap<>();
        result.put("openId", openId);
        result.put("hotelName", session.getHotel().getName());
        result.put("isNewGame", !com.restaurant.game.SaveManager.hasSaveFile(openId));
        result.put("running", session.isRunning());

        return ApiResponse.success("登录成功", result);
    }

    /**
     * 获取完整游戏状态
     * GET /api/game/state?openId=xxx
     */
    @GetMapping("/state")
    public ApiResponse<GameStateDTO> getState(@RequestParam String openId) {
        GameSession session = sessionManager.getSession(openId);
        if (session == null || !session.hasGame()) {
            return ApiResponse.error(1002, "请先登录");
        }

        GameStateDTO state = GameStateDTO.fromSession(session);
        return ApiResponse.success(state);
    }

    /**
     * 开始/暂停营业
     * POST /api/game/toggle
     * Body: { "openId": "xxx" }
     */
    @PostMapping("/toggle")
    public ApiResponse<Map<String, Object>> toggleRunning(@RequestBody Map<String, String> params) {
        String openId = params.get("openId");
        GameSession session = sessionManager.getSession(openId);

        if (session == null || !session.hasGame()) {
            return ApiResponse.error(1002, "请先登录");
        }

        if (session.isRunning()) {
            session.stopRunning();
        } else {
            session.startRunning();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("running", session.isRunning());
        result.put("message", session.isRunning() ? "开始营业" : "暂停营业");

        return ApiResponse.success(result);
    }

    /**
     * 保存游戏
     * POST /api/game/save
     */
    @PostMapping("/save")
    public ApiResponse<String> saveGame(@RequestBody Map<String, String> params) {
        String openId = params.get("openId");

        if (sessionManager.saveGame(openId)) {
            return ApiResponse.success("保存成功");
        } else {
            return ApiResponse.error(1003, "保存失败");
        }
    }

    /**
     * 获取服务器状态
     * GET /api/game/status
     */
    @GetMapping("/status")
    public ApiResponse<Map<String, Object>> getServerStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("online", sessionManager.getOnlineCount());
        status.put("running", sessionManager.getRunningCount());
        status.put("serverTime", System.currentTimeMillis());
        return ApiResponse.success(status);
    }
}
