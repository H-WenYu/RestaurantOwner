package com.restaurant.ui.swing;

import com.restaurant.game.Hotel;
import com.restaurant.model.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * 餐厅场景渲染面板 - 显示桌子、员工、顾客
 */
public class RestaurantPanel extends JPanel {
    private Hotel hotel;
    
    // 颜色方案
    private static final Color BG_COLOR = new Color(50, 50, 60);
    private static final Color FLOOR_COLOR = new Color(139, 90, 43);
    private static final Color TABLE_EMPTY = new Color(101, 67, 33);
    private static final Color TABLE_OCCUPIED = new Color(76, 175, 80);
    private static final Color WAITER_COLOR = new Color(33, 150, 243);
    private static final Color CHEF_COLOR = new Color(255, 152, 0);
    private static final Color CUSTOMER_COLOR = new Color(156, 39, 176);
    private static final Color CUSTOMER_ANGRY = new Color(244, 67, 54);
    private static final Color KITCHEN_COLOR = new Color(80, 80, 80);
    private static final Color WAITING_AREA = new Color(70, 70, 80);

    public RestaurantPanel(Hotel hotel) {
        this.hotel = hotel;
        setBackground(BG_COLOR);
        setPreferredSize(new Dimension(600, 400));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // 绘制地板
        drawFloor(g2d, width, height);
        
        // 绘制厨房区域
        drawKitchen(g2d, width, height);
        
        // 绘制等位区
        drawWaitingArea(g2d, width, height);
        
        // 绘制桌子
        drawTables(g2d, width, height);
        
        // 绘制员工
        drawEmployees(g2d, width, height);
        
        // 绘制顾客
        drawCustomers(g2d, width, height);
        
        // 绘制当前事件
        drawEvent(g2d, width, height);
    }

    private void drawFloor(Graphics2D g, int width, int height) {
        // 地板背景
        g.setColor(FLOOR_COLOR);
        g.fillRect(0, 50, width, height - 100);
        
        // 地板格子
        g.setColor(new Color(120, 75, 35));
        for (int x = 0; x < width; x += 40) {
            g.drawLine(x, 50, x, height - 50);
        }
        for (int y = 50; y < height - 50; y += 40) {
            g.drawLine(0, y, width, y);
        }
    }

    private void drawKitchen(Graphics2D g, int width, int height) {
        int kitchenWidth = 150;
        int kitchenHeight = 80;
        int x = width - kitchenWidth - 20;
        int y = 60;
        
        // 厨房背景
        g.setColor(KITCHEN_COLOR);
        g.fillRoundRect(x, y, kitchenWidth, kitchenHeight, 10, 10);
        
        // 厨房边框
        g.setColor(new Color(100, 100, 100));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(x, y, kitchenWidth, kitchenHeight, 10, 10);
        
        // 厨房标签
        g.setColor(Color.WHITE);
        g.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        g.drawString("🍳 厨房", x + 45, y + 25);
        
        // 绘制厨师
        List<Chef> chefs = hotel.getChefs();
        for (int i = 0; i < chefs.size(); i++) {
            Chef chef = chefs.get(i);
            int cx = x + 25 + (i % 2) * 60;
            int cy = y + 40 + (i / 2) * 25;
            drawChef(g, chef, cx, cy);
        }
    }

