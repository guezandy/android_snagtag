package com.snagtag.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

/*
 * An extension of ParseObject that makes
 * it more convenient to access information
 * about a given Tag
 */

@ParseClassName("TagHistoryItem")
public class TagHistoryItem extends ParseObject {
    private final String TAG = TagHistoryItem.class.getSimpleName();

/*
    A tag contains:
    unique id : tagId
    ParseUser : current user
    clothingId: clothing item tagged id
 */

    //TagModel constructor with a clothingItem
    public TagHistoryItem(ClothingItem tagged) {
        this.setBarcode(tagged.getBarcode());
        this.setUser(ParseUser.getCurrentUser());

        this.setStore(tagged.getStore());
        this.setDescription(tagged.getDescription());
        this.setType(tagged.getType());
        //this.put("size", tagged.getString("size")); //does each clothing item have a size?
        this.setPrice(tagged.getPrice());
        this.setImage(tagged.getMainImage());
        this.setInCart(false);
        this.setInCloset(false);
        this.setVisible(true);
        this.setFavorite(false);

        ParseUser theUser = this.getUser();
        Log.i(TAG, "Inside the get user: "+theUser.getString("first_name"));
    }


    public TagHistoryItem() {
        //required default constructer
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
        //if(user != null) {
        //    ParseRelation relation = this.getRelation("user");
        //    relation.add(user);
        //}
    }
//TODO: fix this!
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


    public Double getPrice() {
        return (Double)this.getNumber("price");
        //return Double.parseDouble(this.getString("price"));
    }

    public void setPrice(Double price) {
        put("price", price);
    }

    public ParseFile getImage() {
        return getParseFile("main_image");
    }

    public void setImage(ParseFile file) {
        put("main_image", file);
    }

    public String getBarcode() { return getString("barcode");   }

    public void setBarcode(String barcode) { this.put("barcode", barcode);}

//    public void setRelation(final TagHistoryItem item) {
//        if(item != null) {
//            Log.i(TAG, "Adding to relation");
//            ParseRelation relation = ParseUser.getCurrentUser().getRelation("user_tags");
//            relation.add(item);
//        }
//    }

    public void setFavorite(Boolean fav) {
        this.put("favorite", fav);
    }

    public Boolean getFavorite() {
        return this.getBoolean("favorite");
    }

    public void setType(String type) {
        this.put("type", type);
    }

    public int getType() {
       String type = this.getString("type");
       if(type.equals("top/bottom")) {
           return 1;
       } else if(type.equals("top")) {
           return 2;
       } else if(type.equals("bottom")) {
           return 3;
       } else if(type.equals("shoe")) {
           return 4;
       } else {
           return 0;
       }
    }
}
