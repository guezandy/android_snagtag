package com.snagtag;


import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.snagtag.adapter.HomeFragmentAdapter;
import com.snagtag.adapter.LaunchFragmentAdapter;
import com.snagtag.fragment.HomeFragment;
import com.snagtag.fragment.LaunchFragment;
import com.snagtag.util.SystemUiHider;
import com.viewpagerindicator.LinePageIndicator;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class LaunchActivity extends ActionBarActivity {
    public String TAG = LaunchActivity.class.getSimpleName();

    LaunchFragmentAdapter mAdapter;
    ViewPager mPager;
    LinePageIndicator mIndicator;
    Button login;
    Button signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launch);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.container);

        android.support.v7.app.ActionBar actionbar = getSupportActionBar();
        actionbar.hide();

        login = (Button) findViewById(R.id.login);
        signup = (Button) findViewById(R.id.signup);

        Boolean debug = true;
        if(debug) {
            login.callOnClick();
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LaunchActivity.this, ParseLoginDispatchActivity.class);
                startActivity(i);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LaunchActivity.this, RegisterNewAccountActivity.class);
                startActivity(i);
            }
        });

        ParseUser currentUser = ParseUser.getCurrentUser();
        if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
            // Go to the user info activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        replaceFragment(new LaunchFragment(), true, FragmentTransaction.TRANSIT_FRAGMENT_FADE, getString(R.string.title_section_terms));
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "On Resume");
        replaceFragment(new LaunchFragment(), true, FragmentTransaction.TRANSIT_FRAGMENT_FADE, getString(R.string.title_section_terms));
    }
    /**
     * Handles adding all fragments to the view.
     * @param newFragment The fragment to add.
     * @param addToBackstack Whether this Fragment should appear in the backstack or not.
     * @param transition The transition animation to apply
     * @param backstackName The name
     */
    public void replaceFragment(android.support.v4.app.Fragment newFragment, boolean addToBackstack, int transition, String backstackName) {
        // use fragmentTransaction to replace the fragment
        Log.i(TAG, "Initializing Fragment Transaction");
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Log.i(TAG, "Replacing the fragment and calling backstack");
        fragmentTransaction.replace(R.id.container, newFragment, backstackName);
        if (addToBackstack) {
            fragmentTransaction.addToBackStack(backstackName);
        }
        Log.i(TAG, "setting the transition");
        fragmentTransaction.setTransition(transition);
        Log.i(TAG,"Commiting Transaction");
        fragmentTransaction.commit();
    }

}
