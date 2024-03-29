package com.snagtag.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.snagtag.R;
import com.snagtag.fragment.HomeViewPagerFragment;
import com.viewpagerindicator.IconPagerAdapter;

public class HomeFragmentAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
    protected static final String[] CONTENT = new String[] { "Something Something 111111", "22222 Something something", "33333 text text text something text", "4444 last entry", };

    protected static final int[] IMAGES = new int[] {
            R.drawable.stock1,
            R.drawable.stock2,
            R.drawable.stock3,
            R.drawable.stock4
    };
    protected static final int[] ICONS = new int[] {
            R.drawable.perm_group_calendar,
            R.drawable.perm_group_camera,
            R.drawable.perm_group_device_alarms,
            R.drawable.perm_group_location
    };

    private int mCount = CONTENT.length;

    public HomeFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return HomeViewPagerFragment.newInstance(CONTENT[position % CONTENT.length], IMAGES[position% IMAGES.length]);
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return HomeFragmentAdapter.CONTENT[position % CONTENT.length];
    }

    @Override
    public int getIconResId(int index) {
        return ICONS[index % ICONS.length];
    }

    public void setCount(int count) {
        if (count > 0 && count <= 10) {
            mCount = count;
            notifyDataSetChanged();
        }
    }
}
