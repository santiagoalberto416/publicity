package com.pacificfjord.pfapi;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Aaron Vega on 6/26/15.
 */
public class TCSCoolerBeacon {
    private static final String IDENTIFIER = "name";
    private static final String SERIAL_NUMBER = "description";
    private static final String RESPONSABLE = "endUserFirstName";
    private static final String LAST_VISIT = "lastEventTime";
    private static final String IMAGE_URL = "iconUrl";

    private String identifier;
    private String serialNumber;
    private String responsable;
    private DateTime lastVisit;
    private String imageUrl;
    private boolean visited;
    private boolean action;

    public TCSCoolerBeacon(JSONObject jsonObject) throws JSONException {
        this.identifier = jsonObject.getString(IDENTIFIER);
        this.serialNumber = jsonObject.getString(SERIAL_NUMBER);
        this.responsable = jsonObject.getString(RESPONSABLE);
        this.lastVisit = new DateTime(jsonObject.getString(LAST_VISIT));
        this.imageUrl = jsonObject.getString(IMAGE_URL);
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public DateTime getLastVisit() {
        return lastVisit;
    }

    public void setLastVisit(DateTime lastVisit) {
        this.lastVisit = lastVisit;
    }

    public boolean getVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean getAction() {
        return action;
    }

    public void setAction(boolean action) {
        this.action = action;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
