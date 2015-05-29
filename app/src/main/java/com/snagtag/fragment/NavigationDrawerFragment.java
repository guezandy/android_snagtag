package com.snagtag.fragment;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.snagtag.ParseLoginDispatchActivity;
import com.snagtag.R;

import org.json.JSONException;
import org.json.JSONObject;

import static com.snagtag.utils.Constant.ACCOUNT;
//import static com.snagtag.utils.Constant.CART;
import static com.snagtag.utils.Constant.CLOSET;
//import static com.snagtag.utils.Constant.FAVORITES;
//import static com.snagtag.utils.Constant.COLOR_WHEEL;
import static com.snagtag.utils.Constant.HOME;
import static com.snagtag.utils.Constant.LOGOUT;
import static com.snagtag.utils.Constant.OUTFITS;
//import static com.snagtag.utils.Constant.OUTFIT_CREATOR;
//import static com.snagtag.utils.Constant.SNAGS;
//import static com.snagtag.utils.Constant.STORES;
import static com.snagtag.utils.Constant.TERMS;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {
    private final static String TAG = NavigationDrawerFragment.class.getSimpleName();


    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = HOME;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    private ProfilePictureView userProfilePictureView;
    private TextView userNameView;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View mView = (View)inflater.inflate(R.layout.fragment_navigation_drawer,container,false);
        userProfilePictureView = (ProfilePictureView) mView.findViewById(R.id.userProfilePicture);
        userNameView = (TextView) mView.findViewById(R.id.user_name);
        mDrawerListView = (ListView) mView.findViewById(R.id.navigation_list);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        // Fetch Facebook user info if the session is active
        Session session = ParseFacebookUtils.getSession();
        if (session != null && session.isOpened()) {
            makeMeRequest();
        } else {
            userNameView.setText(ParseUser.getCurrentUser().getUsername());
        }
        mDrawerListView.setAdapter(new ArrayAdapter<String>(
                getActionBar().getThemedContext(),
                android.R.layout.simple_list_item_activated_1,

                new String[]{
                        getString(R.string.title_section_home),
                        getString(R.string.title_section_viewoutfits),
                        getString(R.string.title_section_closet),
                        /*"Color Wheel (Coming Soon)",*/
                        /*getString(R.string.title_section_stores), //TODO: V2 */
                        getString(R.string.title_section_account),
                        getString(R.string.title_section_terms),
                        "Logout"
                }){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = inflater.inflate(R.layout.navigation_item, null);
                ((TextView)view.findViewById(R.id.title_navigation)).setText(getItem(position));
                ImageView icon = ((ImageView)view.findViewById(R.id.icon_navigation));
                switch(position) {
                    case HOME:
                        icon.setImageResource(R.drawable.home);
                        break;
                    case OUTFITS:
                        icon.setImageResource(R.drawable.outfits);
                        break;
//                    case OUTFIT_CREATOR:
//                        icon.setImageResource(R.drawable.outfit);
//                        break;
                    case CLOSET:
                        icon.setImageResource(R.drawable.closet);
                        break;
//                    case COLOR_WHEEL:
//                        icon.setImageResource(R.drawable.closet);
//                        break;
 /*                   case STORES:
                        icon.setImageResource(R.drawable.store);
                        break; */
                    case ACCOUNT:
                        icon.setImageResource(R.drawable.account);
                        break;
                    case TERMS:
                        icon.setImageResource(R.drawable.terms);
                        break;
                    case LOGOUT:
                        icon.setImageResource(R.drawable.logout);
                        break;
                    default:
                        break;
                }
                return view;
            }
        });
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        return mView;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        //if (mDrawerLayout != null && isDrawerOpen()) {
        //    inflater.inflate(R.menu.global, menu);
        //    showGlobalContextActionBar();
        //}
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.action_tags) {
            Toast.makeText(getActivity(), "Navigation action.", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
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
                                userProfile.put("first_name", user.getName());
                                if (user.getProperty("gender") != null) {
                                    userProfile.put("gender", user.getProperty("gender"));
                                }
                                if (user.getProperty("email") != null) {
                                    userProfile.put("email", user.getProperty("email"));
                                }

                                // Save the user profile info in a user property
                                ParseUser currentUser = ParseUser.getCurrentUser();
                                currentUser.put("profile", userProfile);
                                currentUser.saveInBackground();

                                // Show the user info
                                updateViewsWithProfileInfo();
                            } catch (JSONException e) {
                                Log.d(TAG, "Error parsing returned user data. " + e);
                            }

                        } else if (response.getError() != null) {
                            if ((response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_RETRY) ||
                                    (response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_REOPEN_SESSION)) {
                                Log.d(TAG, "The facebook session was invalidated." + response.getError());
                                logout();
                            } else {
                                Log.d(TAG,
                                        "Some other error: " + response.getError());
                            }
                        }
                    }
                }
        );
        request.executeAsync();
    }

    private void updateViewsWithProfileInfo() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser.has("profile")) {
            JSONObject userProfile = currentUser.getJSONObject("profile");
            try {

                if (userProfile.has("facebookId")) {
                    userProfilePictureView.setProfileId(userProfile.getString("facebookId"));
                } else {
                    // Show the default, blank user profile picture
                    userProfilePictureView.setProfileId(null);
                }

                if (userProfile.has("first_name")) {
                    userNameView.setText(userProfile.getString("first_name"));
                } else {
                    userNameView.setText("");
                }
            } catch (JSONException e) {
                Log.d(TAG, "Error parsing saved user data.");
            }
        } else {
            if(currentUser.getString("username") != null) {
                userNameView.setText(currentUser.getString("username"));
            } else {
                userNameView.setText("SnagTag user");
            }
        }
    }

    private void logout() {
        // Log the user out
        ParseUser.logOut();

        // Go to the login view
        startLoginActivity();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(getActivity(), ParseLoginDispatchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
