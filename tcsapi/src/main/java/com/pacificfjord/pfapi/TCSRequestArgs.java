package com.pacificfjord.pfapi;

import android.content.ContentValues;

//import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by tom on 16/01/14.
 */
public class TCSRequestArgs {
    public JSONObject payload;
    public ContentValues getParams;
    public String additionalPath;
    public String requestType;
    public TCSURLConnectionDelegate callback;
    public boolean retry = false;
}
