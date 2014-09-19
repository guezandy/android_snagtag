package com.snagtag.utils;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.snagtag.MainActivity;

/**
 * Used to help with Fragment management
 *
 * Created by benjamin on 9/19/14.
 */
public class FragmentUtil {

    private FragmentUtil() {
        //never instantiate.
    }

    /**
     * Helper method to simplify calls to replace Fragments from within the app.  Handles the casting of the Activity to a MainActivity
     * @param activity The activity, should be an instance of MainActivity.  If not, this method is a no-op.
     * @param newFragment The new Fragment to add.
     * @param addToBackstack Whether to add this fragment to the backstack or not.
     * @param transition The transition animation.
     * @param backstackName The identifier for this Fragment on the backstack.
     */
    public static final void replaceFragment(Activity activity, Fragment newFragment, boolean addToBackstack, int transition, String backstackName) {
        if(activity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) activity;
            ((MainActivity) activity).replaceFragment(newFragment, addToBackstack, transition, backstackName);
        }
    }
}
