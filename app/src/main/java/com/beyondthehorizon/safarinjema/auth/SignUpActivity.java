package com.beyondthehorizon.safarinjema.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.beyondthehorizon.safarinjema.MainActivity;
import com.beyondthehorizon.safarinjema.R;
import com.beyondthehorizon.safarinjema.models.SaccoDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import static com.beyondthehorizon.safarinjema.utils.CheckConnection.IsLoggedIn;
import static com.beyondthehorizon.safarinjema.utils.CheckConnection.MyPhoneNumber;
import static com.beyondthehorizon.safarinjema.utils.CheckConnection.REG_APP_PREFERENCES;
import static com.beyondthehorizon.safarinjema.utils.CheckConnection.SaccoName;
import static com.beyondthehorizon.safarinjema.utils.CheckConnection.getConnectivityStatusString;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";
    private EditText editTextMobile, saccoName, saccoRegNo, saccoEmail, saccoPassword, saccoConfirmPassword;
    private ProgressBar progressBar;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private CountryCodePicker ccp;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().setTitle("Create Account");

        saccoConfirmPassword = findViewById(R.id.saccoConfirmPassword);
        saccoPassword = findViewById(R.id.saccoPassword);
        saccoEmail = findViewById(R.id.saccoEmail);
        saccoRegNo = findViewById(R.id.saccoRegNo);
        saccoName = findViewById(R.id.saccoName);
        editTextMobile = findViewById(R.id.editTextMobile);
        progressBar = findViewById(R.id.progressbar);
        pref = getApplicationContext().getSharedPreferences(REG_APP_PREFERENCES, 0); // 0 - for private mode
        editor = pref.edit();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        ccp = findViewById(R.id.ccp);
        editTextMobile = findViewById(R.id.editTextMobile);
        ccp.registerPhoneNumberTextView(editTextMobile);

        findViewById(R.id.buttonContinue).setOnClickListener(v -> {
//            new CheckConnection.InternetCheck().execute();

            String mobile = editTextMobile.getText().toString().trim();
            String sacconame = saccoName.getText().toString().trim();
            String saccoNo = saccoRegNo.getText().toString().trim();
            String saccoemail = saccoEmail.getText().toString().trim();

            String saccopassword = saccoPassword.getText().toString().trim();
            String saccoConfirmpassword = saccoConfirmPassword.getText().toString().trim();
//
            if (sacconame.isEmpty()) {
                saccoName.setError("Enter a valid name");
                saccoName.requestFocus();
                return;
            }
            if (saccoNo.isEmpty()) {
                saccoRegNo.setError("Cannot be empty");
                saccoRegNo.requestFocus();
                return;
            }
            if (saccoemail.isEmpty()) {
                saccoEmail.setError("Cannot be empty");
                saccoEmail.requestFocus();
                return;
            }
            if (saccopassword.isEmpty()) {
                saccoPassword.setError("Cannot be empty");
                saccoPassword.requestFocus();
                return;
            }
            if (saccopassword.compareTo(saccoConfirmpassword) != 0) {
                saccoConfirmPassword.setError("Password did not match");
                saccoConfirmPassword.requestFocus();
                return;
            }
            if (mobile.isEmpty() || mobile.length() < 5) {
                editTextMobile.setError("Enter a valid mobile");
                editTextMobile.requestFocus();
                return;
            }
//            progressBar.setVisibility(View.VISIBLE);
//            String lastNine = mobile.substring(mobile.length() - 9);
//            String mobile2 = "+254" + lastNine;
            if (!getConnectivityStatusString(SignUpActivity.this)) {
                Toast.makeText(SignUpActivity.this, "No Internet connection", Toast.LENGTH_SHORT).show();
                return;
            }

//            new Handler().postDelayed(() -> {

//                if (hasActiveInternetConnection()) {
            editor.putString(SaccoName, sacconame);
//            editor.putString("saccoNo", saccoNo);
//            editor.putString("saccoemail", saccoemail);
//            editor.putString("saccopassword", saccopassword);
            editor.putString(MyPhoneNumber, ccp.getNumber());
            editor.apply();

            mAuth.createUserWithEmailAndPassword(saccoemail, saccopassword)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");

                            FirebaseUser user = mAuth.getCurrentUser();
                            String key = mDatabase.child("saccos").push().getKey();

                            mDatabase.child("saccos").child(key).setValue(new SaccoDetails(
                                    pref.getString(SaccoName, ""),
                                    pref.getString("saccoNo", ""),
                                    pref.getString(MyPhoneNumber, ""),
                                    pref.getString("saccoemail", ""),
                                    pref.getString("saccopassword", "")

                            )).addOnCompleteListener(task1 -> {

                                if (task1.isSuccessful()) {
                                    editor = pref.edit();
//                                    editor.putString(MyPhoneNumber, pref.getString(MyPhoneNumber, ""));
                                    editor.putString(IsLoggedIn, "IsLoggedIn");
                                    editor.apply();

                                    //verification successful we will start the profile activity
                                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(SignUpActivity.this, "Unable to register, Try Again", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                                updateUI(null);
                        }

                    });


//            Intent intent = new Intent(SignUpActivity.this, VerifyPhoneActivity.class);
//            startActivity(intent);
//                } else {
//                    Toast.makeText(SignUpActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
//                    progressBar.setVisibility(View.GONE);
//                }
//            }, 1000);

        });
    }
}
