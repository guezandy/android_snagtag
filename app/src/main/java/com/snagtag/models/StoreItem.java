package com.snagtag.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

/*
 * An extension of ParseObject that makes
 * it more convenient to access information
 * about a given Tag
 */

@ParseClassName("StoreItem")
public class StoreItem extends ParseObject {
    private final String TAG = StoreItem.class.getSimpleName();

    public StoreItem() {

    }

    //store id
    public String getStoreId() {
        return this.getObjectId();
    }

    //store name
    public void setStoreName(String name) {
        put("name", name);
    }

    public String getStoreName() {
        return this.getString("name");
    }

    //store description
    public void setStoreDescription(String description) {
        put("description", description);
    }

    public String getStoreDescription() {
        return this.getString("description");
    }

    //store location
    //TODO: getter and setter for the stores location
    public void setStoreLocation(ParseGeoPoint location) {
        //interesting......
    }


    //icon

    public void setImage(ParseFile icon) {
        put("icon", icon);
    }

    public ParseFile getStoreIcon() {
        return getParseFile("icon");
    }

    public String getStoreStripeId() {
        return this.getString("stripeId");
    }
}