package com.kym.personalexpenditure;

import com.kym.pojo.User;
import com.kym.services.Session;
import com.kym.services.UserServices;
import com.kym.services.Utils;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField txtEmail;
    @FXML
    private PasswordField txtPassword;

    private final UserServices userServices = new UserServices();

    @FXML
    private void handleLogin() {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();

        int result = userServices.loginByEmail(email, password);

        switch (result) {
            case 1:
                User currentUser = Session.getCurrentUser();
                Utils.getAlert("Đăng nhập thành công! Xin chào " + currentUser.getName()).showAndWait();
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("primary.fxml"));
                    Parent root = loader.load();
                    Scene primaryScene = new Scene(root);
                    Stage primaryStage = new Stage();
                    primaryStage.setTitle("Primary Window");
                    primaryStage.setScene(primaryScene);
                    primaryStage.show();

                    // Đóng cửa sổ đăng nhập
                    Stage loginStage = (Stage) txtEmail.getScene().getWindow();
                    loginStage.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Utils.getAlert("Lỗi khi tải giao diện chính.").showAndWait();
                }
                break;
            case -1:
                Utils.getAlert("Email và mật khẩu không được để trống.").showAndWait();
                break;
            case -2:
                Utils.getAlert("Email không đúng định dạng.").showAndWait();
                break;
            case -3:
                Utils.getAlert("Sai mật khẩu.").showAndWait();
                break;
            case -4:
                Utils.getAlert("Email không tồn tại.").showAndWait();
                break;
            default:
                Utils.getAlert("Lỗi kết nối cơ sở dữ liệu.").showAndWait();
                break;
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
