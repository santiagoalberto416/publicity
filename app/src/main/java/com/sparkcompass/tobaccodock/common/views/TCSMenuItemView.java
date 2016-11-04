package com.sparkcompass.tobaccodock.common.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.pacificfjord.pfapi.TCSAppAction;

/**
 * Created by mind-p6 on 8/29/15.
 */
public class TCSMenuItemView extends LinearLayout {
    private TCSAppAction action;
    private String iconUrl = "";

    public boolean setIconUrl(String iconUrl) {
        boolean changed = this.iconUrl.compareTo(iconUrl) != 0;
        this.iconUrl = iconUrl;
        return changed;
    }

    public TCSMenuItemView(Context context) {
        super(context);
    }

    public TCSMenuItemView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public TCSMenuItemView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    public TCSAppAction getAction() {
        return action;
    }

    public void setAction(TCSAppAction action) {
        this.action = action;
    }


}
