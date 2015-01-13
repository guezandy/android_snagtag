package com.snagtag.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
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

    private LruCache<String, Bitmap> mMemoryCache;

    private List<OutfitItem> items = Collections.emptyList();

    public OutfitItemAdapter(Context context, int view) {
        super(context, view);
        mContext = context;
        mView = view;

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void setItems(List<OutfitItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void addItem(OutfitItem item) {
        boolean exists = this.items.contains(item);
        if(!exists) {
            this.items.add(item);
            this.notifyDataSetChanged();
        }
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
        private TextView deleteView;
        private TextView outfitName;

        public OutfitItemClothingHolder(View rootView, OutfitItem item) {
            this.rootView = rootView;
            this.topImage = (ParseImageView) rootView.findViewById(R.id.image_top);
            this.bottomImage = (ParseImageView) rootView.findViewById(R.id.image_bottom);
            this.shoesImage = (ParseImageView) rootView.findViewById(R.id.image_shoes);
            this.outfitName = (TextView) rootView.findViewById(R.id.text_outfit_name);
            this.deleteView = (TextView) rootView.findViewById(R.id.button_delete);
            setItem(item);
        }

        private void setImageFile(final ParseFile file, final ParseImageView view) {
            Bitmap cached = mMemoryCache.get(file.getUrl());
            if(cached != null) {
                view.setImageBitmap(cached);
            } else {
                file.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] bytes, ParseException e) {
                        Bitmap img = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        Log.d("IMAGECACHE", file.getUrl());

                        mMemoryCache.put(file.getUrl(), img);
                        view.setImageBitmap(img);
                        view.invalidate();
                    }
                });
            }
        }

        public void setItem(final OutfitItem item) {
            this.item = item;

            if(item.getTopImage() != null) {
                setImageFile(item.getTopImage(), topImage);
            } else {
                topImage.setImageBitmap(null);
            }

            //topImage.setParseFile(item.getTopImage());
            //topImage.loadInBackground();

            if(item.getBottomImage() != null) {
                setImageFile(item.getBottomImage(), bottomImage);
            } else {
                bottomImage.setImageBitmap(null);
            }
            //bottomImage.setParseFile(item.getBottomImage());
           // bottomImage.loadInBackground();

           if(item.getShoesImage() != null) {
               setImageFile(item.getShoesImage(), shoesImage);
           } else {
               shoesImage.setImageBitmap(null);
           }
           //shoesImage.setParseFile(item.getShoesImage());
           // shoesImage.loadInBackground();
            outfitName.setText(item.getOutfitTitle());
            deleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    item.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            items.remove(item);
                            OutfitItemAdapter.this.notifyDataSetChanged();
                        }
                    });
                }
            });
        }
    }
}
