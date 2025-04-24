package com.kym.services;

import com.kym.pojo.Category;
import com.kym.pojo.JdbcUtils;
import com.kym.pojo.Transaction;
import com.kym.pojo.User;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author Yen My
 */
public class ReportService {

    public void exportTransactionsToCSV(List<Transaction> transactions, String filePath) throws IOException, SQLException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Ghi dòng tiêu đề
            writer.write("STT,Ngày,Danh mục,Số tiền");
            writer.newLine();

            int index = 1;
            for (Transaction t : transactions) {
                String line = String.format(
                        "%d,%s,%s,%.2f",
                        index++,
                        t.getDate(),
                        getCategoryNameById(t.getCategoryId()),
                        t.getAmount()
                );
                writer.write(line);
                writer.newLine();
            }
        }
    }

    public List<Transaction> getTransactionsByUserAndMonth(int month, int year) throws SQLException {
        List<Transaction> result = new ArrayList<>();
        try (Connection cnn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM transactions WHERE user_id = ? AND date >= ? AND date < ?";
            PreparedStatement stm = cnn.prepareStatement(sql);

            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.plusMonths(1);

            stm.setInt(1, Session.getCurrentUser().getUserId());
            stm.setDate(2, java.sql.Date.valueOf(startDate));
            stm.setDate(3, java.sql.Date.valueOf(endDate));

            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Transaction t = new Transaction(
                        rs.getInt("transaction_id"),
                        rs.getDouble("amount"),
                        rs.getDate("date").toLocalDate(),
                        rs.getInt("category_id"),
                        rs.getInt("user_id"),
                        rs.getString("des")
                );
                result.add(t);
            }
        }
        return result;
    }

    public List<Transaction> getTransactionsByUserAndYear(int year) throws SQLException {
        List<Transaction> result = new ArrayList<>();
        try (Connection cnn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM transactions WHERE user_id = ? AND date >= ? AND date < ?";
            PreparedStatement stm = cnn.prepareStatement(sql);

            // Lọc từ đầu năm đến đầu năm sau
            LocalDate startDate = LocalDate.of(year, 1, 1);
            LocalDate endDate = LocalDate.of(year + 1, 1, 1);

            stm.setInt(1, Session.getCurrentUser().getUserId());
            stm.setDate(2, java.sql.Date.valueOf(startDate));
            stm.setDate(3, java.sql.Date.valueOf(endDate));

            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Transaction t = new Transaction(
                        rs.getInt("transaction_id"),
                        rs.getDouble("amount"),
                        rs.getDate("date").toLocalDate(),
                        rs.getInt("category_id"),
                        rs.getInt("user_id"),
                        rs.getString("des")
                );
                result.add(t);
            }
        }
        return result;
    }
    private CategoryService categoryService = new CategoryService(); // Khởi tạo CategoryService

    // Phương thức lấy tên danh mục theo categoryId
    public String getCategoryNameById(int categoryId) throws SQLException {
        return categoryService.getCategoryNameById(categoryId);
    }

}
