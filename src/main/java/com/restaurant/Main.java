package com.restaurant;

import com.restaurant.game.Hotel;
import com.restaurant.game.GameTimer;
import com.restaurant.game.SaveManager;
import com.restaurant.ui.ConsoleUI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * 游戏主入口
 * 小旅店发家记 - 自动经营模拟游戏
 */
public class Main {
    private static Hotel hotel;
    private static GameTimer gameTimer;
    private static ConsoleUI ui;
    private static boolean running = true;

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                            ║");
        System.out.println("║         🏨 小旅店发家记 - Restaurant Owner 🏨              ║");
        System.out.println("║                                                            ║");
        System.out.println("║         一个全自动经营模拟游戏                              ║");
        System.out.println("║                                                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("游戏说明:");
        System.out.println("  - 你的旅店会自动运营，员工会自动工作");
        System.out.println("  - 顾客会自动到来，点单，吃饭，付款");
        System.out.println("  - 赚取金钱和声望，升级旅店解锁新菜品");
        System.out.println("  - 员工会疲劳，需要休息");
        System.out.println("  - 随机事件会影响经营");
        System.out.println();
        System.out.println("操作说明:");
        System.out.println("  Q - 退出游戏（自动保存）");
        System.out.println("  P - 暂停/继续");
        System.out.println("  S - 手动保存游戏");
        System.out.println("  1/2/3/4 - 调整游戏速度 (1x/2x/3x/4x)");
        System.out.println("  H - 招聘员工");
        System.out.println("  T - 购买桌子");
        System.out.println("  M - 查看菜单");
        System.out.println();

        // 检查是否有存档
        if (SaveManager.hasSaveFile()) {
            System.out.println("💾 发现存档！");
            System.out.println("  1 - 继续游戏（读取存档）");
            System.out.println("  2 - 开始新游戏（覆盖存档）");
            System.out.print("请选择: ");
            
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                String choice = reader.readLine();
                
                if ("1".equals(choice)) {
                    hotel = SaveManager.loadGame();
                    if (hotel == null) {
                        hotel = new Hotel("老王餐馆");
                    }
                } else {
                    hotel = new Hotel("老王餐馆");
                    System.out.println("🆕 开始新游戏！");
                }
            } catch (IOException e) {
                hotel = new Hotel("老王餐馆");
            }
        } else {
            System.out.println("按 Enter 键开始新游戏...");
            try {
                System.in.read();
            } catch (IOException e) {
                // ignore
            }
            hotel = new Hotel("老王餐馆");
        }

        gameTimer = new GameTimer(hotel);
        ui = new ConsoleUI(hotel);
        
        gameTimer.setTickListener(ui);
        gameTimer.start();

