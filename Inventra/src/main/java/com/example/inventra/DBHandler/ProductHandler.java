package com.example.inventra.DBHandler;
import com.example.inventra.OOP.Category;
import com.example.inventra.OOP.Product;
import com.example.inventra.OOP.Supplier;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductHandler {
    SQLDatabase sqlDatabase = new SQLDatabase();

    public Category getCategory(int id) {
        String query = "SELECT * FROM Category WHERE catID = ?";
        try (ResultSet resultSet = sqlDatabase.fetchData(query, id)) {
            if (resultSet != null && resultSet.next()) {
                return new Category(
                        resultSet.getInt("catID"),
                        resultSet.getString("categoryName"),
                        resultSet.getInt("tax")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no category is found
    }

    public List<Category> getAllCategories() {
        String query = "SELECT * FROM Category";
        List<Category> categories = new ArrayList<>();

        try (ResultSet resultSet = sqlDatabase.fetchData(query)) {
            while (resultSet != null && resultSet.next()) {
                categories.add(new Category(
                        resultSet.getInt("catID"),
                        resultSet.getString("categoryName"),
                        resultSet.getInt("tax")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories; // Return the list of categories
    }


    // Fetch all products
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM Product";

        try (ResultSet resultSet = sqlDatabase.fetchData(query)) {
            while (resultSet != null && resultSet.next()) {
                products.add(new Product(
                        resultSet.getInt("productID"),
                        resultSet.getString("name"),
                        getCategory(resultSet.getInt("categoryID")),
                        resultSet.getDouble("price"),
                        resultSet.getInt("stockQuantity"),
                        resultSet.getString("barcode"),
                        resultSet.getDouble("purchasePrice")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public List<Product> getLowStockProducts(int stock) {
        List<Product> lowStockProducts = new ArrayList<>();
        String query = "SELECT * FROM Product WHERE stockQuantity < " + String.valueOf(stock);;

        try (ResultSet resultSet = sqlDatabase.fetchData(query)) {
            while (resultSet != null && resultSet.next()) {
                lowStockProducts.add(new Product(
                        resultSet.getInt("productID"),
                        resultSet.getString("name"),
                        getCategory(resultSet.getInt("categoryID")), // Call existing getCategory method
                        resultSet.getDouble("price"),
                        resultSet.getInt("stockQuantity"),
                        resultSet.getString("barcode"),
                        resultSet.getDouble("purchasePrice")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lowStockProducts;
    }


    // Add a new product
    public void addProduct(Product product) {
        String query = "INSERT INTO Product (name, categoryID, price, stockQuantity, barcode, purchasePrice, suppName) VALUES (?, ?, ?, ?, ?, ?, NULL)";
        sqlDatabase.executeQuery(query,
                product.getName(),
                product.getCategoryID(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getBarcode(),
                product.getPurchasePrice());
    }



    // Update a product's field
    public void updateProduct(Product product) {
        String query = "UPDATE Product SET name = ?, categoryID = ?, price = ?, stockQuantity = ?, barcode = ?, purchasePrice = ?, suppName = ? WHERE productID = ?";
        sqlDatabase.executeQuery(query,
                product.getName(),
                product.getCategoryID(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getBarcode(),
                product.getPurchasePrice(),
                (product.getSupplier() != null ? product.getSupplier().getSupplyName() : null), // Handle NULL for supplier
                product.getProductID());
    }



    public void updateProduct1_0(Product product) {
        String query = "UPDATE Product SET name = ?, categoryID = ?, price = ?, stockQuantity = ?, barcode = ?, purchasePrice = ?, suppName = ? WHERE productID = ?";
        sqlDatabase.executeQuery(query,
                product.getName(),
                product.getCategoryID(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getBarcode(),
                product.getPurchasePrice(),
                product.getSupplier().getSupplyName(),
                product.getProductID()
        );
    }

    // Delete a product by ID
    public void deleteProduct(int productID) {
        String query = "DELETE FROM Product WHERE productID = ?";
        sqlDatabase.executeQuery(query, productID);
    }

    // Find a product by ID
    public Product getProductById(int productID) {
        String query = "SELECT * FROM Product WHERE productID = ?";
        try (ResultSet resultSet = sqlDatabase.fetchData(query, productID)) {
            if (resultSet != null && resultSet.next()) {
                return new Product(
                        resultSet.getInt("productID"),
                        resultSet.getString("name"),
                        getCategory(resultSet.getInt("categoryID")),
                        resultSet.getDouble("price"),
                        resultSet.getInt("stockQuantity"),
                        resultSet.getString("barcode"),
                        resultSet.getDouble("purchasePrice")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Save supplier data to the Supplier table
    public void saveSupplier(Supplier supplier) {
        String query = "INSERT INTO Supplier (SupplyName, SupplyDate, Quantity) VALUES (?, ?, ?)";
        sqlDatabase.executeQuery(query,
                supplier.getSupplyName(),
                supplier.getSupplyDate(),
                supplier.getQuantity());
    }

    // Find a product by its barcode
    public Product getProductByBarcode(String barcode) {
        String query = "SELECT * FROM Product WHERE barcode = ?";
        try (ResultSet resultSet = sqlDatabase.fetchData(query, barcode)) {
            if (resultSet != null && resultSet.next()) {
                return new Product(
                        resultSet.getInt("productID"),
                        resultSet.getString("name"),
                        getCategory(resultSet.getInt("categoryID")),
                        resultSet.getDouble("price"),
                        resultSet.getInt("stockQuantity"),
                        resultSet.getString("barcode"),
                        resultSet.getDouble("purchasePrice")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getInvoice() {
        String query = "SELECT MAX(SupplyID) as m FROM Supplier";
        try (ResultSet resultSet = sqlDatabase.fetchData(query)) {
            if (resultSet != null && resultSet.next()) {
                return resultSet.getInt("m");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
