package com.snagtag.service;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.snagtag.R;
import com.snagtag.models.CartItem;
import com.snagtag.models.OutfitItem;
import com.snagtag.models.TagHistoryItem;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Service class to handle interactions with Parse.
 * <p/>
 * Note: This class does not spawn new Threads.
 * <p/>
 * Created by benjamin on 9/19/14.
 */
public class MockParseService implements IParseService {
    private final String TAG = MockParseService.class.getSimpleName();
    private int SNAGS_PER_STORE = 10;
    private int CART_ITEMS = 6;
    private int OUTFIT_ITEMS = 10;

    static int counter = 1;
    private Context context;

    NumberFormat nf = NumberFormat.getCurrencyInstance();

    public MockParseService(Context context) {
        this.context = context;
    }


    @Override
    public void getStoresByTags(Context context, final IParseCallback<List<String>> storesCallback) {
        //ParseQuery query = new ParseQuery("TagHistoryItem");
        ParseUser user = ParseUser.getCurrentUser();
        ParseRelation relation = user.getRelation("user_tags");
        ParseQuery query = relation.getQuery();
        query.whereEqualTo("visible", true);
        final Set<String> set = new TreeSet<String>();
        final List<String> list = new ArrayList<String>();

        query.findInBackground(new FindCallback<TagHistoryItem>() {
            @Override
            public void done(List<TagHistoryItem> snags, ParseException e) {
                if (e != null) {
                    // There was an error
                } else {
                    // snags have all the Clothing the current user tagged.
                    for (TagHistoryItem snag : snags) {
                        Log.i(TAG, "query got: " + snag.getStore());
                        set.add(snag.getStore().toString());
                    }
                    list.addAll(set);
                    list.add("Gap");
                    list.add("Men's Warehouse");
                    storesCallback.onSuccess(list);
                }
            }
        });
        //List<String> list = Arrays.asList(set.toArray(new String[0]));
        Log.i(TAG, "Printing set: " + set.toString());
        //list.addAll(set);

    }

    @Override
    public void getStoresByCartItems(Context context, final IParseCallback<List<String>> storesCallback) {
        List<String> list = new ArrayList<String>();
        list.add("Gap");
        list.add("Men's Warehouse");
        storesCallback.onSuccess(list);
    }

    @Override
    public void getTagHistory(final Context context, final String store, final IParseCallback<List<TagHistoryItem>> itemsCallback) {

        //call itemsCallback.onSuccess with the List<TagHistoryItem> from Parse

        ParseUser user = ParseUser.getCurrentUser();
        Log.i(TAG, user.toString());
        ParseRelation<TagHistoryItem> relation = user.getRelation("user_tags");
        ParseQuery query = relation.getQuery();
        //make sure user didn't delete any snags
        query.whereEqualTo("visible", false);

        //TODO: no stores yet
        query.whereEqualTo("store", store);

        //get ten most recent
        query.orderByDescending("createdAt");
        query.setLimit(SNAGS_PER_STORE);

        query.findInBackground(new FindCallback<TagHistoryItem>() {
            @Override
            public void done(List<TagHistoryItem> results, ParseException e) {
                if (e != null) {
                    // There was an error
                } else {
                    // results have all the Clothing the current user tagged.
                    //Log.i(TAG, results.size());
                    itemsCallback.onSuccess(results);
                }
            }
        });

    }


