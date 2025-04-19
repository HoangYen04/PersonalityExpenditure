package com.kym.personalexpenditure;

import com.kym.pojo.Category;
import com.kym.services.CategoryService;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PrimaryController {
    @FXML
    private Text txtGreeting;  // Text để hiển thị lời chào

    // Phương thức này sẽ được gọi từ LoginController
    public void setUserName(String userName) {
        // Cập nhật nội dung lời chào
        txtGreeting.setText("Chào " + userName);
    }
     @FXML
    private void handleLogout() {
        try {
            // Đóng cửa sổ hiện tại
            Stage currentStage = (Stage) txtGreeting.getScene().getWindow();
            currentStage.close();

            // Mở lại cửa sổ đăng nhập
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            loginStage.setTitle("Đăng nhập");
            loginStage.setScene(new Scene(root));
            loginStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
