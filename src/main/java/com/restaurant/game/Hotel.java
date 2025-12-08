package com.restaurant.game;

import com.restaurant.model.*;

import java.util.*;

/**
 * 旅店主类 - 游戏核心逻辑
 */
public class Hotel {
    private String name;
    private int money;
    private int reputation;
    private int hotelLevel;
    private int hotelExp;
    private int expToNextLevel;
    private List<Waiter> waiters;
    private List<Chef> chefs;
    private List<Table> tables;
    private List<Dish> menu;
    private List<Waiter> waiterPool;
    private List<Chef> chefPool;
    private int lastPoolRefresh;
    private int maxWaiters = 5;
    private int maxChefs = 3;
    private int unlockedWaiterTier = 1;  // 解锁的员工等级
    private int unlockedChefTier = 1;
    private Queue<Customer> waitingCustomers;
    private Queue<Order> orderQueue;
    private List<Order> activeOrders;
    private List<Customer> allCustomers;
    private int totalCustomersServed;
    private int totalCustomersLost;
    private int totalRevenue;
    private int totalTips;
    private int gameTime;
    private double shopRating;      // 店铺评分 1.0-5.5
    private int totalRatings;       // 总评价数
    private int goodRatings;        // 好评数(4星以上)
    private List<String> recentReviews;  // 最近评价
    private int maxTables;
    private boolean hasSecondFloor;
    private Random random;
    private String currentEvent;
    private int eventTimer;
    private List<String> gameLogs;
    private static final int MAX_LOGS = 30;
    public Hotel(String name) {
        this(name, true);
    }


    public Hotel(String name, boolean initDefault) {
        this.name = name;
        this.waiters = new ArrayList<>();
        this.chefs = new ArrayList<>();
        this.tables = new ArrayList<>();
        this.menu = new ArrayList<>();
        this.waiterPool = new ArrayList<>();
        this.chefPool = new ArrayList<>();
        this.waitingCustomers = new LinkedList<>();
        this.orderQueue = new LinkedList<>();
        this.activeOrders = new ArrayList<>();
        this.allCustomers = new ArrayList<>();
        this.recentReviews = new ArrayList<>();
        this.gameLogs = new ArrayList<>();
        this.random = new Random();
        if (initDefault) {
            initializeDefault();
        }
    }

    private void initializeDefault() {
        this.money = 3000;  // 初始资金减少，需要自己招聘
        this.reputation = 20;
        this.hotelLevel = 1;
        this.hotelExp = 0;
        this.expToNextLevel = 5;  // 1级升2级只需5经验
        this.maxWaiters = 5;
        this.maxChefs = 3;
        this.unlockedWaiterTier = 1;
        this.unlockedChefTier = 1;
        this.totalCustomersServed = 0;
        this.totalCustomersLost = 0;
        this.totalRevenue = 0;
        this.totalTips = 0;
        this.gameTime = 0;
        this.shopRating = 3.0;
        this.totalRatings = 0;
        this.goodRatings = 0;
        this.maxTables = 5;
        this.hasSecondFloor = false;
        this.lastPoolRefresh = 0;
        tables.add(new Table(1, 2));
        tables.add(new Table(2, 4));
        menu.add(Dish.createEggFriedRice());
        menu.add(Dish.createTomatoEgg());
        refreshEmployeePool();
        addLog("[开业] " + name + " 开业了！");
        addLog("[资金] 初始资金: $" + money);
        addLog("[提示] 先招聘员工才能开始营业！");
    }
    public void refreshEmployeePool() {
        waiterPool.clear();
        chefPool.clear();
        int maxTier = Math.min(5, 1 + hotelLevel / 15);
        if (hotelLevel >= 70 && goodRatings >= 1000) {
            maxTier = 5;  // 特技员工需要70级+1000好评
        } else if (hotelLevel >= 50) {
            maxTier = Math.min(maxTier, 4);
        } else if (hotelLevel >= 30) {
            maxTier = Math.min(maxTier, 3);
        } else if (hotelLevel >= 10) {
            maxTier = Math.min(maxTier, 2);
        } else {
            maxTier = 1;
        }
        int poolSize = 3 + random.nextInt(3);
        for (int i = 0; i < poolSize; i++) {
            int tier = 1 + random.nextInt(maxTier);
            waiterPool.add(Waiter.generateRandom(tier, hotelLevel));
            chefPool.add(Chef.generateRandom(tier, hotelLevel));
        }
        lastPoolRefresh = gameTime;
        addLog("[招聘] 候选员工已刷新！");
    }
    public void tick() {
        gameTime++;
        if (gameTime - lastPoolRefresh >= 300) {
            refreshEmployeePool();
        }
        if (waiters.isEmpty() || chefs.isEmpty()) {
            if (gameTime % 30 == 0) {
                addLog("[警告] 缺少员工！请先招聘服务员和厨师");
            }
            return;
        }

        updateEvent();
        trySpawnCustomer();
        updateCustomers();
        updateEmployees();
        processWaiters();
        processChefs();
        checkLevelUp();
        cleanupCustomers();
    }

