package com.pacificfjord.pfapi;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Aaron Vega on 6/29/15.
 */
public class TCSAudirotyHistoryItem {
    private static final String UID = "beaconUID";
    private static final String NAME = "auditorName";
    private static final String ARRIVE_TIME = "arriveTime";
    private static final String EXIT_TIME = "exitTime";
    private static final String ACTIONS = "actions";

    private String uid;
    private String name;
    private DateTime arriveTime;
    private DateTime exitTime;
    private String actions;

    public TCSAudirotyHistoryItem(JSONObject json) throws JSONException {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.S");

        this.uid = json.getString(UID);
        this.name = json.getString(NAME);
        this.arriveTime = formatter.parseDateTime(json.getString(ARRIVE_TIME));
        this.exitTime = formatter.parseDateTime(json.getString(EXIT_TIME));
        this.actions = "";
        JSONArray arr = json.getJSONArray(ACTIONS);
        for (int i = 0; i < arr.length(); i++) {
            this.actions += arr.getString(i) + ", ";
        }

        this.actions = this.actions.substring(0, this.actions.length() - 2);
    }

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

    public DateTime getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(DateTime arriveTime) {
        this.arriveTime = arriveTime;
    }

    public DateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(DateTime exitTime) {
        this.exitTime = exitTime;
    }

    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }
}
