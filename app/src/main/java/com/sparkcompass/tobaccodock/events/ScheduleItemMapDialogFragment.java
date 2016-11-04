package com.sparkcompass.tobaccodock.events;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pacificfjord.pfapi.TCSAppInstance;
import com.pacificfjord.pfapi.TCSScheduleItem;
import com.sparkcompass.tobaccodock.R;

/**
 * Created by Aaron Vega on 3/26/15.
 */
public class ScheduleItemMapDialogFragment extends DialogFragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    private static final String TAG = ScheduleItemMapDialogFragment.class.getName();
    public static final String EVENT_ITEM = "event_item";

    private View rootView;
    private GoogleMap map;
    private ImageView closeButton;
    private TextView title;
    private TCSScheduleItem eventItem;


    public static ScheduleItemMapDialogFragment newInstance(TCSScheduleItem eventItem) {
        ScheduleItemMapDialogFragment fragment = new ScheduleItemMapDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EVENT_ITEM, eventItem);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.LoginDialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getDialog().getWindow().setStatusBarColor(Color.parseColor(TCSAppInstance.getInstance().getSelectedSkin().getPrimaryColorDark()));
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }
        }

        try {

            if (savedInstanceState != null) {
                eventItem = savedInstanceState.getParcelable(EVENT_ITEM);
            } else {
                eventItem = getArguments().getParcelable(EVENT_ITEM);
            }

            rootView = inflater.inflate(R.layout.map_page_dialog, container, false);
            closeButton = (ImageView) rootView.findViewById(R.id.close_button);
            closeButton.setVisibility(View.VISIBLE);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            title = (TextView) rootView.findViewById(R.id.header_title);
            title.setText(eventItem.getName());

            ((SupportMapFragment) (getFragmentManager().findFragmentById(R.id.map_fragment_dialog))).getMapAsync(this);

        } catch (InflateException e) {
            e.printStackTrace();
        }

        return rootView;
    }

    private void addLocation(LatLng location) {
        Marker marker = map.addMarker(new MarkerOptions()
                .position(new LatLng(location.latitude, location.longitude))
                .title(eventItem.getFacility().getName()));
        marker.showInfoWindow();
    }

    @Override
    public void onMapLoaded() {
        if (map != null && eventItem != null) {
            addLocation(eventItem.getFacility().getPosition());
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(eventItem.getFacility().getPosition(), 9.0f));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(EVENT_ITEM, eventItem);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }
        map.setOnMapLoadedCallback(this);
    }
}
