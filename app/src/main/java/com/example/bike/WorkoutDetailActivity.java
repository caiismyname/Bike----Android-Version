package com.example.bike;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.bike.MainActivity.thisUser;


public class WorkoutDetailActivity extends AppCompatActivity {

    private workoutClass mThisWorkout;
    private DatabaseReference mDatabase;
    private TextView mPayloadView;
    private Button completionButton;
    private Boolean thisUserHasCompleted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("WorkoutDetailActivity", "detail activity oncreate");

        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Grabbing resources
        setContentView(R.layout.activity_workout_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        mPayloadView = (TextView) findViewById(R.id.workout_detail_payload_textfield);
        completionButton = (Button) findViewById(R.id.toggleWorkoutCompletionButton);


        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Getting mThisWorkout from intent
        Bundle b = getIntent().getExtras();
        mThisWorkout = b.getParcelable("selectedWorkout");

        // Setting texts
        toolbar.setTitle(mThisWorkout.type);
        //mPayloadView.setText(mThisWorkout.getUsersHaveCompletedString());
        thisUserHasCompleted = mThisWorkout.usersHaveCompletedList.contains(thisUser.userName);
        //setButtonText();
        reloadData();

    }

    public void completionButton(View view) {
        Log.d("WorkoutDetailView", "completion Button Clicked");

        startWorkoutListener();

        DatabaseReference workoutRef = mDatabase.child("colleges/" + thisUser.college + "/workouts/" + mThisWorkout.getWorkoutName() + "/usersHaveCompleted/" + thisUser.userName);
        if (thisUserHasCompleted) {
            workoutRef.removeValue();
        }
        else {
            workoutRef.setValue(true);
        }
    }

    public void reloadData() {
        setButtonText();
        mPayloadView.setText(mThisWorkout.getUsersHaveCompletedString());
    }

    public void setButtonText() {
        thisUserHasCompleted = mThisWorkout.usersHaveCompletedList.contains(thisUser.userName);

        if (thisUserHasCompleted) {
            completionButton.setText("Incomplete");
        }
        else {
            completionButton.setText("Complete");
        }
    }

    public void startWorkoutListener() {
        Log.d("workoutDetailActivity", "startWorkoutListener");

        DatabaseReference workoutRef = mDatabase.child("colleges/" + thisUser.college + "/workouts/" + mThisWorkout.getWorkoutName());
        ValueEventListener workoutListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("workoutDetailActivity", "onDataChange");
                mThisWorkout = dataSnapshot.getValue(workoutClass.class);
                mThisWorkout.setWorkoutName(dataSnapshot.getKey().toString());
                mThisWorkout.setUsersHaveCompletedList();

                reloadData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FBDB ERROR", databaseError.toString());
            }
        };
        workoutRef.addValueEventListener(workoutListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("WorkoutDetailActivity", "---------------------- onOptionsitemSelected");
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, WorkoutListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
