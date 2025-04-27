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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
        try {
            BudgetServices budgetService = new BudgetServices();
            budgetService.resetExpiredBudgets();
        } catch (SQLException e) {
            Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, e);
        }

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
            Utils.setupCurrencyTextField(tfAmount);
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
        TableColumn<Budget, String> colCate = new TableColumn<>("Danh mục");
        colCate.setPrefWidth(100);
        colCate.setCellValueFactory(new PropertyValueFactory<>("categoryName"));

        TableColumn<Budget, Double> colAmount = new TableColumn<>("Ngân sách");
        colAmount.setPrefWidth(100);
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colAmount.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.0f", item)); // hoặc %,.2f nếu muốn hiện .00
                }
            }
        });

        TableColumn<Budget, Void> colDelete = new TableColumn<>("Xóa");
        colDelete.setCellFactory(e -> {
            Button btn = new Button("Xóa");
            btn.setStyle("-fx-background-color: red; -fx-text-fill: white;");  // Màu nền đỏ, chữ trắng

            // Lấy đối tượng Budget từ dòng (row) khi nhấn nút xóa
            btn.setOnAction(evt -> {
                Budget bg = (Budget) ((TableRow) ((Button) evt.getSource()).getParent().getParent()).getItem();  // Lấy đối tượng Budget từ TableRow
                deleteCelHandle(bg);
            });

            TableCell<Budget, Void> cell = new TableCell<Budget, Void>() {
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);

                    // Kiểm tra nếu là dòng trống thì không hiển thị nút
                    if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                        setGraphic(null);
                    } else {
                        setGraphic(btn); // Nếu có dữ liệu, hiển thị nút
                    }
                }
            };

            return cell;
        });
        colDelete.setPrefWidth(50);

        
        this.tblBudgets.getColumns().addAll(colCate, colAmount, colDelete);
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
    private void handleLogout(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent reportRoot = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Scene scene = new Scene(reportRoot);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleAddTransaction(ActionEvent event) throws IOException {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("secondary.fxml"));
        Parent reportRoot = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Scene scene = new Scene(reportRoot);
        stage.setScene(scene);
        stage.show();
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
          
            double newBudgetAmount = Utils.parseCurrency(tfAmount.getText());
            int userId = Session.getCurrentUser().getUserId();

            // Kiểm tra tổng chi tiêu và ngân sách hiện tại
            double currentTotalSpending = transactionServices.getTotalSpendingInCurrentMonth(userId);
            double totalBudget = budgetServices.getTotalBudgetByUserId(userId);
            double currentCategorySpending = transactionServices.getSpendingByCategoryAndUser(userId, category.getCategoryId());
            Budget existingBudget = budgetServices.getBudgetByCategoryAndUser(userId, category.getCategoryId());

            if (existingBudget != null) {

                // Cập nhật ngân sách
                
                double totalUpdatedBudget = totalBudget + newBudgetAmount - existingBudget.getAmount();
                if (totalUpdatedBudget < currentTotalSpending) {
                    Utils.getAlert("Tổng ngân sách phải lớn hơn tổng chi tiêu!").showAndWait();
                    return;
                }
                
                if (newBudgetAmount <= 0){
                    budgetServices.deleteBudget(existingBudget.getBudgetId(), userId);
                    Utils.getAlert("Ngân sách đã bị xóa!").showAndWait();
                    loadData();
                    return;
                }
                
               
                existingBudget.setAmount(newBudgetAmount);
                budgetServices.updateBudget(existingBudget);
                Utils.getAlert("Cập nhật ngân sách thành công!").showAndWait();
            } else {
                // Nếu chưa có ngân sách, thêm ngân sách mới
                if ((totalBudget + newBudgetAmount) < currentTotalSpending) {
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
            tfAmount.clear();
        } catch (SQLException ex) {
            ex.printStackTrace();
            Utils.getAlert("Đã có lỗi xảy ra khi thêm hoặc cập nhật ngân sách!").showAndWait();
        }
    }


    
    private void deleteCelHandle(Budget bg){
    int id = bg.getBudgetId();  // Lấy ID của Budget, dùng phương thức getBudgetId()

    // Hiển thị cảnh báo xác nhận xóa
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Xác nhận xóa");
    alert.setHeaderText("Bạn có chắc chắn muốn xóa ngân sách này?");
    alert.showAndWait().ifPresent(response -> {
    if (response == ButtonType.OK) {
        try {
            // Lấy thông tin chi tiêu hiện tại và ngân sách
            double currentTotalSpending = transactionServices.getTotalSpendingInCurrentMonth(Session.getCurrentUser().getUserId());
            double totalBudget = budgetServices.getTotalBudgetByUserId(Session.getCurrentUser().getUserId());

            // Lấy ngân sách hiện tại
            Budget existingBudget = budgetServices.getBudgetByCategoryAndUser(Session.getCurrentUser().getUserId(), bg.getCategoryId());
            if (existingBudget != null) {
                double totalUpdatedBudget = totalBudget - existingBudget.getAmount();  // Cập nhật lại tổng ngân sách sau khi xóa

            // Kiểm tra điều kiện tổng ngân sách phải lớn hơn tổng chi tiêu
            if (totalUpdatedBudget <= currentTotalSpending) {
                Utils.getAlert("Tổng ngân sách phải lớn hơn tổng chi tiêu! Không thể xóa").showAndWait();
                return;
            }

            // Xóa ngân sách
            budgetServices.deleteBudget(id, Session.getCurrentUser().getUserId());
            Utils.getAlert("Xóa ngân sách thành công!").show();
            loadData();  // Cập nhật lại giao diện
            }

            } catch (SQLException ex) {
                Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
                Utils.getAlert("Đã có lỗi xảy ra khi xóa ngân sách!").showAndWait();
            }
        } else {            
        }
      });
    }

    @FXML
    private void handleReportButtonClick(ActionEvent event) throws IOException {
        // Tải giao diện report.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("report1.fxml"));
        Parent reportRoot = loader.load();

        // Lấy Stage hiện tại từ sự kiện
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Tạo Scene mới và đặt vào Stage
        Scene scene = new Scene(reportRoot);
        stage.setScene(scene);
        stage.show();
    }


}