    private void drawWaitingArea(Graphics2D g, int width, int height) {
        int areaWidth = 120;
        int areaHeight = height - 130;
        int x = 15;
        int y = 60;
        
        // 等位区背景
        g.setColor(WAITING_AREA);
        g.fillRoundRect(x, y, areaWidth, areaHeight, 10, 10);
        
        // 边框
        g.setColor(new Color(90, 90, 100));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(x, y, areaWidth, areaHeight, 10, 10);
        
        // 标签
        g.setColor(Color.WHITE);
        g.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
        g.drawString("🚶 等位区", x + 25, y + 20);
        
        // 等位人数
        int waitingCount = hotel.getWaitingCustomers().size();
        g.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
        g.drawString("(" + waitingCount + "人等待)", x + 25, y + 38);
        
        // 绘制等待的顾客图标
        int col = 0, row = 0;
        for (Customer c : hotel.getWaitingCustomers()) {
            if (row > 6) break;
            int cx = x + 20 + col * 35;
            int cy = y + 55 + row * 35;
            
            // 根据耐心值改变颜色
            double patienceRatio = (double) c.getPatience() / c.getMaxPatience();
            if (patienceRatio < 0.3) {
                g.setColor(CUSTOMER_ANGRY);
            } else if (patienceRatio < 0.6) {
                g.setColor(new Color(255, 193, 7));
            } else {
                g.setColor(CUSTOMER_COLOR);
            }
            
            g.fillOval(cx, cy, 25, 25);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Microsoft YaHei", Font.PLAIN, 10));
            g.drawString("🧑", cx + 4, cy + 18);
            
            col++;
            if (col >= 3) {
                col = 0;
                row++;
            }
        }
    }

    private void drawTables(Graphics2D g, int width, int height) {
        List<Table> tables = hotel.getTables();
        int tableSize = 60;
        int startX = 160;
        int startY = 80;
        int cols = 3;
        int spacing = 100;
        
        for (int i = 0; i < tables.size(); i++) {
            Table table = tables.get(i);
            int x = startX + (i % cols) * spacing;
            int y = startY + (i / cols) * spacing;
            
            // 桌子颜色
            if (table.isOccupied()) {
                g.setColor(TABLE_OCCUPIED);
            } else {
                g.setColor(TABLE_EMPTY);
            }
            
            // 绘制桌子
            g.fillRoundRect(x, y, tableSize, tableSize, 8, 8);
            
            // 桌子边框
            g.setColor(new Color(80, 50, 20));
            g.setStroke(new BasicStroke(2));
            g.drawRoundRect(x, y, tableSize, tableSize, 8, 8);
            
            // 桌号
            g.setColor(Color.WHITE);
            g.setFont(new Font("Microsoft YaHei", Font.BOLD, 11));
            String tableLabel = "桌" + table.getId() + " (" + table.getSeats() + "座)";
            g.drawString(tableLabel, x + 5, y + 15);
            
            // 如果有顾客，显示顾客状态
            if (table.isOccupied() && table.getCustomer() != null) {
                Customer c = table.getCustomer();
                drawCustomerAtTable(g, c, x, y, tableSize);
            }
        }
    }

    private void drawCustomerAtTable(Graphics2D g, Customer c, int x, int y, int tableSize) {
        // 顾客图标
        g.setColor(Color.WHITE);
        g.setFont(new Font("Microsoft YaHei", Font.PLAIN, 18));
        g.drawString("🧑", x + tableSize/2 - 8, y + tableSize/2 + 8);
        
        // 状态文字
        g.setFont(new Font("Microsoft YaHei", Font.PLAIN, 9));
        String status = "";
        Color statusColor = Color.WHITE;
        
        switch (c.getState()) {
            case WAITING_ORDER:
                status = "等点单";
                statusColor = new Color(255, 193, 7);
                break;
            case WAITING_FOOD:
                status = "等菜 " + c.getDishesReceived() + "/" + c.getOrders().size();
                statusColor = new Color(255, 152, 0);
                break;
            case EATING:
                status = "用餐中";
                statusColor = new Color(76, 175, 80);
                break;
            case FINISHED:
                status = "吃完了";
                statusColor = new Color(33, 150, 243);
                break;
            default:
                break;
        }
        
        g.setColor(statusColor);
        g.drawString(status, x + 8, y + tableSize - 5);
        
        // 耐心条
        int barWidth = tableSize - 10;
        int barHeight = 4;
        int barX = x + 5;
        int barY = y + tableSize - 15;
        
        g.setColor(new Color(60, 60, 60));
        g.fillRect(barX, barY, barWidth, barHeight);
        
        double patienceRatio = (double) c.getPatience() / c.getMaxPatience();
        if (patienceRatio < 0.3) {
            g.setColor(CUSTOMER_ANGRY);
        } else if (patienceRatio < 0.6) {
            g.setColor(new Color(255, 193, 7));
        } else {
            g.setColor(new Color(76, 175, 80));
        }
        g.fillRect(barX, barY, (int)(barWidth * patienceRatio), barHeight);
    }

