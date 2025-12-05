package com.restaurant.game;

import com.restaurant.model.*;

import java.util.*;

/**
 * 旅店主类 - 游戏核心逻辑
 */
public class Hotel {
    // 基础属性
    private String name;
    private int money;
    private int reputation;      // 声望 0-100
    private int hotelLevel;      // 旅店等级 1-10
    private int hotelExp;        // 旅店经验
    private int expToNextLevel;  // 升级所需经验

    // 员工和设施
    private List<Waiter> waiters;
    private List<Chef> chefs;
    private List<Table> tables;
    private List<Dish> menu;

    // 运营数据
    private Queue<Customer> waitingCustomers;  // 等位的顾客
    private Queue<Order> orderQueue;           // 订单队列
    private List<Order> activeOrders;          // 正在处理的订单
    private List<Customer> allCustomers;       // 所有在店顾客

    // 员工系统
    private int maxWaiters;        // 最大服务员数
    private int maxChefs;          // 最大厨师数
    private int unlockedWaiterTier; // 解锁的服务员等级 (1=普通, 2=高级, 3=金牌)
    private int unlockedChefTier;   // 解锁的厨师等级 (1=学徒, 2=大厨, 3=主厨)

    // 统计数据
    private int totalCustomersServed;
    private int totalCustomersLost;
    private int totalRevenue;
    private int totalTips;
    private int gameTime;  // 游戏时间（tick数）

    // 随机事件
    private Random random;
    private String currentEvent;
    private int eventTimer;

    // 游戏日志
    private List<String> gameLogs;
    private static final int MAX_LOGS = 20;

    public Hotel(String name) {
        this.name = name;
        this.money = 1000;
        this.reputation = 30;
        this.hotelLevel = 1;
        this.hotelExp = 0;
        this.expToNextLevel = 100;

        this.waiters = new ArrayList<>();
        this.chefs = new ArrayList<>();
        this.tables = new ArrayList<>();
        this.menu = new ArrayList<>();

        this.waitingCustomers = new LinkedList<>();
        this.orderQueue = new LinkedList<>();
        this.activeOrders = new ArrayList<>();
        this.allCustomers = new ArrayList<>();

        // 初始员工限制
        this.maxWaiters = 2;
        this.maxChefs = 2;
        this.unlockedWaiterTier = 1;  // 只能招普通服务员
        this.unlockedChefTier = 1;    // 只能招学徒厨师

        this.totalCustomersServed = 0;
        this.totalCustomersLost = 0;
        this.totalRevenue = 0;
        this.totalTips = 0;
        this.gameTime = 0;

        this.random = new Random();
        this.currentEvent = null;
        this.eventTimer = 0;

        this.gameLogs = new ArrayList<>();

        // 初始化默认配置
        initializeDefault();
    }

    /**
     * 初始化默认配置
     */
    private void initializeDefault() {
        // 初始员工
        waiters.add(new Waiter("小明"));
        chefs.add(new Chef("老王"));

        // 初始桌子
        tables.add(new Table(1, 2));
        tables.add(new Table(2, 2));
        tables.add(new Table(3, 4));

        // 初始菜单（1级菜品）
        menu.add(Dish.createEggFriedRice());
        menu.add(Dish.createTomatoEgg());
        menu.add(Dish.createMaPoTofu());
        menu.add(Dish.createKungPaoChicken());

        addLog("🏨 " + name + " 开业了！初始资金: $" + money);
    }

    /**
     * 游戏主循环 - 每秒调用一次
     */
    public void tick() {
        gameTime++;

        // 1. 处理随机事件
        updateEvent();

        // 2. 尝试生成新顾客
        trySpawnCustomer();

        // 3. 更新所有顾客状态
        updateCustomers();

        // 4. 更新所有员工状态
        updateEmployees();

        // 5. 服务员自动工作
        processWaiters();

        // 6. 厨师自动工作
        processChefs();

        // 7. 检查升级
        checkLevelUp();

        // 8. 清理已离开的顾客
        cleanupCustomers();
    }

