package com.example.inventra.Controller;

import com.example.inventra.DBHandler.POSHandler;
import com.example.inventra.OOP.Cashier;
import com.example.inventra.OOP.Order;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class cashierTakeawayController {
    public static int cashierID;
    public static String n;
    public Stage stage;
    public Parent root;
    public Scene scene;
    private final POSHandler posHandler = new POSHandler(); // Handler for database operations
    private final ObservableList<OrderWrapper> orderList = FXCollections.observableArrayList(); // Observable list for binding data
    public static String cashierName; // Cashier's Name
    private Cashier cashier;

    @FXML
    private Text name;

    @FXML
    private TextField timeBox;

    @FXML
    private TableView<OrderWrapper> table;

    @FXML
    private TableColumn<OrderWrapper, Integer> Orderno;

    @FXML
    private TableColumn<OrderWrapper, String> CustomerName;

    @FXML
    private TableColumn<OrderWrapper, String> mobileno;

    @FXML
    private TableColumn<OrderWrapper, String> time;

    @FXML
    private TableColumn<OrderWrapper, String> status;

    @FXML
    private TableColumn<OrderWrapper, Void> actions;

    @FXML
    public void initialize() {
        cashier = new Cashier();
        name.setText(cashierName); // Set the cashier's name
        setupTableView(); // Set up the TableView
        //setupRealTimeClock(); // Set up a real-time clock
        loadTakeawayOrders(); // Load orders of type "Online"
    }

    // Set up TableView columns
    private void setupTableView() {
        Orderno.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getOrderno()).asObject());
        CustomerName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerName()));
        mobileno.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getmobileno()));
        time.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().gettime()));
        status.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));

        // Add delete/action button column
        actions.setCellFactory(getActionButtonCellFactory());

        table.setItems(orderList); // Attach the observable list to the TableView
    }

    private void loadTakeawayOrders() {
        try {
            // Fetch orders with orderType "Online"
            List<Order> onlineOrders = posHandler.getOrdersByType("Online");
            orderList.clear(); // Clear existing data
            for (Order order : onlineOrders) {
                orderList.add(new OrderWrapper(order.getOrderID(), order.getCustomerName(), order.getCustomerNumber(),
                        order.getOrderDate().toString(), order.getStatus()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load online orders.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Callback<TableColumn<OrderWrapper, Void>, TableCell<OrderWrapper, Void>> getActionButtonCellFactory() {
        return param -> new TableCell<>() {
            private final Button actionButton = new Button("Accept");
            {
                actionButton.setOnAction(event -> {
                    OrderWrapper order = getTableView().getItems().get(getIndex());
                    updateOrderstatus(order); // Show details for the selected order
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionButton);
                }
            }
        };
    }

    private void updateOrderstatus(OrderWrapper order) {
        // Implement order detail view functionality here
        //showAlert("Order Details", "Order #" + order.getOrderno() + "\nCustomer: " + order.getCustomerName());
        cashier.updateOrderStatus(order.getOrderno());
        loadTakeawayOrders();
        setupTableView();
    }

    private void setupRealTimeClock() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalTime now = LocalTime.now();
            //timeBox.setText(now.format(formatter)); // Update the clock every second
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    @FXML
    public void loadSellItem(javafx.event.ActionEvent e) throws IOException {
        cashierPOSController.n = n;
        cashierPOSController.IDD = cashierID;
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
        cashierReturnItemController.n = n;
        cashierReturnItemController.cashierID = cashierID;
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
        cashierTakeawayController.cashierID = cashierID;
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

    // Wrapper class for TableView
    private static class OrderWrapper {
        private final int Orderno;
        private final String CustomerName;
        private final String mobileno;
        private final String time;
        private final String status;

        public OrderWrapper(int Orderno, String CustomerName, String mobileno, String time, String status) {
            this.Orderno = Orderno;
            this.CustomerName = CustomerName;
            this.mobileno = mobileno;
            this.time = time;
            this.status = status;
        }

        public int getOrderno() {
            return Orderno;
        }

        public String getCustomerName() {
            return CustomerName;
        }

        public String getmobileno() {
            return mobileno;
        }

        public String gettime() {
            return time;
        }

        public String getStatus() {
            return status;
        }
    }
}
