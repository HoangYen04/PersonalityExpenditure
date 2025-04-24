/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.kym.personalexpenditure;

import com.kym.pojo.Category;
import com.kym.pojo.Transaction;
import com.kym.pojo.User;
import com.kym.services.CategoryService;
import com.kym.services.ReportService;
import com.kym.services.Session;
import com.kym.services.Utils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author ADMIN
 */
public class Report2Controller implements Initializable {


    @FXML
    private Text txtGreeting;

    private int selectedMonth;
    private int selectedYear;
    @FXML
    private Label totalSpendingLabel;
    @FXML
    private TextField monthYearField;
    @FXML
    private TableView<Transaction> transactionTableView;
    private ReportService reportService = new ReportService();
    @FXML
    private PieChart categoryPieChart;
    @FXML
    private Button exportButton;

    public void initialize(URL url, ResourceBundle rb) {
        if (Session.getCurrentUser() != null) {
            txtGreeting.setText("Chào, " + Session.getCurrentUser().getName());
        }
//        setMonthYear(selectedMonth, selectedYear);

        loadCol();
    }

    public void setMonthYear(int month, int year) {
        this.selectedMonth = month;
        this.selectedYear = year;
        if (monthYearField != null) {
            monthYearField.setText(month + "/" + year);

        }

        System.out.println("Đã chọn: Tháng " + selectedMonth + ", Năm " + selectedYear);
        loadTransactions(month, year);
        loadPieChart(month, year);
    }

    public void loadTransactions(int month, int year) {
        try {
            // Lấy danh sách giao dịch từ ReportService

            List<Transaction> transactions = reportService.getTransactionsByUserAndMonth(month, year);
            System.out.println("Transactions loaded: " + transactions.size());
            // Nếu không có giao dịch, hiển thị thông báo
            if (transactions.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Không có giao dịch");
                alert.setHeaderText(null);
                alert.setContentText("Không có giao dịch trong tháng " + month + " năm " + year);
                alert.showAndWait();
                return;
            }
            // Tạo ObservableList để chứa giao dịch
            ObservableList<Transaction> transactionList = FXCollections.observableArrayList(transactions);
            System.out.println("List đã gán cho TableView: " + transactionList.size());
            System.out.println("TableView có cột: " + transactionTableView.getColumns().size());
            for (Transaction t : transactions) {
                System.out.println(t.getTransactionId() + " | " + t.getCategoryId() + " | " + t.getAmount() + " | " + t.getDate());
            }

            double total = calculateTotalSpending(transactions);
            totalSpendingLabel.setText("Tổng chi tiêu: " + total + " VNĐ");
            // Đổ dữ liệu vào TableView
            transactionTableView.setItems(FXCollections.observableList(transactionList));

//            transactionTableView.setItems(transactionList);
        } catch (SQLException e) {
            e.printStackTrace();
            Utils.getAlert("Không thể lấy dữ liệu giao dịch từ cơ sở dữ liệu.").showAndWait();
        }
    }