    /**
     * 尝试生成新顾客
     */
    private void trySpawnCustomer() {
        // 基础概率：10% + 声望加成
        double spawnChance = 0.1 + (reputation * 0.003) + (hotelLevel * 0.02);
        
        // 特殊事件影响
        if ("雨天".equals(currentEvent)) {
            spawnChance *= 2;
        } else if ("隔壁开新店".equals(currentEvent)) {
            spawnChance *= 0.8;
        }

        if (random.nextDouble() < spawnChance) {
            Customer customer = new Customer();
            waitingCustomers.add(customer);
            allCustomers.add(customer);
            addLog("👋 新顾客 " + customer.getName() + " 来了！[携带$" + customer.getMoney() + "]");
        }
    }

    /**
     * 更新所有顾客状态
     */
    private void updateCustomers() {
        for (Customer customer : allCustomers) {
            Customer.CustomerState prevState = customer.getState();
            customer.tick();
            
            // 检查是否生气离开
            if (prevState != Customer.CustomerState.LEFT_ANGRY && 
                customer.getState() == Customer.CustomerState.LEFT_ANGRY) {
                handleAngryCustomer(customer);
            }
            
            // 检查是否吃完
            if (prevState == Customer.CustomerState.EATING && 
                customer.getState() == Customer.CustomerState.FINISHED) {
                handleFinishedCustomer(customer);
            }
        }
    }

    /**
     * 处理生气离开的顾客
     */
    private void handleAngryCustomer(Customer customer) {
        int penalty = 10 + random.nextInt(20);
        money = Math.max(0, money - penalty);
        reputation = Math.max(0, reputation + customer.getReputationEffect());
        totalCustomersLost++;
        
        if (customer.getTable() != null) {
            customer.getTable().clearTable();
        }
        
        addLog("😡 " + customer.getName() + " 等太久生气离开了！声望-" + 
               Math.abs(customer.getReputationEffect()) + " 金钱-$" + penalty);
    }

    /**
     * 处理吃完的顾客
     */
    private void handleFinishedCustomer(Customer customer) {
        int bill = customer.calculateBill();
        int tip = customer.calculateTip();
        
        money += bill + tip;
        totalRevenue += bill;
        totalTips += tip;
        reputation = Math.min(100, reputation + customer.getReputationEffect());
        hotelExp += bill / 10;
        totalCustomersServed++;
        
        // 小费分给服务员
        if (!waiters.isEmpty()) {
            Waiter w = waiters.get(random.nextInt(waiters.size()));
            w.receiveTip(tip);
        }
        
        if (customer.getTable() != null) {
            customer.getTable().clearTable();
        }
        customer.setState(Customer.CustomerState.LEFT_HAPPY);
        
        addLog("💰 " + customer.getName() + " 满意离开！账单$" + bill + " 小费$" + tip + 
               " 声望+" + customer.getReputationEffect());
    }

    /**
     * 更新所有员工状态（休息恢复等）
     */
    private void updateEmployees() {
        for (Waiter w : waiters) {
            w.rest();
        }
        for (Chef c : chefs) {
            c.rest();
        }
    }

    /**
     * 处理服务员工作
     */
    private void processWaiters() {
        for (Waiter waiter : waiters) {
            // 正在休息的服务员跳过
            if (waiter.isResting()) continue;

            switch (waiter.getCurrentTask()) {
                case NONE:
                    // 空闲时寻找工作（只有 canWork 时才分配新任务）
                    if (waiter.canWork()) {
                        assignWaiterTask(waiter);
                    }
                    break;
                    
                case GREETING:
                    // 迎客中
                    waiter.setTaskTimer(waiter.getTaskTimer() - 1);
                    if (waiter.getTaskTimer() <= 0) {
                        finishGreeting(waiter);
                    }
                    break;
                    
                case TAKING_ORDER:
                    // 点单中
                    waiter.setTaskTimer(waiter.getTaskTimer() - 1);
                    if (waiter.getTaskTimer() <= 0) {
                        finishTakingOrder(waiter);
                    }
                    break;
                    
                case DELIVERING:
                    // 上菜中
                    waiter.setTaskTimer(waiter.getTaskTimer() - 1);
                    if (waiter.getTaskTimer() <= 0) {
                        finishDelivering(waiter);
                    }
                    break;
            }
        }
    }

