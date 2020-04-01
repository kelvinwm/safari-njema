package com.beyondthehorizon.safarinjema.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.beyondthehorizon.safarinjema.MainActivity;
import com.beyondthehorizon.safarinjema.R;
import com.beyondthehorizon.safarinjema.adapter.PassengersAdapter;
import com.beyondthehorizon.safarinjema.auth.LoginActivity;
import com.beyondthehorizon.safarinjema.models.SaccoDetails;
import com.beyondthehorizon.safarinjema.models.TripModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.beyondthehorizon.safarinjema.utils.CheckConnection.IsLoggedIn;
import static com.beyondthehorizon.safarinjema.utils.CheckConnection.REG_APP_PREFERENCES;
import static com.beyondthehorizon.safarinjema.utils.CheckConnection.SaccoName;

public class ViewPassengersActivity extends AppCompatActivity {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private DatabaseReference mDatabase;
    private ProgressDialog progressDialog;
    private PassengersAdapter passengersAdapter;
    private RecyclerView recyclerView;
    private SearchView searchView;

    private ArrayList<TripModel> tripModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_passengers);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        searchView = findViewById(R.id.searchView);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        pref = getApplicationContext().getSharedPreferences(REG_APP_PREFERENCES, 0);
        editor = pref.edit();

        mDatabase.child("passengers").child(pref.getString(SaccoName, ""))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                TripModel tripModel = data.getValue(TripModel.class);

                                tripModelArrayList.add(tripModel);
                            }
                            progressDialog.dismiss();
                            passengersAdapter = new PassengersAdapter(getApplicationContext(), tripModelArrayList);
                            recyclerView.setAdapter(passengersAdapter);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!(newText.toLowerCase().trim().isEmpty())) {
                    passengersAdapter.getFilter().filter(newText);
                }
                return false;
            }
        });
    }
}
