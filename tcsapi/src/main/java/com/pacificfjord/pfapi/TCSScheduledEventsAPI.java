package com.pacificfjord.pfapi;

import android.content.ContentValues;

//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aaron Vega on 3/20/15.
 */
public class TCSScheduledEventsAPI {

    public static void getScheduledEvents(final TCSSchedulesSuccessDelegate delegate) {
        JSONObject payload = new JSONObject();
        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.GETSCHEDULES;
        requestArgs.payload = payload;
        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply,
                             Exception e) {
                if (e == null && responseCode == 200) {
                    try {
                        JSONArray schedulesJson = new JSONArray(reply);
                        List<TCSSchedule> list = new ArrayList<TCSSchedule>();
                        for (int i = 0; i < schedulesJson.length(); i++) {
                            list.add(new TCSSchedule(schedulesJson.getJSONObject(i)));
                        }
                        delegate.done(true, list);
                    } catch (JSONException e1) {
                        delegate.done(false, null);
                        e1.printStackTrace();
                    }
                }
            }
        };

        try {
            TCSAPIConnector.getInstance().requestWithArgs(requestArgs);
        } catch (Exception e) {
            delegate.done(false, null);
            e.printStackTrace();
        }
    }

    public static void getScheduledEventItems(String scheduleItemUid, boolean getChildEvents, final TCSScheduleItemsSuccesDelegate delegate) {
        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.GETSCHEDULEDITEMS;

        ContentValues values = new ContentValues();

        if(!getChildEvents){
            values.put("scheduleItemUid", scheduleItemUid);
        } else {
            values.put("parentItemUid", scheduleItemUid);
        }

        requestArgs.getParams = values;

        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply,
                             Exception e) {
                if (e == null && responseCode == 200) {
                    try {
                        JSONArray array = new JSONArray(reply);
                        List<TCSScheduleItem> list = new ArrayList<TCSScheduleItem>();
                        for (int i = 0; i < array.length(); i++) {
                            list.add(new TCSScheduleItem(array.getJSONObject(i)));
                        }
                        delegate.done(true, list);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                        delegate.done(false, null);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                        delegate.done(false, null);
                    }
                }
            }
        };

        try {
            TCSAPIConnector.getInstance().requestWithArgs(requestArgs);
        } catch (Exception e) {
            delegate.done(false, null);
            e.printStackTrace();
        }
    }

    public static void getScheduleItem(String scheduleItemUId, final TCSScheduleItemSuccessDelegate delegate) {
        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.GETSCHEDULEITEM;
        ContentValues values = new ContentValues();
        values.put("itemUid", scheduleItemUId);

        requestArgs.getParams = values;

        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                if (e == null && responseCode == 200) {
                    try {
                        JSONObject json = new JSONObject(reply);
                        TCSScheduleItem item = new TCSScheduleItem(json);
                        delegate.done(true, item);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                        delegate.done(false, null);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                        delegate.done(false, null);
                    }
                }
            }
        };

        try{
            TCSAPIConnector.getInstance().requestWithArgs(requestArgs);
        } catch (Exception e) {
            delegate.done(false,null);
            e.printStackTrace();
        }
    }

    public static void sendRSVPForEvent(String scheduleItemUid, int attendaceStatus, final TCSSuccessDelegate delegate) {
        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.SENDRSVP;

        JSONObject values = new JSONObject();
        try {
            values.put("uid", scheduleItemUid);
            values.put("attendanceStatus", attendaceStatus);
        } catch (JSONException e) {
            e.printStackTrace();
            delegate.done(false);
        }

        requestArgs.payload = values;
        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                if(e != null) {
                    delegate.done(false);
                }else {
                    if(responseCode == 200) {
                        delegate.done(true);
                    }else {
                        delegate.done(false);
                    }
                }
            }
        };

        try {
            TCSAPIConnector.getInstance().requestWithArgs(requestArgs);
        } catch (Exception e) {
            delegate.done(false);
            e.printStackTrace();
        }
    }

    public static interface TCSSchedulesSuccessDelegate {
        void done(boolean success, List<TCSSchedule> schedules);
    }

    public static interface TCSScheduleItemsSuccesDelegate {
        void done(boolean success, List<TCSScheduleItem> scheduleItems);
    }

    public interface TCSScheduleItemSuccessDelegate {
        void done(boolean success, TCSScheduleItem scheduleItem);
    }
}
