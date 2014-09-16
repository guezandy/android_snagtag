package com.example.owner.snagtag_android.models;

import java.util.Date;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

/*
 * An extension of ParseObject that makes
 * it more convenient to access information
 * about a given Clothing 
 */

@ParseClassName("CartItem")
public class CartItem extends ParseObject {

	public CartItem() {
		// A default constructor is required.
	}

	public String getItemId() {
		return this.getString("item_id");
	}

	public void setItemId(String itemId) {
		put("item_id", itemId);
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
	
	public String getPrice() {
		return getString("item_price");
	}

	public void setPrice(String price) {
		put("item_price", price);
	}
	
//	public Date getDate() {
//		return (Date) getString("date");
//	}
	public void setDate(Date time) {
		put("date", time);
	}

	public ParseFile getPhotoFile() {
		return getParseFile("item_image");
	}

	public void setPhotoFile(ParseFile file) {
		put("item_image", file);
	}

}
