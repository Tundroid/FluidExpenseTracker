package com.moleculesoft.fluidfinanceassistant.data.model;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

public class Category {

    private Integer CategoryID;
    private String CategoryName;
    private String CategoryType;

    public Category() {
    }

    public Category(@Nullable Integer id, String name, String type) {
        CategoryID = id;
        CategoryName = name;
        CategoryType = type;
    }

    @JsonIgnore
    public Integer getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(int categoryID) {
        this.CategoryID = categoryID;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        this.CategoryName = categoryName;
    }

    public String getCategoryType() {
        return CategoryType;
    }

    public void setCategoryType(String type) {
        this.CategoryType = type;
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