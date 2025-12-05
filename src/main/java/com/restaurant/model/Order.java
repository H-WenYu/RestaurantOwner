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
    private List<Dish> completedDishes;
    private List<Dish> pendingDishes;
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
     * 获取一道已完成的菜用于上菜
     */
    public Dish getCompletedDishForDelivery() {
        if (completedDishes.isEmpty()) return null;
        return completedDishes.remove(0);
    }

    /**
     * 检查是否有菜可以上
     */
    public boolean hasCompletedDishes() {
        return !completedDishes.isEmpty();
    }

    /**
     * 检查是否所有菜都已送达
     */
    public boolean isFullyDelivered() {
        return pendingDishes.isEmpty() && completedDishes.isEmpty();
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

