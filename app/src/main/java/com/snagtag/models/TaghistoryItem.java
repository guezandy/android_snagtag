package com.snagtag.models;

import java.util.Date;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

/*
 * An extension of ParseObject that makes
 * it more convenient to access information
 * about a given Tag
 */

@ParseClassName("TagHistoryItem")
public class TagHistoryItem extends ParseObject {
/*
    A tag contains:
    unique id : tagId
    ParseUser : current user
    clothingId: clothing item tagged id
 */

    //TagModel constructor with a clothingItem
    public TagHistoryItem(ClothingItem tagged) {
        this.setClothingId(tagged.getObjectId());
        this.setUser(ParseUser.getCurrentUser());

        //do we want to add more info in a tag
        //YES so we can save the info to access them off line
        //We need:
        //Store
        //description
        //color selected (default to no color selected or one color)
        //size selected (default prompt them to pick one)
        this.setStore(tagged.getStore());
        this.setDescription(tagged.getDescription());
        //this.put("size", tagged.getString("size")); //does each clothing item have a size?
        this.setPrice(tagged.getPrice());
        this.setImage(tagged.getMainImage());
        this.setInCart(false);
        this.setInCloset(false);
        this.setVisible(true);
    }

    public void setVisible(Boolean b) {
        this.put("visible", b);
    }

    public Boolean getVisible() {
        return this.getBoolean("visible");
    }

    public void setInCart(Boolean b) {
        this.put("inCart",b);
    }

    public Boolean getInCart() {
        return this.getBoolean("inCart");
    }

    public void setInCloset(Boolean b) {
        this.put("inCloset", b);
    }

    public Boolean getInCloset() {
        return this.getBoolean("inCloset");
    }
    public void setUser(ParseUser user) {
        put("user", user);
    }

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public String getStore() {
        return getString("store");
    }

    public void setStore(String store) {
        put("store", store);
    }

    public String getDescription() {
        return getString("description");
    }

    public void setDescription(String description) {
        put("description", description);
    }

    //check this out
    public Float getPrice() {
        return (Float)this.getNumber("price");
    }

    public void setPrice(Float price) {
        put("price", price);
    }

    public ParseFile getImage() {
        return getParseFile("main_image");
    }

    public void setImage(ParseFile file) {
        put("main_image", file);
    }

    public void setClothingId(String id) {
        put("clothing_id", id);
    }

    public String getClothingId() {
        return this.getString("clothing_id");
    }
}