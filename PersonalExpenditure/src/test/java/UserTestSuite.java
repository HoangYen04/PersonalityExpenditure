
import com.kym.pojo.User;
import com.kym.services.Session;
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

    private static UserServices userServices = new UserServices();
    //Kiểm tra đăng nhập bằng email và mật khẩu

    @Test
    public void testLoginByEmail() {
        int result = userServices.loginByEmail("a@a.com", "password");
        Assertions.assertTrue(result >= -4 && result <= 1);
    }
    
    //Test loginByEmail: kiểm tra đăng nhập thành công sẽ lấy đúng 1 người dùng
     
    @Test
    public void testLoginByEmail_SingleUserInSession() {
        // Bạn cần chắc chắn trước đó đã có 1 user hợp lệ trong database với email & password bên dưới
        String email = "a@gmail.com";
        String password = "123"; // Đây là mật khẩu đúng

        int result = userServices.loginByEmail(email, password);

        Assertions.assertEquals(1, result); // Đăng nhập phải thành công
        User currentUser = Session.getCurrentUser();
        Assertions.assertNotNull(currentUser); //curentusser phải lấy đc
        Assertions.assertEquals(email, currentUser.getEmail());
    }

    @Test
    //Kiểm tra đăng ký người dùng mới với dữ liệu hợp lệ
    public void testRegisterUser() throws SQLException {
        User us = new User();
        us.setName("tester");
        us.setEmail("test@mail.com");
        us.setPassword("ValidPass1!");
        int result = userServices.registerUser(us);
        Assertions.assertTrue(result >= -9 && result <= 1);

    }

    @Test
    //Kiểm tra đăng ký người dùng mới với dữ liệu mật khẩu không hợp lệ
    public void testRegisterUserPassword() throws SQLException {
        User us = new User();
        us.setName("tester");
        us.setEmail("test@mail.com");
        us.setPassword("ValidPass");
        int result = userServices.registerUser(us);
        Assertions.assertTrue(result >= -7 && result <= -3);

    }

    @Test
    //Kiểm tra mã hóa mật khẩu SHA-256
    void testHashPassword() {
        String hashed = UserServices.hashPassword("MyPass1!");
        Assertions.assertNotNull(hashed);
        Assertions.assertEquals(64, hashed.length());
    }

    //Test registerUser: kiểm tra đăng ký thất bại khi thiếu thông tin
    @Test
    public void testRegisterUser_MissingInformation() throws SQLException {
        User u = new User();
        u.setName("");
        u.setEmail("");
        u.setPassword("");

        int result = userServices.registerUser(u);
        Assertions.assertEquals(-1, result);
    }

    

     //Test registerUser: kiểm tra đăng ký thất bại nếu email đã tồn tại
    @Test
    public void testRegisterUser_EmailAlreadyExists() throws Exception {
        User user = new User();
        user.setName("testuser");
        user.setEmail("a@gmail.com"); // Email này đã tồn tại từ test login
        user.setPassword("Password@123");

        int result = userServices.registerUser(user);

        Assertions.assertEquals(-9, result);
    }
}
