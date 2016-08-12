package com.example.bike;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class bikeListActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private RecyclerView recyclerView;
    private recyclerViewAdapter rva;
    List<bikeClass> bikes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_list);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Setting up the recyclerView
        recyclerView = (RecyclerView) findViewById(R.id.bikeListRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutmanager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutmanager);
        rva = new recyclerViewAdapter(bikes);
        recyclerView.setAdapter(rva);
        getBikeList();
    }

    public void getBikeList() {
        Log.d("bikeList", "getBikeList");
        DatabaseReference bikeListRef = mDatabase.child("colleges/" + thisUser.college + "/bikeList");
        final List<bikeClass> bikeList = new ArrayList<>();

        ValueEventListener bikeListListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot bike : dataSnapshot.getChildren()) {
                    bikeClass currentBike = bike.getValue(bikeClass.class);
                    currentBike.setBikeUserName(bike.getKey());
                    currentBike.setRiderNameLists();
                    bikeList.add(currentBike);
                    rva.updateData(bikeList);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("bikeList", databaseError.toString());
            }
        };
        bikeListRef.addListenerForSingleValueEvent(bikeListListener);
    }

    public void setUserBike(String bikeNameInput) {
        final String bikeName = bikeNameInput;
        new AlertDialog.Builder(this).setTitle("Setting Bike").setMessage("You are about to set this bike as your own")
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Set the bike in FB DB
                final DatabaseReference bikeListRef = mDatabase.child("colleges/" + thisUser.college + "/bikeList");
                ValueEventListener bikeListListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot bike : dataSnapshot.getChildren()){
                            String currentBike = bike.getKey().toString();
                            // Removing user from other bikes
                            if (currentBike.equals(bikeName) == false) {
                                bikeListRef.child(currentBike +"/riders/" + thisUser.userName).removeValue();
                            } else {
                                // Adding the user on the current bike
                                bikeListRef.child(currentBike + "/riders/" + thisUser.userName).setValue(thisUser.fullName);
                            }
                        }

                        // To refresh the view
                        getBikeList();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("bikeList", databaseError.toString());
                    }
                };
                bikeListRef.addListenerForSingleValueEvent(bikeListListener);

                // Set the bike in the local userClass
                thisUser.bikeName = bikeName;
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do nothing
            }
        }).show();
    }

    public class recyclerViewAdapter extends RecyclerView.Adapter<recyclerViewAdapter.bikeViewHolder>{

        List<bikeClass> bikes;

        public class bikeViewHolder extends RecyclerView.ViewHolder {
            CardView cardView;
            TextView bikeName;
            TextView bikeStatus;
            TextView bikeRiders;
            TextView bikeSize;

            bikeViewHolder(View itemView) {
                super(itemView);
                cardView = (CardView) itemView.findViewById(R.id.bikeCardView);
                bikeName = (TextView) itemView.findViewById(R.id.bikeNameLabel);
                bikeStatus = (TextView) itemView.findViewById(R.id.bikeStatusLabel);
                bikeRiders = (TextView) itemView.findViewById(R.id.bikeRidersLabel);
                bikeSize = (TextView) itemView.findViewById(R.id.bikeSizeLabel);
            }
        }

        recyclerViewAdapter(List<bikeClass> bikes) {
            this.bikes = bikes;
        }

        @Override
        public int getItemCount() {return bikes.size();}

        @Override
        public bikeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bike_list_content, viewGroup, false);
            bikeViewHolder bvh = new bikeViewHolder(v);
            return bvh;
        }

        @Override
        public void onBindViewHolder(final bikeViewHolder bvh, int i){
            final bikeClass currentBike = bikes.get(i);
            bvh.bikeName.setText(currentBike.getBikeName());
            bvh.bikeSize.setText(currentBike.getSize());
            bvh.bikeStatus.setText(currentBike.getStatus());
            bvh.bikeRiders.setText(currentBike.getRiders());

            switch (currentBike.status) {
                case "Ready":
                    bvh.bikeName.setTextColor(0xff006600);
                    bvh.bikeSize.setTextColor(0xff006600);
                    bvh.bikeStatus.setTextColor(0xff006600);
                    break;
                case "In Use":
                    bvh.bikeName.setTextColor(0xffcc7a00);
                    bvh.bikeSize.setTextColor(0xffcc7a00);
                    bvh.bikeStatus.setTextColor(0xffcc7a00);
                    break;
                case "Unusable":
                    bvh.bikeName.setTextColor(0xffb30000);
                    bvh.bikeSize.setTextColor(0xffb30000);
                    bvh.bikeStatus.setTextColor(0xffb30000);
                    break;
                default:
                    break;
            }

            bvh.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Log.d("bikeList", "onLongClick");
                    setUserBike(currentBike.getBikeUserName());
                    return true;
                }
            });

            
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView){
            super.onAttachedToRecyclerView(recyclerView);
        }

        public void updateData(List<bikeClass> newData) {
            bikes.clear();
            bikes.addAll(newData);
            notifyDataSetChanged();
        }
    }

}
