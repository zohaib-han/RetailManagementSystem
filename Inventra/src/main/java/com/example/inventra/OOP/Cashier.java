package com.example.inventra.OOP;

import com.example.inventra.DBHandler.POSHandler;
import com.example.inventra.DBHandler.UserAuthentication;

public class Cashier extends User{

    private POSHandler posHandler = new POSHandler();

    public Cashier(int id, String fetchedUsername, String password, String email, String phoneNumber) {
        super(id, "Cashier", email, password, fetchedUsername, phoneNumber);
    }

    public Cashier(){
        super();
    }

    @Override
    public int authenticator(String name, String pswrd){
        UserAuthentication user = new UserAuthentication();
        this.setID(user.userAuthentication(name, pswrd, "Cashier"));
        return this.getID();

    }

    public String generateInvoiceData(int id){
        return posHandler.generateInvoiceData(id);
    }

    public void updateOrderStatus(int orderID){
        posHandler.acceptOrder(orderID);
    }

}
