package com.example.inventra.Controller;

import com.example.inventra.DBHandler.POSHandler;
import com.example.inventra.DBHandler.ProductHandler;
import com.example.inventra.OOP.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class cashierPOSController {
    public static int IDD;
    public static String n;
    public Stage stage;
    public Parent root;
    public Scene scene;
    private final ObservableList<ProductWrapper> product_list = FXCollections.observableArrayList();
    private final ProductHandler prod_hand = new ProductHandler();
    private int srCounter = 1; // Counter for SRno
    int orderID = 0;
    //private final int iD;
    @FXML
    private Text name;

    @FXML
    private TableView<ProductWrapper> productsTable;

    @FXML
    private MenuButton Product_name;

    @FXML
    private TextField invoice,barcode, disc, Quantity, total_qt, total_items, tax, disc_rate, misc_charges, sub_total, adjust, totalText,mobileno,clientName;

    @FXML
    private TextField timeBox;

    @FXML
    private TableColumn<ProductWrapper, Integer> SRno, qt;

    @FXML
    private TableColumn<ProductWrapper, String> Barcode, Productname;

    @FXML
    private TableColumn<ProductWrapper, Double> unit_price;

    @FXML
    private TableColumn<ProductWrapper, Float> Total_price, tb_dis, tb_disc;

    @FXML
    private TableColumn<ProductWrapper, Void> del; // Delete button column
    public POSHandler Pos =new POSHandler();

    private Cashier cashier;

    // Initialize the controller
    public void initialize() {
        cashier = new Cashier();
        name.setText(n); // Set the cashier name
        setupTableView();
        loadproduct_names();
        setupRealTimeClock();
        clearFields();
        addListeners(); // Add listeners for live updates
        updateSummaryFields(); // Initialize summary fields
        loadinvoice();
    }

    // Add listeners for live updates
    private void addListeners() {
        tax.textProperty().addListener((observable, oldValue, newValue) -> updateSummaryFields());
        disc_rate.textProperty().addListener((observable, oldValue, newValue) -> updateSummaryFields());
        misc_charges.textProperty().addListener((observable, oldValue, newValue) -> updateSummaryFields());
    }

    // Handle Add Button
    @FXML
    public void handleAddButton() {
        try {
            String barcodeValue = barcode.getText();
            String productName = Product_name.getText();
            String discountValue = disc.getText();
            String quantityValue = Quantity.getText();

            if (barcodeValue.isEmpty() || productName.isEmpty() || discountValue.isEmpty() || quantityValue.isEmpty()) {
                showAlert("Input Error", "Please fill in all fields before adding.");
                return;
            }

            int quantity = Integer.parseInt(quantityValue);
            float discountPercentage = Float.parseFloat(discountValue);

            Product product = prod_hand.getProductByBarcode(barcodeValue);
            if (product == null) {
                showAlert("Product Not Found", "The product with the given barcode does not exist.");
                return;
            }

            double unitPrice = product.getPrice();
            double discountAmount = unitPrice * quantity * (discountPercentage / 100);
            double totalPrice = unitPrice * quantity - discountAmount;

            ProductWrapper newProduct = new ProductWrapper(
                    srCounter++,
                    product,
                    quantity, // Use the entered quantity
                    discountPercentage,
                    (float) discountAmount,
                    (float) totalPrice
            );

            product_list.add(newProduct);
            productsTable.refresh();
            clearFields();
            updateSummaryFields(); // Update summary fields after adding an item

        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid numbers for quantity and discount.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An unexpected error occurred. Please try again.");
        }
    }

    @FXML
    public void handleGenerateBill(){
        try {
            Order order = new Order(0, IDD, LocalDateTime.now(), "completed", mobileno.getText(), clientName.getText(), "In-Store");
            orderID = Pos.saveOrder(order);
            System.out.print("The order ID is ::");
            System.out.println(orderID);
            String customerName = clientName.getText();
            String customerMobile = mobileno.getText();
            if (mobileno.getText().isEmpty() || clientName.getText().isEmpty()) {
                showAlert("Error", "Customer name and mobile number are required.");
                return;
            }
            if(product_list.isEmpty()){
                showAlert("Error","Nothing to add in list");
                return;
            }
            ObservableList<OrderProducts> orderProducts = FXCollections.observableArrayList();
            for (ProductWrapper wrapper : product_list) {
                OrderProducts orderProduct = new OrderProducts(orderID, wrapper.product.getProductID(), wrapper.quantity);
                orderProducts.add(orderProduct);
            }
            Pos.saveOrderProducts(orderID, orderProducts);

            double subtotal = Double.parseDouble(sub_total.getText());
            double taxRate = getFieldValueAsDouble(tax);
            double discountRate = getFieldValueAsDouble(disc_rate);
            double miscCharges = getFieldValueAsDouble(misc_charges);

            double taxAmount = subtotal * (taxRate / 100);
            double discountAmount = subtotal * (discountRate / 100);
            double totalAmount = subtotal + taxAmount - discountAmount + miscCharges;

            Invoice invoice = new Invoice(totalAmount,discountAmount,orderID);
            int invoiceID=Pos.saveInvoice(invoice);
            // Clear fields and table after saving
            clearFields();
            product_list.clear();
            updateSummaryFields();
            generateInvoiceTxt();
            ////I AM HERE

        }
        catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while generating the bill. Please try again.");
        }
}
    // Update summary fields
    private void updateSummaryFields() {
        int totalQuantity = product_list.stream().mapToInt(ProductWrapper::getQuantity).sum();
        int totalItems = product_list.size();
        double subtotal = product_list.stream().mapToDouble(ProductWrapper::getTotalPrice).sum();

        double taxRate = getFieldValueAsDouble(tax);
        double discountRate = getFieldValueAsDouble(disc_rate);
        double miscCharges = getFieldValueAsDouble(misc_charges);

        double taxAmount = subtotal * (taxRate / 100);
        double discountAmount = subtotal * (discountRate / 100);
        double adjustedAmount = miscCharges - discountAmount + taxAmount;

        total_qt.setText(String.valueOf(totalQuantity));
        total_items.setText(String.valueOf(totalItems));
        sub_total.setText(String.format("%.2f", subtotal));
        adjust.setText(String.format("%.2f", adjustedAmount));
        totalText.setText(String.format("💰 %.2f", subtotal + adjustedAmount));
    }

    private double getFieldValueAsDouble(TextField field) {
        try {
            return field.getText().isEmpty() ? 0 : Double.parseDouble(field.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // Clear input fields
    private void clearFields() {
        barcode.clear();
        Product_name.setText("Select Product");
        disc.clear();
        Quantity.clear();
    }

    // Show alert dialog
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void loadinvoice(){
        int inv_no=Pos.send_invno();
        invoice.setText(""+inv_no);
    }
    // Load product names into the MenuButton
    public void loadproduct_names() {
        for (Product product : prod_hand.getAllProducts()) {
            MenuItem menuItem = new MenuItem(product.getName());
            menuItem.setOnAction(e -> {
                Product_name.setText(product.getName());
                barcode.setText(product.getBarcode());
            });
            Product_name.getItems().add(menuItem);
        }
    }

    // Set up TableView columns
    private void setupTableView() {
        SRno.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSrNo()).asObject());
        Productname.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        Barcode.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBarcode()));
        qt.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        unit_price.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getUnitPrice()).asObject());
        tb_disc.setCellValueFactory(cellData -> new SimpleFloatProperty(cellData.getValue().getDiscountPercentage()).asObject());
        tb_dis.setCellValueFactory(cellData -> new SimpleFloatProperty(cellData.getValue().getDiscountAmount()).asObject());
        Total_price.setCellValueFactory(cellData -> new SimpleFloatProperty(cellData.getValue().getTotalPrice()).asObject());

        // Add delete button column
        del.setCellFactory(getDeleteButtonCellFactory());
        productsTable.setItems(product_list);
    }

    public void generateInvoiceTxt() {
        try {
            // Path where the file will be saved
            String filePath = "C:\\Users\\Saeed\\Desktop\\Invoice_"+String.format("%s",orderID) + ".txt";

            // Fetch data from the database
            String invoiceData = cashier.generateInvoiceData(orderID); // Call method from Customer class

            // Write data to file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write(invoiceData);
            }

            System.out.println("Invoice saved at: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Callback<TableColumn<ProductWrapper, Void>, TableCell<ProductWrapper, Void>> getDeleteButtonCellFactory() {
        return param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    ProductWrapper product = getTableView().getItems().get(getIndex());
                    product_list.remove(product);
                    updateSummaryFields(); // Update summary fields after deletion
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        };
    }

    // Real-time clock setup
    public void setupRealTimeClock() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalTime now = LocalTime.now(); // Get current time
            timeBox.setText(now.format(formatter)); // Set formatted time in TextField
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    @FXML
    public void loadReturnItem(javafx.event.ActionEvent e) throws IOException {
        cashierReturnItemController.n = n;
        cashierReturnItemController.cashierID = IDD;
        Parent b = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/inventra/ReturnItem.fxml")));
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
    public void loadLogout(javafx.event.ActionEvent e) throws IOException {
        Parent b = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/inventra/Login.fxml")));
        stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(b, 600, 400);
        //scene.getStylesheets().add(getClass().getResource("POS.css").toExternalForm());
        stage.setTitle("Inventra Retail Management System 1.0");
        stage.setResizable(false);
        stage.setMaximized(true);
        stage.setFullScreen(stage.isFullScreen());
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void loadTakeaway(javafx.event.ActionEvent e) throws IOException {
        cashierTakeawayController.n = n;
        cashierTakeawayController.cashierID = IDD;
        Parent b = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/inventra/Takeaway.fxml")));
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

    // Product wrapper class
    private static class ProductWrapper {
        private final int srNo;
        private final Product product;
        private final int quantity;
        private final float discountPercentage;
        private final float discountAmount;
        private final float totalPrice;

        public ProductWrapper(int srNo, Product product, int quantity, float discountPercentage, float discountAmount, float totalPrice) {
            this.srNo = srNo;
            this.product = product;
            this.quantity = quantity;
            this.discountPercentage = discountPercentage;
            this.discountAmount = discountAmount;
            this.totalPrice = totalPrice;
        }

        public int getSrNo() {
            return srNo;
        }

        public String getName() {
            return product.getName();
        }

        public String getBarcode() {
            return product.getBarcode();
        }

        public int getQuantity() {
            return quantity;
        }

        public double getUnitPrice() {
            return product.getPrice();
        }

        public float getDiscountPercentage() {
            return discountPercentage;
        }

        public float getDiscountAmount() {
            return discountAmount;
        }

        public float getTotalPrice() {
            return totalPrice;
        }
    }
}