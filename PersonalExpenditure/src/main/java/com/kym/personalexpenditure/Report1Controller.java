/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.kym.personalexpenditure;

import com.kym.pojo.Transaction;
import com.kym.services.ReportService;
import com.kym.services.Session;
import com.kym.services.Utils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Yen My
 */
public class Report1Controller implements Initializable {

    @FXML
    private Text txtGreeting;
    @FXML
    private ComboBox<Integer> monthComboBox;
    @FXML
    private ComboBox<Integer> yearComboBox;

    @FXML
    private TableView<Transaction> transactionTableView;
    @FXML
    private PieChart YearPieChart;
    ReportService reportService = new ReportService();
    @FXML
    private Label totalSpendingLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Hiển thị lời chào
        if (Session.getCurrentUser() != null) {
            txtGreeting.setText("Chào, " + Session.getCurrentUser().getName());
        }
        // Thiết lập ComboBox tháng và năm
        setupMonthComboBox();
        setupYearComboBox();
        // Load dữ liệu mặc định
        int currentYear = LocalDate.now().getYear();
        loadTransactions(currentYear);
        loadPieChart(currentYear);
        // Load cột cho bảng TableView
        loadCol();
        // Gắn sự kiện thay đổi năm
        setupYearComboBoxListeners();
    }

    private void setupMonthComboBox() {
        monthComboBox.setEditable(false); // Không cho phép nhập vào ComboBox tháng
        for (int month = 1; month <= 12; month++) {
            monthComboBox.getItems().add(month);
        }
        monthComboBox.setValue(LocalDate.now().getMonthValue());
    }

    private void setupYearComboBox() {
        yearComboBox.setEditable(false); // Không cho phép nhập vào ComboBox năm
        int currentYear = LocalDate.now().getYear();
        for (int year = currentYear - 10; year <= currentYear; year++) {
            yearComboBox.getItems().add(year);
        }
        yearComboBox.setValue(currentYear); // Chọn năm hiện tại làm mặc định
    }

    private void setupYearComboBoxListeners() {
        // Khi chọn năm từ danh sách
        yearComboBox.setOnAction(event -> {
            Integer selectedYear = yearComboBox.getValue(); // Lấy năm đã chọn
            if (selectedYear != null) {
                handleYearInput(selectedYear); // Xử lý năm đã c

            }
        });
    }

    public void switchToReportScene(ActionEvent event, int selectedMonth, int selectedYear) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("report2.fxml"));
            Parent reportRoot = loader.load();

            Scene scene = new Scene(reportRoot);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Báo cáo tài chính");
            stage.show();
        } catch (IOException e) {
            showAlert("Lỗi", "Không thể tải giao diện báo cáo.");
            e.printStackTrace();
        }
    }

    private void handleYearInput(int selectedYear) {
        // Kiểm tra nếu năm hợp lệ
        if (selectedYear < 2015 || selectedYear > 2025) {
            totalSpendingLabel.setText("Không có giao dịch trong năm " + selectedYear);
            return;
        }
        // Cập nhật giá trị ComboBox
        yearComboBox.setValue(selectedYear);
        // Gọi lại các phương thức để load dữ liệu mới
        loadTransactions(selectedYear);
        loadPieChart(selectedYear);
    }

    public void loadPieChart(int year) {
        try {
            List<Transaction> transactions = reportService.getTransactionsByUserAndYear(year);
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
            YearPieChart.setData(pieChartData);
            YearPieChart.setTitle("Biểu đồ chi tiêu");
        } catch (SQLException e) {
            e.printStackTrace();
            Utils.getAlert("Lỗi khi tải dữ liệu biểu đồ!").showAndWait();
        }
    }

    public void loadTransactions(int year) {
        try {
            // Lấy danh sách giao dịch từ ReportService, chỉ theo năm
            List<Transaction> transactions = reportService.getTransactionsByUserAndYear(year);

            // Nếu không có giao dịch trong năm này, hiển thị thông báo
            if (transactions.isEmpty()) {
                totalSpendingLabel.setText("Không có giao dịch trong năm " + year);
                // Làm mới TableView
                transactionTableView.setItems(FXCollections.observableArrayList());  // Xóa hết các dòng trong TableView

                return;
            }

            // Tạo ObservableList để chứa giao dịch
            ObservableList<Transaction> transactionList = FXCollections.observableArrayList(transactions);

            // Tính tổng chi tiêu cho năm đã chọn
             double total = calculateTotalSpending(transactions);
            DecimalFormat currencyFormat = new DecimalFormat("#,###.00");
            String formattedTotal = currencyFormat.format(total);  // Định dạng thành chuỗi

            // Hiển thị kết quả
            totalSpendingLabel.setText("Tổng chi tiêu trong năm " + year + ": " + formattedTotal + " VNĐ");
            // Đổ dữ liệu vào TableView
            transactionTableView.setItems(transactionList);

        } catch (SQLException e) {
            e.printStackTrace();
            Utils.getAlert("Không thể lấy dữ liệu giao dịch từ cơ sở dữ liệu.").showAndWait();
        }
    }

    private double calculateTotalSpending(List<Transaction> transactions) {
        return transactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();
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
            try {
                // Lấy categoryId từ đối tượng Transaction
                int categoryId = cellData.getValue().getCategoryId();
                // Gọi phương thức từ ReportService để lấy tên danh mục
                String categoryName = reportService.getCategoryNameById(categoryId);
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

    @FXML
    private void handleViewReportClick(ActionEvent event) throws SQLException {

        // Lấy giá trị tháng và năm đã chọn từ ComboBox
        Integer selectedMonth = monthComboBox.getValue();
        Integer selectedYear = yearComboBox.getValue();

        try {
            // Kiểm tra xem tháng có hợp lệ không
            if (selectedMonth == null || selectedMonth < 1 || selectedMonth > 12) {
                showAlert("Lỗi", "Tháng phải nằm trong khoảng từ 1 đến 12!");
                return;
            }

            // Kiểm tra xem năm có hợp lệ không và không vượt quá năm hiện tại
            int currentYear = LocalDate.now().getYear();
            int currentMonth = LocalDate.now().getMonthValue();
            if (selectedYear == null || selectedYear > currentYear || (selectedYear == currentYear && selectedMonth > currentMonth)) {
                showAlert("Lỗi", "Tháng/năm không được nằm trong tương lai!");
                return;
            }

            // Gọi hàm tạo báo cáo
            checkTransactionsAndNavigate(selectedMonth, selectedYear, event);
        } catch (Exception ex) {
            showAlert("Lỗi", "Tháng và năm phải là số hợp lệ!");
            ex.printStackTrace();
        }
    }

    public void checkTransactionsAndNavigate(int month, int year, ActionEvent event) throws SQLException {
        try {
            List<Transaction> transactions = reportService.getTransactionsByUserAndMonth(month, year);

            if (transactions.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Không có giao dịch");
                alert.setHeaderText(null);
                alert.setContentText("Không có giao dịch trong tháng " + month + " năm " + year);
                alert.showAndWait();

                // Load lại trang hiện tại (report1.fxml)
                FXMLLoader loader = new FXMLLoader(getClass().getResource("report1.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
            } else {
                // Nếu có giao dịch thì chuyển đến report2.fxml và truyền tháng/năm
                FXMLLoader loader = new FXMLLoader(getClass().getResource("report2.fxml"));
                Parent reportRoot = loader.load();
                Report2Controller controller = loader.getController();
                controller.setMonthYear(month, year);  // Gọi setter để truyền dữ liệu
                Scene scene = new Scene(reportRoot);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Báo cáo tài chính");
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể xử lý giao dịch.");
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
//}
// Phương thức để hiển thị alert

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);  // Không hiển thị header
        alert.showAndWait();  // Hiển thị và chờ người dùng đóng alert
    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent reportRoot = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Scene scene = new Scene(reportRoot);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleAddTransaction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("secondary.fxml"));
        Parent reportRoot = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Scene scene = new Scene(reportRoot);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleOverview(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("primary.fxml"));
        Parent reportRoot = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Scene scene = new Scene(reportRoot);
        stage.setScene(scene);
        stage.show();

    }
}
