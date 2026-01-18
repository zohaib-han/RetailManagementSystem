package com.example.inventra.OOP;

import java.time.LocalDateTime;

public class Invoice {
    private int invoiceID;
    private double amount;
    private LocalDateTime date;
    private double discount;
    private int orderID;

    // Constructor
    public Invoice(int invoiceID, double amount, LocalDateTime date, double discount, int orderID) {
        this.invoiceID = invoiceID;
        this.amount = amount;
        this.date = date;
        this.discount = discount;
        this.orderID = orderID;
    }

    // Overloaded constructor for creating new invoices
    public Invoice(double amount, double discount, int orderID) {
        this.amount = amount;
        this.discount = discount;
        this.orderID = orderID;
        this.date = LocalDateTime.now(); // Set current date and time
    }

    // Getters and setters
    public int getInvoiceID() {
        return invoiceID;
    }

    public void setInvoiceID(int invoiceID) {
        this.invoiceID = invoiceID;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }
}
