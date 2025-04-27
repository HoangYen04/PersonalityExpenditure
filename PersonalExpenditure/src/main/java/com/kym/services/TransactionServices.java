/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kym.services;

import com.kym.pojo.Category;
import com.kym.pojo.JdbcUtils;
import com.kym.pojo.Transaction;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class TransactionServices {

    public List<Transaction> getTransaction(int userId) throws SQLException {
        List<Transaction> result = new ArrayList<>();
        try ( Connection cnn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM transactions WHERE user_id = ?";
            PreparedStatement stm = cnn.prepareCall(sql);
            stm.setInt(1, userId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Transaction t = new Transaction(rs.getInt("transaction_id"), rs.getDouble("amount"), rs.getDate("date").toLocalDate(), rs.getInt("category_id"), rs.getInt("user_id"), rs.getString("des"));

                result.add(t);

            }
            return result;
        }
    }
    private CategoryService categoryService = new CategoryService();

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

    public double getBudgetForCategory(int categoryId, int userId) throws SQLException {
        try ( Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT amount FROM budgets WHERE category_id = ? AND user_id = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, categoryId);
            stm.setInt(2, userId);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getDouble("amount");
            }
        }
        return -1; // -1: chưa có ngân sách cho danh mục này
    }

    public double getTotalSpendingByCategoryThisMonth(int categoryId, int userId) throws SQLException {
        try ( Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT SUM(amount) AS total FROM transactions "
                    + "WHERE category_id = ? AND user_id = ? AND MONTH(date) = MONTH(CURRENT_DATE()) "
                    + "AND YEAR(date) = YEAR(CURRENT_DATE())";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, categoryId);
            stm.setInt(2, userId);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0;
    }

    // Phương thức thêm giao dịch
    public int addTransaction(Transaction transaction) throws SQLException {
        int userId = transaction.getUserId();
        int categoryId = transaction.getCategoryId();
        double newAmount = transaction.getAmount();
        String des = transaction.getDes();

        if (!isCategoryValid(categoryId)) {
            return -1; // Danh mục không hợp lệ
        }

        double budget = getBudgetForCategory(categoryId, userId);
        double totalSpent = getTotalSpendingByCategoryThisMonth(categoryId, userId);

        if (budget == -1) {
            return -3; // Chưa có ngân sách → yêu cầu thiết lập
        }

        if ((totalSpent + newAmount) > budget) {
            return -2; // Vượt quá ngân sách
        }

        try ( Connection conn = JdbcUtils.getConn()) {
            String sql = "INSERT INTO transactions (amount, date, category_id, user_id, des) VALUES (?, ?, ?, ?,?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, newAmount);
            stmt.setDate(2, java.sql.Date.valueOf(transaction.getDate()));
            stmt.setInt(3, categoryId);
            stmt.setInt(4, userId);
            stmt.setString(5, des);
            return stmt.executeUpdate() > 0 ? 1 : 0;
        }
    }

    // Phương thức để lấy tổng chi tiêu của danh mục theo người dùng
    public double getSpendingByCategoryAndUser(int userId, int categoryId) throws SQLException {
        double totalSpending = 0.0;

        String query = "SELECT SUM(amount) AS totalSpending FROM transactions WHERE user_id = ? AND category_id = ?";

        try ( Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.setInt(2, categoryId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                totalSpending = rs.getDouble("totalSpending");
            }
        }

        return totalSpending;
    }

    public double getTotalSpendingInCurrentMonth(int userId) throws SQLException {
        double total = 0;
        try ( Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT SUM(amount) AS total FROM transactions "
                    + "WHERE user_id = ? AND MONTH(date) = MONTH(CURRENT_DATE()) AND YEAR(date) = YEAR(CURRENT_DATE())";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, userId);

            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("total");
            }
        }
        return total;
    }

    public void deleteTransaction(String id, int userId) throws SQLException {
        try ( Connection conn = JdbcUtils.getConn()) {
            String sql = "DELETE FROM transactions WHERE transaction_id=? AND user_id=?";
            PreparedStatement stm = conn.prepareCall(sql);
            stm.setString(1, id); //Tránh SQL Injection
            stm.setInt(2, userId);
            stm.executeUpdate();
        }
    }

    public int updateTransaction(int transactionId, double amount, LocalDate date, int categoryId, String desc, int userId) throws SQLException {

        if (date == null || date.isAfter(LocalDate.now())) {
            return -4;  // Lỗi: Ngày giao dịch không hợp lệ
        }
        if (!isCategoryValid(categoryId)) {
            return -1;  // Lỗi: Danh mục không hợp lệ
        }

        // Kiểm tra ngân sách
        double budget = getBudgetForCategory(categoryId, userId);
        if (budget == -1) {
            return -3;  // Lỗi: Danh mục này chưa có ngân sách
        }
        double oldAmount = getTransactionAmount(transactionId, userId);

        double totalSpent = getTotalSpendingByCategoryThisMonth(categoryId, userId);
        if ((totalSpent + amount - oldAmount) > budget) {
            return -2;
        }

        try ( Connection conn = JdbcUtils.getConn()) {
            // Câu lệnh SQL để cập nhật giao dịch
            String sql = "UPDATE transactions SET amount = ?, date = ?, category_id = ?, des = ? WHERE transaction_id = ? AND user_id = ?";

            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setDouble(1, amount);
            stm.setDate(2, Date.valueOf(date));
            stm.setInt(3, categoryId);
            stm.setString(4, desc);
            stm.setInt(5, transactionId);
            stm.setInt(6, userId);

            return stm.executeUpdate() > 0 ? 1 : 0;

        }

    }

    // Hàm lấy giá trị amount cũ của giao dịch
    private double getTransactionAmount(int transactionId, int userId) throws SQLException {
        String sql = "SELECT amount FROM transactions WHERE transaction_id = ? AND user_id = ?";
        try ( Connection conn = JdbcUtils.getConn();  PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setInt(1, transactionId);
            stm.setInt(2, userId);

            try ( ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("amount");
                }
            }
        }
        return 0; // Nếu không tìm thấy giao dịch, trả về 0
    }

}
