package com.example.bike;

import android.graphics.Color;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_list);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        List<bikeClass> bikes = new ArrayList<>();
        bikes = getBikeList();

        // Setting up the recyclerView
        recyclerView = (RecyclerView) findViewById(R.id.bikeListRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutmanager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutmanager);
        recyclerViewAdapter rva = new recyclerViewAdapter(bikes);
        recyclerView.setAdapter(rva);

    }

    public List<bikeClass> getBikeList() {
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
                }
                recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("bikeList", databaseError.toString());
            }
        };
        bikeListRef.addListenerForSingleValueEvent(bikeListListener);
        return bikeList;
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
        public void onBindViewHolder(bikeViewHolder bvh, int i){
            bikeClass currentBike = bikes.get(i);
            bvh.bikeName.setText(currentBike.getBikeName());
            bvh.bikeSize.setText(currentBike.getSize());
            bvh.bikeStatus.setText(currentBike.getStatus());
            String ridersString = currentBike.getRiders();
            if (ridersString.equals("") == false) {
                bvh.bikeRiders.setText(ridersString);
            }

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


        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView){
            super.onAttachedToRecyclerView(recyclerView);
        }
    }

}
