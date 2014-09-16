package com.example.owner.snagtag_android;

import android.app.Application;
import com.example.owner.snagtag_android.models.CartItem;
import com.example.owner.snagtag_android.models.TagHistoryItem;
import com.example.owner.snagtag_android.models.ClothingItem;

import  com.example.owner.snagtag_android.R;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;
import android.util.Log;

/**
 * Created by Owner on 9/16/2014.
 */


public class SnagtagApplication extends Application {
    private final static String TAG = SnagtagApplication.class.getSimpleName();
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"onCreate");

        /*
         * In this tutorial, we'll subclass ParseObject for convenience to
         * create and modify Meal objects
         */
        ParseObject.registerSubclass(ClothingItem.class);
        ParseObject.registerSubclass(CartItem.class);
        ParseObject.registerSubclass(TagHistoryItem.class);


        /*
         * Fill in this section with your Parse credentials
         */
        Parse.initialize(this, "cuLGNujAgxROlSMPh1FF58asDN8aGc4LCDcsOpk2", "aHQ9A0SjhZTw1r64dQ51H8EDWOp8PREmRacwHO9Y");
        //ParseFacebookUtils.initialize(getString(R.string.facebook_id));

        /*
         * This app lets an anonymous user create and save photos of meals
         * they've eaten. An anonymous user is a user that can be created
         * without a username and password but still has all of the same
         * capabilities as any other ParseUser.
         *
         * After logging out, an anonymous user is abandoned, and its data is no
         * longer accessible. In your own app, you can convert anonymous users
         * to regular users so that data persists.
         *
         * Learn more about the ParseUser class:
         * https://www.parse.com/docs/android_guide#users
         */
        ParseUser.enableAutomaticUser();

        /*
         * For more information on app security and Parse ACL:
         * https://www.parse.com/docs/android_guide#security-recommendations
         */
        ParseACL defaultACL = new ParseACL();

        /*
         * If you would like all objects to be private by default, remove this
         * line
         */
        defaultACL.setPublicReadAccess(true);

        ParseACL.setDefaultACL(defaultACL, true);
    }

}
