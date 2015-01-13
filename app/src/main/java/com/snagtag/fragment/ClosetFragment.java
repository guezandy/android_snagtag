package com.snagtag.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.parse.ParseImageView;
import com.snagtag.R;
import com.snagtag.models.TagHistoryItem;
import com.snagtag.service.IParseCallback;
import com.snagtag.service.ParseService;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;

/**
 * Shows items already purchased.
 *
 * Created by benjamin on 10/7/14.
 */
public class ClosetFragment extends Fragment {
    private View mView;
    private View mItemDetailPopup;

    ViewPager mTopsView;

    private TextView mItemDescription;
    private TextView mItemColor;
    private TextView mItemSize;
    private TextView mItemCost;
    private TextView mItemHistory;
    private ParseImageView mItemDetailImage;

    private View mButtonReorder;
    private View mHeaderFilter;
    private View mFilterOpenIndicator;
    private View mFilterOptions;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_closet, container, false);
        mItemDetailPopup = mView.findViewById(R.id.item_detail_popup);

        mTopsView = (ViewPager) mView.findViewById(R.id.scroll_tops);
        ViewPager bottomsView = (ViewPager) mView.findViewById(R.id.scroll_bottoms);
        ViewPager shoesView = (ViewPager) mView.findViewById(R.id.scroll_shoes);

        mTopsView.setAdapter(new TagHistoryItemPagerAdapter(TagHistoryItemPagerAdapter.TOPS));
        bottomsView.setAdapter(new TagHistoryItemPagerAdapter((TagHistoryItemPagerAdapter.BOTTOMS)));
        shoesView.setAdapter(new TagHistoryItemPagerAdapter((TagHistoryItemPagerAdapter.SHOES)));

        mItemDescription = (TextView)mView.findViewById(R.id.item_description);
        mItemColor = (TextView)mView.findViewById(R.id.item_color);
        mItemSize = (TextView)mView.findViewById(R.id.item_size);
        mItemCost = (TextView)mView.findViewById(R.id.item_cost);
        mItemHistory = (TextView)mView.findViewById(R.id.item_history);
        mItemDetailImage = (ParseImageView)mView.findViewById(R.id.item_image);

        mButtonReorder = mView.findViewById(R.id.button_reorder);
        mHeaderFilter = mView.findViewById(R.id.header_filter);
        mFilterOpenIndicator = mView.findViewById(R.id.filter_open_indicator);
        mFilterOptions = mView.findViewById(R.id.filter_options);
        setClickListeners();

        return mView;
    }

    private void setClickListeners() {
        mItemDetailPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemDetailPopup.setVisibility(View.GONE);
            }
        });
        mButtonReorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Go to order screen?
            }
        });
        mHeaderFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(mFilterOptions.getVisibility()==View.GONE) {
                   Animation animation = AnimationUtils.loadAnimation(ClosetFragment.this.getActivity(), R.anim.in_from_bottom);
                   mFilterOptions.setVisibility(View.VISIBLE);
                   mHeaderFilter.startAnimation(animation);
                   mFilterOpenIndicator.setRotation(0);
               } else {
                   Animation animation = AnimationUtils.loadAnimation(ClosetFragment.this.getActivity(), R.anim.out_to_bottom);
                   animation.setAnimationListener(new Animation.AnimationListener() {
                       @Override
                       public void onAnimationStart(Animation animation) {

                       }

                       @Override
                       public void onAnimationEnd(Animation animation) {
                           mFilterOptions.setVisibility(View.GONE);
                           mFilterOpenIndicator.setRotation(180);
                       }

                       @Override
                       public void onAnimationRepeat(Animation animation) {

                       }
                   });
                   mHeaderFilter.startAnimation(animation);
               }
            }
        });
    }

    private void showDetail(TagHistoryItem selectedItem) {
        mItemDetailPopup.setVisibility(View.VISIBLE);
        mItemHistory.setText("history not in object");
        mItemColor.setText("color not in object");
        NumberFormat format = NumberFormat.getCurrencyInstance();
        mItemCost.setText(format.format(selectedItem.getPrice()));
        mItemDescription.setText(selectedItem.getDescription());
        mItemSize.setText("size not in object");
        mItemDetailImage.setParseFile(selectedItem.getImage());
        mItemDetailImage.loadInBackground();

    }

    /**
     * PagerAdapter to show the outfits in my closet.  I don't know if there is a ParsePagerAdapter, so I'm making one here.
     * I am creating a method in the service to 'get' the TagHistoryItems.  This is the pattern I would prefer to use for ListAdapters as well.
     */
    class TagHistoryItemPagerAdapter extends PagerAdapter implements IParseCallback<List<TagHistoryItem>>{
        static final int TOPS = 0;
        static final int BOTTOMS = 1;
        static final int SHOES = 2;

        TagHistoryItemPagerAdapter(int itemType) {
            switch (itemType) {
                case TOPS:
                    new ParseService(getActivity().getApplicationContext()).getClosetTops(getActivity().getApplicationContext(), this);
                    break;
                case BOTTOMS:
                    new ParseService(getActivity().getApplicationContext()).getClosetBottoms(getActivity().getApplicationContext(), this);
                    break;
                case SHOES:
                    new ParseService(getActivity().getApplicationContext()).getClosetShoes(getActivity().getApplicationContext(), this);
                    break;
            }
        }


        // As per docs, you may use views as key objects directly
        // if they aren't too complex
        private List<TagHistoryItem> mItems = Collections.emptyList();

        @Override
        public void onSuccess(List<TagHistoryItem> items) {
            mItems = items;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTopsView.invalidate();
                    TagHistoryItemPagerAdapter.this.notifyDataSetChanged();
                }
            });

        }

        @Override
        public void onFail(String message) {

        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            LayoutInflater inflater = LayoutInflater.from(getActivity().getApplicationContext());
            View view = inflater.inflate(R.layout.row_item_closet_view, null);
            final TagHistoryItem selectedItem = mItems.get(position);
            ((ParseImageView)view.findViewById(R.id.item_image)).setParseFile(selectedItem.getImage());
            ((ParseImageView)view.findViewById(R.id.item_image)).loadInBackground();


            selectedItem.setDescription("Test item " + position);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    showDetail(selectedItem);
                }
            });
            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        // Important: page takes all available width by default,
        // so let's override this method to fit 5 pages within single screen
        @Override
        public float getPageWidth(int position) {
            return 0.33f;
        }
    }


}
