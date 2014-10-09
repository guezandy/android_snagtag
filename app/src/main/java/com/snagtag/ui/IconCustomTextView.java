package com.snagtag.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.snagtag.utils.FontAwesome;

/**
 * Created by benjamin on 10/9/14.
 */
public class IconCustomTextView extends TextView {

    public IconCustomTextView(Context context) {
        super(context);
        setTypeface(FontAwesome.getInstance(context).getTypeFace());
    }

    public IconCustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(FontAwesome.getInstance(context).getTypeFace());
    }

    public IconCustomTextView(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
        setTypeface(FontAwesome.getInstance(context).getTypeFace());
    }

}