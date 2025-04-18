/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kym.services;

import com.kym.pojo.Category;
import com.kym.pojo.JdbcUtils;
import com.kym.pojo.Transaction;
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
public class TransactionServices {
    public List<Transaction> getTransaction () throws SQLException{
        List <Transaction> result = new ArrayList<>();
        try (Connection cnn = JdbcUtils.getConn()){
            String sql = "SELECT * FROM transactions";
            PreparedStatement stm = cnn.prepareCall(sql);
            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                Transaction t = new Transaction(rs.getInt("transaction_id"), rs.getDouble("amount"),rs.getDate("date").toLocalDate(),rs.getInt("category_id"), rs.getInt("user_id"));
                
                result.add(t);
                
            }
            return result;
        }
    }
    private CategoryService categoryService = new CategoryService(); // Đảm bảo bạn có thể truy cập danh mục
    
    // Kiểm tra tính hợp lệ của danh mục
    public boolean isCategoryValid(int categoryId) throws SQLException {
        List<Category> categories = categoryService.getCates();
        for (Category category : categories) {
            if (category.getCategoryId() == categoryId) {
                return true;
            }
        }
        return false;
    }

    // Phương thức thêm giao dịch
    public boolean addTransaction(Transaction transaction) throws SQLException {
        // Kiểm tra xem danh mục có hợp lệ hay không
        if (!isCategoryValid(transaction.getCategoryId())) {
            System.out.println("Danh mục không hợp lệ");
            return false;  // Không thêm giao dịch nếu danh mục không hợp lệ
        }

        // Lưu giao dịch vào cơ sở dữ liệu (giả sử bạn đã có phương thức này)
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "INSERT INTO transactions (amount, date, category_id, user_id) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, transaction.getAmount());
            stmt.setDate(2, java.sql.Date.valueOf(transaction.getDate()));
            stmt.setInt(3, transaction.getCategoryId());
            stmt.setInt(4, transaction.getUserId());

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        }
    }
}