    /**
     * 分配服务员任务
     */
    private void assignWaiterTask(Waiter waiter) {
        // 优先级1：上菜
        for (Order order : activeOrders) {
            if (order.hasCompletedDishes()) {
                Dish dish = order.getCompletedDishForDelivery();
                waiter.setServingCustomer(order.getCustomer());
                waiter.startDelivering(order);
                addLog("🍽️ " + waiter.getName() + " 去上菜: " + dish.getName());
                return;
            }
        }

        // 优先级2：迎客
        if (!waitingCustomers.isEmpty()) {
            Table emptyTable = findEmptyTable();
            if (emptyTable != null) {
                Customer customer = waitingCustomers.poll();
                waiter.startGreeting(customer);
                addLog("👔 " + waiter.getName() + " 去迎接 " + customer.getName());
                return;
            }
        }

        // 优先级3：点单
        for (Customer customer : allCustomers) {
            if (customer.getState() == Customer.CustomerState.WAITING_ORDER) {
                waiter.setServingCustomer(customer);
                waiter.startTakingOrder();
                addLog("📝 " + waiter.getName() + " 去给 " + customer.getName() + " 点单");
                return;
            }
        }
    }

    /**
     * 完成迎客
     */
    private void finishGreeting(Waiter waiter) {
        Customer customer = waiter.getServingCustomer();
        Table table = findEmptyTable();
        
        if (table != null && customer != null) {
            // 检查是否出错（滑倒等）
            if (waiter.checkError() && "雨天".equals(currentEvent)) {
                addLog("💦 " + waiter.getName() + " 滑倒了！");
                waiter.consumeStamina(10);
            }
            
            table.seatCustomer(customer);
            waiter.gainExp(5);
            addLog("✅ " + customer.getName() + " 入座桌" + table.getId());
        }
        
        waiter.setServingCustomer(null);
        waiter.setCurrentTask(Waiter.WaiterTask.NONE);
        waiter.setBusy(false);
    }

    /**
     * 完成点单
     */
    private void finishTakingOrder(Waiter waiter) {
        Customer customer = waiter.getServingCustomer();
        
        if (customer != null && customer.getState() == Customer.CustomerState.WAITING_ORDER) {
            customer.orderDishes(menu);
            Order order = new Order(customer, gameTime);
            orderQueue.add(order);
            activeOrders.add(order);
            waiter.gainExp(10);
            
            StringBuilder orderStr = new StringBuilder();
            for (Dish d : customer.getOrders()) {
                orderStr.append(d.getName()).append(" ");
            }
            addLog("📋 " + customer.getName() + " 点了: " + orderStr.toString());
        }
        
        waiter.setServingCustomer(null);
        waiter.setCurrentTask(Waiter.WaiterTask.NONE);
        waiter.setBusy(false);
    }

    /**
     * 完成上菜
     */
    private void finishDelivering(Waiter waiter) {
        Order order = waiter.getCarryingOrder();
        
        if (order != null && order.getCustomer() != null) {
            // 检查是否出错（摔盘子）
            if (waiter.checkError()) {
                addLog("💔 " + waiter.getName() + " 摔碎了盘子！");
                money = Math.max(0, money - 20);
            } else {
                // 找到对应的菜品
                Dish deliveredDish = null;
                for (Dish d : order.getDishes()) {
                    if (!order.getCompletedDishes().contains(d) && 
                        !order.getPendingDishes().contains(d)) {
                        deliveredDish = d;
                        break;
                    }
                }
                
                Customer customer = order.getCustomer();
                if (deliveredDish != null) {
                    customer.receiveDish(deliveredDish);
                    addLog("🍽️ 给 " + customer.getName() + " 上了 " + deliveredDish.getName());
                }
                
                waiter.gainExp(15);
            }
            
            // 检查订单是否完全送达
            if (order.isFullyDelivered()) {
                order.markDelivered();
                activeOrders.remove(order);
            }
        }
        
        waiter.finishTask();
    }

    /**
     * 处理厨师工作
     */
    private void processChefs() {
        for (Chef chef : chefs) {
            // 正在休息的厨师跳过
            if (chef.isResting()) continue;

            if (chef.isBusy()) {
                // 正在做菜
                chef.setCookTimer(chef.getCookTimer() - 1);
                if (chef.getCookTimer() <= 0) {
                    finishCooking(chef);
                }
            } else if (chef.canWork()) {
                // 空闲时从订单队列取菜做
                assignChefTask(chef);
            }
        }
    }

