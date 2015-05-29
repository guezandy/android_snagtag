package com.snagtag.models;


import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;

/*
 * An extension of ParseObject that makes
 * it more convenient to access information
 * about a given Clothing Item.
 *
 * All we need is getters and
 */

@ParseClassName("ShippingModel")
public class ShippingModel extends ParseObject {

    public ShippingModel() {
        //default constructor required;
    }

    public ShippingModel(List<String> info) {
        ParseUser user = ParseUser.getCurrentUser();

        //still deciding the best way to store the addresses
        //in its own table to distinguish between shipping and billing

        //TODO: Add is shipping flag
        put("user", ParseUser.getCurrentUser());
        put("name", info.get(0));
        put("street", info.get(1));
        put("city", info.get(2));
        put("state", info.get(3));
        put("zip", info.get(4));

        //or as a user value
        user.put("address", info.get(1));
        user.put("city", info.get(2));
        user.put("state", info.get(3));
        user.put("zipcode", info.get(4));

    }

    public void setUser(ParseUser user) {
        put("user", user);
    }

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setName(String name) {
        this.put("name", name);
    }

    public String getName() {
        return this.getString("name");
    }

    public void setStreet(String street) {
        this.put("street", street);
    }

    public String getStreet() {
        return this.getString("street");
    }

    public void setCity(String city) {
        this.put("city", city);
    }

    public String getCity() {
        return this.getString("city");
    }

    public void setState(String state) {
        this.put("state", state);
    }

    public String getState() {
        return this.getString("state");
    }

    public void setZip(String zip) {
        this.put("zip", zip);
    }

    public String getZip() {
        return this.getString("zip");
    }
}