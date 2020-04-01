package com.beyondthehorizon.safarinjema.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beyondthehorizon.safarinjema.MainActivity;
import com.beyondthehorizon.safarinjema.R;
import com.beyondthehorizon.safarinjema.models.SaccoDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import static com.beyondthehorizon.safarinjema.utils.CheckConnection.IsLoggedIn;
import static com.beyondthehorizon.safarinjema.utils.CheckConnection.MyPhoneNumber;
import static com.beyondthehorizon.safarinjema.utils.CheckConnection.REG_APP_PREFERENCES;
import static com.beyondthehorizon.safarinjema.utils.CheckConnection.SaccoName;

public class VerifyPhoneActivity extends AppCompatActivity {

    //These are the objects needed
    //It is the verification id that will be sent to the user
    private String mVerificationId;
    public int counter;
    //The edittext to input the code
    private EditText editTextCode;
    private TextView otp_code;
    private SharedPreferences pref;
    //firebase auth object
    private FirebaseAuth mAuth;
    public static final String TAG = "OtpVerificationActivity";
    private String verificationid;
    public static final int RC_SIGN_IN = 101;
    private boolean mVerificationInProgress = false;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private RelativeLayout parent_1;
    private TextView error_fire_base;
    private SharedPreferences.Editor editor;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
        pref = getApplicationContext().getSharedPreferences(REG_APP_PREFERENCES, 0); // 0 - for private mode

        //initializing objects
        mAuth = FirebaseAuth.getInstance();
        editTextCode = findViewById(R.id.editTextCode);
        parent_1 = findViewById(R.id.parent_1);
        otp_code = findViewById(R.id.otp_code);
        error_fire_base = findViewById(R.id.error_fire_base);
        otp_code.setClickable(false);

        //if the automatic sms detection did not work, user can also enter the code manually
        //so adding a click listener to the button
        findViewById(R.id.buttonSignIn).setOnClickListener(v -> {
            String code = editTextCode.getText().toString().trim();
            if (code.isEmpty() || code.length() < 6) {
                editTextCode.setError("Enter valid code");
                editTextCode.requestFocus();
                return;
            }
            //verifying the code entered manually
            verifyPhoneNumberWithCode(verificationid, code);
        });

        otp_code.setOnClickListener(v -> {
            startCountDown();//getting mobile number from the previous activity
            //and sending the verification code to the number
            String mobile = pref.getString(MyPhoneNumber, "");
            startPhoneNumberVerification(mobile);
        });

        startCountDown();
    }

    private void startCountDown() {
        counter = 60;
        otp_code.setTextColor(Color.parseColor("#000000"));
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                otp_code.setText(" " + counter);
                counter--;
            }

            public void onFinish() {
                otp_code.setText("  RESEND!");
                otp_code.setClickable(true);
                otp_code.setTextColor(Color.parseColor("#00ff44"));
            }
        }.start();
    }


    @Override
    protected void onStart() {
        super.onStart();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted: " + phoneAuthCredential.getSmsCode());

                editTextCode.setText(phoneAuthCredential.getSmsCode());
                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
//                Toast.makeText(OtpVerificationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                error_fire_base.setText(e.getMessage());
                otp_code.setVisibility(View.GONE);

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                // super.onCodeSent(s, forceResendingToken);
                verificationid = s;
            }
        };

        //getting mobile number from the previous activity and sending the verification code to the number
        String mobile = pref.getString(MyPhoneNumber, "");
        startPhoneNumberVerification(mobile);
    }

    private void startPhoneNumberVerification(String phoneNumber) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        Log.d(TAG, "verifyPhoneNumberWithCode: " + code);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = task.getResult().getUser();


                        mDatabase = FirebaseDatabase.getInstance().getReference();
                        String key = mDatabase.child("saccos").push().getKey();

                        mDatabase.child("saccos")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Log.e(TAG, "onDataChange: " + dataSnapshot);



//                                        if (dataSnapshot.exists()) {
//                                            Toast.makeText(VerifyPhoneActivity.this, "Sacco already registered", Toast.LENGTH_LONG).show();
//                                        }
//
//                                        else {


//                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            editTextCode.setError("Invalid code.");

                            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent_1), "Invalid code.", Snackbar.LENGTH_LONG);
                            snackbar.setAction("Try again", v -> {
                                //getting mobile number from the previous activity and sending the verification code to the number
                                String mobile = pref.getString(MyPhoneNumber, "");
                                startPhoneNumberVerification(mobile);
                                startCountDown();   //getting mobile number from the previous activity
                            });
                            snackbar.show();

                        }

                    }
                });
    }
}