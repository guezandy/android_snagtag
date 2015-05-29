package com.snagtag.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snagtag.R;
import com.snagtag.account_tabs.TabProfile;
import com.snagtag.account_tabs.TabBilling;
import com.snagtag.account_tabs.TabOrders;
import com.snagtag.account_tabs.TabShipping;

// This class is the 3rd fragment in my ViewPager,
// to which I wanted to add 2 tabs....
public class AccountTabsFragment extends Fragment {
    private FragmentTabHost mTabHost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mTabHost = new FragmentTabHost(getActivity());
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.layout.fragment_account_tabs);

        Bundle arg1 = new Bundle();
        arg1.putInt("Arg for Frag1", 1);
        mTabHost.addTab(mTabHost.newTabSpec("Tab1").setIndicator("profile"),
                TabProfile.class, arg1);

        Bundle arg2 = new Bundle();
        arg2.putInt("Arg for Frag2", 2);
        mTabHost.addTab(mTabHost.newTabSpec("Tab2").setIndicator("shipping"),
                TabShipping.class, arg2);

        Bundle arg3 = new Bundle();
        arg3.putInt("Arg for Frag3", 3);
        mTabHost.addTab(mTabHost.newTabSpec("Tab3").setIndicator("billing"),
                TabBilling.class, arg3);

        Bundle arg4 = new Bundle();
        arg4.putInt("Arg for Frag4", 4);
        mTabHost.addTab(mTabHost.newTabSpec("Tab4").setIndicator("orders"),
                TabOrders.class, arg4);

        return mTabHost;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTabHost = null;
    }
}