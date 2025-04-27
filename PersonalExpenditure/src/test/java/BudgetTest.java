/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import com.kym.pojo.Budget;
import com.kym.services.BudgetServices;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author ADMIN
 */
public class BudgetTest {
    private static Connection connection;

    BudgetServices budgetServices = new BudgetServices();
    
    @BeforeAll
    public static void setUp() throws SQLException {
        // Thiết lập kết nối với H2 database
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        
        // Tạo bảng giả lập
        Statement statement = connection.createStatement();
        statement.execute("CREATE TABLE Budget (" +
                "budget_id INT PRIMARY KEY, " +
                "user_id INT, " +
                "category_id INT, " +
                "amount DOUBLE, " +
                "category_name VARCHAR(255))");
        
        // Thêm dữ liệu giả lập
        statement.execute("INSERT INTO Budget (budget_id, user_id, category_id, amount, category_name) VALUES (1, 1, 1, 5000, 'Test Category')");
    }
    
     @AfterAll
    public static void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    //Kieemr tra có lấy đúng ngân sách của user hay không
    void testGetBudgetsByUserId_ValidUser() throws SQLException {
        List<Budget> budgets = budgetServices.getBudgetsByUserId(1);
        assertNotNull(budgets);
        assertTrue(budgets.size() > 0);
    }

    @Test
    //kiểm tra nếu user không tồn tại
    void testGetBudgetsByUserId_InvalidUser() throws SQLException {
        List<Budget> budgets = budgetServices.getBudgetsByUserId(9999);
        assertNotNull(budgets);
        assertEquals(0, budgets.size());
    }

    //Kiểm tra tính đúng tổng ngân sách
    @Test
    void testGetTotalBudgetByUserId_ValidUser() throws SQLException {
        List<Budget> budgets = budgetServices.getBudgetsByUserId(1);

        double expectedTotal = budgets.stream().mapToDouble(Budget::getAmount).sum(); // Tính tổng ngân sách từ từng mục

        double actualTotalBudget = budgetServices.getTotalBudgetByUserId(1);

        assertEquals(expectedTotal, actualTotalBudget, 0.01);
    }

    //nếu không có ngân sách thì phải là 0
    @Test
    void testGetTotalBudgetByUserId_InvalidUser() throws SQLException {
        double totalBudget = budgetServices.getTotalBudgetByUserId(9999);
        assertEquals(0, totalBudget);
    }

    //Lấy đúng ngân sách theo user và category.
    @Test
    void testGetBudgetByCategoryAndUser_Exist() throws SQLException {

        int expectedUserId = 1;
        int expectedCategoryId = 1;

        Budget b = budgetServices.getBudgetByCategoryAndUser(expectedUserId, expectedCategoryId);

        assertNotNull(b); //không rỗng
        assertEquals(expectedUserId, b.getUserId());

        // Nếu Budget có thuộc tính categoryId
        assertEquals(expectedCategoryId, b.getCategoryId());
    }

    //Kiểm tra nếu category không hợp lệ
    @Test
    void testGetBudgetByCategoryAndUser_NotExist() throws SQLException {
        Budget b = budgetServices.getBudgetByCategoryAndUser(1, 9999);
        assertNull(b);
    }

    //Kiểm tra cập nhật thành công 1 ngân sách có sẵn.
    @Test
    void testUpdateBudget() throws SQLException {
        Budget b = new Budget();
        b.setBudgetId(1);
        b.setUserId(1);
        b.setCategoryId(1);
        b.setAmount(500000);

        budgetServices.updateBudget(b);

        //Kiểm tra lại sau update để xem đã cập nhật đúng chưa
        Budget updated = budgetServices.getBudgetByCategoryAndUser(1, 1);
        assertTrue(updated.getAmount() >= 500000);
    }

    //Kiểm tra thêm ngân sách mới thành công.
    void testAddBudget() throws SQLException {
        Budget b = new Budget();
        b.setUserId(13);
        b.setCategoryId(2);
        b.setAmount(3000);
        b.setCategoryName("Test Category");

        budgetServices.addBudget(b);

        Budget added = budgetServices.getBudgetByCategoryAndUser(13, 2);
        assertNotNull(added);

    }
    @Test
    void testDeleteBudget() throws SQLException {
        // Arrange: thêm 1 ngân sách mới trước
        Budget b = new Budget();
        b.setUserId(1);
        b.setCategoryId(2);
        b.setAmount(5000);
        b.setCategoryName("Delete Test");

        budgetServices.addBudget(b);

        // Lấy lại cái budget vừa thêm
        Budget addedBudget = budgetServices.getBudgetByCategoryAndUser(1, 2);
        assertNotNull(addedBudget);

        // Act: Gọi delete
        budgetServices.deleteBudget(addedBudget.getBudgetId(), 1);

        // Assert: Kiểm tra lại bằng SELECT theo budget_id
        Budget deletedBudget = budgetServices.getBudgetById(addedBudget.getBudgetId()); // cần viết thêm hàm getBudgetById()
        assertNull(deletedBudget);

    }
}
