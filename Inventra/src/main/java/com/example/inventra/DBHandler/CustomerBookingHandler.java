package com.example.inventra.DBHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class CustomerBookingHandler {
    SQLDatabase sqlDatabase = new SQLDatabase();

    public int addOrder(int customerId, String customerName, String orderType, LocalDateTime orderDate) {
        String query = """
                INSERT INTO orderr (cashierID, orderDate, status, customerName, orderType)
                VALUES (?, ?, ?, ?, ?)
                """;

        sqlDatabase.executeQuery(query, null, orderDate, "Pending", customerName, orderType);

        // Get the last inserted order ID
        String lastIdQuery = "SELECT MAX(OrderID) as id from orderr";
        try (ResultSet rs = sqlDatabase.fetchData(lastIdQuery)) {
            if (rs != null && rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getOrderNumber(){
        String lastIdQuery = "SELECT MAX(OrderID) as id from orderr";
        try (ResultSet rs = sqlDatabase.fetchData(lastIdQuery)) {
            if (rs != null && rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addOrderProduct(int orderId, int productId, int quantity) {
        String query = """
                INSERT INTO OrderProducts (orderID, productID, quantity)
                VALUES (?, ?, ?)
                """;
        sqlDatabase.executeQuery(query, orderId, productId, quantity);
    }

    public void addInvoice(int orderId) {
        String query = """
                INSERT INTO Invoice (orderID, totalAmount)
                SELECT orderID, SUM(p.price * op.quantity)
                FROM OrderProducts op
                INNER JOIN Product p ON op.productID = p.productID
                WHERE op.orderID = ?
                GROUP BY op.orderID
                """;
        sqlDatabase.executeQuery(query, orderId);
    }
}
