package com.restaurant.ui.swing;

import com.restaurant.game.Hotel;

import javax.swing.*;
import java.awt.*;

/**
 * 状态栏面板 - 显示金钱、声望、等级等
 */
public class StatusPanel extends JPanel {
    private Hotel hotel;
    
    private JLabel nameLabel;
    private JLabel levelLabel;
    private JLabel expLabel;
    private JProgressBar expBar;
    private JLabel moneyLabel;
    private JLabel reputationLabel;
    private JLabel timeLabel;
    private JLabel statsLabel;

    public StatusPanel(Hotel hotel) {
        this.hotel = hotel;
        initComponents();
        initLayout();
        update();
    }

    private void initComponents() {
        setBackground(new Color(30, 30, 40));
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        nameLabel = createLabel("🏨 " + hotel.getName(), 18, true);
        levelLabel = createLabel("Lv.1", 16, true);
        expLabel = createLabel("经验: 0/100", 12, false);
        
        expBar = new JProgressBar(0, 100);
        expBar.setStringPainted(true);
        expBar.setForeground(new Color(76, 175, 80));
        expBar.setBackground(new Color(60, 60, 60));
        expBar.setPreferredSize(new Dimension(150, 20));
        
        moneyLabel = createLabel("💰 $1000", 14, true);
        moneyLabel.setForeground(new Color(255, 215, 0));
        
        reputationLabel = createLabel("⭐ 30", 14, true);
        reputationLabel.setForeground(new Color(255, 193, 7));
        
        timeLabel = createLabel("⏰ 00:00:00", 14, false);
        
        statsLabel = createLabel("服务: 0 | 流失: 0", 12, false);
    }

    private JLabel createLabel(String text, int fontSize, boolean bold) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Microsoft YaHei", bold ? Font.BOLD : Font.PLAIN, fontSize));
        return label;
    }

    private void initLayout() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 20, 5));
        
        // 餐厅名称和等级
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        namePanel.setOpaque(false);
        namePanel.add(nameLabel);
        namePanel.add(levelLabel);
        add(namePanel);
        
        // 经验条
        JPanel expPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        expPanel.setOpaque(false);
        expPanel.add(expLabel);
        expPanel.add(expBar);
        add(expPanel);
        
        // 金钱
        add(moneyLabel);
        
        // 声望
        add(reputationLabel);
        
        // 时间
        add(timeLabel);
        
        // 统计
        add(statsLabel);
    }

    public void update() {
        levelLabel.setText("Lv." + hotel.getHotelLevel());
        
        int exp = hotel.getHotelExp();
        int expMax = hotel.getExpToNextLevel();
        expLabel.setText("经验: " + exp + "/" + expMax);
        expBar.setMaximum(expMax);
        expBar.setValue(exp);
        expBar.setString(exp + "/" + expMax);
        
        moneyLabel.setText("💰 $" + hotel.getMoney());
        
        int rep = hotel.getReputation();
        reputationLabel.setText("⭐ " + rep);
        if (rep >= 70) {
            reputationLabel.setForeground(new Color(76, 175, 80));
        } else if (rep >= 40) {
            reputationLabel.setForeground(new Color(255, 193, 7));
        } else {
            reputationLabel.setForeground(new Color(244, 67, 54));
        }
        
        int time = hotel.getGameTime();
        timeLabel.setText(String.format("⏰ %02d:%02d:%02d", time / 3600, (time % 3600) / 60, time % 60));
        
        statsLabel.setText("服务: " + hotel.getTotalCustomersServed() + 
                          " | 流失: " + hotel.getTotalCustomersLost() +
                          " | 收入: $" + hotel.getTotalRevenue());
    }
}

