/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kym.services;

import com.kym.pojo.Category;
import com.kym.pojo.JdbcUtils;
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
public class CategoryService {
    public List<Category> getCates () throws SQLException{
        List <Category> result = new ArrayList<>();
        try (Connection cnn = JdbcUtils.getConn()){
            String sql = "SELECT * FROM categories";
            PreparedStatement stm = cnn.prepareCall(sql);
            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                Category c = new Category(rs.getInt("category_id"), rs.getString("name"));
                
                result.add(c);
                
            }
            return result;
        }
    }

    
    public Category getCategoryById(int categoryId) throws SQLException {
        Category category = null;
        String sql = "SELECT * FROM categories WHERE category_id = ?";
        
        try (Connection conn = JdbcUtils.getConn(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId); 
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
               
                    category = new Category(rs.getInt("category_id"), rs.getString("name"));
                }
            }
        }
        return category;  
    }
    

      public String getCategoryNameById(int categoryId) throws SQLException {
        String categoryName = "Không có tên danh mục";  // Giá trị mặc định nếu không tìm thấy
        try (Connection cnn = JdbcUtils.getConn()) {
            String sql = "SELECT name FROM categories WHERE category_id = ?";
            PreparedStatement stm = cnn.prepareStatement(sql);
            stm.setInt(1, categoryId);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                categoryName = rs.getString("name");
            }
        }
        return categoryName;
    }

}
