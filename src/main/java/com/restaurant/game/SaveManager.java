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

    private static String getSaveDirectory() {
        String userHome = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase();
        
        if (os.contains("win")) {
            return System.getenv("LOCALAPPDATA") + File.separator + "RestaurantOwner";
        } else if (os.contains("mac")) {
            return userHome + "/Library/Application Support/RestaurantOwner";
        } else {
            return userHome + "/.restaurantowner";
        }
    }

    /**
     * 保存游戏
     */
    public static boolean saveGame(Hotel hotel) {
        try {
            File dir = new File(SAVE_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String savePath = SAVE_DIR + File.separator + SAVE_FILE;
            
            try (DataOutputStream dos = new DataOutputStream(
                    new BufferedOutputStream(new FileOutputStream(savePath)))) {
                
                // 版本号
                dos.writeInt(3); // 存档版本（v3增加了maxTables）
                
                // 基础数据
                dos.writeUTF(hotel.getName());
                dos.writeInt(hotel.getMoney());
                dos.writeInt(hotel.getReputation());
                dos.writeInt(hotel.getHotelLevel());
                dos.writeInt(hotel.getHotelExp());
                dos.writeInt(hotel.getExpToNextLevel());
                dos.writeInt(hotel.getGameTime());
                dos.writeInt(hotel.getMaxWaiters());
                dos.writeInt(hotel.getMaxChefs());
                dos.writeInt(hotel.getUnlockedWaiterTier());
                dos.writeInt(hotel.getUnlockedChefTier());
                dos.writeInt(hotel.getMaxTables());
                
                // 统计数据
                dos.writeInt(hotel.getTotalCustomersServed());
                dos.writeInt(hotel.getTotalCustomersLost());
                dos.writeInt(hotel.getTotalRevenue());
                dos.writeInt(hotel.getTotalTips());
                
                // 评分数据
                dos.writeDouble(hotel.getShopRating());
                dos.writeInt(hotel.getTotalRatings());
                dos.writeInt(hotel.getGoodRatings());
                
                // 服务员
                List<Waiter> waiters = hotel.getWaiters();
                dos.writeInt(waiters.size());
                for (Waiter w : waiters) {
                    saveEmployee(dos, w);
                    dos.writeInt(w.getTotalTips());
                    dos.writeInt(w.getServiceSkill());
                    dos.writeInt(w.getSpeedSkill());
                    dos.writeInt(w.getCharmSkill());
                    dos.writeInt(w.getEmployeeTier());
                }
                
                // 厨师
                List<Chef> chefs = hotel.getChefs();
                dos.writeInt(chefs.size());
                for (Chef c : chefs) {
                    saveEmployee(dos, c);
                    dos.writeInt(c.getTotalDishesCooked());
                    dos.writeInt(c.getMaxDishLevel());
                    dos.writeInt(c.getCookingSkill());
                    dos.writeInt(c.getSpeedSkill());
                    dos.writeInt(c.getCharmSkill());
                    dos.writeInt(c.getEmployeeTier());
                }
                
                // 桌子
                List<Table> tables = hotel.getTables();
                dos.writeInt(tables.size());
                for (Table t : tables) {
                    dos.writeInt(t.getSeats());
                    dos.writeInt(t.getLevel());
                }
                
                // 菜单
                List<Dish> menu = hotel.getMenu();
                dos.writeInt(menu.size());
                for (Dish d : menu) {
                    dos.writeUTF(d.getName());
                }
                
                System.out.println("💾 游戏已保存到: " + savePath);
                System.out.println("   资金: $" + hotel.getMoney() + " | 等级: Lv." + hotel.getHotelLevel());
                return true;
            }
        } catch (IOException e) {
            System.out.println("❌ 保存失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static void saveEmployee(DataOutputStream dos, Employee e) throws IOException {
        dos.writeUTF(e.getName());
        dos.writeInt(e.getLevel());
        dos.writeInt(e.getExp());
        dos.writeInt(e.getExpToNextLevel());
        dos.writeInt(e.getProficiency());
        dos.writeInt(e.getStamina());
        dos.writeInt(e.getMaxStamina());
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

        try (DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(savePath)))) {
            
            int version = dis.readInt();
            if (version < 3) {
                System.out.println("⚠️ 旧版存档不兼容（需要v3+），开始新游戏...");
                return null;
            }
            
            // 基础数据
            String name = dis.readUTF();
            int money = dis.readInt();
            int reputation = dis.readInt();
            int hotelLevel = dis.readInt();
            int hotelExp = dis.readInt();
            int expToNextLevel = dis.readInt();
            int gameTime = dis.readInt();
            int maxWaiters = dis.readInt();
            int maxChefs = dis.readInt();
            int unlockedWaiterTier = dis.readInt();
            int unlockedChefTier = dis.readInt();
            int maxTables = dis.readInt();
            
            // 统计数据
            int totalCustomersServed = dis.readInt();
            int totalCustomersLost = dis.readInt();
            int totalRevenue = dis.readInt();
            int totalTips = dis.readInt();
            
            // 评分数据
            double shopRating = dis.readDouble();
            int totalRatings = dis.readInt();
            int goodRatings = dis.readInt();
            
            // 创建Hotel并恢复数据
            Hotel hotel = new Hotel(name, false); // 不初始化默认数据
            hotel.setMoney(money);
            hotel.setReputation(reputation);
            hotel.setHotelLevel(hotelLevel);
            hotel.setHotelExp(hotelExp);
            hotel.setExpToNextLevel(expToNextLevel);
            hotel.setGameTime(gameTime);
            hotel.setMaxWaiters(maxWaiters);
            hotel.setMaxChefs(maxChefs);
            hotel.setUnlockedWaiterTier(unlockedWaiterTier);
            hotel.setUnlockedChefTier(unlockedChefTier);
            hotel.setMaxTables(maxTables);
            hotel.setTotalCustomersServed(totalCustomersServed);
            hotel.setTotalCustomersLost(totalCustomersLost);
            hotel.setTotalRevenue(totalRevenue);
            hotel.setTotalTips(totalTips);
            hotel.setShopRating(shopRating);
            hotel.setTotalRatings(totalRatings);
            hotel.setGoodRatings(goodRatings);
            
            // 恢复服务员
            int waiterCount = dis.readInt();
            for (int i = 0; i < waiterCount; i++) {
                Waiter w = loadWaiter(dis);
                hotel.getWaiters().add(w);
            }
            
            // 恢复厨师
            int chefCount = dis.readInt();
            for (int i = 0; i < chefCount; i++) {
                Chef c = loadChef(dis);
                hotel.getChefs().add(c);
            }
            
            // 恢复桌子
            int tableCount = dis.readInt();
            for (int i = 0; i < tableCount; i++) {
                int seats = dis.readInt();
                int level = dis.readInt();
                Table t = new Table(i + 1, seats);
                t.setLevel(level);
                hotel.getTables().add(t);
            }
            
            // 恢复菜单
            int menuCount = dis.readInt();
            for (int i = 0; i < menuCount; i++) {
                String dishName = dis.readUTF();
                Dish dish = createDishByName(dishName);
                if (dish != null) {
                    hotel.getMenu().add(dish);
                }
            }
            
            // 刷新招聘池
            hotel.refreshEmployeePool();
            
            hotel.addLog("[存档] 已加载！资金: $" + money);
            System.out.println("存档加载成功！资金: $" + money);
            return hotel;
            
        } catch (IOException e) {
            System.out.println("❌ 读取存档失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static Waiter loadWaiter(DataInputStream dis) throws IOException {
        String name = dis.readUTF();
        int level = dis.readInt();
        int exp = dis.readInt();
        int expToNextLevel = dis.readInt();
        int proficiency = dis.readInt();
        int stamina = dis.readInt();
        int maxStamina = dis.readInt();
        int totalTips = dis.readInt();
        int serviceSkill = dis.readInt();
        int speedSkill = dis.readInt();
        int charmSkill = dis.readInt();
        int tier = dis.readInt();
        
        Waiter w = new Waiter(name, tier, serviceSkill, speedSkill, charmSkill);
        w.restoreState(level, exp, expToNextLevel, proficiency, stamina, maxStamina);
        w.setTotalTips(totalTips);
        return w;
    }

    private static Chef loadChef(DataInputStream dis) throws IOException {
        String name = dis.readUTF();
        int level = dis.readInt();
        int exp = dis.readInt();
        int expToNextLevel = dis.readInt();
        int proficiency = dis.readInt();
        int stamina = dis.readInt();
        int maxStamina = dis.readInt();
        int totalDishesCooked = dis.readInt();
        int maxDishLevel = dis.readInt();
        int cookingSkill = dis.readInt();
        int speedSkill = dis.readInt();
        int charmSkill = dis.readInt();
        int tier = dis.readInt();
        
        Chef c = new Chef(name, tier, cookingSkill, speedSkill, charmSkill);
        c.restoreState(level, exp, expToNextLevel, proficiency, stamina, maxStamina);
        c.setTotalDishesCooked(totalDishesCooked);
        c.setMaxDishLevel(maxDishLevel);
        return c;
    }

    private static Dish createDishByName(String name) {
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

    public static boolean hasSaveFile() {
        String savePath = SAVE_DIR + File.separator + SAVE_FILE;
        return new File(savePath).exists();
    }

    public static boolean deleteSave() {
        String savePath = SAVE_DIR + File.separator + SAVE_FILE;
        File saveFile = new File(savePath);
        if (saveFile.exists()) {
            return saveFile.delete();
        }
        return false;
    }
}
