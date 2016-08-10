package com.example.bike;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.onesignal.OneSignal;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static userClass thisUser;
    private TextView mDisplayName;
    private Button mAnnouncementsButton;
    private Button mWorkoutsButton;
    private Button mBikesButton;
    private Button mGoRideButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDisplayName = (TextView) findViewById(R.id.displayName);
        mAnnouncementsButton = (Button) findViewById(R.id.announcementsButton);
        mWorkoutsButton = (Button) findViewById(R.id.workoutsButton);
        mBikesButton = (Button) findViewById(R.id.bikesButton);
        mGoRideButton = (Button) findViewById(R.id.goRideButton);
    }

    @Override
    protected  void onStart() {
        super.onStart();

        // Retrieve our hasAccount flag to trigger (or not) LoginActivity
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean hasAccount = prefs.getBoolean("hasAccount", false);

        // Logic to trigger LoginActivity
        if (hasAccount) {
            // Has account. Do normal stuff
            OneSignal.startInit(this).init();
            loadUser();
            mDisplayName.setText(thisUser.firstName);
        }
        else {
            // Does not have account
            Intent transitionToLoginActivity = new Intent(this, LoginActivity.class);
            startActivity(transitionToLoginActivity);
        }

    }

    public void loadUser() {
        // Takes information from shared file and creates a userClass object out of it

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        String firstName = pref.getString("firstName", null);
        String lastName = pref.getString("lastName", null);
        String college = pref.getString("college", null);
        String email = pref.getString("email", null);
        String oneSignalUserId = pref.getString("oneSignalUserId", null);
        String bike = pref.getString("bike", null);

        thisUser = new userClass(firstName, lastName, college, email, oneSignalUserId, bike);
        Log.d ("LOAD USER", "USER LOADED");
    }

    public void transitionToAnnouncements(View view){
        Intent transitionToAnnouncements = new Intent(this, announcementsActivity.class);
        startActivity(transitionToAnnouncements);
    }

    public void transitionToWorkouts(View view) {
        Intent transitionToWorkouts = new Intent(this, WorkoutListActivity.class);
        startActivity(transitionToWorkouts);
    }

    public void transitionToGoRide(View view) {
        Intent transitionToGoRide = new Intent(this, goRideActivity.class);
        startActivity(transitionToGoRide);
    }
}
