package com.pacificfjord.pfapi;

import org.json.JSONObject;

/**
 * Created by tom on 16/11/14.
 */
public interface TCSDataReturnedDelegate {
    public void done(JSONObject data, Error error);
}
