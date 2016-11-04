package com.pacificfjord.pfapi.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.pacificfjord.pfapi.R;
import com.pacificfjord.pfapi.TCSAppInstance;
import com.squareup.picasso.Picasso;

/**
 * Created by Aaron Vega on 2/4/15.
 */
public class TCSTitleBar extends RelativeLayout implements TCSSkinnableView {
    public TCSTitleBar(Context context) {
        super(context);
        setSkin();
    }

    public TCSTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSkin();
    }

    public TCSTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setSkin();
    }

    @Override
    public void setSkin() {
        inflate(getContext(), R.layout.tcs_title_bar, this);

//        if(!isInEditMode()) {
        ImageView logo = (ImageView) findViewById(R.id.logo);

        TCSSkin skin = TCSAppInstance.getInstance().getSelectedSkin();
        Picasso.with(getContext()).load(skin.getTitleBar().get(TCSSkin.LOGO)).into(logo);

        setBackgroundColor(Color.parseColor(skin.getPrimaryColor()));
        if (skin.getTitleBar().containsKey(TCSSkin.OPACITY) &&
                !skin.getTitleBar().get(TCSSkin.OPACITY).isEmpty()) {
            getBackground().setAlpha(Integer.valueOf(skin.getTitleBar().get(TCSSkin.OPACITY)));
        } else {
            getBackground().setAlpha(0);
        }
//        }
    }
}
