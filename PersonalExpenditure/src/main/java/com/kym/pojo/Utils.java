/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kym.pojo;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.function.UnaryOperator;
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

            // Nếu không phải số hoặc rỗng thì từ chối
            if (newText.isEmpty()) {
                return change;
            }

            try {
                long value = Long.parseLong(newText);

                if (value < 0) {
                    return null; // Từ chối số âm
                }

                // Cập nhật lại text đã định dạng
                String formatted = numberFormat.format(value);
                change.setText(formatted);
                int caretPosition = change.getControlNewText().length();
                change.setRange(0, change.getControlText().length());
                change.selectRange(caretPosition, caretPosition);
                return change;
            } catch (NumberFormatException e) {
                return null;
            }
        };

        TextFormatter<String> formatter = new TextFormatter<>(filter);
        textField.setTextFormatter(formatter);
    }

    public static long parseFormattedNumber(String formatted) {
        return Long.parseLong(formatted.replace(".", "").replace(",", ""));
    }
}
