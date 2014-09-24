package com.snagtag.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snagtag.R;

/**
 * This fragment displays a single clothing item
 * This will always be provided the nfcId (Barcode number) and will query parse for the item
 * This will be reused for a single item view also
 * Created by Owner on 9/24/2014.
 */
public class SingleItemFragment extends Fragment {
    private RelativeLayout mItemView;
    public String id;
    public TextView description;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mItemView = (RelativeLayout) inflater.inflate(
                R.layout.fragment_item_detail_view, container, false);

        description = (TextView) mItemView.findViewById(R.id.item_description);
        description.setText(this.id);

        return mItemView;
    }

    public void setItemId(String itemId) {
        this.id = itemId;
    }
}
