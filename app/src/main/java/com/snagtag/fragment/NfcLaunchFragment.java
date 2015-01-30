package com.snagtag.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.snagtag.SnagtagApplication;
import com.snagtag.models.ClothingItem;
import com.snagtag.models.TagHistoryItem;
import com.snagtag.scroll.CenteringHorizontalScrollView;
import com.snagtag.service.IParseCallback;
import com.snagtag.service.ParseService;

import java.util.List;

/**
 * This fragment displays a single clothing item
 * This will always be provided the nfcId (Barcode number) and will query parse for the item
 * This will be reused for a single item view also
 * Created by Owner on 9/24/2014.
 */
public class NfcLaunchFragment extends Fragment {
    private final String TAG = NfcLaunchFragment.class.getSimpleName();
    /**
     * barcode number needed to query parse to get information on an item
     */

    private String barcode;
    /**
     * Flag for knowing if the activity was opening due to an nfc tag
     */
    //private Boolean nfcIntent = false;

    /**
     * Components for the view
     */
    private RelativeLayout mItemView;
    private TextView brand;
    private TextView description;
    private TextView price;
    private String tagObjectId;
    private LinearLayout mTopsView;
    private CenteringHorizontalScrollView mTopsScrollView;
    List<TagHistoryItem> mTops;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "Inside on resume");
        while(tagObjectId != null) {
            Log.i(TAG, "TagObjectId is null");
        }
//        createRelation(ParseUser.getCurrentUser(), tagObjectId);
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
        mTopsScrollView = (CenteringHorizontalScrollView) mItemView.findViewById(R.id.tops_scroll_view);
        mTopsView = (LinearLayout) mItemView.findViewById(R.id.tops_view);


        //TODO: Move into service
        onItemSnagged(barcode);
        return mItemView;
    }

    public void setItemId(String itemId) {
        this.barcode = itemId;
    }

    /*public void setNfcIntent(Boolean b) {
        //this.nfcIntent = b;
    }*/

    public NfcLaunchFragment() {
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

                //TODO: CHANGE TO GET ITEM IMAGES
                new ParseService(getActivity().getApplicationContext()).getTops(getActivity().getApplicationContext(), new IParseCallback<List<TagHistoryItem>>() {
                    @Override
                    public void onSuccess(List<TagHistoryItem> items) {
                        mTops = items;
                        setItemsInScroll(mTopsView, items, "You have no mTops in your closet or snags.");

                    }

                    @Override
                    public void onFail(String message) {

                    }
                });
                mTopsScrollView.setOnScrollStoppedListener(new CenteringHorizontalScrollView.OnScrollStoppedListener() {
                    @Override
                    public void onScrollStopped(View view, int index) {
                        //mSelectedTop = mTops.get(index - 1);
                        //setSelected(mSnagIndicatorTop, mTopImageView, mSelectedTop);

                    }
                });

/*                ParseFile photoFile = object.getMainImage();
                if (photoFile != null) {
                    image.setParseFile(photoFile);
                    image.loadInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            // nothing to do
                            Log.i(TAG, "Image uploaded");
                        }
                    });
                }*/
      /* 2 */
                Log.i(TAG, "Incrementing tag count");
                object.increment("tag_count"); //increments the items tag counter
                object.saveInBackground();

      /* 3 */
                Log.i(TAG, "Add item to tag history table");
                TagHistoryItem tag = new TagHistoryItem(object); //creates a tagmodel from a clothingItem
      /* 5 */
                tag.saveInBackground(); //saves to the universal tag history table
                Log.i(TAG, "item saved");
                tagObjectId = tag.getObjectId();

                ParseUser theUser = ParseUser.getCurrentUser();
                //createRelation(theUser, tag.getObjectId());

                Log.i(TAG, "The current user is:"+theUser.getString("first_name"));
                ParseRelation relation = theUser.getRelation("user_tags");
                Log.i(TAG, "the relation is: "+relation.toString());
                //relation.add(tag);
                //theUser.saveInBackground();

                relation.getQuery().findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        for(ParseObject p : list) {
                            Log.i(TAG, "Checking the relation: " + p.getObjectId());
                        }
                    }
                });
                Log.i(TAG, "The user is saved");
            }
        }
    });
    }

    public void createRelation(final ParseUser user, final String latestTagId) {
        if(user == null) {
            Log.i(TAG, "user is null");
        } else {
            Log.i(TAG, "user is not null");
            final ParseRelation<TagHistoryItem> relation = user.getRelation("user_tags");
            if(relation == null) {
                Log.i(TAG, "relation is empty");
            } else {
                Log.i(TAG, "relation is not empty");

            }
            final ParseQuery<TagHistoryItem> query = ParseQuery.getQuery("TagHistoryItem");
            query.getInBackground(latestTagId, new GetCallback<TagHistoryItem>() {
                @Override
                public void done(final TagHistoryItem item, ParseException e) {

                    Log.i(TAG, "Inside create relation and the item adding to relation is: "+item.getDescription());
                        relation.add(item);
                    Log.i(TAG, "User is: "+user.getString("first_name"));
                        user.saveInBackground();

                }
            });
        }
    }

    /**
     * Adds the items to the scrollview.
     *
     * @param view      The Layout inside the Horizontal scroller
     * @param items     The list of Tags
     * @param emptyText Message to display if the list is empty
     */
    private void setItemsInScroll(final LinearLayout view, final List<TagHistoryItem> items, final String emptyText) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                if (items.size() == 0) {
                    TextView empty = new TextView(getActivity());
                    empty.setText(emptyText);
                    //add a blank view to start.
                    view.addView(inflater.inflate(R.layout.row_item_creator_view_empty, view, false));
                    view.addView(empty);
                    view.addView(inflater.inflate(R.layout.row_item_creator_view_empty, view, false));
                    return;
                }
                int i = 1;

                //add a blank view to start.
                view.addView(inflater.inflate(R.layout.row_item_creator_view_empty, view, false));

                for (TagHistoryItem item : items) {
                    ViewGroup itemView = (ViewGroup) inflater.inflate(R.layout.row_item_creator_view, view, false);
                    ParseImageView iv = (ParseImageView) itemView.findViewById(R.id.item_image);
                    iv.setParseFile(item.getImage());
                    iv.loadInBackground();
                    ((TextView) itemView.findViewById(R.id.item_count)).setText(i + "/" + items.size());
                    i++;
                    view.addView(itemView);
                }
                //add a blank views to end.
                view.addView(inflater.inflate(R.layout.row_item_creator_view_empty, view, false));
            }
        });
    }
}
