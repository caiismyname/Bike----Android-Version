package com.example.bike;

/**
 * Created by davidcai on 8/16/16.
 */

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.example.bike.MainActivity.thisUser;

public class announcementClass {
    public String announcementType;
    public String announcementMessage;
    public String announcementTitle;
    public String rideTime;
    public String formattedTime = "";
    public String hostOneSignalUserId;
    public Map<String, String> riders;
    public List<String> ridersUsername = new ArrayList<>();
    public List<String> ridersFullname = new ArrayList<>();

    private DatabaseReference mDatabase;

    announcementClass(String announcementType, String announcementTitle) {
        this.announcementTitle = announcementTitle;
        this.announcementType = announcementType;
    }

    public String getAnnouncementType() {
        return this.announcementType;
    }

    public String getAnnouncementTitle() {
        return this.announcementTitle;
    }

    // Initalizer and helper functions for general messages
    public void initGeneralVars(String message){
        this.announcementMessage = message;
    }

    public String getAnnouncementMessage() {
        return this.announcementMessage;
    }

    // Initalizer and helper functions for ride announcements
    public void initRideVars(String rideTime, String hostOneSignalUserId, Map<String, String>riders){
        this.rideTime = rideTime;
        this.hostOneSignalUserId = hostOneSignalUserId;
        this.riders = riders;
        setOtherRideVars();
    }

    public void setOtherRideVars() {
        formatTime();
        getRiderLists();
    }

    public void formatTime() {

        SimpleDateFormat stringFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        SimpleDateFormat dayFormatter = new SimpleDateFormat("dd");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mm a");
        Date now = new Date();

        try {
            // Converting the ride's time, as a String, to a Date object for comparison
            Date rideTimeDate = stringFormatter.parse(this.rideTime);
            if (now.after(rideTimeDate)) {
                // Ride has passed
                this.formattedTime = "passed";
            } else {
                // Ride is in the future
                // Compare days to see if text should read "Today" or "Tomorrow"
                String nowDay = dayFormatter.format(now);
                String rideDay = dayFormatter.format(rideTimeDate);
                String time = timeFormatter.format(rideTimeDate);
                if (nowDay.equals(rideDay)) {
                    this.formattedTime += "Today " + time;
                } else {
                    this.formattedTime += "Tomorrow " + time;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void getRiderLists() {
        for(Map.Entry<String, String> rider : this.riders.entrySet()) {
            if (rider.getKey().toString().equals("init") == false) {
                this.ridersUsername.add(rider.getKey().toString());
                this.ridersFullname.add(rider.getValue().toString());
            }
        }
    }

    public boolean hasRidePassed() {
        // Returns true if ride has passed, false otherwise
        if (this.formattedTime.equals("passed")){
            return true;
        } else {
            return false;
        }
    }

    public String getRidePayload() {
        String payload = this.formattedTime + "\n";
        if (this.ridersFullname.size() > 0){
            payload += "Joined: ";
            for (String rider : this.ridersFullname) {
                payload += rider + ", ";
            }
        }

        return payload;
    }

    public void joinRide() {
        // Make sure you don't add yourself to your own ride
        if (this.hostOneSignalUserId.equals(thisUser.oneSignalUserId) == false) {
            // Notify host
            try {
                OneSignal.postNotification(new JSONObject("{'contents': {'en': '" + thisUser.fullName + " joined your ride! '}, 'include_player_ids': " + this.hostOneSignalUserId + ", 'data' : {'senderOneSignalUserId': '" + thisUser.oneSignalUserId + "', 'notificationType': 'rideJoined'}}"),
                        new OneSignal.PostNotificationResponseHandler() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                Log.i("OneSignalExample", "postNotification Success: " + response.toString());
                            }

                            @Override
                            public void onFailure(JSONObject response) {
                                Log.e("OneSignalExample", "postNotification Failure: " + response.toString());
                            }
                        });
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Update FBDB
            mDatabase = FirebaseDatabase.getInstance().getReference();
            DatabaseReference rideRef = mDatabase.child("colleges/" + thisUser.college + "/announcements/" + this.announcementTitle + "/riders/" + thisUser.userName);
            rideRef.setValue(thisUser.fullName);
        }
    }

    public void leaveRide() {

        if (this.hostOneSignalUserId.equals(thisUser.oneSignalUserId) == false) {
            // Notify host
            try {
                OneSignal.postNotification(new JSONObject("{'contents': {'en': '" + thisUser.fullName + " left your ride! '}, 'include_player_ids': " + this.hostOneSignalUserId + ", 'data' : {'senderOneSignalUserId': '" + thisUser.oneSignalUserId + "', 'notificationType': 'rideJoined'}}"),
                        new OneSignal.PostNotificationResponseHandler() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                Log.i("OneSignalExample", "postNotification Success: " + response.toString());
                            }

                            @Override
                            public void onFailure(JSONObject response) {
                                Log.e("OneSignalExample", "postNotification Failure: " + response.toString());
                            }
                        });
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Update FBDB
            mDatabase = FirebaseDatabase.getInstance().getReference();
            DatabaseReference rideRef = mDatabase.child("colleges/" + thisUser.college + "/announcements/" + this.announcementTitle + "/riders/" + thisUser.userName);
            rideRef.removeValue();
        }
    }

    // General "get shit"
    public String getPayload() {
        if (this.announcementType.equals("ride")){
            return getRidePayload();
        } else {
            return getAnnouncementMessage();
        }
    }
}
