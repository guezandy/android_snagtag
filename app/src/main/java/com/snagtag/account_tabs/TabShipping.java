package com.snagtag.account_tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;
import com.snagtag.R;
import com.snagtag.adapter.ShippingAdapter;
import com.snagtag.adapter.TagHistoryItemAdapter;
import com.snagtag.models.ShippingModel;
import com.snagtag.models.TagHistoryItem;
import com.snagtag.service.IParseCallback;
import com.snagtag.service.ParseService;

import java.util.List;

/**
 * @author mwho
 *
 */
public class TabShipping extends Fragment {
    /** (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */

    private ParseQueryAdapter<ParseObject> mainAdapter;
    //private CustomAdapter urgentTodosAdapter;
    private ListView listView;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            // We have different layouts, and in one of them this
            // fragment's containing frame doesn't exist.  The fragment
            // may still be created from its saved state, but there is
            // no reason to try to create its view hierarchy because it
            // won't be displayed.  Note this is not needed -- we could
            // just run the code below, where we would create and return
            // the view hierarchy; it would just never be used.
            return null;
        }

        View mView = (RelativeLayout)inflater.inflate(R.layout.tab_account_shipping, container, false);

        final ListView itemGrid = (ListView) mView.findViewById(R.id.account_shipping_list);

        final ShippingAdapter listAdapter = new ShippingAdapter(getActivity().getApplicationContext(), R.layout.row_item_address_view, itemGrid);

        itemGrid.setAdapter(listAdapter);

        new ParseService(getActivity().getApplicationContext()).getShippingPerUser(getActivity().getApplicationContext(), new IParseCallback<List<ShippingModel>>() {
            @Override
            public void onSuccess(List<ShippingModel> items) {
                listAdapter.setItems(items);
            }

            @Override
            public void onFail(String message) {

            }
        });

        return mView;
    }
}