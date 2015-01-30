package com.snagtag.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.snagtag.adapter.OutfitItemAdapter;
import com.snagtag.models.OutfitItem;
import com.snagtag.models.TagHistoryItem;
import com.snagtag.service.IParseCallback;
import com.snagtag.service.ParseService;
import com.viewpagerindicator.TabPageIndicator;
import com.snagtag.R;
import com.snagtag.utils.FragmentUtil;
import com.viewpagerindicator.TitlePageIndicator;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Shows Tutorial screen.
 * Created by benjamin on 10/19/14.
 */
public class OutfitItemEmptyFragment extends Fragment {
    private final String TAG = ViewOutfitFragment.class.getSimpleName();


    private View mView;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Context context = getActivity().getBaseContext();
        mView = inflater.inflate(R.layout.fragment_outfit_empty, container, false);
        return mView;
    }
}