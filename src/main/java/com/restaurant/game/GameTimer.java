package com.restaurant.game;

/**
 * 游戏计时器 - 控制游戏主循环
 */
public class GameTimer implements Runnable {
    private Hotel hotel;
    private boolean running;
    private boolean paused;
    private int tickInterval;  // 毫秒
    private GameTickListener listener;

    public interface GameTickListener {
        void onTick(Hotel hotel);
    }

    public GameTimer(Hotel hotel) {
        this.hotel = hotel;
        this.running = false;
        this.paused = false;
        this.tickInterval = 1000; // 默认1秒一tick
    }

    public void setTickListener(GameTickListener listener) {
        this.listener = listener;
    }

    /**
     * 开始游戏
     */
    public void start() {
        if (!running) {
            running = true;
            new Thread(this).start();
        }
    }

    /**
     * 停止游戏
     */
    public void stop() {
        running = false;
    }

    /**
     * 暂停/继续
     */
    public void togglePause() {
        paused = !paused;
    }

    /**
     * 设置游戏速度
     * @param speed 1=正常, 2=2倍速, 4=4倍速
     */
    public void setSpeed(int speed) {
        this.tickInterval = 1000 / Math.max(1, speed);
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(tickInterval);
                
                if (!paused) {
                    hotel.tick();
                    
                    if (listener != null) {
                        listener.onTick(hotel);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    // Getters
    public boolean isRunning() { return running; }
    public boolean isPaused() { return paused; }
    public int getTickInterval() { return tickInterval; }
    public Hotel getHotel() { return hotel; }
}

