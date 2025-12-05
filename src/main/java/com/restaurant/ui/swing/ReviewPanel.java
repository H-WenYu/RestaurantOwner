package com.restaurant.ui.swing;

import com.restaurant.game.Hotel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * 顾客评价留言板面板
 */
public class ReviewPanel extends JPanel {
    private Hotel hotel;
    private JPanel reviewsContainer;
    
    public ReviewPanel(Hotel hotel) {
        this.hotel = hotel;
        setBackground(new Color(45, 50, 55));
        setLayout(new BorderLayout());
        
        // 评论容器
        reviewsContainer = new JPanel();
        reviewsContainer.setLayout(new BoxLayout(reviewsContainer, BoxLayout.Y_AXIS));
        reviewsContainer.setBackground(new Color(45, 50, 55));
        
        add(reviewsContainer, BorderLayout.NORTH);
        
        update();
    }
    
    public void update() {
        reviewsContainer.removeAll();
        
        List<String> reviews = hotel.getRecentReviews();
        
        if (reviews.isEmpty()) {
            JLabel emptyLabel = new JLabel("暂无评价");
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            reviewsContainer.add(emptyLabel);
        } else {
            // 只显示最新的30条
            int count = Math.min(reviews.size(), 30);
            for (int i = 0; i < count; i++) {
                String review = reviews.get(i);
                JPanel card = createReviewCard(review);
                reviewsContainer.add(card);
                reviewsContainer.add(Box.createVerticalStrut(3));
            }
        }
        
        // 显示统计
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        double avgRating = hotel.getAverageRating();
        int goodCount = hotel.getGoodRatings();
        int totalCount = hotel.getTotalCustomersServed();
        
        JLabel statsLabel = new JLabel(String.format(
            "<html><font color='#FFD54F'>平均评分: %.1f</font> | " +
            "<font color='#4FC3F7'>好评: %d</font> | " +
            "<font color='gray'>总服务: %d</font></html>",
            avgRating, goodCount, totalCount));
        statsLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 10));
        statsPanel.add(statsLabel);
        
        reviewsContainer.add(statsPanel);
        
        revalidate();
        repaint();
    }
    
    private JPanel createReviewCard(String review) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(55, 60, 65));
        card.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        card.setMaximumSize(new Dimension(280, 40));
        
        // 解析评分和评论
        String stars = "";
        String comment = review;
        Color starColor = Color.GRAY;
        
        if (review.contains("★")) {
            int starIndex = review.indexOf("★");
            String ratingStr = review.substring(0, starIndex).trim();
            try {
                double rating = Double.parseDouble(ratingStr);
                stars = getStarsDisplay(rating);
                starColor = getStarColor(rating);
                comment = review.substring(starIndex + 1).trim();
            } catch (NumberFormatException e) {
                // 保持原样
            }
        }
        
        JLabel starsLabel = new JLabel(stars);
        starsLabel.setForeground(starColor);
        starsLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 11));
        
        JLabel commentLabel = new JLabel(comment);
        commentLabel.setForeground(new Color(200, 200, 200));
        commentLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 10));
        
        card.add(starsLabel, BorderLayout.WEST);
        card.add(commentLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private String getStarsDisplay(double rating) {
        StringBuilder sb = new StringBuilder();
        int fullStars = (int) rating;
        boolean halfStar = (rating - fullStars) >= 0.5;
        
        for (int i = 0; i < fullStars && i < 5; i++) {
            sb.append("★");
        }
        if (halfStar && fullStars < 5) {
            sb.append("☆");
        }
        
        // 补齐空星
        int total = fullStars + (halfStar ? 1 : 0);
        for (int i = total; i < 5; i++) {
            sb.append("·");
        }
        
        return sb.toString() + " ";
    }
    
    private Color getStarColor(double rating) {
        if (rating >= 4.0) {
            return new Color(255, 215, 0);  // 金色
        } else if (rating >= 3.0) {
            return new Color(255, 193, 7);  // 黄色
        } else if (rating >= 2.0) {
            return new Color(255, 152, 0);  // 橙色
        } else {
            return new Color(244, 67, 54);  // 红色
        }
    }
}

