package com.restaurant.ui.swing;

import com.restaurant.game.Hotel;
import com.restaurant.game.GameTimer;
import com.restaurant.game.SaveManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 游戏主窗口
 */
public class GameWindow extends JFrame implements GameTimer.GameTickListener {
    private Hotel hotel;
    private GameTimer gameTimer;
    
    // UI组件
    private StatusPanel statusPanel;
    private RestaurantPanel restaurantPanel;
    private LogPanel logPanel;
    private ReviewPanel reviewPanel;
    private ControlPanel controlPanel;
    
    // 游戏状态
    private boolean isPaused = true;  // 默认暂停
    private int gameSpeed = 1;
    private boolean tutorialShown = false;

    public GameWindow(Hotel hotel) {
        this.hotel = hotel;
        this.gameTimer = new GameTimer(hotel);
        gameTimer.setTickListener(this);
        
        initWindow();
        initComponents();
        initLayout();
        initKeyBindings();
        
        // 启动游戏（暂停状态）
        gameTimer.start();
        gameTimer.togglePause();  // 立即暂停
        
        // 显示新手引导
        SwingUtilities.invokeLater(() -> {
            if (hotel.getWaiters().isEmpty() && hotel.getChefs().isEmpty()) {
                showTutorial();
            }
        });
    }

    private void initWindow() {
        setTitle("重生之我在餐厅当老板 - Restaurant Owner");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1200, 750);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);
        
        // 窗口关闭时保存
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(
                    GameWindow.this,
                    "确定要退出吗？游戏将自动保存。",
                    "退出游戏",
                    JOptionPane.YES_NO_OPTION
                );
                if (result == JOptionPane.YES_OPTION) {
                    gameTimer.stop();
                    SaveManager.saveGame(hotel);
                    dispose();
                    System.exit(0);
                }
            }
        });
    }

    private void initComponents() {
        statusPanel = new StatusPanel(hotel);
        restaurantPanel = new RestaurantPanel(hotel);
        logPanel = new LogPanel(hotel);
        reviewPanel = new ReviewPanel(hotel);
        controlPanel = new ControlPanel(hotel, this);
    }

    private void initLayout() {
        setLayout(new BorderLayout(5, 5));
        
        // 顶部状态栏
        add(statusPanel, BorderLayout.NORTH);
        
        // 中间区域：餐厅场景
        add(restaurantPanel, BorderLayout.CENTER);
        
        // 右侧面板（日志+评论）
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(new Color(45, 45, 45));
        rightPanel.setPreferredSize(new Dimension(300, 0));
        
        // 日志面板
        JScrollPane logScroll = new JScrollPane(logPanel);
        logScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 80)),
            "游戏日志",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Microsoft YaHei", Font.BOLD, 12),
            Color.WHITE));
        logScroll.setPreferredSize(new Dimension(290, 300));
        
        // 评论面板
        JScrollPane reviewScroll = new JScrollPane(reviewPanel);
        reviewScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 80)),
            "顾客评价",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Microsoft YaHei", Font.BOLD, 12),
            Color.WHITE));
        reviewScroll.setPreferredSize(new Dimension(290, 250));
        
        rightPanel.add(logScroll);
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(reviewScroll);
        
        add(rightPanel, BorderLayout.EAST);
        
        // 底部控制按钮
        add(controlPanel, BorderLayout.SOUTH);
        
        // 设置背景色
        getContentPane().setBackground(new Color(45, 45, 45));
    }

    private void initKeyBindings() {
        // 快捷键
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0), "pause");
        getRootPane().getActionMap().put("pause", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                togglePause();
            }
        });
        
        // 数字键调速
        for (int i = 1; i <= 4; i++) {
            final int speed = i;
            getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_0 + i, 0), "speed" + i);
            getRootPane().getActionMap().put("speed" + i, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setGameSpeed(speed);
                }
            });
        }
    }

    private void showTutorial() {
        if (tutorialShown) return;
        tutorialShown = true;
        
        JDialog tutorial = new JDialog(this, "欢迎来到重生之我在餐厅当老板！", true);
        tutorial.setSize(450, 400);
        tutorial.setLocationRelativeTo(this);
        tutorial.setLayout(new BorderLayout());
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        content.setBackground(new Color(50, 50, 60));
        
        String[] steps = {
            "欢迎来到重生之我在餐厅当老板！",
            "",
            "【新手指南】",
            "",
            "1. 首先点击 [招聘] 按钮",
            "   - 招聘至少1名服务员",
            "   - 招聘至少1名厨师",
            "",
            "2. 点击 [继续] 或按 P 键开始游戏",
            "",
            "3. 顾客会自动到来",
            "   - 服务员迎客、点单、上菜",
            "   - 厨师在厨房做菜",
            "",
            "4. 顾客结账后获得金钱和经验",
            "",
            "5. 升级餐厅解锁更多内容！",
            "",
            "祝您生意兴隆！"
        };
        
        for (String line : steps) {
            JLabel label = new JLabel(line);
            label.setForeground(line.startsWith("【") ? new Color(255, 193, 7) : Color.WHITE);
            label.setFont(new Font("Microsoft YaHei", 
                line.startsWith("【") ? Font.BOLD : Font.PLAIN, 
                line.startsWith("【") ? 16 : 13));
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(label);
        }
        
        JButton startBtn = new JButton("我知道了，开始游戏！");
        startBtn.setBackground(new Color(255, 193, 7));  // 高亮黄色背景
        startBtn.setForeground(Color.BLACK);  // 黑色文字更清晰
        startBtn.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        startBtn.setFocusPainted(false);
        startBtn.setBorderPainted(false);
        startBtn.setPreferredSize(new Dimension(200, 40));
        startBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startBtn.addActionListener(e -> {
            tutorial.dispose();
        });
        
        // 使用居中面板确保按钮在弹窗中间
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setOpaque(false);
        btnPanel.add(startBtn);
        
        content.add(Box.createVerticalStrut(20));
        content.add(btnPanel);
        
        tutorial.add(new JScrollPane(content), BorderLayout.CENTER);
        tutorial.setVisible(true);
    }

    @Override
    public void onTick(Hotel hotel) {
        // 在EDT线程中更新UI
        SwingUtilities.invokeLater(() -> {
            statusPanel.update();
            restaurantPanel.repaint();
            logPanel.update();
            reviewPanel.update();
            controlPanel.update();
        });
    }

    public void togglePause() {
        isPaused = !isPaused;
        gameTimer.togglePause();
        controlPanel.updatePauseButton(isPaused);
    }

    public void setGameSpeed(int speed) {
        this.gameSpeed = speed;
        gameTimer.setSpeed(speed);
        controlPanel.updateSpeedLabel(speed);
    }

    public boolean isPaused() {
        return isPaused;
    }

    public int getGameSpeed() {
        return gameSpeed;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void saveGame() {
        boolean wasPaused = isPaused;
        if (!wasPaused) {
            gameTimer.togglePause();
        }
        SaveManager.saveGame(hotel);
        JOptionPane.showMessageDialog(this, "游戏已保存！", "保存成功", JOptionPane.INFORMATION_MESSAGE);
        if (!wasPaused) {
            gameTimer.togglePause();
        }
    }
}
