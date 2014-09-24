package com.snagtag.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseFile;

/**
 * Created by Owner on 9/24/2014.
 */
@ParseClassName("OutfitModel")
public class OutfitItem extends ParseObject {

    public OutfitItem() {

    }

    public OutfitItem(TagHistoryItem top, TagHistoryItem bottom, TagHistoryItem shoes) {
        this.setTopRelation(top);
        this.setBottomRelation(bottom);
        this.setShoesRelation(shoes);

        this.setTopImage(top);
        this.setBottomImage(bottom);
        this.setShoesImage(shoes);

        this.setTopInCloset(top);
        this.setBottomInCloset(bottom);
        this.setShoesInCloset(shoes);

        this.setOwnEntireOutfit();

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
        this.put("own_outfit", (getBottomInCloset() && getShoesInCloset() && getTopInCloset()));
    }

    public Boolean getOwnEntireOutfit() {
        return this.getBoolean("own_outfit");
    }
}
