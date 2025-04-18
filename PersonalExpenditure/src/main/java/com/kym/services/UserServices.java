/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kym.services;

import com.kym.pojo.JdbcUtils;
import com.kym.pojo.Transaction;
import com.kym.pojo.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class UserServices {

    public User login(String name, String password) throws SQLException {
        User result = null;  // Sử dụng đối tượng User thay vì List
        try ( Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM users WHERE name = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                result = new User();
                result.setUserId(rs.getInt("id"));
                result.setName(rs.getString("name"));
                result.setEmail(rs.getString("email"));
                result.setPassword(rs.getString("password"));
            }
        }
        return result;  // Trả về một đối tượng User thay vì danh sách
    }
    // Tên không rỗng, đủ 6 ký tự

    public boolean isValidUsername(String username) {
        return username != null && username.trim().length() >= 6;
    }

// Giả định kiểm tra trùng từ DB
    public boolean isUsernameDuplicate(String username) {
        try ( Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT COUNT(*) FROM users WHERE name = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

// Mật khẩu không rỗng
    public boolean isPasswordEmpty(String password) {
        return password == null || password.trim().isEmpty();
    }
}
