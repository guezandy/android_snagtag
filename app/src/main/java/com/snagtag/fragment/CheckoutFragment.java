package com.snagtag.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import com.snagtag.R;
import com.snagtag.service.MockParseService;

/**
 * Created by benjamin on 10/10/14.
 */
public class CheckoutFragment extends Fragment {

    private ViewFlipper mView;
    private MockParseService mParseService;
    private String store;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        store = savedInstanceState.getString("store");
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = (ViewFlipper) inflater.inflate(R.layout.fragment_cart, container, false);
        mParseService = new MockParseService();


        return mView;

    }
}
