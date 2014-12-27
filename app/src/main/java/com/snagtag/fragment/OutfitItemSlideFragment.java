package com.snagtag.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseImageView;
import com.snagtag.R;
import com.snagtag.models.OutfitItem;


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
        TextView text = (TextView) rootView.findViewById(R.id.text_outfit_name);
        text.setText(getOutfitDescription(theOutfit));

        ParseImageView topImage = (ParseImageView) rootView.findViewById(R.id.image_top);
        ParseImageView bottomImage = (ParseImageView) rootView.findViewById(R.id.image_bottom);
        ParseImageView shoesImage = (ParseImageView) rootView.findViewById(R.id.image_shoes);

        topImage.setParseFile(theOutfit.getTopImage());
        topImage.loadInBackground();
        bottomImage.setParseFile(theOutfit.getBottomImage());
        bottomImage.loadInBackground();
        shoesImage.setParseFile(theOutfit.getShoesImage());
        shoesImage.loadInBackground();

        return rootView;
    }

    public String getOutfitDescription(OutfitItem item) {
        return (item.getOutfitTitle()+ "\nContains:\n"+
                "Top: " +item.getTopDescription()+"\n"+
                "Bottom: "+item.getBottomDescription()+"\n"+
                "Shoes: "+item.getShoesDescription()+"\n"
        );
    }
}