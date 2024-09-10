package com.easytransport;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.easytransport.R;
import com.easytransport.busDetailsActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class easyTransport2 extends AppCompatActivity {

    private DatabaseReference myRef;
    private ListView listViewBusNames;
    private String currentlySelectedBus = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy_transport2);

        listViewBusNames = findViewById(R.id.listviewBusNames);

        Intent intent = getIntent();
        String from_to = intent.getStringExtra("F_T");

        // Normalize the search query
        String searchQuery = normalizeString(from_to);

        // Initialize Firebase reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("route");  // Referencing the routes node

        // Fetch and display bus names
        getBusNames(searchQuery);
    }

    private String normalizeString(String input) {
        return input.toLowerCase().replaceAll("\\s+", "");
    }

    private void getBusNames(String parentName) {
        myRef.child(parentName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<String> busNames = new ArrayList<>();
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        busNames.add(childSnapshot.getKey());
                    }
                    displayBusNames(busNames, dataSnapshot);
                } else {
                    Toast.makeText(easyTransport2.this, "No data found for " + parentName, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(easyTransport2.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error retrieving data", databaseError.toException());
            }
        });
    }

    private void displayBusNames(List<String> busNames, DataSnapshot parentSnapshot) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, busNames);
        listViewBusNames.setAdapter(adapter);

        listViewBusNames.setOnItemClickListener((parent, view, position, id) -> {
            String selectedBus = busNames.get(position);
            DataSnapshot selectedBusSnapshot = parentSnapshot.child(selectedBus);

            if (selectedBusSnapshot.exists() && selectedBusSnapshot.hasChildren()) {
                // Prepare data to pass to busDetailsActivity
                Intent intent = new Intent(easyTransport2.this, busDetailsActivity.class);
                intent.putExtra("ROUTE_NAME", normalizeString(getIntent().getStringExtra("F_T")));
                intent.putExtra("BUS_NAME", selectedBus);

                // Prepare bus details string
                StringBuilder busDetailsBuilder = new StringBuilder();
                for (DataSnapshot detailSnapshot : selectedBusSnapshot.getChildren()) {
                    String detail = detailSnapshot.getValue(String.class);
                    if (detail != null) {
                        busDetailsBuilder.append(detail).append("\n");
                    }
                }
                intent.putExtra("BUS_DETAILS", busDetailsBuilder.toString().trim());
                startActivity(intent);
            } else {
                Toast.makeText(easyTransport2.this, "No details found for this bus.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

