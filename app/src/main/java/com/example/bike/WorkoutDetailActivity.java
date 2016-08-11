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
    private Button completionButton;
    private TextView mWeekNumber;
    private TextView mWeekDate;
    private TextView mWorkoutType;
    private TextView mWorkoutPayload;
    private TextView mFinishedUsers;
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
        completionButton = (Button) findViewById(R.id.toggleWorkoutCompletionButton);
        mWeekDate = (TextView) findViewById(R.id.workoutWeekDate);
        mWeekNumber = (TextView) findViewById(R.id.workoutWeekDate);
        mWorkoutType = (TextView) findViewById(R.id.workoutType);
        mWorkoutPayload = (TextView) findViewById(R.id.workoutPayload);
        mFinishedUsers = (TextView) findViewById(R.id.workoutFinished);


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
        thisUserHasCompleted = mThisWorkout.usersHaveCompletedUsernameList.contains(thisUser.userName);
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
            workoutRef.setValue(thisUser.fullName);
        }
    }

    public void reloadData() {
        setButtonText();
        mWeekNumber.setText(mThisWorkout.getWeekNumber());
        mWeekDate.setText(mThisWorkout.getWeekDate());
        mWorkoutType.setText(mThisWorkout.getType());
        mWorkoutPayload.setText(mThisWorkout.getWorkoutPayload());
        mFinishedUsers.setText(mThisWorkout.getUsersHaveCompletedString());
    }

    public void setButtonText() {
        thisUserHasCompleted = mThisWorkout.usersHaveCompletedUsernameList.contains(thisUser.userName);

        if (thisUserHasCompleted) {
            completionButton.setText("Mark as Incomplete");
        }
        else {
            completionButton.setText("Mark as Complete");
        }
    }

    public void startWorkoutListener() {
        Log.d("workoutDetailActivity", "startWorkoutListener");

        DatabaseReference workoutRef = mDatabase.child("colleges/" + thisUser.college + "/workouts/" + mThisWorkout.getWorkoutName());
        ValueEventListener workoutListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mThisWorkout = dataSnapshot.getValue(workoutClass.class);
                mThisWorkout.setWorkoutName(dataSnapshot.getKey().toString());
                mThisWorkout.setUsersHaveCompletedLists();

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
