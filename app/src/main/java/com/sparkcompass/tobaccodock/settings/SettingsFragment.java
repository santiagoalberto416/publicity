package com.sparkcompass.tobaccodock.settings;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.pacificfjord.pfapi.TCSAppInstance;
import com.pacificfjord.pfapi.TCSSuccessDelegate;
import com.pacificfjord.pfapi.views.TCSSkin;
import com.sparkcompass.tobaccodock.R;
import com.sparkcompass.tobaccodock.common.Constants;
import com.sparkcompass.tobaccodock.common.TCSFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mind-p6 on 9/1/15.
 */
public class SettingsFragment extends TCSFragment implements CompoundButton.OnCheckedChangeListener {

    @Bind(R.id.location_messages_switch)
    protected CompoundButton locationMessagesCompound;
    @Bind(R.id.beacon_messages_switch)
    protected CompoundButton beaconMessagesCompound;
    @Bind(R.id.coordinator_layout)
    protected CoordinatorLayout coordinatorLayout;

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SettingsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        ButterKnife.bind(this, rootView);

        customizeViews(TCSAppInstance.getInstance().getSelectedSkin());

        locationMessagesCompound.setChecked(TCSAppInstance.getInstance().isLocationsEnabled());
        locationMessagesCompound.setOnCheckedChangeListener(this);

        beaconMessagesCompound.setChecked(TCSAppInstance.getInstance().isNotificationsEnabled());
        beaconMessagesCompound.setOnCheckedChangeListener(this);

        return rootView;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.location_messages_switch:
                if (isChecked) {
                    int fineLocationPermissionCheck = ContextCompat
                            .checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);

                    if (fineLocationPermissionCheck == PackageManager.PERMISSION_GRANTED) {
                        locationsToggleWithToast(true);
                    } else {
                        if (ActivityCompat
                                .shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                            final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                                    .setTitle("Proximity Information")
                                    .setMessage(
                                            "This feature makes use of location services in order to check you in automatcally into Healthy places. Would you like to review your permissions for this app.")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                    Constants.RR_REQUEST_FINE_LOCATION);
                                        }
                                    }).setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            locationMessagesCompound.setChecked(false);
                                            Snackbar.make(coordinatorLayout, "Bluetooth service was not activated", Snackbar.LENGTH_SHORT)
                                                    .setActionTextColor(ContextCompat.getColor(getContext(), R.color.primary))
                                                    .show();
                                        }
                                    }).create();
                            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface arg0) {
                                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
                                    dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
                                }
                            });
                            dialog.show();
                        } else {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    Constants.RR_REQUEST_FINE_LOCATION);
                        }
                    }
                } else {
                    locationsToggleWithToast(false);
                }
                break;
            case R.id.beacon_messages_switch:
                TCSAppInstance.getInstance().setNotificationsEnabled(isChecked, new TCSSuccessDelegate() {
                    @Override
                    public void done(boolean success) {
                        String message = "";
                        if (isChecked) {
                            message = getActivity().getString(R.string.push_notifications_enabled);
                        } else {
                            message = getActivity().getString(R.string.push_notifications_disabled);
                        }

                        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT)
                                .setActionTextColor(ContextCompat.getColor(getContext(), R.color.primary))
                                .show();
                    }
                });
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Constants.RR_REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationsToggleWithToast(true);
                } else {
                    locationMessagesCompound.setChecked(false);
                }
            }
        }
    }

    private void locationsToggleWithToast(final boolean isChecked) {
        TCSAppInstance.getInstance().setLocationsEnabled(isChecked, new TCSSuccessDelegate() {
            @Override
            public void done(boolean success) {
                String message = "";
                if (isChecked) {
                    message = getActivity().getString(R.string.location_messages_enabled);
                } else {
                    message = getActivity().getString(R.string.location_messages_disabled);
                }

                Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT)
                        .setActionTextColor(ContextCompat.getColor(getContext(), R.color.primary))
                        .show();
            }
        });
    }

    @Override
    public void customizeViews(TCSSkin skinTemplate) {
    }
}
