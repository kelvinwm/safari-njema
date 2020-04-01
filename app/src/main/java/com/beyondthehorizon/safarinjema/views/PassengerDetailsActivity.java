package com.beyondthehorizon.safarinjema.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.beyondthehorizon.safarinjema.R;
import com.beyondthehorizon.safarinjema.models.TripModel;
import com.google.gson.Gson;

import static com.beyondthehorizon.safarinjema.utils.CheckConnection.PassengerDetails;
import static com.beyondthehorizon.safarinjema.utils.CheckConnection.REG_APP_PREFERENCES;

public class PassengerDetailsActivity extends AppCompatActivity {

    private static final String TAG = "PassengerDetails";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private TextView route, driverName, conductorName, numberPlate, totalPassengers, travelDate;
    private TextView names, phoneNo, idNo, seatNo, saccoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_details);

        Gson gson = new Gson();
        pref = getApplicationContext().getSharedPreferences(REG_APP_PREFERENCES, 0);
        editor = pref.edit();

        route = findViewById(R.id.route);
        driverName = findViewById(R.id.driverName);
        conductorName = findViewById(R.id.conductorName);
        numberPlate = findViewById(R.id.numberPlate);
        totalPassengers = findViewById(R.id.totalPassengers);
        travelDate = findViewById(R.id.travelDate);
        saccoName = findViewById(R.id.saccoName);

        names = findViewById(R.id.names);
        phoneNo = findViewById(R.id.phoneNo);
        idNo = findViewById(R.id.idNo);
        seatNo = findViewById(R.id.seatNo);

        TripModel tripModel = gson.fromJson(pref.getString(PassengerDetails, ""), TripModel.class);

        Log.e(TAG, "onCreate: " + tripModel.getNames());

        saccoName.setText("Trip Details " + tripModel.getSaccoName());
        route.setText(tripModel.getFrom() + " - " + tripModel.getDestination());
        driverName.setText(tripModel.getDriverName());
        conductorName.setText(tripModel.getConductorName());
        numberPlate.setText(tripModel.getNumberPlate());
        totalPassengers.setText(tripModel.getPassengerCount());
        travelDate.setText(tripModel.getDateTime());

        names.setText(tripModel.getNames());
        phoneNo.setText("Phone No : " + tripModel.getPhoneNo());
        idNo.setText("ID No : " + tripModel.getIdNo());
        seatNo.setText("Seat No : " + tripModel.getSeatNo());
    }
}
