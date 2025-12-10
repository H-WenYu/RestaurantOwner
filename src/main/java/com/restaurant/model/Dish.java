package com.restaurant.model;

/**
 * 菜品类
 * 包含菜品的基础属性模板
 */
public class Dish {
    private String name;
    private int price; // 售价
    private int cookTime; // 制作时间（秒）
    private int expReward; // 制作经验奖励（旧属性，保留兼容）
    private int satisfaction; // 满意度加成 (0-100)
    private int level; // 菜品等级（需要对应等级厨师）
    private int staminaCost; // 制作消耗的体力
    private int baseServings; // 基础份数（用于批量出餐或大份菜）
    private int cost; // 成本
    private int shopExp; // 顾客点后带给店铺的经验
    private int chefExp; // 厨师做完后带给厨师的经验

    /**
     * 完整构造函数（包含所有新属性）
     */
    public Dish(String name, int price, int cookTime, int expReward, int satisfaction,
            int level, int staminaCost, int baseServings, int cost, int shopExp, int chefExp) {
        this.name = name;
        this.price = price;
        this.cookTime = cookTime;
        this.expReward = expReward;
        this.satisfaction = satisfaction;
        this.level = level;
        this.staminaCost = staminaCost;
        this.baseServings = Math.max(1, baseServings);
        this.cost = cost;
        this.shopExp = shopExp;
        this.chefExp = chefExp;
    }

    /**
     * 兼容旧代码的构造函数（不含新属性）
     */
    public Dish(String name, int price, int cookTime, int expReward, int satisfaction, int level, int staminaCost) {
        this(name, price, cookTime, expReward, satisfaction, level, staminaCost, 1,
                price / 3, expReward, expReward / 2); // 默认：成本=售价/3，店铺经验=expReward，厨师经验=expReward/2
    }

    public Dish(String name, int price, int cookTime, int expReward, int satisfaction, int level, int staminaCost,
            int baseServings) {
        this(name, price, cookTime, expReward, satisfaction, level, staminaCost, baseServings,
                price / 3, expReward, expReward / 2);
    }

    // ========== 预设菜品工厂方法（包含完整属性） ==========
    // 格式: name, price, cookTime, expReward, satisfaction, level, stamina, servings,
    // cost, shopExp, chefExp

    public static Dish createEggFriedRice() {
        return new Dish("蛋炒饭", 15, 3, 5, 20, 1, 5, 1, 5, 5, 3);
    }

    public static Dish createTomatoEgg() {
        return new Dish("番茄炒蛋", 18, 4, 8, 25, 1, 6, 1, 6, 8, 4);
    }

    public static Dish createKungPaoChicken() {
        return new Dish("宫保鸡丁", 28, 6, 15, 40, 1, 8, 1, 10, 10, 6);
    }

    public static Dish createMaPoTofu() {
        return new Dish("麻婆豆腐", 22, 5, 12, 35, 1, 7, 1, 8, 12, 5);
    }

    public static Dish createSweetSourPork() {
        return new Dish("糖醋里脊", 35, 8, 20, 50, 2, 10, 1, 12, 15, 8);
    }

    public static Dish createBraisedPork() {
        return new Dish("红烧肉", 45, 10, 30, 60, 2, 12, 2, 18, 25, 12);
    }

    public static Dish createSteamedFish() {
        return new Dish("清蒸鱼", 58, 12, 35, 65, 2, 12, 2, 25, 20, 15);
    }

    public static Dish createPekingDuck() {
        return new Dish("北京烤鸭", 88, 15, 50, 80, 3, 15, 3, 35, 40, 25);
    }

    public static Dish createLobster() {
        return new Dish("龙虾", 168, 20, 80, 95, 4, 20, 2, 80, 30, 50);
    }

    public static Dish createWagyuSteak() {
        return new Dish("和牛牛排", 288, 25, 100, 100, 5, 25, 1, 150, 50, 80);
    }

    // ========== Getters ==========
    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getCookTime() {
        return cookTime;
    }

    public int getExpReward() {
        return expReward;
    }

    public int getSatisfaction() {
        return satisfaction;
    }

    public int getLevel() {
        return level;
    }

    public int getStaminaCost() {
        return staminaCost;
    }

    public int getBaseServings() {
        return baseServings;
    }

    public int getCost() {
        return cost;
    }

    public int getShopExp() {
        return shopExp;
    }

    public int getChefExp() {
        return chefExp;
    }

    /**
     * 计算利润（售价 - 成本）
     */
    public int getProfit() {
        return price - cost;
    }

    @Override
    public String toString() {
        return String.format("%s ($%d) [Lv.%d 制作%ds 满意度+%d 份数x%d 成本$%d]",
                name, price, level, cookTime, satisfaction, baseServings, cost);
    }
}
