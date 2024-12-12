package com.example.recipes;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Recipe implements Serializable { // Implement Serializable
    private String id;
    private String name;
    private List<String> ingredients;
    private String notes;
    private String imageUrl;
    private List<String> categoryIds;

    // Default constructor required for Firebase
    public Recipe() {}

    public Recipe(String id, String name, List<String> ingredients, String notes, String imageUrl, List<String> categoryIds) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.notes = notes;
        this.imageUrl = imageUrl;
        this.categoryIds = categoryIds;
    }

    public Recipe(String name, String ingredientsText, String notes, String imageUrl, List<String> categoryIds) {
        this.name = name;
        this.ingredients = Arrays.asList(ingredientsText.split(","));
        this.notes = notes;
        this.imageUrl = imageUrl;
        this.categoryIds = categoryIds;
    }

    // Getters and Setters for all fields
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public String getNotes() {
        return notes;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
