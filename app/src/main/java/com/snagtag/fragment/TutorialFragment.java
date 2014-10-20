package com.snagtag.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snagtag.R;
import com.snagtag.utils.FragmentUtil;

/**
 * Shows Tutorial screen.
 * Created by benjamin on 10/19/14.
 */
public class TutorialFragment extends Fragment {

    private View mView;

    private View mContinue;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_tutorial, container, false);

        mContinue = mView.findViewById(R.id.button_continue);

        setClickListeners();

        return mView;
    }

    private void setClickListeners() {
        mContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentUtil.replaceFragment(getActivity(), new ClosetFragment(), true, FragmentTransaction.TRANSIT_FRAGMENT_FADE, "Closet");
            }
        });
    }
}
