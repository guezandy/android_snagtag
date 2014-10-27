package com.snagtag.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
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
public class ParseService {
    private final String TAG = ParseService.class.getSimpleName();
    private int SNAGS_PER_STORE = 10;
    private int CART_ITEMS = 6;
    private int OUTFIT_ITEMS = 10;

    static int counter = 1;
    private Context context;

    NumberFormat nf = NumberFormat.getCurrencyInstance();

    public ParseService(Context context) {
        this.context = context;
    }

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


    public void getStoresByCartItems(Context context, final IParseCallback<List<String>> storesCallback) {
        List<String> list = new ArrayList<String>();
        list.add("Gap");
        list.add("Men's Warehouse");
        storesCallback.onSuccess(list);
    }


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
                    //TODO: Remove this code, it is for testing only.
                    for (int i = 0; i < 20; i++) {
                        results.add(buildDummyTagHistoryItem(store, i, context));
                    }

                    itemsCallback.onSuccess(results);
                }
            }
        });

    }

    public void getCartItems(final Context context, final String store, final IParseCallback<List<CartItem>> itemsCallback) {


        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //TODO: Call Parse
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bitmapdata = stream.toByteArray();

                List<CartItem> cartItems = new ArrayList<CartItem>();
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
                    cartItems.add(item);
                }

                itemsCallback.onSuccess(cartItems);
            }
        });
        t.start();

    }

    public void getOutfitItems(final Context context, final IParseCallback<List<OutfitItem>> itemCallback) {
        //Changing this to get the list on a background thread so the UI works smoother.
        final List<OutfitItem> mockOutfitItems = new ArrayList<OutfitItem>();


        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < OUTFIT_ITEMS; i++) {
                    OutfitItem item = new OutfitItem();
                    TagHistoryItem top = buildDummyTagHistoryItem("", i, context);
                    TagHistoryItem bottom = buildDummyTagHistoryItem("", i, context);
                    TagHistoryItem shoes = buildDummyTagHistoryItem("", i, context);

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
                itemCallback.onSuccess(mockOutfitItems);

            }
        });
        t.start();


    }


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
    //TODO: Remove me, for testing only
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
}