package com.restaurant.ui.swing;

import com.restaurant.game.Hotel;
import com.restaurant.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * 控制面板 - 操作按钮
 */
public class ControlPanel extends JPanel {
    private Hotel hotel;
    private GameWindow gameWindow;
    
    private JButton pauseBtn;
    private JLabel speedLabel;
    private JButton hireBtn;
    private JButton tableBtn;
    private JButton upgradeBtn;
    private JButton menuBtn;
    private JButton saveBtn;

    public ControlPanel(Hotel hotel, GameWindow gameWindow) {
        this.hotel = hotel;
        this.gameWindow = gameWindow;
        initComponents();
        initLayout();
    }

    private void initComponents() {
        setBackground(new Color(35, 35, 45));
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // 暂停按钮
        pauseBtn = createButton("⏸ 暂停 [P]", new Color(255, 152, 0));
        pauseBtn.addActionListener(e -> gameWindow.togglePause());
        
        // 速度标签
        speedLabel = new JLabel("速度: 1x");
        speedLabel.setForeground(Color.WHITE);
        speedLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        
        // 速度按钮
        JPanel speedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        speedPanel.setOpaque(false);
        for (int i = 1; i <= 4; i++) {
            final int speed = i;
            JButton btn = createSmallButton(i + "x");
            btn.addActionListener(e -> gameWindow.setGameSpeed(speed));
            speedPanel.add(btn);
        }
        
        // 招聘按钮
        hireBtn = createButton("👥 招聘 [H]", new Color(33, 150, 243));
        hireBtn.addActionListener(e -> showHireDialog());
        
        // 买桌子按钮
        tableBtn = createButton("🪑 买桌子 [T]", new Color(76, 175, 80));
        tableBtn.addActionListener(e -> showTableDialog());
        
        // 升级按钮
        upgradeBtn = createButton("⬆ 升级 [U]", new Color(156, 39, 176));
        upgradeBtn.addActionListener(e -> showUpgradeDialog());
        
        // 菜单按钮
        menuBtn = createButton("📋 菜单 [M]", new Color(0, 150, 136));
        menuBtn.addActionListener(e -> showMenuDialog());
        
        // 保存按钮
        saveBtn = createButton("💾 保存 [S]", new Color(96, 125, 139));
        saveBtn.addActionListener(e -> gameWindow.saveGame());
    }

    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 35));
        
        // 悬停效果
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bgColor.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });
        
        return btn;
    }

    private JButton createSmallButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(70, 70, 80));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Microsoft YaHei", Font.PLAIN, 10));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(35, 25));
        return btn;
    }

    private void initLayout() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 15, 5));
        
        add(pauseBtn);
        add(speedLabel);
        
        // 速度按钮组
        JPanel speedBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        speedBtns.setOpaque(false);
        for (int i = 1; i <= 4; i++) {
            final int speed = i;
            JButton btn = createSmallButton(i + "x");
            btn.addActionListener(e -> gameWindow.setGameSpeed(speed));
            speedBtns.add(btn);
        }
        add(speedBtns);
        
        add(new JSeparator(SwingConstants.VERTICAL));
        add(hireBtn);
        add(tableBtn);
        add(upgradeBtn);
        add(menuBtn);
        add(saveBtn);
    }

    public void updatePauseButton(boolean isPaused) {
        if (isPaused) {
            pauseBtn.setText("▶ 继续 [P]");
            pauseBtn.setBackground(new Color(76, 175, 80));
        } else {
            pauseBtn.setText("⏸ 暂停 [P]");
            pauseBtn.setBackground(new Color(255, 152, 0));
        }
    }

    public void updateSpeedLabel(int speed) {
        speedLabel.setText("速度: " + speed + "x");
    }

    public void update() {
        // 更新按钮状态（如果需要）
    }

    // ========== 对话框 ==========

    private void showHireDialog() {
        JDialog dialog = new JDialog(gameWindow, "👥 招聘员工", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(gameWindow);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(50, 50, 60));
        
        // 当前员工信息
        JLabel infoLabel = new JLabel(String.format(
            "<html><font color='white'>当前员工: 服务员 %d/%d | 厨师 %d/%d<br>资金: $%d</font></html>",
            hotel.getWaiters().size(), hotel.getMaxWaiters(),
            hotel.getChefs().size(), hotel.getMaxChefs(),
            hotel.getMoney()));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(infoLabel);
        panel.add(Box.createVerticalStrut(15));
        
        // 服务员选项
        panel.add(createHireSection("【服务员】", new String[][]{
            {"普通服务员", "200", "1"},
            {"⭐高级服务员", "500", "2"},
            {"🏆金牌服务员", "1000", "3"}
        }, true, dialog));
        
        panel.add(Box.createVerticalStrut(10));
        
        // 厨师选项
        panel.add(createHireSection("【厨师】", new String[][]{
            {"学徒厨师", "300", "1"},
            {"⭐大厨", "800", "2"},
            {"🏆主厨", "1500", "3"}
        }, false, dialog));
        
        // 关闭按钮
        JButton closeBtn = new JButton("关闭");
        closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeBtn.addActionListener(e -> dialog.dispose());
        panel.add(Box.createVerticalStrut(15));
        panel.add(closeBtn);
        
        dialog.add(new JScrollPane(panel));
        dialog.setVisible(true);
    }

    private JPanel createHireSection(String title, String[][] options, boolean isWaiter, JDialog dialog) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        section.add(titleLabel);
        
        int unlockedTier = isWaiter ? hotel.getUnlockedWaiterTier() : hotel.getUnlockedChefTier();
        
        for (String[] opt : options) {
            String name = opt[0];
            int cost = Integer.parseInt(opt[1]);
            int tier = Integer.parseInt(opt[2]);
            
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            row.setOpaque(false);
            
            JButton btn = new JButton(name + " - $" + cost);
            btn.setPreferredSize(new Dimension(200, 30));
            
            if (tier > unlockedTier) {
                btn.setEnabled(false);
                btn.setText("🔒 " + name + " (Lv." + (tier == 2 ? 3 : 5) + "解锁)");
            } else {
                btn.addActionListener(e -> {
                    int num = (isWaiter ? hotel.getWaiters().size() : hotel.getChefs().size()) + 1;
                    String empName = (tier == 1 ? (isWaiter ? "服务员" : "厨师") : 
                                     (tier == 2 ? (isWaiter ? "高级服务员" : "大厨") : 
                                                  (isWaiter ? "金牌服务员" : "主厨"))) + num;
                    
                    boolean success = isWaiter ? 
                        hotel.hireWaiter(empName, cost, tier) : 
                        hotel.hireChef(empName, cost, tier);
                    
                    if (success) {
                        JOptionPane.showMessageDialog(dialog, "✅ 招聘成功: " + empName);
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "❌ 招聘失败（资金不足或已满员）");
                    }
                });
            }
            
            row.add(btn);
            section.add(row);
        }
        
        return section;
    }

    private void showTableDialog() {
        String[] options = {"2人小桌 - $100", "4人桌 - $200", "6人大桌 - $350", "取消"};
        int choice = JOptionPane.showOptionDialog(gameWindow,
            "当前桌子数: " + hotel.getTables().size() + "\n资金: $" + hotel.getMoney(),
            "🪑 购买桌子",
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
            null, options, options[0]);
        
        int[] seats = {2, 4, 6};
        int[] costs = {100, 200, 350};
        
        if (choice >= 0 && choice < 3) {
            if (hotel.buyTable(seats[choice], costs[choice])) {
                JOptionPane.showMessageDialog(gameWindow, "✅ 购买成功！");
            } else {
                JOptionPane.showMessageDialog(gameWindow, "❌ 资金不足！");
            }
        }
    }

    private void showUpgradeDialog() {
        int level = hotel.getHotelLevel();
        int exp = hotel.getHotelExp();
        int expMax = hotel.getExpToNextLevel();
        int expNeeded = expMax - exp;
        int upgradeCost = expNeeded * 5;
        
        if (level >= 10) {
            JOptionPane.showMessageDialog(gameWindow, "🏆 已达最高等级！");
            return;
        }
        
        String message = String.format(
            "当前等级: Lv.%d\n经验进度: %d / %d\n还需经验: %d\n\n立即升级费用: $%d\n\n选择操作:",
            level, exp, expMax, expNeeded, upgradeCost);
        
        String[] options = {"立即升级 ($" + upgradeCost + ")", "购买50经验 ($250)", "购买100经验 ($450)", "取消"};
        int choice = JOptionPane.showOptionDialog(gameWindow, message, "⬆ 餐厅升级",
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        
        switch (choice) {
            case 0:
                if (hotel.getMoney() >= upgradeCost) {
                    hotel.spendMoney(upgradeCost);
                    hotel.addHotelExp(expNeeded);
                    JOptionPane.showMessageDialog(gameWindow, "🎉 升级成功！");
                } else {
                    JOptionPane.showMessageDialog(gameWindow, "❌ 资金不足！");
                }
                break;
            case 1:
                if (hotel.getMoney() >= 250) {
                    hotel.spendMoney(250);
                    hotel.addHotelExp(50);
                    JOptionPane.showMessageDialog(gameWindow, "✅ 购买成功！经验 +50");
                } else {
                    JOptionPane.showMessageDialog(gameWindow, "❌ 资金不足！");
                }
                break;
            case 2:
                if (hotel.getMoney() >= 450) {
                    hotel.spendMoney(450);
                    hotel.addHotelExp(100);
                    JOptionPane.showMessageDialog(gameWindow, "✅ 购买成功！经验 +100");
                } else {
                    JOptionPane.showMessageDialog(gameWindow, "❌ 资金不足！");
                }
                break;
        }
    }

    private void showMenuDialog() {
        StringBuilder sb = new StringBuilder("<html><table border='1' cellpadding='5'>");
        sb.append("<tr><th>菜品</th><th>价格</th><th>时间</th><th>等级</th></tr>");
        
        for (Dish d : hotel.getMenu()) {
            sb.append(String.format("<tr><td>%s</td><td>$%d</td><td>%ds</td><td>Lv.%d</td></tr>",
                d.getName(), d.getPrice(), d.getCookTime(), d.getLevel()));
        }
        sb.append("</table></html>");
        
        JLabel label = new JLabel(sb.toString());
        JOptionPane.showMessageDialog(gameWindow, label, "📋 当前菜单", JOptionPane.PLAIN_MESSAGE);
    }
}

