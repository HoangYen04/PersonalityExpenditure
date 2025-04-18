/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kym.services;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.function.UnaryOperator;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

/**
 *
 * @author ADMIN
 */
public class Utils {

    public static javafx.scene.control.Alert getAlert(String content) {
        return new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION, content, ButtonType.OK);
    }

    public static void setupCurrencyTextField(TextField textField) {
        NumberFormat numberFormat = new DecimalFormat("#,###");

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText().replace(".", "").replace(",", "");

            // Nếu người dùng chỉ nhập dấu "-" mà chưa nhập số, cho phép
            if (newText.isEmpty() || newText.equals("-")) {
                return change;
            }

            try {
                long value = Long.parseLong(newText);

                // Kiểm tra nếu là số âm
                if (value < 0) {
                    // Hiện cảnh báo nếu là số âm
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Cảnh báo");
                    alert.setHeaderText(null);
                    alert.setContentText("Không được nhập số âm!");
                    alert.showAndWait();

                    return null; // Không cho nhập số âm
                }

                // Định dạng lại số khi hợp lệ
                String formatted = numberFormat.format(value);
                change.setText(formatted);

                // Tính lại vị trí caret an toàn
                int caretPosition = Math.min(formatted.length(), change.getControlNewText().length());
                int originalLength = change.getControlText().length();
                change.setRange(0, originalLength);

                if (caretPosition >= 0 && caretPosition <= formatted.length()) {
                    change.selectRange(caretPosition, caretPosition);
                }

                return change;
            } catch (NumberFormatException e) {
                return null;
            }
        };

        TextFormatter<String> formatter = new TextFormatter<>(filter);
        textField.setTextFormatter(formatter);
    }

    public static double parseCurrency(String input) throws NumberFormatException {
        return Double.parseDouble(input.replace(",", "").replace(".", ""));
    }
}
