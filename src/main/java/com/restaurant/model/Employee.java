package com.restaurant.model;

/**
 * 员工基类 - 所有员工的父类
 * 员工等级：1=实习, 2=普通, 3=中级, 4=高级, 5=特技
 */
public abstract class Employee {
    // 员工等级名称
    public static final String[] TIER_NAMES = {"", "实习", "普通", "中级", "高级", "特技"};
    
    protected String name;
    protected int level;           // 员工等级 1-100
    protected int exp;             // 当前经验值
    protected int expToNextLevel;  // 升级所需经验
    protected int proficiency;     // 熟练度 0-100
    protected int stamina;         // 体力值 0-100
    protected int maxStamina;      // 最大体力
    protected boolean tired;       // 是否疲劳
    protected boolean resting;     // 是否正在休息
    protected int restTimer;       // 休息计时器（秒）
    protected boolean busy;        // 是否正在工作
    
    // 员工品质等级 (1=实习, 2=普通, 3=中级, 4=高级, 5=特技)
    protected int employeeTier;
    
    // 基础属性（各员工子类有自己的具体属性）
    protected int speedSkill;      // 速度 1-100
    protected int charmSkill;      // 魅力 1-100

    public Employee(String name, int tier) {
        this.name = name;
        this.employeeTier = tier;
        this.level = 1;
        this.exp = 0;
        this.expToNextLevel = 50;
        this.proficiency = 10 + tier * 5;
        this.stamina = 80 + tier * 5;
        this.maxStamina = 80 + tier * 5;
        this.tired = false;
        this.resting = false;
        this.restTimer = 0;
        this.busy = false;
        
        // 基础属性根据等级随机
        this.speedSkill = 10 + tier * 10 + (int)(Math.random() * 20);
        this.charmSkill = 10 + tier * 10 + (int)(Math.random() * 20);
    }

    /**
     * 恢复状态（从存档）
     */
    public void restoreState(int level, int exp, int expToNextLevel, 
                            int proficiency, int stamina, int maxStamina) {
        this.level = level;
        this.exp = exp;
        this.expToNextLevel = expToNextLevel;
        this.proficiency = proficiency;
        this.stamina = stamina;
        this.maxStamina = maxStamina;
    }

    /**
     * 消耗体力
     */
    public void consumeStamina(int amount) {
        int actualConsume = Math.max(1, amount - (level / 10) - (speedSkill / 20));
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
        restTimer = Math.max(15, 30 - level / 5);
    }

    /**
     * 休息恢复（每tick调用）
     */
    public void rest() {
        if (resting) {
            restTimer--;
            stamina = Math.min(maxStamina, stamina + 5);
            
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
        
        while (exp >= expToNextLevel && level < 100) {
            exp -= expToNextLevel;
            levelUp();
        }
    }

    /**
     * 升级
     */
    protected void levelUp() {
        level++;
        expToNextLevel = 50 + level * 10;
        proficiency = Math.min(100, proficiency + 2);
        maxStamina = Math.min(150, maxStamina + 2);
        stamina = maxStamina;
        onLevelUp();
    }

    /**
     * 升级时的额外效果（子类重写）
     */
    protected abstract void onLevelUp();

    /**
     * 获取工作效率（受疲劳和属性影响）
     */
    public double getEfficiency() {
        double baseEfficiency = 0.5 + (level * 0.005) + (proficiency * 0.003) + (speedSkill * 0.003);
        if (tired) {
            baseEfficiency *= 0.5;
        }
        return Math.min(2.0, baseEfficiency);
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
        double baseRate = 0.25 - (level * 0.002) - (proficiency * 0.002);
        if (tired) {
            baseRate *= 2;
        }
        return Math.max(0.01, Math.min(0.4, baseRate));
    }

    /**
     * 获取解雇赔偿金
     */
    public int getFireCompensation() {
        int base = employeeTier * 50;
        return base + level * 5;
    }

    /**
     * 获取等级名称
     */
    public String getTierName() {
        return TIER_NAMES[Math.min(employeeTier, 5)];
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
    public int getEmployeeTier() { return employeeTier; }
    public int getSpeedSkill() { return speedSkill; }
    public int getCharmSkill() { return charmSkill; }

    @Override
    public String toString() {
        String status = resting ? "💤休息" : (busy ? "🔥工作" : (tired ? "😓疲劳" : "✅空闲"));
        return String.format("[%s]%s Lv.%d 体力:%d/%d %s", 
                getTierName(), name, level, stamina, maxStamina, status);
    }
}
