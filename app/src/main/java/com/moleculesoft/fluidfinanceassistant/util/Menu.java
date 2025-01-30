package com.moleculesoft.fluidfinanceassistant.util;

public enum Menu {
    BUDGET("Budget"),
    CATEGORY("Category"),
    EXPENSE("Expense"),
    INCOME("Income"),
    SAVING("Saving"),
    SAVING_GOAL("SavingGoal");

    private final String value;

    // Constructor to associate the enum with a specific string value
    Menu(String value) {
        this.value = value;
    }

    // Method to get the string value of the enum
    public String getValue() {
        return value;
    }

    // Static method to convert a string to the corresponding enum
    public static Menu fromValue(String value) {
        for (Menu menu : Menu.values()) {
            if (menu.value.equalsIgnoreCase(value)) {
                return menu;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + value);
    }
}