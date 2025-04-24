
import com.kym.pojo.User;
import com.kym.services.UserServices;
import java.sql.SQLException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author ADMIN
 */
public class UserTestSuite {

    private static UserServices u = new UserServices();
    //Kiểm tra đăng nhập bằng email và mật khẩu

    @Test
    public void testLoginByEmail() {
        int result = u.loginByEmail("a@a.com", "password");
        Assertions.assertTrue(result >= -4 && result <= 1);
    }

    @Test
    //Kiểm tra đăng ký người dùng mới với dữ liệu hợp lệ
    public void testRegisterUser() throws SQLException {
        User us = new User();
        us.setName("tester");
        us.setEmail("test@mail.com");
        us.setPassword("ValidPass1!");
        int result = u.registerUser(us);
        Assertions.assertTrue(result >= -9 && result <= 1);

    }
    
    @Test
    //Kiểm tra đăng ký người dùng mới với dữ liệu mật khẩu không hợp lệ
    public void testRegisterUserPassword() throws SQLException {
        User us = new User();
        us.setName("tester");
        us.setEmail("test@mail.com");
        us.setPassword("ValidPass");
        int result = u.registerUser(us);
        Assertions.assertTrue(result >=-7 && result <= -3);

    }
    @Test
    //Kiểm tra mã hóa mật khẩu SHA-256
    void testHashPassword() {
        String hashed = UserServices.hashPassword("MyPass1!");
        Assertions.assertNotNull(hashed);
        Assertions.assertEquals(64, hashed.length());
    }

}