    /**
     * 分配厨师任务
     */
    private void assignChefTask(Chef chef) {
        for (Order order : activeOrders) {
            Dish dish = order.getNextDishToCook();
            if (dish != null && chef.canCook(dish)) {
                Dish toCook = order.startCookingDish();
                chef.startCooking(order, toCook);
                addLog("🔥 " + chef.getName() + " 开始做 " + toCook.getName() + 
                       " [需要" + chef.getCookTime(toCook) + "s]");
                return;
            }
        }
    }

    /**
     * 完成烹饪
     */
    private void finishCooking(Chef chef) {
        Order order = chef.getCurrentOrder();
        Dish dish = chef.getCurrentDish();
        
        if (order != null && dish != null) {
            // 检查是否把菜做焦了
            if (chef.checkBurnDish()) {
                addLog("🔥 " + chef.getName() + " 把 " + dish.getName() + " 做焦了！重做...");
                // 重新加入队列
                order.getPendingDishes().add(0, dish);
                chef.consumeStamina(5);
            } else {
                order.completeDish(dish);
                addLog("✅ " + chef.getName() + " 完成了 " + dish.getName());
            }
        }
        
        chef.finishCooking();
    }

    /**
     * 找空桌子
     */
    private Table findEmptyTable() {
        for (Table t : tables) {
            if (t.isAvailable()) {
                return t;
            }
        }
        return null;
    }

    /**
     * 检查升级
     */
    private void checkLevelUp() {
        while (hotelExp >= expToNextLevel && hotelLevel < 10) {
            hotelExp -= expToNextLevel;
            hotelLevel++;
            expToNextLevel = (int)(expToNextLevel * 1.5);
            onHotelLevelUp();
        }
    }

    /**
     * 旅店升级
     */
    private void onHotelLevelUp() {
        addLog("🎊 旅店升级到 Lv." + hotelLevel + "！");
        
        // 解锁新内容
        switch (hotelLevel) {
            case 2:
                menu.add(Dish.createSweetSourPork());
                menu.add(Dish.createBraisedPork());
                maxWaiters = 3;
                maxChefs = 3;
                addLog("🍳 解锁新菜品：糖醋里脊、红烧肉");
                addLog("👥 员工槽位扩展！服务员/厨师上限: 3人");
                break;
            case 3:
                menu.add(Dish.createSteamedFish());
                menu.add(Dish.createPekingDuck());
                tables.add(new Table(tables.size() + 1, 4));
                unlockedWaiterTier = 2;  // 解锁高级服务员
                unlockedChefTier = 2;    // 解锁大厨
                addLog("🍳 解锁新菜品：清蒸鱼、北京烤鸭 | 新增1桌");
                addLog("⭐ 解锁高级员工：高级服务员、大厨！");
                break;
            case 4:
                menu.add(Dish.createLobster());
                tables.add(new Table(tables.size() + 1, 6));
                maxWaiters = 4;
                maxChefs = 4;
                addLog("🍳 解锁新菜品：龙虾 | 新增1大桌");
                addLog("👥 员工槽位扩展！服务员/厨师上限: 4人");
                break;
            case 5:
                menu.add(Dish.createWagyuSteak());
                unlockedWaiterTier = 3;  // 解锁金牌服务员
                unlockedChefTier = 3;    // 解锁主厨
                addLog("🍳 解锁终极菜品：和牛牛排！");
                addLog("🏆 解锁顶级员工：金牌服务员、主厨！");
                break;
            case 6:
                maxWaiters = 5;
                maxChefs = 5;
                tables.add(new Table(tables.size() + 1, 8));
                addLog("👥 员工槽位扩展！服务员/厨师上限: 5人");
                addLog("🪑 新增VIP大桌（8人）！");
                break;
        }
        
        // 增加最大座位等
        reputation = Math.min(100, reputation + 5);
    }

    /**
     * 清理已离开的顾客
     */
    private void cleanupCustomers() {
        allCustomers.removeIf(c -> 
            c.getState() == Customer.CustomerState.LEFT_ANGRY || 
            c.getState() == Customer.CustomerState.LEFT_HAPPY);
    }