    public void loadCol() {

        // Cột STT (transactionId)
        TableColumn<Transaction, Integer> colSTT = new TableColumn<>("STT");
        colSTT.setPrefWidth(50);
        colSTT.setCellValueFactory(column
                -> new ReadOnlyObjectWrapper<>(transactionTableView.getItems().indexOf(column.getValue()) + 1)
        );
        colSTT.setSortable(false);

        TableColumn<Transaction, String> colCate = new TableColumn<>("Danh mục");
        colCate.setPrefWidth(100);
        colCate.setCellValueFactory(cellData -> {
            System.out.println("Đã cố gắng hết sức");
            try {
                // Lấy categoryId từ đối tượng Transaction
                int categoryId = cellData.getValue().getCategoryId();
                System.out.println("trc khi gọi report" + categoryId);
                // Gọi phương thức từ ReportService để lấy tên danh mục
                String categoryName = reportService.getCategoryNameById(categoryId);
                System.out.println("Category ID: " + categoryId + " => " + categoryName);  // In ra để kiểm tra
                return new SimpleStringProperty(categoryName);  // Trả về tên danh mục
            } catch (SQLException e) {
                e.printStackTrace();
                return new SimpleStringProperty("Lỗi DB");  // Trả về giá trị lỗi nếu có
            }
        });

        // Cột Số tiền (amount)
        TableColumn<Transaction, Double> colAmount = new TableColumn<>("Số tiền");
        colAmount.setPrefWidth(80);
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount")); // Hiển thị số tiền
        colAmount.setCellFactory(column -> new TableCell<Transaction, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                System.out.println(".updateItem()");
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.0f", item)); // Hiển thị số tiền với định dạng
                }
            }
        });

        // Cột Ngày (date)
        TableColumn<Transaction, String> colDate = new TableColumn<>("Ngày");
        colDate.setPrefWidth(80);
        colDate.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getDate(); // Giả sử bạn dùng LocalDate
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedDate = date.format(formatter);
            return new SimpleStringProperty(formattedDate);
        });

        // Thêm các cột vào TableView
        transactionTableView.getColumns().addAll(colSTT, colCate, colAmount, colDate);
    }

    private double calculateTotalSpending(List<Transaction> transactions) {
        return transactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public void loadPieChart(int month, int year) {
        try {
            List<Transaction> transactions = reportService.getTransactionsByUserAndMonth(month, year);
            Map<String, Double> categoryTotals = new HashMap<>();

            // Tính tổng chi tiêu theo danh mục
            for (Transaction t : transactions) {
                String categoryName = reportService.getCategoryNameById(t.getCategoryId());
                categoryTotals.put(categoryName,
                        categoryTotals.getOrDefault(categoryName, 0.0) + t.getAmount());
            }

            // Tạo ObservableList để chứa dữ liệu cho biểu đồ Pie
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

            // Thêm từng phần tử vào biểu đồ Pie
            for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
                PieChart.Data data = new PieChart.Data(entry.getKey(), entry.getValue());

                // Gắn listener để chờ khi Node được render
                data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                    if (newNode != null) {
                        newNode.setStyle("-fx-opacity: 1;");
                    }
                });

                pieChartData.add(data);
            }

            // Cập nhật biểu đồ
            categoryPieChart.setData(pieChartData);
            categoryPieChart.setTitle("Biểu đồ chi tiêu");
        } catch (SQLException e) {
            e.printStackTrace();
            Utils.getAlert("Lỗi khi tải dữ liệu biểu đồ!").showAndWait();
        }
    }

    @FXML
    private void onExportToExcelClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu báo cáo");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                reportService.exportTransactionsToCSV(transactionTableView.getItems(), file.getAbsolutePath());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thành công");
                alert.setContentText("Xuất báo cáo thành công!");
                alert.showAndWait();
            } catch (IOException | SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setContentText("Không thể xuất báo cáo!");
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void handleViewReportByYear(ActionEvent event) {
        try {
            // Reload lại report1.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("report1.fxml"));
            Parent root = loader.load(); // Load lại FXML

            // Cập nhật lại Stage với scene mới
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show(); // Hiển thị lại Stage với giao diện mới

            // Nếu cần, có thể gọi lại phương thức loadTransactions() để cập nhật dữ liệu
            Report1Controller controller = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            Utils.getAlert("Lỗi khi tải lại báo cáo theo năm!").showAndWait();
        }
    }
    @FXML
    private void handleLogout() {
        try {
            // Đóng cửa sổ hiện tại
            Stage currentStage = (Stage) txtGreeting.getScene().getWindow();
            currentStage.close();

            // Mở lại cửa sổ đăng nhập
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            loginStage.setTitle("Đăng nhập");
            loginStage.setScene(new Scene(root));
            loginStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleAddTransaction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Secondary.fxml"));
            Parent secondaryRoot = loader.load();

            // Optional: truyền dữ liệu người dùng nếu cần trong SecondaryController
            SecondaryController controller = loader.getController();
            controller.setUser(Session.getCurrentUser());

            Stage stage = new Stage();
            stage.setTitle("Thêm giao dịch mới");
            stage.setScene(new Scene(secondaryRoot));
            stage.show();

            // Đóng cửa sổ hiện tại (Primary)
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
     @FXML
    private void handleOverview(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Primary.fxml"));
            Parent primaryRoot = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Tổng quan");
            stage.setScene(new Scene(primaryRoot));
            stage.show();

            // Đóng cửa sổ hiện tại
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
}
