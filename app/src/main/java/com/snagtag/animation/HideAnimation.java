package com.snagtag.animation;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

public class HideAnimation extends Animation {

    private final View mContent;
    private int mHeight;

    public HideAnimation(final View content, int height) {
        mContent = content;
        //mContent.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mHeight = height;

        setDuration(300);
        setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                content.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) mContent.getLayoutParams();

        lp.height = mHeight - (int)(mHeight * interpolatedTime);
        Log.d("TIME", "t " + interpolatedTime + " height " + lp.height + " mHeight " + mHeight);
        mContent.setLayoutParams(lp);

    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}