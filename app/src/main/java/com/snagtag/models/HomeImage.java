package com.snagtag.models;


import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;

/*
 * An extension of ParseObject that makes
 * it more convenient to access information
 * about a given Clothing Item.
 *
 * All we need is getters and
 */

@ParseClassName("HomeImage")
public class HomeImage extends ParseObject {

    public HomeImage() {
        //default constructor required;
    }

    public ParseFile getImage() {
        return this.getParseFile("image");
    }
}