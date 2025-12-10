package com.restaurant.dto;

import com.restaurant.model.DishInstance;

/**
 * 菜品DTO
 */
public class DishDTO {
    private String name;
    private int price; // 实际售价
    private int cost; // 成本
    private int profit; // 利润
    private int cookTime;
    private int requiredLevel; // 解锁所需等级
    private int dishLevel; // 菜品等级
    private int dishExp; // 当前经验
    private int expToNext; // 升级所需经验
    private int shopExp; // 店铺经验
    private int chefExp; // 厨师经验
    private int satisfaction; // 满意度
    private boolean active; // 是否上架

    public DishDTO() {
    }

    public static DishDTO fromDishInstance(DishInstance di) {
        DishDTO dto = new DishDTO();
        dto.name = di.getName();
        dto.price = di.getActualPrice();
        dto.cost = di.getCost();
        dto.profit = di.getActualPrice() - di.getCost();
        dto.cookTime = di.getCookTime();
        dto.requiredLevel = di.getLevel();
        dto.dishLevel = di.getDishLevel();
        dto.dishExp = di.getDishExp();
        dto.expToNext = di.getExpToNextLevel();
        dto.shopExp = di.getActualShopExp();
        dto.chefExp = di.getActualChefExp();
        dto.satisfaction = di.getActualSatisfaction();
        dto.active = di.isActive();
        return dto;
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getCost() {
        return cost;
    }

    public int getProfit() {
        return profit;
    }

    public int getCookTime() {
        return cookTime;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public int getDishLevel() {
        return dishLevel;
    }

    public int getDishExp() {
        return dishExp;
    }

    public int getExpToNext() {
        return expToNext;
    }

    public int getShopExp() {
        return shopExp;
    }

    public int getChefExp() {
        return chefExp;
    }

    public int getSatisfaction() {
        return satisfaction;
    }

    public boolean isActive() {
        return active;
    }
}
