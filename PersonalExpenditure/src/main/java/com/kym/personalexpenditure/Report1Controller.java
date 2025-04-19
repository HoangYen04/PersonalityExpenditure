/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.kym.personalexpenditure;

import com.kym.pojo.Transaction;
import com.kym.pojo.User;
import com.kym.pojo.Category;
import com.kym.pojo.Report;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

/**
 * FXML Controller class
 *
 * @author Yen My
 */
public class Report1Controller implements Initializable {

    @FXML
    private ComboBox<String> monthComboBox;
    @FXML
    private ComboBox<String> yearComboBox;

    @FXML
    private Button viewReportButton;

    private User currentUser;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        monthComboBox.setEditable(true);
        yearComboBox.setEditable(true);

        // Thêm tháng vào ComboBox dưới dạng String
        for (int month = 1; month <= 12; month++) {
            monthComboBox.getItems().add(String.valueOf(month)); // Lưu dưới dạng chuỗi
        }

        int currentYear = LocalDate.now().getYear();
        // Thêm năm vào ComboBox dưới dạng String
        for (int year = currentYear - 10; year <= currentYear; year++) {
            yearComboBox.getItems().add(String.valueOf(year)); // Lưu dưới dạng chuỗi
        }

        // Gắn sự kiện cho nút Xem Báo Cáo
        viewReportButton.setOnAction(e -> {
            handleViewReportClick();
        });
    }

    @FXML
    private void handleViewReportClick() {
        String monthText = monthComboBox.getEditor().getText();
        String yearText = yearComboBox.getEditor().getText();
        if (monthText == null || monthText.isEmpty() || yearText == null || yearText.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập đầy đủ tháng và năm.");
            return;
        }
        try {
            // Convert tháng và năm từ String sang Integer
            int selectedMonth = Integer.parseInt(monthText);
            int selectedYear = Integer.parseInt(yearText);

            // Kiểm tra hợp lệ tháng
            if (selectedMonth < 1 || selectedMonth > 12) {
                showAlert("Lỗi", "Tháng phải nằm trong khoảng từ 1 đến 12!");
                return;
            }

            int currentYear = LocalDate.now().getYear();
            int currentMonth = LocalDate.now().getMonthValue();

            // Kiểm tra hợp lệ năm và tháng
            if (selectedYear > currentYear || (selectedYear == currentYear && selectedMonth > currentMonth)) {
                showAlert("Lỗi", "Tháng/năm không được nằm trong tương lai!");
                return;
            }

            // Gọi hàm tạo báo cáo
//            generateReport(selectedMonth, selectedYear);
        } catch (NumberFormatException ex) {
            showAlert("Lỗi", "Vui lòng nhập đúng định dạng số cho tháng và năm.");
        }
    }

// Phương thức để hiển thị alert
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);  // Không hiển thị header
        alert.showAndWait();  // Hiển thị và chờ người dùng đóng alert
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    private User getDefaultUser() {
        // Tạo user mặc định
        User defaultUser = new User();
        defaultUser.setUserId(1);
        defaultUser.setName("default_user");
        defaultUser.setPassword("123456"); // có thể không cần thiết nếu không kiểm tra đăng nhập
        return defaultUser;
    }
}
