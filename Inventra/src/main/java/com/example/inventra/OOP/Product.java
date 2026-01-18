package com.example.inventra.OOP;

import com.example.inventra.DBHandler.ProductHandler;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.*;

public class Product {
    private IntegerProperty productID;
    private StringProperty name;
    private ObjectProperty<Category> category; // Reference to Category object
    private DoubleProperty price;
    private DoubleProperty purchasePrice;
    private IntegerProperty stockQuantity;
    private StringProperty barcode;
    private String cn;
    private ProductHandler productHandler = new ProductHandler();
    private ObjectProperty<Supplier> supplier; // Reference to Category object


    // Default Constructor
    public Product() {
        this.productID = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty();
        this.category = new SimpleObjectProperty<>();
        this.price = new SimpleDoubleProperty();
        this.stockQuantity = new SimpleIntegerProperty();
        this.barcode = new SimpleStringProperty();
        this.cn = "";
        this.supplier = new SimpleObjectProperty<>();
        this.purchasePrice = new SimpleDoubleProperty();
    }

    // Parameterized Constructor
    public Product(int productID, String name, Category category, double price, int stockQuantity, String barcode, double pur) {
        this.productID = new SimpleIntegerProperty(productID);
        this.name = new SimpleStringProperty(name);
        this.category = new SimpleObjectProperty<>(category);
        this.price = new SimpleDoubleProperty(price);
        this.stockQuantity = new SimpleIntegerProperty(stockQuantity);
        this.barcode = new SimpleStringProperty(barcode);
        this.cn = category.getCategoryName();
        this.purchasePrice = new SimpleDoubleProperty(pur);
        this.supplier = new SimpleObjectProperty<>();
    }

    public Product(int productID, String name, Category category, double price, int stockQuantity, String barcode) {
        this.productID = new SimpleIntegerProperty(productID);
        this.name = new SimpleStringProperty(name);
        this.category = new SimpleObjectProperty<>(category);
        this.price = new SimpleDoubleProperty(price);
        this.stockQuantity = new SimpleIntegerProperty(stockQuantity);
        this.barcode = new SimpleStringProperty(barcode);
        this.cn = category.getCategoryName();
    }

    public Supplier getSupplier(){
        return supplier.get();
    }

    public void setSupplier(Supplier s){
        this.supplier.set(s);
    }


    public double getPurchasePrice() {
        return purchasePrice.get();
    }

    public DoubleProperty purchasePriceProperty() {
        return purchasePrice;
    }

    public void setPurchasePrice(Double p){
        purchasePrice.set(p);
    }


    // Getters and Setters with Properties
    public int getProductID() {
        return productID.get();
    }

    public void setProductID(int productID) {
        this.productID.set(productID);
    }

    public IntegerProperty productIDProperty() {
        return productID;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public Category getCategory() {
        return category.get();
    }

    public void setCategory(Category category) {
        this.category.set(category);
    }

    public ObjectProperty<Category> categoryProperty() {
        return category;
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

    public int getStockQuantity() {
        return stockQuantity.get();
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity.set(stockQuantity);
    }

    public IntegerProperty stockQuantityProperty() {
        return stockQuantity;
    }

    public String getBarcode() {
        return barcode.get();
    }

    public void setBarcode(String barcode) {
        this.barcode.set(barcode);
    }

    public StringProperty barcodeProperty() {
        return barcode;
    }

    // Get Category ID from Category object
    public int getCategoryID() {
        return category.get() != null ? category.get().getCatID() : 0;
    }

    public void saveProduct() {
        productHandler.addProduct(this);
    }

}
