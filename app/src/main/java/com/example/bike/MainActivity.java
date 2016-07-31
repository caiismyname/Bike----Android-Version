package com.example.bike;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.onesignal.OneSignal;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    userClass thisUser;
    TextView mDisplayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d ("ONCREATE", "ONCREATE");
        setContentView(R.layout.activity_main);


        mDisplayName = (TextView) findViewById(R.id.displayName);
    }

    @Override
    protected  void onStart() {
        super.onStart();
        Log.d ("ONSTART", "onstart");
        OneSignal.startInit(this).init();

        loadUser();
        Log.v ("MainActivity name field", thisUser.firstName);
        mDisplayName.setText(thisUser.firstName);
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
        Log.d("FIRST NAME", "loaded user first name" + firstName);
    }
}
