package com.example.inventra.Controller;

import com.example.inventra.DBHandler.POSHandler;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class cashierReturnItemController {
    public static int cashierID;
    public static String n;
    public Stage stage;
    public Parent root;
    public Scene scene;
    private final ObservableList<ProductWrapper> productList = FXCollections.observableArrayList();
    private final POSHandler posHandler = new POSHandler();
    private Cashier cashier;

    @FXML
    private TextField search_inv, total_qt, total_item, totalText;

    @FXML
    private TableView<ProductWrapper> productsTable;

    @FXML
    private TableColumn<ProductWrapper, Integer> SRno, qt;

    @FXML
    private TableColumn<ProductWrapper, String> Barcode, Prod_name;

    @FXML
    private TableColumn<ProductWrapper, Double> unit_price ;

    @FXML
    private TableColumn<ProductWrapper, Float> tb_dis, tb_disc,total;

    @FXML
    private TableColumn<ProductWrapper,Void> del;

    @FXML
    private Text name;

    public double TOTAL;

    @FXML
    public TextField disc,subtotal;

    public void initialize() {
        cashier = new Cashier();
        name.setText(n); // Set cashier name
        setupTableView();
        //setupRealTimeClock();
    }

    // Setup TableView Columns
    private void setupTableView() {
        SRno.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSrNo()).asObject());
        Prod_name.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        Barcode.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBarcode()));
        qt.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        unit_price.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getUnitPrice()).asObject());
        tb_dis.setCellValueFactory(cellData -> new SimpleFloatProperty(cellData.getValue().getDiscountPercentage()).asObject());
        tb_dis.setCellValueFactory(cellData -> new SimpleFloatProperty(cellData.getValue().getDiscountAmount()).asObject());
        total.setCellValueFactory(cellData -> new SimpleFloatProperty(cellData.getValue().getTotalPrice()).asObject());

        // Add delete button column
        del.setCellFactory(getDeleteButtonCellFactory());
        productsTable.setItems(productList);
    }

    @FXML
    public void handleReturn(){

        try {

        int orderID = posHandler.getOrderIDfromInvoice(Integer.parseInt(search_inv.getText()));
        posHandler.deleteorderProduct(orderID);
        double t=TOTAL;
        posHandler.updateInvoice(orderID,t);
            ObservableList<OrderProducts> orderProducts = FXCollections.observableArrayList();
            for (cashierReturnItemController.ProductWrapper wrapper : productList) {
                OrderProducts orderProduct = new OrderProducts(orderID, wrapper.product.getProductID(), wrapper.quantity);
                orderProducts.add(orderProduct);
            }
            posHandler.saveOrderProducts(orderID, orderProducts);
            clearFields();
        updateSummaryFields();
//        productList.clear();
        ////I AM HERE
productsTable.refresh();
    }
    catch (Exception e) {
        e.printStackTrace();
        showAlert("Error", "An error occurred while generating the bill. Please try again.");
    }

}
    private void clearFields() {
        search_inv.clear();
    }
//     Handle Search Invoice Button
    @FXML
    private void handleSearch() {

        String invoiceNumber = search_inv.getText();

        if (invoiceNumber.isEmpty()) {
            showAlert("Input Error", "Please enter a valid invoice number.");
            return;
        }

        try {
            // Fetch order products based on the invoice number

            List<OrderProducts> orderProducts = posHandler.getOrderProductsByInvoiceNumber(invoiceNumber);


            if (orderProducts.isEmpty()) {
                showAlert("No Data Found", "No products found for the given invoice number.");
                return;
            }

            productList.clear(); // Clear the current list



            int srCounter = 1;
            for (OrderProducts orderProduct : orderProducts) {
                try{
                    Product p =posHandler.getProductById(orderProduct.getProductID());

                }
                catch(Exception e){
                    e.printStackTrace();
                    showAlert("Error", "An unexpected error occurred while fetching the products ndsnjfbjs.");
                }
                Product product = posHandler.getProductById(orderProduct.getProductID());
                if (product != null) {
                    ProductWrapper wrapper = new ProductWrapper(
                            srCounter++,
                            product,
                            orderProduct.getQuantity(),
                            0,
                            0,
                            (float) (orderProduct.getQuantity() * product.getPrice())
                    );
                    try {
                        productList.add(wrapper);
                    }
                    catch(Exception e){
                        e.printStackTrace();

                    }
                    }
            }

            updateSummaryFields(); // Update total quantity and items
            productsTable.refresh();

        } catch (Exception e) {
            e.printStackTrace();
//            showAlert("Error", "An unexpected error occurred while fetching the products.");
        }
    }
    private Callback<TableColumn<cashierReturnItemController.ProductWrapper, Void>, TableCell<cashierReturnItemController.ProductWrapper, Void>> getDeleteButtonCellFactory() {
        return param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    cashierReturnItemController.ProductWrapper product = getTableView().getItems().get(getIndex());
                    productList.remove(product);
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

    // Update Summary Fields (Total Quantity and Items)
    private void updateSummaryFields() {
        int totalQuantity = productList.stream().mapToInt(ProductWrapper::getQuantity).sum();
        int totalItems =productList.size();
        disc.setText(String.valueOf(posHandler.getdisc(Integer.parseInt(search_inv.getText()))));
        double totalAmount = productList.stream().mapToDouble(ProductWrapper::getTotalPrice).sum();
        double t=productList.stream().mapToDouble(ProductWrapper::getTotalPrice).count();
        total_qt.setText(String.valueOf(totalQuantity));
        total_item.setText(String.valueOf(totalItems));
        totalText.setText(String.format("💰 %.2f", totalAmount));
        subtotal.setText(String.format("💰 %.2f", totalAmount));
        TOTAL=totalAmount;
    }

    // Utility: Show Alert Dialog
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Real-time clock setup
    private void setupRealTimeClock() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalTime now = LocalTime.now();
            name.setText("Time: " + now.format(formatter)); // Show time on the name field for simplicity
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    @FXML
    public void loadSellItem(javafx.event.ActionEvent e) throws IOException {
        cashierPOSController.IDD = cashierID;
        cashierPOSController.n = n;
        Parent b = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/inventra/POS.fxml")));
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
    public void loadReturnItem(javafx.event.ActionEvent e) throws IOException {
        cashierReturnItemController.cashierID = cashierID;
        cashierReturnItemController.n = n;
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
        stage.setWidth(650);
        stage.setHeight(450);
        stage.setFullScreen(stage.isFullScreen());
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void loadTakeaway(javafx.event.ActionEvent e) throws IOException {
        cashierTakeawayController.cashierID = cashierID;
        cashierTakeawayController.n = n;
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
    // Product Wrapper Class
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
