package com.snagtag.service;


import android.content.Context;
import android.database.DataSetObserver;
import android.widget.BaseAdapter;

import com.parse.ParseQueryAdapter;
import com.snagtag.models.TagHistoryItem;

import java.util.List;

/**
 * Interface to define interactions with Parse.
 *
 * Created by benjamin on 9/19/14.
 */
public interface IParseService {

    public List<String> getStoresByTags(Context context);

    public List<String> getStoresByCartItems(Context context);

    public BaseAdapter getTagHistoryAdapter(Context context, String store);

    public ParseQueryAdapter TagHistoryAdapter(Context context, String store);
    /**
     *
     * @param context
     * @param store
     * @param dataChangedObserver Callback so when the list gets updated asyncronously we can update the totals.
     * @return
     */
    public BaseAdapter getCartItemAdapter(final Context context, String store, DataSetObserver dataChangedObserver);

    public BaseAdapter getCheckoutItemAdapter(final Context context, String store, DataSetObserver dataChangedObserver);

}
