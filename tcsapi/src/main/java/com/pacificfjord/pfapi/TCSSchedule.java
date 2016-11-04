package com.pacificfjord.pfapi;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Aaron Vega on 3/23/15.
 */
public class TCSSchedule {

    private static final String UID = "uid";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";


    private String uid;
    private String name;
    private String description;

    public TCSSchedule() {}

    public TCSSchedule(JSONObject jsonObject) throws JSONException {
        this.uid = jsonObject.getString(UID);
        this.name = jsonObject.getString(NAME);
        this.description = jsonObject.getString(DESCRIPTION);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