    /**
     * 更新随机事件
     */
    private void updateEvent() {
        if (eventTimer > 0) {
            eventTimer--;
            if (eventTimer == 0) {
                addLog("📢 事件结束：" + currentEvent);
                currentEvent = null;
            }
            return;
        }

        // 2%概率触发新事件
        if (random.nextDouble() < 0.02) {
            triggerRandomEvent();
        }
    }

    /**
     * 触发随机事件
     */
    private void triggerRandomEvent() {
        int eventType = random.nextInt(4);
        
        switch (eventType) {
            case 0:
                currentEvent = "雨天";
                eventTimer = 30;
                addLog("🌧️ 【事件】下雨了！顾客翻倍但服务员容易滑倒");
                break;
            case 1:
                currentEvent = "隔壁开新店";
                eventTimer = 40;
                addLog("🏪 【事件】隔壁开了新店！顾客减少20%");
                break;
            case 2:
                currentEvent = "美食评论家";
                eventTimer = 1; // 只持续1秒
                // 这里可以加特殊逻辑
                addLog("👨‍💼 【事件】美食评论家来了！表现好声望大涨！");
                reputation = Math.min(100, reputation + 10);
                break;
            case 3:
                currentEvent = "员工吵架";
                eventTimer = 15;
                // 随机让两个员工休息
                if (!waiters.isEmpty()) {
                    waiters.get(random.nextInt(waiters.size())).startResting();
                }
                if (!chefs.isEmpty()) {
                    chefs.get(random.nextInt(chefs.size())).startResting();
                }
                addLog("😤 【事件】员工吵架了！部分员工暂时无法工作");
                break;
        }
    }

    /**
     * 添加日志
     */
    private void addLog(String log) {
        String timeStr = String.format("[%02d:%02d]", gameTime / 60, gameTime % 60);
        gameLogs.add(timeStr + " " + log);
        if (gameLogs.size() > MAX_LOGS) {
            gameLogs.remove(0);
        }
    }

    /**
     * 招聘服务员（普通）
     */
    public boolean hireWaiter(String name, int cost) {
        return hireWaiter(name, cost, 1);
    }

    /**
     * 招聘服务员（指定等级）
     * @param tier 1=普通($200), 2=高级($500), 3=金牌($1000)
     */
    public boolean hireWaiter(String name, int cost, int tier) {
        if (waiters.size() >= maxWaiters) {
            addLog("❌ 服务员已满员（" + maxWaiters + "人）！升级餐厅可扩展");
            return false;
        }
        if (tier > unlockedWaiterTier) {
            addLog("❌ 尚未解锁该等级服务员！需要餐厅等级更高");
            return false;
        }
        if (money >= cost) {
            money -= cost;
            Waiter waiter = new Waiter(name);
            // 根据等级设置初始属性
            if (tier >= 2) {
                waiter.gainExp(100);  // 高级服务员初始1级经验
                addLog("⭐ 招聘了高级服务员：" + name + " (自带经验)");
            } else if (tier >= 3) {
                waiter.gainExp(250);  // 金牌服务员初始更高
                addLog("🏆 招聘了金牌服务员：" + name + " (精英级别)");
            } else {
                addLog("👔 招聘了新服务员：" + name);
            }
            waiters.add(waiter);
            return true;
        }
        return false;
    }

    /**
     * 招聘厨师（普通）
     */
    public boolean hireChef(String name, int cost) {
        return hireChef(name, cost, 1);
    }

    /**
     * 招聘厨师（指定等级）
     * @param tier 1=学徒($300), 2=大厨($800), 3=主厨($1500)
     */
    public boolean hireChef(String name, int cost, int tier) {
        if (chefs.size() >= maxChefs) {
            addLog("❌ 厨师已满员（" + maxChefs + "人）！升级餐厅可扩展");
            return false;
        }
        if (tier > unlockedChefTier) {
            addLog("❌ 尚未解锁该等级厨师！需要餐厅等级更高");
            return false;
        }
        if (money >= cost) {
            money -= cost;
            Chef chef = new Chef(name);
            // 根据等级设置初始属性
            if (tier >= 2) {
                chef.gainExp(100);  // 大厨初始1级经验
                addLog("⭐ 招聘了大厨：" + name + " (自带经验)");
            } else if (tier >= 3) {
                chef.gainExp(250);  // 主厨初始更高
                addLog("🏆 招聘了主厨：" + name + " (大师级别)");
            } else {
                addLog("👨‍🍳 招聘了学徒厨师：" + name);
            }
            chefs.add(chef);
            return true;
        }
        return false;
    }

