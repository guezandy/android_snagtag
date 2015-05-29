package com.snagtag;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.snagtag.adapter.CartItemAdapter;
import com.snagtag.models.CartItem;
import com.snagtag.service.IParseCallback;
import com.snagtag.service.ParseService;
import com.stripe.exception.AuthenticationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Activity for registering new accounts. This prompts a user for a user name
 * (email address), First and Last name, and a password. After validation, it
 * creates a new ParseUser and redirects the user back to the login page for
 * Authentication.
 */
public class RegisterNewAccountActivity extends Activity {
    private final String TAG = RegisterNewAccountActivity.class.getSimpleName();

    private Dialog progressDialog;
    private ParseService mParseService;
    private ViewFlipper mViewFlipper;

    /*Navigation bar params*/
    private View mArrowBack;
    protected TextView mSkipButton;

    /*Steps bar */
    private TextView mOneCircle;
    private TextView mTwoCircle;
    private TextView mThreeCircle;
    private TextView mAboutHeader;
    private TextView mShippingHeader;
    private TextView mBillingHeader;

    /*About you fields*/
    protected EditText mEditUsername;
    protected EditText mEditEmailAddress;
    protected EditText mEditPassword;
    protected EditText mEditPasswordConfirm;
    protected Button mRegisterButton;
    private Button mFbLoginButton;

    //Shipping fields
    private EditText shippingName;
    private EditText shippingAddress;
    private EditText shippingCity;
    private EditText shippingZipcode;
    private Spinner states;
    private String state;
    private Button nextInShipping;

    //Card fields
    private EditText mCardName;
    private EditText mCardNumber;
    private Button VerifyStripe;
    String mMonth;
    String mYear;

    private static final int ABOUT = 0;
    private static final int SHIPPING = 1;
    private static final int BILLING = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        mParseService = new ParseService(this);

        setContentView(R.layout.activity_register_steps);
        mViewFlipper = (ViewFlipper) findViewById(R.id.register_view_flipper);

        /*Top Nav */
        mOneCircle = (TextView) findViewById(R.id.one);
        mAboutHeader = (TextView) findViewById(R.id.about_header);
        mTwoCircle = (TextView) findViewById(R.id.two);
        mShippingHeader = (TextView) findViewById(R.id.shipping_header);
        mThreeCircle = (TextView) findViewById(R.id.three);
        mBillingHeader = (TextView) findViewById(R.id.billing_header);

        /*Bottom Nav */
        mArrowBack = findViewById(R.id.arrow_back);
        mSkipButton = (TextView) findViewById(R.id.skip_button);
        //Skips starts invisible cant skip the first step
        mSkipButton.setVisibility(View.GONE);

        /**
         * About you
         */
        mEditUsername = (EditText) findViewById(R.id.username);
        mEditEmailAddress = (EditText) findViewById(R.id.user_email);
        mEditPassword = (EditText) findViewById(R.id.password);
        mEditPasswordConfirm = (EditText) findViewById(R.id.confirm_pass);
        mRegisterButton = (Button) findViewById(R.id.register_button);
        mFbLoginButton = (Button) findViewById(R.id.fbLoginButton);

