package com.snagtag.grid;

/**
 * Created by benjamin on 10/8/14.
 */

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.GridView;

public class FittingGridView extends GridView {
    public FittingGridView(Context context) {
        super(context);
    }

    public FittingGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FittingGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSpec = heightMeasureSpec;
        //IF the spec is to wrap content, set it to 1/2 the size of the screen instead...
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        if (getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
            heightSpec = MeasureSpec.makeMeasureSpec(height/2, MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}