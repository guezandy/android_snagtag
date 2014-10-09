package com.snagtag;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import static com.snagtag.utils.Constant.*;


import com.snagtag.fragment.AccountFragment;
import com.snagtag.fragment.CartFragment;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.parse.ParseFacebookUtils;

import com.snagtag.fragment.ClosetFragment;
import com.snagtag.fragment.CreatorFragment;
import com.snagtag.fragment.NavigationDrawerFragment;
import com.snagtag.fragment.SingleItemFragment;
import com.snagtag.fragment.StoreFragment;
import com.snagtag.fragment.TagsDrawerFragment;
import com.snagtag.fragment.TermsFragment;
import com.snagtag.interfaces.NFCHandler;
import com.snagtag.utils.NfcUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, TagsDrawerFragment.TagsDrawerCallbacks, NFCHandler {
    /**
     * Tag to help in debugging
     */
    private final String TAG = MainActivity.class.getSimpleName();

    /**
     * The drawer layout
     */
    private DrawerLayout mDrawer;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Tags Drawer Fragment
     */
    private TagsDrawerFragment mTagsDrawerFragment;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    /**
     * NfcAdapter android class used to communicate with nfc on phone
     */
    private NfcAdapter mNfcAdapter;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        mTagsDrawerFragment = (TagsDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.tags_drawer);

        mDrawer = (DrawerLayout)findViewById(R.id.drawer_layout);

        mTagsDrawerFragment.setUp(
                R.id.tags_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {// no nfc on phone
            Toast.makeText(this, getString(R.string.dialog_no_nfc),
                    Toast.LENGTH_SHORT).show();
            finish(); //do we want to close the app?
        }

        // Fetch Facebook user info if the session is active
        Session session = ParseFacebookUtils.getSession();
        if (session != null && session.isOpened()) {
            makeMeRequest();
        }
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();

        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(getIntent().getAction()) ||
                NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            final String nfcid = processReadIntent(getIntent());
            Log.i(TAG, "Got nfc id "+nfcid);
            Fragment Snag = new SingleItemFragment();
            ((SingleItemFragment) Snag).setItemId(nfcid);
            replaceFragment(Snag, true, FragmentTransaction.TRANSIT_FRAGMENT_FADE, getString(R.string.title_section_tags));
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments

        switch(position) {
            case CART:
                getSupportFragmentManager().popBackStackImmediate();
                replaceFragment(new CartFragment(), true, FragmentTransaction.TRANSIT_FRAGMENT_FADE, getString(R.string.title_section_cart));
                break;
            case OUTFIT_CREATOR:
                replaceFragment(new CreatorFragment(), true, FragmentTransaction.TRANSIT_FRAGMENT_FADE, getString(R.string.title_section_outfits));
                break;
            case CLOSET:
                replaceFragment(new ClosetFragment(), true, FragmentTransaction.TRANSIT_FRAGMENT_FADE, getString(R.string.title_section_closet));
                break;
            case STORES:
                replaceFragment(new StoreFragment(), true, FragmentTransaction.TRANSIT_FRAGMENT_FADE, getString(R.string.title_section_stores));
                break;
            case ACCOUNT:
                replaceFragment(new AccountFragment(), true, FragmentTransaction.TRANSIT_FRAGMENT_FADE, getString(R.string.title_section_account));
                break;
            case TERMS:
                replaceFragment(new TermsFragment(), true, FragmentTransaction.TRANSIT_FRAGMENT_FADE, getString(R.string.title_section_terms));
                break;
            default:
                break;

        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

            getMenuInflater().inflate(R.menu.global, menu);
            restoreActionBar();
            return true;

        //return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_tags) {
            if(mTagsDrawerFragment.isDrawerOpen()) {
                mDrawer.closeDrawer(mTagsDrawerFragment.getView());
            } else {
                mDrawer.openDrawer(mTagsDrawerFragment.getView());
                if(mNavigationDrawerFragment.isDrawerOpen()) {
                    mDrawer.closeDrawer(mNavigationDrawerFragment.getView());
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onTagItemSelected(int position) {

    }

    @Override
    public void onBackPressed() {
        if(mNavigationDrawerFragment.isDrawerOpen()) {
            mDrawer.closeDrawer(mNavigationDrawerFragment.getView());
            return;
        }

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            // set title
            alertDialogBuilder.setTitle(getString(R.string.title_snagtag));

            // set dialog message
            alertDialogBuilder.setMessage(R.string.dialog_exit).setCancelable(false).setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // if this button is clicked, close
                    // current activity
                    MainActivity.super.onBackPressed();
                }
            }).setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // if this button is clicked, just close
                    // the dialog box and do nothing
                    dialog.cancel();
                }
            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        } else {
            super.onBackPressed();
        }
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

    public String processReadIntent(Intent intent) {
        Log.i(TAG, "Reading NFC tag");
        List<NdefMessage> intentMessages = NfcUtils
                .getMessagesFromIntent(intent);
        List<String> payloadStrings = new ArrayList<String>(
                intentMessages.size());

        for (NdefMessage message : intentMessages) {
            for (NdefRecord record : message.getRecords()) {
                byte[] payload = record.getPayload();
                String payloadString = new String(payload);

                if (!TextUtils.isEmpty(payloadString))
                    payloadStrings.add(payloadString);
            }
        }

        if (!payloadStrings.isEmpty()) {
            String content = TextUtils.join(",", payloadStrings);
            // printing for testing
            Log.i(TAG, content);
            return content;
        }
        return null;
    }

    private void makeMeRequest() {
        Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
                new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        if (user != null) {
                            // Create a JSON object to hold the profile info
                            JSONObject userProfile = new JSONObject();
                            try {
                                // Populate the JSON object
                                userProfile.put("facebookId", user.getId());
                                userProfile.put("name", user.getName());
                                if (user.getLocation().getProperty("name") != null) {
                                    userProfile.put("location", (String) user
                                            .getLocation().getProperty("name"));
                                }
                                if (user.getProperty("gender") != null) {
                                    userProfile.put("gender",
                                            (String) user.getProperty("gender"));
                                }
                                if (user.getBirthday() != null) {
                                    userProfile.put("birthday",
                                            user.getBirthday());
                                }
                                if (user.getProperty("relationship_status") != null) {
                                    userProfile
                                            .put("relationship_status",
                                                    (String) user
                                                            .getProperty("relationship_status"));
                                }

                            } catch (JSONException e) {
                                Log.d(TAG,
                                        "Error parsing returned user data.");
                            }

                        } else if (response.getError() != null) {
                            // handle error
                        }
                    }
                });
        request.executeAsync();

    }
}