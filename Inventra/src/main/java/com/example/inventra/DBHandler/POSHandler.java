package com.example.inventra.DBHandler;

import com.example.inventra.OOP.Category;
import com.example.inventra.OOP.Invoice;
import com.example.inventra.OOP.Order;
import com.example.inventra.OOP.OrderProducts;
import com.example.inventra.OOP.Product;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class POSHandler {
    SQLDatabase sqlDatabase = new SQLDatabase();
    Category c =new Category(1,"hel",0);
    // Save an order and return the generated order ID
    public int saveOrder(Order order) {
        String query = "INSERT INTO orderr (cashierID, orderDate, status, customerNumber, customerName, orderType) VALUES (?, ?, ?, ?, ?, ?)";
        int generatedOrderId = -1;

        try {
            sqlDatabase.executeQuery(query,
                    order.getCashierID(),
                    java.sql.Timestamp.valueOf(order.getOrderDate()),
                    order.getStatus(),
                    order.getCustomerNumber(),
                    order.getCustomerName(),
                    order.getOrderType());

            String getLastIdQuery = "SELECT max(orderID) from orderr"; // Fetch the last inserted ID
            ResultSet resultSet = sqlDatabase.fetchData(getLastIdQuery);

            if (resultSet != null && resultSet.next()) {
                generatedOrderId = resultSet.getInt("max(orderID)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return generatedOrderId;
    }
    public int getOrderIDfromInvoice(int inv) {
        String query = "select Invoice.orderID from Invoice where invoiceID =?";
        int generatedOrderId = -1;

        try {
            ResultSet resultSet = sqlDatabase.fetchData(query,inv);

            if (resultSet != null && resultSet.next()) {
                generatedOrderId = resultSet.getInt("orderID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return generatedOrderId;
    }
    public int send_invno(){
        int generatedInvoiceId=-1;
        try{

            String getLastIdQuery = "SELECT max(invoiceID) from Invoice"; // Fetch the last inserted ID
            ResultSet resultSet = sqlDatabase.fetchData(getLastIdQuery);
            if (resultSet != null && resultSet.next()) {
                generatedInvoiceId = resultSet.getInt("max(invoiceID)");
            }

        }catch (SQLException e) {
            e.printStackTrace();
        }
        return generatedInvoiceId+1;
    }
    public void deleteorderProduct(int orderid){
        String query2=" select * from OrderProducts where orderID= ?";
        String query3="UPDATE Product SET stockQuantity = stockQuantity + ? where productID = ?";
            try (ResultSet resultSet = sqlDatabase.fetchData(query2, orderid);) {
                while (resultSet != null && resultSet.next()) {
                sqlDatabase.executeQuery(query3,resultSet.getInt("stockQuantity"),resultSet.getInt("productID"));
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        String query = "DELETE from OrderProducts where orderID =?";

        try {
            sqlDatabase.executeQuery(query,orderid);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void updateInvoice(int invoiceID, double amount){
        String query = "UPDATE Invoice SET amount = ? where invoiceID = ?";
        try {
            sqlDatabase.executeQuery(query,amount,invoiceID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Save an invoice and return the generated invoice ID
    public int saveInvoice(Invoice invoice) {
        String query = "INSERT INTO Invoice (amount, date, discount, orderID) VALUES (?, ?, ?, ?)";
        int generatedInvoiceId = -1;

        try {
            sqlDatabase.executeQuery(query,
                    invoice.getAmount(),
                    java.sql.Timestamp.valueOf(invoice.getDate()),
                    invoice.getDiscount(),
                    invoice.getOrderID());

            String getLastIdQuery = "SELECT max(invoiceID) from Invoice"; // Fetch the last inserted ID
            ResultSet resultSet = sqlDatabase.fetchData(getLastIdQuery);

            if (resultSet != null && resultSet.next()) {
                generatedInvoiceId = resultSet.getInt("max(invoiceID)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return generatedInvoiceId;
    }
    public List<Order> getOrdersByType(String orderType) {
        String query = "SELECT * FROM orderr WHERE orderType = ? AND status = ?";
        List<Order> orders = new ArrayList<>();

        try (ResultSet resultSet = sqlDatabase.fetchData(query, orderType, "Pending")) {
            while (resultSet != null && resultSet.next()) {
                Order order = new Order(
                        resultSet.getInt("orderID"),
                        resultSet.getInt("cashierID"),
                        resultSet.getTimestamp("orderDate").toLocalDateTime(),
                        resultSet.getString("status"),
                        resultSet.getString("customerNumber"),
                        resultSet.getString("customerName"),
                        resultSet.getString("orderType")
                );
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }
    // Save products in an order
    public void saveOrderProducts(int orderId, List<OrderProducts> orderProducts) {
        String query = "INSERT INTO OrderProducts (orderID, productID, quantity) VALUES (?, ?, ?)";
        String query2 = "UPDATE Product SET stockQuantity = stockQuantity - ? where productID = ?";
        for (OrderProducts orderProduct : orderProducts) {
            sqlDatabase.executeQuery(query,
                    orderId,
                    orderProduct.getProductID(),
                    orderProduct.getQuantity());
            sqlDatabase.executeQuery(query2, orderProduct.getQuantity(), orderProduct.getProductID());
        }

    }

    // Retrieve all orders
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM orderr";

        try {
            ResultSet resultSet = sqlDatabase.fetchData(query);

            while (resultSet != null && resultSet.next()) {
                orders.add(new Order(
                        resultSet.getInt("orderID"),
                        resultSet.getInt("cashierID"),
                        resultSet.getTimestamp("orderDate").toLocalDateTime(),
                        resultSet.getString("status"),
                        resultSet.getString("customerNumber"),
                        resultSet.getString("customerName"),
                        resultSet.getString("orderType")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    // Retrieve all invoices
    public List<Invoice> getAllInvoices() {
        List<Invoice> invoices = new ArrayList<>();
        String query = "SELECT * FROM Invoice";

        try {
            ResultSet resultSet = sqlDatabase.fetchData(query);

            while (resultSet != null && resultSet.next()) {
                invoices.add(new Invoice(
                        resultSet.getInt("invoiceID"),
                        resultSet.getDouble("amount"),
                        resultSet.getTimestamp("date").toLocalDateTime(),
                        resultSet.getDouble("discount"),
                        resultSet.getInt("orderID")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return invoices;
    }

    // Retrieve all products in an order
    public List<OrderProducts> getOrderProducts(int orderId) {
        List<OrderProducts> orderProducts = new ArrayList<>();
        String query = "SELECT * FROM OrderProducts WHERE orderID = ?";

        try {
            ResultSet resultSet = sqlDatabase.fetchData(query, orderId);

            while (resultSet != null && resultSet.next()) {
                orderProducts.add(new OrderProducts(
                        resultSet.getInt("orderID"),
                        resultSet.getInt("productID"),
                        resultSet.getInt("quantity")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orderProducts;
    }

    public void acceptOrder(int id){
        String query = "UPDATE orderr SET status = ? WHERE orderID = ? ";
        sqlDatabase.executeQuery(query, "Completed", id);
    }

    public String generateInvoiceData(int specificOrderID) {
        StringBuilder sb = new StringBuilder();
        String query = """
        SELECT 
            i.orderID, 
            o.orderDate, 
            i.amount, 
            o.customerName, 
            p.name AS productName, 
            op.quantity,
            (p.price * op.quantity) AS productTotal
        FROM Invoice i
        INNER JOIN Orderr o ON i.orderID = o.orderID
        INNER JOIN OrderProducts op ON o.orderID = op.orderID
        INNER JOIN Product p ON op.productID = p.productID
        WHERE i.OrderID = ?;
    """;

        try (ResultSet rs = sqlDatabase.fetchData(query, specificOrderID)) {
            // Header
            sb.append("Invoice Details:\n");
            sb.append("----------------------------------------------------\n");

            double totalAmount = 0;
            while (rs != null && rs.next()) {
                sb.append("Order ID: ").append(rs.getInt("OrderID")).append("\n");
                sb.append("Order Date: ").append(rs.getDate("orderDate")).append("\n");
                sb.append("Customer Name: ").append(rs.getString("customerName")).append("\n");
                sb.append("\nProducts:\n");

                // Product details
                sb.append("Product Name: ").append(rs.getString("productName")).append("\n");
                sb.append("Quantity: ").append(rs.getInt("quantity")).append("\n");
                sb.append("Product Total: ").append(rs.getDouble("productTotal")).append("\n");
                sb.append("----------------------------------------------------\n");

                totalAmount += rs.getDouble("productTotal");
            }

            sb.append("\nTotal Amount: ").append(totalAmount).append("\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    public List<OrderProducts> getProductsByInvoiceNumber(int invoiceNumber) {
        String query = "SELECT OrderProducts.orderID, OrderProducts.productID, OrderProducts.quantity, " +
                "Product.name, Product.barcode, Product.price " +
                "FROM OrderProducts " +
                "JOIN Product ON OrderProducts.productID = Product.productID " +
                "JOIN Invoice ON OrderProducts.orderID = Invoice.orderID " +
                "WHERE Invoice.invoiceID = ?";
        List<OrderProducts> products = new ArrayList<>();

        try (ResultSet resultSet = sqlDatabase.fetchData(query, invoiceNumber);) {
            while (resultSet != null && resultSet.next()) {
                OrderProducts product = new OrderProducts(
                        resultSet.getInt("orderID"),
                        resultSet.getInt("productID"),
                        resultSet.getInt("quantity")
                );
                Product prod =new Product();
                // Set additional properties for TableView compatibility
                prod.setName(resultSet.getString("name"));
                prod.setBarcode(resultSet.getString("barcode"));
                prod.setPrice(resultSet.getDouble("price"));
                prod.setPrice(resultSet.getDouble("price") * resultSet.getInt("quantity"));
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }
public Float getdisc(int invoice ){
        Float i=0.0f;
        String query ="select discount from Invoice where Invoice.invoiceID=?";
    try (ResultSet resultSet = sqlDatabase.fetchData(query, invoice);) {
        while (resultSet != null && resultSet.next()) {
            i=resultSet.getFloat("discount");
        }
    }

    catch (Exception e){


    }
    return i;
}
    public List<OrderProducts> getOrderProductsByInvoiceNumber(String invoiceNumber) {
        String query = "SELECT op.* FROM OrderProducts op " +
                "JOIN orderr o ON op.orderID = o.orderID " +
                "JOIN Invoice i ON o.orderID = i.orderID " +
                "WHERE i.invoiceID = ?";
        List<OrderProducts> orderProducts = new ArrayList<>();

        try (ResultSet resultSet = sqlDatabase.fetchData(query, invoiceNumber)) {
            while (resultSet != null && resultSet.next()) {
                orderProducts.add(new OrderProducts(
                        resultSet.getInt("orderID"),
                        resultSet.getInt("productID"),
                        resultSet.getInt("quantity")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderProducts;
    }

    public Product getProductById(int productID) {
        String query = "SELECT * FROM Product WHERE productID = ?";
        try (ResultSet resultSet = sqlDatabase.fetchData(query, productID)) {
            if (resultSet != null && resultSet.next()) {
                return new Product(
                        resultSet.getInt("productID"),
                        resultSet.getString("name"),
                        c, // Assume category fetching is handled elsewhere
                        resultSet.getDouble("price"),
                        resultSet.getInt("stockQuantity"),
                        resultSet.getString("barcode")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
