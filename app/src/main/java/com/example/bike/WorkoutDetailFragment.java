package com.example.bike;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a single Workout detail screen.
 * This fragment is either contained in a {@link WorkoutListActivity}
 * in two-pane mode (on tablets) or a {@link WorkoutDetailActivity}
 * on handsets.
 */
public class WorkoutDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    private workoutClass mWorkout;
    private DatabaseReference mDatabase;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WorkoutDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.

            mWorkout = loadWorkout();

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mWorkout.type);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.workout_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mWorkout != null) {
            ((TextView) rootView.findViewById(R.id.workout_detail)).setText(mWorkout.type);
        }

        return rootView;
    }

    public workoutClass loadWorkout() {
        DatabaseReference workoutRef = mDatabase.child("colleges/" + MainActivity.thisUser.college + "/workouts/" + ARG_ITEM_ID);
        final List<workoutClass> workoutContainer = new ArrayList<workoutClass>();
        ValueEventListener workoutListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                workoutClass thisWorkout = dataSnapshot.getValue(workoutClass.class);
                workoutContainer.add(thisWorkout);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FBDB ERROR", databaseError.toString());
            }
        };

        workoutRef.addListenerForSingleValueEvent(workoutListener);
        return workoutContainer.get(0);
    }
}
