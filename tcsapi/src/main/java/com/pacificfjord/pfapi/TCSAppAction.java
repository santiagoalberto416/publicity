package com.pacificfjord.pfapi;

import android.text.format.DateFormat;
import android.text.method.DateTimeKeyListener;
import android.util.Log;

import com.pacificfjord.pfapi.utilites.TCSUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by tom on 16/01/14.
 */
public class TCSAppAction {
    public static String ACTIONTYPE = "actionType";
    public static String ACTIONNOTIFICATION = "actionNotificationType";
    public static String ACTIONUID = "uid";
    public static String ACTIONPARAMS = "parameters";
    public static String ACTIONCREATIONTIME = "creationTime";
    public static String ACTIONDOMAIN = "actionDomain";

    public static String ACTIONDOMAIN_NONE = "NONE";
    public static String ACTIONDOMAIN_NOTIFYACTION = "NOTIFY";

    //Notification Types
    public static String ACTIONNOTIFICATION_UNSPECIFIED = "UNSPECIFIED";
    public static String ACTIONNOTIFICATION_NONE = "NONE";
    public static String ACTIONNOTIFICATION_SILENT = "SILENT";
    public static String ACTIONNOTIFICATION_ALERT = "ALERT";

    public static String ACTIONTYPE_MESSAGE = "MESSAGE";
    public static String ACTIONTYPE_CUSTOM = "CUSTOM";
    public static String ACTIONTYPE_APPAGE = "APP_PAGE";
    public static String ACTIONTYPE_WEBCONTENT = "WEBVIEW_CONTENT";
    public static String ACTIONTYPE_WEBURL = "WEBVIEW_URL";
    public static String ACTIONTYPE_AR = "AUGMENTED_REALITY";
    public static String ACTIONTYPE_QUESTIONAIRE = "QUESTIONNAIRE";

    public static String KEY_NOTIFICATION_TITLE = "NotificationTitle";
    public static String KEY_NOTIFICATION_MESSAGE = "NotificationMessage";

    private TCSAppActionType actionType;            /**< The Type of the Action */
    private String actionNotification = "";     /**< The Action Notification Method*/
    private String uid = null;                  /**< The UID of the Action */
    private JSONArray parameters;               /**< The Action Parameters */
    private Date creationTime;                  /**< The Creation Time of the Action */
    private Date receiptTime;                   /**< The receipt time of the Action */
    private String domain;                      /**< The Domain of the Action */
    private TCSEvent triggeringEvent = null;    /**< The Event that triggered the Action */

    public enum TCSAppActionType {
        ATYPE_UNSPECIFIED,
        ATYPE_MESSAGE,
        ATYPE_CUSTOM,
        ATYPE_APPAGE,
        ATYPE_WEBCONTENT,
        ATYPE_WEBURL,
        ATYPE_AR,
        ATYPE_QUESTIONAIRE
    };

    /**
     Create this Action with definition provided by the platform. The SDK will do this
     @param action The definition of the action as a Dictionary
     */
    public TCSAppAction(JSONObject action) throws JSONException
    {
        actionType = getActionEnum(action.getString(ACTIONTYPE));
        actionNotification = action.getString(ACTIONNOTIFICATION);
        parameters = action.getJSONArray(ACTIONPARAMS);
        uid = action.getString(ACTIONUID);
        domain = action.getString(ACTIONDOMAIN);
        if(domain==null)
            domain = ACTIONDOMAIN_NONE;

        String dateString = action.getString(ACTIONCREATIONTIME);
        if(dateString!=null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
            try{
                creationTime = formatter.parse(dateString);
            } catch(Exception e){
                creationTime = null;
            }
        }
        receiptTime = new Date();
    }

    @Override
    public boolean equals(Object o) {
        TCSAppAction obj = (TCSAppAction) o;
        return this.uid.equals(obj.getUid());
    }

    public TCSAppActionType getActionType()
    {
        return actionType;
    }

    public String getUid()
    {
        return uid;
    }

    public String getActionNotification()
    {
        return actionNotification;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public Date getReceiptTime() {
        return receiptTime;
    }

    public void setTriggeringEvent(TCSEvent triggeringEvent) {
        this.triggeringEvent = triggeringEvent;
    }

    public TCSEvent getTriggeringEvent() {
        return triggeringEvent;
    }

    /**
     Get an action parameter value by Key.
     @param key the key to the parameter
     @return the Value of the parameter
     */
    public String getParamValueForKey(String key)
    {
        if(parameters==null)
            return null;

        String paramVal = null;
        JSONObject tempObj;

        for(int i=0;i<parameters.length();i++)
        {
            try{
                tempObj = parameters.getJSONObject(i);
                if(tempObj.getString("name").equals(key))
                {
                    paramVal = tempObj.getString("value");
                    break;
                }

            }catch(JSONException e)
            {
                Log.d("TCSAppAction", e.getMessage());
            }
        }

        return paramVal;
    }

    /**
     Get the ActionType enumeration equivalent for a key
     @param key the key of the actionType
     @return the enumeration value for the key
     */
    public static TCSAppActionType getActionEnum(String key)
    {
        TCSAppActionType atype = TCSAppActionType.ATYPE_UNSPECIFIED;

        if(key != null)
        {
            if(key.equals(ACTIONTYPE_AR))
                atype = TCSAppActionType.ATYPE_AR;
            if(key.equals(ACTIONTYPE_APPAGE))
                atype = TCSAppActionType.ATYPE_APPAGE;
            if(key.equals(ACTIONTYPE_CUSTOM))
                atype = TCSAppActionType.ATYPE_CUSTOM;
            if(key.equals(ACTIONTYPE_MESSAGE))
                atype = TCSAppActionType.ATYPE_MESSAGE;
            if(key.equals(ACTIONTYPE_WEBCONTENT))
                atype = TCSAppActionType.ATYPE_WEBCONTENT;
            if(key.equals(ACTIONTYPE_WEBURL))
                atype = TCSAppActionType.ATYPE_WEBURL;
            if(key.equals(ACTIONTYPE_QUESTIONAIRE))
                atype = TCSAppActionType.ATYPE_QUESTIONAIRE;
        }

        return atype;
    }
}
