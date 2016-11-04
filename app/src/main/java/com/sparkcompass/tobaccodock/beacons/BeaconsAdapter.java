package com.sparkcompass.tobaccodock.beacons;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.pacificfjord.pfapi.TCSEvent;
import com.pacificfjord.pfapi.TCSEventManager;
import com.pacificfjord.pfapi.utilites.TCSTransmitter;
import com.sparkcompass.tobaccodock.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mind-p6 on 9/14/15.
 */
public class BeaconsAdapter extends BaseAdapter {

    private LayoutInflater mLayoutInflater;
    private ArrayList<TCSTransmitter> beaconPops = new ArrayList<TCSTransmitter>();
    private Context context;

    public BeaconsAdapter(Context context) {
        this.context = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return beaconPops.size();
    }

    @Override
    public Object getItem(int position) {
        return beaconPops.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final BeaconItem itemView;

        if (convertView == null) {
            itemView = (BeaconItem) mLayoutInflater.inflate(R.layout.beacon_item, parent, false);
        } else {
            itemView = (BeaconItem) convertView;
        }

        ImageView beaconSignal = (ImageView) itemView.findViewById(R.id.beacon_tray_signal);

        final TCSTransmitter transmitter = (TCSTransmitter) getItem(position);

        itemView.update(transmitter);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TCSEvent event = new TCSEvent();
                event.setEventType(TCSEventManager.VAL_BEACON_POP_TOUCH);
                JSONObject values = new JSONObject();
                try {
                    values.put(TCSEvent.KEYVALUE01, transmitter.getName());
                    values.put(TCSEvent.KEYVALUE02, transmitter.getIdentifier());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                event.values = values;

                TCSEventManager.getInstance().logEvent(event);
                ((Activity)context).onBackPressed();
            }
        });

        if (transmitter.isDepart()) {
            float alphaValue = 0.3f;
            itemView.setAlpha(alphaValue);
        } else {
            itemView.setAlpha(1);
        }

        int progress = progressValue(transmitter.getRssi());

        switch (progress) {
            case 1:
                beaconSignal.setImageResource(R.drawable.popprox2);
                break;
            case 2:
                beaconSignal.setImageResource(R.drawable.popprox3);
                break;
            case 3:
                beaconSignal.setImageResource(R.drawable.popprox4);
                break;
            case 4:
                beaconSignal.setImageResource(R.drawable.popprox5);
                break;
            case 5:
                beaconSignal.setImageResource(R.drawable.popprox6);
                break;
            case 6:
                beaconSignal.setImageResource(R.drawable.popprox7);
                break;
            case 7:
                beaconSignal.setImageResource(R.drawable.popprox8);
                break;
            case 8:
                beaconSignal.setImageResource(R.drawable.popprox9);
                break;

            default:
                beaconSignal.setImageResource(R.drawable.popprox1);
                break;
        }

        return itemView;
    }

    public synchronized void updateTransmitters(ArrayList<TCSTransmitter> transmitters) {
        beaconPops = transmitters;
        super.notifyDataSetChanged();
    }

    public synchronized void removeTransmitters() {
        beaconPops.clear();
        super.notifyDataSetChanged();
    }

    public int progressValue(int rssi) {
        double progressValue = 0;
        if (rssi >= -60) {
            progressValue = 1.0;
        } else if (rssi <= -90) {
            progressValue = 0.1;
        } else {
            double range = -60.0 - (-90.0);
            double percentage = (-60.0 - rssi) / range;
            progressValue = (1 - percentage);
        }

        return (int) (progressValue * 8);
    }
}
