package com.restaurant.model;

/**
 * 服务员类
 * 属性：服务、速度、魅力
 */
public class Waiter extends Employee {
    private int serviceSkill;     // 服务技能 1-100
    private int maxTablesServed;  // 同时服务的最大桌数
    private int currentTables;    // 当前服务桌数
    private int totalTips;        // 累计小费
    private Customer servingCustomer;
    private Order carryingOrder;
    private Dish carryingDish;    // 正在运送的菜品
    private int taskTimer;
    private WaiterTask currentTask;

    public enum WaiterTask {
        NONE, GREETING, TAKING_ORDER, DELIVERING
    }

    /**
     * 创建服务员
     * @param name 名字
     * @param tier 品质等级 (1=实习, 2=普通, 3=中级, 4=高级, 5=特技)
     * @param service 服务技能
     * @param speed 速度技能
     * @param charm 魅力技能
     */
    public Waiter(String name, int tier, int service, int speed, int charm) {
        super(name, tier);
        this.serviceSkill = service;
        this.speedSkill = speed;
        this.charmSkill = charm;
        this.maxTablesServed = 1 + tier / 2;
        this.currentTables = 0;
        this.totalTips = 0;
        this.servingCustomer = null;
        this.carryingOrder = null;
        this.taskTimer = 0;
        this.currentTask = WaiterTask.NONE;
    }

    /**
     * 简单构造（用于兼容旧代码）
     */
    public Waiter(String name) {
        this(name, 1, 20, 20, 20);
    }

    /**
     * 生成随机服务员
     */
    public static Waiter generateRandom(int tier, int shopLevel) {
        String[] names = {"小红", "小芳", "小丽", "小美", "小花", "小玉", "小琴", "小雪", 
                         "阿强", "阿明", "阿杰", "阿伟", "小李", "小王", "小张", "小刘"};
        String name = names[(int)(Math.random() * names.length)] + "#" + (int)(Math.random() * 1000);
        
        // 根据等级和店铺等级生成属性
        int baseSkill = tier * 15 + shopLevel;
        int service = baseSkill + (int)(Math.random() * 25) - 10;
        int speed = baseSkill + (int)(Math.random() * 25) - 10;
        int charm = baseSkill + (int)(Math.random() * 25) - 10;
        
        service = Math.max(5, Math.min(100, service));
        speed = Math.max(5, Math.min(100, speed));
        charm = Math.max(5, Math.min(100, charm));
        
        return new Waiter(name, tier, service, speed, charm);
    }

    /**
     * 计算招聘价格
     */
    public int getHirePrice() {
        int basePrice = employeeTier * 100;
        int skillBonus = (serviceSkill + speedSkill + charmSkill) / 3;
        return basePrice + skillBonus * 2;
    }

    @Override
    protected void onLevelUp() {
        // 每10级增加一个可服务桌数
        if (level % 10 == 0) {
            maxTablesServed = Math.min(5, maxTablesServed + 1);
        }
        // 技能小幅提升
        serviceSkill = Math.min(100, serviceSkill + 1);
        System.out.println("🎉 " + name + " 升级到 Lv." + level);
    }

    public void startGreeting(Customer customer) {
        this.servingCustomer = customer;
        this.currentTask = WaiterTask.GREETING;
        this.taskTimer = getGreetingTime();
        this.busy = true;
        consumeStamina(3);
    }

    public void startTakingOrder() {
        this.currentTask = WaiterTask.TAKING_ORDER;
        this.taskTimer = getOrderTime();
        this.busy = true;
        consumeStamina(5);
    }

    public void startDelivering(Order order, Dish dish) {
        this.carryingOrder = order;
        this.carryingDish = dish;
        this.currentTask = WaiterTask.DELIVERING;
        this.taskTimer = getDeliverTime();
        this.busy = true;
        consumeStamina(5);
    }
    
    public Dish getCarryingDish() { return carryingDish; }

    public void finishTask() {
        this.currentTask = WaiterTask.NONE;
        this.busy = false;
        this.carryingOrder = null;
        this.carryingDish = null;
    }

    public int getGreetingTime() {
        return Math.max(1, (int)(4 / getEfficiency()));
    }

    public int getOrderTime() {
        return Math.max(1, (int)(6 / getEfficiency()));
    }

    public int getDeliverTime() {
        return Math.max(1, (int)(4 / getEfficiency()));
    }

    /**
     * 计算小费加成（魅力影响）
     */
    public double getTipBonus() {
        return 1.0 + charmSkill * 0.01;
    }

    public void receiveTip(int amount) {
        int actualTip = (int)(amount * getTipBonus());
        totalTips += actualTip;
        gainExp(actualTip / 2);
    }

    /**
     * 检查是否出错（摔盘子等）
     * 前期基本不会发生
     */
    public boolean checkError() {
        // 等级低于30基本不会出错
        if (level < 30) {
            return false;  // 前期不会摔盘子
        }
        // 30级以上有极小概率
        double errorRate = 0.02 - (serviceSkill * 0.0005);
        return Math.random() < Math.max(0.005, errorRate);
    }

    @Override
    public double getEfficiency() {
        double base = super.getEfficiency();
        return base + speedSkill * 0.005;
    }

    // Getters and Setters
    public int getServiceSkill() { return serviceSkill; }
    public int getMaxTablesServed() { return maxTablesServed; }
    public int getCurrentTables() { return currentTables; }
    public void setCurrentTables(int currentTables) { this.currentTables = currentTables; }
    public int getTotalTips() { return totalTips; }
    public void setTotalTips(int totalTips) { this.totalTips = totalTips; }
    public Customer getServingCustomer() { return servingCustomer; }
    public void setServingCustomer(Customer customer) { this.servingCustomer = customer; }
    public Order getCarryingOrder() { return carryingOrder; }
    public int getTaskTimer() { return taskTimer; }
    public void setTaskTimer(int taskTimer) { this.taskTimer = taskTimer; }
    public WaiterTask getCurrentTask() { return currentTask; }
    public void setCurrentTask(WaiterTask task) { this.currentTask = task; }

    @Override
    public String toString() {
        String taskStr = "";
        switch (currentTask) {
            case GREETING: taskStr = "[迎客" + taskTimer + "s]"; break;
            case TAKING_ORDER: taskStr = "[点单" + taskTimer + "s]"; break;
            case DELIVERING: taskStr = "[上菜" + taskTimer + "s]"; break;
            default: break;
        }
        return "👔" + super.toString() + " " + taskStr + 
               String.format(" [服务:%d 速度:%d 魅力:%d]", serviceSkill, speedSkill, charmSkill);
    }
}
