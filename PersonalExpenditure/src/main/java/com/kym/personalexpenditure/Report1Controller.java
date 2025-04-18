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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

/**
 * FXML Controller class
 *
 * @author Yen My
 */
public class Report1Controller implements Initializable {

    @FXML
    DatePicker datePicker;
    @FXML
    private ComboBox<Integer> monthComboBox;
    @FXML
    private ComboBox<Integer> yearComboBox;
    @FXML
    private Button viewReportButton;
    private User currentUser;
    
    

    public void initialize() {
        currentUser = new User();
        currentUser.setUserId(1);
        // Tháng từ 1 đến 12
        for (int i = 1; i <= 12; i++) {
            monthComboBox.getItems().add(i);
        }

        // Năm từ 2020 đến hiện tại
        int currentYear = LocalDate.now().getYear();
        for (int y = 2020; y <= currentYear; y++) {
            yearComboBox.getItems().add(y);
        }

        // Mặc định là tháng và năm hiện tại
        monthComboBox.setValue(LocalDate.now().getMonthValue());
        yearComboBox.setValue(currentYear);

        viewReportButton.setOnAction(event -> {
            int month = monthComboBox.getValue();
            int year = yearComboBox.getValue();
            createMonthlyReport(month, year); // Gọi hàm xử lý báo cáo
        });
    }


    private void setDateFormat(DatePicker datePicker, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

        datePicker.setPromptText(pattern);
        datePicker.setConverter(new javafx.util.StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return (date != null) ? formatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                if (string == null || string.trim().isEmpty()) {
                    return null;
                }
                try {
                    // Ghép thêm ngày 01 để parse nếu người dùng chỉ nhập MM/yyyy
                    return LocalDate.parse("01/" + string, DateTimeFormatter.ofPattern("dd/" + pattern));
                } catch (Exception e) {
                    System.out.println("Lỗi định dạng: " + string);
                    return null;
                }
            }
        });
    }

    private void showAlert(String vui_lòng_chọn_tháng_và_năm) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    
    public void createMonthlyReport(int month, int year) {
    // Ngày bắt đầu và kết thúc của tháng
    LocalDate startDate = LocalDate.of(year, month, 1);
    LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

    // Kiểm tra không vượt quá ngày hiện tại
    if (startDate.isAfter(LocalDate.now())) {
        showAlert("Tháng được chọn nằm trong tương lai!");
        return;
    }

    // Giả sử có danh sách giao dịch người dùng đang đăng nhập
    List<Transaction> allTransactions = currentUser.getTransactions();

    // Lọc giao dịch trong khoảng thời gian
    List<Transaction> filtered = allTransactions.stream()
        .filter(t -> !t.getDate().isBefore(startDate) && !t.getDate().isAfter(endDate))
        .collect(Collectors.toList());

    if (filtered.isEmpty()) {
        showAlert("Không có giao dịch nào trong tháng này.");
        return;
    }

    // Tính tổng chi tiêu
    double total = filtered.stream()
        .mapToDouble(Transaction::getAmount)
        .sum();

    // Tính tổng theo từng danh mục (chartData)
    Map<String, Double> chartData = new HashMap<>();
    for (Transaction t : filtered) {
        String categoryName = findCategoryNameById(t.getCategoryId());
        chartData.put(categoryName, chartData.getOrDefault(categoryName, 0.0) + t.getAmount());
    }

    // Tạo đối tượng báo cáo
    Report report = new Report();
    report.setUserId(currentUser.getUserId());  // Giả sử currentUser là người đang đăng nhập
    report.setTransactions(transactions);
    report.setTotalSpent(total);
    report.setChartData(chartData);

    // Hiển thị báo cáo (ví dụ: in ra console)
    displayReport(report);
}

private String findCategoryNameById(int id) {
    if (currentUser != null && currentUser.getCategories() != null) {
        for (Category category : currentUser.getCategories()) {
            if (category.getUserId() == id) {
                return category.getName();
            }
        }
    }
    return "Không xác định";
}

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}

