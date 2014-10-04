package com.snagtag.models;


import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

/*
 * An extension of ParseObject that makes
 * it more convenient to access information
 * about a given Clothing Item.
 *
 * All we need is getters and
 */

@ParseClassName("CartItem")
public class CartItem extends ParseObject {

    public CartItem() {
        //default constructor required;
    }

    public CartItem(TagHistoryItem item) {
        put("item", item);
        put("user", ParseUser.getCurrentUser());
    }

    //TODO: Finish adding getters and setters to this class
}