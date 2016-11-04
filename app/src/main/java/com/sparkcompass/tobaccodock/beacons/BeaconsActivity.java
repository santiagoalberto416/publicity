package com.sparkcompass.tobaccodock.beacons;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.pacificfjord.pfapi.beacons.GimbalManager;
import com.pacificfjord.pfapi.utilites.TCSTransmitter;
import com.pacificfjord.pfapi.views.TCSSkin;
import com.sparkcompass.tobaccodock.R;
import com.sparkcompass.tobaccodock.common.TCSActivity;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class BeaconsActivity extends TCSActivity implements View.OnClickListener, GimbalManager.BeaconPopDelegate {

    @Bind(R.id.header_title)
    protected TextView titleView;
    @Bind(R.id.back_button)
    protected ImageView backButton;
    @Bind(R.id.beacons_grid)
    protected GridView beaconsGrid;


    private BeaconActivityCallbacks mCallbacks;
    private BeaconsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacons);
        ButterKnife.bind(this);

        titleView.setVisibility(View.VISIBLE);
        titleView.setText(getString(R.string.title_activity_beacons));

        backButton.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(this);

        beaconsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position, view);
            }
        });

        adapter = new BeaconsAdapter(this);
        beaconsGrid.setAdapter(adapter);
        GimbalManager.getInstance().setBeaconPopDelegate(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_button:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
        super.onBackPressed();
    }

    private void selectItem(int position, Object selectedItem) {
        if (beaconsGrid != null) {
            beaconsGrid.setItemChecked(position, true);
        }

        if (mCallbacks != null && selectedItem != null) {
            mCallbacks.onBeaconGridItemSelected(selectedItem);
        }
    }

    @Override
    public void clearBeaconPops() {
        adapter.removeTransmitters();
    }

    @Override
    public synchronized void beaconPopsUpdate(final ArrayList<TCSTransmitter> transmitters) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.updateTransmitters(transmitters);
            }
        });
    }

    @Override
    public void customizeViews(TCSSkin skinTemplate) {
        super.customizeViews(skinTemplate);
    }
}
