package com.restaurant.dto;

import com.restaurant.model.Chef;
import com.restaurant.model.Waiter;

/**
 * 员工DTO
 */
public class EmployeeDTO {
    private String name;
    private String type; // waiter / chef
    private int tier; // 品质等级
    private String tierName; // 品质名称
    private int level;
    private int exp;
    private int expToNext;
    private int stamina;
    private int maxStamina;
    private boolean busy;
    private boolean resting;

    // 技能
    private int skill1; // 服务员:服务 厨师:厨艺
    private int skill2; // 速度
    private int skill3; // 魅力

    // 统计
    private int totalWork; // 服务员:小费 厨师:做菜数

    // 招聘价格（用于招聘池）
    private int hirePrice;

    public EmployeeDTO() {
    }

    public static EmployeeDTO fromWaiter(Waiter w) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.name = w.getName();
        dto.type = "waiter";
        dto.tier = w.getEmployeeTier();
        dto.tierName = w.getTierName();
        dto.level = w.getLevel();
        dto.exp = w.getExp();
        dto.expToNext = w.getExpToNextLevel();
        dto.stamina = w.getStamina();
        dto.maxStamina = w.getMaxStamina();
        dto.busy = w.isBusy();
        dto.resting = w.isResting();
        dto.skill1 = w.getServiceSkill();
        dto.skill2 = w.getSpeedSkill();
        dto.skill3 = w.getCharmSkill();
        dto.totalWork = w.getTotalTips();
        dto.hirePrice = w.getHirePrice();
        return dto;
    }

    public static EmployeeDTO fromChef(Chef c) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.name = c.getName();
        dto.type = "chef";
        dto.tier = c.getEmployeeTier();
        dto.tierName = c.getTierName();
        dto.level = c.getLevel();
        dto.exp = c.getExp();
        dto.expToNext = c.getExpToNextLevel();
        dto.stamina = c.getStamina();
        dto.maxStamina = c.getMaxStamina();
        dto.busy = c.isBusy();
        dto.resting = c.isResting();
        dto.skill1 = c.getCookingSkill();
        dto.skill2 = c.getSpeedSkill();
        dto.skill3 = c.getCharmSkill();
        dto.totalWork = c.getTotalDishesCooked();
        dto.hirePrice = c.getHirePrice();
        return dto;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getTier() {
        return tier;
    }

    public String getTierName() {
        return tierName;
    }

    public int getLevel() {
        return level;
    }

    public int getExp() {
        return exp;
    }

    public int getExpToNext() {
        return expToNext;
    }

    public int getStamina() {
        return stamina;
    }

    public int getMaxStamina() {
        return maxStamina;
    }

    public boolean isBusy() {
        return busy;
    }

    public boolean isResting() {
        return resting;
    }

    public int getSkill1() {
        return skill1;
    }

    public int getSkill2() {
        return skill2;
    }

    public int getSkill3() {
        return skill3;
    }

    public int getTotalWork() {
        return totalWork;
    }

    public int getHirePrice() {
        return hirePrice;
    }
}
