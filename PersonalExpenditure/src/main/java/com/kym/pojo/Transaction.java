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
public class Transaction {
    private int transactionId;
    private double amount;
    private LocalDate date;
    private int categoryId;
    private int userId;

    public Transaction() {
    }

    public Transaction(int transactionId, double amount, LocalDate date, int categoryId, int userId) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Số tiền phải lớn hơn 0");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày giao dịch không được vượt quá hiện tại");
        }
        this.transactionId = transactionId;
        this.amount = amount;
        this.date = date;
        this.categoryId = categoryId;
        this.userId = userId;
    }
    
    

    /**
     * @return the transactionId
     */
    public int getTransactionId() {
        return transactionId;
    }

    /**
     * @param transactionId the transactionId to set
     */
    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
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
     * @return the date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(LocalDate date) {
        this.date = date;
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

    
}
