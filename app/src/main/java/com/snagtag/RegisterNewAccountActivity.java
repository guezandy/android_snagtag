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

    protected EditText mEditUsername;
	protected EditText mEditEmailAddress;
	protected EditText mEditPassword;
	protected EditText mEditPasswordConfirm;
	protected TextView mSkipButton;
    protected Button mRegister;

    private ParseService mParseService;
    private ViewFlipper mViewFlipper;

    private View mArrowBack;
    private Button mFbLoginButton;
    private Button mNextButton;
    private Spinner states;
    private String state;


    private TextView mEditHeader;
    private TextView mShippingHeader;
    private TextView mReviewHeader;


    //Shipping fields
    private EditText shippingAddress;
    private EditText shippingCity;
    private EditText shippingZipcode;

    //Card fields
    private EditText mCardName;
    private EditText mCardNumber;
    private Button VerifyStripe;
    String mMonth;
    String mYear;

    private static final int EDIT = 0;
    private static final int SHIPPING = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register_steps);

        mViewFlipper = (ViewFlipper) findViewById(R.id.checkout_view_flipper);

        mArrowBack = findViewById(R.id.arrow_back);
        mNextButton = (Button) findViewById(R.id.next);
        mFbLoginButton = (Button) findViewById(R.id.fbLoginButton);

        mSkipButton = (TextView) findViewById(R.id.button_place_order);
        mSkipButton.setVisibility(View.GONE);

        mRegister = (Button) findViewById(R.id.startSnagging);


        mEditHeader = (TextView) findViewById(R.id.edit_header);
        mShippingHeader = (TextView) findViewById(R.id.shipping_header);
        mReviewHeader = (TextView) findViewById(R.id.review_header);


        /**
         * Page one
         */
        mEditUsername = (EditText) findViewById(R.id.username);
        mEditEmailAddress = (EditText) findViewById(R.id.unEmail);
        mEditPassword = (EditText) findViewById(R.id.pass);
        mEditPasswordConfirm = (EditText) findViewById(R.id.comPass);

        /**
         *  Shipping
         */

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
                mParseService = new ParseService(view.getContext());
                mParseService.registerNewUser(view.getContext(), getUserInformation());
                return true;
            } else {
				Toast.makeText(this, "Password doesn't match",
						Toast.LENGTH_SHORT).show();
                return false;
			}
		} else {
/*			Toast.makeText(this, "Fields not filled in", Toast.LENGTH_SHORT)
					.show();*/
            return false;
		}
	}

	// TODO: FIX THESE VALUES
	private boolean validateFields() {
		if (mEditUsername.getText().length() > 0
                && mEditEmailAddress.length() > 0
				&& mEditPassword.getText().length() > 0
				&& mEditPasswordConfirm.getText().length() > 0) {
			return true;
		} else {
			return false;
		}
	}

	private boolean validatePasswordMatch() {
		if (mEditPassword.getText().toString()
				.equals(mEditPasswordConfirm.getText().toString())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Calls the createUserWithUsername method of the Parse to create a new
	 * user. After sign-up is successful, the First and Last name are added to
	 * the user, and an Intent is instantiated to bring the user back to the
	 * login page to confirm authentication.
	 *
	 * Note that a user is not Authorized here to the Android Account Manager,
	 * but rather is added to AccountManager upon successful authentication.
	 */
	public void processSignup(final View view) {
		Toast.makeText(this, "Creating user...", Toast.LENGTH_SHORT).show();

		final ParseUser user = new ParseUser();
		// username is set to email
		user.setUsername(mEditEmailAddress.getText().toString());
		user.setPassword(mEditPassword.getText().toString());
		user.setEmail(mEditEmailAddress.getText().toString().toLowerCase());

		user.signUpInBackground(new SignUpCallback() {
			@Override
			public void done(ParseException e) {
				if (e == null) {
					Toast.makeText(
							view.getContext(),
							"Registration Successful\nSending Confirmation Email",
							Toast.LENGTH_SHORT).show();
					// TODO: Email notification???
					user.setEmail(user.getEmail());
					Intent i = new Intent(RegisterNewAccountActivity.this,
							ParseLoginDispatchActivity.class);
					startActivity(i);
					// Hooray! Let them use the app now.
				} else {
					Toast.makeText(view.getContext(), "Registration Failed",
							Toast.LENGTH_SHORT).show();
					// Sign up didn't succeed. Look at the ParseException
					// to figure out what went wrong
                    Log.e(TAG, "Login failed: "+e.getMessage());
				}
			}
		});
	}



    /**
     * We have a lot of click listeners in this view, so I'm adding them in a separate method just to keep things organized.
     */
    private void setOnClickListeners(final Activity act) {
/*        mArrowForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewFlipper.getDisplayedChild() < mViewFlipper.getChildCount() - 1) {
                    mViewFlipper.setInAnimation(act, R.anim.in_from_right);
                    mViewFlipper.setOutAnimation(act, R.anim.out_to_left);
                    mViewFlipper.setDisplayedChild(mViewFlipper.getDisplayedChild() + 1);
                    registerAccount(view);
                    updateView(mViewFlipper.getDisplayedChild());
                }
            }
        });*/
        mFbLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                     onFBLoginButtonClicked();
            }
        });
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(registerAccount(view)) {
                    Log.i(TAG, "Register account");
                    mViewFlipper.setInAnimation(act, R.anim.in_from_right);
                    mViewFlipper.setOutAnimation(act, R.anim.out_to_left);
                    mViewFlipper.setDisplayedChild(mViewFlipper.getDisplayedChild() + 1);
                    updateView(mViewFlipper.getDisplayedChild());
                } else {
                    Toast.makeText(view.getContext(), "Hey, you're not done yet", Toast.LENGTH_SHORT).show();
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
                Intent i = new Intent(RegisterNewAccountActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        VerifyStripe.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Log.i(TAG, "Were verifying stripe");
               mParseService = new ParseService(view.getContext());

               String mCVV = "123";
               String theCardNumber = "4242424242424242";
               String theMonth = "12";
               String theYear = "14";
               try {
                   //mParseService.registerStripeCustomer(view.getContext(), mCardNumber.getText().toString(), mMonth, mYear, mCVV);
                   mParseService.registerStripeCustomer(view.getContext(), theCardNumber, theMonth, theYear, mCVV);

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
            case EDIT:
                mEditHeader.setTypeface(Typeface.DEFAULT_BOLD);
                mShippingHeader.setTypeface(Typeface.DEFAULT);
                mArrowBack.setVisibility(View.VISIBLE);
                mSkipButton.setVisibility(View.GONE);
                break;
            case SHIPPING:
                mEditHeader.setTypeface(Typeface.DEFAULT);
                mShippingHeader.setTypeface(Typeface.DEFAULT_BOLD);
                mArrowBack.setVisibility(View.GONE);
                mSkipButton.setVisibility(View.VISIBLE);
                mSkipButton.setText("Skip N Start Snaggin'");
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
    private void onFBLoginButtonClicked() {
        RegisterNewAccountActivity.this.progressDialog = ProgressDialog.show(
                RegisterNewAccountActivity.this, "", "Logging in...", true);
        List<String> permissions = Arrays.asList("public_profile", "user_friends", "user_about_me",
                "user_relationships", "user_birthday", "user_location");
        ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                RegisterNewAccountActivity.this.progressDialog.dismiss();
                if (user == null) {
                    Log.d(TAG,
                            "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    Log.d(TAG,
                            "User signed up and logged in through Facebook!");
                    user.put("outfit_count", 0);
                    startMainActivity();

                } else {
                    Log.d(TAG,
                            "User logged in through Facebook!");
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

}
