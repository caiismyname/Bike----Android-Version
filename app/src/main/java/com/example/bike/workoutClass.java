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
    public Map<String, String> usersHaveCompleted;
    public List<String> week;
    public List<String> usersHaveCompletedUsernameList = new ArrayList<>();
    public List<String> usersHaveCompletedFullnameList = new ArrayList<>();

    public workoutClass() {}

    public workoutClass(String workoutName, List<Integer> duration, List<Integer> reps, String type, String unit, Map<String, String> usersHaveCompleted, List<String> week) {

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
        for (String user : usersHaveCompletedFullnameList) {
            finalString += user + "\n";
        }

        return finalString;
    }

    public String getNumberHaveCompleted() {
        Integer number = usersHaveCompletedFullnameList.size();
        return number.toString();
    }


    public String getWorkoutPayload() {
        String finalString = "";
        Integer index = 0;
        Integer maxIndex = this.duration.size();

        while (index < maxIndex) {
            finalString += duration.get(index) + " x " + reps.get(index) + "\n";
            index += 1;
        }

        finalString += this.unit;

        return finalString;
    }

    public String getWeekNumber() {
        return this.week.get(0).toString();
    }

    public String getWeekDate() {
        return this.week.get(1);
    }

    public String getWeekString() {
        return this.week.get(0).toString() + ": " + this.week.get(1);
    }


    // The purpose of this method
    // Is to take the keys (users) from usersHaveCompleted (the map)
    // And transfer them to the corresponding list
    // B/C putParcelable can't do maps
    public void setUsersHaveCompletedLists() {
        for (Map.Entry<String, String> user : this.usersHaveCompleted.entrySet()) {
            if (user.getKey().equals("init") == false) {
                this.usersHaveCompletedUsernameList.add(user.getKey());
                this.usersHaveCompletedFullnameList.add(user.getValue());
            }
        }
    }

    // Constructing an object out of the FB data directly will not give us the
    // FB key for the data. This manually fixes it.
    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
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
            usersHaveCompletedUsernameList = new ArrayList<String>();
            in.readList(usersHaveCompletedUsernameList, String.class.getClassLoader());
        } else {
            usersHaveCompletedUsernameList = null;
        }
        if (in.readByte() == 0x01) {
            usersHaveCompletedFullnameList = new ArrayList<String>();
            in.readList(usersHaveCompletedFullnameList, String.class.getClassLoader());
        } else {
            usersHaveCompletedFullnameList = null;
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
        if (usersHaveCompletedUsernameList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(usersHaveCompletedUsernameList);
        }
        if (usersHaveCompletedFullnameList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(usersHaveCompletedFullnameList);
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