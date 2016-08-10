package com.example.bike;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.fitness.data.Value;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.example.bike.MainActivity.thisUser;

public class announcementsActivity extends AppCompatActivity {

    private TextView mUpcomingRidesPayload;
    private TextView mAnnouncementOneTitle;
    private TextView mAnnouncementOnePayload;
    private TextView mAnnouncementTwoTitle;
    private TextView mAnnouncementTwoPayload;
    private TextView mAnnouncementThreeTitle;
    private TextView mAnnouncementThreePayload;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcements);

        // Grab Resources
        mUpcomingRidesPayload = (TextView) findViewById(R.id.upcomingRidesPayload);
        mAnnouncementOneTitle = (TextView) findViewById(R.id.announcementOneTitle);
        mAnnouncementOnePayload = (TextView) findViewById(R.id.announcementOnePayload);
        mAnnouncementTwoTitle = (TextView) findViewById(R.id.announcementTwoTitle);
        mAnnouncementTwoPayload = (TextView) findViewById(R.id.announcementTwoPayload);
        mAnnouncementThreeTitle = (TextView) findViewById(R.id.announcementThreeTitle);
        mAnnouncementThreePayload = (TextView) findViewById(R.id.announcementThreePayload);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        setAnnouncements();
    }

    public void setAnnouncements() {
        DatabaseReference ridesRef = mDatabase.child("colleges/" + thisUser.college + "/announcements/rides");
        DatabaseReference generalRef = mDatabase.child("colleges/" + thisUser.college + "/announcements/general");

        // To get rides
        ValueEventListener ridesListener = new ValueEventListener() {
            String ridesPayload = "";
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ride : dataSnapshot.getChildren()) {
                    String rideKey = ride.getKey().toString();
                    if(rideKey.equals("init") == false) {
                        // Pas to parseTime method to see if ride has passed
                        String rideTime = ride.getValue().toString();
                        String dateString = parseTime(rideKey, rideTime);
                        // ParseTime returns "" if the ride has been deleted
                        if (dateString != "") {
                            ridesPayload += rideKey + " -- " + dateString + "\n";
                        }
                    }
                }
                // Setting the text of the TextView
                // Don't overwrite "No upcoming rides" if there are no upcoming rides
                if (ridesPayload != "") {
                    mUpcomingRidesPayload.setText(ridesPayload);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Announcements", databaseError.toString());
            }
        };

        ridesRef.addListenerForSingleValueEvent(ridesListener);

        // To get general announcements
        ValueEventListener generalListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer index = 1;
                for (DataSnapshot announcement : dataSnapshot.getChildren()) {
                    if (announcement.getKey().toString().equals("init") == false) {
                        switch (index) {
                            case 1:
                                mAnnouncementOneTitle.setText(announcement.getKey().toString());
                                mAnnouncementOnePayload.setText(announcement.getValue().toString());
                                index += 1;
                                break;
                            case 2:
                                mAnnouncementTwoTitle.setText(announcement.getKey().toString());
                                mAnnouncementTwoPayload.setText(announcement.getValue().toString());
                                index += 1;
                                break;
                            case 3:
                                mAnnouncementThreeTitle.setText(announcement.getKey().toString());
                                mAnnouncementThreePayload.setText(announcement.getValue().toString());
                                break;
                            default:
                                break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Announcements", databaseError.toString());
            }
        };

        generalRef.addListenerForSingleValueEvent(generalListener);
    }

    public String parseTime(String rideKey, String rideTimeString) {

        DatabaseReference rideAnnouncementRef = mDatabase.child("colleges/" + thisUser.college + "/announcements/rides/" + rideKey);
        String dateString = "";

        SimpleDateFormat stringFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        SimpleDateFormat dayFormatter = new SimpleDateFormat("dd");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mm a");
        Date now = new Date();

        try {
            // Converting the ride's time, as a String, to a Date object for comparison
            Date rideTimeDate = stringFormatter.parse(rideTimeString);
            if (now.after(rideTimeDate)) {
                // Ride has passed
                rideAnnouncementRef.removeValue();
            } else {
                // Ride is in the future
                // Compare days to see if text should read "Today" or "Tomorrow"
                String nowDay = dayFormatter.format(now);
                String rideDay = dayFormatter.format(rideTimeDate);
                String time = timeFormatter.format(rideTimeDate);
                if (nowDay.equals(rideDay)) {
                    dateString += "Today " + time;
                } else {
                    dateString += "Tomorrow " + time;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString;
    }

}
