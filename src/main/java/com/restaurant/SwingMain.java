package com.restaurant;

import com.restaurant.game.Hotel;
import com.restaurant.game.SaveManager;
import com.restaurant.ui.swing.GameWindow;

import javax.swing.*;
import java.awt.*;

/**
 * Swing 图形界面版本入口
 */
public class SwingMain {
    
    public static void main(String[] args) {
        // 设置外观
        try {
            // 尝试使用系统外观
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // 使用默认外观
        }
        
        // 设置中文字体
        setUIFont();
        
        // 在EDT线程中启动
        SwingUtilities.invokeLater(() -> {
            // 显示启动界面
            Hotel hotel = showStartupDialog();
            
            if (hotel != null) {
                // 创建游戏窗口
                GameWindow window = new GameWindow(hotel);
                window.setVisible(true);
            }
        });
    }

    /**
     * 显示启动对话框
     */
    private static Hotel showStartupDialog() {
        // 自定义启动对话框
        JDialog startDialog = new JDialog((Frame) null, "🏨 小旅店发家记", true);
        startDialog.setSize(500, 400);
        startDialog.setLocationRelativeTo(null);
        startDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(45, 45, 55));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // 标题
        JLabel titleLabel = new JLabel("🏨 小旅店发家记");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 28));
        titleLabel.setForeground(new Color(255, 193, 7));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        
        JLabel subtitleLabel = new JLabel("Restaurant Owner");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        subtitleLabel.setForeground(new Color(150, 150, 150));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(subtitleLabel);
        
        mainPanel.add(Box.createVerticalStrut(30));
        
        // 游戏说明
        JTextArea descArea = new JTextArea(
            "欢迎来到小旅店发家记！\n\n" +
            "• 从一家小餐馆开始，发展成米其林大酒店\n" +
            "• 雇佣员工，购买桌子，解锁新菜品\n" +
            "• 管理顾客，保持声望，赚取利润\n" +
            "• 随机事件增添乐趣与挑战\n\n" +
            "游戏会自动保存，下次可以继续！"
        );
        descArea.setEditable(false);
        descArea.setOpaque(false);
        descArea.setForeground(new Color(200, 200, 200));
        descArea.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        descArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(descArea);
        
        mainPanel.add(Box.createVerticalStrut(30));
        
        // 按钮面板
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.setOpaque(false);
        
        final Hotel[] result = {null};
        
        // 检查是否有存档
        if (SaveManager.hasSaveFile()) {
            JButton continueBtn = createStyledButton("📂 继续游戏", new Color(76, 175, 80));
            continueBtn.addActionListener(e -> {
                result[0] = SaveManager.loadGame();
                if (result[0] == null) {
                    result[0] = new Hotel("老王餐馆");
                }
                startDialog.dispose();
            });
            btnPanel.add(continueBtn);
            
            JButton newGameBtn = createStyledButton("🆕 新游戏", new Color(33, 150, 243));
            newGameBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(startDialog, 
                    "开始新游戏将覆盖现有存档，确定吗？", 
                    "确认", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    result[0] = new Hotel("老王餐馆");
                    startDialog.dispose();
                }
            });
            btnPanel.add(newGameBtn);
        } else {
            JButton startBtn = createStyledButton("🎮 开始游戏", new Color(76, 175, 80));
            startBtn.addActionListener(e -> {
                result[0] = new Hotel("老王餐馆");
                startDialog.dispose();
            });
            btnPanel.add(startBtn);
        }
        
        JButton exitBtn = createStyledButton("❌ 退出", new Color(244, 67, 54));
        exitBtn.addActionListener(e -> {
            startDialog.dispose();
            System.exit(0);
        });
        btnPanel.add(exitBtn);
        
        mainPanel.add(btnPanel);
        
        // 版本信息
        mainPanel.add(Box.createVerticalStrut(20));
        JLabel versionLabel = new JLabel("Version 1.0 - Swing Edition");
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        versionLabel.setForeground(new Color(100, 100, 100));
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(versionLabel);
        
        startDialog.add(mainPanel);
        startDialog.setVisible(true);
        
        return result[0];
    }

    private static JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(140, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * 设置全局字体
     */
    private static void setUIFont() {
        Font font = new Font("Microsoft YaHei", Font.PLAIN, 12);
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof Font) {
                UIManager.put(key, font);
            }
        }
    }
}

