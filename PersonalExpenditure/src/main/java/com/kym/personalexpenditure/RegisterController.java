/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.kym.personalexpenditure;

import com.kym.pojo.User;
import com.kym.services.UserServices;
import com.kym.services.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {

    @FXML
    private TextField txtEmailR;
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword1;
    @FXML
    private PasswordField txtPassword2;

    private final UserServices userServices = new UserServices();

    @FXML
    private void handleRegister() {
        String email = txtEmailR.getText().trim();
        String username = txtUsername.getText().trim();
        String password1 = txtPassword1.getText().trim();
        String password2 = txtPassword2.getText().trim();

        if (email.isEmpty() || username.isEmpty() || password1.isEmpty() || password2.isEmpty()) {
            Utils.getAlert("Vui lòng điền đầy đủ thông tin.").showAndWait();
            return;
        }

        if (!email.matches("^[\\w.-]+@gmail\\.com$")) {
            Utils.getAlert("Email không hợp lệ (phải có đuôi @gmail.com)").showAndWait();
            return;
        }
        if (!password1.matches("\\d+")) {
            // Thông báo lỗi: chỉ cho phép số
            Utils.getAlert("Mật khẩu chỉ được chứa chữ số.").showAndWait();
            return ;
        }

        if (!password1.equals(password2)) {
            Utils.getAlert("Mật khẩu xác nhận không khớp.").showAndWait();
            return;
        }

        if (username.length() < 6) {
            Utils.getAlert("Tên đăng nhập phải có ít nhất 6 ký tự.").showAndWait();
            return;
        }

        try {
            User user = new User();
            user.setName(username);
            user.setEmail(email);
            user.setPassword(password1);

            boolean success = userServices.registerUser(user);

            if (success) {
                Utils.getAlert("Đăng ký thành công!").showAndWait();
                handleLoginRedirect(); 
            } else {
                Utils.getAlert("Email đã tồn tại.").showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.getAlert("Có lỗi xảy ra khi đăng ký.").showAndWait();
        }
    }

    @FXML
    private void handleLoginRedirect() {
        try {
            // Tải file FXML của cửa sổ đăng nhập
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();

            // Tạo một cửa sổ mới
            Stage loginStage = new Stage();
            loginStage.setTitle("Đăng nhập");
            loginStage.setScene(new Scene(root));
            loginStage.show();

            // Đóng cửa sổ đăng ký
            Stage registerStage = (Stage) txtUsername.getScene().getWindow();
            registerStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
