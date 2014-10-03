package com.snagtag.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;


import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.snagtag.R;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.snagtag.models.TagHistoryItem;

/*
 * The TagHistoryAdapter is an extension of ParseQueryAdapter
 * that has a custom layout for favorite TagHistoryItems
 */

public class TagHistoryAdapter extends ParseQueryAdapter<TagHistoryItem> {

    public TagHistoryAdapter(Context context) {
        super(context, new ParseQueryAdapter.QueryFactory<TagHistoryItem>() {

            public ParseQuery<TagHistoryItem> create() {
                // Here we can configure a ParseQuery to display
                // only top-rated TagHistoryItems.
                ParseQuery query = new ParseQuery("TagHistoryItem");
                ParseUser user = ParseUser.getCurrentUser();
                if(/*No Internet connection ||*/user != null) {
                    //grab tag history from phone
                    query.fromLocalDatastore();
                } else {
                    //grab tag history from the web
                    ParseRelation relation = user.getRelation("user_tags");
                    query = relation.getQuery();
                }
                return query;
            }
        });
    }

    @Override
    public View getItemView(final TagHistoryItem item, View v,
                            ViewGroup parent) {

        if (v == null) {
            v = View.inflate(getContext(), R.layout.row_item_clothing_view,
                    null);
        }

        super.getItemView(item, v, parent);
//TODO: finish adding formatting for the row layout
        TextView description = (TextView) v.findViewById(R.id.item_description);
        description.setText(item.getDescription());

        return v;
    }


}
