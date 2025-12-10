package com.restaurant.session;

import com.restaurant.game.Hotel;
import com.restaurant.game.SaveManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话管理器 - 管理所有用户的游戏会话
 */
@Component
public class SessionManager {

    private final Map<String, GameSession> sessions = new ConcurrentHashMap<>();

    @Value("${game.session-timeout-minutes:30}")
    private int sessionTimeoutMinutes;

    @Value("${game.max-sessions:1000}")
    private int maxSessions;

    @PostConstruct
    public void init() {
        System.out.println("📦 SessionManager 初始化完成，最大会话数: " + maxSessions);
    }

    /**
     * 获取或创建会话
     */
    public GameSession getOrCreateSession(String openId) {
        return sessions.computeIfAbsent(openId, id -> {
            System.out.println("🆕 创建新会话: " + openId);
            return new GameSession(id);
        });
    }

    /**
     * 获取会话（可能为null）
     */
    public GameSession getSession(String openId) {
        GameSession session = sessions.get(openId);
        if (session != null) {
            session.touch();
        }
        return session;
    }

    /**
     * 登录 - 尝试加载存档或创建新游戏
     */
    public GameSession login(String openId, String hotelName) {
        GameSession session = getOrCreateSession(openId);

        if (!session.hasGame()) {
            // 尝试加载存档
            Hotel hotel = SaveManager.loadGame(openId);
            if (hotel != null) {
                session.setHotel(hotel);
                System.out.println("📂 加载存档成功: " + openId);
            } else {
                // 创建新游戏
                session.newGame(hotelName != null ? hotelName : "我的餐厅");
                System.out.println("🎮 创建新游戏: " + openId);
            }
        }

        return session;
    }

    /**
     * 保存游戏
     */
    public boolean saveGame(String openId) {
        GameSession session = sessions.get(openId);
        if (session != null && session.hasGame()) {
            return SaveManager.saveGame(session.getHotel(), openId);
        }
        return false;
    }

    /**
     * 推进所有活跃会话的游戏
     */
    public void tickAllSessions() {
        for (GameSession session : sessions.values()) {
            if (session.isRunning() && session.hasGame()) {
                try {
                    session.tick();
                } catch (Exception e) {
                    System.err.println("❌ 会话tick失败: " + session.getOpenId() + " - " + e.getMessage());
                }
            }
        }
    }

    /**
     * 清理超时会话
     */
    public void cleanupInactiveSessions() {
        long timeoutMs = sessionTimeoutMinutes * 60 * 1000L;
        sessions.entrySet().removeIf(entry -> {
            GameSession session = entry.getValue();
            if (session.isExpired(timeoutMs)) {
                // 保存后移除
                if (session.hasGame()) {
                    SaveManager.saveGame(session.getHotel(), session.getOpenId());
                }
                System.out.println("🗑️ 清理超时会话: " + entry.getKey());
                return true;
            }
            return false;
        });
    }

    /**
     * 获取当前在线人数
     */
    public int getOnlineCount() {
        return sessions.size();
    }

    /**
     * 获取正在运营的会话数
     */
    public int getRunningCount() {
        return (int) sessions.values().stream().filter(GameSession::isRunning).count();
    }
}
