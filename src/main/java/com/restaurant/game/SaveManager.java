package com.restaurant.game;

import com.restaurant.model.*;

import java.io.*;
import java.util.*;

/**
 * 存档管理器 - 负责游戏数据的保存和加载
 */
public class SaveManager {
    private static final String SAVE_FILE = "restaurant_save.dat";
    private static final String SAVE_DIR = getSaveDirectory();

    /**
     * 获取存档目录（跨平台兼容）
     */
    private static String getSaveDirectory() {
        String userHome = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase();
        
        if (os.contains("win")) {
            // Windows: AppData/Local
            return System.getenv("LOCALAPPDATA") + File.separator + "RestaurantOwner";
        } else if (os.contains("mac")) {
            // macOS
            return userHome + "/Library/Application Support/RestaurantOwner";
        } else {
            // Linux 等
            return userHome + "/.restaurantowner";
        }
    }

    /**
     * 保存游戏
     */
    public static boolean saveGame(Hotel hotel) {
        try {
            // 创建存档目录
            File dir = new File(SAVE_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String savePath = SAVE_DIR + File.separator + SAVE_FILE;
            
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(savePath))) {
                
                SaveData data = new SaveData();
                data.extractFrom(hotel);
                oos.writeObject(data);
                
                System.out.println("💾 游戏已保存到: " + savePath);
                return true;
            }
        } catch (IOException e) {
            System.out.println("❌ 保存失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 加载游戏
     */
    public static Hotel loadGame() {
        String savePath = SAVE_DIR + File.separator + SAVE_FILE;
        File saveFile = new File(savePath);
        
        if (!saveFile.exists()) {
            System.out.println("📂 没有找到存档，开始新游戏...");
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(savePath))) {
            
            SaveData data = (SaveData) ois.readObject();
            Hotel hotel = data.restoreToHotel();
            
            System.out.println("💾 存档加载成功！");
            return hotel;
            
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("❌ 读取存档失败: " + e.getMessage());
            System.out.println("📂 开始新游戏...");
            return null;
        }
    }

    /**
     * 检查是否有存档
     */
    public static boolean hasSaveFile() {
        String savePath = SAVE_DIR + File.separator + SAVE_FILE;
        return new File(savePath).exists();
    }

    /**
     * 删除存档
     */
    public static boolean deleteSave() {
        String savePath = SAVE_DIR + File.separator + SAVE_FILE;
        File saveFile = new File(savePath);
        if (saveFile.exists()) {
            return saveFile.delete();
        }
        return false;
    }

    /**
     * 存档数据类 - 保存所有需要持久化的数据
     */
    private static class SaveData implements Serializable {
        private static final long serialVersionUID = 1L;

        // 旅店基础数据
        String hotelName;
        int money;
        int reputation;
        int hotelLevel;
        int hotelExp;
        int expToNextLevel;
        int gameTime;
        
        // 员工限制
        int maxWaiters;
        int maxChefs;
        int unlockedWaiterTier;
        int unlockedChefTier;

        // 统计数据
        int totalCustomersServed;
        int totalCustomersLost;
        int totalRevenue;
        int totalTips;

        // 员工数据
        List<EmployeeData> waiterDataList = new ArrayList<>();
        List<EmployeeData> chefDataList = new ArrayList<>();

        // 桌子数量
        List<Integer> tableSeats = new ArrayList<>();

        // 菜单（只保存菜品名，加载时重建）
        List<String> menuDishNames = new ArrayList<>();

        /**
         * 从Hotel对象提取数据
         */
        void extractFrom(Hotel hotel) {
            this.hotelName = hotel.getName();
            this.money = hotel.getMoney();
            this.reputation = hotel.getReputation();
            this.hotelLevel = hotel.getHotelLevel();
            this.hotelExp = hotel.getHotelExp();
            this.expToNextLevel = hotel.getExpToNextLevel();
            this.gameTime = hotel.getGameTime();
            this.maxWaiters = hotel.getMaxWaiters();
            this.maxChefs = hotel.getMaxChefs();
            this.unlockedWaiterTier = hotel.getUnlockedWaiterTier();
            this.unlockedChefTier = hotel.getUnlockedChefTier();
            this.totalCustomersServed = hotel.getTotalCustomersServed();
            this.totalCustomersLost = hotel.getTotalCustomersLost();
            this.totalRevenue = hotel.getTotalRevenue();
            this.totalTips = hotel.getTotalTips();

            // 保存服务员数据
            for (Waiter w : hotel.getWaiters()) {
                waiterDataList.add(new EmployeeData(w));
            }

            // 保存厨师数据
            for (Chef c : hotel.getChefs()) {
                chefDataList.add(new EmployeeData(c));
            }

            // 保存桌子
            for (Table t : hotel.getTables()) {
                tableSeats.add(t.getSeats());
            }

            // 保存菜单
            for (Dish d : hotel.getMenu()) {
                menuDishNames.add(d.getName());
            }
        }

        /**
         * 恢复到Hotel对象
         */
        Hotel restoreToHotel() {
            Hotel hotel = new Hotel(hotelName);
            
            // 使用反射或setter恢复数据（这里用公共方法）
            hotel.restoreFromSave(this);
            
            return hotel;
        }
    }

    /**
     * 员工数据（用于序列化）
     */
    private static class EmployeeData implements Serializable {
        private static final long serialVersionUID = 1L;
        
        String name;
        int level;
        int exp;
        int expToNextLevel;
        int proficiency;
        int stamina;
        int maxStamina;
        int totalTips;       // 服务员专用
        int totalDishesCooked; // 厨师专用
        int maxDishLevel;    // 厨师专用

        EmployeeData(Employee e) {
            this.name = e.getName();
            this.level = e.getLevel();
            this.exp = e.getExp();
            this.expToNextLevel = e.getExpToNextLevel();
            this.proficiency = e.getProficiency();
            this.stamina = e.getStamina();
            this.maxStamina = e.getMaxStamina();
            
            if (e instanceof Waiter) {
                this.totalTips = ((Waiter) e).getTotalTips();
            } else if (e instanceof Chef) {
                this.totalDishesCooked = ((Chef) e).getTotalDishesCooked();
                this.maxDishLevel = ((Chef) e).getMaxDishLevel();
            }
        }
    }
}

