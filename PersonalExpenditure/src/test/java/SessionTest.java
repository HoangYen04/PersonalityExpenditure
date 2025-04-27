
import com.kym.services.Session;
import com.kym.services.UserServices;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author ADMIN
 */
public class SessionTest {
    private final UserServices userServices = new UserServices();
    

    @Test
    public void testLoginByEmail_Failure_InvalidEmail() {
        String email = "yen1@gmail.com";
        String password = "InvalidPassword123";

        int result = userServices.loginByEmail(email, password);

        // Kiểm tra nếu đăng nhập thất bại vì email không đúng
        Assertions.assertEquals(-4, result); // Không tìm thấy email
        Assertions.assertNull(Session.getCurrentUser()); // Đảm bảo session không có user
    }

    @Test
    public void testLoginByEmail_Failure_WrongPassword() {
        String email = "yen@gmail.com";
        String password = "Hoang";

        int result = userServices.loginByEmail(email, password);

        // Kiểm tra nếu đăng nhập thất bại vì mật khẩu sai
        Assertions.assertEquals(-3, result); // Sai mật khẩu
        Assertions.assertNull(Session.getCurrentUser()); // Đảm bảo session không có user
    }
}
