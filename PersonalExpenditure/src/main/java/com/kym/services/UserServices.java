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

    public int loginByEmail(String email, String password) {
        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            return -1; // Trường trống
        }

        if (!email.matches("^.+@[A-Za-z]+.*$")) {
            return -2; // Email sai định dạng
        }

        try ( Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM users WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String hashedPasswordFromDB = rs.getString("password");
                String hashedPasswordInput = hashPassword(password);

                if (hashedPasswordFromDB.equals(hashedPasswordInput)) {
                    User result = new User();
                    result.setUserId(rs.getInt("user_id"));
                    result.setName(rs.getString("name"));
                    result.setEmail(rs.getString("email"));
                    result.setPassword(hashedPasswordFromDB);

                    Session.setCurrentUser(result);
                    return 1; // Đăng nhập thành công
                } else {
                    return -3; // Sai mật khẩu
                }
            } else {
                return -4; // Không tìm thấy email
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0; // Lỗi kết nối
        }
    }

    public int registerUser(User user) throws SQLException {
        String email = user.getEmail();
        String username = user.getName();
        String password1 = user.getPassword();

        // Kiểm tra trống
        if (email.isEmpty() || username.isEmpty() || password1.isEmpty()) {
            return -1; // Thiếu thông tin
        }

        // Ràng buộc định dạng email
        if (!email.matches("^.+@.+$")) {
            return -2; // Email sai định dạng
        }

        // Ràng buộc mật khẩu
        if (password1.length() < 8) {
            return -3;
        }
        if (!password1.matches(".*[A-Z].*")) {
            return -4;
        }
        if (!password1.matches(".*[a-z].*")) {
            return -5;
        }
        if (!password1.matches(".*\\d.*")) {
            return -6;
        }
        if (!password1.matches(".*[@#$%^&+=!].*")) {
            return -7;
        }

        // Ràng buộc username
        if (username.length() < 6 || username.length() > 30) {
            return -8; // Tên đăng nhập không hợp lệ
        }

        // Kiểm tra trùng email
        try ( Connection conn = JdbcUtils.getConn()) {
            String checkQuery = "SELECT * FROM users WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                return -9; // Email đã tồn tại
            }

            // Hash mật khẩu và lưu
            String hashedPassword = hashPassword(password1);
            user.setPassword(hashedPassword);

            String insertQuery = "INSERT INTO users(name, email, password) VALUES (?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setString(1, username);
            insertStmt.setString(2, email);
            insertStmt.setString(3, hashedPassword);

            return insertStmt.executeUpdate() > 0 ? 1 : 0; // 1: thành công, 0: thất bại không rõ nguyên nhân
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
