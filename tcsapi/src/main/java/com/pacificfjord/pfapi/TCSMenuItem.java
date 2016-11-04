package com.pacificfjord.pfapi;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tom on 16/01/14.
 */

/**
 * Item in a menu
 */
public class TCSMenuItem {
    private int position = 0;
    private String name = "";
    private String iconUrl = null;
    private String uid = null;
    private TCSAppAction action = null;

    public String getName() {
        return name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public TCSAppAction getAction() {
        return action;
    }

    public String getUid() {
        return uid;
    }

    public int getPosition() {
        return position;
    }

    public TCSMenuItem(JSONObject menuItem)throws JSONException
    {
        uid = menuItem.getString("uid");
        name = menuItem.getString("name");
        iconUrl = menuItem.getString("iconUrl");
        JSONObject jsonAction = menuItem.getJSONObject("action");
        if(jsonAction!=null)
            action = new TCSAppAction(jsonAction);
    }

    public TCSMenuItem(String name) {
        this.name = name;

    }
}
