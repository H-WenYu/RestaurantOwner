package com.restaurant.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单类
 */
public class Order {
    private static int idCounter = 0;
    
    private int id;
    private Customer customer;
    private List<Dish> dishes;
    private List<Dish> completedDishes;    // 已完成的菜品
    private List<Dish> claimedDishes;      // 已被服务员领取的菜品（送餐中）
    private List<Dish> pendingDishes;      // 待烹饪的菜品
    private OrderState state;
    private int createTime;  // 创建时间（游戏tick）

    public enum OrderState {
        PENDING,      // 等待处理
        COOKING,      // 正在烹饪
        READY,        // 部分菜品完成
        COMPLETED,    // 全部完成
        DELIVERED     // 已送达
    }

    public Order(Customer customer, int currentTick) {
        this.id = ++idCounter;
        this.customer = customer;
        this.dishes = new ArrayList<>(customer.getOrders());
        this.completedDishes = new ArrayList<>();
        this.claimedDishes = new ArrayList<>();
        this.pendingDishes = new ArrayList<>(dishes);
        this.state = OrderState.PENDING;
        this.createTime = currentTick;
    }

    /**
     * 获取下一道需要烹饪的菜
     */
    public Dish getNextDishToCook() {
        if (pendingDishes.isEmpty()) return null;
        return pendingDishes.get(0);
    }

    /**
     * 开始烹饪一道菜
     */
    public Dish startCookingDish() {
        if (pendingDishes.isEmpty()) return null;
        Dish dish = pendingDishes.remove(0);
        state = OrderState.COOKING;
        return dish;
    }

    /**
     * 完成一道菜
     */
    public void completeDish(Dish dish) {
        completedDishes.add(dish);
        if (pendingDishes.isEmpty()) {
            state = OrderState.COMPLETED;
        } else {
            state = OrderState.READY;
        }
    }

    /**
     * 领取一道已完成的菜用于上菜（转移到已领取列表）
     */
    public Dish claimDishForDelivery() {
        if (completedDishes.isEmpty()) return null;
        Dish dish = completedDishes.remove(0);
        claimedDishes.add(dish);  // 标记为已领取
        return dish;
    }
    
    /**
     * 确认菜品已送达
     */
    public void confirmDishDelivered(Dish dish) {
        claimedDishes.remove(dish);
    }
    
    /**
     * 退回菜品（如果送餐失败）
     */
    public void returnDish(Dish dish) {
        if (claimedDishes.remove(dish)) {
            completedDishes.add(0, dish);  // 放回待上菜列表头部
        }
    }

    /**
     * 检查是否有未被领取的已完成菜品
     */
    public boolean hasUnclaimedDishes() {
        return !completedDishes.isEmpty();
    }
    
    /**
     * 检查是否有菜可以上（兼容旧方法）
     */
    public boolean hasCompletedDishes() {
        return !completedDishes.isEmpty();
    }
    
    /**
     * 获取已完成菜品用于上菜（兼容旧方法，实际调用claimDishForDelivery）
     */
    public Dish getCompletedDishForDelivery() {
        return claimDishForDelivery();
    }

    /**
     * 检查是否所有菜都已送达
     */
    public boolean isFullyDelivered() {
        return pendingDishes.isEmpty() && completedDishes.isEmpty() && claimedDishes.isEmpty();
    }

    /**
     * 标记为已送达
     */
    public void markDelivered() {
        state = OrderState.DELIVERED;
    }

    // Getters
    public int getId() { return id; }
    public Customer getCustomer() { return customer; }
    public List<Dish> getDishes() { return dishes; }
    public List<Dish> getCompletedDishes() { return completedDishes; }
    public List<Dish> getPendingDishes() { return pendingDishes; }
    public OrderState getState() { return state; }
    public int getCreateTime() { return createTime; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("订单#%d [%s] ", id, customer.getName()));
        sb.append("菜品: ");
        for (Dish d : dishes) {
            sb.append(d.getName()).append(" ");
        }
        sb.append(String.format("[待做:%d 完成:%d] %s", 
                pendingDishes.size(), completedDishes.size(), state));
        return sb.toString();
    }
}

