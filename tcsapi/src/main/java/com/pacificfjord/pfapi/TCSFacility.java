package com.pacificfjord.pfapi;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.tcs.clustering.ClusterItem;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Aaron Vega on 4/9/15.
 */
public class TCSFacility implements ClusterItem, Parcelable {


    private static final String UID = "uid";
    private static final String NAME = "name";
    private static final String LOCATION = "location";
    private static final String PARENT_FACILITY = "parentfacilityid";
    private static final String GEOLOCATION = "geolocation";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";

    private String uid;
    private String name;
    private String location;
    private int parentFacilityId;
    private double latitude;
    private double longitude;

    public TCSFacility(JSONObject jsonObject) throws JSONException {

        if (jsonObject.has(LATITUDE) && !jsonObject.isNull(LATITUDE)) {
            this.latitude = Double.valueOf(jsonObject.getString(LATITUDE));
        }

        if (jsonObject.has(LONGITUDE) && !jsonObject.isNull(LONGITUDE)) {
            this.longitude = Double.valueOf(jsonObject.getString(LONGITUDE));
        }

        if (jsonObject.has(NAME) && !jsonObject.isNull(NAME)) {
            this.name = jsonObject.getString(NAME);
        }

        if (jsonObject.has(UID) && !jsonObject.isNull(UID)) {
            this.uid = jsonObject.getString(UID);
        }

        if (jsonObject.has(LOCATION)) {
            location = jsonObject.getString(LOCATION);
        }

        if (jsonObject.has(PARENT_FACILITY)) {
            parentFacilityId = jsonObject.getInt(PARENT_FACILITY);
        }

        if (jsonObject.has(GEOLOCATION) && !jsonObject.isNull(GEOLOCATION)) {
            JSONObject geoJsonObject = jsonObject.getJSONObject(GEOLOCATION);
            if (geoJsonObject.has(NAME)) {
                name = geoJsonObject.getString(NAME);
            }
            if (geoJsonObject.has(LATITUDE)) {
                latitude = geoJsonObject.getDouble(LATITUDE);
            }
            if (geoJsonObject.has(LONGITUDE)) {
                longitude = geoJsonObject.getDouble(LONGITUDE);
            }
        }
    }

    protected TCSFacility(Parcel in) {
        uid = in.readString();
        name = in.readString();
        location = in.readString();
        parentFacilityId = in.readInt();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<TCSFacility> CREATOR = new Creator<TCSFacility>() {
        @Override
        public TCSFacility createFromParcel(Parcel in) {
            return new TCSFacility(in);
        }

        @Override
        public TCSFacility[] newArray(int size) {
            return new TCSFacility[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getParentFacilityId() {
        return parentFacilityId;
    }

    public void setParentFacilityId(int parentFacilityId) {
        this.parentFacilityId = parentFacilityId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(latitude, longitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(name);
        dest.writeString(location);
        dest.writeInt(parentFacilityId);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }
}
