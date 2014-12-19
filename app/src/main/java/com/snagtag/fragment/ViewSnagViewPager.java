package com.snagtag.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.snagtag.R;
import com.snagtag.adapter.SnagViewAdapter;
import com.snagtag.adapter.TagHistoryItemAdapter;
import com.snagtag.models.TagHistoryItem;
import com.snagtag.service.IParseCallback;
import com.snagtag.service.ParseService;

import java.util.List;

public final class ViewSnagViewPager extends Fragment {
    private final String TAG = ViewSnagViewPager.class.getSimpleName();
    private static final String KEY_CONTENT = "TestFragment:Content";

    public static ViewSnagViewPager newInstance(String content) {
        ViewSnagViewPager fragment = new ViewSnagViewPager();
        fragment.mContent = content;
        return fragment;
    }

    private String mContent = "???";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        GridView itemGrid = new GridView(getActivity());
        final SnagViewAdapter gridAdapter = new SnagViewAdapter(getActivity().getApplicationContext(), R.layout.row_snag_clothing_item);
        itemGrid.setAdapter(gridAdapter);
        Log.i(TAG, mContent);
        if(mContent.equals("Tops")) {
            Log.i(TAG, "GOT INSIDE TOPS");
            new ParseService(getActivity().getApplicationContext()).getTops(getActivity().getApplicationContext(), new IParseCallback<List<TagHistoryItem>>() {
                @Override
                public void onSuccess(List<TagHistoryItem> items) {
                    gridAdapter.setItems(items);
                }

                @Override
                public void onFail(String message) {

                }
            });
        } else if(mContent.equals("Bottoms")) {
            Log.i(TAG, "GOT INSIDE Bottoms");
            new ParseService(getActivity().getApplicationContext()).getBottoms(getActivity().getApplicationContext(), new IParseCallback<List<TagHistoryItem>>() {
                @Override
                public void onSuccess(List<TagHistoryItem> items) {
                    gridAdapter.setItems(items);
                }

                @Override
                public void onFail(String message) {

                }
            });

        } else if(mContent.equals("Shoes")) {
            Log.i(TAG, "GOT INSIDE shoes");
            new ParseService(getActivity().getApplicationContext()).getShoes(getActivity().getApplicationContext(), new IParseCallback<List<TagHistoryItem>>() {
                @Override
                public void onSuccess(List<TagHistoryItem> items) {
                    gridAdapter.setItems(items);
                }

                @Override
                public void onFail(String message) {

                }
            });

        } else {

        }

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        //layout.setGravity(Gravity.CENTER);
        layout.addView(itemGrid);

        return layout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }
}
