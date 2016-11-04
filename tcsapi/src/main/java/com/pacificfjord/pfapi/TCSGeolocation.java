package com.pacificfjord.pfapi;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.tcs.clustering.ClusterItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tom on 16/01/14.
 */
public class TCSGeolocation extends Location implements ClusterItem {

    public enum PFGeolocationFenceType{
        NONE,
        CIRCLE,
        POLYGON
    }

    public static final String KEYLATITUDE = "latitude";
    public static final String KEYLONGITUDE = "longitude";
    public static final String KEYNAME = "name";
    public static final String KEYDESCRIPTION = "description";
    public static final String KEYUID = "uid";
    public static final String KEYGEOFENCETYPE = "geofenceType";
    public static final String KEYGEOFENCERADIUS = "geofenceRadius";
    public static final String KEYDISPLAYLOCATION = "displayLocation";
    public static final String KEYDISPLAYGEOFENCE = "displayGeofence";
    public static final String KEYGEOFENCEPOINTS = "geofencePoints";

    private String name = "";
    private String description = "";
    private String UID = "";
    private PFGeolocationFenceType geofenceType = PFGeolocationFenceType.NONE;
    private boolean locationDisplayed = false;
    private boolean fenceDisplayed = false;
    private List<LatLng> geofencePoints = new ArrayList<LatLng>();
    private double radius = 0.0f;

    public String getName()
    {
        return name;
    }
    public String getDescription()
    {
        return description;
    }
    public String getUID()
    {
        return UID;
    }
    public PFGeolocationFenceType getGeofenceType()
    {
        return geofenceType;
    }
    public boolean isLocationDisplayed()
    {
        return locationDisplayed;
    }
    public boolean isFenceDisplayed()
    {
        return fenceDisplayed;
    }

    public double getRadius() {
        return radius;
    }

    public List<LatLng> getGeofencePoints() {
        return geofencePoints;
    }

    @Override
    public LatLng getPosition()
    {
        return new LatLng(getLatitude(), getLongitude());
    }


    public TCSGeolocation(JSONObject location) throws JSONException
    {
        super("TCSAPI");

        double latitude = location.getDouble(KEYLATITUDE);
        setLatitude(latitude);
        double longitude = location.getDouble(KEYLONGITUDE);
        setLongitude(longitude);

        name = location.getString(KEYNAME);
        description = location.getString(KEYDESCRIPTION);
        UID = location.getString(KEYUID);
        String fenceType = location.getString(KEYGEOFENCETYPE);
        setFenceType(fenceType);

        radius = location.getDouble(KEYGEOFENCERADIUS);
        locationDisplayed = location.getBoolean(KEYDISPLAYLOCATION);
        fenceDisplayed = location.getBoolean(KEYDISPLAYGEOFENCE);
        generateGeofencePoints(location.getJSONArray(KEYGEOFENCEPOINTS));
    }

    private void setFenceType(String fenceType)
    {
        geofenceType = PFGeolocationFenceType.NONE;

        if(fenceType.equals("NONE"))
            geofenceType = PFGeolocationFenceType.NONE;
        else if(fenceType.equals("CIRCLE"))
            geofenceType = PFGeolocationFenceType.CIRCLE;
        else if(fenceType.equals("POLYGON"))
            geofenceType = PFGeolocationFenceType.POLYGON;
    }

    private void generateGeofencePoints(JSONArray points) throws JSONException
    {
        JSONObject obj;
        for(int i=0; i<points.length(); i++)
        {
            obj = points.getJSONObject(i);
            geofencePoints.add(new LatLng(obj.getDouble(KEYLATITUDE), obj.getDouble(KEYLONGITUDE)));
        }
    }

    public boolean contains(Location location)
    {
        return contains(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    public boolean contains(LatLng point)
    {
        boolean c = false;

        switch(geofenceType)
        {
            case NONE:break;
            case CIRCLE:
            {
                float[] distance = new float[2];

                Location.distanceBetween( point.latitude, point.longitude,
                       getLatitude(), getLongitude(), distance);

                c = distance[0] <= radius;

                break;
            }
            case POLYGON:
            {
                int i, j, nvert = geofencePoints.size();


                LatLng iLoc;
                LatLng jLoc;
                double iLat,iLon,jLat,jLon;

                for(i = 0, j = nvert - 1; i < nvert; j = i++) {
                    iLoc = geofencePoints.get(i);
                    jLoc = geofencePoints.get(j);
                    iLat = iLoc.latitude;
                    iLon = iLoc.longitude;
                    jLat = jLoc.latitude;
                    jLon = jLoc.longitude;

                    if( ( iLat >= point.latitude != (jLat >= point.latitude) )
                            &&(point.longitude <= (jLon - iLon) * (point.latitude - iLat) / (jLat - iLat) + iLon)
                            )
                        c = !c;
                }
                break;
            }
        }

        return c;

    }
}
