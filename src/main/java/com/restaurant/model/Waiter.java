package com.restaurant.model;

/**
 * 服务员类
 */
public class Waiter extends Employee {
    private int maxTablesServed;  // 同时服务的最大桌数
    private int currentTables;    // 当前服务桌数
    private int totalTips;        // 累计小费
    private Customer servingCustomer; // 当前服务的顾客
    private Order carryingOrder;      // 正在端的菜
    private int taskTimer;            // 任务计时器
    private WaiterTask currentTask;   // 当前任务

    public enum WaiterTask {
        NONE,           // 无任务
        GREETING,       // 迎客
        TAKING_ORDER,   // 点单
        DELIVERING      // 上菜
    }

    public Waiter(String name) {
        super(name);
        this.maxTablesServed = 1;
        this.currentTables = 0;
        this.totalTips = 0;
        this.servingCustomer = null;
        this.carryingOrder = null;
        this.taskTimer = 0;
        this.currentTask = WaiterTask.NONE;
    }

    @Override
    protected void onLevelUp() {
        // 每3级增加一个可服务桌数
        if (level % 3 == 0) {
            maxTablesServed++;
        }
        System.out.println("🎉 " + name + " 升级到 Lv." + level + "！可同时服务 " + maxTablesServed + " 桌");
    }

    /**
     * 开始迎客任务
     */
    public void startGreeting(Customer customer) {
        this.servingCustomer = customer;
        this.currentTask = WaiterTask.GREETING;
        this.taskTimer = getGreetingTime();
        this.busy = true;
        consumeStamina(3);
    }

    /**
     * 开始点单任务
     */
    public void startTakingOrder() {
        this.currentTask = WaiterTask.TAKING_ORDER;
        this.taskTimer = getOrderTime();
        this.busy = true;
        consumeStamina(5);
    }

    /**
     * 开始上菜任务
     */
    public void startDelivering(Order order) {
        this.carryingOrder = order;
        this.currentTask = WaiterTask.DELIVERING;
        this.taskTimer = getDeliverTime();
        this.busy = true;
        consumeStamina(5);
    }

    /**
     * 完成当前任务
     */
    public void finishTask() {
        this.currentTask = WaiterTask.NONE;
        this.busy = false;
        this.carryingOrder = null;
    }

    /**
     * 获取迎客所需时间（秒）
     */
    public int getGreetingTime() {
        return Math.max(1, (int)(3 / getEfficiency()));
    }

    /**
     * 获取点单所需时间（秒）
     */
    public int getOrderTime() {
        return Math.max(1, (int)(5 / getEfficiency()));
    }

    /**
     * 获取上菜所需时间（秒）
     */
    public int getDeliverTime() {
        return Math.max(1, (int)(3 / getEfficiency()));
    }

    /**
     * 收取小费
     */
    public void receiveTip(int amount) {
        totalTips += amount;
        gainExp(amount / 2);
    }

    /**
     * 检查是否出错（摔盘子等）
     */
    public boolean checkError() {
        return Math.random() < getErrorRate();
    }

    // Getters and Setters
    public int getMaxTablesServed() { return maxTablesServed; }
    public int getCurrentTables() { return currentTables; }
    public void setCurrentTables(int currentTables) { this.currentTables = currentTables; }
    public int getTotalTips() { return totalTips; }
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
        return "👔服务员 " + super.toString() + " " + taskStr + " 累计小费:$" + totalTips;
    }
}