        /**
         *  Shipping
         */
        shippingName = (EditText) findViewById(R.id.shipping_name);
        shippingAddress = (EditText) findViewById(R.id.shipping_address);
        shippingCity = (EditText) findViewById(R.id.shipping_city);
        shippingZipcode = (EditText) findViewById(R.id.shipping_zipcode);
        states = (Spinner) findViewById(R.id.shipping_state);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.states_abbreviations, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        states.setAdapter(adapter);
        states.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                state = (String) parent.getItemAtPosition(pos);
                Log.i(TAG, "State is:" + state);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                state = "";
                Log.i(TAG, "State is: "+state);
            }
        });
        nextInShipping = (Button) findViewById(R.id.shipping_next_button);

        /**
         * Billing
         */
        mCardName = (EditText) findViewById(R.id.card_name);
        mCardNumber = (EditText) findViewById(R.id.card_name);

        Spinner month = (Spinner) findViewById(R.id.expiration_month);
        ArrayAdapter<CharSequence> month_adapter = ArrayAdapter.createFromResource(this,
                R.array.exp_month, android.R.layout.simple_spinner_item);
        month_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        month.setAdapter(month_adapter);
        month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                mMonth = (String) parent.getItemAtPosition(pos);
                Log.i(TAG, "Month is:" + mMonth);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mMonth = "";
                Log.i(TAG, "Month is: "+ mMonth);
            }
        });

        Spinner year = (Spinner) findViewById(R.id.expiration_year);
        ArrayAdapter<CharSequence> year_adapter = ArrayAdapter.createFromResource(this,
                R.array.exp_year, android.R.layout.simple_spinner_item);
        year_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        year.setAdapter(year_adapter);
        year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                mYear = (String) parent.getItemAtPosition(pos);
                Log.i(TAG, "year is:" + mYear);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mYear = "";
                Log.i(TAG, "Year is: "+mYear);
            }
        });

        VerifyStripe = (Button) findViewById(R.id.stripe_verify);

        setOnClickListeners(this);
	}

    /*About you validation methods*/
    private boolean validatePasswordMatch() {
        if (mEditPassword.getText().toString()
                .equals(mEditPasswordConfirm.getText().toString())) {
            return true;
        } else {
            return false;
        }
    }
    // TODO: FIX THESE VALUES
    private boolean validateFields() {
        if (mEditUsername.getText().toString().length() > 2
                && mEditEmailAddress.getText().toString().length() > 2
                && mEditPassword.getText().toString().length() > 2
                && mEditPasswordConfirm.getText().toString().length() > 2) {
            return true;
        } else {
            return false;
        }
    }
    public List<String> getUserInformation() {
        final List<String> registerDetails = new ArrayList<String>();
        registerDetails.add(0, mEditUsername.getText().toString());
        registerDetails.add(1, mEditPassword.getText().toString());
        registerDetails.add(2, mEditEmailAddress.getText().toString());
        return registerDetails;
    }
	public Boolean registerAccount(View view) {
		if (validateFields()) {
			if (validatePasswordMatch()) {
                //processSignup(view);
                mParseService.registerNewUser(view.getContext(), getUserInformation());
                return true;
            } else {
				Toast.makeText(this, "Password don't match",
						Toast.LENGTH_SHORT).show();
                return false;
			}
		} else {
			Toast.makeText(this, "Fields not filled in", Toast.LENGTH_SHORT)
					.show();
            return false;
		}
	}

    public Boolean addShipping(View view) {
        if (validateShippingFields()) {
            mParseService.addShippingAddress(view.getContext(), getShippingInfoFromUI());
            return true;
        } else {
			Toast.makeText(this, "Fields not filled in", Toast.LENGTH_SHORT)
					.show();
            return false;
        }
    }

    // TODO: FIX THESE VALUES
    private boolean validateShippingFields() {
        if (shippingName.getText().length() > 1
                && shippingAddress.getText().length() > 1
                && shippingCity.getText().length() > 1
                && shippingZipcode.getText().length() > 4) {
            return true;
        } else {
            return false;
        }
    }

    private List<String> getShippingInfoFromUI() {
        List<String> info = new ArrayList<String>();
        info.add(0, shippingName.getText().toString());
        info.add(1, shippingAddress.getText().toString());
        info.add(2, shippingCity.getText().toString());
        info.add(3, state);
        info.add(4, shippingZipcode.getText().toString());
        return info;
    }


    /**
     * We have a lot of click listeners in this view, so I'm adding them in a separate method just to keep things organized.
     */
    private void setOnClickListeners(final Activity act) {

        mFbLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                     onFBLoginButtonClicked(act);
            }
        });
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(registerAccount(view)) {
                    //TODO: if invalid don't go to the next
                    Log.i(TAG, "Register account");
                    if(ParseUser.getCurrentUser().getObjectId().length() > 0) {
                        mViewFlipper.setInAnimation(act, R.anim.in_from_right);
                        mViewFlipper.setOutAnimation(act, R.anim.out_to_left);
                        mViewFlipper.setDisplayedChild(mViewFlipper.getDisplayedChild() + 1);
                        updateView(mViewFlipper.getDisplayedChild());
                    } else {
                        Toast.makeText(act, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(view.getContext(), "Hey, you're not done yet", Toast.LENGTH_SHORT).show();
                }
            }
        });
        nextInShipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addShipping(v)) {
                    Log.i(TAG, "Add Shipping");
                    mViewFlipper.setInAnimation(act, R.anim.in_from_right);
                    mViewFlipper.setOutAnimation(act, R.anim.out_to_left);
                    mViewFlipper.setDisplayedChild(mViewFlipper.getDisplayedChild() + 1);
                    updateView(mViewFlipper.getDisplayedChild());
                } else {
                    Toast.makeText(v.getContext(), "Missing some information", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mArrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mViewFlipper.getDisplayedChild() == 0) {
                    Intent i = new Intent(RegisterNewAccountActivity.this, LaunchActivity.class);
                    startActivity(i);
                }
            }
        });
        mSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mViewFlipper.getDisplayedChild() == 1) {
                    //go to get billing information
                    mViewFlipper.setInAnimation(act, R.anim.in_from_right);
                    mViewFlipper.setOutAnimation(act, R.anim.out_to_left);
                    mViewFlipper.setDisplayedChild(mViewFlipper.getDisplayedChild() + 1);
                    updateView(mViewFlipper.getDisplayedChild());
                } else if(mViewFlipper.getDisplayedChild() == 2) {
                    startMainActivity();
                }
            }
        });

        VerifyStripe.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Log.i(TAG, "Were verifying stripe");
               RegisterNewAccountActivity.this.progressDialog = ProgressDialog.show(
                       RegisterNewAccountActivity.this, "", "Verifying Card using Stripe....", true);

               //TODO: Save last 4 digits
               String mCVV = "123";
               String theCardNumber = "4242424242424242";
               String theMonth = "12";
               String theYear = "15";
               try {
                   //mParseService.registerStripeCustomer(view.getContext(), mCardNumber.getText().toString(), mMonth, mYear, mCVV);
                   mParseService.registerStripeCustomer(view.getContext(), theCardNumber, theMonth, theYear, mCVV);
                   RegisterNewAccountActivity.this.progressDialog.dismiss();
                   startMainActivity();
               } catch(AuthenticationException e ) {
                   Log.e(TAG, e.getMessage());
               }
           }
        });
    }

    /**
     * Updates the view depending on the step in the checkout process.
     *
     * @param newView The new view to show.
     */
    private void updateView(int newView) {
        switch (newView) {
            case ABOUT:
                mOneCircle.setBackgroundResource(R.drawable.circle_blue_button);
                mTwoCircle.setBackgroundResource(R.drawable.circle_grey);
                mThreeCircle.setBackgroundResource(R.drawable.circle_grey);
                mAboutHeader.setTypeface(Typeface.DEFAULT_BOLD);
                mShippingHeader.setTypeface(Typeface.DEFAULT);
                mBillingHeader.setTypeface(Typeface.DEFAULT);

                mArrowBack.setVisibility(View.VISIBLE);
                mSkipButton.setVisibility(View.GONE);
                break;
            case SHIPPING:
                mOneCircle.setBackgroundResource(R.drawable.circle_grey);
                mTwoCircle.setBackgroundResource(R.drawable.circle_blue_button);
                mThreeCircle.setBackgroundResource(R.drawable.circle_grey);
                mAboutHeader.setTypeface(Typeface.DEFAULT);
                mShippingHeader.setTypeface(Typeface.DEFAULT_BOLD);
                mBillingHeader.setTypeface(Typeface.DEFAULT);

                mArrowBack.setVisibility(View.GONE);
                mSkipButton.setVisibility(View.VISIBLE);
                break;
            case BILLING:
                mOneCircle.setBackgroundResource(R.drawable.circle_grey);
                mTwoCircle.setBackgroundResource(R.drawable.circle_grey);
                mThreeCircle.setBackgroundResource(R.drawable.circle_blue_button);
                mAboutHeader.setTypeface(Typeface.DEFAULT);
                mShippingHeader.setTypeface(Typeface.DEFAULT);
                mBillingHeader.setTypeface(Typeface.DEFAULT_BOLD);
                mArrowBack.setVisibility(View.GONE);
                mSkipButton.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void addShippingInfo(ParseUser user, View view) {
        if(shippingAddress.getText().toString() != null) {
            user.put("address", shippingAddress.getText().toString());
        }
        if(shippingCity.getText().toString() != null) {
            user.put("state", shippingCity.getText().toString());
        }
        user.put("zipcode", shippingZipcode.getText().toString());
        user.put("city", state);
        user.saveInBackground();
    }

    /**
     * Handles facebook login request.
     */
    private void onFBLoginButtonClicked(final Activity act) {
        RegisterNewAccountActivity.this.progressDialog = ProgressDialog.show(
                RegisterNewAccountActivity.this, "", "Logging in...", true);
        List<String> permissions = Arrays.asList("public_profile");
        ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                RegisterNewAccountActivity.this.progressDialog.dismiss();
                if (user == null) {
                    Log.d(TAG,
                            "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    Log.d(TAG,
                            "user signed up and logged in through Facebook!");
                    user.put("outfit_count", 0);
                    user.saveInBackground();
                    //go to get shipping address information
                    mViewFlipper.setInAnimation(act, R.anim.in_from_right);
                    mViewFlipper.setOutAnimation(act, R.anim.out_to_left);
                    mViewFlipper.setDisplayedChild(mViewFlipper.getDisplayedChild() + 1);
                    updateView(mViewFlipper.getDisplayedChild());

                    //startMainActivity();
                } else {
                    Log.d(TAG,
                            "user logged in through Facebook!");
                    //go get shipping address
                    user.saveInBackground();
                    mViewFlipper.setInAnimation(act, R.anim.in_from_right);
                    mViewFlipper.setOutAnimation(act, R.anim.out_to_left);
                    mViewFlipper.setDisplayedChild(mViewFlipper.getDisplayedChild() + 1);
                    updateView(mViewFlipper.getDisplayedChild());
                    //startMainActivity();
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

}