    private void trySpawnCustomer() {
        boolean hasEmptyTable = findEmptyTable() != null;
        int waitingCount = waitingCustomers.size();
        if (!hasEmptyTable && waitingCount >= 5) {
            return;
        }
        double spawnChance;
        if (hasEmptyTable) {
            spawnChance = 0.08 + (hotelLevel * 0.005) + (reputation * 0.001);
        } else {
            spawnChance = 0.03;
        }
        if ("雨天".equals(currentEvent)) {
            spawnChance *= 1.3;
        } else if ("隔壁开新店".equals(currentEvent)) {
            spawnChance *= 0.7;
        }
        if (random.nextDouble() < spawnChance) {
            Customer customer = Customer.generate(hotelLevel, goodRatings);
            waitingCustomers.add(customer);
            allCustomers.add(customer);
            String typeInfo = customer.isSpecial() ? " [" + customer.getType().name + "]" : "";
            addLog("[来客] " + customer.getName() + typeInfo + " 来了！");
        }
    }

    private void updateCustomers() {
        for (Customer customer : allCustomers) {
            Customer.CustomerState prevState = customer.getState();
            customer.tick();
            if (prevState != Customer.CustomerState.LEFT_ANGRY && 
                customer.getState() == Customer.CustomerState.LEFT_ANGRY) {
                handleAngryCustomer(customer);
            }
            if (prevState == Customer.CustomerState.EATING && 
                customer.getState() == Customer.CustomerState.FINISHED) {
                handleFinishedCustomer(customer);
            }
        }
    }

    private void handleAngryCustomer(Customer customer) {
        int penalty = 5 + random.nextInt(15);
        money = Math.max(0, money - penalty);
        reputation = Math.max(0, reputation + customer.getReputationEffect());
        totalCustomersLost++;
        double rating = customer.getRating();
        addRating(rating, customer.getReview());
        if (customer.getTable() != null) {
            customer.getTable().clearTable();
        }
        addLog("[差评] " + customer.getName() + " 生气离开！评分:" + rating + "星");
    }

    private void handleFinishedCustomer(Customer customer) {
        int bill = customer.calculateBill();
        int tip = customer.calculateTip();
        int expGain = customer.getExpReward();
        int totalIncome = bill + tip;
        money += totalIncome;
        totalRevenue += bill;
        totalTips += tip;
        hotelExp += expGain;
        reputation = Math.min(100, reputation + customer.getReputationEffect());
        totalCustomersServed++;
        double rating = customer.getRating();
        addRating(rating, customer.getReview());
        if (!waiters.isEmpty()) {
            Waiter w = waiters.get(random.nextInt(waiters.size()));
            w.receiveTip(tip);
        }
        if (customer.getTable() != null) {
            customer.getTable().clearTable();
        }
        customer.setState(Customer.CustomerState.LEFT_HAPPY);
        String tipInfo = tip > 0 ? "(+小费" + tip + ")" : "";
        addLog("[收入] " + customer.getName() + " 结账 $" + bill + tipInfo + " 评分:" + rating + "星 经验+" + expGain);
    }

