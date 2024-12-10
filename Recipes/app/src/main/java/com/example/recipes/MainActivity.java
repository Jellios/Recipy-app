package com.example.recipes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recipeRecyclerView;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipeList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recipeRecyclerView = findViewById(R.id.recipeRecyclerView);
        recipeRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        recipeList = new ArrayList<>();
        recipeAdapter = new RecipeAdapter(recipeList, this::openRecipeDetails);
        recipeRecyclerView.setAdapter(recipeAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("recipes");

        FloatingActionButton addRecipeButton = findViewById(R.id.addRecipeButton);
        addRecipeButton.setOnClickListener(v -> openAddEditRecipeActivity(null));

        loadRecipes();
    }

    private void loadRecipes() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipeList.clear();
                for (DataSnapshot recipeSnapshot : snapshot.getChildren()) {
                    Recipe recipe = recipeSnapshot.getValue(Recipe.class);
                    recipeList.add(recipe);
                }
                recipeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void openRecipeDetails(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailsActivity.class);
        intent.putExtra("recipeId", recipe.getId());
        startActivity(intent);
    }

    private void openAddEditRecipeActivity(String recipeId) {
        Intent intent = new Intent(this, AddEditRecipeActivity.class);
        if (recipeId != null) {
            intent.putExtra("recipeId", recipeId);
        }
        startActivity(intent);
    }
}
