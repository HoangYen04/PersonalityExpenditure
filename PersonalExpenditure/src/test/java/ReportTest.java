/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import com.kym.pojo.JdbcUtils;
import com.kym.pojo.Transaction;
import com.kym.pojo.User;
import com.kym.services.ReportService;
import com.kym.personalexpenditure.Report1Controller;
import com.kym.services.CategoryService;
import com.kym.services.Session;
import java.lang.module.ModuleDescriptor;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Yen My
 */
public class ReportTest {

    ReportService rs = new ReportService();
    Report1Controller rp = new Report1Controller();

    @Test
    public void testGetTransactionsByUserAndMonth_UserId1() throws SQLException {
        int userId = 1;
        int month = 4;
        int year = 2025;

        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM transactions WHERE user_id = ? AND date >= ? AND date < ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.plusMonths(1);
            stm.setInt(1, userId);
            stm.setDate(2, java.sql.Date.valueOf(startDate));
            stm.setDate(3, java.sql.Date.valueOf(endDate));

            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Transaction t = new Transaction(
                        rs.getInt("transaction_id"),
                        rs.getDouble("amount"),
                        rs.getDate("date").toLocalDate(),
                        rs.getInt("category_id"),
                        rs.getInt("user_id"),
                        rs.getString("des")
                );
                transactions.add(t);
            }
        }

        assertNotNull(transactions);
        assertFalse(transactions.isEmpty());

        for (Transaction t : transactions) {
            assertEquals(1, t.getUserId());
            assertEquals(month, t.getDate().getMonthValue());
            assertEquals(year, t.getDate().getYear());
        }
    }

    @Test
    public void testGetTransactionsByUserAndYear_HasData() throws SQLException {
        // Thiết lập user giả lập (nếu bạn dùng Session mock được)
        User mockUser = new User();
        mockUser.setUserId(1); // userId 1 phải có dữ liệu trong DB năm 2025
        Session.setCurrentUser(mockUser);
        List<Transaction> transactions = rs.getTransactionsByUserAndYear(2025);

        assertNotNull(transactions);
        assertFalse(transactions.isEmpty(), "");

        for (Transaction t : transactions) {
            assertEquals(1, t.getUserId(), "Transaction: 1");
            assertEquals(2025, t.getDate().getYear(), "Transaction year: 2025");
        }
    }

    @Test
    void testCalculateTotalSpending_WithMultipleTransactions() {
        Transaction t1 = new Transaction();
        t1.setTransactionId(1);
        t1.setAmount(100000);
        t1.setDate(LocalDate.now());
        t1.setCategoryId(1);
        t1.setUserId(1);
        t1.setDes("Ăn sáng");

        Transaction t2 = new Transaction();
        t2.setTransactionId(2);
        t2.setAmount(50000);
        t2.setDate(LocalDate.now());
        t2.setCategoryId(2);
        t2.setUserId(1);
        t2.setDes("Đi xe bus");

        Transaction t3 = new Transaction();
        t3.setTransactionId(3);
        t3.setAmount(200000);
        t3.setDate(LocalDate.now());
        t3.setCategoryId(3);
        t3.setUserId(1);
        t3.setDes("Mua đồ dùng");

        // Danh sách các giao dịch
        List<Transaction> transactions = Arrays.asList(t1, t2, t3);

        // Tính tổng chi tiêu mà không cần controller
        double totalSpending;
        totalSpending = rp.calculateTotalSpending(transactions);

        // Kiểm tra kết quả
        assertEquals(350000, totalSpending, 0.01);
    }

    @Test
    void testExportTransactionsToCSV_Success() throws Exception {
        // Arrange: chuẩn bị dữ liệu
        Transaction t1 = new Transaction();
        LocalDate date = LocalDate.of(2025, 4, 25);
        t1.setDate(date);
        t1.setCategoryId(1);  // Danh mục "Ăn uống"
        t1.setAmount(150000.0);

        Transaction t2 = new Transaction();
        LocalDate datee = LocalDate.of(2025, 4, 26);
        t2.setDate(datee);
        t2.setCategoryId(2);  // Danh mục "Đi lại"
        t2.setAmount(80000.0);

        List<Transaction> transactions = Arrays.asList(t1, t2);
        String tempFilePath = "test_transactions.csv";

        // Tạo giả CategoryService
        CategoryService categoryService = new CategoryService() {
            @Override
            public String getCategoryNameById(int categoryId) {
                if (categoryId == 1) {
                    return "Ăn uống";
                } else if (categoryId == 2) {
                    return "Đi lại";
                } else {
                    return "Không có tên danh mục";
                }
            }
        };

        // Tạo ReportService và gán categoryService vào
        ReportService reportService = new ReportService();

        // Act: gọi phương thức cần test
        reportService.exportTransactionsToCSV(transactions, tempFilePath);
        }}