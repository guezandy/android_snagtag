package com.snagtag.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.snagtag.R;

/**
 * Created by benjamin on 10/7/14.
 */
public class StoreFragment extends Fragment {

    private ListView mView;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = (ListView)inflater.inflate(R.layout.fragment_store, container, false);



        return mView;
    }


    private void setClickListeners() {

    }
}
