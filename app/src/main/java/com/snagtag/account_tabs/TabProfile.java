package com.snagtag.account_tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseUser;
import com.snagtag.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author mwho
 *
 */
public class TabProfile extends Fragment {

    public String TAG = TabProfile.class.getSimpleName();
    /** (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        View mView =  (LinearLayout)inflater.inflate(R.layout.tab_account_profile, container, false);

        EditText name = (EditText) mView.findViewById(R.id.account_profile_name);
        EditText email = (EditText) mView.findViewById(R.id.account_profile_email);
        EditText password = (EditText) mView.findViewById(R.id.account_profile_password);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser.has("profile")) {
            JSONObject userProfile = currentUser.getJSONObject("profile");
            try {
                if (userProfile.has("facebookId")) {
                    //userProfilePictureView.setProfileId(userProfile.getString("facebookId"));
                } else {
                    // Show the default, blank user profile picture
                    //userProfilePictureView.setProfileId(null);
                }

                if (userProfile.has("first_name")) {
                    name.setHint(userProfile.getString("first_name"));
                } else {
                    name.setHint("");
                }

                email.setHint("Facebook Login");
                password.setHint("Facebook Auth");
            } catch (JSONException e) {
                Log.d(TAG, "Error parsing saved user data.");
            }
        } else {
            if(currentUser.getString("username") != null) {
                name.setHint(currentUser.getString("username"));
            } else {
                name.setHint("SnagTag user");
            }
            if(currentUser.getString("email") != null) {
                email.setHint(currentUser.getString("email"));
            } else {
                email.setHint("");
            }
            if(currentUser.getString("password") != null) {
                password.setHint("*******");
            } else {
                password.setHint("*******");
            }
        }

        return mView;
    }
}