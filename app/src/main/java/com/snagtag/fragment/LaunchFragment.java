package com.snagtag.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.snagtag.R;
import com.snagtag.adapter.LaunchFragmentAdapter;
import com.viewpagerindicator.LinePageIndicator;
import com.snagtag.adapter.HomeFragmentAdapter;


/**
 * Created by Andrew
 */
public class LaunchFragment extends Fragment {

    private View mView;
    LaunchFragmentAdapter mAdapter;
    ViewPager mPager;
    LinePageIndicator mIndicator;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_launch, container, false);

        mAdapter = new LaunchFragmentAdapter(getActivity().getSupportFragmentManager());

        mPager = (ViewPager)mView.findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (LinePageIndicator)mView.findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);

        return mView;
    }
}
