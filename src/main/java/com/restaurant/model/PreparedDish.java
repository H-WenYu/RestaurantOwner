package com.restaurant.model;

/**
 * 传菜口中的已出菜品记录。
 * 绑定订单、菜品质量以及份数，便于服务员领取并送达。
 */
public class PreparedDish {
    private final Order order;
    private final Dish dish;
    private final double quality;
    private final int servings;

    public PreparedDish(Order order, Dish dish, double quality, int servings) {
        this.order = order;
        this.dish = dish;
        this.quality = quality;
        this.servings = servings;
    }

    public Order getOrder() {
        return order;
    }

    public Dish getDish() {
        return dish;
    }

    public double getQuality() {
        return quality;
    }

    public int getServings() {
        return servings;
    }
}

