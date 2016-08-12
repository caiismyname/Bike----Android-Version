package com.example.bike;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by davidcai on 8/11/16.
 */

public class bikeClass {

    public String bikeUserName;
    public String name;
    public Map<String, String> riders;
    public List<String> ridersUsernames = new ArrayList<>();
    public List<String> ridersFullnames = new ArrayList<>();
    public String size;
    public String status;

    public bikeClass() {}

    public bikeClass(String name, Map<String, String> riders, String size, String status) {
        this.name = name;
        this.riders = riders;
        this.size = size;
        this.status = status;
    }

    public void setBikeUserName(String bikeUserName) {
        this.bikeUserName = bikeUserName;
    }

    public void setRiderNameLists() {
        for (Map.Entry<String, String> rider : this.riders.entrySet()) {
            if (rider.getKey().equals("init") == false) {
                this.ridersUsernames.add(rider.getKey());
                this.ridersFullnames.add(rider.getValue());
            }
        }
    }

    public String getBikeName() {return this.name;}

    public String getBikeUserName() {return this.bikeUserName;}

    public String getRiders() {
        String finalString = "";
        for (String rider : ridersFullnames) {
            finalString += rider +", ";
        }
        if (finalString.equals("") == true) {
            return "No Riders";
        } else {
            return finalString;
        }
    }

    public String getSize() {return "(" + this.size + ")";}

    public String getStatus() {return this.status;}
}