    @Override
    public ParseQueryAdapter TagHistoryAdapter(final Context context, final String store, DataSetObserver dataChangedObserver) {
        // Adapter for the Parse query

        ParseQueryAdapter.QueryFactory<TagHistoryItem> factory =
                new ParseQueryAdapter.QueryFactory<TagHistoryItem>() {
                    public ParseQuery<TagHistoryItem> create() {
                        //ParseQuery query = new ParseQuery("TagHistoryItem");
                        ParseUser user = ParseUser.getCurrentUser();

                        //grabs all user tags
                        ParseRelation relation = user.getRelation("user_tags");
                        //get query so we can sub-query
                        ParseQuery query = relation.getQuery();
                        //make sure user didnt delete any snags
                        query.whereEqualTo("visible", true);

                        //TODO: no stores yet
                        //query.whereEqualTo("store", store);

                        //get ten most recent
                        query.orderByDescending("createdAt");
                        query.setLimit(SNAGS_PER_STORE);

                        return query;
                    }
                };
        //setup query adapter
        final ParseQueryAdapter<TagHistoryItem> tagHistoryPerStore = new ParseQueryAdapter<TagHistoryItem>(context, factory) {
            final ParseQueryAdapter<TagHistoryItem> me = this;

            @Override
            public View getItemView(final TagHistoryItem item, View v, ViewGroup parent) {
                if (v == null) {
                    v = View.inflate(getContext(), R.layout.row_item_clothing_view, null);
                }
                TextView description = (TextView) v.findViewById(R.id.item_description);
                description.setText(item.getDescription() + counter++);

                com.snagtag.ui.IconCustomTextView delete = (com.snagtag.ui.IconCustomTextView) v.findViewById(R.id.delete_item);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // delete From Tag History
                        item.setVisible(false);
                        item.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                me.notifyDataSetChanged();
                            }
                        });

                        //TODO: unpin all items with false as visibility we don't need to store them locally
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
                            Log.i(TAG, "Image uploaded: " + item.getStore() + " , " + item.getDescription());
                        }
                    });
                }

                TextView color = (TextView) v.findViewById(R.id.item_color);
                color.setText(String.format(context.getResources().getString(R.string.item_color), item.getString("color")));

                TextView size = (TextView) v.findViewById(R.id.item_size);
                size.setText(String.format(context.getResources().getString(R.string.item_size), item.getString("size")));

                TextView cost = (TextView) v.findViewById(R.id.item_cost);
                cost.setText(String.format(context.getResources().getString(R.string.item_cost), item.getPrice()));
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
                                    me.notifyDataSetChanged();
                                }
                            });

                        } else {
                            Toast.makeText(v.getContext(), item.getDescription() + " is already in cart.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                return v;
            }
        };
        tagHistoryPerStore.registerDataSetObserver(dataChangedObserver);
        Log.d("TAG_HISTORY_ITEM","TAG HISTORY ITEMS " + tagHistoryPerStore.getCount());
        return tagHistoryPerStore;
    }

    @Override
    public BaseAdapter CartAdapter(final Context context, final String store, DataSetObserver dataChangedObserver) {
        // Adapter for the Parse query
        ParseQueryAdapter.QueryFactory<TagHistoryItem> factory =
                new ParseQueryAdapter.QueryFactory<TagHistoryItem>() {
                    public ParseQuery<TagHistoryItem> create() {
                        //ParseQuery query = new ParseQuery("TagHistoryItem");
                        ParseUser user = ParseUser.getCurrentUser();

                        //grabs all user tags
                        ParseRelation relation = user.getRelation("user_tags");
                        //get query so we can sub-query
                        ParseQuery query = relation.getQuery();

                        //items in cart
                        query.whereEqualTo("inCart", true);
                        //make sure user didnt delete any snags
                        //query.whereEqualTo("visible", true);

                        //TODO: no stores yet but we'll sort the cart by stores
                        //query.whereEqualTo("store", store);

                        //get ten most recent
                        query.orderByDescending("createdAt");
                        //query.setLimit(SNAGS_PER_STORE);

                        return query;
                    }
                };
        //setup query adapter
        ParseQueryAdapter<TagHistoryItem> UserCartPerStore;
        UserCartPerStore = new ParseQueryAdapter<TagHistoryItem>(context, factory) {
            @Override
            public View getItemView(final TagHistoryItem item, View v, ViewGroup parent) {
                if (v == null) {
                    v = View.inflate(getContext(), R.layout.row_item_clothing_view, null);
                }
                TextView description = (TextView) v.findViewById(R.id.item_description);
                description.setText(item.getDescription());

                com.snagtag.ui.IconCustomTextView delete = (com.snagtag.ui.IconCustomTextView) v.findViewById(R.id.delete_item);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO: do we want to delete From Tag History too?
                        //item.setVisible(false);
                        item.setInCart(false);
                        item.saveInBackground();
                        //TODO: unpin all items with false as visibility we don't need to store them locally
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
                            Log.i(TAG, "Image uploaded: " + item.getStore() + " , " + item.getDescription());
                        }
                    });
                }

                TextView color = (TextView) v.findViewById(R.id.item_color);
                color.setText(String.format(context.getResources().getString(R.string.item_color), item.getString("color")));

                TextView size = (TextView) v.findViewById(R.id.item_size);
                size.setText(String.format(context.getResources().getString(R.string.item_size), item.getString("size")));

                TextView cost = (TextView) v.findViewById(R.id.item_cost);
                cost.setText(String.format(context.getResources().getString(R.string.item_cost), item.getPrice()));
