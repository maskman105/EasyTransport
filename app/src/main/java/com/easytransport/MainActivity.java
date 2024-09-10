package com.easytransport;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    public EditText edtFromText, edtToText;
    public Button searchButton, refreshButton, authorButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        edtFromText = findViewById(R.id.edtFromText);
        edtToText = findViewById(R.id.edtToText);

        searchButton = findViewById(R.id.searchButton);
        refreshButton = findViewById(R.id.refreshButton);
        authorButton = findViewById(R.id.authorButton);  // Initialize the Author button

        // Search button click listener
        searchButton.setOnClickListener(v -> {
            String FromText = edtFromText.getText().toString();
            String ToText = edtToText.getText().toString();

            if (FromText.isEmpty() || ToText.isEmpty()) {
                Toast.makeText(MainActivity.this, "Blank Input", Toast.LENGTH_SHORT).show();
            } else {
                goToEasyTransport2();
            }
        });

        // Refresh button to clear both input fields
        refreshButton.setOnClickListener(v -> {
            edtFromText.setText("");
            edtToText.setText("");
            Toast.makeText(MainActivity.this, "Refreshed The Text", Toast.LENGTH_SHORT).show();
        });

        // Author button click listener
        authorButton.setOnClickListener(v -> goToAuthorActivity());
    }

    // Navigate to easyTransport2 Activity
    public void goToEasyTransport2() {
        Intent intent = new Intent(MainActivity.this, easyTransport2.class);
        String from = edtFromText.getText().toString();
        String to = edtToText.getText().toString();
        String from_to = from + "-" + to;
        intent.putExtra("F_T", from_to); // Pass search query to easyTransport2
        startActivity(intent);
    }

    // Navigate to AuthorActivity
    public void goToAuthorActivity() {
        Intent intent = new Intent(MainActivity.this, author.class);
        startActivity(intent);  // Start the Author activity
    }
}
