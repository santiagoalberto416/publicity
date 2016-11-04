package com.pacificfjord.pfapi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tom on 16/01/14.
 */
public class TCSMenu {
    public List<TCSMenuItem> menuItems = new ArrayList<TCSMenuItem>();

    protected String uid = null;
    protected String name = null;
    public String cacheVersion = "";


    public TCSMenu(JSONObject jsonMenu) throws JSONException
    {
        cacheVersion = jsonMenu.getString("cacheVersion");
        JSONObject menu = jsonMenu.getJSONObject("menu");
        uid = menu.getString("uid");
        name = menu.getString("name");
        JSONArray itemsArray = menu.getJSONArray("menuItems");

        createMenuItems(itemsArray);
    }

    protected void createMenuItems(JSONArray itemsArray) throws JSONException
    {
        menuItems.clear();

        for(int i=0; i<itemsArray.length();i++)
        {
            JSONObject jsonItem = itemsArray.getJSONObject(i);
            TCSMenuItem menuItem = new TCSMenuItem(jsonItem);
            if(!menuItems.contains(menuItem))
                menuItems.add(menuItem);
        }
    }

}
