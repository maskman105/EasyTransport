package com.easytransport;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class author extends AppCompatActivity {

    private EditText routeEditText, busNameEditText, remainingTicketsEditText, passwordEditText;
    private Button okButton;
    private DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_author);

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("route");

        // Initialize EditTexts and Button
        routeEditText = findViewById(R.id.routeEditText);
        busNameEditText = findViewById(R.id.busNameEditText);
        remainingTicketsEditText = findViewById(R.id.remainingTicketsEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        okButton = findViewById(R.id.okButton);


        // Set OK button click listener
        okButton.setOnClickListener(v -> {
            String routeName = formatInput(routeEditText.getText().toString());
            String busName = busNameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String remainingTickets = remainingTicketsEditText.getText().toString();

            // Check if any input is empty
            if (TextUtils.isEmpty(routeName) || TextUtils.isEmpty(busName) || TextUtils.isEmpty(password) || TextUtils.isEmpty(remainingTickets)) {
                Toast.makeText(author.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();

                return;
            }

            // Check if password is correct and update remaining tickets
            validatePasswordAndUpdateTickets(routeName, busName, password, remainingTickets);
        });
    }

    // Helper method to format input: makes it case-insensitive and removes spaces
    private String formatInput(String input) {
        if (TextUtils.isEmpty(input)) {
            return ""; // Return empty if the input is empty
        }
        return input.toLowerCase().replaceAll("\\s+", "");
    }

    // Method to validate the password and update remaining tickets if correct
    private void validatePasswordAndUpdateTickets(String routeName, String busName, String inputPassword, String remainingTickets) {
        // Reference to the bus under the specific route
        DatabaseReference busRef = databaseReference.child(routeName).child(busName);

        // Retrieve the stored password and check if it matches the input password
        busRef.child("password").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String storedPassword = dataSnapshot.getValue(String.class);

                if (storedPassword != null && storedPassword.equals(inputPassword)) {
                    // Password is correct, update remaining tickets
                    busRef.child("RemainingTickets").setValue(remainingTickets)
                            .addOnSuccessListener(aVoid -> {
                                // Successfully updated
                                Toast.makeText(author.this, "Remaining tickets updated successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(author.this, MainActivity.class);
                                startActivity(intent);
                            })
                            .addOnFailureListener(e -> {
                                // Failed to update
                                Toast.makeText(author.this, "Failed to update tickets: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    // Password is incorrect
                    Toast.makeText(author.this, "Incorrect password or input", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
                Toast.makeText(author.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
