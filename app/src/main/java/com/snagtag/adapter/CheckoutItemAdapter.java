package com.snagtag.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.snagtag.models.CartItem;

import java.util.Collections;
import java.util.List;

/**
 * Created by benjamin on 10/23/14.
 */
public class CheckoutItemAdapter extends ArrayAdapter<CartItem> {


    private Context mContext;
    private int mView;
    private List<CartItem> items = Collections.emptyList();

    public CheckoutItemAdapter(Context context, int view) {
        super(context,view);
        mContext = context;
        mView = view;
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

        /*if (convertView != null) {
            RowItemClothingViewHolder holder = (RowItemClothingViewHolder) convertView.getTag();
            holder.setItem(items.get(position).getItem());
        } else {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_item_checkout, null);
            RowItemClothingViewHolder holder = new RowItemClothingViewHolder(convertView, items.get(position).getItem());


            convertView.setTag(holder);
        }*/
        return convertView;
    }

}
