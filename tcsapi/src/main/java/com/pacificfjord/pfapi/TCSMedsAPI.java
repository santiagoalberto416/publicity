package com.pacificfjord.pfapi;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by mind-p6 on 8/22/15.
 */
public class TCSMedsAPI {

    public static void getTimeForMeds(final TCSRequestSuccessDelegate delegate) throws Exception {
        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.GETTIMEFORMEDS;

        requestArgs.payload = new JSONObject();

        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                if (e == null && responseCode == 200) {
                    if (delegate != null)
                        delegate.done(true, reply);
                } else {
                    if (delegate != null)
                        delegate.done(false, null);
                }
            }
        };
        TCSAPIConnector.getInstance().requestWithArgs(requestArgs);
    }

    public static void postMedsTaken(String medsIds, final TCSRequestSuccessDelegate delegate) throws Exception {
        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.POSTMEDSTAKEN;

        JSONObject jsonObject = new JSONObject();

        JSONArray ids = new JSONArray();
        String[] idsArray = medsIds.split(",");

        for (String id : idsArray) {
            ids.put(id);
        }

        jsonObject.put("medicationIds", ids);

        requestArgs.payload = jsonObject;


        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                if (e == null && responseCode == 200) {
                    if (delegate != null)
                        delegate.done(true, reply);
                } else {
                    if (delegate != null)
                        delegate.done(false, null);
                }
            }
        };
        TCSAPIConnector.getInstance().requestWithArgs(requestArgs);
    }

    public static void notifyDoctor(final TCSSuccessDelegate delegate) {
        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.NOTIFYDOCTOR;
        requestArgs.payload = new JSONObject();

        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                if (e == null && responseCode == 200) {
                    if(delegate != null) {
                        delegate.done(true);
                    }
                } else {
                    if(delegate != null) {
                        delegate.done(false);
                    }
                }
            }
        };
        try {
            TCSAPIConnector.getInstance().requestWithArgs(requestArgs);
        } catch (Exception e) {
            e.printStackTrace();
            if(delegate != null) {
                delegate.done(false);
            }
        }


    }
}
