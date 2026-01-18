package com.example.inventra.DBHandler;

import com.example.inventra.OOP.Admin;
import com.example.inventra.OOP.Cashier;
import com.example.inventra.OOP.Customer;
import com.example.inventra.OOP.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserAuthentication {
    SQLDatabase sqlDatabase = new SQLDatabase();

    public int userAuthentication(String username, String pswrd, String type) {
        String query = "SELECT * FROM " + type + " WHERE name = ? AND password = ?";
        ResultSet resultSet = sqlDatabase.fetchData(query, username, pswrd);

        try {
            if (resultSet != null) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("ID");
                    return id;
                }
                else {
                    return 0;
                }
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            // Ensure you close the resultSet and other resources here
            try {
                if (resultSet != null && !resultSet.isClosed()) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
