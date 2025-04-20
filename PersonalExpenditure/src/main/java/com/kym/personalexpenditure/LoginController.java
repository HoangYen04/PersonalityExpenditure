package com.kym.personalexpenditure;

import com.kym.pojo.User;
import com.kym.services.UserServices;
import com.kym.services.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;

    private final UserServices userServices = new UserServices();

    @FXML
    private void handleLogin() {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();

        // Kiểm tra đầu vào
        if (email.isEmpty() || password.isEmpty()) {
            Utils.getAlert("Email và mật khẩu không được để trống.").showAndWait();
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@gmail\\.com$")) {
            Utils.getAlert("Email không đúng định dạng.").showAndWait();
            return;
        }

        try {
            User user = userServices.loginByEmail(email, password);
            if (user != null) {
                Utils.getAlert("Đăng nhập thành công! Xin chào " + user.getName()).showAndWait();
                 // Khởi tạo một cửa sổ mới cho cảnh primary.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("primary.fxml"));
            Parent root = loader.load();
            PrimaryController controller = loader.getController();
            Scene primaryScene = new Scene(root);
            
            Stage primaryStage = new Stage(); // Tạo một cửa sổ mới
            primaryStage.setTitle("Primary Window");
            primaryStage.setScene(primaryScene);
            primaryStage.show();

            // Đóng cửa sổ đăng nhập
            Stage loginStage = (Stage) txtEmail.getScene().getWindow();
            loginStage.close();
            } else {
                Utils.getAlert("Sai email hoặc mật khẩu.").showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.getAlert("Lỗi kết nối cơ sở dữ liệu.");
        }
    }
    public void handleRegisterRedirect() {
        try {
            // Tải file FXML của cửa sổ đăng ký
            FXMLLoader loader = new FXMLLoader(getClass().getResource("register.fxml"));
            Parent root = loader.load();
            
            // Tạo một cửa sổ mới
            Stage registerStage = new Stage();
            registerStage.setTitle("Đăng ký");
            registerStage.setScene(new Scene(root));
            registerStage.show();
            
            // Đóng cửa sổ đăng nhập
            Stage loginStage = (Stage) txtEmail.getScene().getWindow();
            loginStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}   