        // 主线程处理用户输入
        handleInput();
    }

    /**
     * 处理用户输入
     */
    private static void handleInput() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        
        while (running) {
            try {
                // 非阻塞检测输入
                if (System.in.available() > 0) {
                    String input = reader.readLine();
                    if (input != null && !input.isEmpty()) {
                        processCommand(input.toUpperCase().trim());
                    }
                }
                Thread.sleep(100);
            } catch (IOException | InterruptedException e) {
                // ignore
            }
        }
        
        gameTimer.stop();
        
        // 退出时自动保存
        System.out.println("\n💾 正在保存游戏...");
        SaveManager.saveGame(hotel);
        
        System.out.println("\n游戏结束！感谢游玩！");
        System.out.println("最终统计:");
        System.out.println("  总收入: $" + hotel.getTotalRevenue());
        System.out.println("  总小费: $" + hotel.getTotalTips());
        System.out.println("  服务顾客: " + hotel.getTotalCustomersServed());
        System.out.println("  旅店等级: Lv." + hotel.getHotelLevel());
        System.out.println("\n下次启动游戏时可以继续！");
        System.exit(0);
    }

    /**
     * 处理命令
     */
    private static void processCommand(String command) {
        switch (command) {
            case "Q":
                running = false;
                break;
            case "P":
                gameTimer.togglePause();
                System.out.println(gameTimer.isPaused() ? "⏸️ 游戏已暂停" : "▶️ 游戏继续");
                break;
            case "1":
                gameTimer.setSpeed(1);
                System.out.println("⏱️ 游戏速度: 1x");
                break;
            case "2":
                gameTimer.setSpeed(2);
                System.out.println("⏱️ 游戏速度: 2x");
                break;
            case "3":
                gameTimer.setSpeed(3);
                System.out.println("⏱️ 游戏速度: 3x");
                break;
            case "4":
                gameTimer.setSpeed(4);
                System.out.println("⏱️ 游戏速度: 4x");
                break;
            case "H":
                showHireMenu();
                break;
            case "T":
                showBuyTableMenu();
                break;
            case "M":
                ui.showMenu();
                break;
            case "S":
                // 手动保存
                gameTimer.togglePause();
                System.out.println("⏸️ 游戏暂停，正在保存...");
                SaveManager.saveGame(hotel);
                System.out.println("按 Enter 继续游戏...");
                try {
                    new BufferedReader(new InputStreamReader(System.in)).readLine();
                } catch (IOException e) {}
                gameTimer.togglePause();
                break;
            case "U":
                showUpgradeMenu();
                break;
            default:
                // 忽略未知命令
                break;
        }
    }

    /**
     * 显示招聘菜单
     */
    private static void showHireMenu() {
        int waiterTier = hotel.getUnlockedWaiterTier();
        int chefTier = hotel.getUnlockedChefTier();
        
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║              👥 招聘员工                         ║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.printf("║  当前员工: 服务员 %d/%d | 厨师 %d/%d              ║\n",
                hotel.getWaiters().size(), hotel.getMaxWaiters(),
                hotel.getChefs().size(), hotel.getMaxChefs());
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.println("║  【服务员】                                      ║");
        System.out.println("║  1. 普通服务员   - $200                          ║");
        if (waiterTier >= 2) {
            System.out.println("║  2. ⭐高级服务员 - $500  (自带经验)              ║");
        } else {
            System.out.println("║  2. 🔒高级服务员 - 餐厅Lv.3解锁                  ║");
        }
        if (waiterTier >= 3) {
            System.out.println("║  3. 🏆金牌服务员 - $1000 (精英级别)              ║");
        } else {
            System.out.println("║  3. 🔒金牌服务员 - 餐厅Lv.5解锁                  ║");
        }
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.println("║  【厨师】                                        ║");
        System.out.println("║  4. 学徒厨师     - $300                          ║");
        if (chefTier >= 2) {
            System.out.println("║  5. ⭐大厨       - $800  (自带经验)              ║");
        } else {
            System.out.println("║  5. 🔒大厨       - 餐厅Lv.3解锁                  ║");
        }
        if (chefTier >= 3) {
            System.out.println("║  6. 🏆主厨       - $1500 (大师级别)              ║");
        } else {
            System.out.println("║  6. 🔒主厨       - 餐厅Lv.5解锁                  ║");
        }
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.println("║  0. 返回                                         ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println("当前资金: $" + hotel.getMoney() + " | 餐厅等级: Lv." + hotel.getHotelLevel());
        System.out.print("请选择: ");
        
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String choice = reader.readLine();
            
            int waiterNum = hotel.getWaiters().size() + 1;
            int chefNum = hotel.getChefs().size() + 1;
            
            switch (choice) {
                case "1": // 普通服务员
                    if (hotel.hireWaiter("服务员" + waiterNum, 200, 1)) {
                        System.out.println("✅ 招聘成功！");
                    }
                    break;
                case "2": // 高级服务员
                    if (waiterTier >= 2) {
                        if (hotel.hireWaiter("高级服务员" + waiterNum, 500, 2)) {
                            System.out.println("✅ 招聘成功！");
                        }
                    } else {
                        System.out.println("❌ 尚未解锁！需要餐厅Lv.3");
                    }
                    break;
                case "3": // 金牌服务员
                    if (waiterTier >= 3) {
                        if (hotel.hireWaiter("金牌服务员" + waiterNum, 1000, 3)) {
                            System.out.println("✅ 招聘成功！");
                        }
                    } else {
                        System.out.println("❌ 尚未解锁！需要餐厅Lv.5");
                    }
                    break;
                case "4": // 学徒厨师
                    if (hotel.hireChef("厨师" + chefNum, 300, 1)) {
                        System.out.println("✅ 招聘成功！");
                    }
                    break;
                case "5": // 大厨
                    if (chefTier >= 2) {
                        if (hotel.hireChef("大厨" + chefNum, 800, 2)) {
                            System.out.println("✅ 招聘成功！");
                        }
                    } else {
                        System.out.println("❌ 尚未解锁！需要餐厅Lv.3");
                    }
                    break;
                case "6": // 主厨
                    if (chefTier >= 3) {
                        if (hotel.hireChef("主厨" + chefNum, 1500, 3)) {
                            System.out.println("✅ 招聘成功！");
                        }
                    } else {
                        System.out.println("❌ 尚未解锁！需要餐厅Lv.5");
                    }
                    break;
            }
        } catch (IOException e) {
            // ignore
        }
    }

    /**
     * 显示购买桌子菜单
     */
    private static void showBuyTableMenu() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║           🪑 购买桌子                ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║  1. 2人小桌 - $100                   ║");
        System.out.println("║  2. 4人桌   - $200                   ║");
        System.out.println("║  3. 6人大桌 - $350                   ║");
        System.out.println("║  0. 返回                             ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.println("当前资金: $" + hotel.getMoney());
        System.out.print("请选择: ");
        
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String choice = reader.readLine();
            
            switch (choice) {
                case "1":
                    if (hotel.buyTable(2, 100)) {
                        System.out.println("✅ 成功购买 2人小桌");
                    } else {
                        System.out.println("❌ 资金不足！");
                    }
                    break;
                case "2":
                    if (hotel.buyTable(4, 200)) {
                        System.out.println("✅ 成功购买 4人桌");
                    } else {
                        System.out.println("❌ 资金不足！");
                    }
                    break;
                case "3":
                    if (hotel.buyTable(6, 350)) {
                        System.out.println("✅ 成功购买 6人大桌");
                    } else {
                        System.out.println("❌ 资金不足！");
                    }
                    break;
            }
        } catch (IOException e) {
            // ignore
        }
    }

    /**
     * 显示升级菜单
     */
    private static void showUpgradeMenu() {
        int currentLevel = hotel.getHotelLevel();
        int currentExp = hotel.getHotelExp();
        int expNeeded = hotel.getExpToNextLevel();
        int expRemaining = expNeeded - currentExp;
        
        // 计算升级费用：每点经验需要 $5
        int upgradeCost = expRemaining * 5;
        
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║              🏨 餐厅升级                         ║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.printf("║  当前等级: Lv.%d                                  ║\n", currentLevel);
        System.out.printf("║  经验进度: %d / %d                               ║\n", currentExp, expNeeded);
        System.out.printf("║  还需经验: %d                                    ║\n", expRemaining);
        System.out.println("╠══════════════════════════════════════════════════╣");
        
        if (currentLevel >= 10) {
            System.out.println("║  🏆 恭喜！餐厅已达到最高等级！                    ║");
        } else {
            System.out.printf("║  1. 💰 花费 $%d 立即升级到 Lv.%d               ║\n", 
                    upgradeCost, currentLevel + 1);
            System.out.printf("║  2. 💵 花费 $%d 购买 50 经验                   ║\n", 250);
            System.out.printf("║  3. 💎 花费 $%d 购买 100 经验                  ║\n", 450);
            System.out.println("╠══════════════════════════════════════════════════╣");
            System.out.println("║  升级解锁预览:                                   ║");
            showNextLevelRewards(currentLevel + 1);
        }
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.println("║  0. 返回                                         ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println("当前资金: $" + hotel.getMoney());
        System.out.print("请选择: ");
        
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String choice = reader.readLine();
            
            switch (choice) {
                case "1":
                    if (currentLevel >= 10) {
                        System.out.println("❌ 已达最高等级！");
                    } else if (hotel.getMoney() >= upgradeCost) {
                        hotel.spendMoney(upgradeCost);
                        hotel.addHotelExp(expRemaining);
                        System.out.println("🎉 餐厅升级成功！");
                    } else {
                        System.out.println("❌ 资金不足！需要 $" + upgradeCost);
                    }
                    break;
                case "2":
                    if (hotel.getMoney() >= 250) {
                        hotel.spendMoney(250);
                        hotel.addHotelExp(50);
                        System.out.println("✅ 购买成功！经验 +50");
                    } else {
                        System.out.println("❌ 资金不足！");
                    }
                    break;
                case "3":
                    if (hotel.getMoney() >= 450) {
                        hotel.spendMoney(450);
                        hotel.addHotelExp(100);
                        System.out.println("✅ 购买成功！经验 +100");
                    } else {
                        System.out.println("❌ 资金不足！");
                    }
                    break;
            }
        } catch (IOException e) {
            // ignore
        }
    }

    /**
     * 显示下一级解锁内容
     */
    private static void showNextLevelRewards(int nextLevel) {
        switch (nextLevel) {
            case 2:
                System.out.println("║    → 新菜品：糖醋里脊、红烧肉                    ║");
                System.out.println("║    → 员工槽位扩展到 3 人                        ║");
                break;
            case 3:
                System.out.println("║    → 新菜品：清蒸鱼、北京烤鸭                    ║");
                System.out.println("║    → ⭐ 解锁高级服务员、大厨                     ║");
                System.out.println("║    → 新增 1 桌                                  ║");
                break;
            case 4:
                System.out.println("║    → 新菜品：龙虾                               ║");
                System.out.println("║    → 员工槽位扩展到 4 人                        ║");
                System.out.println("║    → 新增 1 大桌                                ║");
                break;
            case 5:
                System.out.println("║    → 新菜品：和牛牛排                           ║");
                System.out.println("║    → 🏆 解锁金牌服务员、主厨                    ║");
                break;
            case 6:
                System.out.println("║    → 员工槽位扩展到 5 人                        ║");
                System.out.println("║    → 新增 VIP 大桌（8人）                       ║");
                break;
            default:
                System.out.println("║    → 声望提升，更多顾客                         ║");
                break;
        }
    }
}

