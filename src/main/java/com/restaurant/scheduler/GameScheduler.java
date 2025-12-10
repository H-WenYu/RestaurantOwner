package com.restaurant.scheduler;

import com.restaurant.session.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 游戏调度器 - 定时推进所有活跃游戏
 */
@Component
public class GameScheduler {

    @Autowired
    private SessionManager sessionManager;

    @Value("${game.tick-interval-ms:1000}")
    private int tickInterval;

    /**
     * 定时推进所有活跃会话的游戏
     */
    @Scheduled(fixedRateString = "${game.tick-interval-ms:1000}")
    public void tickAllGames() {
        sessionManager.tickAllSessions();
    }

    /**
     * 每5分钟清理一次过期会话
     */
    @Scheduled(fixedRate = 300000)
    public void cleanupSessions() {
        sessionManager.cleanupInactiveSessions();
    }

    /**
     * 每10分钟自动保存所有游戏
     */
    @Scheduled(fixedRate = 600000)
    public void autoSaveAll() {
        System.out.println("💾 自动保存所有游戏...");
        // TODO: 遍历所有会话并保存
    }
}