    /**
     * 购买桌子
     */
    public boolean buyTable(int seats, int cost) {
        if (money >= cost) {
            money -= cost;
            tables.add(new Table(tables.size() + 1, seats));
            addLog("🪑 购买了新桌子：" + seats + "人桌");
            return true;
        }
        return false;
    }

    /**
     * 花费金钱
     */
    public void spendMoney(int amount) {
        money = Math.max(0, money - amount);
    }

    /**
     * 添加旅店经验（用于手动升级）
     */
    public void addHotelExp(int amount) {
        hotelExp += amount;
        checkLevelUp();
    }

    /**
     * 从存档数据恢复（由SaveManager调用）
     */
    public void restoreFromSave(Object saveDataObj) {
        // 使用反射获取SaveData内部类的数据
        try {
            Class<?> clazz = saveDataObj.getClass();
            
            this.money = (int) clazz.getDeclaredField("money").get(saveDataObj);
            this.reputation = (int) clazz.getDeclaredField("reputation").get(saveDataObj);
            this.hotelLevel = (int) clazz.getDeclaredField("hotelLevel").get(saveDataObj);
            this.hotelExp = (int) clazz.getDeclaredField("hotelExp").get(saveDataObj);
            this.expToNextLevel = (int) clazz.getDeclaredField("expToNextLevel").get(saveDataObj);
            this.gameTime = (int) clazz.getDeclaredField("gameTime").get(saveDataObj);
            this.maxWaiters = (int) clazz.getDeclaredField("maxWaiters").get(saveDataObj);
            this.maxChefs = (int) clazz.getDeclaredField("maxChefs").get(saveDataObj);
            this.unlockedWaiterTier = (int) clazz.getDeclaredField("unlockedWaiterTier").get(saveDataObj);
            this.unlockedChefTier = (int) clazz.getDeclaredField("unlockedChefTier").get(saveDataObj);
            this.totalCustomersServed = (int) clazz.getDeclaredField("totalCustomersServed").get(saveDataObj);
            this.totalCustomersLost = (int) clazz.getDeclaredField("totalCustomersLost").get(saveDataObj);
            this.totalRevenue = (int) clazz.getDeclaredField("totalRevenue").get(saveDataObj);
            this.totalTips = (int) clazz.getDeclaredField("totalTips").get(saveDataObj);

            // 恢复桌子
            @SuppressWarnings("unchecked")
            List<Integer> tableSeats = (List<Integer>) clazz.getDeclaredField("tableSeats").get(saveDataObj);
            this.tables.clear();
            int tableId = 1;
            for (int seats : tableSeats) {
                this.tables.add(new Table(tableId++, seats));
            }

            // 恢复菜单
            @SuppressWarnings("unchecked")
            List<String> menuDishNames = (List<String>) clazz.getDeclaredField("menuDishNames").get(saveDataObj);
            this.menu.clear();
            for (String dishName : menuDishNames) {
                Dish dish = createDishByName(dishName);
                if (dish != null) {
                    this.menu.add(dish);
                }
            }

            // 恢复服务员
            @SuppressWarnings("unchecked")
            List<?> waiterDataList = (List<?>) clazz.getDeclaredField("waiterDataList").get(saveDataObj);
            this.waiters.clear();
            for (Object empData : waiterDataList) {
                Waiter waiter = restoreWaiter(empData);
                if (waiter != null) {
                    this.waiters.add(waiter);
                }
            }

            // 恢复厨师
            @SuppressWarnings("unchecked")
            List<?> chefDataList = (List<?>) clazz.getDeclaredField("chefDataList").get(saveDataObj);
            this.chefs.clear();
            for (Object empData : chefDataList) {
                Chef chef = restoreChef(empData);
                if (chef != null) {
                    this.chefs.add(chef);
                }
            }

            addLog("📂 存档已加载！继续你的餐厅帝国之旅！");
            
        } catch (Exception e) {
            System.out.println("恢复存档时出错: " + e.getMessage());
        }
    }

