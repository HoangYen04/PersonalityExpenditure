/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kym.pojo;

import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class User {
    private int userId;
    private String name;
    private String email;
    private String password;
    private List<Transaction> transactions;
    private List<Category> categories;
    private List<Budget> budgets;

    public User() {
    }

    public User(int userId, String name, String email, String password, List<Transaction> transactions, List<Category> categories, List<Budget> budgets) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.transactions = transactions;
        this.categories = categories;
        this.budgets = budgets;
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
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

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
     * @return the categories
     */
    public List<Category> getCategories() {
        return categories;
    }

    /**
     * @param categories the categories to set
     */
    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    /**
     * @return the budgets
     */
    public List<Budget> getBudgets() {
        return budgets;
    }

    /**
     * @param budgets the budgets to set
     */
    public void setBudgets(List<Budget> budgets) {
        this.budgets = budgets;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    
    
}