    private void drawEmployees(Graphics2D g, int width, int height) {
        // 绘制服务员
        List<Waiter> waiters = hotel.getWaiters();
        int waiterY = height - 90;
        
        for (int i = 0; i < waiters.size(); i++) {
            Waiter waiter = waiters.get(i);
            int x = 160 + i * 80;
            drawWaiter(g, waiter, x, waiterY);
        }
    }

    private void drawWaiter(Graphics2D g, Waiter waiter, int x, int y) {
        // 服务员圆形
        if (waiter.isResting()) {
            g.setColor(new Color(150, 150, 150));
        } else if (waiter.isBusy()) {
            g.setColor(new Color(76, 175, 80));
        } else {
            g.setColor(WAITER_COLOR);
        }
        g.fillOval(x, y, 35, 35);
        
        // 边框
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2));
        g.drawOval(x, y, 35, 35);
        
        // 图标
        g.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        g.drawString("👔", x + 8, y + 24);
        
        // 名字和状态
        g.setFont(new Font("Microsoft YaHei", Font.PLAIN, 9));
        g.setColor(Color.WHITE);
        
        String status = waiter.getName();
        if (waiter.isResting()) {
            status += " 💤";
        } else if (waiter.getCurrentTask() != Waiter.WaiterTask.NONE) {
            switch (waiter.getCurrentTask()) {
                case GREETING: status += " 迎客"; break;
                case TAKING_ORDER: status += " 点单"; break;
                case DELIVERING: status += " 上菜"; break;
            }
        }
        g.drawString(status, x - 5, y + 50);
        
        // 体力条
        drawStaminaBar(g, waiter, x, y + 55);
    }

    private void drawChef(Graphics2D g, Chef chef, int x, int y) {
        // 厨师图标
        if (chef.isResting()) {
            g.setColor(new Color(150, 150, 150));
        } else if (chef.isBusy()) {
            g.setColor(new Color(76, 175, 80));
        } else {
            g.setColor(CHEF_COLOR);
        }
        g.fillOval(x, y, 30, 30);
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        g.drawString("👨‍🍳", x + 5, y + 22);
        
        // 状态
        g.setFont(new Font("Microsoft YaHei", Font.PLAIN, 8));
        String status = chef.isResting() ? "💤" : (chef.isBusy() ? "🔥" : "");
        g.drawString(status, x + 32, y + 15);
    }

    private void drawStaminaBar(Graphics2D g, Employee emp, int x, int y) {
        int barWidth = 35;
        int barHeight = 4;
        
        g.setColor(new Color(60, 60, 60));
        g.fillRect(x, y, barWidth, barHeight);
        
        double ratio = (double) emp.getStamina() / emp.getMaxStamina();
        if (ratio < 0.3) {
            g.setColor(CUSTOMER_ANGRY);
        } else if (ratio < 0.6) {
            g.setColor(new Color(255, 193, 7));
        } else {
            g.setColor(new Color(76, 175, 80));
        }
        g.fillRect(x, y, (int)(barWidth * ratio), barHeight);
    }

    private void drawCustomers(Graphics2D g, int width, int height) {
        // 顾客已经在桌子和等位区绘制了
    }

    private void drawEvent(Graphics2D g, int width, int height) {
        String event = hotel.getCurrentEvent();
        if (event != null && !event.isEmpty()) {
            // 事件横幅
            g.setColor(new Color(255, 87, 34, 200));
            g.fillRoundRect(width/2 - 120, 10, 240, 35, 10, 10);
            
            g.setColor(Color.WHITE);
            g.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
            String eventText = "📢 " + event;
            FontMetrics fm = g.getFontMetrics();
            int textX = width/2 - fm.stringWidth(eventText)/2;
            g.drawString(eventText, textX, 33);
        }
    }
}

