package com.example.inventra.OOP;

import javafx.beans.property.*;

public class OrderProducts {
    private int orderID;
    private int productID;
    private StringProperty barcode;
    private StringProperty productName;
    private IntegerProperty quantity;
    private DoubleProperty price;

    private Order order;
    private Product product;

    public OrderProducts(Order order, Product product, int quantity) {
        this.order = order;
        this.product = product;
        this.quantity = new SimpleIntegerProperty(quantity);
        this.barcode = new SimpleStringProperty(product.getBarcode());
        this.productName = new SimpleStringProperty(product.getName());
        this.price = new SimpleDoubleProperty(product.getPrice() * quantity); // Total price
    }

    public OrderProducts() {
        this.order = null;
        this.product = null;
        this.quantity = new SimpleIntegerProperty(0);
        this.barcode = new SimpleStringProperty();
        this.productName = new SimpleStringProperty();
        this.price = new SimpleDoubleProperty(0.0);
    }

    public OrderProducts(int orderID, int productID, int quantity) {
        this.orderID = orderID;
        this.productID = productID;
        this.quantity = new SimpleIntegerProperty(quantity);
    }

    // Getters and setters for TableView binding
    public String getBarcode() {
        return barcode.get();
    }

    public void setBarcode(String barcode) {
        this.barcode.set(barcode);
    }

    public int getProductID() {
        return productID;
    }


    public StringProperty barcodeProperty() {
        return barcode;
    }

    public String getProductName() {
        return productName.get();
    }

    public void setProductName(String productName) {
        this.productName.set(productName);
    }

    public StringProperty productNameProperty() {
        return productName;
    }

    public int getQuantity() {
        return quantity.get();
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public double getPrice() {
        return price.get();
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public DoubleProperty priceProperty() {
        return price;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