    private void addRating(double rating, String review) {
        totalRatings++;
        if (rating >= 4.0) {
            goodRatings++;
        }
        shopRating = ((shopRating * (totalRatings - 1)) + rating) / totalRatings;
        shopRating = Math.round(shopRating * 10) / 10.0;
        recentReviews.add(0, String.format("%.1f★ %s", rating, review));
        while (recentReviews.size() > 30) {
            recentReviews.remove(recentReviews.size() - 1);
        }
    }
    
    public double getAverageRating() {
        return shopRating;
    }

    private void updateEmployees() {
        for (Waiter w : waiters) { w.rest(); }
        for (Chef c : chefs) { c.rest(); }
    }

    private void processWaiters() {
        for (Waiter waiter : waiters) {
            if (waiter.isResting()) continue;
            waiter.updatePosition();
            switch (waiter.getCurrentTask()) {
                case NONE:
                    if (waiter.canWork()) {
                        assignWaiterTask(waiter);
                    }
                    break;
                case GREETING:
                    waiter.setTaskTimer(waiter.getTaskTimer() - 1);
                    if (waiter.getTaskTimer() <= 0) {
                        finishGreeting(waiter);
                    }
                    break;
                case TAKING_ORDER:
                    waiter.setTaskTimer(waiter.getTaskTimer() - 1);
                    if (waiter.getTaskTimer() <= 0) {
                        finishTakingOrder(waiter);
                    }
                    break;
                case WALKING_TO_KITCHEN:
                    if (waiter.hasReachedTarget()) {
                        startWalkingToTable(waiter);
                    }
                    break;
                case WALKING_TO_TABLE:
                    if (waiter.hasReachedTarget()) {
                        finishDelivering(waiter);
                    }
                    break;
                case DELIVERING:
                    waiter.setTaskTimer(waiter.getTaskTimer() - 1);
                    if (waiter.getTaskTimer() <= 0) {
                        finishDelivering(waiter);
                    }
                    break;
            }
        }
    }
    private void startWalkingToTable(Waiter waiter) {
        int tableId = waiter.getTargetTableId();
        Table targetTable = null;
        for (Table t : tables) {
            if (t.getId() == tableId) {
                targetTable = t;
                break;
            }
        }
        if (targetTable != null && targetTable.getPosX() > 0) {
            waiter.setTarget(targetTable.getPosX() + 30, targetTable.getPosY() + 30);
            waiter.setCurrentTask(Waiter.WaiterTask.WALKING_TO_TABLE);
            addLog("[送餐] " + waiter.getName() + " 端菜前往桌" + tableId);
        } else {
            finishDelivering(waiter);
        }
    }


    private void assignWaiterTask(Waiter waiter) {
        for (Order order : activeOrders) {
            if (order.hasCompletedDishes()) {
                Dish dish = order.getCompletedDishForDelivery();
                Customer customer = order.getCustomer();
                waiter.setServingCustomer(customer);
                waiter.startDelivering(order, dish);
                int targetTableId = -1;
                if (customer != null && customer.getTable() != null) {
                    targetTableId = customer.getTable().getId();
                }
                waiter.setTargetTableId(targetTableId);
                waiter.setCurrentTask(Waiter.WaiterTask.WALKING_TO_KITCHEN);
                addLog("[取菜] " + waiter.getName() + " 去厨房取 " + dish.getName());
                return;
            }
        }
        if (!waitingCustomers.isEmpty()) {
            Table emptyTable = findEmptyTable();
            if (emptyTable != null) {
                Customer customer = waitingCustomers.poll();
                waiter.startGreeting(customer);
                addLog("[迎客] " + waiter.getName() + " 迎接 " + customer.getName());
                return;
            }
        }
        for (Customer customer : allCustomers) {
            if (customer.getState() == Customer.CustomerState.WAITING_ORDER) {
                waiter.setServingCustomer(customer);
                waiter.startTakingOrder();
                addLog("[点单] " + waiter.getName() + " 给 " + customer.getName() + " 点单");
                return;
            }
        }
    }

