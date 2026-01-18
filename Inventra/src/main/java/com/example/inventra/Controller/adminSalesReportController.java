package com.example.inventra.Controller;

import com.example.inventra.DBHandler.SalesHandler;
import com.example.inventra.OOP.Admin;
import com.example.inventra.OOP.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class adminSalesReportController {
    public static int adminID;
    public static String n;
    public Stage stage;
    public Parent root;
    public Scene scene;
    private Admin admin;

    @FXML
    private ComboBox<Integer> yearComboBox;

    @FXML
    private Text totalSalesField, totalProfitField, profitPercentageField;

    @FXML
    private TextField topProductField, topProdT, topProdQ;

    @FXML
    private BarChart<String, Number> salesBarChart;

    @FXML
    private PieChart productPieChart;

    @FXML
    private ComboBox<String> monthComboBox;

    @FXML
    private StackedBarChart<String, Number> salesStackedBarChart;

    @FXML
    private CategoryAxis xAxis; // X-axis for categories (months)

    @FXML
    private NumberAxis yAxis; // Y-axis for numerical values


    private final SalesHandler salesHandler = new SalesHandler();

    @FXML
    private Text name;

    @FXML
    private ImageView pngWarning;
    private boolean flag = false;

    public void initialize(){
        name.setText(n);
        admin = new Admin(); // Initialize Admin object
        //pngWarning.setVisible(flag);
        setupYearComboBox();
        yearComboBox.setOnAction(event -> {
            updateReport();
            updateOrderTypePieChart(yearComboBox.getValue());
            updateBarChart(yearComboBox.getValue());
        });
    }

    @FXML
    private void setupMonthComboBox() {
        // Populate months
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        monthComboBox.setItems(FXCollections.observableArrayList(months));
        monthComboBox.getSelectionModel().selectFirst(); // Default to "All"
    }


    private void setupYearComboBox() {
        ObservableList<Integer> years = FXCollections.observableArrayList();
        int currentYear = java.time.Year.now().getValue();
        for (int i = 0; i < 5; i++) {
            years.add(currentYear - i);
        }
        yearComboBox.setItems(years);
    }

    private void updateBarChart(int year) {
        salesBarChart.getData().clear(); // Clear previous data

        // Fetch product sales data
        Map<String, Integer> productSales = admin.fetchProductSalesByYear(year);

        // Create a data series
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Products Sold");
        List<String> prodName = new ArrayList<>();

        // Populate the series with product data
        for (Map.Entry<String, Integer> entry : productSales.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            prodName.add(entry.getKey());
        }

        // Add the series to the bar chart
        salesBarChart.getData().add(series);

        // Update X-axis categories
        xAxis.setCategories(FXCollections.observableArrayList(prodName));

        // Set labels on X-axis to display vertically
        xAxis.setTickLabelRotation(90);
    }


    private void updateOrderTypePieChart(int year) {
        productPieChart.getData().clear();  // Clear previous data

        // Fetch sales data by order type
        Map<String, Double> salesByOrderType = admin.fetchSalesByOrderType(year);

        // If no data for order types, return
        if (salesByOrderType.isEmpty()) {
            return;
        }

        // Add data to the pie chart
        for (Map.Entry<String, Double> entry : salesByOrderType.entrySet()) {
            PieChart.Data slice = new PieChart.Data(entry.getKey(), entry.getValue());
            productPieChart.getData().add(slice);
        }
    }


    private void updateReport() {
        int selectedYear = yearComboBox.getValue();

        // Update total sales
        double totalSales = admin.fetchTotalSalesByYear(selectedYear);
        totalSalesField.setText(String.format("%.2f", totalSales));

        // Update total profit
        double totalProfit = admin.fetchTotalProfitByYear(selectedYear);
        totalProfitField.setText(String.format("%.2f", totalProfit));

        // Update profit percentage
        double profitPercentage = totalSales > 0 ? (totalProfit / totalSales) * 100 : 0;
        profitPercentageField.setText(String.format("%.2f%%", profitPercentage));

        // Update top product
        Map<String, Object> topProduct = admin.fetchTopProductByYear(selectedYear);

        if (topProduct != null && topProduct.get("productName") != null) {
            // Set the product name
            topProductField.setText(String.valueOf(topProduct.get("productName")));

            // Set the total quantity (convert Object to Integer)
            topProdQ.setText(String.valueOf(topProduct.get("totalQuantity")));

            // Set the total sales (convert Object to Double and format)
            topProdT.setText(String.format("%.2f", topProduct.get("totalSales")));
        } else {
            // Handle the case when no product data is found
            topProductField.setText("No product found");
            topProdQ.setText("0");
            topProdT.setText("0.00");
        }


        // Update stacked bar chart
        updateStackedBarChart(selectedYear);
    }

    private void updateStackedBarChart(int year) {
        salesStackedBarChart.getData().clear();

        XYChart.Series<String, Number> salesSeries = new XYChart.Series<>();
        salesSeries.setName("Sales");

        XYChart.Series<String, Number> profitSeries = new XYChart.Series<>();
        profitSeries.setName("Profit");

        // Define months array
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};

        // Fetch data
        Map<String, Map<String, Double>> monthlyData = admin.fetchMonthlySalesAndProfit(year);

        // Iterate over all months
        for (String month : months) {
            double sales = monthlyData.containsKey(month) ? monthlyData.get(month).get("sales") : 0.0;
            double profit = monthlyData.containsKey(month) ? monthlyData.get(month).get("profit") : 0.0;

            salesSeries.getData().add(new XYChart.Data<>(month, sales));
            profitSeries.getData().add(new XYChart.Data<>(month, profit));
        }

        salesStackedBarChart.getData().addAll(salesSeries, profitSeries);

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
