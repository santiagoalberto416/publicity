package com.pacificfjord.pfapi.utilites;

import android.content.Context;

import com.pacificfjord.pfapi.TCSAppAction;

/**
 * Created by tom on 28/01/14.
 */
public interface TCSAppActionDelegate {
    public void didReceiveAppAction(TCSAppAction action);
    public Context getContextForMessage();
}
