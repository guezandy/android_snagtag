package com.snagtag.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.snagtag.R;
import com.snagtag.models.CartItem;
import com.snagtag.models.TagHistoryItem;

/*
 * The TagHistoryAdapter is an extension of ParseQueryAdapter
 * that has a custom layout for favorite TagHistoryItems
 */

public class TagHistoryAdapter extends ParseQueryAdapter<TagHistoryItem> {
    private final String TAG = TagHistoryAdapter.class.getSimpleName();
    //TODO: Is there a reason why delete is a textview?
    //BAA - We're going to use fontawesome and a background image for most things rather than construct
    // our buttons as a single image. Also, I've defined a single drawable in blue/grey and added a circle_button
    // drawable to display differently for active/inactive/pressed states w/o having
    // to have all sorts of image assets for each button or logic in the classes.
    TextView delete;
    ParseImageView itemImage;
    TextView description;
    TextView color;
    TextView size;
    TextView cost;
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



        cart = (ImageView) v.findViewById(R.id.item_cart);
        if(item.getInCart()) {
            cart.setImageResource(R.drawable.circle_blue_button);
        } else {
            cart.setImageResource(R.drawable.circle_grey);
            cart.setEnabled(false);
        }
        cart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!item.getInCart()) {
                    addTagItemToCart(item);
                } else {
                    Toast.makeText(v.getContext(), item.getDescription() + " is already in cart.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return v;
    }

    private void addTagItemToCart(TagHistoryItem item) {
        CartItem cartItem = new CartItem(item);
        cartItem.saveInBackground();
        item.setInCart(true);
        item.saveInBackground();
    }

    private void addToCloset(TagHistoryItem item) {
        item.setInCloset(true);
        item.saveInBackground();
    }

    private void deleteFromTagHistory(TagHistoryItem item) {
        item.setVisible(false);
        item.saveInBackground();
        //TODO: unpin all mItems with false as visibility we don't need to store them locally
    }
}
