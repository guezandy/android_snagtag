package com.snagtag.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.snagtag.R;
import com.snagtag.models.CartItem;
import com.snagtag.models.TagHistoryItem;
import com.snagtag.service.ParseService;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by benjamin on 10/23/14.
 */
public class CartItemAdapter extends ArrayAdapter<CartItem> {
    private final String TAG = CartItemAdapter.class.getSimpleName();
    private Context mContext;
    private int mView;

    private List<CartItem> items = Collections.emptyList();

    public CartItemAdapter(Context context, int view) {
        super(context, view);
        mContext = context;
        mView = view;

    }

    public void setItems(List<CartItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public CartItem getItem(int position) {
        return items.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView != null) {
            RowItemClothingViewHolder holder = (RowItemClothingViewHolder) convertView.getTag();
            holder.setItem(items.get(position).getItem());
        } else {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mView, null);
            RowItemClothingViewHolder holder = new RowItemClothingViewHolder(convertView, items.get(position).getItem());


            convertView.setTag(holder);
        }
        return convertView;
    }


    /**
     * Inner class to map the fields on a List Item view to the fields in a model object.
     */
    class RowItemClothingViewHolder {
        private TextView description;
        private TextView color;
        private TextView size;
        private TextView cost;
        private TextView deleteButton;
        private ParseImageView image;

        private View itemCart;

        public RowItemClothingViewHolder(View rootView, TagHistoryItem item) {
            this.description = (TextView) rootView.findViewById(R.id.item_description);
            this.color = (TextView) rootView.findViewById(R.id.item_color);
            this.size = (TextView) rootView.findViewById(R.id.item_size);
            this.cost = (TextView) rootView.findViewById(R.id.item_cost);
            this.image = (ParseImageView) rootView.findViewById(R.id.item_image);
            this.itemCart = rootView.findViewById(R.id.item_cart);
            this.deleteButton = (TextView)rootView.findViewById(R.id.button_delete);
            setItem(item);
        }

        public void setItem(final TagHistoryItem item) {
            if(this.description != null) {
                this.description.setText(item.getDescription());
            }

            //Double priceVal = item.getPrice();
            String colorVal = item.getString("color");
            String sizeVal = item.getString("size");

            //this.cost.setText(String.format(mContext.getResources().getString(R.string.item_cost), priceVal == null ? 0.00 : priceVal));
            if(this.color != null) {
                this.color.setText(String.format(mContext.getResources().getString(R.string.item_color), colorVal == null ? "" : colorVal));
            }
            if(this.size != null) {
                this.size.setText(String.format(mContext.getResources().getString(R.string.item_size), sizeVal == null ? "" : sizeVal));
            }
            if(this.image != null) {
                this.image.setParseFile(item.getImage());
                this.image.loadInBackground();
            }
            if (item.getInCart() && this.itemCart != null) {
                this.itemCart.setEnabled(false);
            }
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    item.setInCart(false);
                    item.saveInBackground();
                    ParseQuery<CartItem> query = ParseQuery.getQuery("CartItem");
                    //query.whereEqualTo("user", ParseUser.getCurrentUser());
                    query.whereEqualTo("itemId", item.getObjectId());
                    query.getFirstInBackground(new GetCallback<CartItem>() {
                        @Override
                        public void done(CartItem cartItem, ParseException e) {
                            if(cartItem == null) {
                                Log.i(TAG, "Didnt get the cart item to delete");
                            } else {
                                Log.i(TAG, "Deleting item");
                                cartItem.deleteInBackground(new DeleteCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                    }
                                });
                            }
                        }
                    });
                    CartItemAdapter.this.notifyDataSetChanged();
                }
            });
        }
    }
}


