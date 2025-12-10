package com.restaurant.dto;

import com.restaurant.model.Customer;

/**
 * 顾客DTO
 */
public class CustomerDTO {
    private int id;
    private String name;
    private String type;
    private String state;
    private int patience;
    private int maxPatience;
    private int tableId;
    private int orderedDishes;
    private int receivedDishes;

    public CustomerDTO() {
    }

    public static CustomerDTO fromCustomer(Customer c) {
        CustomerDTO dto = new CustomerDTO();
        dto.id = c.getId();
        dto.name = c.getName();
        dto.type = c.getType().name;
        dto.state = c.getState().name();
        dto.patience = c.getPatience();
        dto.maxPatience = c.getMaxPatience();
        dto.tableId = c.getTable() != null ? c.getTable().getId() : -1;
        dto.orderedDishes = c.getOrders().size();
        dto.receivedDishes = c.getDishesReceived();
        return dto;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getState() {
        return state;
    }

    public int getPatience() {
        return patience;
    }

    public int getMaxPatience() {
        return maxPatience;
    }

    public int getTableId() {
        return tableId;
    }

    public int getOrderedDishes() {
        return orderedDishes;
    }

    public int getReceivedDishes() {
        return receivedDishes;
    }
}
