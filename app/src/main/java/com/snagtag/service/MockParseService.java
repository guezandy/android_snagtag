package com.snagtag.service;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.snagtag.R;
import com.snagtag.models.CartItem;
import com.snagtag.models.TagHistoryItem;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class to handle interactions with Parse.
 * <p/>
 * Note: This class does not spawn new Threads.
 * <p/>
 * Created by benjamin on 9/19/14.
 */
public class MockParseService implements IParseService {
    private int SNAGS_PER_STORE = 30;
    private int CART_ITEMS = 6;
    NumberFormat nf = NumberFormat.getCurrencyInstance();

    @Override
    public List<String> getStoresByTags(Context context) {
        List<String> list = new ArrayList<String>();
        list.add("Gap");
        list.add("Men's Warehouse");
        return list;
    }

    @Override
    public List<String> getStoresByCartItems(Context context) {
        List<String> list = new ArrayList<String>();
        list.add("Gap");
        list.add("Men's Warehouse");
        return list;
    }

    @Override
    public BaseAdapter getTagHistoryAdapter(final Context context, String store) {
        final List<TagHistoryItem> mockItems = new ArrayList<TagHistoryItem>();

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();


        for (int i = 0; i < SNAGS_PER_STORE; i++) {
            TagHistoryItem item = new TagHistoryItem();
            item.setBarcode("338383" + i);
            item.setDescription("Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit");
            item.setInCart(i % 3 == 0);
            item.setInCloset(i % 2 == 0);


            item.setPrice(i + 1.00);
            item.setStore(store);
            item.setVisible(i % 4 == 0);
            item.setImage(new ParseFile("item " + i, bitmapdata));
            mockItems.add(item);
        }

        //Creating and returning an abstract inner class.
        return new ArrayAdapter<TagHistoryItem>(context, R.layout.row_item_snag_view) {
            @Override
            public int getCount() {
                return SNAGS_PER_STORE;
            }

            @Override
            public TagHistoryItem getItem(int position) {
                return mockItems.get(position);
            }


            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView != null) {
                    RowItemClothingViewHolder holder = (RowItemClothingViewHolder) convertView.getTag();
                    holder.setItem(mockItems.get(position));
                } else {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.row_item_clothing_view, null);
                    RowItemClothingViewHolder holder = new RowItemClothingViewHolder(convertView, mockItems.get(position));
                    convertView.setTag(holder);
                }
                return convertView;
            }
        };
    }

    @Override
    public BaseAdapter getCartItemAdapter(final Context context, String store, DataSetObserver dataChangedObserver) {
        final List<CartItem> mockCartItems = new ArrayList<CartItem>();
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();


        for (int i = 0; i < CART_ITEMS; i++) {
            CartItem item = new CartItem();
            item.setItem(new TagHistoryItem());
            item.getItem().setBarcode("338383" + i);
            item.getItem().setDescription("Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit");
            item.getItem().setInCart(i % 3 == 0);
            item.getItem().setInCloset(i % 2 == 0);
            item.getItem().setPrice(i + 1.00);
            item.getItem().setStore(store);
            item.getItem().setVisible(i % 4 == 0);
            item.getItem().setImage(new ParseFile("item " + i, bitmapdata));
            mockCartItems.add(item);
        }

        //Creating and returning an abstract inner class.
        ArrayAdapter<CartItem> cartItemAdapter =
         new ArrayAdapter<CartItem>(context, R.layout.row_item_snag_view) {
            @Override
            public int getCount() {
                return CART_ITEMS;
            }

            @Override
            public CartItem getItem(int position) {
                return mockCartItems.get(position);
            }


            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView != null) {
                    RowItemClothingViewHolder holder = (RowItemClothingViewHolder) convertView.getTag();
                    holder.setItem(mockCartItems.get(position).getItem());
                } else {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.row_item_clothing_view, null);
                    RowItemClothingViewHolder holder = new RowItemClothingViewHolder(convertView, mockCartItems.get(position).getItem());
                    convertView.findViewById(R.id.item_color).setVisibility(View.GONE);
                    convertView.findViewById(R.id.item_size).setVisibility(View.GONE);
                    convertView.findViewById(R.id.item_cart).setVisibility(View.GONE);

                    convertView.setTag(holder);
                }
                return convertView;
            }
        };
        if (dataChangedObserver != null) {
            cartItemAdapter.registerDataSetObserver(dataChangedObserver);
        }
        return cartItemAdapter;
    }

    @Override
    public BaseAdapter getCheckoutItemAdapter(final Context context, String store, DataSetObserver dataChangedObserver) {
        final List<CartItem> mockCartItems = new ArrayList<CartItem>();
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();


        for (int i = 0; i < CART_ITEMS; i++) {
            CartItem item = new CartItem();
            item.setItem(new TagHistoryItem());
            item.getItem().setBarcode("338383" + i);
            item.getItem().setDescription("Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit");
            item.getItem().setInCart(i % 3 == 0);
            item.getItem().setInCloset(i % 2 == 0);
            item.getItem().setPrice(i + 1.00);
            item.getItem().setStore(store);
            item.getItem().setVisible(i % 4 == 0);
            item.getItem().setImage(new ParseFile("item " + i, bitmapdata));
            mockCartItems.add(item);
        }

        //Creating and returning an abstract inner class.
        ArrayAdapter<CartItem> cartItemAdapter =
                new ArrayAdapter<CartItem>(context, R.layout.row_item_checkout) {
                    @Override
                    public int getCount() {
                        return CART_ITEMS;
                    }

                    @Override
                    public CartItem getItem(int position) {
                        return mockCartItems.get(position);
                    }


                    @Override
                    public long getItemId(int position) {
                        return position;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {

                        if (convertView != null) {
                            RowItemClothingViewHolder holder = (RowItemClothingViewHolder) convertView.getTag();
                            holder.setItem(mockCartItems.get(position).getItem());
                        } else {
                            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            convertView = inflater.inflate(R.layout.row_item_checkout, null);
                            RowItemClothingViewHolder holder = new RowItemClothingViewHolder(convertView, mockCartItems.get(position).getItem());


                            convertView.setTag(holder);
                        }
                        return convertView;
                    }
                };
        if (dataChangedObserver != null) {
            cartItemAdapter.registerDataSetObserver(dataChangedObserver);
        }
        return cartItemAdapter;
    }

    /**
     * Inner class to map the fields on a List Item view to the fields in a model object.
     */
    class RowItemClothingViewHolder {
        private TextView description;
        private TextView color;
        private TextView size;
        private TextView cost;
        private ParseImageView image;
        private TagHistoryItem item;
        private View itemCart;

        public RowItemClothingViewHolder(View rootView, TagHistoryItem item) {
            this.description = (TextView) rootView.findViewById(R.id.item_description);
            this.color = (TextView) rootView.findViewById(R.id.item_color);
            this.size = (TextView) rootView.findViewById(R.id.item_size);
            this.cost = (TextView) rootView.findViewById(R.id.item_cost);
            this.image = (ParseImageView) rootView.findViewById(R.id.item_image);
            this.itemCart = rootView.findViewById(R.id.item_cart);
            setItem(item);
        }

        public void setItem(TagHistoryItem item) {
            this.item = item;
            this.description.setText(item.getDescription());
            this.cost.setText(nf.format(item.getPrice()));
            this.image.setParseFile(item.getImage());
            this.image.loadInBackground();
            if (item.getInCart() && this.itemCart != null) {
                this.itemCart.setEnabled(false);
            }
        }
    }

}
