package com.snagtag.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import com.snagtag.R;
import com.snagtag.animation.HideAnimation;
import com.snagtag.animation.ShowAnimation;

/**
 * Fragment to house the Closet of a user.
 *
 * Created by benjamin on 9/22/14.
 */
public class CreatorFragment extends Fragment {

    private ViewGroup mClosetView;
    private LinearLayout mBottomSlider;
    private LinearLayout mOutfitSelectSlider;

    private boolean outfitCreatorOpen = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mClosetView = (ViewGroup) inflater.inflate(
                R.layout.fragment_creator, container, false);
        mBottomSlider = (LinearLayout)mClosetView.findViewById(R.id.bottom_slider);
        mOutfitSelectSlider = (LinearLayout)mClosetView.findViewById(R.id.outfit_select_slider);
        //hook up animation for bottom slider.
        mBottomSlider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = null;
                if(outfitCreatorOpen) {
                    anim = new HideAnimation(mOutfitSelectSlider,(int) getResources().getDimension(R.dimen.outfit_selector_height));

                    outfitCreatorOpen = !outfitCreatorOpen;
                } else {
                    anim = new ShowAnimation(mOutfitSelectSlider, (int) getResources().getDimension(R.dimen.outfit_selector_height));
                    ViewGroup.LayoutParams p = mOutfitSelectSlider.getLayoutParams();
                    p.height=0;
                    mOutfitSelectSlider.setLayoutParams(p);
                    mOutfitSelectSlider.setVisibility(View.VISIBLE);

                    outfitCreatorOpen = !outfitCreatorOpen;
                }
                mOutfitSelectSlider.startAnimation(anim);
            }
        });

        return mClosetView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}