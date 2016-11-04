package com.pacificfjord.pfapi;

//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;
import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aaron Vega on 6/1/15.
 */
public class TCSSegmentationAPI {

    public static void loadSegmentations(final TCSSegmentationSuccessDelegate callback) {
        JSONObject payload = new JSONObject();
        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.LOADSEGMENTATIONS;
        requestArgs.payload = payload;
        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                if(e == null && responseCode == 200) {
                    try {
                        JSONArray categoriesJson = new JSONArray(reply);
                        List<TCSSegmentationCategory> list = new ArrayList<TCSSegmentationCategory>();
                        for(int i = 0; i < categoriesJson.length(); i++) {
                            list.add(new TCSSegmentationCategory(categoriesJson.getJSONObject(i)));
                        }
                        if(callback != null) {
                            callback.done(true, list);
                        }
                    } catch (JSONException e1) {
                        if(callback != null) {
                            callback.done(false, null);
                        }
                        e1.printStackTrace();
                    }
                }
            }
        };

        try {
            TCSAPIConnector.getInstance().requestWithArgs(requestArgs);
        } catch (Exception e) {
            if(callback != null) {
                callback.done(false, null);
            }
            e.printStackTrace();
        }
    }

    public static void getEndUserProfileOptions(String endUserUid, final TCSSegmentationOptionsSuccessDelegate callback) {
        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.GETENDUSERPROFILEOPTIONS;

        ContentValues values = new ContentValues();
        values.put("endUserUid", endUserUid);

        requestArgs.getParams = values;
        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                if(e == null && responseCode == 200) {
                    try {
                        JSONArray profileOptionsJson = new JSONArray(reply);
                        List<TCSProfileOption> list = new ArrayList<TCSProfileOption>();
                        for(int i = 0; i < profileOptionsJson.length(); i++) {
                            list.add(new TCSProfileOption(profileOptionsJson.getJSONObject(i)));
                        }
                        if(callback != null) {
                            callback.done(true, list);
                        }
                    } catch (JSONException e1) {
                        if(callback != null) {
                            callback.done(false, null);
                        }
                        e1.printStackTrace();
                    }
                }
            }
        };

        try {
            TCSAPIConnector.getInstance().requestWithArgs(requestArgs);
        } catch (Exception e) {
            if(callback != null) {
                callback.done(false, null);
            }
            e.printStackTrace();
        }
    }

    public static void updateProfileOptions(String endUserUid, ArrayList<Long> options, final TCSSuccessDelegate callback) {
        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.UPDATEPROFILEOPTIONS;
        JSONObject values = new JSONObject();
        try {
            values.put("endUserUid", endUserUid);
            values.put("options", new JSONArray(options));
        } catch (JSONException e) {
            e.printStackTrace();
            if(callback != null) {
                callback.done(false);
            }
        }

        requestArgs.payload = values;
        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                boolean result = e == null && responseCode == 200;
                if(callback != null) {
                    callback.done(result);
                }
            }
        };
        try {
            TCSAPIConnector.getInstance().requestWithArgs(requestArgs);
        } catch (Exception e) {
            if(callback != null) {
                callback.done(false);
            }
            e.printStackTrace();
        }
    }

    public interface TCSSegmentationSuccessDelegate {
        void done(boolean success, List<TCSSegmentationCategory> profileOptions);
    }

    public interface  TCSSegmentationOptionsSuccessDelegate {
        void done(boolean success, List<TCSProfileOption> profileOptions);
    }
}
