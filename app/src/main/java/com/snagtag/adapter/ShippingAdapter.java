package com.snagtag.adapter;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.snagtag.MainActivity;
import com.snagtag.R;
import com.snagtag.fragment.CartFragment;
import com.snagtag.fragment.NfcLaunchFragment;
import com.snagtag.models.CartItem;
import com.snagtag.models.ShippingModel;
import com.snagtag.models.TagHistoryItem;

import java.util.Collections;
import java.util.List;

public class ShippingAdapter extends ArrayAdapter<ShippingModel> {

    List<ShippingModel> mItems = Collections.emptyList();
    Context mContext;
    private int mView;
    private View listView;

    public ShippingAdapter(Context context, int view, View listView) {
        super(context, view);
        this.mContext = context;
        mView = view;
        this.listView = listView;
    }

    public void setItems(List<ShippingModel> mItems) {
        this.mItems = mItems;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public View getView(int position, View v, final ViewGroup parent) {
        final ParseObject item = mItems.get(position);
        if (v == null) {
            v = View.inflate(mContext, mView, null);
        }

        TextView name = (TextView) v.findViewById(R.id.name);
        name.setText(item.getString("name"));

        TextView street = (TextView) v.findViewById(R.id.street);
        street.setText(item.getString("address"));

        TextView city = (TextView) v.findViewById(R.id.city_state_zip);
        city.setText(item.getString("city"));

        return v;
    }
}