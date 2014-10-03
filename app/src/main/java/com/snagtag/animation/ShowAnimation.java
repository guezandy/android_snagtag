package com.snagtag.animation;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ShowAnimation extends Animation {

    private final View mContent;
    private int mHeight;
    private int mDeltaHeight;
    public ShowAnimation( final View content, int withLayoutParam, int heightLayoutParam) {
        mContent = content;
        mContent.measure(withLayoutParam, heightLayoutParam);
        mHeight = content.getMeasuredHeight();

        setDuration(200);
        setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                content.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public ShowAnimation( final View content, int height) {
        mContent = content;

        mHeight = height;

        setDuration(200);
        setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                content.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) mContent.getLayoutParams();

        lp.height = (int)(mHeight * interpolatedTime);
        Log.d("TIME", "t " + interpolatedTime + " height " + lp.height + " mHeight " + mHeight);
        mContent.setLayoutParams(lp);

    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}