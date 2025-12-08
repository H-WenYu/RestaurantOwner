package com.restaurant.model;

/**
 * 餐桌类
 * 可升级
 */
public class Table {
    private int id;
    private int seats;          // 座位数
    private int level;          // 桌子等级 1-5
    private Customer customer;
    private boolean occupied;
    
    // 位置属性（用于服务员移动）
    private int posX, posY;

    // 升级价格
    private static final int[] UPGRADE_COSTS = {0, 100, 300, 600, 1000};

    public Table(int id, int seats) {
        this.id = id;
        this.seats = seats;
        this.level = 1;
        this.customer = null;
        this.occupied = false;
    }

    /**
     * 获取桌子效率加成（高级桌子让顾客更满意）
     */
    public double getSatisfactionBonus() {
        return 1.0 + (level - 1) * 0.1;
    }

    /**
     * 升级桌子
     */
    public boolean upgrade(int money) {
        if (level >= 5) return false;
        int cost = getUpgradeCost();
        if (money >= cost) {
            level++;
            return true;
        }
        return false;
    }

    /**
     * 获取升级费用
     */
    public int getUpgradeCost() {
        if (level >= 5) return -1;
        return UPGRADE_COSTS[level];
    }

    public void seatCustomer(Customer customer) {
        this.customer = customer;
        this.occupied = true;
        customer.sitDown(this);
    }

    public void clearTable() {
        this.customer = null;
        this.occupied = false;
    }

    public boolean isAvailable() {
        return !occupied;
    }

    // Getters and Setters
    public int getId() { return id; }
    public int getSeats() { return seats; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public Customer getCustomer() { return customer; }
    public boolean isOccupied() { return occupied; }
    
    // 位置相关
    public int getPosX() { return posX; }
    public int getPosY() { return posY; }
    public void setPosition(int x, int y) { this.posX = x; this.posY = y; }

    @Override
    public String toString() {
        String levelStr = level > 1 ? "★".repeat(level) : "";
        if (occupied && customer != null) {
            return String.format("桌%d%s[%d座] -> %s", id, levelStr, seats, customer.getName());
        } else {
            return String.format("桌%d%s[%d座] 空闲", id, levelStr, seats);
        }
    }
}
