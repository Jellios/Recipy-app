package com.example.recipes;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.graphics.Bitmap;
import android.util.Base64;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RecipeDetailsActivity extends AppCompatActivity {

    private ImageView recipeImageView;
    private TextView recipeNameTextView;
    private TextView ingredientsTextView;
    private TextView notesTextView;
    private Button editRecipeButton;
    private DatabaseReference databaseReference;
    private String recipeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        recipeImageView = findViewById(R.id.recipeImageView);
        recipeNameTextView = findViewById(R.id.recipeNameTextView);
        ingredientsTextView = findViewById(R.id.ingredientsTextView);
        notesTextView = findViewById(R.id.notesTextView);
        editRecipeButton = findViewById(R.id.editRecipeButton);

        recipeId = getIntent().getStringExtra("recipeId");
        databaseReference = FirebaseDatabase.getInstance().getReference("recipes").child(recipeId);

        loadRecipeDetails();

        editRecipeButton.setOnClickListener(v -> openAddEditRecipeActivity(recipeId));
    }

    private void loadRecipeDetails() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Recipe recipe = snapshot.getValue(Recipe.class);
                if (recipe != null) {
                    if (recipe.getImageUrl() != null) {
                        Bitmap bitmap = base64ToBitmap(recipe.getImageUrl());
                        recipeImageView.setImageBitmap(bitmap);
                    }
                    recipeNameTextView.setText(recipe.getName());
                    ingredientsTextView.setText(String.join(", ", recipe.getIngredients()));
                    notesTextView.setText(recipe.getNotes());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private Bitmap base64ToBitmap(String base64String) {
        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    private void openAddEditRecipeActivity(String recipeId) {
        Intent intent = new Intent(this, AddEditRecipeActivity.class);
        intent.putExtra("recipeId", recipeId);
        startActivity(intent);
    }
}
