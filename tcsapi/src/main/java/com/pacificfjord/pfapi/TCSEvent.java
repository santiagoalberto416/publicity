package com.pacificfjord.pfapi;

import org.json.JSONObject;

/**
 * Created by tom on 16/01/14.
 */
public class TCSEvent {
    public static final String KEYVALUE01 = "value01";
    public static final String KEYVALUE02 = "value02";
    public static final String KEYVALUE03 = "value03";
    public static final String AUDITORY = "AUDITORY";

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getSubType() {
        return subType;
    }

    private String eventType;
    public JSONObject values;
    private String subType;

    public void setEventType(String eventType)
    {
        this.eventType = eventType;
    }

    public String getEventType()
    {
        return eventType;
    }
}

