package com.example.fluidexpensetracker.util;

public enum Category {
    BUDGET("Budget"),
    EXPENSE("Expense"),
    INCOME("Income"),
    SAVING("Saving");

    private final String value;

    // Constructor to associate the enum with a specific string value
    Category(String value) {
        this.value = value;
    }

    // Method to get the string value of the enum
    public String getValue() {
        return value;
    }

    // Static method to convert a string to the corresponding enum
    public static Category fromValue(String value) {
        for (Category model : Category.values()) {
            if (model.value.equalsIgnoreCase(value)) {
                return model;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + value);
    }
}