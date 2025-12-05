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
    private ControlPanel controlPanel;
    
    // 游戏状态
    private boolean isPaused = false;
    private int gameSpeed = 1;

    public GameWindow(Hotel hotel) {
        this.hotel = hotel;
        this.gameTimer = new GameTimer(hotel);
        gameTimer.setTickListener(this);
        
        initWindow();
        initComponents();
        initLayout();
        initKeyBindings();
        
        // 启动游戏
        gameTimer.start();
    }

    private void initWindow() {
        setTitle("🏨 小旅店发家记 - Restaurant Owner");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1000, 700);
        setMinimumSize(new Dimension(800, 600));
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
        controlPanel = new ControlPanel(hotel, this);
    }

    private void initLayout() {
        setLayout(new BorderLayout(5, 5));
        
        // 顶部状态栏
        add(statusPanel, BorderLayout.NORTH);
        
        // 中间区域：餐厅场景
        add(restaurantPanel, BorderLayout.CENTER);
        
        // 右侧日志
        JScrollPane logScroll = new JScrollPane(logPanel);
        logScroll.setPreferredSize(new Dimension(280, 0));
        logScroll.setBorder(BorderFactory.createTitledBorder("📜 游戏日志"));
        add(logScroll, BorderLayout.EAST);
        
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

    @Override
    public void onTick(Hotel hotel) {
        // 在EDT线程中更新UI
        SwingUtilities.invokeLater(() -> {
            statusPanel.update();
            restaurantPanel.repaint();
            logPanel.update();
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
        gameTimer.togglePause();
        SaveManager.saveGame(hotel);
        JOptionPane.showMessageDialog(this, "💾 游戏已保存！", "保存成功", JOptionPane.INFORMATION_MESSAGE);
        gameTimer.togglePause();
    }
}

