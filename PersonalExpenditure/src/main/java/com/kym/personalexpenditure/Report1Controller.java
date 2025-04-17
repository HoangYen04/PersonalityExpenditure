/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.kym.personalexpenditure;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;

/**
 * FXML Controller class
 *
 * @author Yen My
 */
public class Report1Controller implements Initializable {
    
     DatePicker datePicker;

// Định dạng chỉ hiển thị tháng/năm
   
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
         setDateFormat(datePicker, "MM/yyyy");
        datePicker.setValue(LocalDate.now());

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
}
