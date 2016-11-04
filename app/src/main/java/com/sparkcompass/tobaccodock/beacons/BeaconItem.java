package com.sparkcompass.tobaccodock.beacons;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pacificfjord.pfapi.utilites.TCSTransmitter;
import com.sparkcompass.tobaccodock.R;
import com.sparkcompass.tobaccodock.utils.ImageUtils;


/**
 * Created by mind-p6 on 9/14/15.
 */
public class BeaconItem extends RelativeLayout {

    private String name = "";
    private TextView titleText;
    private ImageView beaconPop;

    public String getName() {
        return name;
    }

    boolean firstUpdate = true;

    public BeaconItem(Context context) {
        super(context);
    }

    public BeaconItem(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public BeaconItem(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    public void update(TCSTransmitter transmitter) {
        boolean isNew = !transmitter.getName().equals(name);
        name = transmitter.getName();

        if (firstUpdate) {
            titleText = (TextView) findViewById(R.id.beacon_tray_item_title);
            beaconPop = (ImageView) findViewById(R.id.beacon_tray_icon);
            firstUpdate = false;
        }

        if (isNew) {
            titleText.setText(transmitter.getDisplayName());
            ImageUtils.replaceImage(transmitter.getIconUrl(), beaconPop, getContext());
        }
    }
}
