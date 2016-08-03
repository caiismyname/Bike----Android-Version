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
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class WorkoutDetailActivity extends AppCompatActivity {

    private workoutClass mThisWorkout;
    private DatabaseReference mDatabase;
    private String workoutUsername;
    private TextView mPayloadView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("WO DEtail Activity", "detail activity oncreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        mPayloadView = (TextView) findViewById(R.id.workout_detail_payload_textfield);


        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Getting mThisWorkout from intent
        Bundle b = getIntent().getExtras();
        mThisWorkout = b.getParcelable("selectedWorkout");

        //loadWorkout();

        toolbar.setTitle(mThisWorkout.type);
        mPayloadView.setText(mThisWorkout.getUsersHaveCompletedString());




        /**
        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {

            Log.d("WorkoutDetailActivity", "---------------------- saved instance state == null");

            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(WorkoutDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(WorkoutDetailFragment.ARG_ITEM_ID));
            WorkoutDetailFragment fragment = new WorkoutDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.workout_detail_container, fragment)
                    .commit();
        }
         */
    }

    public void loadWorkout() {

        Log.d("workoutDetailActivity", "loadWorkout");

        DatabaseReference workoutRef = mDatabase.child("colleges/" + MainActivity.thisUser.college + "/workouts/" + workoutUsername);
        final List<workoutClass> workoutContainer = new ArrayList<workoutClass>();
        ValueEventListener workoutListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mThisWorkout = dataSnapshot.getValue(workoutClass.class);
                Log.d("workoutDetailActivity", "loadWorkout -- " + mThisWorkout.getWeekDate());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FBDB ERROR", databaseError.toString());
            }
        };

        workoutRef.addListenerForSingleValueEvent(workoutListener);
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
