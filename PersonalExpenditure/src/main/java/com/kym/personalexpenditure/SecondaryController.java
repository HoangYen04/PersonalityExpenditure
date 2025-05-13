package com.kym.personalexpenditure;

import com.kym.services.CheckData;
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
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
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
    @FXML
    private TableView<Transaction> tblTransaction;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
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
                dpDate.getEditor().addEventFilter(KeyEvent.ANY, Event::consume);

                this.loadColumns();
                this.loadTableData();
            } catch (SQLException ex) {
                Logger.getLogger(SecondaryController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (Exception e) {
            CheckData.check(e);
        }
    }
    private User currentUser;  //Truyền ng dùng cho primary

    public void setUser(User user) {
        this.currentUser = user;
    }

    public void handleSaveTransaction() {
        try {
            TransactionServices transactionServices = new TransactionServices();
            double amount = Utils.parseCurrency(tfAmount.getText());
            LocalDate date = dpDate.getValue();
            String des = tfDesc.getText();
            Category selectedCategory = categories.getSelectionModel().getSelectedItem();

            if (selectedCategory == null) {
                Utils.getAlert("Bạn cần chọn danh mục cho giao dịch!").showAndWait();
                return;
            }

            Transaction transaction = new Transaction();
            transaction.setAmount(amount);
            transaction.setDate(date);
            transaction.setCategoryId(selectedCategory.getCategoryId());
            transaction.setUserId(Session.getCurrentUser().getUserId());
            transaction.setDes(des);

            int result = transactionServices.addTransaction(transaction);

            switch (result) {
                case 1:
                    Utils.getAlert("Giao dịch đã được lưu thành công!").showAndWait();
                    clearInput();
                    loadTableData();
                    break;
                case -1:
                    Utils.getAlert("Danh mục không hợp lệ!").showAndWait();
                    break;
                case -2:
                    Utils.getAlert("Giao dịch vượt quá ngân sách của danh mục này!").showAndWait();
                    break;
                case -3:
                    Utils.getAlert("Danh mục này chưa có ngân sách. Vui lòng thiết lập ngân sách trước!").showAndWait();
                    openPrimaryWithSelectedCategory(selectedCategory);
                    break;
                default:
                    Utils.getAlert("Có lỗi xảy ra khi lưu giao dịch!").showAndWait();
                    break;
            }

        } catch (Exception e) {
            Utils.getAlert("Lỗi: " + e.getMessage()).showAndWait();
        }
    }

    private void openPrimaryWithSelectedCategory(Category category) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Primary.fxml"));
            Parent root = loader.load();

            // Truyền category qua controller của Primary
            PrimaryController controller = loader.getController();
            controller.setSelectedCategory(category); // Hàm này tạo bên PrimaryController để focus category

            Stage stage = new Stage();
            stage.setTitle("Tổng quan & Thiết lập ngân sách");
            stage.setScene(new Scene(root));
            stage.show();

            // Đóng màn hiện tại
            Stage currentStage = (Stage) categories.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            Utils.getAlert("Không thể chuyển đến màn hình Tổng quan!").showAndWait();
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
    private void handleOverview(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("primary.fxml"));
        Parent reportRoot = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Scene scene = new Scene(reportRoot);
        stage.setScene(scene);
        stage.show();
    }

    public void loadTableData() {
        TransactionServices s = new TransactionServices();
        try {
            // Get transactions with keyword filtering
            this.tblTransaction.setItems(FXCollections.observableList(s.getTransaction(Session.getCurrentUser().getUserId())));
        } catch (SQLException ex) {
            Logger.getLogger(SecondaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadColumns() {
        CategoryService c = new CategoryService();
        // Amount column
        TableColumn<Transaction, Double> colAmount = new TableColumn<>("Số tiền");
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colAmount.setPrefWidth(90);
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

        TableColumn<Transaction, String> colDate = new TableColumn<>("Ngày giao dịch:");
        colDate.setCellValueFactory(transaction -> {
            // Gọi phương thức getFormattedDate() để lấy ngày theo định dạng
            return new SimpleStringProperty(transaction.getValue().getFormattedDate());
        });
        colDate.setPrefWidth(150);

        TableColumn<Transaction, Integer> colCategoryId = new TableColumn<>("Danh mục");
        colCategoryId.setCellValueFactory(new PropertyValueFactory<>("categoryId"));
        colCategoryId.setPrefWidth(100);

        TableColumn<Transaction, String> colDes = new TableColumn<>("Mô tả");
        colDes.setCellValueFactory(new PropertyValueFactory<>("des"));
        colDes.setPrefWidth(100);

        TableColumn colDelete = new TableColumn();
        colDelete.setCellFactory(e -> {
            Button btn = new Button("Xóa");
            btn.setStyle("-fx-background-color: red; -fx-text-fill: white;");  // Màu nền đỏ, chữ trắng

            //Từ button  Lên cell  Cell lên cha là cái dòng (nguyên cái dòng là đối tượng Question)  Lấy id Question
            btn.setOnAction(evt -> {
                Transaction tr = (Transaction) ((TableRow) (((Button) evt.getSource()).getParent().getParent())).getItem();
                deleteColHandle(tr);
            });

            TableCell<Transaction, Void> cell = new TableCell<Transaction, Void>() {
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);

                    // Kiểm tra nếu là dòng trống thì không hiển thị nút
                    if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                        setGraphic(null);
                    } else {
                        setGraphic(btn);
                    }
                }
            };

            return cell;
        });

        TableColumn colEdit = new TableColumn();
        colEdit.setCellFactory(e -> {
            Button btnEdit = new Button("Sửa");
            btnEdit.setStyle("-fx-background-color: gray; -fx-text-fill: white;");  // Màu xám

            btnEdit.setOnAction(evt -> {
                Transaction selectedTransaction = ((TableRow<Transaction>) ((Button) evt.getSource()).getParent().getParent()).getItem();
                editColHandle(selectedTransaction);
            });

            TableCell<Transaction, Void> cell = new TableCell<Transaction, Void>() {
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);

                    // Kiểm tra nếu là dòng trống thì không hiển thị nút
                    if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                        setGraphic(null);
                    } else {
                        setGraphic(btnEdit);
                    }
                }
            };

            return cell;
        });
        colDelete.setPrefWidth(50);
        colEdit.setPrefWidth(50);

        this.tblTransaction.getColumns().addAll(colAmount, colDate, colCategoryId, colDes, colDelete, colEdit);
    }

    @FXML
    private void updateTransaction(int transactionId) {
        double amount = Utils.parseCurrency(tfAmount.getText());

        LocalDate date = dpDate.getValue();
        Category category = categories.getValue();
        String desc = tfDesc.getText();

        int userId = Session.getCurrentUser().getUserId();

        try {
            TransactionServices s = new TransactionServices();
            int result = s.updateTransaction(transactionId, amount, date, category.getCategoryId(), desc, userId);

            switch (result) {
                case 1:
                    Utils.getAlert("Giao dịch đã được cập nhật thành công!").showAndWait();
                    clearInput();
                    loadTableData();
                    break;
                case -1:
                    Utils.getAlert("Danh mục không hợp lệ!").showAndWait();
                    clearInput();
                    break;
                case -2:
                    Utils.getAlert("Giao dịch vượt quá ngân sách của danh mục này!").showAndWait();
                    clearInput();
                    break;
                case -3:
                    Utils.getAlert("Danh mục này chưa có ngân sách. Vui lòng thiết lập ngân sách trước!").showAndWait();
                    openPrimaryWithSelectedCategory(category);
                    break;
                case -4:
                    Utils.getAlert("Ngày giao dịch không hợp lệ!").showAndWait();
                    clearInput();
                    break;

                default:
                    Utils.getAlert("Có lỗi xảy ra khi lưu giao dịch!").showAndWait();
                    clearInput();
                    break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(SecondaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void clearInput() {
        tfAmount.clear();
        tfDesc.clear();
        categories.getSelectionModel().clearSelection();
        dpDate.setValue(LocalDate.now());

    }

    private void deleteColHandle(Transaction tr) {
        String id = tr.toString();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText("Bạn có chắc chắn muốn xóa giao dịch này?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Nếu người dùng chọn "OK", xóa giao dịch
                TransactionServices s = new TransactionServices();
                try {
                    s.deleteTransaction(id, Session.getCurrentUser().getUserId()); // Gọi phương thức xóa với id
                    Utils.getAlert("Xóa giao dịch thành công!").show();
                    loadTableData(); // Tải lại dữ liệu sau khi xóa
                } catch (SQLException ex) {
                    Logger.getLogger(SecondaryController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
            }
        });
    }

    private void editColHandle(Transaction selectedTransaction) {
        CategoryService c = new CategoryService();

        // Điền thông tin giao dịch vào các trường text
        DecimalFormat df = new DecimalFormat("#.##");  // Hiển thị tối đa 2 chữ số thập phân
        String amount = df.format(selectedTransaction.getAmount());  // Định dạng số tiền
        tfAmount.setText(amount);
        dpDate.setValue(selectedTransaction.getDate());

        try {
            categories.setValue(c.getCategoryById(selectedTransaction.getCategoryId()));  // Cần phải có hàm này để lấy category theo ID
        } catch (SQLException ex) {
            Logger.getLogger(SecondaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tfDesc.setText(selectedTransaction.getDes());

        // Thay đổi nút "Thêm giao dịch" thành "Sửa giao dịch"
        btnSave.setText("Sửa giao dịch");

        // Lưu lại ID giao dịch để biết là đang sửa giao dịch nào
        btnSave.setOnAction(saveEvent -> {
            updateTransaction(selectedTransaction.getTransactionId());  // Gọi hàm updateTransaction để sửa giao dịch
            btnSave.setText("Thêm giao dịch");
            btnSave.setOnAction(event -> handleSaveTransaction());
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
