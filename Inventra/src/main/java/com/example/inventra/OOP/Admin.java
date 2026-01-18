package com.example.inventra.OOP;

import com.example.inventra.DBHandler.ProductHandler;
import com.example.inventra.DBHandler.SalesHandler;
import com.example.inventra.DBHandler.UserAuthentication;

import java.util.List;
import java.util.Map;

public class Admin extends User{
    private SalesHandler salesHandler;
    private ProductHandler productHandler;

    public Admin(int id, String fetchedUsername, String password, String email, String phoneNumber) {
        super(id, "Adminn", email, password, fetchedUsername, phoneNumber);
        this.salesHandler = new SalesHandler(); // Initialize SalesHandler
        this.productHandler = new ProductHandler();
    }

    public Admin(){
        super();
        this.salesHandler = new SalesHandler(); // Initialize SalesHandler
        this.productHandler = new ProductHandler();
    }

    @Override
    public int authenticator(String name, String pswrd){
        UserAuthentication user = new UserAuthentication();
        this.setID(user.userAuthentication(name, pswrd, "Adminn"));
        return this.getID();
    }

    public double fetchTotalSalesByYear(int year) {
        return salesHandler.getTotalSalesByYear(year);
    }

    public double fetchTotalProfitByYear(int year) {
        return salesHandler.getTotalProfitByYear(year);
    }

    public Map<String, Object> fetchTopProductByYear(int year) {
        return salesHandler.getTopProductByYear(year);
    }

    public Map<String, Map<String, Double>> fetchMonthlySalesAndProfit(int year) {
        return salesHandler.getMonthlySalesAndProfit(year);
    }

    public Map<String, Double> fetchSalesByOrderType(int year) {
        return salesHandler.getSalesByOrderType(year);
    }

    public Map<String, Double> fetchSalesByOrderType1(int year, int month) {
        return salesHandler.getSalesByOrderType1(year, month);
    }

    public Map<String, Integer> fetchProductSalesByYear(int year) {
        return salesHandler.getProductSalesByYear(year);
    }

    public Map<String, Integer> fetchProductSalesByMonth(int year, int month) {
        return salesHandler.getProductSalesByMonth(year, month);
    }

    public Map<String, Double> fetchTotalSalesAndProfitByMonth(int year, int month) {
        return salesHandler.getTotalSalesAndProfitByMonth(year, month);
    }

    public Map<String, Object> fetchTopProductByMonth(int year, int month) {
        return salesHandler.getTopProductByMonth(year, month);
    }

    public Map<Integer, Map<String, Double>>fetchDailySalesAndProfit(int year, int month){
        return salesHandler.fetchDailySalesAndProfit(year,month);
    }

    public List<Category> getCategories(){
        return productHandler.getAllCategories();
    }

    public List<Product> getAllProducts(){
        return productHandler.getAllProducts();
    }

    public List<Product> getLowStcokProducts(int lowLimit){
        return productHandler.getLowStockProducts(lowLimit);
    }

    public void updateProcduct(int i , Product product){
        if (i == 0)
            productHandler.updateProduct(product);
        else
            productHandler.updateProduct1_0(product);
    }

    public void deleteProduct(Product p){
        productHandler.deleteProduct(p.getProductID());
    }

    public void addProduct(Product p){
        productHandler.addProduct(p);
    }

    public Product getProductByBarcode(String b){
        return productHandler.getProductByBarcode(b);
    }

    public void saveSupplierBill (Supplier supplier){
        productHandler.saveSupplier(supplier);
    }

    public int getInvoice(){
        return productHandler.getInvoice();
    }

}


