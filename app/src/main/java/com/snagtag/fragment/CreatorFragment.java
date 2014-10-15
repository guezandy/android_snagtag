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
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.parse.ParseImageView;
import com.snagtag.R;
import com.snagtag.animation.HideAnimation;
import com.snagtag.animation.ShowAnimation;
import com.snagtag.models.TagHistoryItem;
import com.snagtag.scroll.CenteringHorizontalScrollView;
import com.snagtag.service.IParseCallback;
import com.snagtag.service.IParseService;
import com.snagtag.service.MockParseService;

import java.util.List;

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

    private LinearLayout topsView;
    private LinearLayout bottomsView;
    private LinearLayout shoesView;

    private CenteringHorizontalScrollView topsScrollView;
    private CenteringHorizontalScrollView bottomsScrollView;
    private CenteringHorizontalScrollView shoesScrollView;

    private ParseImageView topImageView;
    private ParseImageView bottomImageView;
    private ParseImageView shoesImageView;

    private View buttonCart;
    private View buttonSave;
    private View buttonCloset;


    //Lists of items
    List<TagHistoryItem>tops;
    List<TagHistoryItem>bottoms;
    List<TagHistoryItem>shoes;

    private TagHistoryItem selectedTop;
    private TagHistoryItem selectedShoes;
    private TagHistoryItem selectedBottom;

    private boolean outfitCreatorOpen = false;

    private static final int OUTFIT_LIST = 0;
    private static final int OUTFIT_VIEW = 1;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        mCreatorView = (ViewFlipper) inflater.inflate(
                R.layout.fragment_creator, container, false);
        mBottomSlider = (LinearLayout) mCreatorView.findViewById(R.id.bottom_slider);
        mOutfitSelectSlider = (LinearLayout) mCreatorView.findViewById(R.id.outfit_select_slider);
        mOutfitGrid = (GridView) mCreatorView.findViewById(R.id.grid_outfit);
        mButtonNewOutfit = mCreatorView.findViewById(R.id.button_new);
        selectorChevron = mCreatorView.findViewById(R.id.selector_chevron);

        topsScrollView = (CenteringHorizontalScrollView)mCreatorView.findViewById(R.id.tops_scroll_view);
        bottomsScrollView = (CenteringHorizontalScrollView)mCreatorView.findViewById(R.id.bottoms_scroll_view);
        shoesScrollView = (CenteringHorizontalScrollView)mCreatorView.findViewById(R.id.shoes_scroll_view);

        topImageView = (ParseImageView)mCreatorView.findViewById(R.id.image_top_selected);
        bottomImageView = (ParseImageView)mCreatorView.findViewById(R.id.image_bottom_selected);
        shoesImageView = (ParseImageView)mCreatorView.findViewById(R.id.image_shoe_selected);

        buttonCart = mCreatorView.findViewById(R.id.button_cart);
        buttonSave = mCreatorView.findViewById(R.id.button_snag);
        buttonCloset = mCreatorView.findViewById(R.id.button_closet);

        IParseService service = new MockParseService();
        mOutfitGrid.setAdapter(service.getOutfitItemAdapter(getActivity().getApplicationContext(), null, null));

        setupClosetScrollers();
        setOnClickListeners();
        return mCreatorView;
    }


    private void setupClosetScrollers() {
        topsView = (LinearLayout) mCreatorView.findViewById(R.id.tops_view);
        new MockParseService().getTops(getActivity().getApplicationContext(), new IParseCallback<List<TagHistoryItem>>() {
            @Override
            public void onSuccess(List<TagHistoryItem> items) {
                tops = items;
                setItemsInScroll(topsView, items, "You have no tops in your closet or snags.");

            }

            @Override
            public void onFail(String message) {

            }
        });
        topsScrollView.setOnScrollStoppedListener(new CenteringHorizontalScrollView.OnScrollStoppedListener() {
            @Override
            public void onScrollStopped(View view, int index) {
                selectedTop = tops.get(index-1);
                topImageView.setParseFile(selectedTop.getImage());
                topImageView.loadInBackground();

            }
        });


        bottomsView = (LinearLayout) mCreatorView.findViewById(R.id.bottoms_view);
        new MockParseService().getBottoms(getActivity().getApplicationContext(), new IParseCallback<List<TagHistoryItem>>() {
            @Override
            public void onSuccess(List<TagHistoryItem> items) {
                bottoms = items;
                setItemsInScroll(bottomsView, items, "You have no bottoms or dresses in your closet or snags.");

            }

            @Override
            public void onFail(String message) {

            }
        });

        bottomsScrollView.setOnScrollStoppedListener(new CenteringHorizontalScrollView.OnScrollStoppedListener() {
            @Override
            public void onScrollStopped(View view, int index) {
                selectedBottom = tops.get(index-1);
                bottomImageView.setParseFile(selectedBottom.getImage());
                bottomImageView.loadInBackground();

            }
        });

        shoesView = (LinearLayout) mCreatorView.findViewById(R.id.shoes_view);
        new MockParseService().getShoes(getActivity().getApplicationContext(), new IParseCallback<List<TagHistoryItem>>() {
            @Override
            public void onSuccess(List<TagHistoryItem> items) {
                shoes = items;
                setItemsInScroll(shoesView, items, "You have no shoes in your closet or snags.");
            }

            @Override
            public void onFail(String message) {

            }
        });

        shoesScrollView.setOnScrollStoppedListener(new CenteringHorizontalScrollView.OnScrollStoppedListener() {
            @Override
            public void onScrollStopped(View view, int index) {
                selectedShoes = tops.get(index-1);
                shoesImageView.setParseFile(selectedShoes.getImage());
                shoesImageView.loadInBackground();
            }
        });

    }


    /**
     * Adds the items to the scrollview.
     *
     * @param view The Layout inside the Horizontal scroller
     * @param items The list of Tags
     * @param emptyText Message to display if the list is empty
     */
    private void setItemsInScroll(LinearLayout view, List<TagHistoryItem> items, String emptyText) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        if (items.size() == 0) {
            TextView empty = new TextView(getActivity());
            empty.setText("You have no snags or shoes in your closet.");
            //add a blank view to start.
            view.addView(inflater.inflate(R.layout.row_item_creator_view_empty, view, false));
            view.addView(empty);
            view.addView(inflater.inflate(R.layout.row_item_creator_view_empty, view, false));
            return;
        }
        int i = 1;

        //add a blank view to start.
        view.addView(inflater.inflate(R.layout.row_item_creator_view_empty, view, false));

        for (TagHistoryItem item : items) {
            ViewGroup itemView = (ViewGroup) inflater.inflate(R.layout.row_item_creator_view, view, false);
            ParseImageView iv = (ParseImageView) itemView.findViewById(R.id.item_image);
            iv.setParseFile(item.getImage());
            iv.loadInBackground();
            ((TextView) itemView.findViewById(R.id.item_count)).setText(i + "/" + items.size());
            i++;
            view.addView(itemView);
        }
        //add a blank views to end.
        view.addView(inflater.inflate(R.layout.row_item_creator_view_empty, view, false));
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

        buttonCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedTop != null && !selectedTop.getInCloset() ) {
                        //TODO:Add top to closet
                }
                if(selectedBottom != null && !selectedBottom.getInCloset()) {
                    //TODO:Add bottom to closet
                }
                if(selectedShoes != null && selectedShoes.getInCloset()) {
                    //TODO:Add shoes to closet
                }
                buttonCart.setEnabled(false);
            }
        });
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: grab selected items, add to Outfit and save....
                closeBottomSlider();
                mCreatorView.setDisplayedChild(OUTFIT_LIST);
            }
        });
    }

    private void closeBottomSlider() {
        Animation anim = new HideAnimation(mOutfitSelectSlider, (int) getResources().getDimension(R.dimen.outfit_selector_height));
        outfitCreatorOpen = false;
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

        outfitCreatorOpen = true;
        mOutfitSelectSlider.startAnimation(anim);
        selectorChevron.setRotation(0);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}