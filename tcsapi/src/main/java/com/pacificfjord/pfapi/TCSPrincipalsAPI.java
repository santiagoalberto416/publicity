package com.pacificfjord.pfapi;

import android.content.ContentValues;

import com.pacificfjord.pfapi.views.TCSSkin;

//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mind-p6 on 8/6/15.
 */
public class TCSPrincipalsAPI {

    public static void getPrincipal(String principalUID, final TCSRequestSuccessDelegate delegate) throws Exception {
        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.GETPRINCIPALDETAILS;

        ContentValues values = new ContentValues();
        values.put("principalUid", principalUID);

        requestArgs.getParams = values;

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

    public static void getPrincipals(final TCSRequestSuccessDelegate delegate) throws Exception {
        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.GETPRINCIPALS;

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

    public static void getLinkedPrincipals(final TCSRequestSuccessDelegate delegate) throws Exception {
        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.GETLINKEDPRINCIPALS;

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



    public static void linkPrincipal(String principalUID, final TCSRequestSuccessDelegate delegate) throws Exception {
        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.LINKTOPRINCIPAL;

        requestArgs.additionalPath = "?principalUid=" + principalUID;

        requestArgs.payload = new JSONObject();

        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                if (e == null && responseCode == 201) {
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

    public static void unlinkPrincipal(String principalUID, final TCSRequestSuccessDelegate delegate) throws Exception {
        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.UNLINKFROMPRINCIPAL;

        requestArgs.additionalPath = "?principalUid=" + principalUID;

        requestArgs.payload = new JSONObject();

        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                if (e == null && responseCode == 201) {
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

    public static void getFacilities(final TCSRequestSuccessDelegate delegate) throws Exception {
        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.GETFACILITIES;

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
}
