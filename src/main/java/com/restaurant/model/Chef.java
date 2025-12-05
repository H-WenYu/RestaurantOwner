package com.restaurant.model;

/**
 * 厨师类
 */
public class Chef extends Employee {
    private int maxDishLevel;       // 能做的最高菜品等级
    private Order currentOrder;     // 当前正在做的订单
    private int cookTimer;          // 烹饪计时器
    private int totalDishesCooked;  // 累计做菜数量
    private Dish currentDish;       // 当前正在做的菜

    public Chef(String name) {
        super(name);
        this.maxDishLevel = 1;
        this.currentOrder = null;
        this.cookTimer = 0;
        this.totalDishesCooked = 0;
        this.currentDish = null;
    }

    @Override
    protected void onLevelUp() {
        // 每2级增加可做菜品等级
        if (level % 2 == 0) {
            maxDishLevel++;
        }
        System.out.println("🎉 " + name + " 升级到 Lv." + level + "！可制作 " + maxDishLevel + " 级菜品");
    }

    /**
     * 开始烹饪
     */
    public void startCooking(Order order, Dish dish) {
        this.currentOrder = order;
        this.currentDish = dish;
        this.cookTimer = getCookTime(dish);
        this.busy = true;
        consumeStamina(dish.getStaminaCost());
    }

    /**
     * 获取烹饪时间（受等级和效率影响）
     */
    public int getCookTime(Dish dish) {
        return Math.max(1, (int)(dish.getCookTime() / getEfficiency()));
    }

    /**
     * 完成烹饪
     */
    public void finishCooking() {
        if (currentDish != null) {
            totalDishesCooked++;
            gainExp(currentDish.getExpReward());
        }
        this.currentOrder = null;
        this.currentDish = null;
        this.cookTimer = 0;
        this.busy = false;
    }

    /**
     * 检查是否把菜做焦了
     */
    public boolean checkBurnDish() {
        return Math.random() < getErrorRate();
    }

    /**
     * 检查是否能做某道菜
     */
    public boolean canCook(Dish dish) {
        return dish.getLevel() <= maxDishLevel;
    }

    // Getters and Setters
    public int getMaxDishLevel() { return maxDishLevel; }
    public Order getCurrentOrder() { return currentOrder; }
    public int getCookTimer() { return cookTimer; }
    public void setCookTimer(int cookTimer) { this.cookTimer = cookTimer; }
    public int getTotalDishesCooked() { return totalDishesCooked; }
    public Dish getCurrentDish() { return currentDish; }

    @Override
    public String toString() {
        String cookStr = (currentDish != null) ? 
            " [烹饪:" + currentDish.getName() + " " + cookTimer + "s]" : "";
        return "👨‍🍳厨师 " + super.toString() + cookStr + " 累计做菜:" + totalDishesCooked;
    }
}

