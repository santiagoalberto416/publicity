package com.pacificfjord.pfapi;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Aaron Vega on 4/9/15.
 */
public class TCSPrincipal {

    protected static final String NAME = "name";
    protected static final String USERIMAGE = "userImage";
    protected static final String VIDEOURL = "videoUrl";
    protected static final String UID = "userUid";
    protected static final String FACILITY = "facility";

    protected String name;
    protected String userImage;
    protected String videoUrl;
    protected String uid;
    protected TCSFacility facility;

    public TCSPrincipal() {}

    public TCSPrincipal(JSONObject jsonObject) throws JSONException {
        this.name = jsonObject.getString(NAME);
        this.userImage = jsonObject.getString(USERIMAGE);
        this.videoUrl = jsonObject.getString(VIDEOURL);
        this.uid = jsonObject.getString(UID);
        this.facility = new TCSFacility(jsonObject.getJSONObject(FACILITY));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public TCSFacility getFacility() {
        return facility;
    }

    public void setFacility(TCSFacility facility) {
        this.facility = facility;
    }
}