    /**
     * 根据菜品名创建菜品
     */
    private Dish createDishByName(String name) {
        switch (name) {
            case "蛋炒饭": return Dish.createEggFriedRice();
            case "番茄炒蛋": return Dish.createTomatoEgg();
            case "麻婆豆腐": return Dish.createMaPoTofu();
            case "宫保鸡丁": return Dish.createKungPaoChicken();
            case "糖醋里脊": return Dish.createSweetSourPork();
            case "红烧肉": return Dish.createBraisedPork();
            case "清蒸鱼": return Dish.createSteamedFish();
            case "北京烤鸭": return Dish.createPekingDuck();
            case "龙虾": return Dish.createLobster();
            case "和牛牛排": return Dish.createWagyuSteak();
            default: return null;
        }
    }

    /**
     * 从存档数据恢复服务员
     */
    private Waiter restoreWaiter(Object empData) {
        try {
            Class<?> clazz = empData.getClass();
            String name = (String) clazz.getDeclaredField("name").get(empData);
            Waiter waiter = new Waiter(name);
            
            // 使用反射设置属性
            setEmployeeFields(waiter, empData);
            
            return waiter;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从存档数据恢复厨师
     */
    private Chef restoreChef(Object empData) {
        try {
            Class<?> clazz = empData.getClass();
            String name = (String) clazz.getDeclaredField("name").get(empData);
            Chef chef = new Chef(name);
            
            // 使用反射设置属性
            setEmployeeFields(chef, empData);
            
            return chef;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 设置员工属性
     */
    private void setEmployeeFields(Employee emp, Object empData) throws Exception {
        Class<?> dataClass = empData.getClass();
        Class<?> empClass = emp.getClass().getSuperclass(); // Employee类
        
        // 获取并设置属性
        java.lang.reflect.Field levelField = empClass.getDeclaredField("level");
        levelField.setAccessible(true);
        levelField.set(emp, dataClass.getDeclaredField("level").get(empData));
        
        java.lang.reflect.Field expField = empClass.getDeclaredField("exp");
        expField.setAccessible(true);
        expField.set(emp, dataClass.getDeclaredField("exp").get(empData));
        
        java.lang.reflect.Field expNextField = empClass.getDeclaredField("expToNextLevel");
        expNextField.setAccessible(true);
        expNextField.set(emp, dataClass.getDeclaredField("expToNextLevel").get(empData));
        
        java.lang.reflect.Field profField = empClass.getDeclaredField("proficiency");
        profField.setAccessible(true);
        profField.set(emp, dataClass.getDeclaredField("proficiency").get(empData));
        
        java.lang.reflect.Field staminaField = empClass.getDeclaredField("stamina");
        staminaField.setAccessible(true);
        staminaField.set(emp, dataClass.getDeclaredField("stamina").get(empData));
        
        java.lang.reflect.Field maxStaminaField = empClass.getDeclaredField("maxStamina");
        maxStaminaField.setAccessible(true);
        maxStaminaField.set(emp, dataClass.getDeclaredField("maxStamina").get(empData));
    }

    // Getters
    public String getName() { return name; }
    public int getMoney() { return money; }
    public int getReputation() { return reputation; }
    public int getHotelLevel() { return hotelLevel; }
    public int getHotelExp() { return hotelExp; }
    public int getExpToNextLevel() { return expToNextLevel; }
    public List<Waiter> getWaiters() { return waiters; }
    public List<Chef> getChefs() { return chefs; }
    public List<Table> getTables() { return tables; }
    public List<Dish> getMenu() { return menu; }
    public Queue<Customer> getWaitingCustomers() { return waitingCustomers; }
    public List<Order> getActiveOrders() { return activeOrders; }
    public List<Customer> getAllCustomers() { return allCustomers; }
    public int getTotalCustomersServed() { return totalCustomersServed; }
    public int getTotalCustomersLost() { return totalCustomersLost; }
    public int getTotalRevenue() { return totalRevenue; }
    public int getTotalTips() { return totalTips; }
    public int getGameTime() { return gameTime; }
    public String getCurrentEvent() { return currentEvent; }
    public List<String> getGameLogs() { return gameLogs; }
    public int getMaxWaiters() { return maxWaiters; }
    public int getMaxChefs() { return maxChefs; }
    public int getUnlockedWaiterTier() { return unlockedWaiterTier; }
    public int getUnlockedChefTier() { return unlockedChefTier; }
}

