import com.kym.pojo.Category;
import com.kym.pojo.JdbcUtils;
import com.kym.services.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CategoryServiceTest {

    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryService();
    }

    @Test
    void testGetCates() throws SQLException {
        // Kiểm tra danh sách các danh mục trong cơ sở dữ liệu
        List<Category> categories = categoryService.getCates();

        // Kiểm tra rằng ít nhất có một danh mục
        assertNotNull(categories);
        assertTrue(categories.size() > 0);

        // Kiểm tra dữ liệu danh mục đầu tiên
        Category category = categories.get(0);
        assertNotNull(category);
        assertNotNull(category.getCategoryId());
        assertNotNull(category.getName());
    }

    @Test
    void testGetCategoryById() throws SQLException {
        // Kiểm tra lấy danh mục theo ID
        Category category = categoryService.getCategoryById(1);

        assertNotNull(category);
        assertEquals(1, category.getCategoryId());
        assertEquals("Ăn uống", category.getName());

    }

    @Test
    void testGetCategoryNameById() throws SQLException {
        // Chèn một danh mục mẫu vào cơ sở dữ liệu kiểm thử
        insertTestCategory();

        // Kiểm tra lấy tên danh mục theo ID
        String categoryName = categoryService.getCategoryNameById(5);

        assertEquals("Category 5", categoryName);

        // Xóa danh mục mẫu sau kiểm tra
        deleteTestCategory();
    }

    @Test
    void testGetCategoryNameById_NoCategory() throws SQLException {
        // Kiểm tra trường hợp không tồn tại danh mục với ID đó
        String categoryName = categoryService.getCategoryNameById(999);

        assertEquals("Không có tên danh mục", categoryName);
    }
    
   

    // Hàm chèn danh mục mẫu vào cơ sở dữ liệu
    private void insertTestCategory() throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "INSERT INTO categories (category_id, name) VALUES (5, 'Category 5')";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.executeUpdate();
        }
    }

    // Hàm xóa danh mục mẫu sau khi kiểm tra
    private void deleteTestCategory() throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "DELETE FROM categories WHERE category_id = 5";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.executeUpdate();
        }
    }
}
