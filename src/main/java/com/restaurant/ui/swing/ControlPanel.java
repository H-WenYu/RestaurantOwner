package com.restaurant.ui.swing;

import com.restaurant.game.Hotel;
import com.restaurant.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * 控制面板 - 操作按钮
 */
public class ControlPanel extends JPanel {
    private Hotel hotel;
    private GameWindow gameWindow;
    
    private JButton pauseBtn;
    private JLabel speedLabel;
    private JButton[] speedBtns = new JButton[4];
    private int currentSpeed = 1;
    private JButton hireBtn;
    private JButton staffBtn;  // 员工名单
    private JButton tableBtn;
    private JButton upgradeBtn;
    private JButton menuBtn;
    private JButton buildBtn;
    private JButton saveBtn;

    // 颜色
    private static final Color SPEED_NORMAL = new Color(70, 70, 80);
    private static final Color SPEED_SELECTED = new Color(255, 152, 0);  // 橙色高亮

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
        pauseBtn = createButton("|| 暂停 [P]", new Color(255, 152, 0));
        pauseBtn.addActionListener(e -> gameWindow.togglePause());
        
        // 速度标签
        speedLabel = new JLabel("速度: 1x");
        speedLabel.setForeground(Color.WHITE);
        speedLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        
        // 招聘按钮
        hireBtn = createButton("招聘 [H]", new Color(33, 150, 243));
        hireBtn.addActionListener(e -> showHireDialog());
        
        // 员工名单按钮
        staffBtn = createButton("员工 [E]", new Color(233, 30, 99));
        staffBtn.addActionListener(e -> showStaffDialog());
        
        // 买桌子按钮
        tableBtn = createButton("买桌子 [T]", new Color(76, 175, 80));
        tableBtn.addActionListener(e -> showTableDialog());
        
        // 升级按钮
        upgradeBtn = createButton("升级 [U]", new Color(156, 39, 176));
        upgradeBtn.addActionListener(e -> showUpgradeDialog());
        
        // 菜单按钮
        menuBtn = createButton("菜单 [M]", new Color(0, 150, 136));
        menuBtn.addActionListener(e -> showMenuDialog());
        
        // 建造按钮
        buildBtn = createButton("改造 [B]", new Color(121, 85, 72));
        buildBtn.addActionListener(e -> enterBuildMode());
        
        // 保存按钮
        saveBtn = createButton("保存 [S]", new Color(96, 125, 139));
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
        btn.setPreferredSize(new Dimension(100, 35));
        
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

    private JButton createSpeedButton(int speed) {
        JButton btn = new JButton(speed + "x");
        boolean isSelected = (speed == 1);
        btn.setBackground(isSelected ? SPEED_SELECTED : SPEED_NORMAL);
        btn.setForeground(isSelected ? Color.BLACK : Color.WHITE);  // 选中时黑字
        btn.setFont(new Font("Microsoft YaHei", Font.BOLD, 11));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(45, 28));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addActionListener(e -> {
            gameWindow.setGameSpeed(speed);
            updateSpeedButtons(speed);
        });
        
