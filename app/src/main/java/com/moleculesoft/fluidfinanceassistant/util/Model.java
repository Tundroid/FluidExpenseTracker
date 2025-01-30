package com.moleculesoft.fluidfinanceassistant.util;

public enum Model {
    BUDGET("budget"),
    CATEGORY("category"),
    EXPENSE("expense"),
    INCOME("income"),
    SAVING("saving"),
    SAVING_GOAL("saving_goal");

    private final String value;

    // Constructor to associate the enum with a specific string value
    Model(String value) {
        this.value = value;
    }

    // Method to get the string value of the enum
    public String getValue() {
        return value;
    }

    // Static method to convert a string to the corresponding enum
    public static Model fromValue(String value) {
        for (Model model : Model.values()) {
            if (model.value.equalsIgnoreCase(value)) {
                return model;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + value);
    }
}