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

@ParseClassName("ClothingEntity")
public class ClothingItem extends ParseObject {
/*
* Clothing Item contains all information about a clothing item need
*
* String clothingID = ParseObject-> ObjectId
* String description
* Float price
* String type (shirt, pant, etc)
* String store
*
*
* */
    public ClothingItem() {
        // A default constructor is required.
    }

    public String getItemId() {
        return this.getObjectId();
    }

    public ParseUser getAuthor() {
        return getParseUser("user");
    }

    public void setAuthor(ParseUser user) {
        put("user", user);
    }

    public String getStore() {
        return getString("item_store");
    }

    public void setStore(String store) {
        put("item_store", store);
    }

    public String getDescription() {
        return getString("item_description");
    }

    public void setDescription(String description) {
        put("item_description", description);
    }

    public Float getPrice() {
        return (Float) getNumber("item_price");
    }

    public void setPrice(Float price) {
        put("item_price", price);
    }

    //	public Date getDate() {
//		return (Date) getString("date");
//	}
    public void setDate(Date time) {
        put("date", time);
    }

    public ParseFile getMainImage() {
        return getParseFile("item_image");
    }

    public void setMainImage(ParseFile file) {
        put("item_image", file);
    }

}
