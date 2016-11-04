package com.pacificfjord.pfapi.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import com.pacificfjord.pfapi.R;
import com.pacificfjord.pfapi.TCSAppInstance;

/**
 * Created by Aaron Vega on 1/26/15.
 */
public class TCSEditText extends EditText implements TCSSkinnableView {

    public TCSEditText(Context context) {
        super(context);
        initView(context, null);
    }

    public TCSEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public TCSEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        setBackgroundResource(R.drawable.rounded_corners);
        setSkin();
    }

    private void setBackgroundColor(String bgColor) {
        if (!bgColor.isEmpty()) {
            getBackground().setColorFilter(Color.parseColor(bgColor), PorterDuff.Mode.SRC_ATOP);
        }
    }

    @Override
    public void setSkin() {
//        if(currentTheme.equals(theme)) {
//            return;
//        }

        final TCSSkin appTheme = TCSAppInstance.getInstance().getSelectedSkin();

        setBackgroundColor(appTheme.getInputText().get(TCSSkin.BG_COLOR));

        setTextColor(appTheme.getInputText().get(TCSSkin.TEXT_COLOR));

        if (appTheme.getInputText().containsKey(TCSSkin.PLACEHOLDER_COLOR) &&
                !appTheme.getInputText().get(TCSSkin.PLACEHOLDER_COLOR).isEmpty()) {
            this.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.toString().length() <= 0) {
                        setHintTextColor(Color.parseColor(
                                appTheme.getInputText().get(TCSSkin.PLACEHOLDER_COLOR)));
                    }
                }
            });
        }
    }

    private void setTextColor(String color) {
        if (!color.isEmpty()) {
            setTextColor(Color.parseColor(color));
//            setHintTextColor(Color.parseColor(TCSSkin.TEXT_COLOR));
        }
    }
}
