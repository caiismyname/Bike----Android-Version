package com.example.bike;

import android.provider.ContactsContract;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.bike.MainActivity.thisUser;

public class announcementsCardViewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private recyclerViewAdapter rva;
    private List<announcementClass> announcements = new ArrayList<>();
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcements_list);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Setting up the recyclerview
        recyclerView = (RecyclerView) findViewById(R.id.announcementsRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        rva = new recyclerViewAdapter(announcements);
        recyclerView.setAdapter(rva);

        getAnnouncements();

    }

    public void getAnnouncements() {
        Log.d("AnnouncementsCardView", "getAnnouncements");
        final DatabaseReference announcementsRef =  mDatabase.child("colleges/" + thisUser.college + "/announcements/");
        final List<announcementClass> tempAnnouncementsList = new ArrayList<>();

        ValueEventListener announcementsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot announcement : dataSnapshot.getChildren()) {
                    String announcementTitle = announcement.getKey().toString();
                    if (announcementTitle.equals("init") == false) {
                        String announcementType = announcement.child("type").getValue().toString();
                        Log.d("AnnouncementsCardView", announcementTitle);
                        announcementClass currentAnnouncement = new announcementClass(announcementType, announcementTitle);

                        if (currentAnnouncement.getAnnouncementType().equals("ride")) {
                            String rideTime = announcement.child("rideTime").getValue().toString();
                            String hostOneSignalUserid = announcement.child("hostOneSignalUserId").getValue().toString();
                            Map<String, String> riders = (Map<String, String>) announcement.child("riders").getValue();
                            currentAnnouncement.initRideVars(rideTime, hostOneSignalUserid, riders);

                        } else {
                            String message = announcement.child("message").getValue().toString();
                            currentAnnouncement.initGeneralVars(message);
                        }
                        // If ride has passed, remove it
                        if (currentAnnouncement.getAnnouncementType().equals("ride")) {
                            if (currentAnnouncement.hasRidePassed()) {
                                announcementsRef.child(announcementTitle).removeValue();
                            } else {
                                tempAnnouncementsList.add(currentAnnouncement);
                                rva.updateData(tempAnnouncementsList);
                            }
                        } else {
                            tempAnnouncementsList.add(currentAnnouncement);
                            rva.updateData(tempAnnouncementsList);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("getAnnouncements", "database error " + databaseError.toString());
            }
        };

        announcementsRef.addListenerForSingleValueEvent(announcementsListener);
    }

    public class recyclerViewAdapter extends RecyclerView.Adapter<recyclerViewAdapter.announcementViewHolder>{

        List<announcementClass> announcements;

        public class announcementViewHolder extends RecyclerView.ViewHolder {
            CardView cardView;
            TextView announcementTitle;
            TextView announcementPayload;

            announcementViewHolder(View itemView) {
                super(itemView);
                cardView = (CardView) itemView.findViewById(R.id.announcementCardView);
                announcementTitle = (TextView) itemView.findViewById(R.id.announcementTitle);
                announcementPayload = (TextView) itemView.findViewById(R.id.announcementPayload);


            }

        }

        recyclerViewAdapter(List<announcementClass> announcements) {this.announcements = announcements;}

        @Override
        public int getItemCount(){
            return announcements.size();
        }

        @Override
        public announcementViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_announcements_content, viewGroup, false);
            announcementViewHolder avn = new announcementViewHolder(v);
            return avn;
        }

        @Override
        public void onBindViewHolder(announcementViewHolder avh, int i) {
            final announcementClass thisAnnouncement = announcements.get(i);
            avh.announcementTitle.setText(announcements.get(i).getAnnouncementTitle());
            avh.announcementPayload.setText(announcements.get(i).getPayload());

            if (thisAnnouncement.getAnnouncementType().equals("ride") == true) {
                avh.announcementTitle.setTextColor(0xff014181);
            }

            avh.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("AnnouncementsCardView", "onClick");
                    if (thisAnnouncement.getAnnouncementType().equals("ride") == true) {
                        thisAnnouncement.joinRide();
                    }

                    getAnnouncements();
                }
            });

            avh.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Log.d("AnnouncementsCardView", "onLongClick");
                    if (thisAnnouncement.getAnnouncementType().equals("ride") == true) {
                        thisAnnouncement.leaveRide();
                    }

                    getAnnouncements();
                    return true;
                }
            });

        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        public void updateData(List<announcementClass> newData){
            announcements.clear();
            announcements.addAll(newData);
            notifyDataSetChanged();
        }

    }
}
