package com.restaurant.model;

/**
 * 菜品类
 */
public class Dish {
    private String name;
    private int price;          // 价格
    private int cookTime;       // 制作时间（秒）
    private int expReward;      // 制作经验奖励
    private int satisfaction;   // 满意度加成 (0-100)
    private int level;          // 菜品等级（需要对应等级厨师）
    private int staminaCost;    // 制作消耗的体力
    private int baseServings;   // 基础份数（用于批量出餐或大份菜）

    public Dish(String name, int price, int cookTime, int expReward, int satisfaction, int level, int staminaCost) {
        this(name, price, cookTime, expReward, satisfaction, level, staminaCost, 1);
    }

    public Dish(String name, int price, int cookTime, int expReward, int satisfaction, int level, int staminaCost, int baseServings) {
        this.name = name;
        this.price = price;
        this.cookTime = cookTime;
        this.expReward = expReward;
        this.satisfaction = satisfaction;
        this.level = level;
        this.staminaCost = staminaCost;
        this.baseServings = Math.max(1, baseServings);
    }

    // 预设菜品工厂方法
    public static Dish createEggFriedRice() {
        return new Dish("蛋炒饭", 15, 3, 5, 20, 1, 5);
    }

    public static Dish createTomatoEgg() {
        return new Dish("番茄炒蛋", 18, 4, 8, 25, 1, 6);
    }

    public static Dish createKungPaoChicken() {
        return new Dish("宫保鸡丁", 28, 6, 15, 40, 1, 8);
    }

    public static Dish createSweetSourPork() {
        return new Dish("糖醋里脊", 35, 8, 20, 50, 2, 10);
    }

    public static Dish createMaPoTofu() {
        return new Dish("麻婆豆腐", 22, 5, 12, 35, 1, 7);
    }

    public static Dish createBraisedPork() {
        return new Dish("红烧肉", 45, 10, 30, 60, 2, 12);
    }

    public static Dish createPekingDuck() {
        return new Dish("北京烤鸭", 88, 15, 50, 80, 3, 15);
    }

    public static Dish createSteamedFish() {
        return new Dish("清蒸鱼", 58, 12, 35, 65, 2, 12);
    }

    public static Dish createLobster() {
        return new Dish("龙虾", 168, 20, 80, 95, 4, 20);
    }

    public static Dish createWagyuSteak() {
        return new Dish("和牛牛排", 288, 25, 100, 100, 5, 25);
    }

    // Getters
    public String getName() { return name; }
    public int getPrice() { return price; }
    public int getCookTime() { return cookTime; }
    public int getExpReward() { return expReward; }
    public int getSatisfaction() { return satisfaction; }
    public int getLevel() { return level; }
    public int getStaminaCost() { return staminaCost; }
    public int getBaseServings() { return baseServings; }

    @Override
    public String toString() {
        return String.format("%s ($%d) [Lv.%d 制作%ds 满意度+%d 份数x%d]",
                name, price, level, cookTime, satisfaction, baseServings);
    }
}

