package com.sparkcompass.tobaccodock.login;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by danielalcantara on 11/4/15.
 */
public interface GoogleSignInSuccessListener {
    void onConnectionSuccess(GoogleApiClient googleApiClient);

    void onConnectionFailed();
}
