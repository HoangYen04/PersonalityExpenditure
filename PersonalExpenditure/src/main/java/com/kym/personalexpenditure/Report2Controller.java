/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.kym.personalexpenditure;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author ADMIN
 */
public class Report2Controller implements Initializable {

    private int selectedMonth;
    private int selectedYear;

    public void setMonthYear(int month, int year) {
        this.selectedMonth = month;
        this.selectedYear = year;

        // Gọi hàm load dữ liệu hoặc vẽ biểu đồ tại đây nếu muốn
        loadReportData();
    }

    private void loadReportData() {
        // Tại đây bạn lấy dữ liệu theo tháng và năm đã truyền vào
        // Sau đó vẽ biểu đồ, ví dụ:
        System.out.println("Vẽ biểu đồ cho tháng " + selectedMonth + " năm " + selectedYear);
        // TODO: Truy vấn DB và hiển thị dữ liệu lên biểu đồ
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

}