    private void finishGreeting(Waiter waiter) {
        Customer customer = waiter.getServingCustomer();
        Table table = findEmptyTable();
        if (table != null && customer != null) {
            if (waiter.checkError() && "雨天".equals(currentEvent)) {
                addLog("[事故] " + waiter.getName() + " 滑倒了！");
                waiter.consumeStamina(10);
            }
            
            table.seatCustomer(customer);
            waiter.gainExp(3);
            addLog("[入座] " + customer.getName() + " 入座桌" + table.getId());
        }
        
        waiter.setServingCustomer(null);
        waiter.setCurrentTask(Waiter.WaiterTask.NONE);
        waiter.setBusy(false);
    }

    private void finishTakingOrder(Waiter waiter) {
        Customer customer = waiter.getServingCustomer();
        
        if (customer != null && customer.getState() == Customer.CustomerState.WAITING_ORDER) {
            customer.orderDishes(menu);
            Order order = new Order(customer, gameTime);
            orderQueue.add(order);
            activeOrders.add(order);
            waiter.gainExp(5);
            
            StringBuilder orderStr = new StringBuilder();
            for (Dish d : customer.getOrders()) {
                orderStr.append(d.getName()).append(" ");
            }
            addLog("[订单] " + customer.getName() + " 点了: " + orderStr);
        }
        
        waiter.setServingCustomer(null);
        waiter.setCurrentTask(Waiter.WaiterTask.NONE);
        waiter.setBusy(false);
    }

    private void finishDelivering(Waiter waiter) {
        Order order = waiter.getCarryingOrder();
        Dish deliveredDish = waiter.getCarryingDish();
        
        if (order != null && order.getCustomer() != null) {
            if (hotelLevel >= 10 && waiter.checkError()) {
                addLog("[事故] " + waiter.getName() + " 摔碎盘子！");
                money = Math.max(0, money - 15);
                if (deliveredDish != null) {
                    order.confirmDishDelivered(deliveredDish);
                }
            } else if (deliveredDish != null) {
                Customer customer = order.getCustomer();
                customer.receiveDish(deliveredDish);
                order.confirmDishDelivered(deliveredDish);
                waiter.gainExp(8);
                addLog("[送达] " + customer.getName() + " 收到 " + deliveredDish.getName());
            }
            
            if (order.isFullyDelivered()) {
                order.markDelivered();
                activeOrders.remove(order);
            }
        }
        waiter.setTargetTableId(-1);
        waiter.finishTask();
        waiter.moveToHome();
    }

    private void processChefs() {
        for (Chef chef : chefs) {
            if (chef.isResting()) continue;

            if (chef.isBusy()) {
                chef.setCookTimer(chef.getCookTimer() - 1);
                if (chef.getCookTimer() <= 0) {
                    finishCooking(chef);
                }
            } else if (chef.canWork()) {
                assignChefTask(chef);
            }
        }
    }

    private void assignChefTask(Chef chef) {
        for (Order order : activeOrders) {
            Dish dish = order.getNextDishToCook();
            if (dish != null && chef.canCook(dish)) {
                Dish toCook = order.startCookingDish();
                chef.startCooking(order, toCook);
                addLog("[烹饪] " + chef.getName() + " 开始做 " + toCook.getName());
                return;
            }
        }
    }

