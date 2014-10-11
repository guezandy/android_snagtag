package com.snagtag.grid;

/**
 * Created by benjamin on 10/8/14.
 */

import android.content.Context;
import android.util.AttributeSet;
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

        if (getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
            heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        }

        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}