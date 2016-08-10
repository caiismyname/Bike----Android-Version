package com.example.bike;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.bike.MainActivity.thisUser;

public class goRideActivity extends AppCompatActivity {

    private Button mNowButton;
    private Button mTenMinutesButton;
    private Button mThrityMinutesButton;
    private Button mSixtyMinuteButton;
    private Button mAtTimeButton;
    private Button mGoRideSendButton;
    private TextView mAtTimeDisplay;

    private DatabaseReference mDatabase;
    private Integer modifier = 0;
    private Integer atTimeHour;
    private Integer atTimeMinute;
    private Integer nowHour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("goRideActivity", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_ride);

        // Grab resources
        mNowButton = (Button) findViewById(R.id.nowButton);
        mTenMinutesButton = (Button) findViewById(R.id.tenMinutesButton);
        mThrityMinutesButton = (Button) findViewById(R.id.thirtyMinutesButton);
        mSixtyMinuteButton = (Button) findViewById(R.id.sixtyMinutesButton);
        mAtTimeButton = (Button) findViewById(R.id.atTimeButton);
        mGoRideSendButton = (Button) findViewById(R.id.goRideSendButton);
        mAtTimeDisplay = (TextView) findViewById(R.id.atTimeDisplay);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Set Colors
        mNowButton.setBackgroundColor(Color.MAGENTA);
        mGoRideSendButton.setBackgroundColor(Color.MAGENTA);

    }

    public void getTeammates(final List<String> messages, final List<String> times){
        DatabaseReference teammatesRef = mDatabase.child("colleges/" + thisUser.college + "/users");

        final List<String> listOfTeammates = new ArrayList<>();

        ValueEventListener teammatesListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int maxIndex = (int) dataSnapshot.getChildrenCount();
                int index = 1;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.getKey().equals(thisUser.userName) == false) {
                        listOfTeammates.add("'" + child.getValue().toString() + "'");
                    }
                    index += 1;

                    if (index > maxIndex) {
                        postNotification(listOfTeammates, messages, times);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("goRideActivity", "Database Error");
            }
        };

        teammatesRef.addListenerForSingleValueEvent(teammatesListener);

    }

    public void resetButtonColors() {
        mNowButton.setBackgroundColor(Color.LTGRAY);
        mTenMinutesButton.setBackgroundColor(Color.LTGRAY);
        mThrityMinutesButton.setBackgroundColor(Color.LTGRAY);
        mSixtyMinuteButton.setBackgroundColor(Color.LTGRAY);
        mAtTimeButton.setBackgroundColor(Color.LTGRAY);
    }

    public void setModifier(View view){
        // Set the modifier var. so when notification is sent,
        // future notifications can be scheduled

        resetButtonColors();

        switch(view.getId()){
            case R.id.nowButton:
                modifier = 0;
                view.setBackgroundColor(Color.MAGENTA);
                break;
            case R.id.tenMinutesButton:
                modifier = 10;
                view.setBackgroundColor(Color.MAGENTA);
                break;
            case R.id.thirtyMinutesButton:
                modifier = 30;
                view.setBackgroundColor(Color.MAGENTA);
                break;
            case R.id.sixtyMinutesButton:
                modifier = 60;
                view.setBackgroundColor(Color.MAGENTA);
                break;
            default:
                break;
        }

        Log.d("goRideActivity", "notification modifier = " + modifier.toString());
    }

    public void displayPickerDialog(View view) {
        modifier = -1;
        Calendar now = Calendar.getInstance();
        nowHour = now.HOUR_OF_DAY;
        now.add(Calendar.HOUR_OF_DAY, 1);

        TimePickerDialog dialog = new TimePickerDialog(this, onTimeSetListener, now.HOUR_OF_DAY, now.MINUTE, false);
        dialog.show();
    }

    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            atTimeHour = hourOfDay;
            atTimeMinute = minute;

            String finalString = "";
            if (hourOfDay > 12) {
                Integer newHour = hourOfDay - 12;
                finalString += newHour + ":" + minute + "PM";
            }
            else {
                finalString += hourOfDay + ":" + minute + "AM";
            }

            if (hourOfDay <= nowHour) {
                finalString += " Tomorrow";
            }
            mAtTimeDisplay.setText(finalString);
        }
    };


    public List<String> getNotificationTimes() {
        List<String> listOfTimes = new ArrayList<>();
        Calendar now = Calendar.getInstance();
        Log.d("now", now.getTime().toString());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        String isoDate = "";

        // Initial notification
        now.add(Calendar.SECOND, 10);
        listOfTimes.add(now.getTime().toString());

        // Future notificaion
        if (modifier.intValue() >= 30){
            now.add(Calendar.MINUTE, modifier);
            listOfTimes.add(now.getTime().toString());

            isoDate = formatter.format(now.getTime());

        }
        else if (modifier.intValue() < 0){
            if (atTimeHour.intValue() <= nowHour) {
                now.add(Calendar.DAY_OF_MONTH, 1);
            }
            now.set(Calendar.HOUR_OF_DAY, atTimeHour);
            now.set(Calendar.MINUTE, atTimeMinute);
            listOfTimes.add(now.getTime().toString());

            isoDate = formatter.format(now.getTime());

        }

        Log.d("getNotificationTimes", listOfTimes.toString());
        setAnnouncements(isoDate);
        return listOfTimes;
    }

    public List<String> createMessages() {
        List<String> messages = new ArrayList<>();

        switch (modifier) {
            case 0:
                messages.add(thisUser.fullName + " is going on a ride right now!");
                break;
            case 10:
                messages.add(thisUser.fullName + " is going on a ride in 10 minutes!");
                break;
            case 30:
                messages.add(thisUser.fullName + " is going on a ride in 30 minutes!");
                messages.add(thisUser.fullName + " is going on a ride right now!");
                break;
            case 60:
                messages.add(thisUser.fullName + " is going on a ride in an hour!");
                messages.add(thisUser.fullName + " is going on a ride right now!");
                break;
            case -1:
                messages.add(thisUser.fullName + " is going on a ride at " + mAtTimeButton.getText());
                messages.add(thisUser.fullName + " is going on a ride right now!");
        }
        return messages;
    }

    public void setAnnouncements(String rideTime) {
        Log.d("goRideActivity", "setAnnoucements");
        Log.d("setAnnouncements", rideTime);
        DatabaseReference announcementRef = mDatabase.child("colleges/" + thisUser.college + "/announcements/rides/" + thisUser.fullName + "'s ride");
        announcementRef.setValue(rideTime);
    }

    public void postNotification(List<String> userIds, List<String> listOfMessages, List<String> listOfNotificationTimes) {
        Integer maxIndex = listOfMessages.size();
        for (Integer index = 0 ; index.intValue() < maxIndex.intValue() ; index += 1) {
            try {
                Log.d("userIds", userIds.toString());
                OneSignal.postNotification(new JSONObject("{'contents': {'en': '" + listOfMessages.get(index) + " '}, 'include_player_ids': " + userIds.toString() + ", 'send_after': '" + listOfNotificationTimes.get(index) + "', 'data' : {'senderOneSignalUserId': '" + thisUser.oneSignalUserId + "', 'notificationType': 'goingOnRide'}}"),
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
        }
    }

    public void notificationShell(View view) {
        Log.d("goRideActivity", "notificationShell");
        getTeammates(createMessages(), getNotificationTimes());

        // Confirmation Alert
        AlertDialog confirmationAlert = new AlertDialog.Builder(this).create();
        confirmationAlert.setTitle("Ride Posted");
        confirmationAlert.setMessage("Your teammates know you're going on a ride!");
        confirmationAlert.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        confirmationAlert.show();

    }

}
