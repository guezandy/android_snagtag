package com.snagtag.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Owner on 9/24/2014.
 */
@ParseClassName("OutfitModel")
public class OutfitItem extends ParseObject {

    public OutfitItem() {
        //required default constructer
    }

    public OutfitItem(TagHistoryItem top, TagHistoryItem bottom, TagHistoryItem shoes) {
        if(top != null) {
            this.setTopRelation(top);
            this.setTopImage(top);
            this.setTopInCloset(top);
            this.setTopDescription(top);
        }
        if(bottom != null) {
            this.setBottomRelation(bottom);
            this.setBottomImage(bottom);
            this.setBottomInCloset(bottom);
            this.setBottomDescription(bottom);
        }
        if(shoes != null) {
            this.setShoesRelation(shoes);
            this.setShoesImage(shoes);
            this.setShoesInCloset(shoes);
            this.setShoesDescription(shoes);
        }
        this.setOwnEntireOutfit();
        this.setUser();
    }

    public OutfitItem(TagHistoryItem top, TagHistoryItem bottom, TagHistoryItem shoes, TagHistoryItem acc) {
        if(top != null) {
            this.setTopRelation(top);
            this.setTopImage(top);
            this.setTopInCloset(top);
            this.setTopDescription(top);
        }
        if(bottom != null) {
            this.setBottomRelation(bottom);
            this.setBottomImage(bottom);
            this.setBottomInCloset(bottom);
            this.setBottomDescription(bottom);
        }
        if(shoes != null) {
            this.setShoesRelation(shoes);
            this.setShoesImage(shoes);
            this.setShoesInCloset(shoes);
            this.setShoesDescription(shoes);
        }
        if(acc != null) {
            this.setAccRelation(acc);
            this.setAccImage(acc);
            this.setAccInCloset(acc);
            this.setAccDescription(acc);
        }
        this.setOwnEntireOutfit();
        this.setUser();
    }

    public void setTopRelation(TagHistoryItem top) {
        this.put("top", top);
    }

//    public TagHistoryItem getTopRelation() {
//        ParseRelation relation = getRelation("top");
//    }

    public void setBottomRelation(TagHistoryItem bottom) {
        this.put("bottom", bottom);
    }
// public TagHistoryItem getBottomRelation() {
//
//}
    public void setShoesRelation(TagHistoryItem shoes) {
        this.put("shoes", shoes);
    }

    public void setTopImage(TagHistoryItem top) {
        this.put("top_image", top.getImage());
    }

    public ParseFile getTopImage() {
        return this.getParseFile("top_image");
    }

    public void setBottomImage(TagHistoryItem bottom) {
        this.put("bottom_image", bottom.getImage());
    }

    public ParseFile getBottomImage() {
        return this.getParseFile("bottom_image");
    }

    public void setShoesImage(TagHistoryItem shoes) {
        this.put("shoes_image", shoes.getImage());
    }

    public ParseFile getShoesImage() {
        return this.getParseFile("shoes_image");
    }

    public void setTopInCloset(TagHistoryItem top) {
        put("topInCloset", top.getInCloset());
    }

    public Boolean getTopInCloset() {
        return this.getBoolean("topInCloset");
    }

    public void setBottomInCloset(TagHistoryItem bottom) {
        put("bottomInCloset", bottom.getInCloset());
    }

    public Boolean getBottomInCloset() {
        return this.getBoolean("bottomInCloset");
    }

    public void setShoesInCloset(TagHistoryItem shoes) {
        put("shoesInCloset", shoes.getInCloset());
    }

    public Boolean getShoesInCloset() {
        return this.getBoolean("shoesInCloset");
    }

    public void setOutfitTitle(String title) {
        this.put("title", title);
    }

    public String getOutfitTitle() {
        return this.getString("title");
    }

    public void setOwnEntireOutfit() {
        this.put("own_outfit", (getBottomInCloset() && getShoesInCloset() && getTopInCloset() && getAccInCloset()));
    }

    public Boolean getOwnEntireOutfit() {
        return this.getBoolean("own_outfit");
    }

    public void setUser() {
        this.put("user", ParseUser.getCurrentUser());
    }

    public ParseUser getUser() {
        return this.getParseUser("user");
    }

    public void setTopDescription(TagHistoryItem top) {
        this.put("top_description", top.getDescription());
    }
    public String getTopDescription() {
        return this.getString("top_description");
    }

    public void setBottomDescription(TagHistoryItem bottom) {
        this.put("bottom_description", bottom.getDescription());
    }

    public String getBottomDescription() {
        return this.getString("bottom_description");
    }

    public void setShoesDescription(TagHistoryItem shoes) {
        this.put("shoes_description", shoes.getDescription());
    }

    public String getShoesDescription() {
        return this.getString("shoes_description");
    }

    public void setAccRelation(TagHistoryItem acc) {
        this.put("acc", acc);
    }

    public void setAccImage(TagHistoryItem acc) {
        this.put("acc_image", acc.getImage());
    }

    public ParseFile getAccImage() {
        return this.getParseFile("acc_image");
    }

    public void setAccDescription(TagHistoryItem acc) {
        this.put("acc_description", acc.getDescription());
    }

    public String getAccDescription() {
        return this.getString("acc_description");
    }

    public void setAccInCloset(TagHistoryItem acc) {
        put("accInCloset", acc.getInCloset());
    }

    public Boolean getAccInCloset() {
        return this.getBoolean("accInCloset");
    }

}
