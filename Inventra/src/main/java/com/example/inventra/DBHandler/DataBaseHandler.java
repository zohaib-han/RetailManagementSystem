package com.example.inventra.DBHandler;

import java.sql.Connection;
import java.sql.ResultSet;

public abstract class DataBaseHandler {
    public abstract Connection connect();
    public abstract void disconnect(Connection connection);
    public abstract void executeQuery(String query, Object... parameters);
    public abstract ResultSet fetchData(String query, Object... parameters);

}
