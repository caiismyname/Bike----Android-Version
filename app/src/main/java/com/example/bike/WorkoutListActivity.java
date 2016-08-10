package com.example.bike;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Workouts. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link WorkoutDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class WorkoutListActivity extends AppCompatActivity {


    private List<workoutClass> initWorkoutList = new ArrayList<>();
    private RecyclerView recyclerView; // Is defined here to allow access to it's adapter in the getWorkoutsList method
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        initWorkoutList = getWorkoutsList();

        setContentView(R.layout.activity_workout_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        recyclerView = (RecyclerView) findViewById(R.id.workout_list);
        assert recyclerView != null;
        RecyclerView.Adapter mAdapter = new SimpleItemRecyclerViewAdapter(initWorkoutList);
        recyclerView.setAdapter(mAdapter);

    }

    public List<workoutClass> getWorkoutsList() {
        Log.d("Bike", "getWorkoutsList");
        final List<workoutClass> workoutList = new ArrayList<workoutClass>();
        DatabaseReference workoutListRef = mDatabase.child("colleges/" + MainActivity.thisUser.college + "/workouts");

        ValueEventListener workoutsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    workoutClass currentWorkout = child.getValue(workoutClass.class);
                    currentWorkout.setUsersHaveCompletedLists(); // Called to prepare usersHaveCompletedList, b/c parcelable can't pass maps
                    currentWorkout.setWorkoutName(child.getKey().toString());
                    workoutList.add(currentWorkout);
                }

                // Refresh List to display info
                recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e ("BIKE/FBDB", "Database Error");
            }
        };
        workoutListRef.addListenerForSingleValueEvent(workoutsListener);
        return workoutList;
    }



    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<workoutClass> mValues;

        public SimpleItemRecyclerViewAdapter(List<workoutClass> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.workout_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            Log.d("WorkoutListActivity", "onbindViewHolder");

            //holder.mItem = mValues.get(position).getWorkoutName();
            holder.mIdView.setText(mValues.get(position).getWeekDate());
            holder.mContentView.setText(mValues.get(position).getType());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d("WOList Activity", "onClick");

                    // Intent to transition to workoutDetailActivity
                    Context context = v.getContext();
                    Intent intent = new Intent(context, WorkoutDetailActivity.class);

                    // Bundle that contains the selected workout, as a workoutClass object
                    Bundle b = new Bundle();
                    b.putParcelable("selectedWorkout", mValues.get(position));
                    intent.putExtras(b);

                    context.startActivity(intent);

                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public String mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
