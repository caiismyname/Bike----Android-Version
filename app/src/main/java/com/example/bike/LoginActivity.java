package com.example.bike;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OneSignal;

public class LoginActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Button mCreateButton;
    EditText mFirstNameField;
    EditText mLastNameField;
    EditText mEmailField;
    EditText mPasswordField;
    Spinner mCollegeField;
    String collegeValue;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        OneSignal.startInit(this).init();

        mCreateButton = (Button)  findViewById(R.id.createAccountButton);
        mFirstNameField = (EditText) findViewById(R.id.firstNameField);
        mLastNameField = (EditText) findViewById(R.id.lastNameField);
        mEmailField = (EditText) findViewById(R.id.emailField);
        mPasswordField = (EditText) findViewById(R.id.passwordField);
        mCollegeField = (Spinner) findViewById(R.id.collegeSpinnerField);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.loginCollegeChoices, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCollegeField.setAdapter(spinnerAdapter);
        mCollegeField.setOnItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("BIKE", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("BIKE", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

    }

    public void createFirebaseAccount(View view) {
        // This is the function that is associated with the "Create Account" button

        mAuth.createUserWithEmailAndPassword(mEmailField.getText().toString(), mPasswordField.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("BIKE", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            createFBDBEntry();
                            transitionToMain();
                        }
                    }
                });
    }

    public void createFBDBEntry() {
        // Get OneSignal UserID
        final String[] oneSignalUserId = new String[1];
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                Log.d("debug", "User:" + userId);
                oneSignalUserId[0] = userId;
                if (registrationId != null)
                    Log.d("debug", "registrationId:" + registrationId);
            }
        });


        // Create userClass object
        userClass thisUser = new userClass(mFirstNameField.getText().toString(), mLastNameField.getText().toString(), collegeValue, mEmailField.getText().toString(), oneSignalUserId[0], "none");


        // Database Init
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // First DB entry, users/[user]
        DatabaseReference userRef = database.getReference("users/" + thisUser.userName);
        userRef.child("bike").setValue(thisUser.bikeName);
        userRef.child("college").setValue(thisUser.college);
        userRef.child("completedwo").child("init").setValue(true); // Hardcoded here b/c not kept locally
        userRef.child("email").setValue(thisUser.email);
        userRef.child("name").setValue(thisUser.firstName + " " + thisUser.lastName);
        userRef.child("oneSignalUserId").setValue(thisUser.oneSignalUserId);

        //Second DB entry, colleges/[college]/users
        DatabaseReference teamRef = database.getReference("colleges/" + thisUser.college + "/users");
        teamRef.child(thisUser.userName).setValue(true);

        //Save thisUser
        saveUser(thisUser);

    }

    public void saveUser(userClass userToSave) {
        // Create SharedPreferences instance
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("firstName", userToSave.firstName);
        editor.putString("lastName", userToSave.lastName);
        editor.putString("college", userToSave.college);
        editor.putString("email", userToSave.email);
        editor.putString("oneSignalUserId", userToSave.oneSignalUserId);
        editor.putString("bike", userToSave.bikeName);

        Boolean saveStatus = editor.commit();
        Log.d ("BIKE", "User has been saved: " + saveStatus);
    }

    public void transitionToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        collegeValue = parent.getItemAtPosition(pos).toString();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // IDK what to do here for now
    }
}