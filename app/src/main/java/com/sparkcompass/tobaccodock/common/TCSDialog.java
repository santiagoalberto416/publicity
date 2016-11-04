package com.sparkcompass.tobaccodock.common;

import android.app.Dialog;
import android.content.Context;

import com.sparkcompass.tobaccodock.R;


/**
 * Created by danielalcantara on 10/27/15.
 */
public abstract class TCSDialog extends Dialog implements TemplateView{

    public TCSDialog(Context context) {
        super(context, R.style.DialogSlideAnim);
    }
}
