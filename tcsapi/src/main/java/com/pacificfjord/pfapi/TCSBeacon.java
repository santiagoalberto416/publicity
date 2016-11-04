package com.pacificfjord.pfapi;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tom on 16/01/14.
 *
 * Beacon Definition as defined on the platform
 */
public class TCSBeacon {

    public static final String KEYLATITUDE = "latitude";
    public static final String KEYLONGITUDE = "longitude";
    public static final String KEYUID = "uid";
    public static final String KEYDEVICEID = "deviceId";
    public static final String KEYNAME = "name";
    public static final String KEYALTERNATEDISPLAYNAME = "alternateDisplayName";
    public static final String KEYDESCRIPTION = "description";
    public static final String KEYSTATUS = "status";
    public static final String KEYENTRYBEHAVIOUR = "beaconEntryBehaviour";
    public static final String KEYICONURL = "iconUrl";
    public static final String KEYENTRYRSSI = "entryRSSI";
    public static final String KEYEXITRSSI = "exitRSSI";

    protected String mUID; /**< The beacon UID */
    protected String mDeviceId;/**< The beacon deviceId */
    protected String mDisplayName;/**< The beacon displayName */
    protected String mDisplayDescription;/**< The beacon description */
    protected String mIconUrl;/**< The beacon iconUrl */
    protected LatLng mLocation;/**< The beacon GPS Location  */
    protected String mStatus;/**< The beacon status */
    protected String mBeaconEntryBehaviour;/**< The beacon entry behaviour */
    protected int mEntryRSSI;/**< The beacon entryRSSI */
    protected int mExitRSSI;/**< The beacon exitRSSI */

    public TCSBeacon(JSONObject beacon) throws JSONException {
        mEntryRSSI = -100;
        mExitRSSI = -100;
        try { // We don't always have a valid GPS location
            double latitude = beacon.getDouble(KEYLATITUDE);
            double longitude = beacon.getDouble(KEYLONGITUDE);
            mLocation = new LatLng(latitude, longitude);
        }catch(JSONException e){
            //Log.d("TCSFyxManager", "Not valid gps Location");
        }

        mDisplayName = beacon.getString(KEYNAME);
        mDisplayDescription = beacon.getString(KEYDESCRIPTION);
        mUID = beacon.getString(KEYUID);
        mDeviceId = beacon.getString(KEYDEVICEID);

        //If we get an alternate name that is not null or empty then we get
        String alternateDisplayName = beacon.getString(KEYALTERNATEDISPLAYNAME);
        if(alternateDisplayName!=null && !alternateDisplayName.isEmpty())
            mDisplayName = alternateDisplayName;

        mDisplayDescription = beacon.getString(KEYDESCRIPTION);
        mStatus = beacon.getString(KEYSTATUS);
        mBeaconEntryBehaviour = beacon.getString(KEYENTRYBEHAVIOUR);
        mIconUrl = beacon.getString(KEYICONURL);

        if(beacon.has(KEYENTRYRSSI) && beacon.has(KEYEXITRSSI)){
            mEntryRSSI = beacon.getInt(KEYENTRYRSSI);
            mExitRSSI = beacon.getInt(KEYEXITRSSI);
        }
    }

    public String getUID() {
        return mUID;
    }


    public String getDeviceId() {
        return mDeviceId;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public String getDisplayDescription() {
        return mDisplayDescription;
    }

    public String getIconUrl() {
        return mIconUrl;
    }

    public LatLng getLocation() {
        return mLocation;
    }

    public String getStatus() {
        return mStatus;
    }

    public String getBeaconEntryBehaviour() {
        return mBeaconEntryBehaviour;
    }

    public int getEntryRSSI() {
        return mEntryRSSI;
    }

    public int getExitRSSI() {
        return mExitRSSI;
    }
}
