package com.pacificfjord.pfapi.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.pacificfjord.pfapi.TCSAppInstance;

/**
 * Created by Aaron Vega on 2/3/15.
 */
public class TCSTextView extends TextView implements TCSSkinnableView {
    public TCSTextView(Context context) {
        super(context);
        setSkin();
    }

    public TCSTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSkin();
    }

    public TCSTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setSkin();
    }

    @Override
    public void setSkin() {
//        TCSSkin appTheme = TCSAppInstance.getInstance().getSelectedSkin();
//
//        String color = appTheme != null && appTheme.getLabel().containsKey(TCSSkin.TEXT_COLOR) ?
//                appTheme.getLabel().get(TCSSkin.TEXT_COLOR) : "";
//
//        if (!color.isEmpty()) {
//            setTextColor(Color.parseColor(color));
//        }
    }
}
