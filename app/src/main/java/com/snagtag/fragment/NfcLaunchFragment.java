package com.snagtag.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.snagtag.MainActivity;
import com.snagtag.R;
import com.snagtag.SnagtagApplication;
import com.snagtag.models.CartItem;
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
    private LinearLayout mSimilarView;
    private CenteringHorizontalScrollView mSimilarScrollView;
    List<ClothingItem> mSimilar;
    private ParseImageView mItemImage;
    private Spinner mSizeSpinner;
    private TextView addToCart;
    private TagHistoryItem tag;


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
        mSimilarScrollView = (CenteringHorizontalScrollView) mItemView.findViewById(R.id.similar_scroll_view);
        mSimilarView = (LinearLayout) mItemView.findViewById(R.id.similar_view);
        mItemImage = (ParseImageView) mItemView.findViewById(R.id.nfc_item_image);
        mSizeSpinner = (Spinner) mItemView.findViewById(R.id.size_spinner);
        addToCart = (TextView) mItemView.findViewById(R.id.button_cart);

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
                final ClothingItem object = (ClothingItem) item;
                brand.setText(object.getStore());
                description.setText(object.getDescription());
                price.setText("$"+object.getPrice().toString());

                /*Gets similar item scroller images**/
                new ParseService(getActivity().getApplicationContext()).getSimilarItems(getActivity().getApplicationContext(), object, new IParseCallback<List<ClothingItem>>() {
                    @Override
                    public void onSuccess(List<ClothingItem> items) {
                        mSimilar = items;
                        setItemsInScroll(mSimilarView, items, "No Similar Items");
                    }

                    @Override
                    public void onFail(String message) {

                    }
                });
                mSimilarScrollView.setOnScrollStoppedListener(new CenteringHorizontalScrollView.OnScrollStoppedListener() {
                    @Override
                    public void onScrollStopped(View view, int index) {
                        //mSelectedTop = mTops.get(index - 1);
                        //setSelected(mSnagIndicatorTop, mTopImageView, mSelectedTop);

                    }
                });

                ParseFile photoFile = object.getMainImage();
                if (photoFile != null) {
                    mItemImage.setParseFile(photoFile);
                    mItemImage.loadInBackground(new GetDataCallback() {
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
                final ParseQuery<TagHistoryItem> avoidDuplicateQuery = ParseQuery.getQuery("TagHistoryItem"); //or clothingItem
                avoidDuplicateQuery.whereEqualTo("barcode", object.getBarcode());
                avoidDuplicateQuery.whereEqualTo("user", ParseUser.getCurrentUser());
                avoidDuplicateQuery.getFirstInBackground(new GetCallback<TagHistoryItem>()
                {
                    public void done(TagHistoryItem exists, ParseException e) {
                        if(e == null) {
                            //object exists
                            tag = exists;
                            if(tag.getVisible() == false) {
                                tag.setVisible(true);
                            }
                            tag.saveInBackground();
                            Toast.makeText(getActivity(), "Duplicate Snag", Toast.LENGTH_SHORT).show();
                        } else {
                            if(e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                                //object doesn't exist
                                tag = new TagHistoryItem(object);
                                tag.saveInBackground(); //saves to the universal tag history table
                                Toast.makeText(getActivity(), "Added to Snags", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                //unknown error, debug
                            }
                        }
                    }
                });
      /* 5 */
                Log.i(TAG, "item saved");
                if(tag != null) {
                    tagObjectId = tag.getObjectId();
                }

                ParseUser theUser = ParseUser.getCurrentUser();
                //createRelation(theUser, tag.getObjectId());

               /**Add to cart button**/
                addToCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!tag.getInCart()) {
                            addTagItemToCart(tag);
                        } else {
                            Toast.makeText(v.getContext(), tag.getDescription() + " is already in cart.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                Log.i(TAG, "The current user is:"+theUser.getString("first_name"));
                //ParseRelation relation = theUser.getRelation("user_tags");
                //Log.i(TAG, "the relation is: "+relation.toString());
                //relation.add(tag);
                //theUser.saveInBackground();

/*                relation.getQuery().findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        for(ParseObject p : list) {
                            Log.i(TAG, "Checking the relation: " + p.getObjectId());
                        }
                    }
                });
                Log.i(TAG, "The user is saved");
*/
                /**Setup sizes**/
                if(object.getType().equals("shoe")) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                            R.array.shoe_sizes, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSizeSpinner.setAdapter(adapter);
                } else {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                            R.array.clothes_sizes, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSizeSpinner.setAdapter(adapter);
                }
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
                    Log.i(TAG, "user is: "+user.getString("first_name"));
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
    private void setItemsInScroll(final LinearLayout view, final List<ClothingItem> items, final String emptyText) {
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

                for (final ClothingItem item : items) {
                    ViewGroup itemView = (ViewGroup) inflater.inflate(R.layout.row_item_creator_view, view, false);
                    ParseImageView iv = (ParseImageView) itemView.findViewById(R.id.item_image);
                    iv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getActivity(), "Pressed: "+item.getDescription(), Toast.LENGTH_SHORT).show();
                            NfcLaunchFragment frag = new NfcLaunchFragment();
                            frag.setItemId(item.getBarcode());
                            ((MainActivity) getActivity()).replaceFragment(frag, true, FragmentTransaction.TRANSIT_FRAGMENT_FADE, getString(R.string.title_snagtag));
                        }
                    });
                    iv.setParseFile(item.getMainImage());
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

    private void addTagItemToCart(TagHistoryItem item) {
        CartItem cartItem = new CartItem(item);
        cartItem.saveInBackground();
        item.setInCart(true);
        item.saveInBackground();
        Toast.makeText(getActivity(), "Added "+item.getDescription()+" Cart", Toast.LENGTH_SHORT).show();
    }
}
