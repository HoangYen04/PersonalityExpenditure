package com.kym.personalexpenditure;

import com.kym.pojo.Budget;
import com.kym.pojo.Category;
import com.kym.pojo.User;
import com.kym.services.BudgetServices;
import com.kym.services.CategoryService;
import com.kym.services.Session;
import com.kym.services.TransactionServices;
import com.kym.services.Utils;
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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PrimaryController implements Initializable {

    @FXML
    private Text txtGreeting;  // Text để hiển thị lời chào
    @FXML
    private Text txtTotalSpending;
    @FXML
    private Text txtWarning;
    @FXML
    private Text txtTotalSpending1;
    @FXML
    private TextField tfAmount;
    @FXML
    private TableView<Budget> tblBudgets;
    @FXML
    private ComboBox<Category> categories;
    private BudgetServices budgetServices = new BudgetServices();
    private TransactionServices transactionServices = new TransactionServices();
    private Category selectedCategory;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Lấy thông tin người dùng từ Session
        if (Session.getCurrentUser() != null) {
            txtGreeting.setText("Chào, " + Session.getCurrentUser().getName());
        }
        if (selectedCategory != null) {
            categories.getSelectionModel().select(selectedCategory);
            categories.requestFocus(); // Focus comboBox
        }
        CategoryService c = new CategoryService();

        try {
            List<Category> cates = c.getCates();
            this.categories.setItems(FXCollections.observableList(cates));
            TransactionServices transactionServices = new TransactionServices();
            double totalSpending = transactionServices.getTotalSpendingInCurrentMonth(Session.getCurrentUser().getUserId());

            txtTotalSpending.setText(String.format("%,.0f VNĐ", totalSpending)); // Định dạng số
        } catch (SQLException ex) {
            Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
        loadCol();
        loadData();
        loadTotalBudget();
        checkSpendingWarning();
    }

    public void setSelectedCategory(Category category) {
        this.selectedCategory = category;
        javafx.application.Platform.runLater(() -> {
            categories.getSelectionModel().select(category);
            categories.requestFocus();
        });
    }

    public void loadData() {
        try {
            List<Budget> budgets = budgetServices.getBudgetsByUserId(Session.getCurrentUser().getUserId());
            tblBudgets.setItems(FXCollections.observableList(budgets));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadCol() {
        TableColumn colCate = new TableColumn("Danh mục");
        colCate.setPrefWidth(200);
        colCate.setCellValueFactory(new PropertyValueFactory<>("categoryName"));

        TableColumn colAmount = new TableColumn("Ngân sách");
        colAmount.setPrefWidth(200);
        colAmount.setCellValueFactory(new PropertyValueFactory("amount"));

        this.tblBudgets.getColumns().addAll(colCate, colAmount);
    }

    private void loadTotalBudget() {
        try {
            double totalBudget = budgetServices.getTotalBudgetByUserId(Session.getCurrentUser().getUserId());
            txtTotalSpending1.setText(String.format("%,.0f VNĐ", totalBudget));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void checkSpendingWarning() {
        try {
            double totalSpending = transactionServices.getTotalSpendingInCurrentMonth(Session.getCurrentUser().getUserId());
            double totalBudget = budgetServices.getTotalBudgetByUserId(Session.getCurrentUser().getUserId());

            if (totalBudget > 0 && totalSpending >= 0.8 * totalBudget) {
                txtWarning.setOpacity(1.0);  // Hiện cảnh báo
            } else {
                txtWarning.setOpacity(0.0);  // Ẩn cảnh báo
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
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

    @FXML
    private void handleAddBudget(ActionEvent event) {
        Category selectedCategory = categories.getValue();
        try {
            Category category = categories.getSelectionModel().getSelectedItem();
            if (category == null) {
                Utils.getAlert("Vui lòng chọn danh mục!").showAndWait();
                return;
            }
            String amountText = tfAmount.getText().trim();
            if (amountText.isEmpty()) {
                Utils.getAlert("Vui lòng nhập số tiền ngân sách!").showAndWait();
                return;
            }

            if (!amountText.matches("\\d+(\\.\\d+)?")) {
                Utils.getAlert("Số tiền không hợp lệ! Chỉ nhập số.").showAndWait();
                return;
            }

            double newBudgetAmount = Utils.parseCurrency(tfAmount.getText());
            int userId = Session.getCurrentUser().getUserId();

            // Kiểm tra tổng chi tiêu và ngân sách hiện tại
            double currentTotalSpending = transactionServices.getTotalSpendingInCurrentMonth(userId);
            double totalBudget = budgetServices.getTotalBudgetByUserId(userId);
            double currentCategorySpending = transactionServices.getSpendingByCategoryAndUser(userId, category.getCategoryId());
            Budget existingBudget = budgetServices.getBudgetByCategoryAndUser(userId, category.getCategoryId());

            if (existingBudget != null) {
                // Nếu đã có ngân sách cho danh mục, kiểm tra điều kiện chỉnh sửa
                if (newBudgetAmount <= existingBudget.getAmount()) {
                    Utils.getAlert("Ngân sách mới phải lớn hơn ngân sách cũ!").showAndWait();
                    return;
                }

                // Cập nhật ngân sách
                double totalUpdatedBudget = totalBudget + newBudgetAmount - existingBudget.getAmount();
                if (totalUpdatedBudget <= currentTotalSpending) {
                    Utils.getAlert("Tổng ngân sách phải lớn hơn tổng chi tiêu!").showAndWait();
                    return;
                }

                existingBudget.setAmount(newBudgetAmount);
                budgetServices.updateBudget(existingBudget);
                Utils.getAlert("Cập nhật ngân sách thành công!").showAndWait();
            } else {
                // Nếu chưa có ngân sách, thêm ngân sách mới
                if ((totalBudget + newBudgetAmount) <= currentTotalSpending) {
                    Utils.getAlert("Tổng ngân sách phải lớn hơn tổng chi tiêu!").showAndWait();
                    return;
                }

                String categoryName = selectedCategory.getName();
                int categoryId = selectedCategory.getCategoryId();

                Budget newBudget = new Budget();
                newBudget.setAmount(newBudgetAmount);
                newBudget.setUserId(userId);
                newBudget.setCategoryId(category.getCategoryId());
                newBudget.setCategoryName(categoryName);
                budgetServices.addBudget(newBudget);
                Utils.getAlert("Thêm ngân sách thành công!").showAndWait();
            }

            // Cập nhật lại giao diện
            loadData();
            loadTotalBudget();
        } catch (SQLException ex) {
            ex.printStackTrace();
            Utils.getAlert("Đã có lỗi xảy ra khi thêm hoặc cập nhật ngân sách!").showAndWait();
        }
    }

}
