package com.moleculesoft.fluidfinanceassistant.data.model;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

public class Income {

    private Integer IncomeID;
    private String IncomeDate;
    private double Amount;
    private String Category;
    private String IncomeDescription;

    public Income() {
    }

    public Income(@Nullable Integer id, String date, double amount, String category, String description) {
        IncomeID = id;
        IncomeDate = date;
        Amount = amount;
        Category = category;
        IncomeDescription = description;
    }

    @JsonIgnore
    public Integer getIncomeID() {
        return IncomeID;
    }

    public void setIncomeID(Integer incomeID) {
        this.IncomeID = incomeID;
    }

    public String getIncomeDate() {
        return IncomeDate;
    }

    public void setIncomeDate(String incomeDate) {
        this.IncomeDate = incomeDate;
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

    public String getIncomeDescription() {
        return IncomeDescription;
    }

    public void setIncomeDescription(String incomeDescription) {
        this.IncomeDescription = incomeDescription;
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