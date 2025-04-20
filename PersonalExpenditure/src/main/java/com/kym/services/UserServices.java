package com.kym.services;

import com.kym.pojo.JdbcUtils;
import com.kym.pojo.User;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserServices {

    public User loginByEmail(String email, String password) throws SQLException {
        User result = null;
        try ( Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM users WHERE email = ?"; // Chỉ lấy email
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String hashedPasswordFromDB = rs.getString("password"); // Lấy mật khẩu đã băm từ DB
                String hashedPasswordInput = hashPassword(password); // Băm mật khẩu người dùng nhập vào

                if (hashedPasswordFromDB.equals(hashedPasswordInput)) {
                    result = new User();
                    result.setUserId(rs.getInt("user_id"));
                    result.setName(rs.getString("name"));
                    result.setEmail(rs.getString("email"));
                    result.setPassword(hashedPasswordFromDB); // Lưu lại mật khẩu băm từ DB
                    
                    
                    Session.setCurrentUser(result);
                }
            }
        }
        return result;
    }

    public boolean registerUser(User user) throws SQLException {
        String password = user.getPassword();

        try ( Connection conn = JdbcUtils.getConn()) {
            // Kiểm tra xem email đã tồn tại chưa
            String checkQuery = "SELECT * FROM users WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, user.getEmail());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                return false;
                // Email đã tồn tại
            }
            String hashedPassword = hashPassword(password);
            user.setPassword(hashedPassword);

            // Thêm người dùng mới
            String insertQuery = "INSERT INTO users(name, email, password) VALUES (?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setString(1, user.getName());
            insertStmt.setString(2, user.getEmail());
            insertStmt.setString(3, user.getPassword());

            return insertStmt.executeUpdate() > 0;
        }
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
