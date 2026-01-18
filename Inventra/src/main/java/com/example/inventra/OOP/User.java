package com.example.inventra.OOP;

import com.example.inventra.DBHandler.UserHandler;
import javafx.beans.property.*;

import java.util.List;

public abstract class User {

    private final IntegerProperty id;
    private final StringProperty username;
    private final StringProperty password;
    private final StringProperty email;
    private final StringProperty phoneNumber; // Add phoneNumber field
    private final StringProperty type;
    private static final UserHandler userHandler = new UserHandler();

    public User(int id, String type, String email, String password, String username, String phoneNumber) {
        this.id = new SimpleIntegerProperty(id);
        this.type = new SimpleStringProperty(type);
        this.email = new SimpleStringProperty(email);
        this.password = new SimpleStringProperty(password);
        this.username = new SimpleStringProperty(username);
        this.phoneNumber = new SimpleStringProperty(phoneNumber); // Initialize phoneNumber
    }

    public User() {
        this(0, "", "", "", "", "");
    }

    public int getID() {
        return id.get();
    }

    public void setID(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getUsername() {
        return username.get();
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public StringProperty emailProperty() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber.get();
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.set(phoneNumber);
    }

    public StringProperty phoneNumberProperty() {
        return phoneNumber;
    }

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public String getType() {
        return type.get();
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public static List<User> getAllUsers(String type) {
        return userHandler.getAllUsers(type);
    }

    public void add() {
        userHandler.addUser(this);
    }

    public void update() {
        userHandler.updateUser(this);
    }

    public void delete() {
        userHandler.deleteUser(this);
    }

    public abstract int authenticator(String name, String pswrd);

}
