package com.snagtag.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.snagtag.R;
import com.snagtag.models.ClothingItem;
import com.snagtag.models.TagHistoryItem;

import java.util.List;

/**
 * This fragment displays a single clothing item
 * This will always be provided the nfcId (Barcode number) and will query parse for the item
 * This will be reused for a single item view also
 * Created by Owner on 9/24/2014.
 */
public class SingleItemFragment extends Fragment {
    private final String TAG = SingleItemFragment.class.getSimpleName();
    /**
     * barcode number needed to query parse to get information on an item
     */

    private String barcode;
    /**
     * Flag for knowing if the activity was opening due to an nfc tag
     */
    private Boolean nfcIntent = false;

    /**
     * Components for the view
     */
    private RelativeLayout mItemView;
    private TextView brand;
    private TextView description;
    private TextView price;
    private ParseImageView image;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         * Initializing view components
         */
        mItemView = (RelativeLayout) inflater.inflate(
                R.layout.fragment_item_detail_view, container, false);
        Log.i(TAG, "ID is: " + barcode);
        brand = (TextView) mItemView.findViewById(R.id.label_brand);
        description = (TextView) mItemView.findViewById(R.id.label_description);
        price = (TextView) mItemView.findViewById(R.id.label_price);
        image = (ParseImageView) mItemView.findViewById(R.id.item_image);

        //TODO: Move into service
        onItemSnagged(barcode);

        return mItemView;
    }

    public void setItemId(String itemId) {
        this.barcode = itemId;
    }

    public void setNfcIntent(Boolean b) {
        this.nfcIntent = b;
    }

    public SingleItemFragment() {
        //default constructor for fragment
    }

public void onItemSnagged(String nfcid) {
/*
	tagItem
	Queries parse for clothingItem with matching barcode number
		1. updates the screen with the items info and image
		2. increments that clothing items counter (Tag counter)
		3. makes a taghistory item and stores the item to taghistory table
		4. adds the item to the users taghistory relation
		5. Add the tag to local phone mem
*/
    barcode = nfcid; //"12345"; //for testing
    final ParseQuery<ParseObject> query = ParseQuery.getQuery("ClothingItem"); //or clothingItem
    query.whereEqualTo("barcode", barcode); //grab the clothingItem whose barcode num matches the nfcId
    query.getFirstInBackground(new GetCallback<ParseObject>() {
        @Override
        public void done(final ParseObject item, ParseException e) {
            if (item == null) {
                Log.d("Scan Tag", "The request failed. " + e.getMessage(), e);

            } else {
                Log.d("Scan Tag", "Retrieved the clothingItem.");
      /* 1 */
                ClothingItem object = (ClothingItem) item;
                brand.setText(object.getStore());
                description.setText(object.getDescription());
                price.setText(object.getPrice().toString());
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
      /* 2 */
                Log.i(TAG, "Incrementing tag count");
                object.increment("tag_count"); //increments the items tag counter
                object.saveInBackground();

      /* 3 */
                Log.i(TAG, "Add item to tag history table");
                final TagHistoryItem tag = new TagHistoryItem(object); //creates a tagmodel from a clothingItem
                tag.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
      /* 5 */
                        //Adding to local database here to avoid thread overlap
                        //TODO: Check for unique
                        tag.pinInBackground();
                    }
                }); //saves to the universal tag history table
	  /* 4 */
                ParseUser user = ParseUser.getCurrentUser();//gets the current user
                if(user != null) { //Allows an nfc intent even if not registered.
                    ParseRelation usersTags = user.getRelation("user_tags"); //gets all the users tags
                    //TODO: Add if unique
                    usersTags.add(tag);
                    //userTags.addUnique(object);//add if unique
                    user.saveEventually();
                }
            }
        }
    });
    }
}
