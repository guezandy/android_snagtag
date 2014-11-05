package com.snagtag.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

/*
 * An extension of ParseUser that makes
 * it more convenient to access information
 * about a given user
 */

public class UserModel extends ParseUser {
    private final String TAG = UserModel.class.getSimpleName();

    public UserModel() {
        //required default constructer
    }

    //set age range: age (Should we make it range or an int... idk it's late at night)
    public void setAgeRange(String range) {
        put("age", range);
    }

    public String getAgeRange() {
        return this.getString("age");
    }
    //set firstname: first_name;
    public void setFirstName(String fname) {
        this.put("first_name", fname);
    }

    public String getFirstName() {
        return this.getString("first_name");
    }

    //set lastname: last_name

    public void setLastName(String lname) {
        this.put("last_name", lname);
    }

    public String getLastName() {
        return this.getString("last_name");
    }

    //set phone number: phone
    public void setPhoneNumber(String number) {
        this.put("phone", number);
    }
    public String getPhoneNumber() {
        return this.getString("phone");
    }

    //set street address: shipping_address

    //set zip code: shipping_zip

    //set billing address:/bill_address

    //set billing zip: bill_zip
}
