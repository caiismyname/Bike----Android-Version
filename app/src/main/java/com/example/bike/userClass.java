package com.example.bike;

import java.sql.Array;
import java.util.ArrayList;

/**
 * Created by davidcai on 7/29/16.
 */
public class userClass {
    public String firstName;
    public String lastName;
    public String userName;
    public String college;
    public String email;
    public String bikeName;
    public String oneSignalUserId;

    public userClass(String firstName, String lastName, String college, String email, String oneSignalUserId, String bikeName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.college = college;
        this.userName = college + firstName + lastName;
        this.email = email;
        this.bikeName = bikeName;
        this.oneSignalUserId = oneSignalUserId;
    }
}
