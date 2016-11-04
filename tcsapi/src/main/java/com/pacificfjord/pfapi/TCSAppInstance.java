package com.pacificfjord.pfapi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import com.pacificfjord.pfapi.beacons.GimbalManager;
import com.pacificfjord.pfapi.utilites.TCSAppActionDelegate;
import com.pacificfjord.pfapi.utilites.TCSNotificationService;
import com.pacificfjord.pfapi.utilites.TCSUtilities;
import com.pacificfjord.pfapi.views.TCSSkin;

import net.danlew.android.joda.JodaTimeAndroid;

//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by tom on 16/01/14.
 */
public class TCSAppInstance {
    private static TCSAppInstance instance = new TCSAppInstance();

    public static TCSAppInstance getInstance() {
        return instance;
    }

    private boolean runningInBackground;

    public static final String APPRESUME = "PF_APP_RESUME";
    public static final String APPPAUSE = "PF_APP_PAUSE";
    public static final String IMAGETARGETRECOGNITION = "IMAGE_TARGET_RECOGNITION";
    public static final String IMAGETARGETKEY = "IMAGE_TARGET_KEY";

    public static final String KEYAPPINSTANCEUID = "appInstanceUID";
    public static final String KEYAPPUID = "appUID";
    public static final String KEYSDKTARGET = "sdkTarget";
    public static final String KEYOSTYPE = "osType";
    public static final String KEYOSVERSION = "osVersion";
    public static final String KEYAPPVERSION = "appVersion";
    public static final String KEYDEVICE = "device";
    public static final String KEYREMEMBEREDAPPUID = "KEYREMEMBEREDAPPID";
    public static final String KEYADVERTISINGID = "KEYADVERTISINGID";
    public static final String KEYSPARKTAGSENABLE = "KEYSPARKTAGSENABLE";

    public static final String ERROR_INVALID_EMAIL = "Error_Invalid_Email";
    public static final String ERROR_INVALID_PASSWORD = "Error_Invalid_Password";
    public static final String ERROR_CONNECTION_FAILED = "Error_Connection_Failed";
    public static final String ERROR_REQUEST_FAILED = "Error_Request_Failed";
    public static final String ERROR_UNKNOWN = "Error_Unknown";
    public static final String SUCCESS = "Success";


    public static final String PREF_FYX_ENABLED = "PREF_FYX_ENABLED";
    public static final String PREF_LOCATION_ENABLED = "PREF_LOCATION_ENABLED";
    public static final String PREF_MESSAGES_ENABLED = "PREF_MESSAGES_ENABLED";
    public static final String PREF_APP_HAS_RUN = "PREF_APP_HAS_RUN";
    public static final String PREF_SELECTED_SKIN = "PREF_SELECTED_SKIN";

    public static final String KEYUSERNAME = "userName";
    public static final String KEYEMAILADDRESS = "emailAddress";
    public static final String KEYPASSWORD = "password";
    public static final String KEYOLDPASSWORD = "oldPassword";
    public static final String KEYNEWPASSWORD = "newPassword";
    public static final String KEYENDUSER = "endUser";
    public static final String KEYENDUSERACCESSTOKEN = "endUserAccessToken";
    public static final String KEYERRORCODE = "errorCode";
    public static final String KEYREGISTRATIONKEY = "registrationId";

    public static final String KEYEULAACCEPTED = "EULAACCEPTED";
    public static final String KEYTCSHASRUN = "TCSHASERUN";

    public static final String KEYDEFAULTGCMREGISTRATIONKEY = "PFDTHTOKEN";
    public static final String KEYDEFAULTGCMKEY = "KEYDEFAULTGCMKEY";
    public static final String JKEY_MENU = "menu";
    public static final String JKEY_CACHESTATUS = "cacheStatus";
    public static final String JKEY_CACHEVERSION = "cacheVersion";
    public static final String OUT_OF_SYNC = "OUT_OF_SYNC";
    public static final String kTCSAppActionsUpdated = "TCSAPPACTIONSUPDATED";
    public static final String kTCSAppInstanceActive = "APPINSTANCE_ACTIVE";
    private static final String KEY_ACTIONS = "actions";

    private static final String TAG = "TCSAppInstance";

    private String appUID = null;
    private String defaultAppUID = null;
    private String appVersion = null;
    private String appInstanceUID = null;
    private String endUserAccessToken = null;
    private String userProfileCacheVersion = "";
    private String gcmRegistrationKey = null;
    private List<TCSAppAction> appActionList = new ArrayList<TCSAppAction>();
    private Map<String, TCSMenu> menuDefinitions = new Hashtable<String, TCSMenu>();
    private Map<Integer, TCSQuestionnaire> questionnairesDefinitions = new Hashtable<Integer, TCSQuestionnaire>();
    private JSONObject endUserDetails = null;

    private SharedPreferences uDefs = null;
    private Application application = null;
    private Context applicationContext = null;
    private Class targetNotificationClass = null;

    private boolean applicationIsAwake = false;
    private boolean receiversRegistered = false;

    private TCSAppActionDelegate appActionDelegate = null;
    private TCSSuccessDelegate pointsUpdatedDelegate = null;
    public static final String KEYPROFILEIMAGE = "image";

    private int appIcon = 0;
    private int alertIcon = 0;

    private TCSSkin appSkin;

    public void setAppSkin(TCSSkin skin) {
        appSkin = skin;
    }

    public String getAppUID() {
        return appUID;
    }

    public void setAppUID(String appUID) {
        this.appUID = appUID;

        //First one set is the default.
        if (defaultAppUID == null) {
            defaultAppUID = appUID;
        }
    }

    public void stopTCSFXYManager() {
        GimbalManager.getInstance().stopMonitoring();
        GimbalManager.getInstance().clearBeacons();
    }

