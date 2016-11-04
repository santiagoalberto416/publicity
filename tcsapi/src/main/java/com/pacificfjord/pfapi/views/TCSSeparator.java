package com.pacificfjord.pfapi.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.pacificfjord.pfapi.R;
import com.pacificfjord.pfapi.TCSAppInstance;

/**
 * Created by Aaron Vega on 3/5/15.
 */
public class TCSSeparator extends RelativeLayout implements TCSSkinnableView {
    public TCSSeparator(Context context) {
        super(context);
        setSkin();
    }

    public TCSSeparator(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSkin();
    }

    public TCSSeparator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setSkin();
    }

    @Override
    public void setSkin() {
        inflate(getContext(), R.layout.separator, this);

        TCSSkin appSkin = TCSAppInstance.getInstance().getSelectedSkin();

        setBackgroundColor(Color.parseColor(appSkin.getSeparator()));
        getBackground().setAlpha(125);
    }
}
