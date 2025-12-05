package com.restaurant.model;

/**
 * 餐桌类
 */
public class Table {
    private int id;
    private int seats;          // 座位数
    private Customer customer;  // 当前顾客
    private boolean occupied;   // 是否有人

    public Table(int id, int seats) {
        this.id = id;
        this.seats = seats;
        this.customer = null;
        this.occupied = false;
    }

    /**
     * 顾客入座
     */
    public void seatCustomer(Customer customer) {
        this.customer = customer;
        this.occupied = true;
        customer.sitDown(this);
    }

    /**
     * 顾客离开
     */
    public void clearTable() {
        this.customer = null;
        this.occupied = false;
    }

    /**
     * 检查是否空闲
     */
    public boolean isAvailable() {
        return !occupied;
    }

    // Getters
    public int getId() { return id; }
    public int getSeats() { return seats; }
    public Customer getCustomer() { return customer; }
    public boolean isOccupied() { return occupied; }

    @Override
    public String toString() {
        if (occupied && customer != null) {
            return String.format("桌%d[%d座] -> %s", id, seats, customer.getName());
        } else {
            return String.format("桌%d[%d座] 空闲", id, seats);
        }
    }
}

