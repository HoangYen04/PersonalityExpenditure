/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kym.services;

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
}
