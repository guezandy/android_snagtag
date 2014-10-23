package com.snagtag.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.SaveCallback;
import com.snagtag.R;
import com.snagtag.models.CartItem;
import com.snagtag.models.TagHistoryItem;

import java.util.Collections;
import java.util.List;

public class TagHistoryItemAdapter extends ArrayAdapter<TagHistoryItem> {

    List<TagHistoryItem> mItems = Collections.emptyList();
    Context mContext;
    private int mView;

    public TagHistoryItemAdapter(Context context, int view) {
        super(context, view);
        this.mContext = context;
        mView = view;
    }

    public void setItems(List<TagHistoryItem> mItems) {
        this.mItems = mItems;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        final TagHistoryItem item = mItems.get(position);
        if (v == null) {
            v = View.inflate(mContext, mView, null);
        }
        TextView description = (TextView) v.findViewById(R.id.item_description);

        description.setText("");

        com.snagtag.ui.IconCustomTextView delete = (com.snagtag.ui.IconCustomTextView) v.findViewById(R.id.delete_item);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // delete From Tag History
                item.setVisible(false);
                item.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        TagHistoryItemAdapter.this.notifyDataSetChanged();
                    }
                });

                //TODO: unpin all mItems with false as visibility we don't need to store them locally
            }
        });

        ParseImageView itemImage = (ParseImageView) v.findViewById(R.id.item_image);
        ParseFile photoFile = item.getImage();
        if (photoFile != null) {
            itemImage.setParseFile(photoFile);
            itemImage.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    // nothing to do
                    Log.i("TAG", "Image uploaded: " + item.getStore() + " , " + item.getDescription());
                }
            });
        }

        TextView color = (TextView) v.findViewById(R.id.item_color);
        color.setText(String.format(mContext.getResources().getString(R.string.item_color), item.getString("color")));

        TextView size = (TextView) v.findViewById(R.id.item_size);
        size.setText(String.format(mContext.getResources().getString(R.string.item_size), item.getString("size")));

        TextView cost = (TextView) v.findViewById(R.id.item_cost);
        cost.setText(String.format(mContext.getResources().getString(R.string.item_cost), item.getPrice()));
//TODO: Reload the list after each one of these is pressed.
        com.snagtag.ui.IconCustomTextView cart = (com.snagtag.ui.IconCustomTextView) v.findViewById(R.id.item_cart);
        if (item.getInCart()) {
            cart.setEnabled(false);
        }
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!item.getInCart()) {
                    //add item to cart
                    CartItem cartItem = new CartItem(item);
                    cartItem.saveInBackground();
                    item.setInCart(true);
                    item.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            TagHistoryItemAdapter.this.notifyDataSetChanged();
                        }
                    });

                } else {
                    Toast.makeText(v.getContext(), item.getDescription() + " is already in cart.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return v;
    }
}