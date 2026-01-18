package com.example.inventra.Controller;

import com.example.inventra.DBHandler.SalesHandler;
import com.example.inventra.OOP.Admin;
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

public class adminDailySalesReportController {
    public static int adminID;
    public static String n;
    public Stage stage;
    public Parent root;
    public Scene scene;
    private Admin admin;
    private int currentYear = java.time.Year.now().getValue();

    @FXML
    private Text name;

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
    private ImageView pngWarning;
    private boolean flag = false;

    public void initialize(){
        name.setText(n);
        admin = new Admin(); // Initialize Admin object
        //pngWarning.setVisible(flag);
        setupMonthComboBox();
        monthComboBox.setOnAction(event -> {
            updateReport();
        });
    }

    private void setupMonthComboBox() {
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        monthComboBox.setItems(FXCollections.observableArrayList(months));
//        monthComboBox.getSelectionModel().selectFirst();
    }

    private void updateOrderTypePieChart(int year, int month) {
        productPieChart.getData().clear();  // Clear previous data

        // Fetch sales data by order type
        Map<String, Double> salesByOrderType = admin.fetchSalesByOrderType1(year, month);

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
        String selectedMonth = monthComboBox.getValue();
        int monthIndex = monthComboBox.getItems().indexOf(selectedMonth) + 1;

        Map<String, Double> totals = admin.fetchTotalSalesAndProfitByMonth(currentYear, monthIndex);
        double totalSales = totals.getOrDefault("totalSales", 0.0);
        double totalProfit = totals.getOrDefault("totalProfit", 0.0);
        double profitPercentage = totalSales > 0 ? (totalProfit / totalSales) * 100 : 0;
        totalSalesField.setText(String.format("%.2f", totalSales));
        totalProfitField.setText(String.format("%.2f", totalProfit));
        profitPercentageField.setText(String.format("%.2f%%", profitPercentage));


        // Update top product
        Map<String, Object> topProduct = admin.fetchTopProductByMonth(currentYear, monthIndex);
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
        updateStackedBarChart(monthIndex);
        updateOrderTypePieChart(currentYear,monthIndex);
        updateBarChart(monthIndex);
//        updateOrderTypePieChart(currentYear, monthIndex);
    }

    private void updateStackedBarChart(int month) {
        salesStackedBarChart.getData().clear();

        XYChart.Series<String, Number> salesSeries = new XYChart.Series<>();
        salesSeries.setName("Sales");

        XYChart.Series<String, Number> profitSeries = new XYChart.Series<>();
        profitSeries.setName("Profit");

        // Fetch the daily sales and profit data for the selected month
        Map<Integer, Map<String, Double>> dailyData = admin.fetchDailySalesAndProfit(currentYear, month);

        // Iterate over all days of the month and fill the chart with the corresponding data
        for (int day = 1; day <= 30; day++) {
            double sales = dailyData.containsKey(day) ? dailyData.get(day).get("sales") : 0.0;
            double profit = dailyData.containsKey(day) ? dailyData.get(day).get("profit") : 0.0;

            salesSeries.getData().add(new XYChart.Data<>(String.valueOf(day), sales));
            profitSeries.getData().add(new XYChart.Data<>(String.valueOf(day), profit));
        }

        salesStackedBarChart.getData().addAll(salesSeries, profitSeries);

        // Set the x-axis categories to represent the days of the month (e.g., 1, 2, 3, ..., 31)
        //xAxis.setCategories(FXCollections.observableArrayList(generateDayLabels(dailyData.size())));
    }

    // Helper method to generate day labels (1, 2, 3, ..., N)
    private List<String> generateDayLabels(int numberOfDays) {
        List<String> dayLabels = new ArrayList<>();
        for (int i = 1; i <= numberOfDays; i++) {
            dayLabels.add(String.valueOf(i));
        }
        return dayLabels;
    }

    private void updateBarChart(int month) {
        salesBarChart.getData().clear();

        Map<String, Integer> productSales = admin.fetchProductSalesByMonth(currentYear, month);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Products Sold");
        List<String> prodName = new ArrayList<>();

        // Create the series for product sales
        for (Map.Entry<String, Integer> entry : productSales.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            prodName.add(entry.getKey());
        }

        salesBarChart.getData().add(series);

        // Set x-axis categories as product names
        xAxis.setCategories(FXCollections.observableArrayList(prodName));
        xAxis.setTickLabelRotation(90);
    }

    private void updatePieChart(int year, int month) {
//        productPieChart.getData().clear();
//
//        // Fetch product sales for the selected month
//        Map<String, Double> productSales = admin.fetchSalesByOrderType1(year, month);
//
//        for (Map.Entry<String, Double> entry : productSales.entrySet()) {
//            PieChart.Data slice = new PieChart.Data(entry.getKey(), entry.getValue());
//            productPieChart.getData().add(slice);
//        }
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