        return btn;
    }

    private void updateSpeedButtons(int selectedSpeed) {
        currentSpeed = selectedSpeed;
        for (int i = 0; i < 4; i++) {
            if (speedBtns[i] != null) {
                boolean isSelected = (i + 1) == selectedSpeed;
                speedBtns[i].setBackground(isSelected ? SPEED_SELECTED : SPEED_NORMAL);
                speedBtns[i].setForeground(isSelected ? Color.BLACK : Color.WHITE);
            }
        }
    }

    private void initLayout() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        add(pauseBtn);
        add(speedLabel);
        
        // 速度按钮组
        JPanel speedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        speedPanel.setOpaque(false);
        for (int i = 0; i < 4; i++) {
            speedBtns[i] = createSpeedButton(i + 1);
            speedPanel.add(speedBtns[i]);
        }
        add(speedPanel);
        
        // 分隔符
        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setPreferredSize(new Dimension(2, 30));
        add(sep);
        
        add(hireBtn);
        add(staffBtn);
        add(tableBtn);
        add(upgradeBtn);
        add(menuBtn);
        add(buildBtn);
        add(saveBtn);
    }

    public void updatePauseButton(boolean isPaused) {
        if (isPaused) {
            pauseBtn.setText("> 继续 [P]");
            pauseBtn.setBackground(new Color(76, 175, 80));
        } else {
            pauseBtn.setText("|| 暂停 [P]");
            pauseBtn.setBackground(new Color(255, 152, 0));
        }
    }

    public void updateSpeedLabel(int speed) {
        speedLabel.setText("速度: " + speed + "x");
        updateSpeedButtons(speed);
    }

    public void update() {
        // 更新按钮状态
    }

    // ========== 建造模式 ==========
    
    private void enterBuildMode() {
        int result = JOptionPane.showConfirmDialog(gameWindow,
            "进入改造模式将会：\n" +
            "- 清空所有顾客\n" +
            "- 暂停游戏收益\n" +
            "- 可以调整桌子和员工位置\n\n" +
            "确定要进入改造模式吗？",
            "改造模式",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (result == JOptionPane.YES_OPTION) {
            hotel.clearAllCustomers();
            gameWindow.togglePause();
            JOptionPane.showMessageDialog(gameWindow,
                "已进入改造模式！\n\n" +
                "功能开发中...\n" +
                "（后续版本将支持拖拽桌子和员工）",
                "改造模式",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ========== 员工名单对话框 ==========

    private void showStaffDialog() {
        JDialog dialog = new JDialog(gameWindow, "员工名单", true);
        dialog.setSize(600, 550);
        dialog.setLocationRelativeTo(gameWindow);
        dialog.setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(50, 50, 60));
        
        // 标题信息
        JLabel titleLabel = new JLabel(String.format(
            "<html><font color='#4FC3F7' size='4'>服务员: %d/%d | 厨师: %d/%d</font></html>",
            hotel.getWaiters().size(), hotel.getMaxWaiters(),
            hotel.getChefs().size(), hotel.getMaxChefs()));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(15));
        
        // 服务员列表
        mainPanel.add(createStaffSection("【服务员】", hotel.getWaiters(), true, dialog));
        mainPanel.add(Box.createVerticalStrut(15));
        
        // 厨师列表
        mainPanel.add(createStaffSection("【厨师】", hotel.getChefs(), false, dialog));
        
        // 底部按钮
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(new Color(50, 50, 60));
        JButton closeBtn = new JButton("关闭");
        closeBtn.addActionListener(e -> dialog.dispose());
        bottomPanel.add(closeBtn);
        
        dialog.add(new JScrollPane(mainPanel), BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JPanel createStaffSection(String title, List<? extends Employee> employees, boolean isWaiter, JDialog dialog) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), title,
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Microsoft YaHei", Font.BOLD, 14),
            Color.WHITE));
        
        if (employees.isEmpty()) {
            JLabel emptyLabel = new JLabel("暂无员工");
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            section.add(emptyLabel);
        } else {
            for (int i = 0; i < employees.size(); i++) {
                Employee emp = employees.get(i);
                section.add(createStaffCard(emp, i, isWaiter, dialog));
                section.add(Box.createVerticalStrut(5));
            }
        }
        
        return section;
    }

    private JPanel createStaffCard(Employee emp, int index, boolean isWaiter, JDialog dialog) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(new Color(60, 60, 70));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 90)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        card.setMaximumSize(new Dimension(550, 90));
        
        // 左侧：员工信息
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        
        // 名字和品质
        String tierName = emp.getTierName();
        Color tierColor = getTierColor(emp.getEmployeeTier());
        JLabel nameLabel = new JLabel(emp.getName() + " [" + tierName + "]");
        nameLabel.setForeground(tierColor);
        nameLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        
        // 等级和经验
        JLabel levelLabel = new JLabel(String.format("Lv.%d  经验: %d/%d", 
            emp.getLevel(), emp.getExp(), emp.getExpToNextLevel()));
        levelLabel.setForeground(new Color(200, 200, 200));
        levelLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
        
        // 属性
        String stats;
        if (isWaiter) {
            Waiter w = (Waiter) emp;
            stats = String.format("服务:%d  速度:%d  魅力:%d  体力:%d/%d", 
                w.getServiceSkill(), w.getSpeedSkill(), w.getCharmSkill(),
                w.getStamina(), w.getMaxStamina());
        } else {
            Chef c = (Chef) emp;
            stats = String.format("厨艺:%d  速度:%d  魅力:%d  体力:%d/%d", 
                c.getCookingSkill(), c.getSpeedSkill(), c.getCharmSkill(),
                c.getStamina(), c.getMaxStamina());
        }
        JLabel statsLabel = new JLabel(stats);
        statsLabel.setForeground(new Color(150, 150, 150));
        statsLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 10));
        
        // 状态
        String status = emp.isResting() ? "休息中" : (emp.isBusy() ? "工作中" : "空闲");
        Color statusColor = emp.isResting() ? Color.GRAY : (emp.isBusy() ? new Color(76, 175, 80) : new Color(33, 150, 243));
        JLabel statusLabel = new JLabel("状态: " + status);
        statusLabel.setForeground(statusColor);
        statusLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 10));
        
        infoPanel.add(nameLabel);
        infoPanel.add(levelLabel);
        infoPanel.add(statsLabel);
        infoPanel.add(statusLabel);
        
        // 右侧：解雇按钮
        int compensation = emp.getFireCompensation();
        JButton fireBtn = new JButton("解雇 ($" + compensation + ")");
        fireBtn.setBackground(new Color(244, 67, 54));
        fireBtn.setForeground(Color.WHITE);
        fireBtn.setFont(new Font("Microsoft YaHei", Font.BOLD, 11));
        fireBtn.setPreferredSize(new Dimension(100, 35));
        fireBtn.setFocusPainted(false);
        fireBtn.setBorderPainted(false);
        
        final int idx = index;
        fireBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(dialog,
                "确定要解雇 " + emp.getName() + " 吗？\n需要支付赔偿金 $" + compensation,
                "确认解雇",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = isWaiter ? hotel.fireWaiter(idx) : hotel.fireChef(idx);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "已解雇 " + emp.getName());
                    dialog.dispose();
                    showStaffDialog();  // 刷新
                } else {
                    JOptionPane.showMessageDialog(dialog, "解雇失败！资金不足");
                }
            }
        });
        
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(fireBtn, BorderLayout.EAST);
        
        return card;
    }

    // ========== 招聘对话框 - 抽卡模式 ==========

    private void showHireDialog() {
        JDialog dialog = new JDialog(gameWindow, "招聘员工", true);
        dialog.setSize(550, 500);
        dialog.setLocationRelativeTo(gameWindow);
        dialog.setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(50, 50, 60));
        
        // 顶部信息
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setOpaque(false);
        JLabel infoLabel = new JLabel(String.format(
            "<html><font color='#4FC3F7' size='4'>当前员工: 服务员 %d/%d | 厨师 %d/%d</font><br>" +
            "<font color='#FFD54F' size='4'>资金: $%d</font></html>",
            hotel.getWaiters().size(), hotel.getMaxWaiters(),
            hotel.getChefs().size(), hotel.getMaxChefs(),
            hotel.getMoney()));
        infoPanel.add(infoLabel);
        mainPanel.add(infoPanel);
        mainPanel.add(Box.createVerticalStrut(15));
        
        // 刷新按钮和费用
        JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        refreshPanel.setOpaque(false);
        JButton refreshBtn = new JButton("刷新候选员工 ($50)");
        refreshBtn.setBackground(new Color(255, 152, 0));
        refreshBtn.setForeground(Color.BLACK);  // 黑色文字更清晰
        refreshBtn.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setPreferredSize(new Dimension(180, 35));
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> {
            if (hotel.getMoney() >= 50) {
                hotel.spendMoney(50);
                hotel.refreshEmployeePool();
                dialog.dispose();
                showHireDialog(); // 重新打开
            } else {
                JOptionPane.showMessageDialog(dialog, "资金不足！需要 $50");
            }
        });
        refreshPanel.add(refreshBtn);
        mainPanel.add(refreshPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        
        // 服务员候选
        mainPanel.add(createCandidateSection("【服务员候选】", hotel.getWaiterPool(), true, dialog));
        mainPanel.add(Box.createVerticalStrut(15));
        
        // 厨师候选
        mainPanel.add(createCandidateSection("【厨师候选】", hotel.getChefPool(), false, dialog));
        
        // 底部按钮
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(new Color(50, 50, 60));
        JButton closeBtn = new JButton("关闭");
        closeBtn.addActionListener(e -> dialog.dispose());
        bottomPanel.add(closeBtn);
        
        dialog.add(new JScrollPane(mainPanel), BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JPanel createCandidateSection(String title, List<?> pool, boolean isWaiter, JDialog dialog) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), title,
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Microsoft YaHei", Font.BOLD, 14),
            Color.WHITE));
        
        if (pool.isEmpty()) {
            JLabel emptyLabel = new JLabel("暂无候选人，请刷新");
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            section.add(emptyLabel);
        } else {
            for (Object emp : pool) {
                section.add(createCandidateCard(emp, isWaiter, dialog));
                section.add(Box.createVerticalStrut(5));
            }
        }
        
        return section;
    }

    private JPanel createCandidateCard(Object emp, boolean isWaiter, JDialog dialog) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(new Color(60, 60, 70));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 90)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        card.setMaximumSize(new Dimension(500, 80));
        
        String name, tierName, stats;
        int tier, cost;
        Color tierColor;
        
        if (isWaiter) {
            Waiter w = (Waiter) emp;
            name = w.getName();
            tier = w.getEmployeeTier();
            tierName = getTierName(tier);
            tierColor = getTierColor(tier);
            stats = String.format("服务:%d 速度:%d 魅力:%d", 
                w.getServiceSkill(), w.getSpeedSkill(), w.getCharmSkill());
            cost = getHireCost(tier, true);
        } else {
            Chef c = (Chef) emp;
            name = c.getName();
            tier = c.getEmployeeTier();
            tierName = getTierName(tier);
            tierColor = getTierColor(tier);
            stats = String.format("厨艺:%d 速度:%d 魅力:%d", 
                c.getCookingSkill(), c.getSpeedSkill(), c.getCharmSkill());
            cost = getHireCost(tier, false);
        }
        
        // 左侧：名字和品质
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(name);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        
        JLabel tierLabel = new JLabel("[" + tierName + "]");
        tierLabel.setForeground(tierColor);
        tierLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
        
        JLabel statsLabel = new JLabel(stats);
        statsLabel.setForeground(new Color(180, 180, 180));
        statsLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
        
        leftPanel.add(nameLabel);
        leftPanel.add(tierLabel);
        leftPanel.add(statsLabel);
        
        // 右侧：招聘按钮（高亮黄色）
        JButton hireBtn = new JButton("招聘 $" + cost);
        hireBtn.setBackground(new Color(255, 193, 7));  // 高亮黄色
        hireBtn.setForeground(Color.BLACK);  // 黑色文字更清晰
        hireBtn.setFont(new Font("Microsoft YaHei", Font.BOLD, 11));
        hireBtn.setPreferredSize(new Dimension(90, 35));
        hireBtn.setFocusPainted(false);
        hireBtn.setBorderPainted(false);
        hireBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // 检查是否可以招聘
        boolean canHire = hotel.getMoney() >= cost;
        if (isWaiter) {
            canHire = canHire && hotel.getWaiters().size() < hotel.getMaxWaiters();
        } else {
            canHire = canHire && hotel.getChefs().size() < hotel.getMaxChefs();
        }
        
        if (!canHire) {
            hireBtn.setEnabled(false);
            hireBtn.setBackground(Color.GRAY);
        }
        
        final boolean isWaiterFinal = isWaiter;
        final Object empFinal = emp;
        hireBtn.addActionListener(e -> {
            boolean success;
            if (isWaiterFinal) {
                Waiter w = (Waiter) empFinal;
                success = hotel.hireWaiterFromPool(w, cost);
            } else {
                Chef c = (Chef) empFinal;
                success = hotel.hireChefFromPool(c, cost);
            }
            
            if (success) {
                JOptionPane.showMessageDialog(dialog, "招聘成功！");
                dialog.dispose();
                showHireDialog();
            } else {
                JOptionPane.showMessageDialog(dialog, "招聘失败！资金不足或已满员");
            }
        });
        
        card.add(leftPanel, BorderLayout.CENTER);
        card.add(hireBtn, BorderLayout.EAST);
        
        return card;
    }

    private String getTierName(int tier) {
        switch (tier) {
            case 1: return "实习";
            case 2: return "普通";
            case 3: return "中级";
            case 4: return "高级";
            case 5: return "特级";
            default: return "未知";
        }
    }

    private Color getTierColor(int tier) {
        switch (tier) {
            case 1: return new Color(180, 180, 180);  // 灰色
            case 2: return new Color(76, 175, 80);    // 绿色
            case 3: return new Color(33, 150, 243);   // 蓝色
            case 4: return new Color(156, 39, 176);   // 紫色
            case 5: return new Color(255, 215, 0);    // 金色
            default: return Color.WHITE;
        }
    }

    private int getHireCost(int tier, boolean isWaiter) {
        int base = isWaiter ? 150 : 200;
        return base * tier + (tier - 1) * 100;
    }

    private void showTableDialog() {
        String[] options = {"2人小桌 - $100", "4人桌 - $200", "6人大桌 - $350", "取消"};
        int choice = JOptionPane.showOptionDialog(gameWindow,
            "当前桌子数: " + hotel.getTables().size() + "/" + hotel.getMaxTables() + 
            "\n资金: $" + hotel.getMoney(),
            "购买桌子",
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
            null, options, options[0]);
        
        int[] seats = {2, 4, 6};
        int[] costs = {100, 200, 350};
        
        if (choice >= 0 && choice < 3) {
            if (hotel.buyTable(seats[choice], costs[choice])) {
                JOptionPane.showMessageDialog(gameWindow, "购买成功！");
            } else {
                JOptionPane.showMessageDialog(gameWindow, "购买失败！资金不足或桌子已满");
            }
        }
    }

    private void showUpgradeDialog() {
        int level = hotel.getHotelLevel();
        int exp = hotel.getHotelExp();
        int expMax = hotel.getExpToNextLevel();
        boolean canUpgrade = hotel.canLevelUp();
        int upgradeCost = hotel.getUpgradeCost();
        
        StringBuilder message = new StringBuilder();
        message.append(String.format("当前等级: Lv.%d / 150\n", level));
        message.append(String.format("经验进度: %d / %d\n\n", exp, expMax));
        
        if (canUpgrade) {
            message.append("✅ 经验已满！可以升级\n");
            message.append(String.format("升级费用: $%d\n\n", upgradeCost));
            message.append(String.format("当前资金: $%d", hotel.getMoney()));
        } else {
            int expNeeded = expMax - exp;
            message.append(String.format("还需经验: %d\n\n", expNeeded));
            message.append("📌 经验来源：\n");
            message.append("  - 顾客消费\n");
            message.append("  - 员工工作\n");
            message.append("  - 菜品售出");
        }
        
        if (level >= 150) {
            JOptionPane.showMessageDialog(gameWindow, 
                "🎉 恭喜！您已达到最高等级 Lv.150！", 
                "已满级", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if (canUpgrade) {
            String[] options = {"升级 ($" + upgradeCost + ")", "取消"};
            int choice = JOptionPane.showOptionDialog(gameWindow, message.toString(), "餐厅升级",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            
            if (choice == 0) {
                if (hotel.performLevelUp()) {
                    JOptionPane.showMessageDialog(gameWindow, 
                        "🎉 升级成功！现在是 Lv." + hotel.getHotelLevel() + "！");
                } else {
                    JOptionPane.showMessageDialog(gameWindow, "升级失败！资金不足");
                }
            }
        } else {
            JOptionPane.showMessageDialog(gameWindow, message.toString(), "餐厅升级", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showMenuDialog() {
        JDialog dialog = new JDialog(gameWindow, "菜单管理", true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(gameWindow);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(50, 50, 60));
        
        // 标题
        JLabel titleLabel = new JLabel("【当前菜单】");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(10));
        
        // 菜品列表
        for (Dish d : hotel.getMenu()) {
            JPanel dishPanel = new JPanel(new BorderLayout());
            dishPanel.setBackground(new Color(60, 60, 70));
            dishPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            dishPanel.setMaximumSize(new Dimension(450, 50));
            
            JLabel dishLabel = new JLabel(String.format(
                "<html><font color='white'>%s</font> " +
                "<font color='#4FC3F7'>$%d</font> " +
                "<font color='gray'>| 时间:%ds | Lv.%d</font></html>",
                d.getName(), d.getPrice(), d.getCookTime(), d.getLevel()));
            dishPanel.add(dishLabel, BorderLayout.CENTER);
            
            mainPanel.add(dishPanel);
            mainPanel.add(Box.createVerticalStrut(5));
        }
        
        mainPanel.add(Box.createVerticalStrut(15));
        
        // 提示
        JLabel tipLabel = new JLabel("<html><font color='gray'>提示：升级餐厅可解锁更多菜品</font></html>");
        tipLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(tipLabel);
        
        // 关闭按钮
        JButton closeBtn = new JButton("关闭");
        closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeBtn.addActionListener(e -> dialog.dispose());
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(closeBtn);
        
        dialog.add(new JScrollPane(mainPanel));
        dialog.setVisible(true);
    }
}
