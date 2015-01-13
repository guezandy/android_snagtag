package com.snagtag.service;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.snagtag.ParseLoginDispatchActivity;
import com.snagtag.R;
import com.snagtag.models.CartItem;
import com.snagtag.models.OutfitItem;
import com.snagtag.models.StoreItem;
import com.snagtag.models.TagHistoryItem;
import com.snagtag.models.UserModel;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import com.stripe.android.*;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.AuthenticationException;

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

    public boolean APPDEBUG = false;

    public ParseService(Context context) {
        this.context = context;
    }

    public void getStoresByTags(Context context, final IParseCallback<List<String>> storesCallback) {
        //ParseQuery query = new ParseQuery("TagHistoryItem");
        //ParseUser user = ParseUser.getCurrentUser();
        //ParseRelation relation = user.getRelation("user_tags");
        //ParseQuery query = relation.getQuery();
        ParseQuery<TagHistoryItem> query = ParseQuery.getQuery("TagHistoryItem");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
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
                    if (APPDEBUG) {
                        list.add("Gap");
                        list.add("Men's Warehouse");
                    }
                    storesCallback.onSuccess(list);
                }
            }
        });
    }


    public void getStoresByCartItems(Context context, final IParseCallback<List<String>> storesCallback) {

        if(APPDEBUG) {
            final List<String> list = new ArrayList<String>();
            list.add("Gap");
            list.add("Men's Warehouse");
            storesCallback.onSuccess(list);
        } else {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("CartItem");
            query.whereEqualTo("user", ParseUser.getCurrentUser());
            query.whereEqualTo("visible", true);

            final List<String> list = new ArrayList<String>();
            final Set<String> set = new TreeSet<String>();

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> entireCart, ParseException e) {
                    if (e != null) {
                        // There was an error
                    } else {
                        // snags have all the Clothing the current user tagged.
                        final List<String> outterList = new ArrayList<String>();
                        final List<String> innerList = new ArrayList<String>();
                        for (ParseObject singleCartItem : entireCart) {
                            Log.i(TAG, "cart query got: " + singleCartItem.getString("itemId"));
                            outterList.add(singleCartItem.getString("itemId"));
                        }
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("TagHistoryItem");
                        query.whereContainedIn("objectId", outterList);
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> parseObjects, ParseException e) {
                                if (e != null) {
                                    // There was an error
                                } else {
                                    // snags have all the Clothing the current user tagged.
                                    for (ParseObject cartItemAsTag : parseObjects) {
                                        //Log.i(TAG, "query got: " + snag.getStore());
                                        set.add(cartItemAsTag.getString("store"));
                                    }
                                    innerList.addAll(set);
                                    storesCallback.onSuccess(innerList);
                                }
                            }
                        });
                        if(APPDEBUG) {
                            list.add("Gap");
                            list.add("Men's Warehouse");
                        }
                        //storesCallback.onSuccess(list);
                    }
                }
            });
        }
    }


    public void getTagHistory(final Context context, final String store, final IParseCallback<List<TagHistoryItem>> itemsCallback) {


        ParseQuery<TagHistoryItem> query = ParseQuery.getQuery("TagHistoryItem");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.whereEqualTo("store", store);
        //get ten most recent
        query.setLimit(SNAGS_PER_STORE);
        //query.whereEqualTo("visible", true);
        query.orderByDescending("createdAt");


        query.findInBackground(new FindCallback<TagHistoryItem>() {
            @Override
            public void done(List<TagHistoryItem> results, ParseException e) {
                if (e != null) {
                    // There was an error
                } else {
                    // results have all the Clothing the current user tagged.
                    //Log.i(TAG, results.size());
                    //TODO: Remove this code, it is for testing only.
                    //for (int i = 0; i < 20; i++) {
                    //    results.add(buildDummyTagHistoryItem(store, i, context));
                    //}

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
                if(APPDEBUG) {
                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] bitmapdata = stream.toByteArray();
                }

                List<CartItem> cartItems = new ArrayList<CartItem>();
                ParseQuery<CartItem> query = ParseQuery.getQuery("CartItem");
                query.whereEqualTo("user", ParseUser.getCurrentUser());
                //query.whereEqualTo("visible", true);

                final List<String> list = new ArrayList<String>();
                final Set<String> set = new TreeSet<String>();

                query.findInBackground(new FindCallback<CartItem>() {
                    @Override
                    public void done(List<CartItem> entireCart, ParseException e) {
                        if (e != null) {
                            // There was an error
                        } else {
                            final List<CartItem> innerList = new ArrayList<CartItem>();
                            for (CartItem singleCartItem : entireCart) {
                                Log.i(TAG, "cart items query: " + singleCartItem.getString("itemId"));
                                if(singleCartItem.getParseObject("item").getString("store").equals(store))
                                    innerList.add(singleCartItem);
                            }
                            itemsCallback.onSuccess(innerList);
                        }
                    }
                });


                if(APPDEBUG) {
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
                        //item.getItem().setImage(new ParseFile("item " + i, bitmapdata));
                        cartItems.add(item);
                    }
                }

                //itemsCallback.onSuccess(cartItems);
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
                ParseQuery<OutfitItem> query = ParseQuery.getQuery("OutfitModel");
                //query.whereEqualTo("user", ParseUser.getCurrentUser());
                //query.whereEqualTo("visible", true);
                //get ten most recent
                query.orderByDescending("createdAt");

                query.findInBackground(new FindCallback<OutfitItem>() {
                    @Override
                    public void done(List<OutfitItem> outfits, ParseException e) {
                        if (e != null) {
                            // There was an error
                        } else {
                            itemCallback.onSuccess(outfits);
                        }
                    }
                });

/*                for (int i = 0; i < OUTFIT_ITEMS; i++) {
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
*/
            }
        });
        t.start();
    }

    public void getTops(final Context context, final IParseCallback<List<TagHistoryItem>> callback) {
        final List<TagHistoryItem> tops = new ArrayList<TagHistoryItem>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                ParseQuery<TagHistoryItem> query = ParseQuery.getQuery("TagHistoryItem");
                query.whereEqualTo("user", ParseUser.getCurrentUser());
                //query.whereEqualTo("inCloset", true);
                //query.whereEqualTo("visible", true);
                query.whereEqualTo("type", "top");
                query.orderByDescending("createdAt");
                //query.setLimit(SNAGS_PER_STORE);

                query.findInBackground(new FindCallback<TagHistoryItem>() {
                    @Override
                    public void done(List<TagHistoryItem> results, ParseException e) {
                        if (e != null) {
                            // There was an error
                        } else {
                            callback.onSuccess(results);
                        }
                    }
                });
//                for (int i = 0; i < SNAGS_PER_STORE; i++) {
//                    TagHistoryItem item = buildDummyTagHistoryItem("", i, context);
//                    mockItems.add(item);
//                }
                //callback.onSuccess(tops);
            }
        });
        t.start();
    }

    public void getBottoms(final Context context, final IParseCallback<List<TagHistoryItem>> callback) {
        final List<TagHistoryItem> bottoms = new ArrayList<TagHistoryItem>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                ParseQuery<TagHistoryItem> query = ParseQuery.getQuery("TagHistoryItem");
                query.whereEqualTo("user", ParseUser.getCurrentUser());
                //query.whereEqualTo("inCloset", true);
                //query.whereEqualTo("visible", true);
                String[] types = {"bottom", "top/bottom"};
                query.whereContainedIn("type", Arrays.asList(types));
                query.orderByDescending("createdAt");
                //query.setLimit(SNAGS_PER_STORE);
                query.findInBackground(new FindCallback<TagHistoryItem>() {
                    @Override
                    public void done(List<TagHistoryItem> results, ParseException e) {
                        if (e != null) {
                            // There was an error
                        } else {
                            callback.onSuccess(results);
                        }
                    }
                });
//                for (int i = 0; i < SNAGS_PER_STORE; i++) {
//                    TagHistoryItem item = buildDummyTagHistoryItem("", i, context);
//                    mockItems.add(item);
//                }

                //callback.onSuccess(bottoms);
            }
        });
        t.start();
    }


    public void getShoes(final Context context, final IParseCallback<List<TagHistoryItem>> callback) {
        final List<TagHistoryItem> shoes = new ArrayList<TagHistoryItem>();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                ParseQuery<TagHistoryItem> query = ParseQuery.getQuery("TagHistoryItem");
                query.whereEqualTo("user", ParseUser.getCurrentUser());
                //query.whereEqualTo("inCloset", true);
                //query.whereEqualTo("visible", true);
                query.whereEqualTo("type", "shoe");
                query.orderByDescending("createdAt");
                //query.setLimit(SNAGS_PER_STORE);
                query.findInBackground(new FindCallback<TagHistoryItem>() {
                    @Override
                    public void done(List<TagHistoryItem> results, ParseException e) {
                        if (e != null) {
                            // There was an error
                        } else {
                            callback.onSuccess(results);
                        }
                    }
                });

//                for (int i = 0; i < SNAGS_PER_STORE; i++) {
//                    TagHistoryItem item = buildDummyTagHistoryItem("", i, context);
//                    mockItems.add(item);
//                }
               // callback.onSuccess(shoes);

            }
        });
        t.start();
    }


    public void getClosetTops(final Context context, final IParseCallback<List<TagHistoryItem>> callback) {
        final List<TagHistoryItem> mockItems = new ArrayList<TagHistoryItem>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                ParseQuery<TagHistoryItem> query = ParseQuery.getQuery("TagHistoryItem");
                query.whereEqualTo("user", ParseUser.getCurrentUser());
                //query.whereEqualTo("inCloset", true);
                //query.whereEqualTo("visible", true);
                query.whereEqualTo("type", "top");
                query.orderByDescending("createdAt");
                //query.setLimit(SNAGS_PER_STORE);
                query.findInBackground(new FindCallback<TagHistoryItem>() {
                    @Override
                    public void done(List<TagHistoryItem> results, ParseException e) {
                        if (e != null) {
                            // There was an error
                        } else {
                            callback.onSuccess(results);
                        }
                    }
                });

//                for (int i = 0; i < SNAGS_PER_STORE; i++) {
//                    TagHistoryItem item = buildDummyTagHistoryItem("", i, context);
//                    mockItems.add(item);
//                }
//                callback.onSuccess(mockItems);
            }
        });
        t.start();
    }


    public void getClosetBottoms(final Context context, final IParseCallback<List<TagHistoryItem>> callback) {
        final List<TagHistoryItem> mockItems = new ArrayList<TagHistoryItem>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                ParseQuery<TagHistoryItem> query = ParseQuery.getQuery("TagHistoryItem");
                query.whereEqualTo("user", ParseUser.getCurrentUser());
                //query.whereEqualTo("inCloset", true);
                //query.whereEqualTo("visible", true);
                String[] types = {"bottom", "top/bottom"};
                query.whereContainedIn("type", Arrays.asList(types));
                query.orderByDescending("createdAt");
                //query.setLimit(SNAGS_PER_STORE);
                query.findInBackground(new FindCallback<TagHistoryItem>() {
                    @Override
                    public void done(List<TagHistoryItem> results, ParseException e) {
                        if (e != null) {
                            // There was an error
                        } else {
                            callback.onSuccess(results);
                        }
                    }
                });
//                for (int i = 0; i < SNAGS_PER_STORE; i++) {
//                    TagHistoryItem item = buildDummyTagHistoryItem("", i, context);
//                    mockItems.add(item);
//               }
//                callback.onSuccess(mockItems);
            }
        });
        t.start();
    }


    public void getClosetShoes(final Context context, final IParseCallback<List<TagHistoryItem>> callback) {
        final List<TagHistoryItem> mockItems = new ArrayList<TagHistoryItem>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                ParseQuery<TagHistoryItem> query = ParseQuery.getQuery("TagHistoryItem");
                query.whereEqualTo("user", ParseUser.getCurrentUser());
                //query.whereEqualTo("inCloset", true);
                //query.whereEqualTo("visible", true);
                query.whereEqualTo("type", "shoe");
                query.orderByDescending("createdAt");
                //query.setLimit(SNAGS_PER_STORE);
                query.findInBackground(new FindCallback<TagHistoryItem>() {
                    @Override
                    public void done(List<TagHistoryItem> results, ParseException e) {
                        if (e != null) {
                            // There was an error
                        } else {
                            callback.onSuccess(results);
                        }
                    }
                });
//                for (int i = 0; i < SNAGS_PER_STORE; i++) {
//                    TagHistoryItem item = buildDummyTagHistoryItem("", i, context);
//                    mockItems.add(item);
//                }
//                callback.onSuccess(mockItems);
            }
        });
        t.start();
    }

    public void getAllStores(final Context context, final IParseCallback<List<StoreItem>> callback) {
        final List<StoreItem> mockItems = new ArrayList<StoreItem>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < SNAGS_PER_STORE; i++) {
                    StoreItem item = buildDummyStoreItem(i, context);
                    mockItems.add(item);
                }

                callback.onSuccess(mockItems);
            }
        });
        t.start();
    }

    public void registerNewUser(final Context context, List<String> registerDetails) {
        final UserModel user = new UserModel();
        // username is set to email
        user.setUsername(registerDetails.get(0));
        user.setPassword(registerDetails.get(1));
        user.setEmail(registerDetails.get(2));
        user.setFirstName(registerDetails.get(3));
        user.setLastName(registerDetails.get(4));

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(
                            context,
                            "Registration Successful\nSending Confirmation Email",
                            Toast.LENGTH_SHORT).show();
                    // TODO: Email notification???
                    user.setEmail(user.getEmail());
                    // Hooray! Let them use the app now.
                    //Intent i = new Intent(context, ParseLoginDispatchActivity.class);
                    //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //context.getApplicationContext().startActivity(i);
                } else {
                    Toast.makeText(context, "Registration Failed: "+e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    Log.e(TAG, "Login failed: "+e.getMessage());
                }
            }
        });
    }

    public void saveNewOutfit(Context context, TagHistoryItem mTop, TagHistoryItem mBottom, TagHistoryItem mShoes,  final IParseCallback<OutfitItem> callback) {
        final OutfitItem newOutfit = new OutfitItem(mTop, mBottom, mShoes);
        newOutfit.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null) {
                    Log.i(TAG, "New outfit save failed with error: "+e.getMessage());
                } else {
                    Log.i(TAG, "New outfit saved!");
                    callback.onSuccess(newOutfit);
                }
            }
        });
    }

    private StoreItem buildDummyStoreItem(int i, Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();
        StoreItem item = new StoreItem();
        item.setImage(new ParseFile("store "+i, bitmapdata));
        item.setStoreDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc eu.");
        item.setStoreName(i + "Consectetur adipiscing elit");
        return item;

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

    /**
     * Register a stripe customer
     */

    public void registerStripeCustomer(Context context, String cardNumber, String expMonth, String expYear, String CVV) throws AuthenticationException {
        Log.i(TAG, "Registering stripe customer in the service");
        Card card = new Card(cardNumber, Integer.parseInt(expMonth), Integer.parseInt(expYear), CVV);
        card.validateNumber();
        card.validateExpMonth();
        card.validateExpYear();
        card.validateCVC();
        card.validateCard();
        Log.i(TAG, "Card validated");
        Stripe stripe = new Stripe(context.getString(R.string.stripe_card_sample));
        Log.i(TAG, "New stripe create");
        stripe.createToken(
                card,
                new TokenCallback() {
                    public void onSuccess(Token token) {
                        Log.i(TAG, "token created on callback");
                        // Send token to your server
                        HashMap body = new HashMap<String, Object>();
                        body.put("stripeToken",token.getId());
                        String email = ParseUser.getCurrentUser().getString("email");
                        //String email = "edeleon4@mit.edu";
                        body.put("descriptionEmail", email );
                        ParseCloud.callFunctionInBackground("createCustomer", body , new FunctionCallback<Object>() {
                            @Override
                            public void done(Object result, ParseException e) {
                                if (e == null) {
                                    // result is "Hello world!"
                                    Log.i(TAG, "parse cloud code ran successfully");
                                    //Log.i(TAG, result);
                                }
                            }
                        });
                    }
                    public void onError(Exception error) {
                        // Show localized error message
                        Log.i(TAG, error.getMessage().toString());
                    }
                }
        );

    }
}