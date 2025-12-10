package com.restaurant.session;

import com.restaurant.game.Hotel;

/**
 * 游戏会话 - 每个用户一个实例
 */
public class GameSession {

    private final String oderId; // 微信 openId
    private Hotel hotel; // 游戏实例
    private long lastActiveTime; // 最后活跃时间
    private boolean running; // 是否正在营业（tick中）

    public GameSession(String oderId) {
        this.oderId = oderId;
        this.hotel = null;
        this.lastActiveTime = System.currentTimeMillis();
        this.running = false;
    }

    /**
     * 创建新游戏
     */
    public void newGame(String hotelName) {
        this.hotel = new Hotel(hotelName, true);
        this.lastActiveTime = System.currentTimeMillis();
        this.running = false;
    }

    /**
     * 设置游戏实例（加载存档时用）
     */
    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
        this.lastActiveTime = System.currentTimeMillis();
    }

    /**
     * 推进游戏一帧
     */
    public void tick() {
        if (hotel != null && running) {
            hotel.tick();
        }
        this.lastActiveTime = System.currentTimeMillis();
    }

    /**
     * 开始营业
     */
    public void startRunning() {
        this.running = true;
        this.lastActiveTime = System.currentTimeMillis();
    }

    /**
     * 暂停营业
     */
    public void stopRunning() {
        this.running = false;
    }

    /**
     * 检查会话是否超时
     */
    public boolean isExpired(long timeoutMs) {
        return System.currentTimeMillis() - lastActiveTime > timeoutMs;
    }

    /**
     * 更新活跃时间
     */
    public void touch() {
        this.lastActiveTime = System.currentTimeMillis();
    }

    // Getters
    public String getOpenId() {
        return oderId;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public boolean isRunning() {
        return running;
    }

    public long getLastActiveTime() {
        return lastActiveTime;
    }

    public boolean hasGame() {
        return hotel != null;
    }
}
