package com.sparkcompass.tobaccodock.common;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.pacificfjord.pfapi.TCSAppInstance;

import java.io.IOException;

/**
 * Created by mind-p6 on 8/28/15.
 */
public class RegisterGCMTask extends AsyncTask<Void, Void, Void> {

    private Context context;
    private GoogleCloudMessaging gcm;

    public RegisterGCMTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(context);
            }

            String regId = gcm.register(Constants.GCM_SENDER_ID);
            TCSAppInstance.getInstance().setGCMRegistrationKey(regId);
            Log.d(Constants.LOG_TAG, "Device registered, registration ID=" + regId);
        } catch (IOException ex) {
            Log.d(Constants.LOG_TAG, "Error :" + ex.getMessage());
        }
        return null;
    }
}
