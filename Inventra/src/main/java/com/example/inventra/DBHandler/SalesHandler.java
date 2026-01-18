package com.example.inventra.DBHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SalesHandler {
    SQLDatabase sqlDatabase = new SQLDatabase();
    String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};


    // 1. Get Total Sales for a Given Year
    public double getTotalSalesByYear(int year) {
        String query = """
            SELECT SUM(p.price * op.quantity) AS totalSales
            FROM OrderProducts op
            INNER JOIN Product p ON op.productID = p.productID
            INNER JOIN orderr o ON op.orderID = o.orderID
            WHERE YEAR(o.orderDate) = ? AND o.status = ?
        """;

        try (ResultSet rs = sqlDatabase.fetchData(query, year, "Completed")) {
            if (rs != null && rs.next()) {
                return rs.getDouble("totalSales");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // 2. Get Total Profit for a Given Year
    public double getTotalProfitByYear(int year) {
        String query = """
            SELECT SUM((p.price - p.purchasePrice) * op.quantity) AS totalProfit
            FROM OrderProducts op
            INNER JOIN Product p ON op.productID = p.productID
            INNER JOIN orderr o ON op.orderID = o.orderID
            WHERE YEAR(o.orderDate) = ? AND o.status = ?
        """;

        try (ResultSet rs = sqlDatabase.fetchData(query, year, "Completed")) {
            if (rs != null && rs.next()) {
                return rs.getDouble("totalProfit");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public Map<String, Double> getTotalSalesAndProfitByMonth(int year, int month) {
        String query = """
        SELECT SUM(p.price * op.quantity) AS totalSales,
               SUM((p.price - p.purchasePrice) * op.quantity) AS totalProfit
        FROM OrderProducts op
        INNER JOIN Product p ON op.productID = p.productID
        INNER JOIN orderr o ON op.orderID = o.orderID
        WHERE YEAR(o.orderDate) = ? AND MONTH(o.orderDate) = ? AND o.status = ?
    """;

        Map<String, Double> totals = new HashMap<>();
        try (ResultSet rs = sqlDatabase.fetchData(query, year, month, "Completed")) {
            if (rs != null && rs.next()) {
                totals.put("totalSales", rs.getDouble("totalSales"));
                totals.put("totalProfit", rs.getDouble("totalProfit"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totals;
    }


    // 3. Get Top Product for a Given Year
    public Map<String, Object> getTopProductByYear(int year) {
        String query = """
            SELECT p.name, SUM(op.quantity) AS totalQuantity, SUM(p.price * op.quantity) AS totalSales
            FROM OrderProducts op
            INNER JOIN Product p ON op.productID = p.productID
            INNER JOIN orderr o ON op.orderID = o.orderID
            WHERE YEAR(o.orderDate) = ? AND o.status = ?
            GROUP BY p.name
            ORDER BY totalQuantity DESC
            LIMIT 1
        """;

        Map<String, Object> result = new HashMap<>();
        try (ResultSet rs = sqlDatabase.fetchData(query, year, "Completed")) {
            if (rs != null && rs.next()) {
                result.put("productName", rs.getString("name"));
                result.put("totalQuantity", rs.getInt("totalQuantity"));
                result.put("totalSales", rs.getDouble("totalSales"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 4. Get Monthly Sales and Profit for a Given Year
    public Map<String, Map<String, Double>> getMonthlySalesAndProfit(int year) {
        String query = """
            SELECT MONTH(o.orderDate) AS month, 
                   SUM(p.price * op.quantity) AS totalSales, 
                   SUM((p.price - p.purchasePrice) * op.quantity) AS totalProfit
            FROM OrderProducts op
            INNER JOIN Product p ON op.productID = p.productID
            INNER JOIN orderr o ON op.orderID = o.orderID
            WHERE YEAR(o.orderDate) = ? AND o.status = ?
            GROUP BY MONTH(o.orderDate)
        """;

        Map<String, Map<String, Double>> monthlyData = new HashMap<>();
        try (ResultSet rs = sqlDatabase.fetchData(query, year, "Completed")) {
            while (rs != null && rs.next()) {
                String month = rs.getString("month");
                Map<String, Double> values = new HashMap<>();
                values.put("sales", rs.getDouble("totalSales"));
                values.put("profit", rs.getDouble("totalProfit"));
                monthlyData.put(months[Integer.parseInt(month) - 1], values);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return monthlyData;
    }

    public Map<String, Object> getTopProductByMonth(int year, int month) {
        String query = """
        SELECT p.name AS productName, SUM(op.quantity) AS totalQuantity, SUM(op.quantity * p.price) AS totalSales
        FROM OrderProducts op
        INNER JOIN Product p ON op.productID = p.productID
        INNER JOIN orderr o ON op.orderID = o.orderID
        WHERE YEAR(o.orderDate) = ? AND MONTH(o.orderDate) = ? AND o.status = ?
        GROUP BY p.name
        ORDER BY totalQuantity DESC
        LIMIT 1
    """;

        Map<String, Object> topProduct = new HashMap<>();
        try (ResultSet rs = sqlDatabase.fetchData(query, year, month, "Completed")) {
            if (rs != null && rs.next()) {
                topProduct.put("productName", rs.getString("productName"));
                topProduct.put("totalQuantity", rs.getInt("totalQuantity"));
                topProduct.put("totalSales", rs.getDouble("totalSales"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topProduct;
    }

    public Map<Integer, Map<String, Double>> fetchDailySalesAndProfit(int year, int month) {
        String query = """
        SELECT DAY(o.orderDate) AS day,
               SUM(p.price * op.quantity) AS totalSales,
               SUM((p.price - p.purchasePrice) * op.quantity) AS totalProfit
        FROM OrderProducts op
        INNER JOIN Product p ON op.productID = p.productID
        INNER JOIN orderr o ON op.orderID = o.orderID
        WHERE YEAR(o.orderDate) = ? AND MONTH(o.orderDate) = ? AND o.status = ?
        GROUP BY DAY(o.orderDate)
        ORDER BY day;
    """;

        Map<Integer, Map<String, Double>> dailyData = new HashMap<>();
        try (ResultSet rs = sqlDatabase.fetchData(query, year, month, "Completed")) {
            while (rs != null && rs.next()) {
                int day = rs.getInt("day");
                double sales = rs.getDouble("totalSales");
                double profit = rs.getDouble("totalProfit");

                Map<String, Double> dayData = new HashMap<>();
                dayData.put("sales", sales);
                dayData.put("profit", profit);

                dailyData.put(day, dayData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dailyData;
    }


    public Map<String, Double> getSalesByOrderType(int year) {
        String query = """
        SELECT o.orderType, SUM(p.price * op.quantity) AS totalSales
        FROM OrderProducts op
        INNER JOIN Product p ON op.productID = p.productID
        INNER JOIN orderr o ON op.orderID = o.orderID
        WHERE YEAR(o.orderDate) = ? AND o.status = ?
        GROUP BY o.orderType
    """;

        Map<String, Double> salesByOrderType = new HashMap<>();
        try (ResultSet rs = sqlDatabase.fetchData(query, year, "Completed")) {
            while (rs != null && rs.next()) {
                salesByOrderType.put(rs.getString("orderType"), rs.getDouble("totalSales"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salesByOrderType;
    }

    public Map<String, Double> getSalesByOrderType1(int year, int month) {
        String query = """
        SELECT o.orderType, SUM(p.price * op.quantity) AS totalSales
        FROM OrderProducts op
        INNER JOIN Product p ON op.productID = p.productID
        INNER JOIN orderr o ON op.orderID = o.orderID
        WHERE MONTH(o.orderDate) = ? AND o.status = ? AND YEAR(o.orderDate) = ?
        GROUP BY o.orderType
    """;

        Map<String, Double> salesByOrderType = new HashMap<>();
        try (ResultSet rs = sqlDatabase.fetchData(query, month, "Completed", year)) {
            while (rs != null && rs.next()) {
                salesByOrderType.put(rs.getString("orderType"), rs.getDouble("totalSales"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salesByOrderType;
    }

    public Map<String, Integer> getProductSalesByYear(int year) {
        String query = """
        SELECT p.name AS productName, SUM(op.quantity) AS totalQuantity
        FROM OrderProducts op
        INNER JOIN Product p ON op.productID = p.productID
        INNER JOIN orderr o ON op.orderID = o.orderID
        WHERE YEAR(o.orderDate) = ? AND o.status = ?
        GROUP BY p.name
        ORDER BY totalQuantity DESC
    """;

        Map<String, Integer> productSales = new HashMap<>();
        try (ResultSet rs = sqlDatabase.fetchData(query, year, "Completed")) {
            while (rs != null && rs.next()) {
                productSales.put(rs.getString("productName"), rs.getInt("totalQuantity"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productSales;
    }
    public Map<String, Integer> getProductSalesByMonth(int year, int month) {
        String query = """
        SELECT p.name AS productName, SUM(op.quantity) AS totalQuantity
        FROM OrderProducts op
        INNER JOIN Product p ON op.productID = p.productID
        INNER JOIN orderr o ON op.orderID = o.orderID
        WHERE YEAR(o.orderDate) = ? AND o.status = ? AND MONTH(o.orderDate) = ?
        GROUP BY p.name
        ORDER BY totalQuantity DESC
    """;

        Map<String, Integer> productSales = new HashMap<>();
        try (ResultSet rs = sqlDatabase.fetchData(query, year, "Completed", month)) {
            while (rs != null && rs.next()) {
                productSales.put(rs.getString("productName"), rs.getInt("totalQuantity"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productSales;
    }
}
