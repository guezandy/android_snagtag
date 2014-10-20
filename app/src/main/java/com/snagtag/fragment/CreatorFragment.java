package com.snagtag.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.snagtag.R;
import com.snagtag.animation.HideAnimation;
import com.snagtag.animation.ShowAnimation;
import com.snagtag.models.OutfitItem;
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
    private static final int OUTFIT_LIST = 0;
    private static final int OUTFIT_VIEW = 1;

    private ViewFlipper mCreatorView;
    private LinearLayout mBottomSlider;
    private LinearLayout mOutfitSelectSlider;
    private GridView mOutfitGrid;
    private View mButtonNewOutfit;
    private View mSelectorChevron;

    private LinearLayout mTopsView;
    private LinearLayout mBottomsView;
    private LinearLayout mShoesView;

    private CenteringHorizontalScrollView mTopsScrollView;
    private CenteringHorizontalScrollView mBottomsScrollView;
    private CenteringHorizontalScrollView mShoesScrollView;

    private ParseImageView mTopImageView;
    private ParseImageView mBottomImageView;
    private ParseImageView mShoesImageView;

    private View mButtonCart;
    private View mButtonSave;
    private View mButtonAddToCart;

    private TextView mItemStore;
    private TextView mItemDescription;
    private TextView mItemColor;
    private TextView mItemCost;
    private TextView mItemSize;
    private TextView mOutfitName;

    private View mSnagIndicatorTop;
    private View mSnagIndicatorBottom;
    private View mSnagIndicatorShoes;

    private View mItemDetailPopup;

    //Lists of items
    List<TagHistoryItem> mTops;
    List<TagHistoryItem> mBottoms;
    List<TagHistoryItem> mShoes;

    private TagHistoryItem mSelectedTop;
    private TagHistoryItem mSelectedShoes;
    private TagHistoryItem mSelectedBottom;

    private boolean mOutfitCreatorOpen = false;


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
        mSelectorChevron = mCreatorView.findViewById(R.id.selector_chevron);

        mTopsScrollView = (CenteringHorizontalScrollView) mCreatorView.findViewById(R.id.tops_scroll_view);
        mBottomsScrollView = (CenteringHorizontalScrollView) mCreatorView.findViewById(R.id.bottoms_scroll_view);
        mShoesScrollView = (CenteringHorizontalScrollView) mCreatorView.findViewById(R.id.shoes_scroll_view);

        mTopImageView = (ParseImageView) mCreatorView.findViewById(R.id.image_top_selected);
        mBottomImageView = (ParseImageView) mCreatorView.findViewById(R.id.image_bottom_selected);
        mShoesImageView = (ParseImageView) mCreatorView.findViewById(R.id.image_shoe_selected);

        mButtonCart = mCreatorView.findViewById(R.id.button_cart);
        mButtonSave = mCreatorView.findViewById(R.id.button_snag);
        mButtonAddToCart = mCreatorView.findViewById(R.id.button_add_item);

        mSnagIndicatorBottom = mCreatorView.findViewById(R.id.snag_indicator_bottom);
        mSnagIndicatorShoes = mCreatorView.findViewById(R.id.snag_indicator_shoes);
        mSnagIndicatorTop = mCreatorView.findViewById(R.id.snag_indicator_top);

        mItemStore = (TextView) mCreatorView.findViewById(R.id.item_store);
        mItemDescription = (TextView) mCreatorView.findViewById(R.id.item_description);
        mItemColor = (TextView) mCreatorView.findViewById(R.id.item_color);
        mItemCost = (TextView) mCreatorView.findViewById(R.id.item_cost);
        mItemSize = (TextView) mCreatorView.findViewById(R.id.item_size);
        mOutfitName = (TextView) mCreatorView.findViewById(R.id.input_outfit_name);
        mItemDetailPopup = mCreatorView.findViewById(R.id.item_detail_popup);

        IParseService service = new MockParseService(getActivity().getApplicationContext());


        mOutfitGrid.setAdapter(service.getOutfitItemAdapter(getActivity(), null, null));

        setupClosetScrollers();
        setOnClickListeners();
        return mCreatorView;
    }


    private void setupClosetScrollers() {
        mTopsView = (LinearLayout) mCreatorView.findViewById(R.id.tops_view);
        new MockParseService(getActivity().getApplicationContext()).getTops(getActivity().getApplicationContext(), new IParseCallback<List<TagHistoryItem>>() {
            @Override
            public void onSuccess(List<TagHistoryItem> items) {
                mTops = items;
                setItemsInScroll(mTopsView, items, "You have no mTops in your closet or snags.");

            }

            @Override
            public void onFail(String message) {

            }
        });
        mTopsScrollView.setOnScrollStoppedListener(new CenteringHorizontalScrollView.OnScrollStoppedListener() {
            @Override
            public void onScrollStopped(View view, int index) {
                mSelectedTop = mTops.get(index - 1);
                setSelected(mSnagIndicatorTop, mTopImageView, mSelectedTop);

            }
        });


        mBottomsView = (LinearLayout) mCreatorView.findViewById(R.id.bottoms_view);
        new MockParseService(getActivity().getApplicationContext()).getBottoms(getActivity().getApplicationContext(), new IParseCallback<List<TagHistoryItem>>() {
            @Override
            public void onSuccess(List<TagHistoryItem> items) {
                mBottoms = items;
                setItemsInScroll(mBottomsView, items, "You have no mBottoms or dresses in your closet or snags.");

            }

            @Override
            public void onFail(String message) {

            }
        });

        mBottomsScrollView.setOnScrollStoppedListener(new CenteringHorizontalScrollView.OnScrollStoppedListener() {
            @Override
            public void onScrollStopped(View view, int index) {
                mSelectedBottom = mTops.get(index - 1);
                mBottomImageView.setParseFile(mSelectedBottom.getImage());
                mBottomImageView.loadInBackground();

            }
        });

        mShoesView = (LinearLayout) mCreatorView.findViewById(R.id.shoes_view);
        new MockParseService(getActivity().getApplicationContext()).getShoes(getActivity().getApplicationContext(), new IParseCallback<List<TagHistoryItem>>() {
            @Override
            public void onSuccess(List<TagHistoryItem> items) {
                mShoes = items;
                setItemsInScroll(mShoesView, items, "You have no mShoes in your closet or snags.");
            }

            @Override
            public void onFail(String message) {

            }
        });

        mShoesScrollView.setOnScrollStoppedListener(new CenteringHorizontalScrollView.OnScrollStoppedListener() {
            @Override
            public void onScrollStopped(View view, int index) {
                mSelectedShoes = mTops.get(index - 1);
                mShoesImageView.setParseFile(mSelectedShoes.getImage());
                mShoesImageView.loadInBackground();
            }
        });

    }

    /**
     * Sets the view states for selecting an item
     *
     * @param tagIndicator The view for the tag indicator
     * @param imageView    The view for the clothing image
     * @param historyItem  The item selected
     */
    private void setSelected(View tagIndicator, ParseImageView imageView, TagHistoryItem historyItem) {
        if (!historyItem.getInCloset()) {
            tagIndicator.setVisibility(View.VISIBLE);
        }
        imageView.setParseFile(historyItem.getImage());
        imageView.loadInBackground();
    }

    private void setSelected(View tagIndicator, ParseImageView imageView, ParseFile image, boolean inCloset, CenteringHorizontalScrollView scroller, int scrollIndex) {
        if (!inCloset) {
            tagIndicator.setVisibility(View.VISIBLE);
        }
        imageView.setParseFile(image);
        imageView.loadInBackground();
        float offset = getResources().getDimension(R.dimen.item_selector_width) * scrollIndex;
        scroller.setScrollX((int) offset);
        scroller.setCenter();

    }

    /**
     * Adds the items to the scrollview.
     *
     * @param view      The Layout inside the Horizontal scroller
     * @param items     The list of Tags
     * @param emptyText Message to display if the list is empty
     */
    private void setItemsInScroll(final LinearLayout view, final List<TagHistoryItem> items, final String emptyText) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                if (items.size() == 0) {
                    TextView empty = new TextView(getActivity());
                    empty.setText(emptyText);
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
        });


    }


    private void setOnClickListeners() {

        //hook up animation for bottom slider.
        mBottomSlider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOutfitCreatorOpen) {
                    closeBottomSlider();
                } else {
                    openBottomSlider();
                }
            }
        });

        mButtonNewOutfit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //make sure existing items are zeroed out.
                mSelectedTop = null;
                mSelectedBottom = null;
                mSelectedShoes = null;
                mTopImageView.setParseFile(null);
                mBottomImageView.setParseFile(null);
                mShoesImageView.setParseFile(null);

                mShoesScrollView.setScrollX(0);
                mBottomsScrollView.setScrollX(0);
                mTopsScrollView.setScrollX(0);
                mOutfitName.setText("");

                mCreatorView.setDisplayedChild(OUTFIT_VIEW);
                openBottomSlider();
            }
        });

        mButtonCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedTop != null && !mSelectedTop.getInCloset()) {
                    //TODO:Add top to closet
                }
                if (mSelectedBottom != null && !mSelectedBottom.getInCloset()) {
                    //TODO:Add bottom to closet
                }
                if (mSelectedShoes != null && mSelectedShoes.getInCloset()) {
                    //TODO:Add shoes to closet
                }
                mButtonCart.setEnabled(false);
            }
        });
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: grab selected items, add to Outfit and save...

                closeBottomSlider();
                mCreatorView.setDisplayedChild(OUTFIT_LIST);
            }
        });
        mTopImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDetail(mSelectedTop);
            }
        });
        mBottomImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDetail(mSelectedBottom);
            }
        });
        mShoesImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDetail(mSelectedShoes);
            }
        });
        mItemDetailPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemDetailPopup.setVisibility(View.GONE);
            }
        });

        mButtonAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Add selected item to cart;
                mItemDetailPopup.setVisibility(View.GONE);

            }
        });

        mOutfitGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                OutfitItem item = (OutfitItem) adapterView.getItemAtPosition(i);
                mOutfitName.setText(item.getOutfitTitle());
                if (item.getTopImage() != null) {
                    setSelected(mSnagIndicatorTop, mTopImageView, item.getTopImage(), item.getTopInCloset(), mTopsScrollView, i);
                } else {
                    mTopsScrollView.setScrollX(0);
                }
                if (item.getBottomImage() != null) {
                    setSelected(mSnagIndicatorBottom, mBottomImageView, item.getBottomImage(), item.getBottomInCloset(), mBottomsScrollView, i);
                } else {
                    mBottomsScrollView.setScrollX(0);
                }
                if (item.getShoesImage() != null) {
                    setSelected(mSnagIndicatorShoes, mShoesImageView, item.getShoesImage(), item.getShoesInCloset(), mShoesScrollView, i);
                } else {
                    mShoesScrollView.setScrollX(0);
                }

                mCreatorView.setDisplayedChild(OUTFIT_VIEW);
                openBottomSlider();


            }
        });
    }

    private void showDetail(TagHistoryItem item) {
        if (item != null) {
            mItemStore.setText(item.getStore());
            mItemDescription.setText(item.getDescription());
            mItemColor.setText(item.getString("color"));
            mItemCost.setText(item.getPrice().toString());
            mItemSize.setText(item.getString("size"));
            mItemDetailPopup.setVisibility(View.VISIBLE);
        }
    }

    private void closeBottomSlider() {
        Animation anim = new HideAnimation(mOutfitSelectSlider, (int) getResources().getDimension(R.dimen.outfit_selector_height));
        mOutfitCreatorOpen = false;
        mOutfitSelectSlider.startAnimation(anim);
        mSelectorChevron.setRotation(180);
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

        mOutfitCreatorOpen = true;
        mOutfitSelectSlider.startAnimation(anim);
        mSelectorChevron.setRotation(0);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}