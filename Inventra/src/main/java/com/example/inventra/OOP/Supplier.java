package com.example.inventra.OOP;

import java.util.Date;

public class Supplier {
    private int supplyID;
    private String supplyName;
    private String supplyDate;
    private int quantity;
    private  float totalAmount;

    public Supplier() {
        this.supplyID = 0;
        this.quantity = 0;
        this.supplyDate = "";
        this.supplyName = "";
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Supplier(int supplyID, int quantity, String supplyDate, String supplyName) {
        this.supplyID = supplyID;
        this.quantity = quantity;
        this.supplyDate = supplyDate;
        this.supplyName = supplyName;
    }

    public int getSupplyID() {
        return supplyID;
    }

    public void setSupplyID(int supplyID) {
        this.supplyID = supplyID;
    }

    public String getSupplyName() {
        return supplyName;
    }

    public void setSupplyName(String supplyName) {
        this.supplyName = supplyName;
    }

    public String getSupplyDate() {
        return supplyDate;
    }

    public void setSupplyDate(String supplyDate) {
        this.supplyDate = supplyDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
