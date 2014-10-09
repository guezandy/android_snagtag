package com.snagtag.service;


import android.content.Context;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Interface to define interactions with Parse.
 *
 * Created by benjamin on 9/19/14.
 */
public interface IParseService {

    public List<String> getStoresByTags(Context context);

    public BaseAdapter getTagHistoryAdapter(Context context, String store);

}
