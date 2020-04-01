package com.beyondthehorizon.safarinjema;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.beyondthehorizon.safarinjema.auth.LoginActivity;
import com.beyondthehorizon.safarinjema.auth.SignUpActivity;

import static com.beyondthehorizon.safarinjema.utils.CheckConnection.IsLoggedIn;
import static com.beyondthehorizon.safarinjema.utils.CheckConnection.REG_APP_PREFERENCES;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        pref = getApplicationContext().getSharedPreferences(REG_APP_PREFERENCES, 0); // 0 - for private mode

        if (pref.getString(IsLoggedIn, "").isEmpty()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}
