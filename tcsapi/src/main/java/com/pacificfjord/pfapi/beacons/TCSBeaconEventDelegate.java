package com.pacificfjord.pfapi.beacons;

import com.pacificfjord.pfapi.utilites.TCSTransmitter;

/**
 * Created by Aaron Vega on 9/8/16.
 */
public interface TCSBeaconEventDelegate {

    void onBeaconSighting(TCSTransmitter transmitter);

    void onDidDepart(TCSTransmitter transmitter);

    void syncAdditionalBeacons();
}
