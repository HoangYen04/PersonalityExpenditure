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
   
    private int userId;
    private List<Transaction> transactions;
    private double totalSpent;
    private Map<String, Double> chartData;

    public Report() {
    }

    public Report(int userId, List<Transaction> transactions, double totalSpent, Map<String, Double> chartData) {
        this.userId = userId;
        this.transactions = transactions;
        this.totalSpent = totalSpent;
        this.chartData = chartData;
    }

   
  
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
