package com.snagtag.fragment;

import android.app.Activity;
import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.SaveCallback;
import com.snagtag.R;
import com.snagtag.models.CartItem;
import com.snagtag.models.TagHistoryItem;
import com.snagtag.service.IParseCallback;
import com.snagtag.service.IParseService;
import com.snagtag.service.MockParseService;

import java.util.Collections;
import java.util.List;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class TagsDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_TAGS = "tags_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private TagsDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;

    private View mFragmentContainerView;

    private View mContainer;
    private LinearLayout mStoreLayout;
    private IParseService mParseService;


    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    public TagsDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParseService = new MockParseService(getActivity().getApplicationContext());

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_TAGS, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContainer = inflater.inflate(R.layout.fragment_tags_drawer, container);
        //tagHistoryAdapter = new TagHistoryAdapter(getActivity());
        mStoreLayout = (LinearLayout) mContainer.findViewById(R.id.store_layout);
        mParseService.getStoresByTags(getActivity().getApplicationContext(), new IParseCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> items) {

                for (String store : items) {
                    final View storeView = inflater.inflate(R.layout.row_item_snag_view, null);
                    final ViewFlipper flipper = (ViewFlipper) storeView.findViewById(R.id.grid_flipper);
                    TextView title = (TextView) storeView.findViewById(R.id.title_store_name);
                    title.setText(store);
                    final View header = storeView.findViewById(R.id.store_header);
                    final View openIndicator = storeView.findViewById(R.id.store_open_indicator);
                    final String storeName = store;
                    header.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final GridView itemGrid = (GridView) storeView.findViewById(R.id.item_grid);
                            if (itemGrid.getVisibility() == View.VISIBLE) {
                                flipper.setVisibility(View.GONE);
                                flipper.setDisplayedChild(0);
                                openIndicator.setRotation(0);
                            } else {
                                flipper.setVisibility(View.VISIBLE);
                                openIndicator.setRotation(180);
                                //Adding the storeName as a parameter for parse query purposes;
                               /* itemGrid.setAdapter(mParseService.TagHistoryAdapter(getActivity().getApplicationContext(), storeName, new DataSetObserver() {
                                    @Override
                                    public void onChanged() {
                                        if (itemGrid.getCount() > 0) {

                                        }
                                    }
                                }));*/

                                final TagItemArrayAdapter gridAdapter = new TagItemArrayAdapter(getActivity().getApplicationContext(), R.layout.row_item_snag_view);

                                itemGrid.setAdapter(gridAdapter);
                                new MockParseService(getActivity().getApplicationContext()).getTagHistory(getActivity().getApplicationContext(), storeName, new IParseCallback<List<TagHistoryItem>>() {
                                    @Override
                                    public void onSuccess(List<TagHistoryItem> items) {
                                        gridAdapter.setItems(items);
                                        flipper.setDisplayedChild(1);
                                    }

                                    @Override
                                    public void onFail(String message) {

                                    }
                                });
                            }
                        }
                    });
                    mStoreLayout.addView(storeView);
                }
            }

            @Override
            public void onFail(String message) {
                Log.d("TAGS_DRAWER", "Parse failed " + message);
            }
        });


        return mContainer;
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
                    sp.edit().putBoolean(PREF_USER_LEARNED_TAGS, true).apply();
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
        //if (mDrawerListView != null) {
        //    mDrawerListView.setItemChecked(position, true);
        //}
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onTagItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (TagsDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement TagsDrawerCallbacks.");
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
        // }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.action_tags) {
            Toast.makeText(getActivity(), "Tags action.", Toast.LENGTH_SHORT).show();
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
    public static interface TagsDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onTagItemSelected(int position);
    }

    class TagItemArrayAdapter extends ArrayAdapter<TagHistoryItem> {
        ArrayAdapter<TagHistoryItem> me = this;
        List<TagHistoryItem> items = Collections.emptyList();
        Context context;

        TagItemArrayAdapter(Context context, int view) {
            super(context, view);
            this.context = context;
        }

        public void setItems(List<TagHistoryItem> items) {
            this.items = items;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            final TagHistoryItem item = items.get(position);
            if (v == null) {
                v = View.inflate(context, R.layout.row_item_clothing_view, null);
            }
            TextView description = (TextView) v.findViewById(R.id.item_description);
            description.setText("");

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
                        Log.i("TAG", "Image uploaded: " + item.getStore() + " , " + item.getDescription());
                    }
                });
            }

            TextView color = (TextView) v.findViewById(R.id.item_color);
            color.setText(String.format(getActivity().getApplicationContext().getResources().getString(R.string.item_color), item.getString("color")));

            TextView size = (TextView) v.findViewById(R.id.item_size);
            size.setText(String.format(getActivity().getApplicationContext().getResources().getString(R.string.item_size), item.getString("size")));

            TextView cost = (TextView) v.findViewById(R.id.item_cost);
            cost.setText(String.format(getActivity().getApplicationContext().getResources().getString(R.string.item_cost), item.getPrice()));
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
    }

    ;
}
