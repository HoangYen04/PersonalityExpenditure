package com.kym.personalexpenditure;

import com.kym.pojo.Category;
import com.kym.pojo.User;
import com.kym.services.CategoryService;
import com.kym.services.Session;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PrimaryController implements Initializable {

    @FXML
    private Text txtGreeting;  // Text để hiển thị lời chào
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Lấy thông tin người dùng từ Session
        if (Session.getCurrentUser() != null) {
            txtGreeting.setText("Chào, " + Session.getCurrentUser().getName());
        }
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

    @FXML
    private void handleAddTransaction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Secondary.fxml"));
            Parent secondaryRoot = loader.load();

            // Optional: truyền dữ liệu người dùng nếu cần trong SecondaryController
            SecondaryController controller = loader.getController();
            controller.setUser(Session.getCurrentUser());

            Stage stage = new Stage();
            stage.setTitle("Thêm giao dịch mới");
            stage.setScene(new Scene(secondaryRoot));
            stage.show();

            // Đóng cửa sổ hiện tại (Primary)
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
