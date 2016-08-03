package com.example.bike;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by davidcai on 8/1/16.
 */

public class workoutClass implements Parcelable {
    public String workoutName;
    public List<Integer> duration;
    public List<Integer> reps;
    public String type;
    public String unit;
    public Map<String, Boolean> usersHaveCompleted;
    public List<String> week;
    public List<String> usersHaveCompletedList = new ArrayList<>();


    public workoutClass(){
    }

    public workoutClass(String workoutName, List<Integer> duration, List<Integer> reps, String type, String unit, Map<String, Boolean> usersHaveCompleted, List<String> week){

        this.workoutName = workoutName;
        this.duration = duration;
        this.reps = reps;
        this.type = type;
        this.unit = unit;
        this.week = week;
        this.usersHaveCompleted = usersHaveCompleted;

    }

    public String getWorkoutName() {
        return this.workoutName;
    }

    public String getType() {
        return this.type;
    }

    public String getUsersHaveCompletedString() {
        String finalString = "";
        for(String user: usersHaveCompletedList){
            finalString += user + "\n";
        }

        return finalString;
    }

    public String getWorkoutPayload() {
        String finalString= "";
        Integer index = 0;
        Integer maxIndex = this.duration.size();

        while(index < maxIndex) {
            finalString += duration.get(index) + " x " + reps.get(index) + "\n";
            index += 1;
        }

        return finalString;
    }

    public String getWeekNumber(){
        return this.week.get(0);
    }

    public String getWeekDate() {
        return this.week.get(1);
    }


    // The purpose of this method
    // Is to take the keys (users) from usersHaveCompleted (the map)
    // And transfer them to the corresponding list
    // B/C putParcelable can't do maps
    public void setUsersHaveCompletedList() {
        for (String user: this.usersHaveCompleted.keySet()) {
            if (user.equals("init") == false) {
                Log.d("setUSersHaveComplete", "username " + user);
                this.usersHaveCompletedList.add(user);
            }
        }
    }



    //
    //
    //
    //
    // The following code is to implement the Parcelable class, to allow
    // WorkoutListACtivity to pass the selected workout to WorkoutDetailActivity

    protected workoutClass(Parcel in) {
        workoutName = in.readString();
        if (in.readByte() == 0x01) {
            duration = new ArrayList<Integer>();
            in.readList(duration, Integer.class.getClassLoader());
        } else {
            duration = null;
        }
        if (in.readByte() == 0x01) {
            reps = new ArrayList<Integer>();
            in.readList(reps, Integer.class.getClassLoader());
        } else {
            reps = null;
        }
        type = in.readString();
        unit = in.readString();
        if (in.readByte() == 0x01) {
            week = new ArrayList<String>();
            in.readList(week, String.class.getClassLoader());
        } else {
            week = null;
        }
        if (in.readByte() == 0x01) {
            usersHaveCompletedList = new ArrayList<String>();
            in.readList(usersHaveCompletedList, String.class.getClassLoader());
        } else {
            usersHaveCompletedList = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(workoutName);
        if (duration == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(duration);
        }
        if (reps == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(reps);
        }
        dest.writeString(type);
        dest.writeString(unit);
        if (week == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(week);
        }
        if (usersHaveCompletedList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(usersHaveCompletedList);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<workoutClass> CREATOR = new Parcelable.Creator<workoutClass>() {
        @Override
        public workoutClass createFromParcel(Parcel in) {
            return new workoutClass(in);
        }

        @Override
        public workoutClass[] newArray(int size) {
            return new workoutClass[size];
        }
    };
}


