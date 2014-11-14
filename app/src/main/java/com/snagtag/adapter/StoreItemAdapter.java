package com.snagtag.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseImageView;
import com.snagtag.R;
import com.snagtag.models.StoreItem;

/**
 * Adapter to get a list of StoreItem
 * <p/>
 * Created by benjamin on 11/7/14.
 */
public class StoreItemAdapter extends ArrayAdapter<StoreItem> {
    private int mView;

    public StoreItemAdapter(Context context, int resource) {
        super(context, resource);
        mView = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StoreViewHolder holder;
        StoreItem item = getItem(position);
        if (convertView == null) {
            convertView = View.inflate(getContext(), mView, null);
            holder = new StoreViewHolder(convertView);
        } else {
            holder = (StoreViewHolder) convertView.getTag();
        }
        holder.setStore(item);

        return convertView;
    }

    class StoreViewHolder {
        private ParseImageView logo;
        private TextView name;
        private TextView description;
        private StoreItem store;

        public StoreViewHolder(View rootView) {
            rootView.setTag(this);
            logo = (ParseImageView) rootView.findViewById(R.id.image_store);
            name = (TextView) rootView.findViewById(R.id.name_store);
            description = (TextView) rootView.findViewById(R.id.description_store);

        }

        public void setStore(StoreItem store) {
            this.store = store;
            logo.setParseFile(store.getStoreIcon());
            logo.loadInBackground();
            name.setText(store.getStoreName());
            description.setText(store.getStoreDescription());
        }

    }
}