//TODO: Reload the list after each one of these is pressed.


                com.snagtag.ui.IconCustomTextView cart = (com.snagtag.ui.IconCustomTextView) v.findViewById(R.id.item_cart);
                cart.setVisibility(View.GONE);
                if (!item.getInCart()) {
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
                            item.saveInBackground();
                        } else {
                            Toast.makeText(v.getContext(), item.getDescription() + " is already in cart.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                return v;
            }
        };

        if (dataChangedObserver != null) {
            UserCartPerStore.registerDataSetObserver(dataChangedObserver);
        }
        return UserCartPerStore;
    }

    @Override
    public BaseAdapter getTagHistoryAdapter(final Context context, String store) {
        final List<TagHistoryItem> mockItems = new ArrayList<TagHistoryItem>();
        for (int i = 0; i < SNAGS_PER_STORE; i++) {
            TagHistoryItem item = buildDummyTagHistoryItem(store, i, context);
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


    private TagHistoryItem buildDummyTagHistoryItem(String store, int i, Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();
        TagHistoryItem item = new TagHistoryItem();
        item.setBarcode("338383" + i);
        item.setDescription("Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit");
        item.setInCart(i % 3 == 0);
        item.setInCloset(i % 2 == 0);
        item.setPrice(i + 1.00);
        item.setStore(store == null ? "" : store);
        item.setVisible(i % 4 == 0);
        item.setImage(new ParseFile("item " + i, bitmapdata));
        return item;
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
                new ArrayAdapter<CartItem>(context, R.layout.row_item_clothing_view) {
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
            this.description.setText(item.getDescription());

            Double priceVal = item.getPrice();
            String colorVal = item.getString("color");
            String sizeVal =  item.getString("size");

            this.cost.setText(String.format(context.getResources().getString(R.string.item_cost), priceVal == null ? 0.00 : priceVal));
            this.color.setText(String.format(context.getResources().getString(R.string.item_color), colorVal == null ? "" : colorVal));
            this.size.setText(String.format(context.getResources().getString(R.string.item_size), sizeVal == null ? "" : sizeVal));
            this.image.setParseFile(item.getImage());
            this.image.loadInBackground();
            if (item.getInCart() && this.itemCart != null) {
                this.itemCart.setEnabled(false);
            }

        }
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

    @Override
    public BaseAdapter getOutfitItemAdapter(final Context context, final String store, final DataSetObserver dataChangedObserver) {
        //Changing this to get the list on a background thread so the UI works smoother.
        final List<OutfitItem> mockOutfitItems = new ArrayList<OutfitItem>();

        //Creating and returning an abstract inner class.
        final ArrayAdapter<OutfitItem> outfitItemArrayAdapter =
                new ArrayAdapter<OutfitItem>(context, R.layout.row_item_outfit_view) {
                    @Override
                    public int getCount() {
                        return mockOutfitItems.size();
                    }

                    @Override
                    public OutfitItem getItem(int position) {
                        return mockOutfitItems.get(position);
                    }


                    @Override
                    public long getItemId(int position) {
                        return position;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {

                        if (convertView != null) {
                            OutfitItemClothingHolder holder = (OutfitItemClothingHolder) convertView.getTag();
                            holder.setItem(mockOutfitItems.get(position));
                        } else {
                            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            convertView = inflater.inflate(R.layout.row_item_outfit_view, null);
                            OutfitItemClothingHolder holder = new OutfitItemClothingHolder(convertView, mockOutfitItems.get(position));


                            convertView.setTag(holder);
                        }
                        return convertView;
                    }
                };

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < OUTFIT_ITEMS; i++) {
                    OutfitItem item = new OutfitItem();
                    TagHistoryItem top = buildDummyTagHistoryItem(store, i, context);
                    TagHistoryItem bottom = buildDummyTagHistoryItem(store, i, context);
                    TagHistoryItem shoes = buildDummyTagHistoryItem(store, i, context);

                    item.setBottomImage(bottom);
                    item.setBottomInCloset(bottom);
                    item.setBottomRelation(bottom);
                    item.setOutfitTitle("Outfit " + i);
                    item.setOwnEntireOutfit();
                    item.setShoesImage(shoes);
                    item.setShoesInCloset(shoes);
                    item.setShoesRelation(shoes);
                    item.setTopImage(top);
                    item.setTopInCloset(top);
                    item.setTopRelation(top);

                    mockOutfitItems.add(item);
                }
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        outfitItemArrayAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        t.start();


        if (dataChangedObserver != null) {
            outfitItemArrayAdapter.registerDataSetObserver(dataChangedObserver);
        }
        return outfitItemArrayAdapter;

    }

    @Override
    public void getTops(final Context context, final IParseCallback<List<TagHistoryItem>> callback) {
        final List<TagHistoryItem> mockItems = new ArrayList<TagHistoryItem>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < SNAGS_PER_STORE; i++) {
                    TagHistoryItem item = buildDummyTagHistoryItem("", i, context);
                    mockItems.add(item);
                }

                callback.onSuccess(mockItems);
            }
        });
        t.start();


    }

    @Override
    public void getBottoms(final Context context, final IParseCallback<List<TagHistoryItem>> callback) {
        final List<TagHistoryItem> mockItems = new ArrayList<TagHistoryItem>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < SNAGS_PER_STORE; i++) {
                    TagHistoryItem item = buildDummyTagHistoryItem("", i, context);
                    mockItems.add(item);
                }

                callback.onSuccess(mockItems);
            }
        });
        t.start();
    }

    @Override
    public void getShoes(final Context context, final IParseCallback<List<TagHistoryItem>> callback) {
        final List<TagHistoryItem> mockItems = new ArrayList<TagHistoryItem>();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < SNAGS_PER_STORE; i++) {
                    TagHistoryItem item = buildDummyTagHistoryItem("", i, context);
                    mockItems.add(item);
                }
                callback.onSuccess(mockItems);

            }
        });
        t.start();


    }

    @Override
    public void getClosetTops(final Context context, final IParseCallback<List<TagHistoryItem>> callback) {
        final List<TagHistoryItem> mockItems = new ArrayList<TagHistoryItem>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < SNAGS_PER_STORE; i++) {
                    TagHistoryItem item = buildDummyTagHistoryItem("", i, context);
                    mockItems.add(item);
                }

                callback.onSuccess(mockItems);
            }
        });
        t.start();
    }

    @Override
    public void getClosetBottoms(final Context context, final IParseCallback<List<TagHistoryItem>> callback) {
        final List<TagHistoryItem> mockItems = new ArrayList<TagHistoryItem>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < SNAGS_PER_STORE; i++) {
                    TagHistoryItem item = buildDummyTagHistoryItem("", i, context);
                    mockItems.add(item);
                }

                callback.onSuccess(mockItems);
            }
        });
        t.start();
    }

    @Override
    public void getClosetShoes(final Context context, final IParseCallback<List<TagHistoryItem>> callback) {
        final List<TagHistoryItem> mockItems = new ArrayList<TagHistoryItem>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < SNAGS_PER_STORE; i++) {
                    TagHistoryItem item = buildDummyTagHistoryItem("", i, context);
                    mockItems.add(item);
                }

                callback.onSuccess(mockItems);
            }
        });
        t.start();
    }
}
