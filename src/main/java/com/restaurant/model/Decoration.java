package com.restaurant.model;

/**
 * 简单的装修/装饰物定义，用于增加环境与清洁度。
 */
public class Decoration {
    private final String name;
    private final int ambienceBonus;
    private final int cleanlinessBonus;
    private final int cost;

    public Decoration(String name, int ambienceBonus, int cleanlinessBonus, int cost) {
        this.name = name;
        this.ambienceBonus = ambienceBonus;
        this.cleanlinessBonus = cleanlinessBonus;
        this.cost = cost;
    }

    public String getName() { return name; }
    public int getAmbienceBonus() { return ambienceBonus; }
    public int getCleanlinessBonus() { return cleanlinessBonus; }
    public int getCost() { return cost; }
}