    public void reset() {
        TCSGeoManager.getInstance().stopGeoManager();
        logOutEndUser();
        appActionList.clear();
        SharedPreferences.Editor editor = uDefs.edit();
        editor.putString(KEYOSVERSION, "");
        editor.putString(KEYOSTYPE, "");
        editor.putString(KEYDEVICE, "");
        editor.commit();
        menuDefinitions.clear();
        questionnairesDefinitions.clear();
        userProfileCacheVersion = "";
        appInstanceUID = null;
        appUID = defaultAppUID;
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String action = intent.getAction();

            if (action.equals(APPRESUME)) {
                onResume();
            }
            if (action.equals(APPPAUSE)) {
                onPause();
            }
            if (action.equals(IMAGETARGETRECOGNITION)) {
                applicationReceivedImageRecognition(intent);
            }
        }
    };

    public void setAppActionDelegate(TCSAppActionDelegate delegate) {
        appActionDelegate = delegate;
    }

    public void setupAppFromApplication(String appUID, Application application) {
        this.application = application;
        targetNotificationClass = application.getApplicationContext().getClass();
        applicationContext = application.getApplicationContext();

        setup(appUID);
    }

    public void setupApp(String appUID, Context context) {
        application = ((Activity) context).getApplication();
        targetNotificationClass = context.getClass();
        applicationContext = context.getApplicationContext();

        setup(appUID);
    }

    public void setup(String appUID) {
        uDefs = applicationContext.getSharedPreferences(applicationContext.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = uDefs.edit();

        if (!uDefs.contains(PREF_FYX_ENABLED)) {
            editor.putBoolean(PREF_FYX_ENABLED, false);
        }
//        if (!uDefs.contains(PREF_LOCATION_ENABLED)) {
//            editor.putBoolean(PREF_LOCATION_ENABLED, true);
//        }
        if (!uDefs.contains(PREF_MESSAGES_ENABLED)) {
            editor.putBoolean(PREF_MESSAGES_ENABLED, true);
        }

        editor.commit();

        try {
            appVersion = applicationContext.getPackageManager().getPackageInfo(applicationContext.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            appVersion = "UNKNOWN";
        }

        setAppUID(appUID);

        String appId = application.getPackageName();
        PackageManager pm = application.getPackageManager();
        try {
            appIcon = pm.getApplicationInfo(appId, 0).icon;
        } catch (PackageManager.NameNotFoundException e) {
            appIcon = 0;
        }

        TCSAPIConnector.getInstance().MonitorReachability(applicationContext);
    }

    public Application getApplication() {
        return application;
    }

    public Context getApplicationContext() {
        return applicationContext;
    }

    public Class getTargetNotificationClass() {
        return targetNotificationClass;
    }

    public void setTargetNotificationClass(Class targetNotificationClass) {
        this.targetNotificationClass = targetNotificationClass;
    }

    public String getAppInstanceUID() {
        return appInstanceUID;
    }

    public String getEndUserAccessToken() {
        return endUserAccessToken;
    }

    public boolean isApplicationIsAwake() {
        return applicationIsAwake;
    }

    public void setApplicationIsAwake(boolean applicationIsAwake) {
        this.applicationIsAwake = applicationIsAwake;
    }

    private TCSAppInstance() {

    }

    public int getAppIcon() {
        return appIcon;
    }

    public int getAlertIcon() {
        if (alertIcon == 0) {
            return appIcon;
        } else {
            return alertIcon;
        }
    }

    public void setAlertIcon(int alertIcon) {
        this.alertIcon = alertIcon;
    }

    /**
     * This should be called when the main Activity is started with an Intent
     * We check the intent for a KEYNOTIFICATIONACTIONUID and fire an appropriate action as necessary
     *
     * @param intent The intent from the Notification
     */
    public void receivedIntent(Intent intent) {
        String actionUID = intent.getStringExtra(TCSNotificationService.KEYNOTIFICATIONACTIONUID);
        if (actionUID != null) {
            for (TCSAppAction action : appActionList) {
                if (actionUID.equals(action.getUid()) && appActionDelegate != null) {
                    appActionDelegate.didReceiveAppAction(action);
                }
            }
        }
        //We want to make sure that the intent does not keep firing every time we reopen the app
        intent.removeExtra(TCSNotificationService.KEYNOTIFICATIONACTIONUID);
    }

    private void onPause() {
        applicationIsAwake = false;
    }

    private void onResume() {
        if (applicationIsAwake) {
            return;
        }

        TCSEvent event = new TCSEvent();
        event.setEventType(TCSEventManager.VAL_APP_INSTANCE_AWAKEN);
        event.values = null;

        if (TCSAPIConnector.getInstance().internetIsReachable()) {
            TCSEventManager.getInstance().logEvent(event);
            TCSGeoManager.getInstance().syncLocations(null);
            GimbalManager.getInstance().syncBeacons(null);
            reportSystemUpdates();
        }

        applicationIsAwake = true;
    }

    private void applicationReceivedImageRecognition(Intent intent) {
        String targetID = intent.getStringExtra(IMAGETARGETKEY);

        TCSEvent event = new TCSEvent();
        event.setEventType(TCSEventManager.VAL_IMAGE_TARGET_RECOGNITION);
        JSONObject values = new JSONObject();
        try {
            values.put(TCSEvent.KEYVALUE01, targetID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        event.values = values;
        TCSEventManager.getInstance().logEvent(event);
    }

    /**
     * Begins the appInstance
     *
     * @param callback
     */
    public void beginWithCallback(final TCSAppInstanceBeginDelegate callback) {
        //get the appInstanceUID from preferences if it exists
        loadAppInstanceUID();
        //appInstanceUID is null so get it from the server
        if (appInstanceUID == null) {
            JSONObject postItems = new JSONObject();

            try {
                postItems.put(KEYAPPUID, appUID);
                postItems.put(KEYSDKTARGET, "ANDROID");
                postItems.put(KEYOSTYPE, "ANDROID");
                postItems.put(KEYOSVERSION, Build.VERSION.RELEASE);
                postItems.put(KEYAPPVERSION, appVersion);
                postItems.put(KEYDEVICE, Build.MODEL);
            } catch (JSONException e) {
                Log.d("TCSAppInstance", e.getMessage());
            }

            TCSRequestArgs requestArgs = new TCSRequestArgs();
            requestArgs.requestType = TCSAPIConstants.TCSRequestType.REGISTERAPPINSTANCE;
            requestArgs.retry = true;
            requestArgs.payload = postItems;
            requestArgs.callback = new TCSURLConnectionDelegate() {
                @Override
                public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                    if (e != null) {
                        Log.d("TCSAppInstance", e.getMessage());
                        callback.done(false, "CONNECTIONFAILED");
                    } else {
                        if (responseCode == 201) {
                            try {
                                JSONObject json = new JSONObject(reply);
                                appInstanceUID = (String) json.get(KEYAPPINSTANCEUID);
                                saveAppInstanceUID();

                                //Save the current values for
                                //osVersion
                                //AppVersion
                                //Device >>
                                SharedPreferences.Editor editor = uDefs.edit();
                                editor.putString(KEYOSVERSION, Build.VERSION.RELEASE);
                                editor.putString(KEYAPPVERSION, appVersion);
                                editor.putString(KEYDEVICE, Build.MODEL);
                                editor.commit();
                                //<<
                                engageAppInstance();
                                callback.done(true, null);
                            } catch (JSONException ej) {
                                Log.d("TCSAppInstance", ej.getMessage());
                                callback.done(false, "DATAERROR");
                            }
                        }
                    }
                }
            };

            try {
                TCSAPIConnector.getInstance().requestWithArgs(requestArgs);
            } catch (Exception e) {
                Log.d("TCSAppInstance", e.getMessage());
                callback.done(false, "REQUESTFAILED");
            }
        } else {
            endUserAccessToken = uDefs.getString(KEYENDUSERACCESSTOKEN, null);
            engageAppInstance();
            callback.done(true, null);
        }
    }

    /**
     * Start AppInstance Processing
     */
    protected void engageAppInstance() {
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(new Intent(kTCSAppInstanceActive));

        boolean fyxEnabled = uDefs.getBoolean(PREF_FYX_ENABLED, false);
        boolean geoEnabled = uDefs.getBoolean(PREF_LOCATION_ENABLED, true);

        TCSEvent event = new TCSEvent();
        if (!isRunningInBackground()) {
            event.setEventType(TCSEventManager.VAL_APP_INSTANCE_AWAKEN);
            event.values = null;
        } else {
            event.setEventType(TCSEventManager.VAL_APPLICATION_CLOSE); //TOM TODO - Does this eventtype exist - was it added for Advanta
            event.values = null;
        }

//        TCSFYXManager.getInstance().setEnabled(fyxEnabled);

        if (fyxEnabled) {

            Log.d(TAG, "CODENAME: " + Build.VERSION.CODENAME);
            Log.d(TAG, "INCREMENTAL: " + Build.VERSION.INCREMENTAL);
            Log.d(TAG, "RELEASE: " + Build.VERSION.RELEASE);
            Log.d(TAG, "SDK_INT: " + Build.VERSION.SDK_INT);

            if (supportsBeacons()) {
                //                TCSFYXManager.getInstance().startFYX();
                GimbalManager.getInstance().startMonitoring();
            } else {
                Log.d(TAG, "This device or Android release is not supported for bluetooth beacons");
            }
        }
        if (geoEnabled) {
            TCSGeoManager.getInstance().startGeoManager();
        }

        if (TCSAPIConnector.getInstance().internetIsReachable()) {
            TCSGeoManager.getInstance().syncLocations(null);
            GimbalManager.getInstance().syncBeacons(null);
            syncGCMRegistrationKey();
            TCSEventManager.getInstance().logEvent(event);
        }

        if (!receiversRegistered) {
            LocalBroadcastManager.getInstance(applicationContext).registerReceiver(mMessageReceiver, new IntentFilter(APPRESUME));
            LocalBroadcastManager.getInstance(applicationContext).registerReceiver(mMessageReceiver, new IntentFilter(APPPAUSE));
            LocalBroadcastManager.getInstance(applicationContext).registerReceiver(mMessageReceiver, new IntentFilter(IMAGETARGETRECOGNITION));
            receiversRegistered = true;
        }

        JodaTimeAndroid.init(applicationContext);

        reportSystemUpdates();
        applicationIsAwake = true;
    }

    private void reportSystemUpdates() {
        String osVersion = Build.VERSION.RELEASE;
        String device = Build.MODEL;

        String oldOsVersion = uDefs.getString(KEYOSVERSION, "");
        String oldAppVersion = uDefs.getString(KEYAPPVERSION, "");
        String oldDevice = uDefs.getString(KEYDEVICE, "");

        if (!osVersion.equals(oldOsVersion) || !appVersion.equals(oldAppVersion) || !device.equals(oldDevice)) {
            JSONObject values = new JSONObject();
            try {
                values.put(KEYOSVERSION, osVersion);
                values.put(KEYAPPVERSION, appVersion);
                values.put(KEYDEVICE, device);
            } catch (JSONException e) {

            }
            TCSRequestArgs requestArgs = new TCSRequestArgs();
            requestArgs.requestType = TCSAPIConstants.TCSRequestType.UPDATEAPPINSTANCE;
            requestArgs.payload = values;
            requestArgs.callback = new TCSURLConnectionDelegate() {
                @Override
                public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                    if (e == null && responseCode == 200) {
                        SharedPreferences.Editor editor = uDefs.edit();
                        editor.putString(KEYOSVERSION, Build.VERSION.RELEASE);
                        editor.putString(KEYAPPVERSION, appVersion);
                        editor.putString(KEYDEVICE, Build.MODEL);
                        editor.commit();
                    }
                }
            };

            try {
                TCSAPIConnector.getInstance().requestWithArgs(requestArgs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Saves The appInstanceID to sharedSettings
     */
    public boolean saveAppInstanceUID() {
        if (uDefs == null) {
            return false;
        }

        SharedPreferences.Editor editor = uDefs.edit();
        editor.putString(appUID, appInstanceUID);

        return editor.commit();
    }

    /**
     * Retrieves the appInstanceUID from sharedPreferences
     */
    public void loadAppInstanceUID() {
        appInstanceUID = uDefs.getString(appUID, null);
    }

    /**
     * Is the appInstance Active
     *
     * @return whether or not the instance is active
     */
    public boolean isAppActive() {
        return appInstanceUID != null;
    }

    public void setGCMRegistrationKey(String key) {
        gcmRegistrationKey = key;

        SharedPreferences.Editor editor = uDefs.edit();
        editor.putString(KEYDEFAULTGCMKEY, gcmRegistrationKey);
        editor.apply();

        //Try to sync - if we have a registered appInstance this will execute a sync.
        //If not it will happen after the appInstance is Registered.
        syncGCMRegistrationKey();
    }

    private void syncGCMRegistrationKey() {
        if (isAppActive() && gcmRegistrationKey != null) {
            String previousGCMRegistrationKeyMD5 = uDefs.getString(KEYDEFAULTGCMREGISTRATIONKEY, "");
            final String gcmRegistrationKeyMD5 = TCSUtilities.getMd5Hash(gcmRegistrationKey);

            if (previousGCMRegistrationKeyMD5 != null && gcmRegistrationKeyMD5.equals(previousGCMRegistrationKeyMD5)) {
                //The key has not changed
                gcmRegistrationKey = null;
                return;
            }

            TCSRequestArgs requestArgs = new TCSRequestArgs();
            requestArgs.requestType = TCSAPIConstants.TCSRequestType.REGISTERGCSDEVICE;

            JSONObject payload = new JSONObject();
            try {
                payload.put(KEYREGISTRATIONKEY, gcmRegistrationKey);
            } catch (JSONException e) {

            }

            requestArgs.payload = payload;

            requestArgs.callback = new TCSURLConnectionDelegate() {
                @Override
                public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                    if (responseCode == 201 && e == null) {
                        SharedPreferences.Editor editor = uDefs.edit();
                        editor.putString(KEYDEFAULTGCMREGISTRATIONKEY, gcmRegistrationKeyMD5);
                        gcmRegistrationKey = null;
                        editor.apply();
                    }
                }
            };

            try {
                TCSAPIConnector.getInstance().requestWithArgs(requestArgs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateGCMRegistrationKey() {

        String gcmKey = uDefs.getString(KEYDEFAULTGCMKEY, "");

        if (isAppActive() && (gcmKey != null && !gcmKey.isEmpty())) {
            TCSRequestArgs requestArgs = new TCSRequestArgs();
            requestArgs.requestType = TCSAPIConstants.TCSRequestType.REGISTERGCSDEVICE;

            JSONObject payload = new JSONObject();
            try {
                payload.put(KEYREGISTRATIONKEY, gcmKey);
            } catch (JSONException e) {

            }

            requestArgs.payload = payload;

            requestArgs.callback = new TCSURLConnectionDelegate() {
                @Override
                public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                    if (responseCode == 201 && e == null) {

                    }
                }
            };

            try {
                TCSAPIConnector.getInstance().requestWithArgs(requestArgs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sync a menu definition from the server
     *
     * @param menuName
     * @param delegate
     */
    public void syncMenu(final String menuName, final TCSSuccessDelegate delegate) {
        JSONObject payload = new JSONObject();
        TCSRequestArgs requestArgs = new TCSRequestArgs();

        requestArgs.requestType = TCSAPIConstants.TCSRequestType.SYNCMENU;
        requestArgs.additionalPath = "/" + menuName;

        if (menuDefinitions.containsKey(menuName)) {
            String flag = menuDefinitions.get(menuName).cacheVersion;
            requestArgs.additionalPath = "/" + menuName + "?cacheVersion=" + flag;
            try {
                payload.put(JKEY_CACHEVERSION, flag);
            } catch (JSONException e) {
                Log.d("TCSAppInstance", e.getMessage());
            }
        }

        requestArgs.payload = payload;
        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                if (e == null && responseCode == 200) {
                    try {
                        JSONObject menuObject = new JSONObject(reply);
                        newMenuDefinition(menuObject, menuName, delegate);
                    } catch (JSONException ej) {
                        delegate.done(false);
                    }
                } else {
                    delegate.done(false);
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

    protected void newMenuDefinition(JSONObject menu, String menuName, TCSSuccessDelegate delegate) {
        String cacheStatus = "";
        try {
            cacheStatus = menu.getString(JKEY_CACHESTATUS);
        } catch (JSONException e) {
            Log.d("TCSAppInstance", e.getMessage());
        }

        //if the menu does not exist then create it, otherwise update it.
        if (!menuDefinitions.containsKey(menuName)) {
            try {
                TCSMenu newMenu = new TCSMenu(menu);
                menuDefinitions.put(menuName, newMenu);
                newMenu.cacheVersion = menu.getString(JKEY_CACHEVERSION);
                delegate.done(true);
            } catch (JSONException e) {
                delegate.done(false);
            }
        } else if (cacheStatus.equals(OUT_OF_SYNC)) // The menu is out of sync - update.
        {
            try {
                JSONObject menuUpdateData = menu.getJSONObject("menu");
                TCSMenu tempMenu = menuDefinitions.get(menuName);
                tempMenu.createMenuItems(menuUpdateData.getJSONArray("menuItems"));
                tempMenu.cacheVersion = menu.getString(JKEY_CACHEVERSION);
                delegate.done(true);
            } catch (JSONException e) {
                delegate.done(false);
            }
        } else {
            delegate.done(true); // The menu exists and is in sync
        }
    }

    /**
     * Load a menu definition from device storage
     *
     * @param menu
     * @param delegate
     */
    public void loadMenuDefinition(String menu, TCSSuccessDelegate delegate) {
        //TODO - implement loading menu definition from file
    }


    public int menuItemCount(String menuName) {
        TCSMenu menu = menuDefinitions.get(menuName);

        return menu != null ? menu.menuItems.size() : 0;
    }

    public TCSMenu menuWithName(String menu) {
        return menuDefinitions.get(menu);
    }

    public TCSQuestionnaire questionnaireWithId(int questionnaireId) {
        return questionnairesDefinitions.get(questionnaireId);
    }

    public int appActionCount() {
        return appActionList.size();
    }

    public List<TCSAppAction> getAppActionList() {
        return appActionList;
    }

    public void addAppActions(List<TCSAppAction> actions, boolean alwaysDoAlert) {
        for (TCSAppAction action : actions) {
            addAppAction(action, alwaysDoAlert);
        }
    }

    public void addAppAction(TCSAppAction action, boolean alwaysDoAlert) {
        if(action.getActionNotification().equals(TCSAppAction.ACTIONNOTIFICATION_NONE)){
            appActionDelegate.didReceiveAppAction(action);
            return;
        }

        if (!isInActionList(action)) {
            if (action.getActionNotification().equals(TCSAppAction.ACTIONNOTIFICATION_ALERT)) {
                generateAlertWithAction(action);
            }

            appActionList.add(action);
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(new Intent(kTCSAppActionsUpdated));
        } else if (alwaysDoAlert && !action.getActionNotification().equals(TCSAppAction.ACTIONNOTIFICATION_NONE)) {
            generateAlertWithAction(action);
        }
    }

    private void generateAlertWithAction(final TCSAppAction action) {
        if (!isNotificationsEnabled()) {
            return;
        }
        //If the application is in the background ...
        if (!applicationIsAwake) {
            Intent notificationIntent = new Intent(applicationContext, TCSNotificationService.class);
            notificationIntent
                    .putExtra(TCSNotificationService.KEYNOTIFICATIONTITLE, action.getParamValueForKey(TCSNotificationService.KEYNOTIFICATIONTITLE));
            notificationIntent.putExtra(TCSNotificationService.KEYNOTIFICATIONMESSAGE,
                    action.getParamValueForKey(TCSNotificationService.KEYNOTIFICATIONMESSAGE));
            notificationIntent.putExtra(TCSNotificationService.KEYNOTIFICATIONACTIONUID,
                    action.getUid());
            notificationIntent.putExtra(TCSNotificationService.KEYNOTIFICATIONACTIONICON, appIcon);

            applicationContext.startService(notificationIntent);
        } else //Otherwise the app is open - Show the message to the user with an option to do something about it.
        {
            String title = action.getParamValueForKey(TCSAppAction.KEY_NOTIFICATION_TITLE);
            String message = action.getParamValueForKey(TCSAppAction.KEY_NOTIFICATION_MESSAGE);

            if (appActionDelegate != null) {
                Context context = appActionDelegate.getContextForMessage();

                AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                        .setTitle(title)
                        .setMessage(message)
                        .setIcon(appIcon);

                dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (action.getActionType() != TCSAppAction.TCSAppActionType.ATYPE_MESSAGE) {
                            appActionDelegate.didReceiveAppAction(action); // Not just a message.
                        }

                        TCSEvent event = new TCSEvent();
                        event.setEventType(TCSEventManager.VAL_USER_ACCEPT_ACTION);
                        JSONObject values = new JSONObject();
                        try {
                            values.put(TCSEvent.KEYVALUE01, action.getUid());
                        } catch (Exception e) {

                        }
                        event.values = values;
                        TCSEventManager.getInstance().logEvent(event);
                    }
                });

                if (action.getActionType() != TCSAppAction.TCSAppActionType.ATYPE_MESSAGE) { //Not just a message
                    dialog.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
                dialog.setCancelable(false);
                dialog.show();
            }
        }
    }

    public void getNotifyActionsWithCompletion(final TCSSuccessDelegate delegate) {
        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.GETNOTIFYACTIONS;
        requestArgs.payload = new JSONObject();
        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                if (e == null && responseCode == 200) {
                    try {
                        JSONObject replyObject = new JSONObject(reply);
                        JSONArray appActions = replyObject.getJSONArray(KEY_ACTIONS);
                        for (int i = 0; i < appActions.length(); i++) {
                            TCSAppAction action = new TCSAppAction(appActions.getJSONObject(i));
                            TCSAppInstance.getInstance().addAppAction(action, false);
                            if (delegate != null) {
                                delegate.done(true);
                            }
                        }
                    } catch (JSONException e2) {
                        Log.d("TCSEventManager", e2.getLocalizedMessage());
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
        }
    }

    public void removeAppAction(TCSAppAction action) {
        if (appActionList.contains(action)) {
            appActionList.remove(action);
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(new Intent(kTCSAppActionsUpdated));
        }
    }

    private boolean isInActionList(TCSAppAction action) {
        for (TCSAppAction atn : appActionList) {
            if (atn.getUid().equals(action.getUid())) {
                return true;
            }
        }
        return false;
    }

    public void ReceivedLocalNotification() {
        //TODO - implement
    }

    public void eulaAndPrivacyWasAccepted() {
        SharedPreferences.Editor editor = uDefs.edit();
        editor.putBoolean(KEYEULAACCEPTED, true);
        editor.commit();
    }

    public boolean hasEulaAndPrivacyBeenAccepted() {
        return uDefs.getBoolean(KEYEULAACCEPTED, false);
    }

    public void registerUser(String email, String password, final TCSRequestSuccessDelegate delegate) {
        if (!TCSUtilities.isValidEmail(email)) {
            delegate.done(false, ERROR_INVALID_EMAIL);
        }
        if (password == null || password.isEmpty()) {
            delegate.done(false, ERROR_INVALID_PASSWORD);
        }

        TCSRequestArgs requestArgs = new TCSRequestArgs();

        requestArgs.requestType = TCSAPIConstants.TCSRequestType.REGISTERUSER;

        JSONObject values = new JSONObject();
        try {
            values.put(KEYEMAILADDRESS, email);
            values.put(KEYPASSWORD, password);
        } catch (JSONException e) {

        }

        requestArgs.payload = values;
        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                if (e != null) {
                    delegate.done(false, ERROR_CONNECTION_FAILED);
                } else {
                    try {
                        JSONObject replyObject = new JSONObject(reply);
                        if (responseCode == 201) {
                            endUserDetails = replyObject.getJSONObject(KEYENDUSER);
                            delegate.done(true, SUCCESS);
                        } else {
                            String errorCode = replyObject.getString(KEYERRORCODE);
                            delegate.done(false, errorCode);
                        }
                    } catch (JSONException e2) {
                        delegate.done(false, ERROR_UNKNOWN);
                    }
                }
            }
        };

        try {
            TCSAPIConnector.getInstance().requestWithArgs(requestArgs);
        } catch (Exception e) {
            delegate.done(false, ERROR_UNKNOWN);
            e.printStackTrace();
        }
    }

    public void registerUserParameters(Map<String, Object> params, final TCSDataReturnedDelegate delegate) {
        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.REGISTERUSER;

//        requestArgs.payload = new JSONObject(params);
        JSONObject object = new JSONObject(params);
        object.remove("attributes");
        JSONObject attr = new JSONObject((Map) params.get("attributes"));
        try {
            object.put("attributes", attr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestArgs.payload = object;
        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                JSONObject data = null;
                Error error = null;

                if (e != null) {
                    error = new Error(e.getMessage(), e);
                } else {
                    try {
                        data = new JSONObject(reply);
                        if (responseCode == 201) {

                        } else {
                            error = new Error(ERROR_REQUEST_FAILED, null);
                        }
                    } catch (JSONException e1) {
                        error = new Error(ERROR_REQUEST_FAILED, e1);
                    }
                }
                if (delegate != null) {
                    delegate.done(data, error);
                }
            }
        };

        try {
            TCSAPIConnector.getInstance().requestWithArgs(requestArgs);
        } catch (Exception e) {
            delegate.done(null, new Error(e.getMessage(), e));
        }
    }

    public void loginUser(String email, String password, final TCSRequestSuccessDelegate delegate) {
        if (email == null) {
            delegate.done(false, ERROR_INVALID_EMAIL);
            return;
        }
        if (password == null) {
            delegate.done(false, ERROR_INVALID_PASSWORD);
            return;
        }

        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.LOGINUSER;

        JSONObject values = new JSONObject();
        try {
            values.put(KEYUSERNAME, email);
            values.put(KEYPASSWORD, password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        requestArgs.payload = values;
        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                if (e != null) {
                    delegate.done(false, ERROR_CONNECTION_FAILED);
                } else {
                    try {
                        JSONObject replyObject = new JSONObject(reply);

                        if (responseCode == 200) {
                            endUserAccessToken = replyObject.getString(KEYENDUSERACCESSTOKEN);

                            SharedPreferences.Editor editor = uDefs.edit();
                            editor.putString(KEYENDUSERACCESSTOKEN, endUserAccessToken);
                            editor.commit();

                            enableSparkTags(true);

                            endUserDetails = replyObject.getJSONObject(KEYENDUSER);
                            delegate.done(true, reply);
                        } else {
                            String errorCode = replyObject.getString(KEYERRORCODE);
                            delegate.done(false, errorCode);
                        }
                    } catch (JSONException e2) {
                        delegate.done(false, ERROR_UNKNOWN);
                    }
                }
            }
        };

        try {
            TCSAPIConnector.getInstance().requestWithArgs(requestArgs);
        } catch (Exception e) {
            delegate.done(false, ERROR_UNKNOWN);
        }
    }

    public void updateUserEmail(String email, String password, final TCSRequestSuccessDelegate delegate) {
        if (email == null) {
            delegate.done(false, ERROR_INVALID_EMAIL);
            return;
        }
        if (password == null) {
            delegate.done(false, ERROR_INVALID_PASSWORD);
            return;
        }

        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.CHANGEEMAIL;

        JSONObject values = new JSONObject();
        try {
            values.put(KEYEMAILADDRESS, email);
            values.put(KEYPASSWORD, password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        requestArgs.payload = values;
        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                if (e != null) {
                    delegate.done(false, ERROR_CONNECTION_FAILED);
                } else if (responseCode == 200) {
                    delegate.done(true, SUCCESS);
                } else {
                    try {
                        JSONObject replyObject = new JSONObject(reply);
                        String errorCode = replyObject.getString(KEYERRORCODE);
                        delegate.done(false, errorCode);
                    } catch (JSONException e2) {
                        delegate.done(false, ERROR_UNKNOWN);
                    }
                }
            }
        };

        try {
            TCSAPIConnector.getInstance().requestWithArgs(requestArgs);
        } catch (Exception e) {
            delegate.done(false, ERROR_UNKNOWN);
        }
    }

    public void updateUserPassword(String oldPassword, String newPassword, final TCSRequestSuccessDelegate delegate) {
        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.CHANGEPASSWORD;

        JSONObject values = new JSONObject();
        try {
            values.put(KEYOLDPASSWORD, oldPassword);
            values.put(KEYNEWPASSWORD, newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
            delegate.done(false, ERROR_UNKNOWN);
        }

        requestArgs.payload = values;
        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                if (e != null) {
                    delegate.done(false, ERROR_CONNECTION_FAILED);
                } else {
                    try {
                        JSONObject replyObject = new JSONObject(reply);
                        if (responseCode == 200) {
                            endUserAccessToken = replyObject.getString(KEYENDUSERACCESSTOKEN);

                            SharedPreferences.Editor editor = uDefs.edit();
                            editor.putString(KEYENDUSERACCESSTOKEN, endUserAccessToken);
                            editor.commit();

                            delegate.done(true, SUCCESS);
                        } else {
                            String errorCode = replyObject.getString(KEYERRORCODE);
                            delegate.done(false, errorCode);
                        }
                    } catch (JSONException e2) {
                        delegate.done(false, ERROR_UNKNOWN);
                    }
                }
            }
        };

        try {
            TCSAPIConnector.getInstance().requestWithArgs(requestArgs);
        } catch (Exception e) {
            delegate.done(false, ERROR_UNKNOWN);
        }
    }

    public void updateUserProfile(JSONObject profile, final TCSRequestSuccessDelegate delegate) {
        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.UPDATEUSERPROFILE;
        requestArgs.payload = profile;
        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                if (e != null) {
                    delegate.done(false, ERROR_CONNECTION_FAILED);
                } else if (responseCode == 200) {
                    delegate.done(true, SUCCESS);
                } else {
                    try {
                        JSONObject replyObject = new JSONObject(reply);
                        String errorCode = replyObject.getString(KEYERRORCODE);
                        delegate.done(false, errorCode);
                    } catch (JSONException e2) {
                        delegate.done(false, ERROR_UNKNOWN);
                    }
                }
            }
        };

        try {
            TCSAPIConnector.getInstance().requestWithArgs(requestArgs);
        } catch (Exception e) {
            delegate.done(false, ERROR_UNKNOWN);
        }
    }

    public void forgotUserPassword(String email, final TCSRequestSuccessDelegate delegate) {
        if (!TCSUtilities.isValidEmail(email)) {
            delegate.done(false, ERROR_INVALID_EMAIL);
            return;
        }

        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.FORGOTPASSWORD;

        JSONObject values = new JSONObject();
        try {
            values.put(KEYEMAILADDRESS, email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        requestArgs.payload = values;
        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                if (e != null) {
                    delegate.done(false, ERROR_CONNECTION_FAILED);
                } else if (responseCode == 200) {
                    delegate.done(true, SUCCESS);
                } else {
                    try {
                        JSONObject replyObject = new JSONObject(reply);
                        String errorCode = replyObject.getString(KEYERRORCODE);
                        delegate.done(false, errorCode);
                    } catch (JSONException e2) {
                        delegate.done(false, ERROR_UNKNOWN);
                    }
                }
            }
        };

        try {
            TCSAPIConnector.getInstance().requestWithArgs(requestArgs);
        } catch (Exception e) {
            delegate.done(false, ERROR_UNKNOWN);
        }
    }

    public void getUserProfile(final TCSUserProfileDelegate delegate) {
        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.GETUSERPROFILE;

        JSONObject values = new JSONObject();
        try {
            values.put(userProfileCacheVersion, JKEY_CACHEVERSION);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        requestArgs.payload = values;

        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                if (e != null) {
                    delegate.done(false, null);
                } else {
                    try {
                        JSONObject replyObject = new JSONObject(reply);
                        if (responseCode == 200) {
                            String cacheStatus = replyObject.getString(JKEY_CACHESTATUS);
                            if (cacheStatus.equals(OUT_OF_SYNC)) {
                                userProfileCacheVersion = replyObject.getString(JKEY_CACHEVERSION);
                                endUserDetails = replyObject.getJSONObject(KEYENDUSER);
                            }
                            delegate.done(true, endUserDetails);
                        }
                    } catch (JSONException e2) {
                        delegate.done(false, null);
                    }
                }
            }
        };

        try {
            TCSAPIConnector.getInstance().requestWithArgs(requestArgs);
        } catch (Exception e) {
            delegate.done(false, null);
        }
    }

    public void getAppUIDByCode(String appCode, final TCSDataReturnedDelegate delegate) {
        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.GETAPPUID;
        ContentValues values = new ContentValues();
        values.put("appCode", appCode);
        requestArgs.getParams = values;
        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                JSONObject data = null;
                Error error = null;

                if (e != null) {
                    error = new Error(e.getMessage(), e);
                } else {
                    try {
                        data = new JSONObject(reply);
                        if (responseCode == 200) {
                            //Fine don't need to do anything else
                        } else {
                            error = new Error(ERROR_REQUEST_FAILED, null);
                        }
                    } catch (JSONException e1) {
                        error = new Error(ERROR_REQUEST_FAILED, e1);
                    }
                }
                if (delegate != null) {
                    delegate.done(data, error);
                }
            }
        };

        try {
            TCSAPIConnector.getInstance().requestWithArgs(requestArgs);
        } catch (Exception e) {
            delegate.done(null, new Error(e.getMessage(), e));
        }
    }

    /**
     * Get Questionnaires
     *
     * @param delegate
     */
    public void getQuestionnaires(final TCSSuccessDelegate delegate) {
        JSONObject payload = new JSONObject();
        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.GETQUESTIONS;
        requestArgs.payload = payload;
        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                if (e == null && responseCode == 200) {
                    try {
                        JSONObject questionnairesJSON = new JSONObject(reply);
                        JSONArray questionnaires = questionnairesJSON.getJSONArray("questionnaires");
                        JSONArray questions = questionnairesJSON.getJSONArray("questions");
                        JSONArray choices = questionnairesJSON.getJSONArray("questionChoices");
                        for (int i = 0; i < questionnaires.length(); i++) {
                            TCSQuestionnaire questionnaire = new TCSQuestionnaire(questionnaires.getJSONObject(i));
                            questionnaire.createQuestions(questions, choices);
                            questionnairesDefinitions.put(questionnaire.getThisId(), questionnaire);
                        }

                        delegate.done(true);
                    } catch (JSONException ej) {
                        delegate.done(false);
                    }
                } else {
                    delegate.done(false);
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

    public void sendAnswersForQuestionnaireId(int questionnaireId, JSONArray answers, final TCSSuccessDelegate delegate) {
        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.SENDANSWERS;


        JSONObject values = new JSONObject();
        try {
            values.put("answerList", answers);
            JSONObject questionnaireIdJSON = new JSONObject();
            questionnaireIdJSON.put("questionnaireId", questionnaireId);
            values.put("answers", questionnaireIdJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        requestArgs.payload = values;
        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                if (e != null) {
                    delegate.done(false);
                } else {
                    if (responseCode == 200 || responseCode == 201) {
                        delegate.done(true);
                    } else {
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

    public void setImageProfile(String PathFile, final TCSDataReturnedDelegate delegate) {
        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.PROFILEIMAGE;
        JSONObject values = new JSONObject();
        try {
            values.put(KEYPROFILEIMAGE, PathFile);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        requestArgs.payload = values;
        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                JSONObject data = null;
                Error error = null;

                if (e != null) {
                    error = new Error(e.getMessage(), e);
                } else {
                    try {
                        data = new JSONObject(reply);
                        if (responseCode == 200) {

                        } else {
                            error = new Error(ERROR_REQUEST_FAILED, null);
                        }
                    } catch (JSONException e1) {
                        error = new Error(ERROR_REQUEST_FAILED, e1);
                    }
                }
                if (delegate != null) {
                    delegate.done(data, error);
                }
            }
        };

        try {
            TCSAPIConnector.getInstance().requestWithArgs(requestArgs);
        } catch (Exception e) {
            delegate.done(null, new Error(e.getMessage(), e));
        }
    }

    /* terms received url */
    public void getTerms(final TCSDataReturnedDelegate delegate) {
        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.GETTERMS;
        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                JSONObject data = null;
                Error error = null;

                if (e != null) {
                    error = new Error(e.getMessage(), e);
                } else {
                    try {
                        data = new JSONObject(reply);
                        if (responseCode == 200) {

                        } else {
                            error = new Error(ERROR_REQUEST_FAILED, null);
                        }
                    } catch (JSONException e1) {
                        error = new Error(ERROR_REQUEST_FAILED, e1);
                    }
                }
                if (delegate != null) {
                    delegate.done(data, error);
                }
            }
        };

        try {
            TCSAPIConnector.getInstance().requestWithArgs(requestArgs);
        } catch (Exception e) {
            delegate.done(null, new Error(e.getMessage(), e));
        }
    }

    /* accepted terms */
    public void acceptTerms( final TCSRequestSuccessDelegate delegate) {
        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.ACCEPTTERMS;
        JSONObject values = new JSONObject();
        requestArgs.payload = values;
        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                if (e != null) {
                    delegate.done(false, ERROR_CONNECTION_FAILED);
                } else if (responseCode == 200) {
                    delegate.done(true, SUCCESS);
                } else {
                    try {
                        JSONObject replyObject = new JSONObject(reply);
                        String errorCode = replyObject.getString(KEYERRORCODE);
                        delegate.done(false, errorCode);
                    } catch (JSONException e2) {
                        delegate.done(false, ERROR_UNKNOWN);
                    }
                }
            }
        };
        try {
            TCSAPIConnector.getInstance().requestWithArgs(requestArgs);
        } catch (Exception e) {
            delegate.done(false, ERROR_UNKNOWN);
        }
    }

    /**
     * Is the user logged in?
     *
     * @return whether the user is logged in or not
     */
    public boolean userIsLoggedIn() {
        return endUserAccessToken != null && !endUserAccessToken.isEmpty();
    }

    /**
     * Logs out the end user.
     */
    public void logOutEndUser() {
        endUserAccessToken = null;
        SharedPreferences.Editor editor = uDefs.edit();
        editor.putString(KEYENDUSERACCESSTOKEN, "");
        endUserDetails = null;
        editor.commit();
    }

    public boolean isLocationsEnabled() {
        return uDefs == null ? false : uDefs.getBoolean(PREF_LOCATION_ENABLED, false);
    }

    public boolean isBluetoothEnabled() {

        return uDefs == null ? false : uDefs.getBoolean(PREF_FYX_ENABLED, false);
    }

    public boolean isNotificationsEnabled() {
        return uDefs == null ? false : uDefs.getBoolean(PREF_MESSAGES_ENABLED, false);
    }

    public void setLocationsEnabled(boolean enabled, TCSSuccessDelegate delegate) {
        SharedPreferences.Editor editor = uDefs.edit();
        editor.putBoolean(PREF_LOCATION_ENABLED, enabled);
        editor.commit();

        if (enabled) {
            TCSGeoManager.getInstance().setEnabled(true);
            TCSGeoManager.getInstance().startGeoManager();
            delegate.done(true);
        } else {
            TCSGeoManager.getInstance().setEnabled(false);
            delegate.done(false);
        }
    }

    public void setBluetoothEnabled(boolean enabled, TCSSuccessDelegate delegate) {
        SharedPreferences.Editor editor = uDefs.edit();
        editor.putBoolean(PREF_FYX_ENABLED, enabled);
        editor.commit();

        if (enabled) {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                delegate.done(false);
                return;
            } else {
                if (!mBluetoothAdapter.isEnabled()) {
                    delegate.done(false);
                    return;
                } else {
//                    TCSFYXManager.getInstance().setEnabled(true);
//                    TCSFYXManager.getInstance().startFYX();
                    GimbalManager.getInstance().startMonitoring();
                    delegate.done(true);
                }
            }
        } else {
//            TCSFYXManager.getInstance().setEnabled(false);
//            TCSFYXManager.getInstance().clearTransmitters();
            GimbalManager.getInstance().clearTransmitters();
            delegate.done(false);
        }
    }

    public void setNotificationsEnabled(boolean enabled, TCSSuccessDelegate delegate) {
        SharedPreferences.Editor editor = uDefs.edit();
        editor.putBoolean(PREF_MESSAGES_ENABLED, enabled);
        editor.commit();

        delegate.done(enabled);
    }

    public boolean supportsBeacons() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    public void setRememberedAppUID(String uid) {
        SharedPreferences.Editor editor = uDefs.edit();
        editor.putString(KEYREMEMBEREDAPPUID, uid);
        editor.apply();
    }

    public String getRememberedAppUID() {
        if (uDefs != null) {
            return uDefs.getString(KEYREMEMBEREDAPPUID, null);
        }
        return null;
    }

    public void setAdvertisingId(String advertId) {
        SharedPreferences.Editor editor = uDefs.edit();
        editor.putString(KEYADVERTISINGID, advertId);
        editor.apply();
    }

    public void enableSparkTags(boolean enable) {
        SharedPreferences.Editor editor = uDefs.edit();
        editor.putBoolean(KEYSPARKTAGSENABLE, enable);
        editor.apply();
    }

    public boolean isSparkTagsEnabled() {
        return uDefs.getBoolean(KEYSPARKTAGSENABLE, false);
    }

    public String getAdvertisingId() {
        if (uDefs != null) {
            return uDefs.getString(KEYADVERTISINGID, null);
        }

        return null;
    }

    public TCSSkin getSelectedSkin() {
        return appSkin;
    }

    public void setRunningInBackground(boolean runningInBackground) {
        this.runningInBackground = runningInBackground;
    }

    public boolean isRunningInBackground() {
        return runningInBackground;
    }

    public void turnOnLocationServices(Context applicationContext) {
        if (uDefs == null) {
            uDefs = applicationContext.getSharedPreferences(applicationContext.getPackageName(), Context.MODE_PRIVATE);
        }

        SharedPreferences.Editor editor = uDefs.edit();
        editor.putBoolean(PREF_LOCATION_ENABLED, true);
        editor.putBoolean(PREF_FYX_ENABLED, true);
        editor.commit();

        TCSGeoManager.getInstance().setEnabled(true);
    }

    public void turnOffLocationServices(Context applicationContext) {
        if (uDefs == null) {
            uDefs = applicationContext.getSharedPreferences(applicationContext.getPackageName(), Context.MODE_PRIVATE);
        }

        SharedPreferences.Editor editor = uDefs.edit();
        editor.putBoolean(PREF_LOCATION_ENABLED, false);
        editor.putBoolean(PREF_FYX_ENABLED, false);
        editor.commit();

        TCSGeoManager.getInstance().setEnabled(false);

    }

    public void setPointsUpdater(TCSSuccessDelegate delegate) {
        this.pointsUpdatedDelegate = delegate;
    }

    public void triggerPointsUpdater() {
        if (this.pointsUpdatedDelegate != null) {
            this.pointsUpdatedDelegate.done(true);
        }
    }

    public void getTemplates(final TCSSuccessDelegate delegate) throws Exception {
        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.GETTEMPLATES;

        JSONObject payload = new JSONObject();
        requestArgs.payload = payload;

        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                if (e == null && responseCode == 200) {
                    try {
                        TCSAppInstance.getInstance().setAppSkin(new TCSSkin(new JSONObject(reply)));
                        if (delegate != null) {
                            delegate.done(true);
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                        if (delegate != null) {
                            delegate.done(false);
                        }
                    }
                } else {
                    if (delegate != null) {
                        delegate.done(false);
                    }
                }
            }
        };
        TCSAPIConnector.getInstance().requestWithArgs(requestArgs);
    }

}
