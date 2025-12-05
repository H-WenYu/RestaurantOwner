package com.restaurant.model;

/**
 * 员工基类 - 所有员工的父类
 */
public abstract class Employee {
    protected String name;
    protected int level;           // 等级 1-10
    protected int exp;             // 当前经验值
    protected int expToNextLevel;  // 升级所需经验
    protected int proficiency;     // 熟练度 0-100
    protected int stamina;         // 体力值 0-100
    protected int maxStamina;      // 最大体力
    protected boolean tired;       // 是否疲劳
    protected boolean resting;     // 是否正在休息
    protected int restTimer;       // 休息计时器（秒）
    protected boolean busy;        // 是否正在工作

    public Employee(String name) {
        this.name = name;
        this.level = 1;
        this.exp = 0;
        this.expToNextLevel = 100;
        this.proficiency = 10;
        this.stamina = 100;
        this.maxStamina = 100;
        this.tired = false;
        this.resting = false;
        this.restTimer = 0;
        this.busy = false;
    }

    /**
     * 消耗体力
     */
    public void consumeStamina(int amount) {
        // 等级越高，体力消耗越少
        int actualConsume = Math.max(1, amount - (level - 1));
        stamina = Math.max(0, stamina - actualConsume);
        
        if (stamina < 30) {
            tired = true;
        }
        
        if (stamina <= 0) {
            startResting();
        }
    }

    /**
     * 开始休息
     */
    public void startResting() {
        resting = true;
        busy = false;
        restTimer = 30; // 默认休息30秒
    }

    /**
     * 休息恢复（每tick调用）
     */
    public void rest() {
        if (resting) {
            restTimer--;
            stamina = Math.min(maxStamina, stamina + 5); // 每秒恢复5点体力
            
            if (restTimer <= 0 || stamina >= maxStamina) {
                resting = false;
                tired = false;
                stamina = maxStamina;
            }
        }
    }

    /**
     * 获得经验值
     */
    public void gainExp(int amount) {
        exp += amount;
        
        while (exp >= expToNextLevel && level < 10) {
            exp -= expToNextLevel;
            levelUp();
        }
    }

    /**
     * 升级
     */
    protected void levelUp() {
        level++;
        expToNextLevel = (int) (expToNextLevel * 1.5);
        proficiency = Math.min(100, proficiency + 10);
        maxStamina += 10;
        stamina = maxStamina;
        onLevelUp();
    }

    /**
     * 升级时的额外效果（子类重写）
     */
    protected abstract void onLevelUp();

    /**
     * 获取工作效率（受疲劳影响）
     */
    public double getEfficiency() {
        double baseEfficiency = 0.5 + (level * 0.05) + (proficiency * 0.005);
        if (tired) {
            baseEfficiency *= 0.5; // 疲劳时效率减半
        }
        return Math.min(1.5, baseEfficiency);
    }

    /**
     * 是否可以工作
     */
    public boolean canWork() {
        return !resting && !busy && stamina > 0;
    }

    /**
     * 计算出错概率（0-1）
     */
    public double getErrorRate() {
        double baseRate = 0.3 - (level * 0.02) - (proficiency * 0.002);
        if (tired) {
            baseRate *= 2; // 疲劳时出错率翻倍
        }
        return Math.max(0.01, Math.min(0.5, baseRate));
    }

    // Getters and Setters
    public String getName() { return name; }
    public int getLevel() { return level; }
    public int getExp() { return exp; }
    public int getExpToNextLevel() { return expToNextLevel; }
    public int getProficiency() { return proficiency; }
    public int getStamina() { return stamina; }
    public int getMaxStamina() { return maxStamina; }
    public boolean isTired() { return tired; }
    public boolean isResting() { return resting; }
    public boolean isBusy() { return busy; }
    public void setBusy(boolean busy) { this.busy = busy; }
    public int getRestTimer() { return restTimer; }
    public void setRestTimer(int restTimer) { this.restTimer = restTimer; }

    @Override
    public String toString() {
        String status = resting ? "💤休息中" : (busy ? "🔥工作中" : (tired ? "😓疲劳" : "✅空闲"));
        return String.format("%s Lv.%d [体力:%d/%d] [经验:%d/%d] %s", 
                name, level, stamina, maxStamina, exp, expToNextLevel, status);
    }
}

