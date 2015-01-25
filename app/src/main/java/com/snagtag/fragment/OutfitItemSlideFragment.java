package com.snagtag.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseUser;
import com.snagtag.MainActivity;
import com.snagtag.R;
import com.snagtag.models.OutfitItem;
import com.snagtag.ui.IconCustomTextView;


public class OutfitItemSlideFragment extends Fragment {
    OutfitItem theOutfit;

    public static OutfitItemSlideFragment newInstance(OutfitItem content) {
        OutfitItemSlideFragment fragment = new OutfitItemSlideFragment();
        fragment.theOutfit = content;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_outfit_slide, container, false);
        TextView title = (TextView) rootView.findViewById(R.id.text_outfit_name);
        title.setText(theOutfit.getOutfitTitle());

        ParseImageView topImage = (ParseImageView) rootView.findViewById(R.id.image_top);
        ParseImageView bottomImage = (ParseImageView) rootView.findViewById(R.id.image_bottom);
        ParseImageView shoesImage = (ParseImageView) rootView.findViewById(R.id.image_shoes);
        ParseImageView accImage = (ParseImageView) rootView.findViewById(R.id.image_acc);
        topImage.setParseFile(theOutfit.getTopImage());
        topImage.loadInBackground();
        bottomImage.setParseFile(theOutfit.getBottomImage());
        bottomImage.loadInBackground();
        shoesImage.setParseFile(theOutfit.getShoesImage());
        shoesImage.loadInBackground();
        accImage.setParseFile(theOutfit.getAccImage());
        accImage.loadInBackground();

        IconCustomTextView delete = (IconCustomTextView) rootView.findViewById(R.id.button_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                theOutfit.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        ParseUser.getCurrentUser().increment("outfit_count", -1);
                        ParseUser.getCurrentUser().saveInBackground();
                        ((MainActivity) getActivity()).replaceFragment(new CreatorFragment2(), true, FragmentTransaction.TRANSIT_FRAGMENT_FADE, getString(R.string.title_section_outfits));
                    }
                });
            }
        });
        return rootView;
    }

}