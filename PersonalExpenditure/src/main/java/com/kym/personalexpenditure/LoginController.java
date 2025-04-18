package com.kym.personalexpenditure;

import com.kym.pojo.User;
import com.kym.services.UserServices;
import com.kym.services.Utils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Button btnLogin;

    private final UserServices userServices = new UserServices();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnLogin.setOnAction(evt -> handleLogin());
    }

    public void handleLogin() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        // Kiểm tra tên đăng nhập
        if (!userServices.isValidUsername(username)) {
            Utils.getAlert("Tên đăng nhập phải có ít nhất 6 ký tự và không được để trống.");
            return;
        }

        // Kiểm tra mật khẩu
        if (userServices.isPasswordEmpty(password)) {
            Utils.getAlert("Mật khẩu không được để trống.");
            return;
        }

        try {
            // Gọi service để kiểm tra đăng nhập
            User user = userServices.login(username, password);  // Chỉnh sửa ở đây
            if (user != null) {
                // Đăng nhập thành công, chuyển sang màn hình chính
                App.setRoot("primary");
            } else {
                Utils.getAlert("Tên đăng nhập hoặc mật khẩu không đúng!");
            }
        } catch (Exception e) {
            Utils.getAlert("Đã xảy ra lỗi: " + e.getMessage());
        }
    }

}
