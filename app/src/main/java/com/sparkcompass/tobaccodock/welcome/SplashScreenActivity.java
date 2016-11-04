package com.sparkcompass.tobaccodock.welcome;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.pacificfjord.pfapi.TCSAppInstance;
import com.pacificfjord.pfapi.TCSSuccessDelegate;
import com.pacificfjord.pfapi.views.TCSSkin;
import com.sparkcompass.tobaccodock.BuildConfig;
import com.sparkcompass.tobaccodock.R;
import com.sparkcompass.tobaccodock.common.Constants;
import com.sparkcompass.tobaccodock.common.TCSActivity;
import com.sparkcompass.tobaccodock.common.TCSService;
import com.sparkcompass.tobaccodock.common.UserPreferences;
import com.sparkcompass.tobaccodock.eulapp.TermsDialog;
import com.sparkcompass.tobaccodock.home.MainActivity;
import com.sparkcompass.tobaccodock.utils.NetworkUtils;

public class SplashScreenActivity extends TCSActivity implements DialogInterface.OnDismissListener {

    private ApplicationReadyBroadcastReceiver broadcastReceiver;
    private boolean appStatus;
    private boolean hasFeaturedEvents;
    public static final String HAS_FEATURED_EVENTS = "has_featured_events";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (NetworkUtils.isNetworkAvailable(this)) {
            broadcastReceiver = new ApplicationReadyBroadcastReceiver();
            IntentFilter intentFilter = new IntentFilter(Constants.APP_READY);
            registerReceiver(broadcastReceiver, intentFilter);

            TCSAppInstance.getInstance().setupAppFromApplication(Constants.PRODUCTION_API_KEY, getApplication());
            int fineLocationPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

            if (fineLocationPermissionCheck != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    final AlertDialog dialog = new AlertDialog.Builder(this)
                            .setTitle("Proximity Information")
                            .setMessage(
                                    "This feature makes use of location services in order to detect proximity beacons related to nearby places. Would you like to review your permissions for this app.")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat
                                            .requestPermissions(SplashScreenActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                    Constants.RR_REQUEST_FINE_LOCATION);
                                }
                            }).setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create();
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface arg0) {
                            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(SplashScreenActivity.this, R.color.primary));
                            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(SplashScreenActivity.this, R.color.primary));
                        }
                    });
                    dialog.show();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            Constants.RR_REQUEST_FINE_LOCATION);
                }
            } else {
                if (BuildConfig.VERSION_CODE < 23) {
                    TCSAppInstance.getInstance().turnOnLocationServices(getApplicationContext());
                }
                startApp();
            }

        } else {
            notConnectedToInternet();
        }

        FacebookSdk.sdkInitialize(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.RR_REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (TCSAppInstance.getInstance().isAppActive()) {
                        TCSAppInstance.getInstance().setLocationsEnabled(true, new TCSSuccessDelegate() {
                            @Override
                            public void done(boolean success) {

                            }
                        });
                    } else {
                        TCSAppInstance.getInstance().turnOnLocationServices(getApplicationContext());
                    }
                } else {
                    TCSAppInstance.getInstance().turnOffLocationServices(getApplicationContext());
                }

                startApp();
            }
        }
    }

    private void startApp() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!UserPreferences.getTermsAccepted(SplashScreenActivity.this)) {
                    TermsDialog dialog = new TermsDialog(SplashScreenActivity.this);
                    dialog.setOnDismissListener(SplashScreenActivity.this);
                    dialog.show();
                } else if (isServiceRunning(TCSService.class)) {
                    stopService(new Intent(SplashScreenActivity.this, TCSService.class));
                    Intent intent = new Intent(getApplicationContext(), TCSService.class);
                    startService(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), TCSService.class);
                    startService(intent);
                }
            }
        }, 1000);
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (isServiceRunning(TCSService.class)) {
            stopService(new Intent(SplashScreenActivity.this, TCSService.class));
        }

        Intent intent = new Intent(getApplicationContext(), TCSService.class);
        startService(intent);
    }

    @Override
    public void customizeViews(TCSSkin skinTemplate) {

    }

    private void notConnectedToInternet() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.internet_connection))
                .setMessage(getResources().getString(R.string.internet_connection_message))
                .setNeutralButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });

        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(ContextCompat.getColor(SplashScreenActivity.this, R.color.primary));
            }
        });
        alertDialog.show();
    }

    private void validateNextActivity() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Class nextActivity = hasFeaturedEvents ? SwitchAppActivity.class : MainActivity.class;

                if (appStatus) {
                    Intent intent = new Intent(SplashScreenActivity.this, nextActivity);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    finish();
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreenActivity.this);
                    builder.setTitle(getResources().getString(R.string.connection_error))
                            .setMessage(getResources().getString(R.string.connection_error_message))
                            .setNeutralButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface arg0) {
                            alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(ContextCompat.getColor(SplashScreenActivity.this, R.color.primary));
                        }
                    });
                    alertDialog.show();
                }
            }
        }, 1000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (broadcastReceiver != null) {
            try {
                unregisterReceiver(broadcastReceiver);
            } catch (Exception ex) {
                Log.e("ON_STOP", ex.getMessage());
            }
        }
    }


    public class ApplicationReadyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.APP_READY)) {
                appStatus = intent.getBooleanExtra(Constants.STATUS, false);
                hasFeaturedEvents = intent.getBooleanExtra(HAS_FEATURED_EVENTS, false);
                validateNextActivity();
            }
        }
    }
}
