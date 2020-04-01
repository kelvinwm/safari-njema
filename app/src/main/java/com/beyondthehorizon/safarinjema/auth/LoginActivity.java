package com.beyondthehorizon.safarinjema.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.beyondthehorizon.safarinjema.MainActivity;
import com.beyondthehorizon.safarinjema.R;
import com.beyondthehorizon.safarinjema.models.SaccoDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.beyondthehorizon.safarinjema.utils.CheckConnection.IsLoggedIn;
import static com.beyondthehorizon.safarinjema.utils.CheckConnection.REG_APP_PREFERENCES;
import static com.beyondthehorizon.safarinjema.utils.CheckConnection.SaccoName;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private TextView signUp;
    private EditText saccoEmail, saccoPassword;
    private ImageButton buttonContinue;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        getSupportActionBar().setTitle("Login");

        buttonContinue = findViewById(R.id.buttonContinue);
        saccoEmail = findViewById(R.id.saccoEmail);
        saccoPassword = findViewById(R.id.saccoPassword);
        signUp = findViewById(R.id.signUp);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);
        signUp.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignUpActivity.class)));

        pref = getApplicationContext().getSharedPreferences(REG_APP_PREFERENCES, 0); // 0 - for private mode
        editor = pref.edit();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        buttonContinue.setOnClickListener(v -> {
            String email = saccoEmail.getText().toString().trim();
            String password = saccoPassword.getText().toString().trim();
            if (email.isEmpty()) {
                saccoEmail.setError("Enter a valid email");
                saccoEmail.requestFocus();
                return;
            }
            if (password.isEmpty()) {
                saccoPassword.setError("Enter a valid email");
                saccoPassword.requestFocus();
                return;
            }
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            mDatabase.child("saccos").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.exists()) {

                                        for (DataSnapshot datas : dataSnapshot.getChildren()) {
                                            SaccoDetails saccoDetails = datas.getValue(SaccoDetails.class);

                                            if (saccoDetails.getSaccoEmail().compareTo(email) == 0) {
                                                progressDialog.dismiss();
                                                editor = pref.edit();
                                                editor.putString(IsLoggedIn, "IsLoggedIn");
                                                editor.putString(SaccoName, saccoDetails.getSaccoName());
                                                editor.apply();
                                                // verification successful we will start the profile activity
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }


                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());

                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                                    updateUI(null);
//                                    // ...
                        }

                        // ...
                    });
//                mDatabase.child("saccos").child(email).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists()) {
//                            SaccoDetails saccoDetails = dataSnapshot.getValue(SaccoDetails.class);
//
//                            Log.e(TAG, "onDataChange: " + saccoDetails);
//                            Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
//
//                        }else{
//                            Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
        });
    }
}
