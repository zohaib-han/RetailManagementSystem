package com.example.inventra.Controller;

import com.example.inventra.DBHandler.ProductHandler;
import com.example.inventra.OOP.Admin;
import com.example.inventra.OOP.Category;
import com.example.inventra.OOP.Product;
import com.example.inventra.OOP.Supplier;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class adminManageProductsController {
    public static int adminID;
    public static String n;
    public Stage stage;
    public Parent root;
    public Scene scene;
    private List<Category> categories;
    private Admin admin;

    @FXML
    private TableView<Product> productTableView;

    @FXML
    private TableColumn<Product, Integer> productIDColumn;

    @FXML
    private TableColumn<Product, String> nameColumn;

    @FXML
    private TableColumn<Product, String> categoryColumn;

    @FXML
    private TableColumn<Product, Double> priceColumn;

    @FXML
    private TableColumn<Product, Integer> stockQuantityColumn;

    @FXML
    private TableColumn<Product, String> barcodeColumn;

    @FXML
    private Text name;

    @FXML
    private Button addButton;

    @FXML
    private TextField barcode, quantity, price, prodName, searchBar;

    @FXML
    public ComboBox<String> cate;

    @FXML
    private Text warning;

    @FXML
    private ToggleButton lowQuantity;

    @FXML
    private VBox notificationPane;

    @FXML
    private ImageView pngWarning;

    private boolean flag = false;

    private final ObservableList<Product> productList = FXCollections.observableArrayList();

    public Category getCategory(){
        for (Category category : categories){
            if (category.getCategoryName().equals(cate.getValue())){
                return category;
            }
        }
        return null;
    }

    public void fillingCategory(){

        categories = admin.getCategories();
        for (Category category : categories){
            cate.getItems().add(category.getCategoryName());
        }
    }

    public void initialize() {
        admin = new Admin();
        fillingCategory();
        name.setText(n);

        // Load product data directly from database
        loadProductData();

        updateNotifications();
        pngWarning.setVisible(flag);

        // Set up table columns
        productIDColumn.setCellValueFactory(cellData -> cellData.getValue().productIDProperty().asObject());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        categoryColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCategory().getCategoryName()));
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        stockQuantityColumn.setCellValueFactory(cellData -> cellData.getValue().stockQuantityProperty().asObject());
        barcodeColumn.setCellValueFactory(cellData -> cellData.getValue().barcodeProperty());

        // Make columns editable
        makeColumnsEditable();

        // Add right-click delete functionality
        productTableView.setRowFactory(tv -> {
            TableRow<Product> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(event -> handleDelete(row.getItem()));
            contextMenu.getItems().add(deleteItem);

            row.setContextMenu(contextMenu);
            return row;
        });
    }

    private void updateNotifications() {
        notificationPane.getChildren().clear(); // Clear existing notifications

        List<Product> lowStockProducts = admin.getLowStcokProducts(2);

        if (!lowStockProducts.isEmpty()) {
            flag = true;
            for (Product product : lowStockProducts) {
                HBox notification = new HBox();
                notification.setStyle(
                        "-fx-background-color: #FFDA22; " +
                                "-fx-padding: 10; " +
                                "-fx-spacing: 15; " +
                                "-fx-font-weight: bold; " +
                                "-fx-text-fill: red;"
                );

                Label message = new Label("Low stock Alert -> " + product.getName() + " ( Stock : " + product.getStockQuantity() + " )");
                Button closeButton = new Button("X");

                closeButton.setOnAction(event -> notificationPane.getChildren().remove(notification)); // Remove notification on click

                notification.getChildren().addAll(message, closeButton);
                notificationPane.getChildren().add(notification);
            }
        }
        else
            flag = false;
    }

    private void loadProductData() {
        productList.clear();
        productList.addAll(admin.getAllProducts()); // Direct database call for table initialization
        productTableView.setItems(productList);
    }

    private void makeColumnsEditable() {
        productTableView.setEditable(true);

        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(event -> {
            Product product = event.getRowValue();
            product.setName(event.getNewValue());
            admin.updateProcduct(0, product);
            updateNotifications();
        });

        priceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        priceColumn.setOnEditCommit(event -> {
            Product product = event.getRowValue();
            product.setPrice(event.getNewValue());
            admin.updateProcduct(0, product);
            updateNotifications();
        });

        stockQuantityColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        stockQuantityColumn.setOnEditCommit(event -> {
            Product product = event.getRowValue();
            product.setStockQuantity(event.getNewValue());
            admin.updateProcduct(0, product);
            updateNotifications();
        });

        barcodeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        barcodeColumn.setOnEditCommit(event -> {
            Product product = event.getRowValue();
            product.setBarcode(event.getNewValue());
            admin.updateProcduct(0, product);
            updateNotifications();
        });
    }

    private void handleDelete(Product product) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Are you sure you want to delete this product?");
        alert.setContentText("Product: " + product.getName());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            admin.deleteProduct(product); // Delegate delete logic to Product class
            productList.remove(product); // Remove from table
        }
    }

    public boolean checkBarcode(String bar){
        for (Product p: productList){
            if (p.getBarcode().equals(bar))
                return false;
        }
        return true;
    }

    @FXML
    private void addProduct() {
        String name = prodName.getText();
        String b = barcode.getText();
        if (!(cate.getValue() == null) && !prodName.getText().isEmpty() && !barcode.getText().isEmpty() ){
            if (!checkBarcode(b)){
                warning.setText("Barcode AlreadyAssigned to Product!");
                return;
            }
            Product newProduct = new Product();
            newProduct.setName(name);
            newProduct.setCategory(getCategory()); // Set category
            newProduct.setPrice(0);
            newProduct.setStockQuantity(0);
            newProduct.setBarcode(b);
            newProduct.setPurchasePrice(0.00);
            newProduct.setSupplier(null); // Supplier not required at add time

            try {
                admin.addProduct(newProduct);
                loadProductData(); // Refresh product list
                barcode.setText("");
                prodName.setText("");
                warning.setText("Product added successfully!");
            } catch (Exception e) {
                warning.setText("Error adding product: " + e.getMessage());
            }
        }
        else {
            warning.setText("Invalid Information!");
        }
    }


    @FXML
    private void searchProducts() {
        String searchText = searchBar.getText().toLowerCase();

        if (searchText.isEmpty()) {
            // Reload all products if search bar is cleared
            loadProductData();
        } else {
            ObservableList<Product> filteredList = FXCollections.observableArrayList();

            for (Product product : productList) {
                if (product.getName().toLowerCase().contains(searchText) ||
                        product.getBarcode().toLowerCase().contains(searchText) ||
                        product.getCategory().getCategoryName().toLowerCase().contains(searchText)) {
                    filteredList.add(product);
                }
            }

            productTableView.setItems(filteredList); // Display filtered products
        }
    }

    @FXML
    private void loadLowQuantityStock() {
        productList.clear();

        if (lowQuantity.isSelected()) {
            List<Product> lowStockProducts = admin.getLowStcokProducts(4);
            productList.addAll(lowStockProducts);
            warning.setText("Showing products with low stock!"); // Show feedback
        } else {
            loadProductData();
        }

        productTableView.setItems(productList);
    }

    @FXML
    public void loadManageProducts(javafx.event.ActionEvent e) throws IOException {
        Parent b = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/inventra/ManageProducts.fxml")));
        stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(b, 1280, 800);
        //scene.getStylesheets().add(getClass().getResource("POS.css").toExternalForm());
        stage.setTitle("Inventra Retail Management System 1.0");
        stage.setResizable(false);
        stage.setMaximized(true);
        stage.setFullScreen(stage.isFullScreen());
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void loadManageUsers(javafx.event.ActionEvent e) throws IOException {
        adminManageUser.adminID = adminID;
        adminManageUser.n = n;
        Parent b = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/inventra/ManageUsers.fxml")));
        stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(b, 1280, 800);
        //scene.getStylesheets().add(getClass().getResource("ManageUsers.css").toExternalForm());
        stage.setTitle("Inventra Retail Management System 1.0");
        stage.setResizable(false);
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void loadPurchase(javafx.event.ActionEvent e) throws IOException {
        adminPurchase.adminID = adminID;
        adminPurchase.n = n;
        Parent b = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/inventra/Purchase.fxml")));
        stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(b, 1280, 800);
        //scene.getStylesheets().add(getClass().getResource("Purchase.css").toExternalForm());
        stage.setTitle("Inventra Retail Management System 1.0");
        stage.setResizable(false);
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void loadLogout(javafx.event.ActionEvent e) throws IOException{
        Parent b = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/inventra/Login.fxml")));
        stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(b, 600, 400);
        //scene.getStylesheets().add(getClass().getResource("Login.css").toExternalForm());
        stage.setTitle("Inventra Retail Management System 1.0");
        stage.setResizable(false);
        stage.setMaximized(false);
        stage.setWidth(650);
        stage.setHeight(450);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void loadInventoy(){

    }

    @FXML
    public void loadSalesReport(javafx.event.ActionEvent e) throws IOException{
        adminSalesReportController.adminID = adminID;
        adminSalesReportController.n = n;
        Parent b = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/inventra/SalesReport.fxml")));
        stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(b, 1280, 800);
        //scene.getStylesheets().add(getClass().getResource("Purchase.css").toExternalForm());
        stage.setTitle("Inventra Retail Management System 1.0");
        stage.setResizable(false);
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void loadDailySales(javafx.event.ActionEvent e) throws IOException{
            adminDailySalesReportController.adminID = adminID;
            adminDailySalesReportController.n = n;
            Parent b = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/inventra/DailySalesReport.fxml")));
            stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
            scene = new Scene(b, 1280, 800);
            //scene.getStylesheets().add(getClass().getResource("Purchase.css").toExternalForm());
            stage.setTitle("Inventra Retail Management System 1.0");
            stage.setResizable(false);
            stage.setMaximized(true);
            stage.setScene(scene);
            stage.show();
    }
}


