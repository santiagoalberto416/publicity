package com.pacificfjord.pfapi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Aaron Vega on 6/25/15.
 */
public class TCSAccountableBeaconAPI {
    public static void getBeaconDetails(String beaconUid, final TCSCoolerSuccessDelegate delegate) {
        JSONObject payload = new JSONObject();
        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.GETACCOUNTABLEBEACON;
        requestArgs.payload = payload;
        requestArgs.additionalPath = "/" + beaconUid;
        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply,
                             Exception e) {
                if (e == null && responseCode == 200) {

                    TCSCoolerBeacon cooler = null;
                    try {
                        JSONObject jsonObject = new JSONObject(reply);
                        cooler = new TCSCoolerBeacon(jsonObject);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    delegate.done(true, cooler);
                } else {
                    if (delegate != null) {
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

    public static void getAuditoryHistory(final TCSAuditoryHistorySuccessDelegate delegate) {
        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.GETAUDITORYHISTORY;

        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                if (e == null && responseCode == 200) {
                    try {
                        List<TCSAudirotyHistoryItem> list = new ArrayList<TCSAudirotyHistoryItem>();
                        JSONArray array = new JSONArray(reply);
                        for(int i = 0; i < array.length(); i++) {
                            list.add(new TCSAudirotyHistoryItem(array.getJSONObject(i)));
                        }
                        delegate.done(true, list);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    if(delegate != null) {
                        delegate.done(false, null);
                    }
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

    public static void postAuditoryOptions(String beaconUid, List<Integer> options, final TCSSuccessDelegate delegate) {
        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.POSTAUDITORYOPTIONS;

        JSONObject values = new JSONObject();
        try {
            values.put("beaconUid", beaconUid);
            values.put("choicesId", new JSONArray(options));
        } catch (JSONException e) {
            e.printStackTrace();
            if (delegate != null) {
                delegate.done(false);
            }
        }

        requestArgs.payload = values;
        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                if (e != null) {
                    delegate.done(false);
                } else {
                    if (responseCode == 200) {
                        if (delegate != null) {
                            delegate.done(true);
                        }
                    } else {
                        if (delegate != null) {
                            delegate.done(false);
                        }
                    }
                }
            }
        };

        try {
            TCSAPIConnector.getInstance().requestWithArgs(requestArgs);
        } catch (Exception e) {
            e.printStackTrace();
            if (delegate != null) {
                delegate.done(false);
            }
        }
    }

    public static void getAuditoryOptions(final TCSAuditoryOptionSuccessDelegate delegate) {
        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.GETAUDITORYOPTIONS;
        requestArgs.payload = new JSONObject();

        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                if (e == null && responseCode == 200) {
                    try {
                        List<TCSAuditoryOption> list = new ArrayList<TCSAuditoryOption>();
                        JSONArray array = new JSONArray(reply);
                        for (int i = 0; i < array.length(); i++) {
                            list.add(new TCSAuditoryOption(array.getJSONObject(i)));
                        }
                        delegate.done(true, list);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                        delegate.done(false, null);
                    }
                } else {
                    if (delegate != null) {
                        delegate.done(false, null);
                    }
                }
            }
        };

        try {
            TCSAPIConnector.getInstance().requestWithArgs(requestArgs);
        } catch (Exception e) {
            e.printStackTrace();
            if (delegate != null) {
                delegate.done(false, null);
            }
        }
    }

    public interface TCSCoolerSuccessDelegate {
        void done(boolean success, TCSCoolerBeacon cooler);
    }

    public interface TCSAuditoryHistorySuccessDelegate {
        void done(boolean success, List<TCSAudirotyHistoryItem> list);
    }

    public interface TCSAuditoryOptionSuccessDelegate {
        void done(boolean success, List<TCSAuditoryOption> list);
    }
}
