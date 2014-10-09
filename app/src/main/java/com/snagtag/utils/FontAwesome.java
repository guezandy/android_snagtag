package com.snagtag.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by benjamin on 10/9/14.
 */
public class FontAwesome {

    private static FontAwesome instance;
    private static Typeface typeface;

    public FontAwesome() {

    }

    public static FontAwesome getInstance(Context context) {
        synchronized (FontAwesome.class) {
            if (instance == null) {
                instance = new FontAwesome();
                typeface = Typeface.createFromAsset(context.getResources().getAssets(), "FontAwesome.otf");
            }
            return instance;
        }
    }

    public Typeface getTypeFace() {
        return typeface;
    }
}
