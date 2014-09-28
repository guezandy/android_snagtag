package com.snagtag;

import android.app.Application;
import com.snagtag.models.OutfitItem;
import com.snagtag.models.TagHistoryItem;
import com.snagtag.models.ClothingItem;

import  com.snagtag.R;
import com.parse.Parse;
import com.parse.ParseACL;
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
         * Registering Parse Subclasses
         *
         */
        ParseObject.registerSubclass(ClothingItem.class);
        ParseObject.registerSubclass(OutfitItem.class);
        ParseObject.registerSubclass(TagHistoryItem.class);

        /*
            Initialize the ability to store data locally
         */
        //Parse.enableLocalDatastore();
        /*
         * Parse credentials and initialize
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
