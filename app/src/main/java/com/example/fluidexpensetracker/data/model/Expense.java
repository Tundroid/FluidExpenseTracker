package com.example.fluidexpensetracker.data.model;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

public class Expense {

    private Integer ExpenseID;
    private String ExpenseDate;
    private double Amount;
    private String Category;
    private String ExpenseDescription;

    public Expense() {
    }

    public Expense(@Nullable Integer id, String date, double amount, String category, String description) {
        ExpenseID = id;
        ExpenseDate = date;
        Amount = amount;
        Category = category;
        ExpenseDescription = description;
    }

    @JsonIgnore
    public Integer getExpenseID() {
        return ExpenseID;
    }

    public void setExpenseID(Integer expenseID) {
        this.ExpenseID = expenseID;
    }

    public String getExpenseDate() {
        return ExpenseDate;
    }

    public void setExpenseDate(String expenseDate) {
        this.ExpenseDate = expenseDate;
    }

    public double getAmount() {
        return Amount;
    }

    public void setAmount(double amount) {
        this.Amount = amount;
    }

    @JsonIgnore
    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        this.Category = category;
    }

    public String getExpenseDescription() {
        return ExpenseDescription;
    }

    public void setExpenseDescription(String expenseDescription) {
        this.ExpenseDescription = expenseDescription;
    }

    @Override
    public String toString() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE);
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

}