    private void finishCooking(Chef chef) {
        Order order = chef.getCurrentOrder();
        Dish dish = chef.getCurrentDish();
        
        if (order != null && dish != null) {
            if (chef.checkBurnDish()) {
                addLog("[事故] " + chef.getName() + " 把 " + dish.getName() + " 做焦了！");
                order.getPendingDishes().add(0, dish);
                chef.consumeStamina(5);
            } else {
                order.completeDish(dish);
                addLog("[完成] " + chef.getName() + " 完成 " + dish.getName());
            }
        }
        
        chef.finishCooking();
    }

    private Table findEmptyTable() {
        for (Table t : tables) {
            if (t.isAvailable()) {
                return t;
            }
        }
        return null;
    }
    private void checkLevelUp() {
        // 不再自动升级，只检查是否可以升级
    }
    
    /**
     * 检查是否可以升级（经验足够）
     */
    public boolean canLevelUp() {
        return hotelLevel < 150 && hotelExp >= expToNextLevel;
    }
    
    /**
     * 获取升级所需费用（等级越高越贵）
     */
    public int getUpgradeCost() {
        if (hotelLevel <= 10) {
            return 100 + hotelLevel * 50;
        } else if (hotelLevel <= 30) {
            return 500 + (hotelLevel - 10) * 100;
        } else if (hotelLevel <= 60) {
            return 2500 + (hotelLevel - 30) * 200;
        } else if (hotelLevel <= 100) {
            return 8500 + (hotelLevel - 60) * 400;
        } else {
            return 24500 + (hotelLevel - 100) * 800;
        }
    }
    
    /**
     * 执行升级（需要花钱）
     */
    public boolean performLevelUp() {
        if (!canLevelUp()) {
            addLog("[错误] 经验不足，无法升级！");
            return false;
        }
        int cost = getUpgradeCost();
        if (money < cost) {
            addLog("[错误] 资金不足！升级需要$" + cost);
            return false;
        }
        
        money -= cost;
        hotelExp -= expToNextLevel;
        hotelLevel++;
        expToNextLevel = getExpForLevel(hotelLevel + 1);
        onHotelLevelUp();
        addLog("[升级] 花费$" + cost + "升级到Lv." + hotelLevel + "!");
        return true;
    }
    
    private int getExpForLevel(int level) {
        if (level <= 2) return 5;
        if (level <= 5) return 10 + (level - 2) * 10;
        if (level <= 10) return 50 + (level - 5) * 15;
        if (level <= 20) return 125 + (level - 10) * 20;
        if (level <= 50) return 325 + (level - 20) * 30;
        if (level <= 100) return 1225 + (level - 50) * 50;
        return 3725 + (level - 100) * 80;  // 100级以上
    }

    private void onHotelLevelUp() {
        addLog("[升级] 餐厅升级到 Lv." + hotelLevel + "！");
        switch (hotelLevel) {
            case 2:
                menu.add(Dish.createMaPoTofu());
                addLog("[解锁] 新菜品：麻婆豆腐");
                break;
            case 3:
                menu.add(Dish.createKungPaoChicken());
                maxTables = 8;
                addLog("[解锁] 新菜品：宫保鸡丁 | 桌子上限+3");
                break;
            case 5:
                menu.add(Dish.createSweetSourPork());
                addLog("[解锁] 新菜品：糖醋里脊");
                break;
            case 10:
                unlockedWaiterTier = 2;
                unlockedChefTier = 2;
                menu.add(Dish.createBraisedPork());
                maxTables = 12;
                addLog("[解锁] 普通员工！新菜品：红烧肉");
                break;
            case 15:
                menu.add(Dish.createSteamedFish());
                addLog("[解锁] 新菜品：清蒸鱼");
                break;
            case 20:
                menu.add(Dish.createPekingDuck());
                maxTables = 18;
                hasSecondFloor = true;
                addLog("[解锁] 二楼！新菜品：北京烤鸭");
                break;
            case 30:
                unlockedWaiterTier = 3;
                unlockedChefTier = 3;
                menu.add(Dish.createLobster());
                maxTables = 24;
                addLog("[解锁] 中级员工！新菜品：龙虾");
                break;
            case 50:
                unlockedWaiterTier = 4;
                unlockedChefTier = 4;
                menu.add(Dish.createWagyuSteak());
                maxTables = 30;
                addLog("[解锁] 高级员工！终极菜品：和牛牛排！");
                break;
            case 70:
                if (goodRatings >= 1000) {
                    unlockedWaiterTier = 5;
                    unlockedChefTier = 5;
                    addLog("[解锁] 特技员工！");
                }
                break;
        }
        
        reputation = Math.min(100, reputation + 3);
        refreshEmployeePool();
    }

