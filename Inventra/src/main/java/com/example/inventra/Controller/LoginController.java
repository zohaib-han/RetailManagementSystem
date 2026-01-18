package com.example.inventra.Controller;

import com.example.inventra.DBHandler.DataBaseHandler;
import com.example.inventra.DBHandler.SQLDatabase;
import com.example.inventra.DBHandler.UserAuthentication;
import com.example.inventra.OOP.Admin;
import com.example.inventra.OOP.Cashier;
import com.example.inventra.OOP.Customer;
import com.example.inventra.OOP.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    public Stage stage;
    public Parent root;
    public Scene scene;
    public String Name;

    @FXML
    public TextField username;

    @FXML
    public PasswordField password;

    @FXML
    public Button loginButton;

    @FXML
    public ComboBox<String> dropDown;

    @FXML
    public Label warning;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dropDown.getItems().addAll("Adminn", "Cashier", "Customer");
    }

    @FXML
    public void resetWarning(){
        if (warning.getText().equals("Select User Kindly !!")) {
            warning.setText("");
        }
    }

    @FXML
    public void setLoginButton(javafx.event.ActionEvent e){
        String selectedValue = dropDown.getValue();
        if (selectedValue == null || selectedValue.isEmpty()) {
            warning.setText("Select User Kindly !!");
            return;
        }
        else if (username.getText().isEmpty() || password.getText().isEmpty()){
            warning.setText("UserName/Password Is Empty !! !");
            return;
        }
        else {
            final int flag = isFlag(selectedValue);
            if (flag == 0){
                warning.setText("Incorrect UserName/Password !! !");
            }
            else{
                warning.setText("Successfull");
                if (selectedValue.equals("Adminn")){
                    try {
                        adminManageProductsController.adminID = flag;
                        adminManageProductsController.n = username.getText();
                        loadAdmin(e);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                        warning.setText("Failed to load the admin dashboard.");
                    }
                } else if (selectedValue.equals("Cashier")) {
                    try {
                        cashierPOSController.IDD = flag;
                        cashierPOSController.n = username.getText();
                        loadCashier(e);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                        warning.setText("Failed to load the cashier dashboard.");
                    }
                } else if (selectedValue.equals("Customer")) {
                    try {
                        customerTakeawayController.customerID = flag;
                        customerTakeawayController.n = username.getText();
                        loadCustomer(e);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                        warning.setText("Failed to load the customer dashboard.");
                    }
                }
            }
        }
    }

    private int isFlag(String selectedValue) {
        User admin = new Admin();
        User cashier = new Cashier();
        User customer = new Customer();
        int flag = 0;
        if (selectedValue.equals("Adminn")){
            flag = admin.authenticator(username.getText(), password.getText());
        } else if (selectedValue.equals("Cashier")) {
            flag = cashier.authenticator(username.getText(), password.getText());
        } else if (selectedValue.equals("Customer")) {
            flag = customer.authenticator(username.getText(), password.getText());
        }
        return flag;
    }

    public void loadAdmin(javafx.event.ActionEvent e) throws IOException {
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

    public void loadCashier(javafx.event.ActionEvent e) throws IOException {
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

    public void loadCustomer(javafx.event.ActionEvent e) throws IOException {
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
}
