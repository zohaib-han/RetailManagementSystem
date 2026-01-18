package com.example.inventra.OOP;

import java.time.LocalDateTime;

public class Order {
    private int orderID;
    private Cashier cashier;
    private String orderDate;
    private String status;
    private Customer customer;
    private String orderType;
    private String customerNumber;
    private String customerName;
    private int cashierID;
    private LocalDateTime orderDat;



    public Order(int orderID, Cashier cashier, String orderDate, String status, Customer customer, String orderType) {
        this.orderID = orderID;
        this.cashier = cashier;
        this.orderDate = orderDate;
        this.status = status;
        this.customer = customer;
        this.orderType = orderType;
    }

    public Order(int orderID, int cashierID, LocalDateTime orderDate, String status, String customerNumber, String customerName, String orderType) {
        this.orderID = orderID;
        this.cashierID = cashierID;
        this.orderDat = orderDate;
        this.status = status;
        this.customerNumber = customerNumber;
        this.customerName = customerName;
        this.orderType = orderType;
    }


    public Order() {
        this.orderID = 0;
        this.cashier = null;
        this.orderDate = null;
        this.status = "";
        this.customer = null;
        this.orderType = "";
    }

    public LocalDateTime getOrderDate() {
        return orderDat;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDat = orderDate;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getCashierID() {
        return cashierID;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public Cashier getCashier() {
        return cashier;
    }

    public void setCashier(Cashier cashier) {
        this.cashier = cashier;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
}
