package com.pacificfjord.pfapi;

import org.json.JSONObject;

/**
 * Created by tom on 21/01/14.
 */
public interface TCSUserProfileDelegate {
    public void done(boolean success, JSONObject profile);
}