    private void cleanupCustomers() {
        allCustomers.removeIf(c -> 
            c.getState() == Customer.CustomerState.LEFT_ANGRY || 
            c.getState() == Customer.CustomerState.LEFT_HAPPY);
    }

    private void updateEvent() {
        if (eventTimer > 0) {
            eventTimer--;
            if (eventTimer == 0) {
                addLog("[事件] 事件结束：" + currentEvent);
                currentEvent = null;
            }
            return;
        }
        double eventChance = 0.005;  // 0.5%基础概率
        if (hotelLevel >= 10) {
            eventChance = 0.008;
        } else if (hotelLevel >= 20) {
            eventChance = 0.01;
        }
        
        if (random.nextDouble() < eventChance) {
            triggerRandomEvent();
        }
    }

    private void triggerRandomEvent() {
        double rand = random.nextDouble();
        if (hotelLevel < 10) {
            if (rand < 0.7) {
                currentEvent = "美食评论家";
                eventTimer = 1;
                reputation = Math.min(100, reputation + 5);
                addLog("[好事] 美食评论家来了！声望+5");
            } else {
                currentEvent = "雨天";
                eventTimer = 20;
                addLog("[事件] 下雨了！顾客稍微多一点");
            }
            return;
        }
        if (rand < 0.4) {
            currentEvent = "美食评论家";
            eventTimer = 1;
            reputation = Math.min(100, reputation + 8);
            addLog("[好事] 美食评论家来了！声望+8");
        } else if (rand < 0.7) {
            currentEvent = "雨天";
            eventTimer = 25;
            addLog("[事件] 下雨了！");
        } else if (rand < 0.9) {
            currentEvent = "隔壁开新店";
            eventTimer = 30;
            addLog("[事件] 隔壁开新店！顾客稍减少");
        } else {
            if (hotelLevel >= 20 && waiters.size() >= 2 && chefs.size() >= 2) {
                currentEvent = "员工吵架";
                eventTimer = 10;
                if (random.nextBoolean() && !waiters.isEmpty()) {
                    waiters.get(random.nextInt(waiters.size())).startResting();
                    addLog("[小事故] 员工闹别扭休息一下");
                } else if (!chefs.isEmpty()) {
                    chefs.get(random.nextInt(chefs.size())).startResting();
                    addLog("[小事故] 厨师闹别扭休息一下");
                }
            } else {
                currentEvent = "美食评论家";
                eventTimer = 1;
                reputation = Math.min(100, reputation + 5);
                addLog("[好事] 美食评论家来了！");
            }
        }
    }

    public void addLog(String log) {
        String timeStr = String.format("[%02d:%02d]", gameTime / 60, gameTime % 60);
        gameLogs.add(timeStr + " " + log);
        if (gameLogs.size() > MAX_LOGS) {
            gameLogs.remove(0);
        }
    }

    // ========== 招聘和解雇 ==========

