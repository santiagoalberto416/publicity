package com.pacificfjord.pfapi.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.pacificfjord.pfapi.R;
import com.pacificfjord.pfapi.TCSAppInstance;

/**
 * Created by Aaron Vega on 1/29/15.
 */
public class TCSButton extends Button implements TCSSkinnableView {

    public TCSButton(Context context) {
        super(context);
        initView(context, null);
    }

    public TCSButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public TCSButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {

//        setBackgroundResource(R.drawable.rounded_corners);

        setSkin();
    }

    private void setFont(String font, String fontSize) {
        if (!font.isEmpty()) {
            setTypeface(Typeface.createFromAsset(getContext().getAssets(), font));
        }
        if (!fontSize.isEmpty()) {
            setTextSize(Float.valueOf(fontSize));
        }
    }

    @Override
    public void setSkin() {
//        if(currentTheme.equals(theme)) {
//            return;
//        }

        TCSSkin appTheme = TCSAppInstance.getInstance().getSelectedSkin();
        setBackgroundResource(R.drawable.rounded_corners);
        if (appTheme.getButton().containsKey(TCSSkin.BG_COLOR)) {
            String bgColor = appTheme.getButton().get(TCSSkin.BG_COLOR);

            if (!bgColor.isEmpty()) {
                getBackground().setColorFilter(Color.parseColor(bgColor), PorterDuff.Mode.SRC_ATOP);
            }
        }

        if (appTheme.getButton().containsKey(TCSSkin.TEXT_COLOR) &&
                !appTheme.getButton().get(TCSSkin.TEXT_COLOR).isEmpty()) {
            setTextColor(appTheme.getButton().get(TCSSkin.TEXT_COLOR));
        }

    }

    private void setTextColor(String color) {
        if (!color.isEmpty()) {
            setTextColor(Color.parseColor(color));
        }
    }
}
