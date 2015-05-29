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

@ParseClassName("BillingModel")
public class BillingModel extends ParseObject {

    public BillingModel() {
        //default constructor required;
    }

    public BillingModel(List<String> info) {
        put("user", ParseUser.getCurrentUser());
        put("card_type", info.get(0));
        put("last4_num", info.get(1));
        put("exp_date", info.get(2));
    }

}