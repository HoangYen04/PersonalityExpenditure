/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kym.pojo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author ADMIN
 */
public class JdbcUtils {

    private static Connection testConnection;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

//    public static Connection getConn() throws SQLException {
//        return DriverManager.getConnection("jdbc:mysql://localhost/personalexpenditure", "root", "admin");
//    }

//    // Phương thức cho phép thay đổi kết nối (dùng trong kiểm thử)
//    public static void setConnection(Connection conn) {
//        connection = conn;
//    }
//
//    // Phương thức đóng kết nối (nếu cần)
//    public static void closeConnection() throws SQLException {
//        if (connection != null && !connection.isClosed()) {
//            connection.close();
//        }
//    }
    public static void overrideConnection(Connection c) {
        testConnection = c;
    }

    public static Connection getConn() throws SQLException {
        if (testConnection != null) {
            return testConnection;
        }
        // còn nếu testConnection == null thì lấy connection thật
        return DriverManager.getConnection("jdbc:mysql://localhost/personalexpenditure", "root", "admin");
    }
}
