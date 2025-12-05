package com.restaurant.model;

/**
 * 厨师类
 * 属性：厨艺、速度、魅力
 */
public class Chef extends Employee {
    private int cookingSkill;       // 厨艺技能 1-100
    private int maxDishLevel;       // 能做的最高菜品等级
    private Order currentOrder;
    private int cookTimer;
    private int totalDishesCooked;
    private Dish currentDish;

    /**
     * 创建厨师
     * @param name 名字
     * @param tier 品质等级 (1=实习, 2=普通, 3=中级, 4=高级, 5=特技)
     * @param cooking 厨艺技能
     * @param speed 速度技能
     * @param charm 魅力技能
     */
    public Chef(String name, int tier, int cooking, int speed, int charm) {
        super(name, tier);
        this.cookingSkill = cooking;
        this.speedSkill = speed;
        this.charmSkill = charm;
        this.maxDishLevel = tier;
        this.currentOrder = null;
        this.cookTimer = 0;
        this.totalDishesCooked = 0;
        this.currentDish = null;
    }

    /**
     * 简单构造（用于兼容旧代码）
     */
    public Chef(String name) {
        this(name, 1, 20, 20, 20);
    }

    /**
     * 生成随机厨师
     */
    public static Chef generateRandom(int tier, int shopLevel) {
        String[] names = {"老王", "老李", "老张", "老刘", "阿强", "大厨", "小厨", 
                         "铁柱", "二蛋", "狗剩", "建国", "国强", "志明", "伟哥"};
        String name = names[(int)(Math.random() * names.length)] + "#" + (int)(Math.random() * 1000);
        
        // 根据等级和店铺等级生成属性
        int baseSkill = tier * 15 + shopLevel;
        int cooking = baseSkill + (int)(Math.random() * 25) - 10;
        int speed = baseSkill + (int)(Math.random() * 25) - 10;
        int charm = baseSkill + (int)(Math.random() * 25) - 10;
        
        cooking = Math.max(5, Math.min(100, cooking));
        speed = Math.max(5, Math.min(100, speed));
        charm = Math.max(5, Math.min(100, charm));
        
        return new Chef(name, tier, cooking, speed, charm);
    }

    /**
     * 计算招聘价格
     */
    public int getHirePrice() {
        int basePrice = employeeTier * 150;
        int skillBonus = (cookingSkill + speedSkill + charmSkill) / 3;
        return basePrice + skillBonus * 3;
    }

    @Override
    protected void onLevelUp() {
        // 每15级增加可做菜品等级
        if (level % 15 == 0) {
            maxDishLevel = Math.min(10, maxDishLevel + 1);
        }
        // 技能小幅提升
        cookingSkill = Math.min(100, cookingSkill + 1);
        System.out.println("🎉 " + name + " 升级到 Lv." + level);
    }

    public void startCooking(Order order, Dish dish) {
        this.currentOrder = order;
        this.currentDish = dish;
        this.cookTimer = getCookTime(dish);
        this.busy = true;
        consumeStamina(dish.getStaminaCost());
    }

    /**
     * 获取烹饪时间（受厨艺和速度影响）
     * 大幅加快做菜速度
     */
    public int getCookTime(Dish dish) {
        double efficiency = getEfficiency() + cookingSkill * 0.01 + speedSkill * 0.01;
        // 基础时间减半，效率再加成
        int baseTime = Math.max(1, dish.getCookTime() / 2);
        return Math.max(1, (int)(baseTime / efficiency));
    }

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
     * 检查是否把菜做焦了（厨艺影响）
     * 前期基本不会发生
     */
    public boolean checkBurnDish() {
        // 等级低于30基本不会做焦
        if (level < 30) {
            return false;  // 前期不会做焦
        }
        // 30级以上有极小概率
        double burnRate = 0.02 - (cookingSkill * 0.0005);
        return Math.random() < Math.max(0.005, burnRate);
    }

    /**
     * 检查菜品质量加成（厨艺和魅力影响）
     */
    public double getQualityBonus() {
        return 1.0 + (cookingSkill + charmSkill) * 0.005;
    }

    public boolean canCook(Dish dish) {
        return dish.getLevel() <= maxDishLevel;
    }

    @Override
    public double getEfficiency() {
        double base = super.getEfficiency();
        return base + speedSkill * 0.005;
    }

    // Getters and Setters
    public int getCookingSkill() { return cookingSkill; }
    public int getMaxDishLevel() { return maxDishLevel; }
    public void setMaxDishLevel(int maxDishLevel) { this.maxDishLevel = maxDishLevel; }
    public Order getCurrentOrder() { return currentOrder; }
    public int getCookTimer() { return cookTimer; }
    public void setCookTimer(int cookTimer) { this.cookTimer = cookTimer; }
    public int getTotalDishesCooked() { return totalDishesCooked; }
    public void setTotalDishesCooked(int totalDishesCooked) { this.totalDishesCooked = totalDishesCooked; }
    public Dish getCurrentDish() { return currentDish; }

    @Override
    public String toString() {
        String cookStr = (currentDish != null) ? 
            " [做:" + currentDish.getName() + " " + cookTimer + "s]" : "";
        return "👨‍🍳" + super.toString() + cookStr + 
               String.format(" [厨艺:%d 速度:%d 魅力:%d]", cookingSkill, speedSkill, charmSkill);
    }
}
