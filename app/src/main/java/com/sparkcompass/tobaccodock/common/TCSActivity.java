package com.sparkcompass.tobaccodock.common;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.facebook.CallbackManager;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.pacificfjord.pfapi.TCSAppInstance;
import com.pacificfjord.pfapi.TCSEvent;
import com.pacificfjord.pfapi.TCSEventManager;
import com.pacificfjord.pfapi.views.TCSSkin;
import com.sparkcompass.tobaccodock.R;
import com.sparkcompass.tobaccodock.home.MainActivity;
import com.sparkcompass.tobaccodock.login.GoogleSignInListener;
import com.sparkcompass.tobaccodock.login.GoogleSignInSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;

public abstract class TCSActivity extends AppCompatActivity implements TemplateView, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleSignInListener {

    @Bind(R.id.header_toolbar)
    protected RelativeLayout headerToolbar;

    protected CallbackManager callbackManager;
    protected static final int RC_SIGN_IN = 0;
    protected GoogleApiClient mGoogleApiClient;

    /* Is there a ConnectionResult resolution in progress? */
    protected boolean mIsResolving = false;
    /* Should we automatically resolve ConnectionResults when possible? */
    protected boolean mShouldResolve = false;
    protected GoogleSignInSuccessListener googleListener;
    public static boolean didAppWentToBg = false;
    public static boolean windowHasFocus = false;
    public static boolean backButtonPressed = false;


    @Override
    protected void onStart() {
        applicationWillEnterForeground();
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();

        TCSAppInstance.getInstance().setTargetNotificationClass(this.getClass());
    }

    @Override
    protected void onResume() {
        super.onResume();
        customizeViews(TCSAppInstance.getInstance().getSelectedSkin());
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            if(TCSAppInstance.getInstance().getRememberedAppUID() != null && !TCSAppInstance.getInstance().getRememberedAppUID().isEmpty()) {
                TCSAppInstance.getInstance().setAppUID(TCSAppInstance.getInstance().getRememberedAppUID());
            }

            Parcelable[] rawMsgs = getIntent().getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if(rawMsgs != null) {
                NdefMessage[] msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }

                String tagId = new String(msgs[0].getRecords()[0].getPayload()).substring(3);
                TCSEvent event = new TCSEvent();
                event.setEventType(TCSEventManager.VAL_IMAGE_TARGET_RECOGNITION);
                JSONObject values = new JSONObject();
                try {
                    values.put(TCSEvent.KEYVALUE01, tagId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                event.values = values;

                TCSEventManager.getInstance().logEvent(event);

                Log.i("Message",tagId);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        applicationDidEnterBackground();
    }

    @Override
    public void onBackPressed() {
        if (this instanceof MainActivity) {
            super.onBackPressed();
        } else {
            backButtonPressed = true;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        windowHasFocus = hasFocus;
        if (backButtonPressed && !hasFocus) {
            backButtonPressed = false;
            windowHasFocus = true;
        }
        super.onWindowFocusChanged(hasFocus);
    }

    private void applicationWillEnterForeground() {
        if (didAppWentToBg) {
            didAppWentToBg = false;
            Intent intent = new Intent(TCSAppInstance.APPRESUME);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            AppEventsLogger.activateApp(this);
        }
    }

    public void applicationDidEnterBackground() {
        if (!windowHasFocus) {
            didAppWentToBg = true;
            Intent intent = new Intent(TCSAppInstance.APPPAUSE);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            AppEventsLogger.deactivateApp(this);

        }
    }

    @Override
    public void customizeViews(TCSSkin skinTemplate) {

        if(headerToolbar != null) {
            headerToolbar.getBackground().setColorFilter(Color.parseColor(skinTemplate.getColor()), PorterDuff.Mode.SRC_ATOP);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor(skinTemplate.getPrimaryColorDark()));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 64206:
                callbackManager.onActivityResult(requestCode, resultCode, data);
                break;
            case 3672:
//                LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
//                break;
            case RC_SIGN_IN:
                if (resultCode != RESULT_OK) {
                    mShouldResolve = false;
                }
                mIsResolving = false;
                mGoogleApiClient.connect();
                break;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mShouldResolve = false;
        if (googleListener != null) {
            googleListener.onConnectionSuccess(mGoogleApiClient);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (googleListener != null) {
            googleListener.onConnectionFailed();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else if (googleListener != null) {
                googleListener.onConnectionFailed();
            }
        }
    }

    @Override
    public void onGoogleSignInButtonPressed() {
        mShouldResolve = true;
        mGoogleApiClient.connect();
    }
}
