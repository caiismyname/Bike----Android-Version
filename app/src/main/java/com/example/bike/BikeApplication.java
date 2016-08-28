package com.example.bike;

import android.app.Application;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.bike.MainActivity.thisUser;

/**
 * Created by davidcai on 8/5/16.
 */

public class BikeApplication extends Application {
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("bikeapplication", "onCreate");
        OneSignal.startInit(this)
                .init();

    }

    // This fires when a notification is opened by tapping on it or one is received while the app is running.
    private class MyNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {

            //OSNotificationAction.ActionType actionType = result.action.actionType;
            JSONObject data = result.notification.payload.additionalData;

            String messageTitle;
            String messageBody = result.notification.payload.body;
            AlertDialog.Builder builder = null;

            if (result.notification.isAppInFocus) {// If a push notification is received when the app is being used it does not display in the notification bar so display in the app.
                Log.d("bikeApplication", "isActive");
                String notificationType = "";
                String senderId = "";
                try {
                    notificationType = data.getString("notificationType");
                    senderId = data.getString("senderOneSignalUserId");
                } catch (JSONException e){
                    e.printStackTrace();
                }

                if (notificationType.equals("goingOnRide")) {
                    Log.d("bikeApplication", "goingOnRide response");
                    final String finalSenderId = senderId;
                    builder = new AlertDialog.Builder(BikeApplication.this)
                            .setTitle("A teammate is riding!")
                            .setMessage( messageBody + "\n Would you like to join them?");

                    if (builder != null) {
                        Log.d("bikeApplication", "builder not null");
                        builder.setCancelable(true)
                                .setPositiveButton("wahchow", null)
                                .create().show();
                    }
                }
            }


        }
    }

    public void joinRide(String receivingId) {
        try {
            OneSignal.postNotification(new JSONObject("{'contents': {'en':" + thisUser.fullName + " has joined your ride!}, 'include_player_ids': " + receivingId + ", 'data': {'senderOneSignalUserId': " + thisUser.oneSignalUserId + ", 'notificationType': 'rideJoined'}}"),
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

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
