package com.example.recipes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddEditRecipeActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText recipeNameEditText;
    private EditText ingredientsEditText;
    private EditText notesEditText;
    private ImageView recipeImageView;
    private Button saveRecipeButton;
    private Button selectImageButton;
    private String imageBase64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_recipe);

        recipeNameEditText = findViewById(R.id.recipeNameEditText);
        ingredientsEditText = findViewById(R.id.ingredientsEditText);
        notesEditText = findViewById(R.id.notesEditText);
        recipeImageView = findViewById(R.id.recipeImageView);
        saveRecipeButton = findViewById(R.id.saveRecipeButton);
        selectImageButton = findViewById(R.id.selectImageButton);

        saveRecipeButton.setOnClickListener(v -> saveRecipe());
        selectImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });
    }

    private void saveRecipe() {
        String name = recipeNameEditText.getText().toString().trim();
        String ingredientsText = ingredientsEditText.getText().toString().trim();
        String notes = notesEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(ingredientsText)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a Recipe object
        List<String> categoryIds = new ArrayList<>(); // Add logic to select categories
        Recipe recipe = new Recipe(name, ingredientsText, notes, imageBase64, categoryIds);

        // Save the recipe to Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("recipes");
        String recipeId = databaseReference.push().getKey(); // Generate a unique ID
        recipe.setId(recipeId);

        databaseReference.child(recipeId).setValue(recipe)
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Recipe saved", Toast.LENGTH_SHORT).show();
                finish();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to save recipe: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", "Failed to save recipe", e);
            });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                recipeImageView.setImageBitmap(bitmap);
                imageBase64 = bitmapToBase64(bitmap); // Convert to Base64
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
