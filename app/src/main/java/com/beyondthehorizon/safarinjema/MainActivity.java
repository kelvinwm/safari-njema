package com.beyondthehorizon.safarinjema;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beyondthehorizon.safarinjema.auth.LoginActivity;
import com.beyondthehorizon.safarinjema.auth.SignUpActivity;
import com.beyondthehorizon.safarinjema.models.TripModel;
import com.beyondthehorizon.safarinjema.views.ViewPassengersActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.beyondthehorizon.safarinjema.utils.CheckConnection.ConductorName;
import static com.beyondthehorizon.safarinjema.utils.CheckConnection.CountControl;
import static com.beyondthehorizon.safarinjema.utils.CheckConnection.Destination;
import static com.beyondthehorizon.safarinjema.utils.CheckConnection.DriverName;
import static com.beyondthehorizon.safarinjema.utils.CheckConnection.REG_APP_PREFERENCES;
import static com.beyondthehorizon.safarinjema.utils.CheckConnection.SaccoName;
import static com.beyondthehorizon.safarinjema.utils.CheckConnection.TotalPassengers;
import static com.beyondthehorizon.safarinjema.utils.CheckConnection.UserFrom;
import static com.beyondthehorizon.safarinjema.utils.CheckConnection.VehicleNumber;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Button addNewTrip, addNewTripp, addPassenger;
    private TextView saccoName, numberPlate, totalPassengers, driverName;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private LinearLayout tripDetails;
    private DatabaseReference mDatabase;
    private EditText lastName, firstName, idNo, phoneNo, seatNo;
    private ProgressDialog progressDialog;
    private int countControl;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seatNo = findViewById(R.id.seatNo);
        phoneNo = findViewById(R.id.phoneNo);
        idNo = findViewById(R.id.idNo);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        addPassenger = findViewById(R.id.addPassenger);
        driverName = findViewById(R.id.driverName);
        tripDetails = findViewById(R.id.tripDetails);
        addNewTrip = findViewById(R.id.addNewTrip);
        addNewTripp = findViewById(R.id.addNewTripp);
        saccoName = findViewById(R.id.saccoName);
        numberPlate = findViewById(R.id.numberPlate);
        totalPassengers = findViewById(R.id.totalPassengers);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        pref = getApplicationContext().getSharedPreferences(REG_APP_PREFERENCES, 0);
        editor = pref.edit();

        progressDialog = new ProgressDialog(this);
        addNewTrip.setOnClickListener(v -> showCustomDialog());
        addNewTripp.setOnClickListener(v -> showCustomDialog());
        mDatabase = FirebaseDatabase.getInstance().getReference();


        if (pref.getString(VehicleNumber, "").isEmpty()) {
            showCustomDialog();
            tripDetails.setVisibility(View.GONE);
            addNewTripp.setVisibility(View.VISIBLE);
        } else {
            updateTripDetails();
        }

        addPassenger.setOnClickListener(v -> {
            String fname = firstName.getText().toString().trim();
            String lname = lastName.getText().toString().trim();
            String pNo = phoneNo.getText().toString().trim();
            String idNum = idNo.getText().toString().trim();
            String seatNum = seatNo.getText().toString().trim();
            progressDialog.setMessage("Please wait..Adding passenger");
            progressDialog.setCanceledOnTouchOutside(false);

            if (pref.getInt(CountControl, 0) == Integer.parseInt(pref.getString(TotalPassengers, ""))) {
                Toast.makeText(this, "Maximum passengers for the trip reached", Toast.LENGTH_LONG).show();
                return;
            }
            if (fname.isEmpty()) {
                Toast.makeText(this, "First name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (lname.isEmpty()) {
                Toast.makeText(this, "Last name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (pNo.isEmpty()) {
                Toast.makeText(this, "Phone number cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm a", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());
            String key = mDatabase.child("passengers").push().getKey();
            progressDialog.show();
            mDatabase.child("passengers").child(pref.getString(SaccoName, "")).child(key).setValue(new TripModel(
                    pref.getString(SaccoName, ""),
                    pref.getString(DriverName, ""),
                    pref.getString(ConductorName, ""),
                    pref.getString(VehicleNumber, ""),
                    pref.getString(UserFrom, ""),
                    pref.getString(Destination, ""),
                    currentDateandTime,
                    fname + " " + lname,
                    idNum,
                    pNo,
                    pref.getString(TotalPassengers, ""),
                    seatNum
            )).addOnCompleteListener(task -> {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    firstName.setText("");
                    lastName.setText("");
                    idNo.setText("");
                    phoneNo.setText("");
                    seatNo.setText("");

                    countControl = pref.getInt(CountControl, 0);
                    countControl += 1;
                    editor.putInt(CountControl, countControl);
                    editor.apply();
                    Toast.makeText(MainActivity.this, "Passenger added Successfully", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(MainActivity.this, "Unable to add passenger. Try again", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void updateTripDetails() {
        addNewTripp.setVisibility(View.GONE);
        tripDetails.setVisibility(View.VISIBLE);

        saccoName.setText("Sacco Name: " + pref.getString(SaccoName, ""));
        driverName.setText("Driver Name: " + pref.getString(DriverName, ""));
        numberPlate.setText("Loading Vehicle: " + pref.getString(VehicleNumber, ""));
        totalPassengers.setText("Total Passenger Count: " + pref.getString(TotalPassengers, ""));
    }

    private void showCustomDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.new_trip_item, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        EditText numberPlate = dialogView.findViewById(R.id.numberPlate);
        EditText driverName = dialogView.findViewById(R.id.driverName);
        EditText conductorName = dialogView.findViewById(R.id.conductorName);
        EditText userFrom = dialogView.findViewById(R.id.userFrom);
        EditText userDestination = dialogView.findViewById(R.id.userDestination);
        EditText totalPassengers = dialogView.findViewById(R.id.totalPassengers);
        Button addTrip = dialogView.findViewById(R.id.addTrip);
        //finally creating the alert dialog and displaying it
        AlertDialog alertDialog = builder.create();
        if (alertDialog.getWindow() != null)
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
        alertDialog.show();

        addTrip.setOnClickListener(v -> {
            String NumberPlate = numberPlate.getText().toString().trim();
            String drivername = driverName.getText().toString().trim();
            String conductorname = conductorName.getText().toString().trim();
            String userfrom = userFrom.getText().toString().trim();
            String total = totalPassengers.getText().toString().trim();
            String userdestination = userDestination.getText().toString().trim();

            if (NumberPlate.isEmpty()) {
                Toast.makeText(MainActivity.this, "NumberPlate cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (drivername.isEmpty()) {
                Toast.makeText(MainActivity.this, "Driver name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (conductorname.isEmpty()) {
                Toast.makeText(MainActivity.this, "Conductor name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (userfrom.isEmpty()) {
                Toast.makeText(MainActivity.this, "Add pickup location", Toast.LENGTH_SHORT).show();
                return;
            }
            if (userdestination.isEmpty()) {
                Toast.makeText(MainActivity.this, "Destination cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (total.isEmpty()) {
                Toast.makeText(MainActivity.this, "Passenger count cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            editor.putString(TotalPassengers, total);
            editor.putString(VehicleNumber, NumberPlate);
            editor.putString(DriverName, drivername);
            editor.putString(ConductorName, conductorname);
            editor.putString(UserFrom, userfrom);
            editor.putString(Destination, userdestination);
            editor.apply();
            updateTripDetails();

            alertDialog.dismiss();
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_add:
                showCustomDialog();
                return true;
            case R.id.action_settings:
                showCustomDialog();
                return true;
            case R.id.action_passengers:
                startActivity(new Intent(MainActivity.this, ViewPassengersActivity.class));
                return true;
            case R.id.action_logout:
                logoutUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logoutUser() {
        mAuth.signOut();
        editor.clear();
        editor.apply();
        //verification successful we will start the profile activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: ");
    }
}
