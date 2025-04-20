/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kym.pojo;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ADMIN
 */
public class Report {
    private LocalDate startDate;
    private LocalDate endDate;
    private int userId;
    private List<Transaction> transactions;
    private double totalSpent;
    private Map<String, Double> chartData;

   public Report(LocalDate start, LocalDate end, int userId, List<Transaction> transactions) {
        if (start.isAfter(end) || end.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Khoảng thời gian không hợp lệ");
        }
        this.startDate = start;
        this.endDate = end;
        this.userId = userId;
        this.transactions = transactions;
        this.totalSpent = calculateTotal(transactions);
//        this.chartData = transactions.isEmpty() ? null : generateChartData(transactions);
    }
   
   private double calculateTotal(List<Transaction> transactions) {
        return transactions.stream().mapToDouble(Transaction::getAmount).sum();
    }

//    private Map<String, Double> generateChartData(List<Transaction> transactions) {
//        // Giả lập xử lý: gom nhóm theo tên danh mục
//        return transactions.stream().collect(
//            java.util.stream.Collectors.groupingBy(
//                t -> t.getCategory().getName(),
//                java.util.stream.Collectors.summingDouble(Transaction::getAmount)
//            )
//        );
//    }

    
    /**
     * @return the startDate
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the user
     */
    

    /**
     * @return the transactions
     */
    public List<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * @param transactions the transactions to set
     */
    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    /**
     * @return the totalSpent
     */
    public double getTotalSpent() {
        return totalSpent;
    }

    /**
     * @param totalSpent the totalSpent to set
     */
    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }

    /**
     * @return the chartData
     */
    public Map<String, Double> getChartData() {
        return chartData;
    }

    /**
     * @param chartData the chartData to set
     */
    public void setChartData(Map<String, Double> chartData) {
        this.chartData = chartData;
    }

    /**
     * @return the userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    
}
