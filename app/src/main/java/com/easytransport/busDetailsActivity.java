package com.easytransport;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class busDetailsActivity extends AppCompatActivity {

    private TextView routeBusTextView, busDetailsTextView;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_details);

        databaseReference = FirebaseDatabase.getInstance().getReference("route");

        routeBusTextView = findViewById(R.id.routeBusTextView);
        busDetailsTextView = findViewById(R.id.busDetailsTextView);

        // Get the intent and extract the data
        Intent intent = getIntent();
        String routeName = intent.getStringExtra("ROUTE_NAME");
        String busName = intent.getStringExtra("BUS_NAME");

        DatabaseReference busRef = databaseReference.child(routeName).child(busName);

        // Set the text views with the received data
        routeBusTextView.setText(routeName + "\nBus: " + busName);

        // Fetch and display the bus details
        busRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    StringBuilder busDetailsBuilder = new StringBuilder();
                    for (DataSnapshot detailSnapshot : dataSnapshot.getChildren()) {
                        String key = detailSnapshot.getKey();
                        String value = detailSnapshot.getValue(String.class);
                        if (key != null && !key.equals("password")) { // Exclude tickets from details
                            busDetailsBuilder.append(key).append(": ").append(value).append("\n\n");
                        }
                    }
                    busDetailsTextView.setText(busDetailsBuilder.toString().trim());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
}