    public boolean hireWaiterFromPool(int index) {
        if (index < 0 || index >= waiterPool.size()) return false;
        if (waiters.size() >= maxWaiters) {
            addLog("[错误] 服务员已满员！");
            return false;
        }
        
        Waiter w = waiterPool.get(index);
        int cost = w.getHirePrice();
        
        if (money < cost) {
            addLog("[错误] 资金不足！需要$" + cost);
            return false;
        }
        
        money -= cost;
        waiters.add(w);
        waiterPool.remove(index);
        addLog("[招聘] " + w.getTierName() + "服务员: " + w.getName());
        return true;
    }

    /**
     * 从招聘池招聘厨师
     */
    public boolean hireChefFromPool(int index) {
        if (index < 0 || index >= chefPool.size()) return false;
        if (chefs.size() >= maxChefs) {
            addLog("[错误] 厨师已满员！");
            return false;
        }
        
        Chef c = chefPool.get(index);
        int cost = c.getHirePrice();
        
        if (money < cost) {
            addLog("[错误] 资金不足！需要$" + cost);
            return false;
        }
        
        money -= cost;
        chefs.add(c);
        chefPool.remove(index);
        addLog("招聘了 " + c.getTierName() + "厨师: " + c.getName());
        return true;
    }

    /**
     * 从招聘池招聘服务员（直接传入对象）
     */
    public boolean hireWaiterFromPool(Waiter w, int cost) {
        if (waiters.size() >= maxWaiters) {
            addLog("服务员已满员！");
            return false;
        }
        if (money < cost) {
            addLog("资金不足！需要$" + cost);
            return false;
        }
        
        money -= cost;
        waiters.add(w);
        waiterPool.remove(w);
        addLog("招聘了 " + w.getTierName() + "服务员: " + w.getName());
        return true;
    }

    /**
     * 从招聘池招聘厨师（直接传入对象）
     */
    public boolean hireChefFromPool(Chef c, int cost) {
        if (chefs.size() >= maxChefs) {
            addLog("厨师已满员！");
            return false;
        }
        if (money < cost) {
            addLog("资金不足！需要$" + cost);
            return false;
        }
        
        money -= cost;
        chefs.add(c);
        chefPool.remove(c);
        addLog("招聘了 " + c.getTierName() + "厨师: " + c.getName());
        return true;
    }

    /**
     * 清空所有顾客（建造模式用）
     */
    public void clearAllCustomers() {
        waitingCustomers.clear();
        for (Table t : tables) {
            t.clearTable();
        }
        allCustomers.clear();
        orderQueue.clear();
        activeOrders.clear();
        addLog("已清空所有顾客");
    }

    /**
     * 解雇服务员
     */
    public boolean fireWaiter(int index) {
        if (index < 0 || index >= waiters.size()) return false;
        
        Waiter w = waiters.get(index);
        int compensation = w.getFireCompensation();
        
        if (money < compensation) {
            addLog("[错误] 资金不足支付赔偿金$" + compensation);
            return false;
        }
        
        money -= compensation;
        waiters.remove(index);
        addLog("[解雇] " + w.getName() + " 赔偿$" + compensation);
        return true;
    }

    /**
     * 解雇厨师
     */
    public boolean fireChef(int index) {
        if (index < 0 || index >= chefs.size()) return false;
        
        Chef c = chefs.get(index);
        int compensation = c.getFireCompensation();
        
        if (money < compensation) {
            addLog("[错误] 资金不足支付赔偿金$" + compensation);
            return false;
        }
        
        money -= compensation;
        chefs.remove(index);
        addLog("[解雇] " + c.getName() + " 赔偿$" + compensation);
        return true;
    }

    // 兼容旧接口
    public boolean hireWaiter(String name, int cost, int tier) {
        if (waiters.size() >= maxWaiters || money < cost) return false;
        money -= cost;
        waiters.add(new Waiter(name, tier, 20 + tier*10, 20 + tier*10, 20 + tier*10));
        return true;
    }
    
