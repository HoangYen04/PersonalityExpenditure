/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kym.services;

/**
 *
 * @author Admin
 */
// CheckData.java
// CheckData.java
// CheckData.java
// CheckData.java
public class CheckData {
    public static void check(Exception e) {
        if (e instanceof IllegalArgumentException) {
            // Hiển thị thông báo lỗi
            showErrorDialog("Dữ liệu trong CSDL của bạn bị lỗi.\nVui lòng xuất file log và gửi cho quản trị viên!");

            // Ghi log lỗi (tùy bạn muốn log ở đâu)
            logErrorToFile(e);
        } else {
            // Nếu không phải IllegalArgumentException, thì xử lý bình thường hoặc ném lại
            throw new RuntimeException(e);
        }
    }

    private static void showErrorDialog(String message) {
        // JavaFX ví dụ: 
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Lỗi dữ liệu");
            alert.setHeaderText(null);
            alert.setContentText(message);

            // Thêm nút "Xuất file log" (bằng ButtonType)
            javafx.scene.control.ButtonType exportLogButton = new javafx.scene.control.ButtonType("Xuất file log");
            alert.getButtonTypes().add(exportLogButton);

            alert.showAndWait().ifPresent(response -> {
                if (response == exportLogButton) {
                    // Nếu người dùng nhấn "Xuất file log"
                    exportLog();
                }
            });
        });
    }

    private static void logErrorToFile(Exception e) {
        try {
            java.io.FileWriter fw = new java.io.FileWriter("error_log.txt", true);
            fw.write(java.time.LocalDateTime.now() + " - " + e.toString() + "\n");
            for (StackTraceElement ste : e.getStackTrace()) {
                fw.write("\tat " + ste.toString() + "\n");
            }
            fw.write("\n");
            fw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void exportLog() {
        // Ở đây bạn có thể mở file explorer, hoặc copy file log ra cho người dùng.
        System.out.println("Xuất file log tại: error_log.txt");
    }
}

