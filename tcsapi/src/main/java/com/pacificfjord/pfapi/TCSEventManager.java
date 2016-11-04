package com.pacificfjord.pfapi;

import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by tom on 16/01/14.
 */
public class TCSEventManager {
    public static final String KEY_EVENTTYPE = "eventType";
    public static final String KEY_SUBTYPE = "subtype";
    public static final String KEY_ACTIONS = "actions";
    public static final String VAL_IMAGE_TARGET_RECOGNITION = "IMAGE_TARGET_RECOGNITION";
    public static final String VAL_GEOFENCE_ENTRY = "GEOFENCE_ENTRY";
    public static final String VAL_GEOFENCE_EXIT = "GEOFENCE_EXIT";
    public static final String VAL_BEACON_PROXIMITY_ENTRY = "BEACON_PROXIMITY_ENTRY";
    public static final String VAL_BEACON_PROXIMITY_EXIT = "BEACON_PROXIMITY_EXIT";
    public static final String VAL_BEACON_PROXIMITY_DWELL = "BEACON_PROXIMITY_DWELL";
    public static final String VAL_BEACON_POP_TOUCH = "BEACON_POP_TOUCH";
    public static final String VAL_GENERIC = "GENERIC";
    public static final String VAL_GENERIC_APP = "GENERIC_APP";
    public static final String VAL_APP_INSTANCE_AWAKEN = "APP_INSTANCE_AWAKEN";
    public static final String VAL_APPLICATION_CLOSE = "APPLICATION_CLOSE";
    public static final String VAL_FACEBOOK_POST = "FACEBOOK_POST";
    public static final String VAL_TWITTER_TWEET = "TWITTER_TWEET";
    public static final String VAL_USER_ACCEPT_ACTION = "USER_ACCEPT_ACTION";
    public static final String VAL_END_USER_REGISTRATION = "END_USER_REGISTRATION";
    public static final String VAL_END_USER_LOGIN = "END_USER_LOGIN";
    public static final String VAL_END_USER_UPDATE_DETAILS = "END_USER_UPDATE_DETAILS";
    public static final String VAL_END_USER_MENU_INTERACTION = "END_USER_MENU_INTERACTION";

    private static TCSEventManager ourInstance = new TCSEventManager();/**<The static instance of the manager*/

    public static TCSEventManager getInstance() {
        return ourInstance;
    }

    private TCSEventModifyDelegate eventModifyDelegate = null;

    public void setEventModifyDelegate(TCSEventModifyDelegate eventModifyDelegate) {
        this.eventModifyDelegate = eventModifyDelegate;
    }

    /**
     Manager for sending events to the Platform
     Used Log and event - See TCSEvent.h to see how to create a TCSEvent to send
     */
    private TCSEventManager() {
    }

    /**
     Log an Event

     To Log an event, Do the following:

     TCSEvent *event = [[TCSEvent alloc] init];
     event.eventType = ENTER_EVENT_TYPE_HERE; // See ENUMS IN TCSEvent.h - USE GENERIC_APP for custom events

     NSMutableDictionary *values = [[NSMutableDictionary alloc]init];

     // Set one or more of three optional value
     // These three values are logged with the event in the eventLog of the platform (if supplied)
     [values setObject:someString1 forKey:@"value01"];
     [values setObject:someString2 forKey:@"value02"];
     [values setObject:someString3 forKey:@"value03"];

     event.values = values;

     [[TCSEventManager instance] logEvent:event];

     @param event The event to Log
     */
    public void logEvent(final TCSEvent event)
    {
        if(eventModifyDelegate != null)
            eventModifyDelegate.eventWillBeSubmitted(event);

        JSONObject values = event.values != null ? event.values : new JSONObject();

        if(!values.has("latitude"))
        {
            //TODO - get the location from the GeoManager
            Location location =  TCSGeoManager.getInstance().lastKnownLocationIfAvailable();
            if(location!=null)
            {
                try {
                    long now = new Date().getTime();

                    long age = now - location.getTime();

                    if(age < TCSGeoManager.ONE_HOUR)
                    {
                        values.put("latitude",location.getLatitude());
                        values.put("longitude",location.getLongitude());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        try{
            //Now that eventType is the same string we can just put it in.
            values.put(KEY_EVENTTYPE, event.getEventType());

            if(event.getSubType() != null && !event.getSubType().isEmpty()) {
                values.put(KEY_SUBTYPE, event.getSubType());
            }
        }catch(JSONException e)
        {
            return;
        }

        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.LOGEVENT;
        requestArgs.payload = values;
        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                if(e==null && responseCode == 201)
                {
                    try{
                        JSONObject replyObject = new JSONObject(reply);
                        JSONArray appActions = replyObject.getJSONArray(KEY_ACTIONS);
                        for(int i=0;i<appActions.length();i++)
                        {
                            TCSAppAction action = new TCSAppAction(appActions.getJSONObject(i));

                            action.setTriggeringEvent(event);

                            if(event.getEventType().equals(VAL_BEACON_POP_TOUCH))
                                TCSAppInstance.getInstance().addAppAction(action,true);
                            else
                                TCSAppInstance.getInstance().addAppAction(action,false);
                        }
                    }catch (JSONException e2)
                    {
                        Log.d("TCSEventManager", e2.getLocalizedMessage());
                    }
                }
            }
        };

        try {
            TCSAPIConnector.getInstance().requestWithArgs(requestArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logEvent(final TCSEvent event, final TCSEventCallbackDelegate delegate)
    {
        if(eventModifyDelegate != null)
            eventModifyDelegate.eventWillBeSubmitted(event);

        JSONObject values = event.values != null ? event.values : new JSONObject();

        if(!values.has("latitude"))
        {
            //TODO - get the location from the GeoManager
            Location location =  TCSGeoManager.getInstance().lastKnownLocationIfAvailable();
            if(location!=null)
            {
                try {
                    long now = new Date().getTime();

                    long age = now - location.getTime();

                    if(age < TCSGeoManager.ONE_HOUR)
                    {
                        values.put("latitude",location.getLatitude());
                        values.put("longitude",location.getLongitude());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        try{
            //Now that eventType is the same string we can just put it in.
            values.put(KEY_EVENTTYPE, event.getEventType());

            if(event.getSubType() != null && !event.getSubType().isEmpty()) {
                values.put(KEY_SUBTYPE, event.getSubType());
            }
        }catch(JSONException e)
        {
            return;
        }

        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.LOGEVENT;
        requestArgs.payload = values;
        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                if(e==null && responseCode == 201)
                {
                    try{
                        JSONObject replyObject = new JSONObject(reply);
                        JSONArray appActions = replyObject.getJSONArray(KEY_ACTIONS);
                        for(int i=0;i<appActions.length();i++)
                        {
                            TCSAppAction action = new TCSAppAction(appActions.getJSONObject(i));

                            action.setTriggeringEvent(event);

                            if(event.getEventType().equals(VAL_BEACON_POP_TOUCH))
                                TCSAppInstance.getInstance().addAppAction(action,true);
                            else
                                TCSAppInstance.getInstance().addAppAction(action,false);
                        }
                        delegate.done(true, TCSAppInstance.getInstance().getAppActionList());
                    }catch (JSONException e2)
                    {
                        delegate.done(false, null);
                        Log.d("TCSEventManager", e2.getLocalizedMessage());
                    }
                } else {
                    delegate.done(false, null);
                }
            }
        };

        try {
            TCSAPIConnector.getInstance().requestWithArgs(requestArgs);
        } catch (Exception e) {
            e.printStackTrace();
            delegate.done(false, null);
        }
    }
}
