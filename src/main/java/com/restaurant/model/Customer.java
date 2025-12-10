package com.restaurant.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 顾客类
 * 包含特殊顾客类型（主播、网红等）
 */
public class Customer {
    private static int idCounter = 0;
    private static final String[] NORMAL_NAMES = {
        "张三", "李四", "王五", "赵六", "钱七", "孙八", 
        "周九", "吴十", "郑某", "冯某", "陈某", "褚某",
        "小明", "小红", "小刚", "老王", "老李", "老张"
    };
    private static final Random random = new Random();

    // 顾客类型
    public enum CustomerType {
        NORMAL(1, "普通顾客", 1.0),
        FOODIE(2, "美食爱好者", 1.5),
        BLOGGER(3, "小博主", 2.0),
        INFLUENCER(4, "网红", 3.0),
        CELEBRITY(5, "明星", 5.0),
        FOOD_CRITIC(6, "美食评论家", 4.0),
        VIP(7, "VIP客户", 3.5);

        public final int level;      // 需要店铺等级
        public final String name;
        public final double expMultiplier;

        CustomerType(int level, String name, double expMultiplier) {
            this.level = level;
            this.name = name;
            this.expMultiplier = expMultiplier;
        }
    }

    // 评价语
    private static final String[] GOOD_REVIEWS = {
        "太好吃了！下次还来！", "服务很周到，很满意！", "环境不错，菜品很棒！",
        "物超所值，强烈推荐！", "厨师手艺很好！", "服务员很热情！",
        "味道正宗，量大实惠！", "等待时间不长，很好！"
    };
    private static final String[] BAD_REVIEWS = {
        "等太久了，不满意。", "菜品一般般。", "服务态度需要改进。",
        "下次不会再来了。", "性价比不高。", "期望过高了。"
    };

    private int id;
    private String name;
    private CustomerType type;
    private int patience;
    private int maxPatience;
    private int hunger;
    private int money;
    private List<Dish> orders;
    private CustomerState state;
    private Table table;
    private int totalSatisfaction;
    private int dishesReceived;
    private int eatingTimer;
    private int baseExp;  // 基础经验值
    private boolean complained;

    public enum CustomerState {
        WAITING_SEAT, WAITING_ORDER, WAITING_FOOD, EATING, FINISHED, LEFT_ANGRY, LEFT_HAPPY
    }

    public Customer() {
        this(CustomerType.NORMAL);
    }

    public Customer(CustomerType type) {
        this.id = ++idCounter;
        this.type = type;
        
        if (type == CustomerType.NORMAL) {
            this.name = NORMAL_NAMES[random.nextInt(NORMAL_NAMES.length)] + "#" + id;
        } else {
            this.name = type.name + "#" + id;
        }
        
        this.maxPatience = 120 + random.nextInt(120) + (type == CustomerType.VIP ? 60 : 0);
        this.patience = maxPatience;
        this.hunger = random.nextInt(30);
        this.money = 100 + random.nextInt(400) + type.level * 50;
        this.orders = new ArrayList<>();
        this.state = CustomerState.WAITING_SEAT;
        this.table = null;
        this.totalSatisfaction = 0;
        this.dishesReceived = 0;
        this.eatingTimer = 0;
        this.baseExp = (int)(type.expMultiplier);
        this.complained = false;
    }

    /**
     * 根据店铺等级生成顾客
     */
    public static Customer generate(int shopLevel, int goodRatings) {
        double rand = random.nextDouble();
        
        // 特殊顾客出现概率
        if (shopLevel >= 70 && goodRatings >= 1000 && rand < 0.02) {
            return new Customer(CustomerType.CELEBRITY);
        } else if (shopLevel >= 50 && rand < 0.03 + shopLevel * 0.001) {
            return new Customer(CustomerType.FOOD_CRITIC);
        } else if (shopLevel >= 30 && rand < 0.05 + shopLevel * 0.002) {
            return new Customer(CustomerType.INFLUENCER);
        } else if (shopLevel >= 15 && rand < 0.08 + shopLevel * 0.003) {
            return new Customer(CustomerType.BLOGGER);
        } else if (shopLevel >= 5 && rand < 0.10 + shopLevel * 0.004) {
            return new Customer(CustomerType.FOODIE);
        } else if (shopLevel >= 10 && rand < 0.05) {
            return new Customer(CustomerType.VIP);
        }
        
        return new Customer(CustomerType.NORMAL);
    }

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

    public void sitDown(Table table) {
        this.table = table;
        this.state = CustomerState.WAITING_ORDER;
    }

