package com.snagtag.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

import com.snagtag.R;
import com.snagtag.animation.HideAnimation;
import com.snagtag.animation.ShowAnimation;
import com.snagtag.service.IParseService;
import com.snagtag.service.MockParseService;

/**
 * Fragment to house the Closet of a user.
 * <p/>
 * Created by benjamin on 9/22/14.
 */
public class CreatorFragment extends Fragment {

    private ViewFlipper mCreatorView;
    private LinearLayout mBottomSlider;
    private LinearLayout mOutfitSelectSlider;
    private GridView mOutfitGrid;
    private View mButtonNewOutfit;
    private View selectorChevron;

    private boolean outfitCreatorOpen = false;

    private static final int OUTFIT_LIST = 0;
    private static final int OUTFIT_VIEW = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mCreatorView = (ViewFlipper) inflater.inflate(
                R.layout.fragment_creator, container, false);
        mBottomSlider = (LinearLayout) mCreatorView.findViewById(R.id.bottom_slider);
        mOutfitSelectSlider = (LinearLayout) mCreatorView.findViewById(R.id.outfit_select_slider);
        mOutfitGrid = (GridView)mCreatorView.findViewById(R.id.grid_outfit);
        mButtonNewOutfit = mCreatorView.findViewById(R.id.button_new);
        selectorChevron = mCreatorView.findViewById(R.id.selector_chevron);

        IParseService service = new MockParseService();
        mOutfitGrid.setAdapter(service.getOutfitItemAdapter(getActivity().getApplicationContext(), null, null));

        setOnClickListeners();
        return mCreatorView;
    }


    private void setOnClickListeners() {

        //hook up animation for bottom slider.
        mBottomSlider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (outfitCreatorOpen) {
                    closeBottomSlider();
                } else {
                    openBottomSlider();

                }
            }
        });

        mButtonNewOutfit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCreatorView.setDisplayedChild(OUTFIT_VIEW);
                openBottomSlider();
            }
        });
    }

    private void closeBottomSlider() {
        Animation anim = new HideAnimation(mOutfitSelectSlider, (int) getResources().getDimension(R.dimen.outfit_selector_height));
        outfitCreatorOpen = !outfitCreatorOpen;
        mOutfitSelectSlider.startAnimation(anim);
        selectorChevron.setRotation(180);
    }

    /**
     * Encapsulates code to open bottom slider, since this will likely be called from a couple of different places.
     */
    private void openBottomSlider() {
        Animation anim = new ShowAnimation(mOutfitSelectSlider, (int) getResources().getDimension(R.dimen.outfit_selector_height));
        ViewGroup.LayoutParams p = mOutfitSelectSlider.getLayoutParams();
        p.height = 0;
        mOutfitSelectSlider.setLayoutParams(p);
        mOutfitSelectSlider.setVisibility(View.VISIBLE);

        outfitCreatorOpen = !outfitCreatorOpen;
        mOutfitSelectSlider.startAnimation(anim);
        selectorChevron.setRotation(0);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}