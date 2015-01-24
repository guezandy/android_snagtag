package com.snagtag.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.parse.ParseUser;
import com.snagtag.R;
import com.snagtag.adapter.CartItemAdapter;
import com.snagtag.models.CartItem;
import com.snagtag.models.StripeOrderModel;
import com.snagtag.service.IParseCallback;
import com.snagtag.service.ParseService;
import com.snagtag.utils.Constant;

import java.util.List;

/**
 * Includes all the steps to checkout.
 *
 * Created by benjamin on 10/10/14.
 */
public class CheckoutFragment extends Fragment {

    private View mView;
    private String mStore;
    private ViewFlipper mViewFlipper;
    private ListView itemListView;


    private TextView mStoreNameView;
    private View mArrowForward;
    private View mArrowBack;

    private TextView mEditHeader;
    private TextView mShippingHeader;
    private TextView mReviewHeader;
    private View mPlaceOrderButton;
    private View mApplyDiscountCodeButton;

    //Shipping fields
    private TextView mUserName;
    private TextView mCityStateZip;
    private TextView mStreetAddress;

    //Card fields
    private TextView mCardName;
    private TextView mCardNumber;
    private TextView mExpiration;

    private static final int EDIT = 0;
    private static final int SHIPPING = 1;
    private static final int REVIEW = 2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle args = getArguments();
        mStore = args.getString(Constant.PARAM_STORE);

        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_checkout, container, false);
        mViewFlipper = (ViewFlipper) mView.findViewById(R.id.checkout_view_flipper);

        itemListView = (ListView) mView.findViewById(R.id.items_list);

        mStoreNameView = (TextView) mView.findViewById(R.id.store_name);
        mArrowForward = mView.findViewById(R.id.arrow_forward);
        mArrowBack = mView.findViewById(R.id.arrow_back);
        mPlaceOrderButton = mView.findViewById(R.id.button_place_order);
        mApplyDiscountCodeButton = mView.findViewById(R.id.button_apply_discount_code);

        mEditHeader = (TextView) mView.findViewById(R.id.edit_header);
        mShippingHeader = (TextView) mView.findViewById(R.id.shipping_header);
        mReviewHeader = (TextView) mView.findViewById(R.id.review_header);

        mUserName = (TextView) mView.findViewById(R.id.user_name);
        mStreetAddress = (TextView) mView.findViewById(R.id.street_address);
        mCityStateZip = (TextView) mView.findViewById(R.id.city_state_zip);

        mCardName = (TextView) mView.findViewById(R.id.card_name);
        mCardNumber = (TextView) mView.findViewById(R.id.card_number);
        mExpiration = (TextView) mView.findViewById(R.id.card_expiration);


        setOrderInformation();
        setOnClickListeners();

        return mView;

    }

    /**
     * Set all the billing, shippping, and item information.
     */
    private void setOrderInformation() {
        //TODO: get the info for this order and set it instead of the placeholder text.
        mStoreNameView.setText(mStore + " Cart");
        mUserName.setText("Sample Name");
        mStreetAddress.setText("30 W. Mifflin, Suite 404");
        mCityStateZip.setText("Madison, WI 53719");

        mCardName.setText("Noble Applications");
        mCardNumber.setText("Card #: xxxx xxxx xxxx 1234");
        mExpiration.setText("Exp Date: 10/14");
        final CartItemAdapter checkoutAdapter = new CartItemAdapter(getActivity().getApplicationContext(), R.layout.row_item_checkout);
        itemListView.setAdapter(checkoutAdapter);

        new ParseService(getActivity().getApplicationContext()).getCartItems(getActivity().getApplicationContext(), mStore, new IParseCallback<List<CartItem>>() {
            @Override
            public void onSuccess(final List<CartItem> items) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        checkoutAdapter.setItems(items);
                        StripeOrderModel order = new StripeOrderModel(ParseUser.getCurrentUser(), items, mStore);
                    }
                });

            }

            @Override
            public void onFail(String message) {

            }
        });

    }

    /**
     * We have a lot of click listeners in this view, so I'm adding them in a separate method just to keep things organized.
     */
    private void setOnClickListeners() {
        mArrowForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewFlipper.getDisplayedChild() < mViewFlipper.getChildCount() - 1) {
                    mViewFlipper.setInAnimation(CheckoutFragment.this.getActivity(), R.anim.in_from_right);
                    mViewFlipper.setOutAnimation(CheckoutFragment.this.getActivity(), R.anim.out_to_left);
                    mViewFlipper.setDisplayedChild(mViewFlipper.getDisplayedChild() + 1);
                    updateView(mViewFlipper.getDisplayedChild());
                }
            }
        });
        mArrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewFlipper.getDisplayedChild() > 0) {
                    mViewFlipper.setInAnimation(CheckoutFragment.this.getActivity(), R.anim.in_from_left);
                    mViewFlipper.setOutAnimation(CheckoutFragment.this.getActivity(), R.anim.out_to_right);
                    mViewFlipper.setDisplayedChild(mViewFlipper.getDisplayedChild() - 1);
                    updateView(mViewFlipper.getDisplayedChild());
                }
            }
        });
        mPlaceOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Place order and show receipt (Should probably add receipt to users receipts and open the order history to this order.
            }
        });

        mApplyDiscountCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Handle the application of the discount code here.
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
                mReviewHeader.setTypeface(Typeface.DEFAULT);
                break;
            case SHIPPING:
                mEditHeader.setTypeface(Typeface.DEFAULT);
                mShippingHeader.setTypeface(Typeface.DEFAULT_BOLD);
                mReviewHeader.setTypeface(Typeface.DEFAULT);
                mArrowForward.setVisibility(View.VISIBLE);
                mPlaceOrderButton.setVisibility(View.GONE);
                break;
            case REVIEW:
                mEditHeader.setTypeface(Typeface.DEFAULT);
                mShippingHeader.setTypeface(Typeface.DEFAULT);
                mReviewHeader.setTypeface(Typeface.DEFAULT_BOLD);
                mArrowForward.setVisibility(View.GONE);
                mPlaceOrderButton.setVisibility(View.VISIBLE);
                break;
        }
    }

}
