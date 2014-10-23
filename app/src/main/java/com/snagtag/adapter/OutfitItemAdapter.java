package com.snagtag.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseImageView;
import com.snagtag.R;
import com.snagtag.models.OutfitItem;

import java.util.Collections;
import java.util.List;

/**
 * Created by benjamin on 10/23/14.
 */
public class OutfitItemAdapter extends ArrayAdapter<OutfitItem> {
    private Context mContext;
    private int mView;

    private List<OutfitItem> items = Collections.emptyList();

    public OutfitItemAdapter(Context context, int view) {
        super(context, view);
        mContext = context;
        mView = view;
    }

    public void setItems(List<OutfitItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public OutfitItem getItem(int position) {
        return items.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView != null) {
            OutfitItemClothingHolder holder = (OutfitItemClothingHolder) convertView.getTag();
            holder.setItem(items.get(position));
        } else {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mView, null);
            OutfitItemClothingHolder holder = new OutfitItemClothingHolder(convertView, items.get(position));


            convertView.setTag(holder);
        }
        return convertView;
    }
    /**
     * Inner class to map the fields on a List Item view to the fields in a model object.
     */
    class OutfitItemClothingHolder {
        private OutfitItem item;
        private View rootView;
        private ParseImageView topImage;
        private ParseImageView bottomImage;
        private ParseImageView shoesImage;
        private TextView outfitName;

        public OutfitItemClothingHolder(View rootView, OutfitItem item) {
            this.rootView = rootView;
            this.topImage = (ParseImageView) rootView.findViewById(R.id.image_top);
            this.bottomImage = (ParseImageView) rootView.findViewById(R.id.image_bottom);
            this.shoesImage = (ParseImageView) rootView.findViewById(R.id.image_shoes);
            this.outfitName = (TextView) rootView.findViewById(R.id.text_outfit_name);
            setItem(item);
        }

        public void setItem(OutfitItem item) {
            this.item = item;
            topImage.setParseFile(item.getTopImage());
            topImage.loadInBackground();
            bottomImage.setParseFile(item.getBottomImage());
            bottomImage.loadInBackground();
            shoesImage.setParseFile(item.getShoesImage());
            shoesImage.loadInBackground();
            outfitName.setText(item.getOutfitTitle());
        }
    }
}
