package com.example.inventra.OOP;

public class Category {
    private int catID;
    private String categoryName;
    private int tax;

    // Default Constructor
    public Category() {
    }

    // Parameterized Constructor
    public Category(int catID, String categoryName, int tax) {
        this.catID = catID;
        this.categoryName = categoryName;
        this.tax = tax;
    }

    public int getCatID() {
        return catID;
    }

    public void setCatID(int catID) {
        this.catID = catID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }
}

