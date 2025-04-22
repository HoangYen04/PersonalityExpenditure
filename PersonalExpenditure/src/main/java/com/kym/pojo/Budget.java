/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kym.pojo;

import java.time.LocalDate;

/**
 *
 * @author ADMIN
 */
public class Budget {
    private int budgetId;
    private double amount;
    private LocalDate startDate;
    private int categoryId;
    private int userId;
    private String categoryName;

    public Budget() {
    }

    public Budget(int budgetId, double amount, LocalDate startDate, int categoryId, int userId, double currentSpending) {
         if (amount < currentSpending) {
            throw new IllegalArgumentException("Ngân sách không được nhỏ hơn số đã chi");
        }
        this.budgetId = budgetId;
        this.amount = amount;
        this.startDate = startDate;
        this.categoryId = categoryId;
        this.userId = userId;
    }
    
    
    public boolean isOver80Percent(double totalSpent) {
        return totalSpent >= 0.8 * amount;
    }

    public void updateAmount(double newAmount, double currentSpending) {
        if (newAmount < currentSpending) {
            throw new IllegalArgumentException("Không thể giảm ngân sách xuống thấp hơn số đã chi");
        }
        this.amount = newAmount;
    }

    /**
     * @return the budgetId
     */
    public int getBudgetId() {
        return budgetId;
    }

    /**
     * @param budgetId the budgetId to set
     */
    public void setBudgetId(int budgetId) {
        this.budgetId = budgetId;
    }

    /**
     * @return the amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

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
     * @return the category
     */
    

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

    /**
     * @return the categoryId
     */
    public int getCategoryId() {
        return categoryId;
    }

    /**
     * @param categoryId the categoryId to set
     */
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * @return the categoryName
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * @param categoryName the categoryName to set
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return String.valueOf(this.budgetId);
    }
    
    
}
