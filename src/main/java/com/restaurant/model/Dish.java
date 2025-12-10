package com.restaurant.model;

/**
 * 菜品类
 */
public class Dish {
    private String name;
    private int price;          // 基础售价
    private int cost;           // 成本
    private int cookTime;       // 制作时间（秒）
    private int chefExpReward;  // 厨师经验奖励
    private int shopExpReward;  // 店铺经验奖励
    private int dishExpGain;    // 菜品自身获得的经验
    private int dishExp;        // 当前菜品经验
    private int dishLevel;      // 菜品等级（影响售价与经验产出）
    private int expToNextLevel; // 下一等级所需经验
    private int satisfaction;   // 满意度加成 (0-100)
    private int level;          // 菜品等级（需要对应等级厨师）
    private int staminaCost;    // 制作消耗的体力
    private int baseServings;   // 基础份数（用于批量出餐或大份菜）
    private int requiredCookingSkill; // 推荐厨艺下限

    public Dish(String name, int price, int cost, int cookTime, int chefExpReward, int shopExpReward, int dishExpGain, int satisfaction, int level, int staminaCost) {
        this(name, price, cost, cookTime, chefExpReward, shopExpReward, dishExpGain, satisfaction, level, staminaCost, 1);
    }

    public Dish(String name, int price, int cost, int cookTime, int chefExpReward, int shopExpReward, int dishExpGain, int satisfaction, int level, int staminaCost, int baseServings) {
        this.name = name;
        this.price = price;
        this.cost = cost;
        this.cookTime = cookTime;
        this.chefExpReward = chefExpReward;
        this.shopExpReward = shopExpReward;
        this.dishExpGain = dishExpGain;
        this.dishExp = 0;
        this.dishLevel = 1;
        this.expToNextLevel = 50;
        this.satisfaction = satisfaction;
        this.level = level;
        this.staminaCost = staminaCost;
        this.baseServings = Math.max(1, baseServings);
        this.requiredCookingSkill = Math.max(10, level * 15);
    }

    // 预设菜品工厂方法
    public static Dish createEggFriedRice() {
        return new Dish("蛋炒饭", 15, 6, 3, 5, 5, 3, 20, 1, 5);
    }

    public static Dish createTomatoEgg() {
        return new Dish("番茄炒蛋", 18, 8, 4, 6, 7, 4, 25, 1, 6);
    }

    public static Dish createKungPaoChicken() {
        return new Dish("宫保鸡丁", 28, 12, 6, 10, 12, 6, 40, 1, 8);
    }

    public static Dish createSweetSourPork() {
        return new Dish("糖醋里脊", 35, 15, 8, 12, 14, 8, 50, 2, 10);
    }

    public static Dish createMaPoTofu() {
        return new Dish("麻婆豆腐", 22, 10, 5, 9, 10, 5, 35, 1, 7);
    }

    public static Dish createBraisedPork() {
        return new Dish("红烧肉", 45, 20, 10, 16, 18, 9, 60, 2, 12);
    }

    public static Dish createPekingDuck() {
        return new Dish("北京烤鸭", 88, 40, 15, 24, 28, 12, 80, 3, 15);
    }

    public static Dish createSteamedFish() {
        return new Dish("清蒸鱼", 58, 24, 12, 18, 20, 10, 65, 2, 12);
    }

    public static Dish createLobster() {
        return new Dish("龙虾", 168, 70, 20, 35, 40, 18, 95, 4, 20);
    }

    public static Dish createWagyuSteak() {
        return new Dish("和牛牛排", 288, 120, 25, 45, 50, 20, 100, 5, 25);
    }

    // Getters
    public String getName() { return name; }
    public int getPrice() { return price; }
    public int getSalePrice() { return (int) Math.round(price * (1 + 0.08 * (dishLevel - 1))); }
    public int getCost() { return cost; }
    public int getCookTime() { return cookTime; }
    public int getChefExpReward() { return (int) Math.round(chefExpReward * (1 + 0.05 * (dishLevel - 1))); }
    public int getShopExpReward() { return (int) Math.round(shopExpReward * (1 + 0.05 * (dishLevel - 1))); }
    public int getDishExpGain() { return dishExpGain; }
    public int getDishExp() { return dishExp; }
    public int getDishLevel() { return dishLevel; }
    public int getExpToNextLevel() { return expToNextLevel; }
    public int getSatisfaction() { return satisfaction; }
    public int getLevel() { return level; }
    public int getStaminaCost() { return staminaCost; }
    public int getBaseServings() { return baseServings; }
    public int getRequiredCookingSkill() { return requiredCookingSkill; }

    public void gainDishExp(int amount) {
        this.dishExp += Math.max(0, amount);
        while (dishExp >= expToNextLevel) {
            dishExp -= expToNextLevel;
            dishLevel++;
            expToNextLevel = Math.min(500, expToNextLevel + 25);
        }
    }

    public void setDishLevel(int dishLevel) {
        this.dishLevel = Math.max(1, dishLevel);
    }

    public void setDishExp(int dishExp) {
        this.dishExp = Math.max(0, dishExp);
    }

    @Override
    public String toString() {
        return String.format("%s ($%d | 成本%d) [菜Lv.%d/厨Lv.%d 制作%ds 满意度+%d 份数x%d]",
                name, getSalePrice(), cost, dishLevel, level, cookTime, satisfaction, baseServings);
    }
}
