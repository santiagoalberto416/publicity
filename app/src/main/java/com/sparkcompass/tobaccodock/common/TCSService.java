package com.sparkcompass.tobaccodock.common;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.pacificfjord.pfapi.TCSAPIConnector;
import com.pacificfjord.pfapi.TCSAppInstance;
import com.pacificfjord.pfapi.TCSAppInstanceBeginDelegate;
import com.pacificfjord.pfapi.TCSGeoManager;
import com.pacificfjord.pfapi.TCSMenu;
import com.pacificfjord.pfapi.TCSSuccessDelegate;
import com.pacificfjord.pfapi.beacons.GimbalManager;
import com.sparkcompass.tobaccodock.BuildConfig;
import com.sparkcompass.tobaccodock.welcome.SplashScreenActivity;


/**
 * Created by Aaron Vega on 2/9/16.
 */
public class TCSService extends Service {
    public static final String FEATURED_APPS = "FeaturedApps";
    private String[] menus = {Constants.MAIN_MENU, Constants.TOOLBAR_MENU, Constants.BANNER_MENU, Constants.TWITTER, FEATURED_APPS};
    private int syncedMenusCounter = 0;

    private boolean fromIntent = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("**********", "TCSService started");

        String gimbalKey;
            TCSAppInstance.getInstance()
                    .setupAppFromApplication(Constants.PRODUCTION_API_KEY, getApplication());
            TCSAPIConnector.getInstance().appURL = Constants.PRODUCTION_API_URL;
            TCSAPIConnector.getInstance().baseURL = Constants.PRODUCTION_API_URL;
            gimbalKey = Constants.GIMBAL_KEY_PRODUCTION;


        if (TCSAppInstance.getInstance().supportsBeacons()) {
            GimbalManager.registerApp(getApplication(), gimbalKey);
        }

        TCSAppInstance.getInstance().enableSparkTags(false);

        fromIntent = intent != null;
        TCSAppInstance.getInstance().setRunningInBackground(!fromIntent);

        startApplicationInstance();

        registerReceiver(broadcastReceiver, new IntentFilter(Constants.KEY_RESTART_APP));

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        TCSGeoManager.getInstance().stopGeoManager();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private void startApplicationInstance() {
        if (!TCSAppInstance.getInstance().isAppActive()) {
            TCSAppInstance.getInstance()
                    .beginWithCallback(new TCSAppInstanceBeginDelegate() {
                                           @Override
                                           public void done(boolean success, String reply) {
                                               if (success) {
                                                   Log.d("*****", "AppInstance began successfully");
                                                   registerGCMService();
                                                   syncApplicationContent();
                                                   TCSAppInstance.getInstance().updateGCMRegistrationKey();
                                                   if (!fromIntent) {
                                                       TCSAppInstance.getInstance().setApplicationIsAwake(false);
                                                       TCSAppInstance.getInstance().setTargetNotificationClass(SplashScreenActivity.class);
                                                   }
                                               } else {
                                                   Log.d("******", "AppInstance did not begin successfully");
                                                   notifyAppIsReady(false);
                                               }
                                           }
                                       }

                    );
        } else {
            notifyAppIsReady(true);
        }
    }

    private void registerGCMService() {
        new RegisterGCMTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void syncApplicationContent() {
        try {
            TCSAppInstance.getInstance().getTemplates(new TCSSuccessDelegate() {
                @Override
                public void done(boolean success) {
                    if (success) {
                        syncApplicationMenus();
                    } else {
                        notifyAppIsReady(false);
                    }
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void syncApplicationMenus() {

        TCSAppInstance.getInstance().getQuestionnaires(new TCSSuccessDelegate() {
            @Override
            public void done(boolean success) {
                if (success) {
                    Log.v("Questionnaires", "Update success");
                } else {
                    Log.v("Questionnaires", "Update failed");
                }
            }
        });

        for (final String menu : menus) {
            TCSAppInstance.getInstance().syncMenu(menu, new TCSSuccessDelegate() {
                @Override
                public void done(boolean success) {
                    if (success) {
                        Log.d(Constants.LOG_TAG, menu + " success");
                    } else {
                        Log.d(Constants.LOG_TAG, menu + " failed");
                    }

                    syncedMenusCounter++;

                    if (syncedMenusCounter == menus.length) {
                        notifyAppIsReady(true);
                        syncedMenusCounter = 0;
                    }
                }
            });
        }
    }

    public void notifyAppIsReady(boolean status) {
        Log.i("********", "Notifying App Is Ready status " + status);
        Intent intent = new Intent(Constants.APP_READY);
        intent.putExtra(Constants.STATUS, status);
        TCSMenu featuredEventsMenu = TCSAppInstance.getInstance().menuWithName(FEATURED_APPS);
        boolean hasFeaturedEvents = featuredEventsMenu != null && featuredEventsMenu.menuItems.size() > 0 ? true : false;
        intent.putExtra(SplashScreenActivity.HAS_FEATURED_EVENTS, hasFeaturedEvents);
        sendBroadcast(intent);
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(Constants.KEY_RESTART_APP)) {
                startApplicationInstance();
            }
        }
    };
}
