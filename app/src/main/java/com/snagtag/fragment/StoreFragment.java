package com.snagtag.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.snagtag.R;
import com.snagtag.adapter.StoreItemAdapter;
import com.snagtag.models.StoreItem;
import com.snagtag.service.IParseCallback;
import com.snagtag.service.ParseService;

import java.util.List;

/**
 * Shows lists of all the stores that have Snag Tags.
 * Created by benjamin on 10/7/14.
 */
public class StoreFragment extends Fragment {

    private ListView mView;
    private StoreItemAdapter mAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = (ListView) inflater.inflate(R.layout.fragment_store, container, false);
        mAdapter = new StoreItemAdapter(getActivity(), R.layout.row_item_store);
        mView.setAdapter(mAdapter);

        ParseService service = new ParseService(getActivity());
        service.getAllStores(getActivity(), new IParseCallback<List<StoreItem>>() {
            @Override
            public void onSuccess(final List<StoreItem> items) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.addAll(items);
                        mAdapter.notifyDataSetChanged();
                    }
                });


            }

            @Override
            public void onFail(String message) {

            }
        });

        setClickListeners();

        return mView;
    }


    private void setClickListeners() {

    }
}
