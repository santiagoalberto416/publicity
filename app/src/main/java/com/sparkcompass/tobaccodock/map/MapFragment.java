package com.sparkcompass.tobaccodock.map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.tcs.clustering.ClusterManager;
import com.google.maps.tcs.clustering.view.DefaultClusterRenderer;
import com.pacificfjord.pfapi.TCSGeoManager;
import com.pacificfjord.pfapi.TCSGeolocation;
import com.sparkcompass.tobaccodock.R;

import java.util.Collection;

import butterknife.ButterKnife;

/**
 * Created by mind-p6 on 9/1/15.
 */
public class MapFragment extends Fragment implements OnMapLoadedCallback, OnMapReadyCallback, GoogleMap.OnCameraIdleListener{

    private boolean myLocationFound = false;
    private GoogleMap map;
    private ClusterManager<TCSGeolocation> clusterManager;
    private CameraPosition previousCameraPosition;

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    public MapFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, rootView);

        ((SupportMapFragment) (getChildFragmentManager().findFragmentById(R.id.map_fragment))).getMapAsync(this);
        return rootView;
    }

    private void addLocation(TCSGeolocation location) {
        Marker melbourne = map.addMarker(new MarkerOptions()
                .position(location.getPosition())
                .title(location.getName())
                .snippet(location.getDescription()));
    }

    private void addFence(TCSGeolocation location) {
        if (location.getGeofenceType() == TCSGeolocation.PFGeolocationFenceType.CIRCLE) {
            Circle circle = map.addCircle(new CircleOptions()
                    .center(location.getPosition())
                    .radius(location.getRadius())
                    .strokeColor(Color.BLUE)
                    .strokeWidth(3) // In meters);
                    .fillColor(0x5F0000FF)
            );
        }
        if (location.getGeofenceType() == TCSGeolocation.PFGeolocationFenceType.POLYGON) {
            Polygon polygon = map.addPolygon(new PolygonOptions()
                    .addAll(location.getGeofencePoints())
                    .strokeColor(Color.BLUE)
                    .strokeWidth(3)
                    .fillColor(0x5F0000FF)
            );
        }
    }

    @Override
    public void onMapLoaded() {
        if (map != null) {
            Collection<TCSGeolocation> locations = TCSGeoManager.getInstance().getGeolocationDefinitions().values();

            clusterManager.addItems(locations);

            for (TCSGeolocation location : locations) {
                if (location.isLocationDisplayed()) {
                    addLocation(location);
                }
                if (location.isFenceDisplayed())
                    addFence(location);
            }

            clusterManager.cluster();
            map.setOnInfoWindowClickListener(clusterManager);
            clusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<TCSGeolocation>() {
                @Override
                public void onClusterItemInfoWindowClick(final TCSGeolocation item) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                            .setTitle("Get Directions")
                            .setMessage("Would you like to get directions to this location?")
                            .setPositiveButton(R.string.get_directions, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String uri = "geo:0,0?q=" + item.getDescription();
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                    startActivity(intent);
                                    dialog.dismiss();
                                }
                            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    final AlertDialog dialog = builder.create();
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface arg0) {
                            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
                            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
                        }
                    });
                    dialog.show();
                }
            });
        }
    }

    @Override
    public void onCameraIdle() {
        CameraPosition position = map.getCameraPosition();

        if(previousCameraPosition == null || previousCameraPosition.zoom != position.zoom){
            previousCameraPosition = map.getCameraPosition();
            clusterManager.cluster();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        try {
            map.setMyLocationEnabled(true);

            if (map != null) {
                try {
                    map.setMyLocationEnabled(true);
                } catch (SecurityException e) {

                }

                map.setOnMapLoadedCallback(this);
                clusterManager = new ClusterManager<>(getActivity().getBaseContext(), map);
                clusterManager.setRenderer(new LocationRenderer());
                map.setOnCameraIdleListener(this);
            }
        }catch (SecurityException e){

        }
    }

    private class LocationRenderer extends DefaultClusterRenderer<TCSGeolocation> {

        public LocationRenderer() {
            super(getActivity().getApplicationContext(), map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(TCSGeolocation location, MarkerOptions markerOptions) {
            // Draw a single person.
            // Set the info window to show their name.
            markerOptions.title(location.getName()).snippet(location.getDescription());
        }
    }
}
