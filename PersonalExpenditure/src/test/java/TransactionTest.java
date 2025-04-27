/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import com.kym.pojo.JdbcUtils;
import org.junit.jupiter.api.Test;
import com.kym.pojo.Transaction;
import com.kym.services.TransactionServices;
import java.sql.Connection;
import java.sql.PreparedStatement;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author ADMIN
 */
public class TransactionTest {

    private static TransactionServices transactionServices;

    @BeforeAll
    static void setup() {
        transactionServices = new TransactionServices();
    }

    @Test
    //Kiểm tra lấy giao dịch từ người dùng có bị rỗng không        
    void testGetTransaction() throws SQLException {
        int userId = 1; // ID của người dùng cần kiểm tra
        List<Transaction> transactions = transactionServices.getTransaction(userId);

        assertNotNull(transactions);
        assertFalse(transactions.isEmpty()); // Giả sử người dùng có giao dịch
    }

    @Test
    void testAddTransaction() throws SQLException {
        // Tạo giao dịch mẫu
        Transaction transaction = new Transaction(50, 10000, LocalDate.now(), 1, 1, "Test transaction");

        // Kiểm tra thêm giao dịch hợp lệ
        int result = transactionServices.addTransaction(transaction);
        assertEquals(1, result); // Kết quả -2 vì không thêm được vì vượt ngân sách

        // Kiểm tra thêm giao dịch với danh mục không hợp lệ
        transaction.setCategoryId(-1); // Sử dụng ID danh mục không hợp lệ
        result = transactionServices.addTransaction(transaction);
        assertEquals(-1, result); // Kết quả là -1 nếu danh mục không hợp lệ

        // Kiểm tra thêm giao dịch vượt quá ngân sách
        transaction.setCategoryId(1); // Giả sử danh mục 1 có ngân sách thấp hơn
        result = transactionServices.addTransaction(transaction);
        assertEquals(1, result); // Kết quả là 1 vì không vượt ngân sách

        // Kiểm tra thêm giao dịch khi chưa có ngân sách
        transaction.setCategoryId(4); // Giả sử danh mục 2 chưa có ngân sách
        result = transactionServices.addTransaction(transaction);
        assertEquals(-3, result); // Kết quả là -3 nếu chưa có ngân sách

        // Xóa luôn dựa vào description "test transaction"
        try ( Connection conn = JdbcUtils.getConn()) {
            String sql = "DELETE FROM transactions WHERE des = ? AND user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "test transaction");
            stmt.setInt(2, transaction.getUserId());
            stmt.executeUpdate();
        }

    }

    @Test
    //Test xóa giao dịch (nhớ đổi thông tin mỗi lần test)
    void testDeleteTransaction() throws SQLException {
        String transactionId = "15";
        int userId = 1;

        // Trước khi xóa, lấy thông tin giao dịch cần test
        Transaction deletedTransaction = null;
        List<Transaction> transactions = transactionServices.getTransaction(userId);
        for (Transaction t : transactions) {
            if (t.getTransactionId() == Integer.parseInt(transactionId)) {
                deletedTransaction = t;
                break;
            }
        }

        // Trước khi xóa, chắc chắn giao dịch tồn tại hay không trước
        assertTrue(transactions.stream().anyMatch(t -> t.getTransactionId() == Integer.parseInt(transactionId)));

        transactionServices.deleteTransaction(transactionId, userId);

        // Sau khi xóa, kiểm tra giao dịch đã xóa
        transactions = transactionServices.getTransaction(userId);
        assertFalse(transactions.stream().anyMatch(t -> t.getTransactionId() == Integer.parseInt(transactionId)));
        // Sau khi kiểm tra, khôi phục lại giao dịch đã xóa
        try ( Connection conn = JdbcUtils.getConn()) {
            String sql = "INSERT INTO transactions (transaction_id, amount, date, category_id, user_id, des) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, deletedTransaction.getTransactionId());
            stmt.setDouble(2, deletedTransaction.getAmount());
            stmt.setDate(3, java.sql.Date.valueOf(deletedTransaction.getDate()));
            stmt.setInt(4, deletedTransaction.getCategoryId());
            stmt.setInt(5, deletedTransaction.getUserId());
            stmt.setString(6, deletedTransaction.getDes());
            stmt.executeUpdate();
        }
    }


    @Test
    void testGetSpendingByCategoryAndUser() throws SQLException {
        int userId = 1;
        int categoryId = 1;

        double totalSpending = transactionServices.getSpendingByCategoryAndUser(userId, categoryId);
        assertTrue(totalSpending >= 0); // Kiểm tra tổng chi tiêu phải lớn hơn hoặc bằng 0
    }

}
