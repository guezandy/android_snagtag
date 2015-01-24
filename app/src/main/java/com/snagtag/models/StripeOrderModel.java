package com.snagtag.models;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/*
Process Stripe orders
 */

@ParseClassName("OrderItem")
public class StripeOrderModel extends ParseObject {
    private final String TAG = StoreItem.class.getSimpleName();

    public StripeOrderModel() {

    }

    public StripeOrderModel(ParseUser user, List<CartItem> items, String store) {
        this.setCustomerId(user.getString("customerId"));
        this.setPurchaseItems(items);
        ParseQuery<StoreItem> query = ParseQuery.getQuery("StoreItem");
        query.whereEqualTo("name", store);
        query.getFirstInBackground(new GetCallback<StoreItem>() {
            @Override
            public void done(StoreItem results, ParseException e) {
                if (e != null) {
                    // There was an error
                } else {
                    setStore(results);
                }
            }
        });
    }

    /**
     * We need customer id, items to purchase, shipping address, billing address, and store stripe id
     */
    public void setCustomerId(String customerId) {
        this.put("customerId", customerId);
    }

    public String getCustomerId() {
        return this.getString("customerId");
    }

    //TODO: Check if this works
    public void setPurchaseItems(List<CartItem> items) {
        this.put("items", items);
    }

    //TODO: get purchased Items is done with a getRelation i think.

    public void setStoreStripeId(StoreItem store) {
        this.put("storeStripeId", store.getStoreStripeId());
    }

    public String getStoreStripeId(StoreItem store) {
        return this.getString("storeStripeId");
    }

    public void setStore(StoreItem store) {
        //makes relationt to store
        this.put("store", store);
    }

    public StoreItem getStore() {
        //TODO: get store relation
        return null;
    }

    public void setShippingStreetAddress(String stAdd) {
        this.put("streetAddress", stAdd);
    }

    public String getShippingStreetAddress() {
        return this.getString("streetAddress");
    }

    public void setShippingCity(String city) {
        this.put("shippingCity", city);
    }

    public String getShippingCity() {
        return this.getString("shippingCity");
    }

    public void setShippingState(String state) {
        this.put("shippingState", state);
    }

    public String getShippingState() {
        return this.getString("shippingState");
    }

    public void setShippingZip(String zipcode) {
        put("shippingZip", zipcode);
    }

    public String getShippingZip() {
        return this.getString("shippingZip");
    }

    public void setShippingName(String shipName) {
        put("shippingName", shipName);
    }

    public String getShippingName() {
        return this.getString("shippingName");
    }


}