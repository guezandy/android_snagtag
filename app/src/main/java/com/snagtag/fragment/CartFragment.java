package com.snagtag.fragment;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.snagtag.R;
import com.snagtag.adapter.CartItemAdapter;
import com.snagtag.models.CartItem;
import com.snagtag.service.IParseCallback;
import com.snagtag.service.ParseService;
import com.snagtag.utils.Constant;
import com.snagtag.utils.FragmentUtil;

import java.util.List;

/**
 * Represents a Cart of items to purchase.
 * Created by benjamin on 10/7/14.
 */
public class CartFragment extends Fragment {

    private static final float COLUMN_WIDTH = 49f;

    private ViewFlipper mView;
    private LinearLayout storeListLayout;
    private ParseService mParseService;
    private CartItemAdapter cartItemAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = (ViewFlipper) inflater.inflate(R.layout.fragment_cart, container, false);
        storeListLayout = (LinearLayout) mView.findViewById(R.id.cart_list_store);
        mParseService = new ParseService(getActivity().getApplicationContext());
        mParseService.getStoresByCartItems(getActivity().getApplicationContext(), new IParseCallback<List<String>>() {
            @Override
            public void onSuccess(final List<String> items) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        for (final String store : items) {
                            final View storeView = inflater.inflate(R.layout.row_item_cart_store, null);
                            TextView title = (TextView) storeView.findViewById(R.id.title_store_name);
                            title.setText(store);
                            final View header = storeView.findViewById(R.id.title_bar);
                            final View openIndicator = storeView.findViewById(R.id.store_open_indicator);
                            final View detailLayout = storeView.findViewById(R.id.detail_layout);

                            final TextView totalView = (TextView) storeView.findViewById(R.id.item_cost);
                            final TextView countView = (TextView) storeView.findViewById(R.id.item_summary);

                            View checkout = storeView.findViewById(R.id.button_checkout);
                            checkout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Bundle args = new Bundle();
                                    args.putString(Constant.PARAM_STORE, store);
                                    CheckoutFragment checkoutFragment = new CheckoutFragment();
                                    checkoutFragment.setArguments(args);
                                    FragmentUtil.replaceFragment(getActivity(), checkoutFragment, true, 0, getResources().getString(R.string.label_checkout));

                                }
                            });

                            header.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    final LinearLayout itemGrid = (LinearLayout) storeView.findViewById(R.id.item_grid);
                                    if (detailLayout.getVisibility() == View.VISIBLE) {
                                        ScaleAnimation anim = new ScaleAnimation(1, 1, 1, 0);
                                        anim.setDuration(300);

                                        anim.setAnimationListener(new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationStart(Animation animation) {

                                            }

                                            @Override
                                            public void onAnimationEnd(Animation animation) {
                                                detailLayout.setVisibility(View.GONE);
                                                openIndicator.setRotation(0);
                                                double total = 0.0;
                                                for (int i = 0; i < cartItemAdapter.getCount(); i++) {
                                                    CartItem item = (CartItem) cartItemAdapter.getItem(i);
                                                    total = total + item.getItem().getPrice();
                                                }
                                                totalView.setText(String.format(getString(R.id.item_subtotal), total));
                                                countView.setText(String.format(getString(R.id.item_subtotal), cartItemAdapter.getCount()));
                                                totalView.setVisibility(View.VISIBLE);
                                                countView.setVisibility(View.VISIBLE);
                                                itemGrid.removeAllViews();
                                            }

                                            @Override
                                            public void onAnimationRepeat(Animation animation) {

                                            }
                                        });
                                        detailLayout.startAnimation(anim);

                                    } else {
                                        ScaleAnimation anim = new ScaleAnimation(1, 1, 0, 1);
                                        anim.setDuration(300);
                                        detailLayout.startAnimation(anim);
                                        detailLayout.setVisibility(View.VISIBLE);
                                        openIndicator.setRotation(180);
                                        CartItemsObserver caObserver = new CartItemsObserver(null, itemGrid, countView, totalView);
                                        cartItemAdapter = new CartItemAdapter(getActivity().getApplicationContext(), R.layout.row_item_cart_item);
                                        caObserver.setItemsAdapter(cartItemAdapter);

                                        new ParseService(getActivity().getApplicationContext()).getCartItems(getActivity().getApplicationContext(), store, new IParseCallback<List<CartItem>>() {
                                            @Override
                                            public void onSuccess(List<CartItem> items) {
                                                cartItemAdapter.setItems(items);
                                                LinearLayout currentRow = null;
                                                for (int i = 0; i < cartItemAdapter.getCount(); i++) {

                                                    if (i % 2 == 0) {
                                                        currentRow = new LinearLayout(getActivity());
                                                        currentRow.setOrientation(LinearLayout.HORIZONTAL);
                                                        currentRow.setBackgroundColor(getResources().getColor(R.color.grey_light));
                                                        currentRow.setPadding(4, 2, 4, 2);
                                                        itemGrid.addView(currentRow);
                                                    }
                                                    View v = cartItemAdapter.getView(i, null, currentRow);
                                                    // Get params:
                                                    LinearLayout.LayoutParams loparams = (LinearLayout.LayoutParams) v.getLayoutParams();
                                                    if (loparams == null) {
                                                        loparams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, COLUMN_WIDTH);
                                                        loparams.setMargins(2, 0, 2, 0);
                                                    }
                                                    v.setLayoutParams(loparams);
                                                    currentRow.addView(v);

                                                }
                                            }

                                            @Override
                                            public void onFail(String message) {

                                            }
                                        });



                                        totalView.setVisibility(View.GONE);
                                        countView.setVisibility(View.GONE);
                                    }
                                }
                            });
                            storeListLayout.addView(storeView);
                        }
                    }
                });


            }

            @Override
            public void onFail(String message) {

            }
        });


        return mView;
    }

    /*public int getDip(int pixel) {
        float scale = getActivity().getBaseContext().getResources().getDisplayMetrics().density;
        return (int) (pixel * scale + 0.5f);
    }*/

    class CartItemsObserver extends DataSetObserver {

        private BaseAdapter itemsAdapter;
        private TextView itemsView, totalView;
        private LinearLayout grid;

        public CartItemsObserver(BaseAdapter itemsAdapter, LinearLayout grid, TextView itemsView, TextView totalView) {
            this.itemsAdapter = itemsAdapter;
            this.totalView = totalView;
            this.itemsView = itemsView;
            this.grid = grid;
        }

        public void setItemsAdapter(BaseAdapter items) {
            this.itemsAdapter = items;
        }

        @Override
        public void onChanged() {
            double total = 0.0;
            LinearLayout currentRow = null;
            for (int i = 0; i < itemsAdapter.getCount(); i++) {
                CartItem item = (CartItem) itemsAdapter.getItem(i);
                total = total + item.getItem().getPrice();
                if (i % 2 == 0) {
                    currentRow = new LinearLayout(getActivity());
                    currentRow.setOrientation(LinearLayout.HORIZONTAL);
                    grid.addView(currentRow);
                }


                currentRow.addView(cartItemAdapter.getView(i, null, currentRow));
            }
            totalView.setText("" + total);
            itemsView.setText(("" + itemsAdapter.getCount()));
        }
    }
}
