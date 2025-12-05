package com.restaurant.ui;

import com.restaurant.game.Hotel;
import com.restaurant.game.GameTimer;
import com.restaurant.model.*;

import java.util.List;

/**
 * 控制台UI - 显示游戏状态
 */
public class ConsoleUI implements GameTimer.GameTickListener {
    private Hotel hotel;
    private boolean clearScreen;

    public ConsoleUI(Hotel hotel) {
        this.hotel = hotel;
        this.clearScreen = true;
    }

    public void setClearScreen(boolean clearScreen) {
        this.clearScreen = clearScreen;
    }

    @Override
    public void onTick(Hotel hotel) {
        render();
    }

    /**
     * 渲染游戏界面
     */
    public void render() {
        if (clearScreen) {
            clearConsole();
        }

        StringBuilder sb = new StringBuilder();
        
        // 标题栏
        sb.append("\n");
        sb.append("╔════════════════════════════════════════════════════════════════════════╗\n");
        sb.append(String.format("║  🏨 %s  Lv.%d  [经验: %d/%d]%s║\n", 
                padRight(hotel.getName(), 20),
                hotel.getHotelLevel(),
                hotel.getHotelExp(),
                hotel.getExpToNextLevel(),
                repeat(" ", 20)));
        sb.append("╠════════════════════════════════════════════════════════════════════════╣\n");

        // 资源状态
        sb.append(String.format("║  💰 资金: $%-8d  ⭐ 声望: %-3d  ⏰ 时间: %s%s║\n",
                hotel.getMoney(),
                hotel.getReputation(),
                formatTime(hotel.getGameTime()),
                repeat(" ", 24)));

        // 当前事件
        if (hotel.getCurrentEvent() != null) {
            sb.append(String.format("║  📢 当前事件: %-56s ║\n", hotel.getCurrentEvent()));
        }

        sb.append("╠════════════════════════════════════════════════════════════════════════╣\n");

        // 统计数据
        sb.append(String.format("║  📊 统计: 服务顾客: %-5d | 流失: %-5d | 总收入: $%-8d | 小费: $%-6d ║\n",
                hotel.getTotalCustomersServed(),
                hotel.getTotalCustomersLost(),
                hotel.getTotalRevenue(),
                hotel.getTotalTips()));

        sb.append("╠════════════════════════════════════════════════════════════════════════╣\n");

        // 员工状态
        sb.append("║  👥 员工状态:                                                          ║\n");
        for (Waiter w : hotel.getWaiters()) {
            sb.append(String.format("║    %s%s║\n", padRight(w.toString(), 68), ""));
        }
        for (Chef c : hotel.getChefs()) {
            sb.append(String.format("║    %s%s║\n", padRight(c.toString(), 68), ""));
        }

        sb.append("╠════════════════════════════════════════════════════════════════════════╣\n");

        // 餐桌状态
        sb.append("║  🪑 餐桌状态:                                                          ║\n");
        StringBuilder tableStr = new StringBuilder();
        for (Table t : hotel.getTables()) {
            if (t.isOccupied()) {
                tableStr.append(String.format("[桌%d:🧑] ", t.getId()));
            } else {
                tableStr.append(String.format("[桌%d:空] ", t.getId()));
            }
        }
        sb.append(String.format("║    %s%s║\n", padRight(tableStr.toString(), 68), ""));

        sb.append("╠════════════════════════════════════════════════════════════════════════╣\n");

        // 等位顾客
        sb.append(String.format("║  🚶 等位: %-3d人                                                        ║\n", 
                hotel.getWaitingCustomers().size()));

        // 店内顾客
        sb.append("║  👥 店内顾客:                                                          ║\n");
        List<Customer> customers = hotel.getAllCustomers();
        int shown = 0;
        for (Customer c : customers) {
            if (shown >= 5) {
                sb.append(String.format("║    ... 还有 %d 位顾客%s║\n", 
                        customers.size() - shown, repeat(" ", 50)));
                break;
            }
            sb.append(String.format("║    %s%s║\n", padRight(c.toString(), 68), ""));
            shown++;
        }
        if (customers.isEmpty()) {
            sb.append("║    (暂无顾客)                                                          ║\n");
        }

        sb.append("╠════════════════════════════════════════════════════════════════════════╣\n");

        // 订单队列
        sb.append(String.format("║  📋 活跃订单: %-3d                                                      ║\n",
                hotel.getActiveOrders().size()));
        int orderShown = 0;
        for (Order o : hotel.getActiveOrders()) {
            if (orderShown >= 3) {
                sb.append(String.format("║    ... 还有 %d 个订单%s║\n", 
                        hotel.getActiveOrders().size() - orderShown, repeat(" ", 49)));
                break;
            }
            sb.append(String.format("║    %s%s║\n", padRight(o.toString(), 68), ""));
            orderShown++;
        }
        if (hotel.getActiveOrders().isEmpty()) {
            sb.append("║    (暂无订单)                                                          ║\n");
        }

        sb.append("╠════════════════════════════════════════════════════════════════════════╣\n");

        // 游戏日志
        sb.append("║  📜 最近动态:                                                          ║\n");
        List<String> logs = hotel.getGameLogs();
        int logStart = Math.max(0, logs.size() - 8);
        for (int i = logStart; i < logs.size(); i++) {
            String log = logs.get(i);
            if (log.length() > 68) {
                log = log.substring(0, 65) + "...";
            }
            sb.append(String.format("║    %s%s║\n", padRight(log, 68), ""));
        }
        if (logs.isEmpty()) {
            sb.append("║    (暂无日志)                                                          ║\n");
        }

        sb.append("╚════════════════════════════════════════════════════════════════════════╝\n");

        // 菜单提示
        sb.append("\n");
        sb.append("操作: [Q]退出 | [P]暂停 | [S]保存 | [1-4]调速 | [H]招聘 | [T]买桌 | [U]升级 | [M]菜单\n");

        System.out.print(sb.toString());
    }

    /**
     * 显示菜单
     */
    public void showMenu() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║          📜 当前菜单                 ║");
        System.out.println("╠══════════════════════════════════════╣");
        for (Dish d : hotel.getMenu()) {
            System.out.printf("║  %s%s║\n", padRight(d.toString(), 36), "");
        }
        System.out.println("╚══════════════════════════════════════╝");
    }

    /**
     * 清屏
     */
    private void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // 如果清屏失败，打印空行
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    /**
     * 格式化时间
     */
    private String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

    /**
     * 右填充字符串
     */
    private String padRight(String str, int length) {
        if (str == null) str = "";
        // 计算字符串的显示宽度（中文字符占2个宽度）
        int displayWidth = getDisplayWidth(str);
        if (displayWidth >= length) {
            return str;
        }
        return str + repeat(" ", length - displayWidth);
    }

    /**
     * 计算字符串显示宽度
     */
    private int getDisplayWidth(String str) {
        int width = 0;
        for (char c : str.toCharArray()) {
            if (c >= 0x4E00 && c <= 0x9FFF) {
                width += 2; // 中文字符
            } else if (c >= 0x1F300 && c <= 0x1F9FF) {
                width += 2; // emoji
            } else {
                width += 1;
            }
        }
        return width;
    }

    /**
     * 重复字符串
     */
    private String repeat(String str, int times) {
        if (times <= 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
}

