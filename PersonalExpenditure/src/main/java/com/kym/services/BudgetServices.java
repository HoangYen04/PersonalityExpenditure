package com.kym.services;

import com.kym.pojo.Budget;
import com.kym.pojo.JdbcUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BudgetServices {

    public List<Budget> getBudgetsByUserId(int userId) throws SQLException {
        List<Budget> budgets = new ArrayList<>();
        Connection conn = JdbcUtils.getConn();

        String sql = "SELECT b.*, c.name AS category_name "
                + "FROM budgets b "
                + "JOIN categories c ON b.category_id = c.category_id "
                + "WHERE b.user_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, userId);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Budget b = new Budget();
            b.setBudgetId(rs.getInt("budget_id"));
            b.setUserId(userId);
            b.setCategoryId(rs.getInt("category_id"));
            b.setAmount(rs.getDouble("amount"));
            b.setCategoryName(rs.getString("category_name"));  // Gán tên danh mục

            budgets.add(b);
        }

        return budgets;
    }

    public double getTotalBudgetByUserId(int userId) throws SQLException {
        Connection conn = JdbcUtils.getConn();
        String sql = "SELECT SUM(amount) AS total_budget FROM budgets WHERE user_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return rs.getDouble("total_budget");
        }
        return 0.0;
    }
    
    public Budget getBudgetByCategoryAndUser(int userId, int categoryId) throws SQLException {
        Budget budget = null;
        
        String query = "SELECT * FROM budgets WHERE user_id = ? AND category_id = ?";
        
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.setInt(2, categoryId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                budget = new Budget();
                budget.setAmount(rs.getDouble("amount"));
                budget.setUserId(rs.getInt("user_id"));
                budget.setCategoryId(rs.getInt("category_id"));
            }
        }
        
        return budget;
    }
    
    public void updateBudget(Budget budget) throws SQLException {
        String query = "UPDATE budgets SET amount = ? WHERE user_id = ? AND category_id = ?";
        
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setDouble(1, budget.getAmount());
            stmt.setInt(2, budget.getUserId());
            stmt.setInt(3, budget.getCategoryId());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Ngân sách đã được cập nhật thành công!");
            } else {
                System.out.println("Không tìm thấy ngân sách để cập nhật.");
            }
        }
    }
    public void addBudget(Budget budget) throws SQLException {
        String query = "INSERT INTO budgets (user_id, category_id, amount,category_name) VALUES (?, ?, ?,?)";
        
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, budget.getUserId()); // userId
            stmt.setInt(2, budget.getCategoryId()); // categoryId
            stmt.setDouble(3, budget.getAmount());
            stmt.setString(4, budget.getCategoryName());

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Ngân sách đã được thêm thành công!");
            } else {
                System.out.println("Không thể thêm ngân sách.");
            }
        }
    }

    

}
