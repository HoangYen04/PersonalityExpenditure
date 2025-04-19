package com.kym.personalexpenditure;

import com.kym.pojo.Category;
import com.kym.pojo.Transaction;
import com.kym.pojo.User;
import com.kym.services.Utils;
import com.kym.services.CategoryService;
import com.kym.services.Session;
import com.kym.services.TransactionServices;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class SecondaryController implements Initializable {

    @FXML
    private ComboBox<Category> categories;
    @FXML
    private TextField tfAmount;
    @FXML
    private DatePicker dpDate;
    @FXML
    private TextField tfDesc;
    @FXML
    private Button btnSave;
    @FXML
    private Text txtGreeting;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (Session.getCurrentUser() != null) {
            txtGreeting.setText("Chào, " + Session.getCurrentUser().getName());
        }
        CategoryService c = new CategoryService();
        try {
            List<Category> cates = c.getCates();
            this.categories.setItems(FXCollections.observableList(cates));
            Utils.setupCurrencyTextField(tfAmount);
            dpDate.setValue(LocalDate.now());

            // Thiết lập ngày không cho phép chọn quá ngày hiện tại
            dpDate.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    // Chỉ cho phép chọn ngày nhỏ hơn hoặc bằng ngày hiện tại
                    setDisable(empty || date.isAfter(LocalDate.now()));
                }
            });
        } catch (SQLException ex) {
            Logger.getLogger(SecondaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private User currentUser;

    public void setUser(User user) {
        this.currentUser = user;
    }

    public void handleSaveTransaction() {
        try {
            TransactionServices transactionServices = new TransactionServices();
            double amount = Utils.parseCurrency(tfAmount.getText());
            LocalDate date = dpDate.getValue();
            Category selectedCategory = categories.getSelectionModel().getSelectedItem();
            String description = tfDesc.getText();

            if (selectedCategory == null) {
                // Thông báo nếu không chọn danh mục
                Utils.getAlert("Bạn cần chọn danh mục cho giao dịch!").showAndWait();
                return;
            }

            Transaction transaction = new Transaction();
            transaction.setAmount(amount);
            transaction.setDate(date);
            transaction.setCategoryId(selectedCategory.getCategoryId());
            transaction.setUserId(Session.getCurrentUser().getUserId());

            if (transactionServices.addTransaction(transaction)) {
                Utils.getAlert("Giao dịch đã được lưu thành công!").showAndWait();

                tfAmount.clear();
                tfDesc.clear();
                categories.getSelectionModel().clearSelection();
                dpDate.setValue(LocalDate.now());
            } else {
                Utils.getAlert("Giao dịch không hợp lệ!").showAndWait();
            }
        } catch (Exception e) {
            Utils.getAlert("Đã có lỗi xảy ra: " + e.getMessage()).showAndWait();
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
    private void handleOverview(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Primary.fxml"));
            Parent primaryRoot = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Tổng quan");
            stage.setScene(new Scene(primaryRoot));
            stage.show();

            // Đóng cửa sổ hiện tại
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