    public boolean hireChef(String name, int cost, int tier) {
        if (chefs.size() >= maxChefs || money < cost) return false;
        money -= cost;
        chefs.add(new Chef(name, tier, 20 + tier*10, 20 + tier*10, 20 + tier*10));
        return true;
    }

    public boolean buyTable(int seats, int cost) {
        if (tables.size() >= maxTables) {
            addLog("[错误] 桌子已达上限！升级餐厅解锁更多");
            return false;
        }
        if (money >= cost) {
            money -= cost;
            tables.add(new Table(tables.size() + 1, seats));
            addLog("[购买] " + seats + "人桌");
            return true;
        }
        return false;
    }

    public void spendMoney(int amount) {
        money = Math.max(0, money - amount);
    }

    public void addHotelExp(int amount) {
        hotelExp += amount;
        checkLevelUp();
    }

    // ========== Getters and Setters ==========
    
    public String getName() { return name; }
    public int getMoney() { return money; }
    public void setMoney(int money) { this.money = money; }
    public int getReputation() { return reputation; }
    public void setReputation(int reputation) { this.reputation = reputation; }
    public int getHotelLevel() { return hotelLevel; }
    public void setHotelLevel(int hotelLevel) { this.hotelLevel = hotelLevel; }
    public int getHotelExp() { return hotelExp; }
    public void setHotelExp(int hotelExp) { this.hotelExp = hotelExp; }
    public int getExpToNextLevel() { return expToNextLevel; }
    public void setExpToNextLevel(int expToNextLevel) { this.expToNextLevel = expToNextLevel; }
    public List<Waiter> getWaiters() { return waiters; }
    public List<Chef> getChefs() { return chefs; }
    public List<Table> getTables() { return tables; }
    public List<Dish> getMenu() { return menu; }
    public Queue<Customer> getWaitingCustomers() { return waitingCustomers; }
    public List<Order> getActiveOrders() { return activeOrders; }
    public List<Customer> getAllCustomers() { return allCustomers; }
    public int getTotalCustomersServed() { return totalCustomersServed; }
    public void setTotalCustomersServed(int v) { this.totalCustomersServed = v; }
    public int getTotalCustomersLost() { return totalCustomersLost; }
    public void setTotalCustomersLost(int v) { this.totalCustomersLost = v; }
    public int getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(int v) { this.totalRevenue = v; }
    public int getTotalTips() { return totalTips; }
    public void setTotalTips(int v) { this.totalTips = v; }
    public int getGameTime() { return gameTime; }
    public void setGameTime(int gameTime) { this.gameTime = gameTime; }
    public String getCurrentEvent() { return currentEvent; }
    public List<String> getGameLogs() { return gameLogs; }
    public int getMaxWaiters() { return maxWaiters; }
    public void setMaxWaiters(int v) { this.maxWaiters = v; }
    public int getMaxChefs() { return maxChefs; }
    public void setMaxChefs(int v) { this.maxChefs = v; }
    public int getUnlockedWaiterTier() { return unlockedWaiterTier; }
    public void setUnlockedWaiterTier(int v) { this.unlockedWaiterTier = v; }
    public int getUnlockedChefTier() { return unlockedChefTier; }
    public void setUnlockedChefTier(int v) { this.unlockedChefTier = v; }
    public double getShopRating() { return shopRating; }
    public void setShopRating(double v) { this.shopRating = v; }
    public int getTotalRatings() { return totalRatings; }
    public void setTotalRatings(int v) { this.totalRatings = v; }
    public int getGoodRatings() { return goodRatings; }
    public void setGoodRatings(int v) { this.goodRatings = v; }
    public List<String> getRecentReviews() { return recentReviews; }
    public int getMaxTables() { return maxTables; }
    public void setMaxTables(int maxTables) { this.maxTables = maxTables; }
    public boolean hasSecondFloor() { return hasSecondFloor; }
    public List<Waiter> getWaiterPool() { return waiterPool; }
    public List<Chef> getChefPool() { return chefPool; }
}
