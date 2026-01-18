package com.example.inventra.Controller;

import com.example.inventra.OOP.Cashier;
import com.example.inventra.OOP.Customer;
import com.example.inventra.OOP.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class adminManageUser {
    public static int adminID;
    public static String n;
    public Stage stage;
    public Parent root;
    public Scene scene;

    @FXML
    private TableView<User> userTableView;

    @FXML
    private TableColumn<User, Integer> userIDColumn;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> phoneNumberColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> userTypeColumn;

    @FXML
    private TextField usernameField, emailField, passwordField, phoneNumberField;

    @FXML
    private ComboBox<String> userTypeComboBox;

    @FXML
    private Button addUserButton;

    @FXML
    private Label warningLabel;

    @FXML
    private MenuButton typeMenu;

    private final ObservableList<User> userList = FXCollections.observableArrayList();

    @FXML
    private Text name;

    public void initialize() {
        name.setText(n);
        setupTypeMenu();
        setupTableColumns();

        // Set up ComboBox for user types
        userTypeComboBox.getItems().addAll("Cashier", "Customer");

        // Enable editing
        makeColumnsEditable();
    }

    private void setupTypeMenu() {
        // Create MenuItems for "Cashier" and "Customer"
        MenuItem cashierMenuItem = new MenuItem("Cashier");
        cashierMenuItem.setOnAction(e -> {
            typeMenu.setText("Cashier");
            loadUserData("Cashier");
        });

        MenuItem customerMenuItem = new MenuItem("Customer");
        customerMenuItem.setOnAction(e -> {
            typeMenu.setText("Customer");
            loadUserData("Customer");
        });

        // Add items to the MenuButton
        typeMenu.getItems().addAll(cashierMenuItem, customerMenuItem);
    }

    private void setupTableColumns() {
        userIDColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        usernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        phoneNumberColumn.setCellValueFactory(cellData -> cellData.getValue().phoneNumberProperty()); // Bind phoneNumber
        userTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));

        userTableView.setItems(userList);
    }

    @FXML
    private void loadUserData(String type) {
        if (type == null || type.isEmpty()) {
            warningLabel.setText("Please select a user type.");
            return;
        }

        userList.clear(); // Clear existing data
        userList.addAll(User.getAllUsers(type)); // Load users based on type
        userTableView.setItems(userList); // Bind to TableView
        warningLabel.setText(""); // Clear warnings
    }

    private void makeColumnsEditable() {
        userTableView.setEditable(true);

        usernameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        usernameColumn.setOnEditCommit(event -> {
            User user = event.getRowValue();
            user.setUsername(event.getNewValue());
            user.update();
        });

        emailColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        emailColumn.setOnEditCommit(event -> {
            User user = event.getRowValue();
            user.setEmail(event.getNewValue());
            user.update();
        });

        phoneNumberColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        phoneNumberColumn.setOnEditCommit(event -> {
            User user = event.getRowValue();
            user.setPhoneNumber(event.getNewValue());
            user.update();
        });
    }

    @FXML
    private void addUser() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String phoneNumber = phoneNumberField.getText();
        String userType = userTypeComboBox.getValue();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || phoneNumber.isEmpty() || userType == null) {
            warningLabel.setText("Please fill all fields!");
            return;
        }

        User newUser;
        if ("Cashier".equals(userType)) {
            newUser = new Cashier(0, username, password, email, phoneNumber);
        } else {
            newUser = new Customer(0, username, password, email, phoneNumber);
        }

        newUser.add();
        loadUserData(typeMenu.getText());
        warningLabel.setText("User added successfully!");
    }

    @FXML
    private void deleteUser() {
        User selectedUser = userTableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            selectedUser.delete();
            userList.remove(selectedUser);
        } else {
            warningLabel.setText("No user selected!");
        }
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
    public void loadManageProducts(javafx.event.ActionEvent e) throws IOException {
        adminManageProductsController.adminID = adminID;
        adminManageProductsController.n = n;
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
