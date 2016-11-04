package com.pacificfjord.pfapi;

import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tom on 16/01/14.
 */
public class TCSGeoManager implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static TCSGeoManager ourInstance = new TCSGeoManager();

    public static TCSGeoManager getInstance() {
        return ourInstance;
    }

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 10;
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 10;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

    public final static int ONE_MINUTE = 60 * MILLISECONDS_PER_SECOND; // milliseconds in one minute
    public final static int ONE_HOUR = 60 * ONE_MINUTE; // milliseconds in one hour

    public static final String KEYLOCATIONS = "geolocations";
    public static final String KEYENTITYUID = "entityUID";

    private Map<String, TCSEnclosingFence> enclosingFences = new HashMap<String, TCSEnclosingFence>();
    private Map<String, TCSGeolocation> geolocationDefinitions = new HashMap<String, TCSGeolocation>();

    private LocationRequest mLocationRequest;
    private GoogleApiClient mLocationClient;
    private String geolocationCacheVersion = "";
    private Date lastLocationCheck;
    private Date lastBackgroundLocationCheck;
    private double minEnclosingFenceTime = 30.0f;
    private boolean enabled = false;

    private Location lastKnownLocation;

    private TCSGeoManager() {
        mLocationRequest = LocationRequest.create();
        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        // Set the update interval to 5 seconds
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        // Set the fastest update interval to 1 second
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        mLocationClient = new GoogleApiClient.Builder(TCSAppInstance.getInstance().getApplicationContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();// new LocationClient(TCSAppInstance.getInstance().getApplicationContext(), this, this);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if (!enabled)
            mLocationClient.disconnect();
    }

    public double getMinEnclosingFenceTime() {
        return minEnclosingFenceTime;
    }

    public void setMinEnclosingFenceTime(double minEnclosingFenceTime) {
        this.minEnclosingFenceTime = minEnclosingFenceTime;
    }

    public Map<String, TCSGeolocation> getGeolocationDefinitions() {
        return geolocationDefinitions;
    }

    public void startGeoManager() {
        mLocationClient.connect();
    }

    public void stopGeoManager() {
        mLocationClient.disconnect();
    }

    public void syncLocations(final TCSRequestSuccessDelegate delegate) {
        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.GETGEOLOCATIONS;

        JSONObject values = new JSONObject();
        try {
            values.put(TCSAPIConnector.JKEY_CACHEVERSION, geolocationCacheVersion);
        } catch (JSONException e) {

        }
        requestArgs.payload = values;
        requestArgs.additionalPath = "?cacheVersion=" + geolocationCacheVersion;
        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                boolean result = false;
                String resultDesc = "";
                if (e != null) {
                    result = false;
                    resultDesc = TCSAppInstance.ERROR_CONNECTION_FAILED;
                } else {
                    if (responseCode == 200) {
                        try {
                            JSONObject jobj = new JSONObject(reply);
                            newGeolocationDefinitions(jobj);
                            result = true;
                            resultDesc = TCSAppInstance.SUCCESS;
                        } catch (JSONException e2) {
                            result = false;
                            resultDesc = TCSAppInstance.ERROR_UNKNOWN;
                        }

                    }
                }
                if (delegate != null)
                    delegate.done(result, resultDesc);
            }
        };
        try {
            TCSAPIConnector.getInstance().requestWithArgs(requestArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearGeofences() {
        synchronized (geolocationDefinitions) {
            geolocationDefinitions.clear();
        }
    }

    private void addGeofence(String uid, TCSGeolocation location) {
        synchronized (geolocationDefinitions) {
            geolocationDefinitions.put(uid, location);
        }
    }

    public void newGeolocationDefinitions(JSONObject locations) {
        String cacheStatus = "";

        try {
            cacheStatus = locations.getString(TCSAPIConnector.JKEY_CACHESTATUS);
        } catch (JSONException e) {

        }

        if (cacheStatus.equals(TCSAPIConnector.OUT_OF_SYNC)) {
            JSONArray geoLocations;
            try {
                geoLocations = locations.getJSONArray(KEYLOCATIONS);
            } catch (JSONException e) {
                return;
            }

            clearGeofences();

            JSONObject obj;
            synchronized (geolocationDefinitions) {
                for (int i = 0; i < geoLocations.length(); i++) {
                    try {
                        obj = geoLocations.getJSONObject(i);
                        TCSGeolocation newLocation = new TCSGeolocation(obj);
                        addGeofence(newLocation.getUID(), newLocation);
                    } catch (JSONException e) {
                    }
                }
            }

            try {
                geolocationCacheVersion = locations.getString(TCSAPIConnector.JKEY_CACHEVERSION);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void checkLocation(Location location) {
        synchronized (geolocationDefinitions) {
            Collection<TCSGeolocation> locationsCol = geolocationDefinitions.values();
            for (TCSGeolocation current : locationsCol) {
                //Check our location definitions to see if we are inside a fence
                if (current.contains(location)) {
                    TCSEnclosingFence enclosingFence = enclosingFences.get(current.getUID());
                    //If we haven't entered the fence yet then enter it
                    if (enclosingFence == null) {
                        TCSEvent event = new TCSEvent();
                        event.setEventType(TCSEventManager.VAL_GEOFENCE_ENTRY);
                        JSONObject values = new JSONObject();
                        try {
                            values.put(TCSGeolocation.KEYLATITUDE, "" + location.getLatitude());
                            values.put(TCSGeolocation.KEYLONGITUDE, "" + location.getLongitude());
                            values.put(KEYENTITYUID, current.getUID());
                        } catch (JSONException e) {

                        }
                        event.values = values;
                        TCSEventManager.getInstance().logEvent(event);

                        //Record the fact we entered the fence by creating an enclosing fence
                        TCSEnclosingFence newEnclosingFence = new TCSEnclosingFence(current.getUID(), new Date(), minEnclosingFenceTime);
                        enclosingFences.put(current.getUID(), newEnclosingFence);
                    } else //update the datetime on the enclosing fence
                        enclosingFence.setLastReEntryDateTime(new Date());
                } else {
                    //Check to see if we have an enclosing fence for this geofence.
                    TCSEnclosingFence enclosingFence = enclosingFences.get(current.getUID());
                    //If there is one and the minFenceContainmentTime has elapsed then we can trigger the exit event;
                    if (enclosingFence != null && enclosingFence.testExitTime(new Date())) {
                        TCSEvent event = new TCSEvent();
                        event.setEventType(TCSEventManager.VAL_GEOFENCE_EXIT);
                        JSONObject values = new JSONObject();
                        try {
                            values.put(TCSGeolocation.KEYLATITUDE, "" + location.getLatitude());
                            values.put(TCSGeolocation.KEYLONGITUDE, "" + location.getLongitude());
                            values.put(KEYENTITYUID, current.getUID());
                        } catch (JSONException e) {

                        }
                        event.values = values;
                        TCSEventManager.getInstance().logEvent(event);

                        //Now remove the fence since we have exited the geofence.
                        enclosingFences.remove(current.getUID());
                    }
                }
            }
        }
        lastLocationCheck = new Date();
    }

    public Location lastKnownLocationIfAvailable() {
//        if(mLocationClient.isConnected())
//        {
//            Location location = mLocationClient.getLastLocation();
//
//            return location;
//        }
        return lastKnownLocation;
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Toast.makeText(TCSAppInstance.getInstance().getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
        // If already requested, start periodic updates
        //if (mUpdatesRequested) {
//            mLocationClient.requestLocationUpdates(mLocationRequest, this);
        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(TCSAppInstance.getInstance().getApplicationContext(), "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            //try {
            // Start an Activity that tries to resolve the error
            //connectionResult.startResolutionForResult(
            //this,
            //CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                * Thrown if Google Play services canceled the original
                * PendingIntent
                */
            //} catch (IntentSender.SendIntentException e) {
            // Log the error
            //    e.printStackTrace();
            //}
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            //showErrorDialog(connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // Report to the UI that the location was updated
        //String msg = "Updated Location: " +
        //Double.toString(location.getLatitude()) + "," +
        //Double.toString(location.getLongitude());
        //Toast.makeText(TCSAppInstance.getInstance().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        lastKnownLocation = location;

        checkLocation(location);
        //TODO - manage concurrent modifications.
    }
}
