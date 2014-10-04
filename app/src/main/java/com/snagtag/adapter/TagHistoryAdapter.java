package com.snagtag.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.snagtag.R;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.snagtag.models.CartItem;
import com.snagtag.models.TagHistoryItem;

/*
 * The TagHistoryAdapter is an extension of ParseQueryAdapter
 * that has a custom layout for favorite TagHistoryItems
 */

public class TagHistoryAdapter extends ParseQueryAdapter<TagHistoryItem> {
    private final String TAG = TagHistoryAdapter.class.getSimpleName();
    //TODO: Is there a reason why delete is a textview?
    TextView delete;
    ParseImageView itemImage;
    TextView description;
    TextView color;
    TextView size;
    TextView cost;
    ImageView closet;
    ImageView cart;


    public TagHistoryAdapter(Context context) {
        super(context, new ParseQueryAdapter.QueryFactory<TagHistoryItem>() {

            public ParseQuery<TagHistoryItem> create() {
                // Here we can configure a ParseQuery to display
                // only top-rated TagHistoryItems.
                ParseQuery query = new ParseQuery("TagHistoryItem");
                ParseUser user = ParseUser.getCurrentUser();
                //TODO: Algo.... no se que
                if(/*No Internet connection ||*/user != null) {
                    //grab tag history from phone
                    query.fromLocalDatastore();
                } else {
                    //grab tag history from the web
                    ParseRelation relation = user.getRelation("user_tags");
                    query = relation.getQuery();
                }
                query.whereEqualTo("visible", true);
                return query;
            }
        });
    }

    @Override
    public View getItemView(final TagHistoryItem item, View v,
                            ViewGroup parent) {

        if (v == null) {
            v = View.inflate(getContext(), R.layout.row_item_clothing_view,
                    null);
        }

        super.getItemView(item, v, parent);
//TODO: finish adding formatting for the row layout
        description = (TextView) v.findViewById(R.id.item_description);
        description.setText(item.getDescription());

        delete = (TextView) v.findViewById(R.id.delete_item);
        delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFromTagHistory(item);
            }
        });

        itemImage = (ParseImageView) v.findViewById(R.id.item_image);
        ParseFile photoFile = item.getImage();
        if (photoFile != null) {
            itemImage.setParseFile(photoFile);
            itemImage.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    // nothing to do
                    Log.i(TAG, "Image uploaded: "+item.getStore()+ " , "+item.getDescription());
                }
            });
        }

        color = (TextView) v.findViewById(R.id.item_color);
        color.setText("");

        size = (TextView) v.findViewById(R.id.item_size);
        size.setText("");

        cost = (TextView) v.findViewById(R.id.item_cost);
        cost.setText(String.valueOf(item.getPrice()));
//TODO: Reload the list after each one of these is pressed.
        closet = (ImageView) v.findViewById(R.id.item_closet);
        if(item.getInCloset() == true) {
            closet.setImageResource(R.drawable.circle_blue);
        } else {
            closet.setImageResource(R.drawable.circle_grey);
        }
        closet.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item.getInCloset() == false) {
                    addToCloset(item);
                } else {
                    Toast.makeText(v.getContext(), item.getDescription()+ " already in closet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cart = (ImageView) v.findViewById(R.id.item_cart);
        if(item.getInCart() == true) {
            cart.setImageResource(R.drawable.circle_blue);
        } else {
            cart.setImageResource(R.drawable.circle_grey);
        }
        cart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item.getInCart() == false) {
                    addTagItemToCart(item);
                } else {
                    Toast.makeText(v.getContext(), item.getDescription()+ " is already in cart.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return v;
    }

    public void addTagItemToCart(TagHistoryItem item) {
        CartItem cartItem = new CartItem(item);
        cartItem.saveInBackground();
        item.setInCart(true);
        item.saveInBackground();
    }

    public void addToCloset(TagHistoryItem item) {
        item.setInCloset(true);
        item.saveInBackground();
    }

    public void deleteFromTagHistory(TagHistoryItem item) {
        item.setVisible(false);
        item.saveInBackground();
        //TODO: unpin all items with false as visibility we don't need to store them locally
    }
}
