package com.snagtag.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.snagtag.R;
import com.snagtag.models.ClothingItem;
import com.snagtag.models.TagHistoryItem;

/**
 * This fragment displays a single clothing item
 * This will always be provided the nfcId (Barcode number) and will query parse for the item
 * This will be reused for a single item view also
 * Created by Owner on 9/24/2014.
 */
public class SingleItemFragment extends Fragment {
    private final String TAG = SingleItemFragment.class.getSimpleName();
    private RelativeLayout mItemView;
    public String nfcId;
    public TextView brand;
    public TextView description;
    public TextView price;
    public ParseImageView image;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mItemView = (RelativeLayout) inflater.inflate(
                R.layout.fragment_item_detail_view, container, false);
        Log.i(TAG, "ID is: " + nfcId);
        brand = (TextView) mItemView.findViewById(R.id.label_brand);
        description = (TextView) mItemView.findViewById(R.id.label_description);
        price = (TextView) mItemView.findViewById(R.id.label_price);
        image = (ParseImageView) mItemView.findViewById(R.id.item_image);
/*
	tagItem
		params: String of nfc tag which is the barcode number
		return: void

		1. updates the screen with the items info and image
		2. increments that clothing items counter
		3. makes a taghistory item and stores the item to taghistory table
		4. adds the item to the users taghistory relation
		5. Add the tag to local phone mem
*/

        ParseQuery<ClothingItem> query = ParseQuery.getQuery("ClothingEntity"); //or clothingItem
        query.whereEqualTo("barcode", nfcId); //grab the clothingItem whose barcode num matches the nfcId
        query.getFirstInBackground(new GetCallback<ClothingItem>() {
            public void done(ClothingItem object, ParseException e) { //or clothingItem
                if (object == null) {
                    Log.d("Scan Tag", "The getFirst request failed.");
                } else {
                    Log.d("Scan Tag", "Retrieved the clothingItem.");
              /* 1 */
                    brand.setText(object.getStore());
                    description.setText(object.getDescription());
                    ParseFile photoFile = object.getMainImage();
                    if (photoFile != null) {
                        image.setParseFile(photoFile);
                        image.loadInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                // nothing to do
                                Log.i(TAG, "Image uploaded");
                            }
                        });
                    }
                        //price
                        //image
              /*
               * Check for similar colors and put the images
               * Check inventory and put the sizes
                ......
              */

              /* 2 */
                        object.increment("tag_count"); //increments the items tag counter

              /* 3 */
                        TagHistoryItem tag = new TagHistoryItem(object); //creates a tagmodel from a clothingItem
                        tag.saveEventually();  //saves to the universal tag history table

              /* 4 */
                        ParseUser user = ParseUser.getCurrentUser();//gets the current user
                        ParseRelation<TagHistoryItem> usersTags = user.getRelation("user_tags"); //gets all the users tags
                        usersTags.add(tag);
                        //usersTags.addUnique(tag);//add if unique
                        user.saveInBackground();

              /* 5 */
                        //use local datastore with a relation??
                        //get relation
                        //relation.add(tag)
                        //relation.pinInBackground() //adds to local mem


             /* Add to cart */
                        if(false) {
                            TagHistoryItem adding = new TagHistoryItem(object);
                            adding.setInCart(true);
                            adding.saveInBackground();//added to cart table;
                            ParseRelation<TagHistoryItem> userCart = user.getRelation("user_cart");

                            //keep in mind the case of not unique but not in cart. If removed from cart through boolean
                            userCart.add(adding);
                            user.saveInBackground();
                        }
                    }
                }
            });


        return mItemView;
    }

    public void setItemId(String itemId) {
        this.nfcId = itemId;
    }

    public SingleItemFragment() {
        //default constructor for fragment
    }

}
