package com.example.inventra.DBHandler;

import com.example.inventra.OOP.Cashier;
import com.example.inventra.OOP.Customer;
import com.example.inventra.OOP.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserHandler {
    SQLDatabase sqlDatabase = new SQLDatabase();

    public List<User> getAllUsers(String type) {
        List<User> users = new ArrayList<>();

        // Query Cashier table
        if (type.equals("Cashier")) {
            String cashierQuery = "SELECT * FROM Cashier";
            try (ResultSet resultSet = sqlDatabase.fetchData(cashierQuery)) {
                while (resultSet != null && resultSet.next()) {
                    int id = resultSet.getInt("ID");
                    String username = resultSet.getString("name");
                    String password = resultSet.getString("password");
                    String email = resultSet.getString("email");
                    String phoneNumber = resultSet.getString("phone_num");

                    // Create a Cashier object and add it to the list
                    users.add(new Cashier(id, username, password, email, phoneNumber));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            // Query Customer table
            String customerQuery = "SELECT * FROM Customer";
            try (ResultSet resultSet = sqlDatabase.fetchData(customerQuery)) {
                while (resultSet != null && resultSet.next()) {
                    int id = resultSet.getInt("ID");
                    String username = resultSet.getString("name");
                    String password = resultSet.getString("password");
                    String email = resultSet.getString("email");
                    String phoneNumber = resultSet.getString("phone_num");

                    // Create a Customer object and add it to the list
                    users.add(new Customer(id, username, password, email, phoneNumber));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Return the combined list of users
        return users;
    }


    public void addUser(User user) {
        String query = "INSERT INTO " +  user.getType() + " (name, password, email, phone_num) VALUES (?, ?, ?, ?)";
        sqlDatabase.executeQuery(query, user.getUsername(), user.getPassword(), user.getEmail(), user.getPhoneNumber());
    }

    public void updateUser(User user) {
        String query = "UPDATE " +  user.getType() + " SET email = ?, name = ?, password = ? WHERE ID = ?";
        sqlDatabase.executeQuery(query, user.getEmail(), user.getUsername(), user.getPassword(), user.getID());
    }

    public void deleteUser(User user) {
        String query = "DELETE FROM " +  user.getType() + " WHERE ID = ?";
        sqlDatabase.executeQuery(query, user.getID());
    }
}
