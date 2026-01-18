package com.example.inventra.OOP;

import com.example.inventra.DBHandler.CustomerBookingHandler;
import com.example.inventra.DBHandler.ProductHandler;
import com.example.inventra.DBHandler.UserAuthentication;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Customer extends  User{

    ProductHandler productHandler = new ProductHandler();
    CustomerBookingHandler customerBookingHandler = new CustomerBookingHandler();

    public Customer(int id, String fetchedUsername, String password, String email, String phoneNumber) {
        super(id, "Customer", email, password, fetchedUsername, phoneNumber);
    }

    public Customer(){
        super();
    }

    @Override
    public int authenticator(String name, String pswrd){
        UserAuthentication user = new UserAuthentication();
        this.setID(user.userAuthentication(name, pswrd, "Customer"));
        return this.getID();
    }

    public List<Product> getAllAvaliableProducts(){
        List<Product> all = productHandler.getAllProducts();
        List<Product> available = new ArrayList<>();
        for (Product p: all){
            if (p.getStockQuantity() >= 1){
                available.add(p);
            }
        }
        return available;
    }

    public boolean processOrder(List<OrderProducts> orderProductList, String customerName, String orderType) {
        int orderId = customerBookingHandler.addOrder(this.getID(), customerName, orderType, LocalDateTime.now());
        if (orderId <= 0) {
            return false;
        }

        for (OrderProducts op : orderProductList) {
            customerBookingHandler.addOrderProduct(orderId, op.getProduct().getProductID(), op.getQuantity());
        }
        //bookingHandler.addInvoice(orderId);
        return true;
    }

    public int getOrderNumber(){
        return customerBookingHandler.getOrderNumber();
    }
}
