package com.snagtag.fragment;

import android.os.Bundle;
import android.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.ParseImageView;
import com.snagtag.R;

public final class LaunchViewPagerFragment extends android.support.v4.app.Fragment {
    private static final String KEY_CONTENT = "TestFragment:Content";

    public static android.support.v4.app.Fragment newInstance(String content) {
        LaunchViewPagerFragment fragment = new LaunchViewPagerFragment();

        fragment.mContent = (content);
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

        TextView text = new TextView(getActivity());
        text.setGravity(Gravity.CENTER_HORIZONTAL);
        text.setText(mContent);
        text.setTextSize(12 * getResources().getDisplayMetrics().density);
        text.setPadding(1, 1, 1, 1);
        text.setTextColor(getResources().getColor(R.color.white));

/*        ParseImageView image = new ParseImageView(getActivity());
        image.setImageResource(R.drawable.snaglogo);
        image.loadInBackground();*/

        RelativeLayout layout = new RelativeLayout(getActivity());
        layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        layout.setGravity(Gravity.BOTTOM);
        layout.setPadding(50,50,50,50);
        layout.addView(text);
        //layout.addView(image);

        return layout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }
}
