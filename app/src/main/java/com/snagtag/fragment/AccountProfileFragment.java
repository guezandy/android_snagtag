package com.snagtag.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.FacebookRequestError;
import com.facebook.LoginActivity;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by benjamin on 10/7/14.
 */
public class AccountProfileFragment extends Fragment {
    private final static String TAG = AccountFragment.class.getSimpleName();
    private View mView;
    private ProfilePictureView userProfilePictureView;
    private TextView userNameView;
    private ListView accountListView;
    private ListView listview;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_account_profile, container, false);


        userProfilePictureView = (ProfilePictureView) mView.findViewById(R.id.userProfilePicture);
        userNameView = (TextView) mView.findViewById(R.id.userName);
        accountListView = (ListView) mView.findViewById(R.id.account);
        listview = (ListView) mView.findViewById(R.id.listView);

        // Fetch Facebook user info if the session is active
        Session session = ParseFacebookUtils.getSession();
        if (session != null && session.isOpened()) {
            makeMeRequest();
        }

        /**
         * Account Information ListView Setup
         */
        String[] accountValues = new String[] {"Profile Settings", "Purchase History", "Share Settings"};
        final ArrayList<String> accountList = new ArrayList<String>();
        for (int i = 0; i < accountValues.length; ++i) {
            accountList.add(accountValues[i]);
        }
        final StableArrayAdapter accountAdapter = new StableArrayAdapter(getActivity(),
                android.R.layout.simple_list_item_1, accountList);
        accountListView.setAdapter(accountAdapter);
        accountListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);

                switch(position) {
                    case 0:
                        Log.i(TAG, "Account: Selected: "+ (String) parent.getItemAtPosition(position) +" at position: "+position);
                        break;
                    case 1:
                        Log.i(TAG, "Account Selected: "+ (String) parent.getItemAtPosition(position) +" at position: "+position);
                        break;
                    case 2:
                        Log.i(TAG, "Account Selected: "+ (String) parent.getItemAtPosition(position) +" at position: "+position);
                        break;
                    default:
                        Log.i(TAG, "We in default");
                        break;
                }
                // Create new fragment and transaction
                Fragment newFragment = new ViewSnagFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.container, newFragment);
                transaction.addToBackStack(null);
                // Commit the transaction
                transaction.commit();
            }
        });

        String[] values = new String[] { "View Snags", "View Outfits", "View Likes"};
        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < values.length; ++i) {
            list.add(values[i]);
        }
        final StableArrayAdapter adapter = new StableArrayAdapter(getActivity(),
                android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                Fragment newFragment = null;
                switch(position) {
                    case 0:
                        Log.i(TAG, "Selected: "+ (String) parent.getItemAtPosition(position) +" at position: "+position);
                        newFragment = new ViewSnagFragment();
                        break;
                    case 1:
                        Log.i(TAG, "Selected: "+ (String) parent.getItemAtPosition(position) +" at position: "+position);
                        newFragment = new ViewOutfitFragment();
                        break;
                    case 2:
                        Log.i(TAG, "Selected: "+ (String) parent.getItemAtPosition(position) +" at position: "+position);
                        break;
                    default:
                        Log.i(TAG, "We in default");
                        break;
                }
                // Create new fragment and transaction
                if(newFragment == null) {
                    newFragment = new AccountFragment();
                }
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.container, newFragment);
                transaction.addToBackStack(null);
                // Commit the transaction
                transaction.commit();
            }
        });
        return mView;
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
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

                if (userProfile.has("name")) {
                    userNameView.setText(userProfile.getString("name"));
                } else {
                    userNameView.setText("");
                }
/*
                if (userProfile.has("gender")) {
                    userGenderView.setText(userProfile.getString("gender"));
                } else {
                    userGenderView.setText("");
                }

                if (userProfile.has("email")) {
                    userEmailView.setText(userProfile.getString("email"));
                } else {
                    userEmailView.setText("");
                }
*/
            } catch (JSONException e) {
                Log.d(TAG, "Error parsing saved user data.");
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
