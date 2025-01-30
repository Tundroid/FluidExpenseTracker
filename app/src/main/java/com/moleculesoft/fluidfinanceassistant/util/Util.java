package com.moleculesoft.fluidfinanceassistant.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.moleculesoft.fluidfinanceassistant.data.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class Util {

    private static User appUser;
    private static final String PREFS_NAME = "user_prefs";

    public static Menu ACTIVE_MENU;
    public static Model ACTIVE_MODEL;
    public static Category ACTIVE_CATEGORY;

    public static final List<String> MENU_ARRAY = Arrays.asList(
            "Budget",
            "Category",
            "Expense",
            "Income",
            "Saving",
            "SavingGoal"
    );


    /**
     * Initialize the userId from SharedPreferences.
     * This should be called once, typically in the Application class or main activity.
     *
     * @param context Application context
     */
    public static void initialize(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        appUser = new User(
                sp.getInt("UserID", 0),
                sp.getString("FullName", null),
                sp.getString("Email", null)
        );
    }

    /**
     * Save the user to SharedPreferences and update the static variable.
     *
     * @param context Application context
     * @param user    The user to save
     */
    public static void saveAppUser(Context context, User user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("UserID", user.getId());
        editor.putString("FullName", user.getName());
        editor.putString("Email", user.getEmail());
        editor.apply();
        appUser = user;
    }

    /**
     * Get the stored user.
     *
     * @return The user
     */
    public static User getAppUser() {
        return appUser;
    }

    public static Date getDateObject(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

//    public String getFormattedDate(String outputFormat) {
//        Date dateObject = getDateObject();
//        if (dateObject != null) {
//            SimpleDateFormat outputFormatter = new SimpleDateFormat(outputFormat, Locale.getDefault());
//            return outputFormatter.format(dateObject);
//        }
//        return ""; // Or handle the error as needed
//    }
}
