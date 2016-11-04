package com.pacificfjord.pfapi;

/**
 * Created by tom on 17/01/14.
 */
public interface TCSURLConnectionDelegate {
    public void done(TCSURLConnection connection, int responseCode, String reply, Exception e);
}