    public void orderDishes(List<Dish> menu) {
        int dishCount = 1 + random.nextInt(3);
        int remainingMoney = money;
        
        List<Dish> availableDishes = new ArrayList<>(menu);
        
        for (int i = 0; i < dishCount && !availableDishes.isEmpty(); i++) {
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

    public void receiveDish(Dish dish) { receiveDish(dish, 1.0); }

    public void receiveDish(Dish dish, double quality) {
        double patienceFactor = 0.7 + ((double) patience / maxPatience) * 0.3;
        int satisfactionGain = (int) (dish.getSatisfaction() * quality * patienceFactor);
        totalSatisfaction += Math.max(5, satisfactionGain);
        dishesReceived++;

        if (dishesReceived >= orders.size()) {
            state = CustomerState.EATING;
            eatingTimer = 5 + orders.size() * 2;
        }
    }

    public boolean shouldComplain() {
        return !complained && state == CustomerState.WAITING_FOOD && patience < maxPatience / 3;
    }

    public void markComplained() {
        complained = true;
    }

    public int calculateBill() {
        int total = 0;
        for (Dish d : orders) {
            total += d.getPrice();
        }
        return total;
    }

    public int calculateTip() {
        if (orders.isEmpty()) return 0;
        int avgSatisfaction = totalSatisfaction / orders.size();
        int bill = calculateBill();
        return (int)(bill * avgSatisfaction * 0.003 * type.expMultiplier);
    }

    /**
     * 获取带来的经验值
     */
    public int getExpReward() {
        if (state == CustomerState.LEFT_ANGRY) {
            return 0;
        }
        int base = baseExp + orders.size();
        double satisfactionBonus = orders.isEmpty() ? 1.0 : 1.0 + (totalSatisfaction / orders.size()) * 0.01;
        return (int)(base * type.expMultiplier * satisfactionBonus);
    }

    /**
     * 获取评分（1-5.5星）
     * 基于：出餐速度（耐心剩余）、菜品满意度（厨艺）、服务综合评价
     * 正常服务平均在4.7-5.3区间
     */
    public double getRating() {
        if (state == CustomerState.LEFT_ANGRY) {
            return 1.0 + random.nextDouble();  // 生气离开：1-2星
        }
        
        // 耐心比例（出餐速度影响）- 占比30%
        double patienceRatio = (double) patience / maxPatience;
        double speedScore = patienceRatio;  // 0~1
        
        // 菜品满意度（厨艺影响）- 占比40%
        double satisfactionScore = 0.7;  // 基础0.7
        if (!orders.isEmpty() && dishesReceived > 0) {
            int avgSatisfaction = totalSatisfaction / dishesReceived;
            satisfactionScore = Math.min(1.0, avgSatisfaction / 100.0 + 0.3);  // 30~100 -> 0.6~1.3 capped at 1.0
        }
        
        // 服务加成（完成用餐）- 占比30%
        double serviceScore = (state == CustomerState.FINISHED || state == CustomerState.LEFT_HAPPY) ? 0.9 : 0.7;
        
        // 综合评分：基础4.0 + 速度加成(0~0.6) + 满意度加成(0~0.6) + 服务加成(0~0.4) + 随机(±0.2)
        double rating = 4.0 
            + speedScore * 0.6      // 快速出餐最多+0.6
            + satisfactionScore * 0.6   // 好吃最多+0.6
            + serviceScore * 0.4    // 服务好最多+0.4
            + (random.nextDouble() - 0.5) * 0.4;  // 随机±0.2
        
        // 特殊顾客更挑剔
        if (type == CustomerType.FOOD_CRITIC) {
            rating -= 0.3;  // 评论家更严格
        } else if (type == CustomerType.VIP) {
            rating += 0.2;  // VIP 更宽容
        }
        
        rating = Math.max(1.0, Math.min(5.5, rating));
        
        // 四舍五入到0.5
        return Math.round(rating * 2) / 2.0;
    }

    /**
     * 获取评价语
     */
    public String getReview() {
        if (getRating() >= 4.0) {
            return GOOD_REVIEWS[random.nextInt(GOOD_REVIEWS.length)];
        } else {
            return BAD_REVIEWS[random.nextInt(BAD_REVIEWS.length)];
        }
    }

    public int getReputationEffect() {
        if (state == CustomerState.LEFT_ANGRY) {
            return -10 - (maxPatience - patience) / 5;
        } else if (state == CustomerState.LEFT_HAPPY || state == CustomerState.FINISHED) {
            int avgSatisfaction = orders.isEmpty() ? 0 : totalSatisfaction / orders.size();
            return (int)(avgSatisfaction / 10 * type.expMultiplier);
        }
        return 0;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public CustomerType getType() { return type; }
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
    public boolean isSpecial() { return type != CustomerType.NORMAL; }

    @Override
    public String toString() {
        String typeIcon = "";
        switch (type) {
            case BLOGGER: typeIcon = "📱"; break;
            case INFLUENCER: typeIcon = "🌟"; break;
            case CELEBRITY: typeIcon = "⭐"; break;
            case FOOD_CRITIC: typeIcon = "📝"; break;
            case VIP: typeIcon = "👑"; break;
            case FOODIE: typeIcon = "🍴"; break;
            default: typeIcon = "🧑"; break;
        }
        
        String stateStr = "";
        switch (state) {
            case WAITING_SEAT: stateStr = "等位"; break;
            case WAITING_ORDER: stateStr = "等点单"; break;
            case WAITING_FOOD: stateStr = "等菜[" + dishesReceived + "/" + orders.size() + "]"; break;
            case EATING: stateStr = "用餐" + eatingTimer + "s"; break;
            case FINISHED: stateStr = "吃完"; break;
            case LEFT_ANGRY: stateStr = "😡离开"; break;
            case LEFT_HAPPY: stateStr = "😄离开"; break;
        }
        
        return String.format("%s%s [耐心:%d/%d] %s", typeIcon, name, patience, maxPatience, stateStr);
    }
}
