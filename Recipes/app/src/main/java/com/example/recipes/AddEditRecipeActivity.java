package com.example.recipes;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public class AddEditRecipeActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_CODE = 2;

    private EditText recipeNameEditText;
    private EditText ingredientsEditText;
    private EditText notesEditText;
    private ImageView recipeImageView;
    private Button saveRecipeButton;
    private Button selectImageButton;
    private Uri imageUri;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private String recipeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_recipe);

        recipeNameEditText = findViewById(R.id.recipeNameEditText);
        ingredientsEditText = findViewById(R.id.ingredientsEditText);
        notesEditText = findViewById(R.id.notesEditText);
        recipeImageView = findViewById(R.id.recipeImageView);
        selectImageButton = findViewById(R.id.selectImageButton);
        saveRecipeButton = findViewById(R.id.saveRecipeButton);

        recipeId = getIntent().getStringExtra("recipeId");
        databaseReference = FirebaseDatabase.getInstance().getReference("recipes");
        storageReference = FirebaseStorage.getInstance().getReference("recipe_images");

        if (recipeId != null) {
            loadRecipeDetails();
        }

        saveRecipeButton.setOnClickListener(v -> saveRecipe());
        selectImageButton.setOnClickListener(v -> openImagePicker());
    }

    private void loadRecipeDetails() {
        databaseReference.child(recipeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Recipe recipe = snapshot.getValue(Recipe.class);
                if (recipe != null) {
                    recipeNameEditText.setText(recipe.getName());
                    ingredientsEditText.setText(String.join(", ", recipe.getIngredients()));
                    notesEditText.setText(recipe.getNotes());
                    displayImage(recipe.getImageUrl());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
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

        if (imageUri != null) {
            uploadImageToFirebaseStorage();
        } else {
            saveRecipeToDatabase(name, ingredientsText, notes, "");
        }
    }

    private void displayImage(String imageUrl) {
        Glide.with(this)
                .load(imageUrl)
                .into(recipeImageView);
    }

    private void uploadImageToFirebaseStorage() {
        StorageReference fileReference = storageReference.child(UUID.randomUUID().toString());
        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            saveRecipeToDatabase(recipeNameEditText.getText().toString(), ingredientsEditText.getText().toString(),
                                    notesEditText.getText().toString(), imageUrl);
                        })
                )
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show());
    }

    private void saveRecipeToDatabase(String name, String ingredientsText, String notes, String imageUrl) {
        Recipe recipe = new Recipe(name, ingredientsText, notes, imageUrl);
        databaseReference.child(recipeId).setValue(recipe)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Recipe saved", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save recipe", Toast.LENGTH_SHORT).show());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                recipeImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void openAddEditRecipeActivity(String recipeId) {
        Intent intent = new Intent(this, AddEditRecipeActivity.class);
        intent.putExtra("recipeId", recipeId);
        startActivity(intent);
    }
}
