package com.example.inventra.Controller;

import com.example.inventra.OOP.Customer;
import com.example.inventra.OOP.Order;
import com.example.inventra.OOP.OrderProducts;
import com.example.inventra.OOP.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class customerTakeawayController {
    public static int customerID;
    public static String n;
    public Stage stage;
    public Parent root;
    public Scene scene;
    private Customer customer;
    private List<Product> productList;
    private Order order;

    private ObservableList<OrderProducts> orderProductList = FXCollections.observableArrayList();

    @FXML
    private ComboBox<String> prodCombo, bar;

    @FXML
    private TextField quantity, orderNo, quantityCount, itemCount, loyaltyPoint, totalText, total1;

    @FXML
    private Button billBtn1, billBtn;

    @FXML
    private TableView<OrderProducts> tableView;

    @FXML
    private TableColumn<OrderProducts, String> colBarcode, colProductName;

    @FXML
    private TableColumn<OrderProducts, Integer> colQuantity;

    @FXML
    private TableColumn<OrderProducts, Double> colPrice;

    @FXML
    private Label warningText;

    private int quan = 0;
    private int items = 0;
    private double subtotal = 0;

    public void initialize(){
        order = new Order();
        customer = new Customer();
        customer.setID(customerID);
        productList = customer.getAllAvaliableProducts();
        setupTableView();
        setupProdcutsCombo();
        orderNo.setText(String.format("%s",customer.getOrderNumber()+1));
        prodCombo.setOnAction(event -> {
            updateBarcode();
        });
    }

    private void setupProdcutsCombo() {
        ObservableList<String>  prod = FXCollections.observableArrayList();
        int currentYear = java.time.Year.now().getValue();
        for (Product p: productList) {
            prod.add(p.getName());
        }
        prodCombo.setItems(prod);
    }

    private void updateBarcode(){
        for (Product p: productList) {
            if (p.getName().equals(prodCombo.getValue())){
                bar.setValue(p.getBarcode());
            }
        }
    }

    private void setupTableView() {
        colBarcode.setCellValueFactory(cellData -> cellData.getValue().barcodeProperty());
        colProductName.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
        colQuantity.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        colPrice.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());

        tableView.setItems(orderProductList);

        // Add context menu for deletion
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(event -> deleteSelectedRecord());
        contextMenu.getItems().add(deleteItem);

        tableView.setContextMenu(contextMenu);
    }


    @FXML
    public void addProduct() {
        String selectedProduct = prodCombo.getValue();
        String enteredQuantity = quantity.getText();

        if (selectedProduct == null || enteredQuantity.isEmpty()) {
            warningText.setText("Please select a product and enter a quantity.");
            return;
        }

        try {
            int qty = Integer.parseInt(enteredQuantity);
            for (Product p : productList) {
                if (p.getName().equals(selectedProduct)) {
                    if (qty > p.getStockQuantity()) {
                        warningText.setText("Not enough quantity available.");
                        return;
                    }

                    // Add product to TableView with correct property initialization
                    OrderProducts op = new OrderProducts();
                    op.setProduct(p);
                    op.setOrder(order);
                    op.setQuantity(qty);
                    quan += qty;
                    quantityCount.setText(String.format("%s",quan));

                    items = orderProductList.size() + 1;
                    items++;
                    itemCount.setText(String.format("%s",items));
                    System.out.println(orderProductList.size());
                    // Populate display properties for TableView
                    op.setBarcode(p.getBarcode());
                    op.setProductName(p.getName());
                    op.setPrice(p.getPrice() * qty);

                    subtotal += (p.getPrice() * qty);
                    totalText.setText(String.format("%.2f",subtotal));
                    total1.setText(String.format("%.2f",subtotal));

                    orderProductList.add(op);
                    warningText.setText(""); // Clear warning text
                    return;
                }
            }
        } catch (NumberFormatException e) {
            warningText.setText("Please enter a valid quantity.");
        }
    }


    private void deleteSelectedRecord() {
        OrderProducts selectedItem = tableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            orderProductList.remove(selectedItem);
            quan -= selectedItem.getQuantity();
            quantityCount.setText(String.format("%s",quan));

            items--;
            itemCount.setText(String.format("%s",quan));

            subtotal -= (selectedItem.getProduct().getPrice() * selectedItem.getQuantity());
            totalText.setText(String.format("%.2f",subtotal));
            total1.setText(String.format("%.2f",subtotal));
        }
    }

    @FXML
    public void processOrder() {
        if (orderProductList.isEmpty()) {
            warningText.setText("No products added to the order.");
            return;
        }

        boolean isOrderProcessed = customer.processOrder(orderProductList, n, "Online");
        if (isOrderProcessed) {
            warningText.setText("Order processed successfully.");
            orderProductList.clear(); // Clear TableView
        } else {
            warningText.setText("Failed to process the order.");
        }
    }

    @FXML
    public void loadTakeaway(javafx.event.ActionEvent e) throws IOException {
        Parent b = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/inventra/customerTakeaway.fxml")));
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
}
