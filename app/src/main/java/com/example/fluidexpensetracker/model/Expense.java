package com.example.fluidexpensetracker.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Expense {

    private int id;
    private String date; // Store as String for simplicity, handle parsing separately
    private double amount;
    private String category;
    private String description;
    public static int ID;

    // Constructors
    public Expense() {
        // Default constructor (important for some libraries/frameworks)
    }

    public Expense(int id, String date, double amount, String category, String description) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.description = description;
        ID = id;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    //Helper method to get Date object
    public Date getDateObject() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // Define your date format
        try {
            return format.parse(this.date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Or handle the error as needed
        }
    }

    //Helper method to format date for display
    public String getFormattedDate(String outputFormat) {
        Date dateObject = getDateObject();
        if (dateObject != null) {
            SimpleDateFormat outputFormatter = new SimpleDateFormat(outputFormat, Locale.getDefault());
            return outputFormatter.format(dateObject);
        }
        return ""; // Or handle the error as needed
    }


    @Override
    public String toString() {
        return "Expense{" +
                "date='" + date + '\'' +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}