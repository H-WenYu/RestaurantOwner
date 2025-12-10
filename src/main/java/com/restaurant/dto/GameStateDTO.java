package com.restaurant.dto;

import com.restaurant.game.Hotel;
import com.restaurant.model.*;
import com.restaurant.session.GameSession;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 游戏状态DTO - 返回给前端的完整游戏状态
 */
public class GameStateDTO {

    // 基础信息
    private String hotelName;
    private int money;
    private int reputation;
    private int hotelLevel;
    private int hotelExp;
    private int expToNextLevel;
    private int gameTime;
    private boolean running;

    // 统计
    private int totalCustomersServed;
    private int totalCustomersLost;
    private int totalRevenue;
    private int totalTips;
    private double shopRating;
    private int totalRatings;
    private int goodRatings;

    // 容量限制
    private int maxWaiters;
    private int maxChefs;
    private int maxTables;

    // 员工
    private List<EmployeeDTO> waiters;
    private List<EmployeeDTO> chefs;

    // 顾客
    private int waitingCustomers;
    private int seatedCustomers;
    private List<CustomerDTO> allCustomers;

    // 菜单
    private List<DishDTO> activeMenu;
    private List<DishDTO> unlockedDishes;

    // 桌子
    private int tableCount;
    private int occupiedTables;

    // 事件
    private String currentEvent;

    // 日志
    private List<String> recentLogs;

    public GameStateDTO() {
    }

    public static GameStateDTO fromSession(GameSession session) {
        if (session == null || !session.hasGame()) {
            return null;
        }

        Hotel hotel = session.getHotel();
        GameStateDTO dto = new GameStateDTO();

        // 基础信息
        dto.hotelName = hotel.getName();
        dto.money = hotel.getMoney();
        dto.reputation = hotel.getReputation();
        dto.hotelLevel = hotel.getHotelLevel();
        dto.hotelExp = hotel.getHotelExp();
        dto.expToNextLevel = hotel.getExpToNextLevel();
        dto.gameTime = hotel.getGameTime();
        dto.running = session.isRunning();

        // 统计
        dto.totalCustomersServed = hotel.getTotalCustomersServed();
        dto.totalCustomersLost = hotel.getTotalCustomersLost();
        dto.totalRevenue = hotel.getTotalRevenue();
        dto.totalTips = hotel.getTotalTips();
        dto.shopRating = hotel.getShopRating();
        dto.totalRatings = hotel.getTotalRatings();
        dto.goodRatings = hotel.getGoodRatings();

        // 容量
        dto.maxWaiters = hotel.getMaxWaiters();
        dto.maxChefs = hotel.getMaxChefs();
        dto.maxTables = hotel.getMaxTables();

        // 员工
        dto.waiters = hotel.getWaiters().stream()
                .map(EmployeeDTO::fromWaiter)
                .collect(Collectors.toList());
        dto.chefs = hotel.getChefs().stream()
                .map(EmployeeDTO::fromChef)
                .collect(Collectors.toList());

        // 顾客
        dto.waitingCustomers = hotel.getWaitingCustomers().size();
        dto.allCustomers = hotel.getAllCustomers().stream()
                .map(CustomerDTO::fromCustomer)
                .collect(Collectors.toList());
        dto.seatedCustomers = (int) hotel.getAllCustomers().stream()
                .filter(c -> c.getTable() != null)
                .count();

        // 菜单
        dto.activeMenu = hotel.getActiveMenu().stream()
                .map(DishDTO::fromDishInstance)
                .collect(Collectors.toList());
        dto.unlockedDishes = hotel.getUnlockedDishes().values().stream()
                .map(DishDTO::fromDishInstance)
                .collect(Collectors.toList());

        // 桌子
        dto.tableCount = hotel.getTables().size();
        dto.occupiedTables = (int) hotel.getTables().stream()
                .filter(t -> !t.isAvailable())
                .count();

        // 事件和日志
        dto.currentEvent = hotel.getCurrentEvent();
        List<String> logs = hotel.getGameLogs();
        dto.recentLogs = logs.size() > 10 ? logs.subList(logs.size() - 10, logs.size()) : new ArrayList<>(logs);

        return dto;
    }

    // Getters (省略setter以节省空间，Spring会自动处理)
    public String getHotelName() {
        return hotelName;
    }

    public int getMoney() {
        return money;
    }

    public int getReputation() {
        return reputation;
    }

    public int getHotelLevel() {
        return hotelLevel;
    }

    public int getHotelExp() {
        return hotelExp;
    }

    public int getExpToNextLevel() {
        return expToNextLevel;
    }

    public int getGameTime() {
        return gameTime;
    }

    public boolean isRunning() {
        return running;
    }

    public int getTotalCustomersServed() {
        return totalCustomersServed;
    }

    public int getTotalCustomersLost() {
        return totalCustomersLost;
    }

    public int getTotalRevenue() {
        return totalRevenue;
    }

    public int getTotalTips() {
        return totalTips;
    }

    public double getShopRating() {
        return shopRating;
    }

    public int getTotalRatings() {
        return totalRatings;
    }

    public int getGoodRatings() {
        return goodRatings;
    }

    public int getMaxWaiters() {
        return maxWaiters;
    }

    public int getMaxChefs() {
        return maxChefs;
    }

    public int getMaxTables() {
        return maxTables;
    }

    public List<EmployeeDTO> getWaiters() {
        return waiters;
    }

    public List<EmployeeDTO> getChefs() {
        return chefs;
    }

    public int getWaitingCustomers() {
        return waitingCustomers;
    }

    public int getSeatedCustomers() {
        return seatedCustomers;
    }

    public List<CustomerDTO> getAllCustomers() {
        return allCustomers;
    }

    public List<DishDTO> getActiveMenu() {
        return activeMenu;
    }

    public List<DishDTO> getUnlockedDishes() {
        return unlockedDishes;
    }

    public int getTableCount() {
        return tableCount;
    }

    public int getOccupiedTables() {
        return occupiedTables;
    }

    public String getCurrentEvent() {
        return currentEvent;
    }

    public List<String> getRecentLogs() {
        return recentLogs;
    }
}
