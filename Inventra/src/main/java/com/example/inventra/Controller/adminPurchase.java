package com.example.inventra.Controller;

import com.example.inventra.DBHandler.ProductHandler;
import com.example.inventra.OOP.Admin;
import com.example.inventra.OOP.Product;
import com.example.inventra.OOP.Supplier;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class adminPurchase {
    public static int adminID;
    public static String n;
    public Stage stage;
    public Parent root;
    public Scene scene;
    public Admin admin;

    @FXML
    private Text a;

    @FXML
    private Text name;

    @FXML
    private TextField timeBox;

    @FXML
    private TextField supplierName, barcode, quantity, purchasePrice, salePrice;

    @FXML
    private DatePicker supplyDate;

    @FXML
    private MenuButton productMenu;

    @FXML
    private TableView<Product> productTableView;

    @FXML
    private TableColumn<Product, String> productNameColumn, supplierNameColumn, barcodeColumn;

    @FXML
    private TableColumn<Product, Integer> quantityColumn;

    @FXML
    private TableColumn<Product, Float> purchasePriceColumn, totalAmountColumn;

    @FXML
    private TableColumn<Product, String> supplyDateColumn;

    @FXML
    private Text warning;

    @FXML
    private TextField totalText, itemCount, quantityCount, ttt, invoiceNumber;

    private float total = 0;
    private int quan = 0;
    private final ObservableList<Product> productList = FXCollections.observableArrayList();
    private List<Product> products;

    public void initialize() {
        admin = new Admin();
        invoiceNumber.setText(String.format("%d", admin.getInvoice() + 1));
        name.setText(n);
        setupRealTimeClock();
        setupProductMenu();
        setupTableView();
    }

    private void setupRealTimeClock() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalTime now = LocalTime.now();
            timeBox.setText(now.format(formatter));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void setupProductMenu() {
        products = admin.getAllProducts();
        for (Product product : products) {
            MenuItem menuItem = new MenuItem(product.getName());
            menuItem.setOnAction(e -> {
                productMenu.setText(product.getName());
                barcode.setText(product.getBarcode());
            });
            productMenu.getItems().add(menuItem);
        }

        barcode.textProperty().addListener((obs, oldVal, newVal) -> {
            Product product = admin.getProductByBarcode(newVal);
            if (product != null) {
                productMenu.setText(product.getName());
            }
        });
    }

    private void setupTableView() {
        productNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        supplierNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSupplier().getSupplyName()));
        barcodeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBarcode()));
        quantityColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSupplier().getQuantity()).asObject());
        purchasePriceColumn.setCellValueFactory(cellData -> new SimpleFloatProperty((float) cellData.getValue().getPurchasePrice()).asObject());
        totalAmountColumn.setCellValueFactory(cellData -> new SimpleFloatProperty(cellData.getValue().getSupplier().getTotalAmount()).asObject());
        supplyDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSupplier().getSupplyDate()));

        productTableView.setItems(productList);
    }

    @FXML
    private void addToTable() {
        if (supplierName.getText().isEmpty() || barcode.getText().isEmpty() || quantity.getText().isEmpty() ||
                purchasePrice.getText().isEmpty() || salePrice.getText().isEmpty() || supplyDate.getValue() == null) {
            warning.setText("Please fill all fields.");
            return;
        }

        try {
            int quantityValue = Integer.parseInt(quantity.getText());
            float purchasePriceValue = Float.parseFloat(purchasePrice.getText());

            // Create supplier object
            Supplier supplier = new Supplier();
            supplier.setSupplyName(supplierName.getText());
            supplier.setSupplyDate( supplyDate.getValue().toString());
            supplier.setQuantity(quantityValue);


            // Link supplier to product
            Product product = admin.getProductByBarcode(barcode.getText());
            if (product == null) {
                warning.setText("Product not found for the given barcode.");
                return;
            }

            product.setSupplier(supplier);
            product.setPrice(Double.parseDouble(salePrice.getText()));
            supplier.setTotalAmount(quantityValue * purchasePriceValue);
            quan += supplier.getQuantity();
            quantityCount.setText(String.format("%d", quan));
            productList.add(product);
            productTableView.refresh();
            itemCount.setText(String.format("%d", productList.size()));


            total += supplier.getTotalAmount();
            totalText.setText(String.format("%.2f", total));
            ttt.setText(String.format("%.2f", total));

            clearFields();
            warning.setText("");
        } catch (NumberFormatException e) {
            warning.setText("Invalid numeric input for Quantity or Price.");
        }
    }

    private void clearFields() {
        supplierName.clear();
        barcode.clear();
        quantity.clear();
        purchasePrice.clear();
        salePrice.clear();
        supplyDate.setValue(null);
        productMenu.setText("Select Product");
    }

    @FXML
    private void saveData() {
        if (productList.isEmpty()) {
            warning.setText("No data to save!");
            return;
        }

        for (Product product : productList) {
            Supplier supplier = product.getSupplier();

            // Save supplier to the database
            admin.saveSupplierBill(supplier);

            // Update product in the database
            product.setStockQuantity(product.getStockQuantity() + supplier.getQuantity());
            admin.updateProcduct(1, product);
        }

        productList.clear();
        productTableView.refresh();
        total = 0;
        totalText.setText("0.00");
        warning.setText("Data saved successfully!");
    }

    @FXML
    public void loadManageProducts(javafx.event.ActionEvent e) throws IOException {
        adminManageProductsController.adminID = adminID;
        adminManageProductsController.n = n;
        Parent b = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/inventra/ManageProducts.fxml")));
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(b, 1280, 800);
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
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(b, 1280, 800);
        stage.setTitle("Inventra Retail Management System 1.0");
        stage.setResizable(false);
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
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

    @FXML
    public void loadLogout(javafx.event.ActionEvent e) throws IOException {
        Parent b = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/inventra/Login.fxml")));
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(b, 600, 400);
        stage.setTitle("Inventra Retail Management System 1.0");
        stage.setResizable(false);
        stage.setMaximized(false);
        stage.setWidth(650);
        stage.setHeight(450);
        stage.setScene(scene);
        stage.show();
    }
}
