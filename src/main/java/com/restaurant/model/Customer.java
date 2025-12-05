package com.restaurant.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 顾客类
 */
public class Customer {
    private static int idCounter = 0;
    private static final String[] NAMES = {
        "张三", "李四", "王五", "赵六", "钱七", "孙八", 
        "周九", "吴十", "郑某", "冯某", "陈某", "褚某",
        "小明", "小红", "小刚", "老王", "老李", "老张"
    };
    private static final Random random = new Random();

    private int id;
    private String name;
    private int patience;       // 耐心值 (0-100)
    private int maxPatience;    // 最大耐心
    private int hunger;         // 饥饿度 (0-100)
    private int money;          // 携带金钱
    private List<Dish> orders;  // 点的菜
    private CustomerState state;
    private Table table;        // 坐的桌子
    private int totalSatisfaction; // 总满意度
    private int dishesReceived;    // 已上的菜数量
    private int eatingTimer;       // 吃饭计时器

    public enum CustomerState {
        WAITING_SEAT,    // 等待入座
        WAITING_ORDER,   // 等待点单
        WAITING_FOOD,    // 等待上菜
        EATING,          // 正在吃饭
        FINISHED,        // 吃完了
        LEFT_ANGRY,      // 生气离开
        LEFT_HAPPY       // 满意离开
    }

    public Customer() {
        this.id = ++idCounter;
        this.name = NAMES[random.nextInt(NAMES.length)] + "#" + id;
        this.maxPatience = 30 + random.nextInt(40); // 30-70
        this.patience = maxPatience;
        this.hunger = random.nextInt(30); // 初始饥饿度0-30
        this.money = 100 + random.nextInt(400); // 100-500
        this.orders = new ArrayList<>();
        this.state = CustomerState.WAITING_SEAT;
        this.table = null;
        this.totalSatisfaction = 0;
        this.dishesReceived = 0;
        this.eatingTimer = 0;
    }

    /**
     * 每tick更新
     */
    public void tick() {
        if (state == CustomerState.WAITING_SEAT || 
            state == CustomerState.WAITING_ORDER || 
            state == CustomerState.WAITING_FOOD) {
            patience--;
            hunger++;
            
            if (patience <= 0) {
                state = CustomerState.LEFT_ANGRY;
            }
        } else if (state == CustomerState.EATING) {
            eatingTimer--;
            if (eatingTimer <= 0) {
                state = CustomerState.FINISHED;
            }
        }
    }

    /**
     * 入座
     */
    public void sitDown(Table table) {
        this.table = table;
        this.state = CustomerState.WAITING_ORDER;
    }

    /**
     * 点菜
     */
    public void orderDishes(List<Dish> menu) {
        // 随机点1-3道菜，但不能超过携带的钱
        int dishCount = 1 + random.nextInt(3);
        int remainingMoney = money;
        
        List<Dish> availableDishes = new ArrayList<>(menu);
        
        for (int i = 0; i < dishCount && !availableDishes.isEmpty(); i++) {
            // 筛选买得起的菜
            List<Dish> affordable = new ArrayList<>();
            for (Dish d : availableDishes) {
                if (d.getPrice() <= remainingMoney) {
                    affordable.add(d);
                }
            }
            
            if (affordable.isEmpty()) break;
            
            Dish chosen = affordable.get(random.nextInt(affordable.size()));
            orders.add(chosen);
            remainingMoney -= chosen.getPrice();
        }
        
        this.state = CustomerState.WAITING_FOOD;
    }

    /**
     * 收到一道菜
     */
    public void receiveDish(Dish dish) {
        totalSatisfaction += dish.getSatisfaction();
        dishesReceived++;
        
        if (dishesReceived >= orders.size()) {
            // 所有菜都上齐了，开始吃饭
            state = CustomerState.EATING;
            eatingTimer = 5 + orders.size() * 2; // 吃饭时间
        }
    }

    /**
     * 计算总账单
     */
    public int calculateBill() {
        int total = 0;
        for (Dish d : orders) {
            total += d.getPrice();
        }
        return total;
    }

    /**
     * 计算小费（基于满意度）
     */
    public int calculateTip() {
        if (orders.isEmpty()) return 0;
        int avgSatisfaction = totalSatisfaction / orders.size();
        int bill = calculateBill();
        return (int)(bill * avgSatisfaction * 0.002); // 满意度越高小费越多
    }

    /**
     * 获取最终评价对声望的影响
     */
    public int getReputationEffect() {
        if (state == CustomerState.LEFT_ANGRY) {
            return -10 - (maxPatience - patience) / 5;
        } else if (state == CustomerState.LEFT_HAPPY || state == CustomerState.FINISHED) {
            int avgSatisfaction = orders.isEmpty() ? 0 : totalSatisfaction / orders.size();
            return avgSatisfaction / 10;
        }
        return 0;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public int getPatience() { return patience; }
    public int getMaxPatience() { return maxPatience; }
    public int getHunger() { return hunger; }
    public int getMoney() { return money; }
    public List<Dish> getOrders() { return orders; }
    public CustomerState getState() { return state; }
    public void setState(CustomerState state) { this.state = state; }
    public Table getTable() { return table; }
    public int getDishesReceived() { return dishesReceived; }
    public int getEatingTimer() { return eatingTimer; }

    @Override
    public String toString() {
        String stateStr = "";
        switch (state) {
            case WAITING_SEAT: stateStr = "🚶等位"; break;
            case WAITING_ORDER: stateStr = "📋等点单"; break;
            case WAITING_FOOD: stateStr = "⏳等菜[" + dishesReceived + "/" + orders.size() + "]"; break;
            case EATING: stateStr = "🍽️吃饭" + eatingTimer + "s"; break;
            case FINISHED: stateStr = "😊吃完"; break;
            case LEFT_ANGRY: stateStr = "😡离开"; break;
            case LEFT_HAPPY: stateStr = "😄满意离开"; break;
        }
        return String.format("%s [耐心:%d/%d 饥饿:%d] %s", 
                name, patience, maxPatience, hunger, stateStr);
    }
}

