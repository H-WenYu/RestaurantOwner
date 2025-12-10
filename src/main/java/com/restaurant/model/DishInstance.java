package com.restaurant.model;

/**
 * 菜品实例类 - 跟踪玩家已解锁菜品的运行时状态
 * 每个解锁的菜品有自己的等级和经验
 */
public class DishInstance {
    private Dish baseDish; // 基础菜品模板
    private int dishLevel; // 菜品等级 (从1开始)
    private int dishExp; // 菜品当前经验
    private int expToNextLevel; // 升级所需经验
    private boolean active; // 是否上架

    /**
     * 创建新的菜品实例（刚解锁时使用）
     */
    public DishInstance(Dish baseDish) {
        this.baseDish = baseDish;
        this.dishLevel = 1;
        this.dishExp = 0;
        this.expToNextLevel = getExpForLevel(2);
        this.active = false;
    }

    /**
     * 从存档恢复
     */
    public DishInstance(Dish baseDish, int level, int exp) {
        this.baseDish = baseDish;
        this.dishLevel = level;
        this.dishExp = exp;
        this.expToNextLevel = getExpForLevel(level + 1);
        this.active = false;
    }

    /**
     * 获取升到指定等级所需的经验
     */
    private int getExpForLevel(int level) {
        if (level <= 2)
            return 20;
        if (level <= 5)
            return 20 + (level - 2) * 15;
        if (level <= 10)
            return 65 + (level - 5) * 25;
        return 190 + (level - 10) * 40; // 10级以上
    }

    /**
     * 给菜品增加经验（顾客点餐后调用）
     * 
     * @return 是否升级了
     */
    public boolean addExp(int amount) {
        dishExp += amount;
        boolean leveledUp = false;

        while (dishExp >= expToNextLevel && dishLevel < 50) { // 最高50级
            dishExp -= expToNextLevel;
            dishLevel++;
            expToNextLevel = getExpForLevel(dishLevel + 1);
            leveledUp = true;
        }

        return leveledUp;
    }

    /**
     * 获取实际店铺经验（基础 + 等级加成）
     * 每级增加10%
     */
    public int getActualShopExp() {
        double bonus = 1.0 + (dishLevel - 1) * 0.1;
        return (int) (baseDish.getShopExp() * bonus);
    }

    /**
     * 获取实际售价（基础 + 等级加成）
     * 每级增加5%
     */
    public int getActualPrice() {
        double bonus = 1.0 + (dishLevel - 1) * 0.05;
        return (int) (baseDish.getPrice() * bonus);
    }

    /**
     * 获取实际厨师经验（基础 + 等级加成）
     * 每级增加10%
     */
    public int getActualChefExp() {
        double bonus = 1.0 + (dishLevel - 1) * 0.1;
        return (int) (baseDish.getChefExp() * bonus);
    }

    /**
     * 获取实际满意度（基础 + 等级加成）
     * 每级增加2
     */
    public int getActualSatisfaction() {
        return baseDish.getSatisfaction() + (dishLevel - 1) * 2;
    }

    // ========== Getters and Setters ==========
    public Dish getBaseDish() {
        return baseDish;
    }

    public String getName() {
        return baseDish.getName();
    }

    public int getDishLevel() {
        return dishLevel;
    }

    public int getDishExp() {
        return dishExp;
    }

    public int getExpToNextLevel() {
        return expToNextLevel;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // 便捷方法：获取基础属性
    public int getLevel() {
        return baseDish.getLevel();
    } // 解锁等级

    public int getCost() {
        return baseDish.getCost();
    }

    public int getCookTime() {
        return baseDish.getCookTime();
    }

    public int getStaminaCost() {
        return baseDish.getStaminaCost();
    }

    public int getBaseServings() {
        return baseDish.getBaseServings();
    }

    @Override
    public String toString() {
        String activeStr = active ? " [上架]" : "";
        return String.format("%s Lv.%d (%d/%d)%s $%d 利润$%d 店铺经验+%d",
                baseDish.getName(), dishLevel, dishExp, expToNextLevel, activeStr,
                getActualPrice(), getActualPrice() - getCost(), getActualShopExp());
    }
}
