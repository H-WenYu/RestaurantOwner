package com.restaurant.ui.swing;

import com.restaurant.game.Hotel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * 日志面板 - 显示游戏事件日志
 */
public class LogPanel extends JPanel {
    private Hotel hotel;
    private JTextArea logArea;
    private int lastLogCount = 0;

    public LogPanel(Hotel hotel) {
        this.hotel = hotel;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(40, 40, 50));
        
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(new Color(40, 40, 50));
        logArea.setForeground(new Color(200, 200, 200));
        logArea.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setMargin(new Insets(5, 5, 5, 5));
        
        add(logArea, BorderLayout.CENTER);
    }

    public void update() {
        List<String> logs = hotel.getGameLogs();
        
        // 只在有新日志时更新
        if (logs.size() != lastLogCount) {
            lastLogCount = logs.size();
            
            StringBuilder sb = new StringBuilder();
            // 显示最近20条
            int start = Math.max(0, logs.size() - 20);
            for (int i = start; i < logs.size(); i++) {
                sb.append(logs.get(i)).append("\n");
            }
            
            logArea.setText(sb.toString());
            // 滚动到底部
            logArea.setCaretPosition(logArea.getDocument().getLength());
        }
    }
}

