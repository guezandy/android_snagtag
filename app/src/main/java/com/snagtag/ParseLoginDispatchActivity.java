package com.snagtag;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;


/**
 * Placeholder for ParseLoginActivity.  Will we be using this or rolling our own?
 */
public class ParseLoginDispatchActivity extends Activity {

    private final String TAG = ParseLoginDispatchActivity.class.getSimpleName();
    private Button fbLoginButton;
    private Button loginButton;
    private View mBack;
    private TextView registerButton;
    private Dialog progressDialog;

    private EditText username;
    private EditText password;
    private String mUsername;
    private String mPassword;
    private TextView mErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (EditText) findViewById(R.id .loginUsername);
        password = (EditText) findViewById(R.id.loginPassword);
        mErrorMessage = (TextView) findViewById(R.id.errorMessage);

        // Fetch Facebook user info if the session is active
        fbLoginButton = (Button) findViewById(R.id.fbLoginButton);
        fbLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFBLoginButtonClicked();
            }
        });

        mBack = (View) findViewById(R.id.arrow_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ParseLoginDispatchActivity.this, LaunchActivity.class);
                startActivity(i);
            }
        });

        registerButton = (TextView) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                Intent i = new Intent(ParseLoginDispatchActivity.this,
                        RegisterNewAccountActivity.class);
                startActivity(i);
            }
        });
        registerButton.setVisibility(View.GONE);

        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                onLoginButtonClicked();
            }
        });

        // Check if there is a currently logged in user
        // and they are linked to a Facebook account.
        ParseUser currentUser = ParseUser.getCurrentUser();
        if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
            // Go to the user info activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        //TODO: remove this debugging code set to true to skip login
        boolean debugMode = false;
        if (debugMode) {
            username.setText("demo");
            password.setText("demo");
            loginButton.callOnClick();
        }
    }
//TODO: fix login failed

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }

    /**
     * Handles regular Login
     */
    private void onLoginButtonClicked() {
        Log.i(TAG, "onLoginButtonClicked");
        ParseLoginDispatchActivity.this.progressDialog = ProgressDialog.show(
                ParseLoginDispatchActivity.this, "", "Logging in...", true);
        if (validateFields()) {
            mUsername = username.getText().toString();
            mPassword = password.getText().toString();
            if (TextUtils.isEmpty(mUsername) || TextUtils.isEmpty(mPassword)) {
                mErrorMessage
                        .setText("Please enter a valid username and password.");
                this.progressDialog.dismiss();
            } else {
                // showProgress();
                userLogin();
            }
        } else {
            this.progressDialog.dismiss();
            Toast.makeText(getApplicationContext(),
                    "Please Insert valid Username and Password",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // TODO: set the limits on sizes of username length and password length and more login rules
    private boolean validateFields() {
        Log.i(TAG, "validateFields");
        if (username.length() > 0 && password.getText().length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Handles parse user login
     */
    public void userLogin() {
        Log.i(TAG, "userLogin");
        ParseUser.logInInBackground(mUsername, mPassword, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    //TODO: Change to getString("username");
                    Toast.makeText(getApplicationContext(),
                            "Welcome :, " + user.getString("username"),
                            Toast.LENGTH_SHORT).show();

                    SnagtagApplication.setUserId(user.getObjectId());
                    SnagtagApplication.setOutfitCount(user.getInt("outfit_count"));
                    Log.i(TAG, "Outfit count: "+user.getNumber("outfit_count"));
                    Intent i = new Intent(ParseLoginDispatchActivity.this,
                            MainActivity.class);
                    startActivity(i);
                } else {
                    // Signup failed. Look at the ParseException to see what
                    // happened.
                    Log.e(TAG,"Login failed: " +e.getMessage());
                    Toast.makeText(getApplicationContext(),
                            "Login failed please try again", Toast.LENGTH_SHORT)
                            .show();
                    ParseLoginDispatchActivity.this.progressDialog.dismiss();
                }
            }
        });
    }

    /**
     * Handles facebook login request.
     */
    private void onFBLoginButtonClicked() {
            ParseLoginDispatchActivity.this.progressDialog = ProgressDialog.show(
                    ParseLoginDispatchActivity.this, "", "Logging in...", true);
            List<String> permissions = Arrays.asList("public_profile");
            ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException err) {
                    ParseLoginDispatchActivity.this.progressDialog.dismiss();
                    if (user == null) {
                        Log.d(TAG,
                                "Uh oh. The user cancelled the Facebook login.");
                    } else if (user.isNew()) {
                        Log.d(TAG,
                                "user signed up and logged in through Facebook!");
                        if(user.getNumber("outfit_count") == null) {
                            user.put("outfit_count",0);
                        }
                        startMainActivity();
                    } else {
                        Log.d(TAG,
                                "user logged in through Facebook!");
                        if(user.getNumber("outfit_count") == null) {
                            user.put("outfit_count",0);
                        }
                        startMainActivity();
                    }
                }
            });
        }




    /**
     * Starts the Main Activity.
     */
    public void startMainActivity() {
        Log.i(TAG, "starting Main Activity");
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //@Override
    protected Class<?> getTargetClass() {
        return MainActivity.class;
    }
}