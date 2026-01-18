package com.example.inventra.DBHandler;

import java.sql.*;

public class SQLDatabase extends DataBaseHandler {

    private static final String URL = "jdbc:mysql://localhost:3306/retail_management";
    private static final String USER = "root";
    private static final String PASSWORD = "12345678";

    @Override
    public Connection connect() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void disconnect(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void executeQuery(String query, Object... parameters) {
        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (int i = 0; i < parameters.length; i++) {
                preparedStatement.setObject(i + 1, parameters[i]);
            }

            preparedStatement.executeUpdate(); // Use executeUpdate for insert, update, delete queries
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public ResultSet fetchData(String query, Object... parameters) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connect();
            preparedStatement = connection.prepareStatement(query);

            for (int i = 0; i < parameters.length; i++) {
                preparedStatement.setObject(i + 1, parameters[i]);
            }
            resultSet = preparedStatement.executeQuery();

            return resultSet;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
