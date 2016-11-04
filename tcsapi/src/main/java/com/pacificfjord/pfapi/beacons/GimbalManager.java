package com.pacificfjord.pfapi.beacons;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.gimbal.android.BeaconEventListener;
import com.gimbal.android.BeaconManager;
import com.gimbal.android.BeaconSighting;
import com.gimbal.android.Communication;
import com.gimbal.android.CommunicationListener;
import com.gimbal.android.CommunicationManager;
import com.gimbal.android.Gimbal;
import com.gimbal.android.PlaceEventListener;
import com.gimbal.android.PlaceManager;
import com.gimbal.android.Push;
import com.gimbal.android.Visit;
import com.pacificfjord.pfapi.TCSAPIConnector;
import com.pacificfjord.pfapi.TCSAPIConstants;
import com.pacificfjord.pfapi.TCSAppInstance;
import com.pacificfjord.pfapi.TCSBeacon;
import com.pacificfjord.pfapi.TCSEvent;
import com.pacificfjord.pfapi.TCSEventManager;
import com.pacificfjord.pfapi.TCSRequestArgs;
import com.pacificfjord.pfapi.TCSRequestSuccessDelegate;
import com.pacificfjord.pfapi.TCSURLConnection;
import com.pacificfjord.pfapi.TCSURLConnectionDelegate;
import com.pacificfjord.pfapi.utilites.TCSTimeUtilities;
import com.pacificfjord.pfapi.utilites.TCSTransmitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Aaron Vega on 4/29/15.
 */
public class GimbalManager {

    private static final String TAG = "TCSFYXManager";
    private static final String JKEY_BEACONS = "beacons";
    private static final String JKEY_BEACON_DWELLS = "dwellDefinitions";

    public static final String BEACONENTRYBEHAVIOUR_BEACONPOP = "DISPLAY";

    protected float mBackgroundResightingInterval = 6 * 60 * 60 * 1000; //6 hours in milliseconds
    protected float inactiveInterval = 10 * 1000;

    protected String beaconCacheVersion = "";
//    public static String goTagsCacheVersion = "";

    protected Map<String, TCSBeacon> beaconDefinitions = new HashMap<>();
    protected Map<String, TCSBeaconDwellDefinition> beaconDwellDefinitions = new HashMap<String, TCSBeaconDwellDefinition>();
    private final ConcurrentHashMap<String, TCSTransmitter> transmitters = new ConcurrentHashMap<>();
    protected ArrayList<TCSTransmitter> closestTransmitters = new ArrayList<TCSTransmitter>();

    protected final int mMaxBeaconPops = 20;

    protected BeaconPopDelegate beaconPopDelegate = null;
    protected BeaconPopCountDelegate beaconPopCountDelegate = null;

    private static GimbalManager gimbalManager;

    private BeaconManager beaconManager;

    private TCSBeaconEventDelegate beaconEventDelegate;

    private PlaceEventListener placeEventListener = new PlaceEventListener() {
        @Override
        public void onVisitStart(Visit visit) {
            super.onVisitStart(visit);
            Log.i("Info:", "Enter: " + visit.getPlace().getName() + ", at: " + new Date(visit.getArrivalTimeInMillis()));
        }

        @Override
        public void onVisitEnd(Visit visit) {
            super.onVisitEnd(visit);
            Log.i("Info:", "Exit: " + visit.getPlace().getName() + ", at: " + new Date(visit.getDepartureTimeInMillis()));
        }

        @Override
        public void onBeaconSighting(BeaconSighting beaconSighting, List<Visit> list) {
            super.onBeaconSighting(beaconSighting, list);


            Visit visit = list.get(0);
            if (visit != null) {
                TCSBeaconDwellDefinition dwellDefinition = beaconDwellDefinitions.get(visit.getPlace().getName());
                if (dwellDefinition != null) {
                    dwellDefinition.reset();
                    int dwell = dwellDefinition.testDwellTime((int) visit.getDwellTimeInMillis() / 1000);
                    if (visit.getDwellTimeInMillis() < 0) {
                        dwell = dwellDefinition.testDwellTime((int) (System.currentTimeMillis() - visit.getArrivalTimeInMillis()) / 1000);
                    }
                    Log.i("Info: ", "Arriving Time: " + new Date(visit.getArrivalTimeInMillis()) + " Departure Time: " + new Date(visit.getDepartureTimeInMillis())
                            + " Dwell Time: " + visit.getDwellTimeInMillis() / 1000);
                    if (dwell > 0) {
                        TCSEvent event = new TCSEvent();
                        event.setEventType(TCSEventManager.VAL_BEACON_PROXIMITY_DWELL);
                        JSONObject values = new JSONObject();
                        String iso8601String =
                                String.valueOf(android.text.format.DateFormat
                                        .format("yyyy-MM-dd'T'hh:mm:ssz", visit.getArrivalTimeInMillis()));
                        try {
                            values.put(TCSEvent.KEYVALUE01, visit.getPlace().getName());
                            values.put(TCSEvent.KEYVALUE02, Integer.toString(dwell));
                            values.put("eventTime", iso8601String); // Send the visit start time
                            event.values = values;
                            TCSEventManager.getInstance().logEvent(event);
                        } catch (JSONException e) {
                            Log.d(TAG, e.getMessage());
                        }
                    }
                }
            }
        }
    };

    public void setBeaconEventDelegate(TCSBeaconEventDelegate beaconEventDelegate) {
        this.beaconEventDelegate = beaconEventDelegate;
    }

    private CommunicationListener communicationListener = new CommunicationListener() {
        @Override
        public Collection<Communication> presentNotificationForCommunications(Collection<Communication> communications, Visit visit) {
            for (Communication comm : communications) {
                Log.i("INFO", "Place Communication: " + visit.getPlace().getName() + " message: " + comm.getTitle());
            }

            return communications;
        }

        @Override
        public Collection<Communication> presentNotificationForCommunications(Collection<Communication> communications, Push push) {
            for (Communication communication : communications) {
                Log.i("INFO", "Received a Push Communication with message: " + communication.getTitle());
            }

            return communications;
        }

        @Override
        public void onNotificationClicked(List<Communication> list) {
            Log.i("INFO", "Notification was clicked on");
        }
    };

    private BeaconEventListener beaconSightingListener = new BeaconEventListener() {
        @Override
        public void onBeaconSighting(BeaconSighting beaconSighting) {
            super.onBeaconSighting(beaconSighting);

            //            Log.i("INFO", "****************** Sighting: " + beaconSighting.toString());
            if (beaconDefinitions.size() <= 0) {
                return;
            }

            TCSBeacon beacon = beaconDefinitions.get(beaconSighting.getBeacon().getName());
            TCSTransmitter transmitter = transmitters.get(beaconSighting.getBeacon().getName());


            if (transmitter == null) {
                transmitter = new TCSTransmitter();
                transmitter.setIdentifier(beaconSighting.getBeacon().getIdentifier());
                transmitter.setName(beaconSighting.getBeacon().getName());
//                transmitter.setOwnerId(beaconSighting.getBeacon(). visit.getTransmitter().getOwnerId());

                addTransmitter(transmitter);
            }

            if (beacon != null) {
                transmitter.setBeaconEntryBehaviour(beacon.getBeaconEntryBehaviour());
                transmitter.setIconUrl(beacon.getIconUrl());
                transmitter.setDisplayName(beacon.getDisplayName());
                transmitter.setDisplayDescription(beacon.getDisplayDescription());

            }

            transmitter.setPreviousRSSI(transmitter.getRssi());
            transmitter.setRssi(beaconSighting.getRSSI());
            transmitter.setBattery(beaconSighting.getBeacon().getBatteryLevel().ordinal());
            transmitter.setTemperature(beaconSighting.getBeacon().getTemperature());
            transmitter.setTimeInMillis(beaconSighting.getTimeInMillis());

            if (beaconEventDelegate != null) {
                beaconEventDelegate.onBeaconSighting(transmitter);
            }

            if (beacon == null) {
                return;
            }

            Date now = new Date();

            //We only want an event when we first see this or if a significant enough time period has gone past that we can assume that a
            //sighting for a beacon has not departed beacuse the phone went to the lockscreen.
            if ((now.getTime() - transmitter.getLastSighted().getTime()) > mBackgroundResightingInterval) {
                //the first sighting is guaranteed to fire this as the time is set to 1/1/1970
                //Following fires will occur if the elapsed time is greater than backGroundResightingInterval or if the beacon departs and then arrives again.

                //Also if we have an exclusive beacon count, check that this beacon is not a member
                //If it is then don't fire the entry.

                TCSEvent event = new TCSEvent();
                event.setEventType(TCSEventManager.VAL_BEACON_PROXIMITY_ENTRY);
                JSONObject values = new JSONObject();

//                String iso8601String =
//                        String.valueOf(android.text.format.DateFormat.format("yyyy-MM-dd'T'hh:mm:ssz", beaconSighting.getTimeInMillis()));

                String iso8601String = TCSTimeUtilities.GetUTCdatetimeAsString(new Date(beaconSighting.getTimeInMillis()));

                try {
                    values.put(TCSEvent.KEYVALUE01, transmitter.getName());
                    values.put(TCSEvent.KEYVALUE02, transmitter.getIdentifier());
                    values.put("eventTime", iso8601String); // Send the visit start time
                    event.values = values;
                    TCSEventManager.getInstance().logEvent(event);
                } catch (JSONException e) {
                    Log.d(TAG, e.getMessage());
                }
            }

            transmitter.setLastSighted(new Date(beaconSighting.getTimeInMillis()));

            if (transmitter.getBeaconEntryBehaviour().equals(BEACONENTRYBEHAVIOUR_BEACONPOP)) {
                //Show this beacon as a beacon pop - update its state when in foreground
                notifyBeaconPopStatusChanged(transmitter);
            }

            if (beaconPopCountDelegate != null) {
                beaconPopCountDelegate.beaconPopCountUpdated();
            }
        }

    };

    public static GimbalManager getInstance() {
        if (gimbalManager == null) {
            gimbalManager = new GimbalManager();
        }

        return gimbalManager;
    }

    public GimbalManager() {
        PlaceManager.getInstance().addListener(placeEventListener);
        CommunicationManager.getInstance().addListener(communicationListener);
        beaconManager = new BeaconManager();
        beaconManager.addListener(beaconSightingListener);
    }

    public void startMonitoring() {
        PlaceManager.getInstance().startMonitoring();
        beaconManager.startListening();
        CommunicationManager.getInstance().startReceivingCommunications();
        new InactiveTransmittersAsyncTask().execute();
    }

    public void stopMonitoring() {
        PlaceManager.getInstance().stopMonitoring();
        CommunicationManager.getInstance().stopReceivingCommunications();
        if (beaconManager != null) {
            beaconManager.stopListening();
        }
    }

    public void clearBeacons() {
        beaconDefinitions.clear();
        clearTransmitters();
    }

    public static void registerApp(Application application, String apiKey) {
        Gimbal.setApiKey(application, apiKey);
    }

    public void setBeaconPopDelegate(BeaconPopDelegate beaconPopDelegate) {
        this.beaconPopDelegate = beaconPopDelegate;
    }

    public void setBeaconPopCountDelegate(BeaconPopCountDelegate beaconPopCountDelegate) {
        this.beaconPopCountDelegate = beaconPopCountDelegate;
    }

    public int getBeaconCount() {
        return closestTransmitters.size();
    }

    public void clearTransmitters() {
        synchronized (transmitters) {
            transmitters.clear();
        }
        if (beaconPopDelegate != null) {
            beaconPopDelegate.clearBeaconPops();
        }
    }

    private void addTransmitter(TCSTransmitter transmitter) {
        synchronized (transmitters) {
            transmitters.put(transmitter.getName(), transmitter);
        }
    }

    public TCSBeacon getBeaconDefinitionByIdentifier(String identifier) {
        if (beaconDefinitions == null) {
            return null;
        }

        for (TCSBeacon beacon : beaconDefinitions.values()) {
            if (beacon.getDeviceId().equals(identifier)) {
                return beacon;
            }
        }

        return null;
    }

    private void newBeaconDefinitions(JSONObject definitions) {
        Log.d(TAG, "Received Beacons");

        try {
            String syncStatus = definitions.getString(TCSAPIConnector.JKEY_CACHESTATUS);

            //Check to see that if we are out of sync with the server definitions
            if (syncStatus.equals(TCSAPIConnector.OUT_OF_SYNC)) {
                JSONArray beacons = definitions.getJSONArray(JKEY_BEACONS);
                if (beaconDefinitions == null) {
                    beaconDefinitions = new HashMap<>();
                } else {
                    beaconDefinitions.clear();
                }

                //BOOL signalStrenthDefaultDone = NO;
                for (int i = 0; i < beacons.length(); i++) {
                    TCSBeacon tcsBeacon = new TCSBeacon(beacons.getJSONObject(i));
                    beaconDefinitions.put(tcsBeacon.getDeviceId(), tcsBeacon);
                }


                JSONArray dwells = definitions.getJSONArray(JKEY_BEACON_DWELLS);

                beaconCacheVersion = definitions.getString(TCSAPIConnector.JKEY_CACHEVERSION);

                if (dwells.length() == 0) {
                    return;
                }

                //Invalidate all the current definitions
                Iterator it = beaconDwellDefinitions.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    ((TCSBeaconDwellDefinition) pair.getValue()).invalidate();
                }

                if (beaconDwellDefinitions == null) {
                    beaconDwellDefinitions = new HashMap<String, TCSBeaconDwellDefinition>();
                } else {
                    beaconDwellDefinitions.clear();
                }

                //Add the new dwell time definitions
                for (int i = 0; i < dwells.length(); i++) {
                    String deviceId = dwells.getJSONObject(i).getString("beaconDeviceId");
                    int dwellTime = dwells.getJSONObject(i).getInt("dwellTime");

                    if (dwellTime < 0) {
                        continue;
                    }

                    TCSBeaconDwellDefinition definition = beaconDwellDefinitions.get(deviceId);

                    if (definition == null) {
                        definition = new TCSBeaconDwellDefinition(deviceId);
                        beaconDwellDefinitions.put(deviceId, definition);
                    } else {
                        definition.setInvalid(false);
                    }

                    definition.addDwellTime(dwellTime);

                }

                //We now want to remove invalid definitions and invalid dwellTimes

                Iterator it2 = beaconDwellDefinitions.entrySet().iterator();
                while (it2.hasNext()) {
                    Map.Entry pair = (Map.Entry) it2.next();
                    if (((TCSBeaconDwellDefinition) pair.getValue()).isInvalid()) {
                        beaconDwellDefinitions.remove(pair.getKey().toString());
                    } else {
                        ((TCSBeaconDwellDefinition) pair.getValue()).clean();
                    }
                }


            }
        } catch (JSONException e) {
        }
    }

    public void syncBeacons(final TCSRequestSuccessDelegate delegate) {
        TCSRequestArgs requestArgs = new TCSRequestArgs();
        requestArgs.requestType = TCSAPIConstants.TCSRequestType.GETBEACONS;

        JSONObject values = new JSONObject();
        try {
            values.put(TCSAPIConnector.JKEY_CACHEVERSION, beaconCacheVersion);
        } catch (JSONException e) {
        }

        requestArgs.payload = values;
        requestArgs.additionalPath = "?cacheVersion=" + beaconCacheVersion;
        requestArgs.callback = new TCSURLConnectionDelegate() {
            @Override
            public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
                boolean result = false;
                String resultDesc = "ERROR";

                if (e != null) {
                    result = false;
                    resultDesc = TCSAppInstance.ERROR_CONNECTION_FAILED;
                } else {
                    if (responseCode == 200) {
                        try {
                            JSONObject jobj = new JSONObject(reply);
                            newBeaconDefinitions(jobj);
                            result = true;
                            resultDesc = TCSAppInstance.SUCCESS;
                        } catch (JSONException e2) {
                            result = false;
                            resultDesc = TCSAppInstance.ERROR_UNKNOWN;
                        }

                    }
                }
                if (delegate != null) {
                    delegate.done(result, resultDesc);
                }
            }
        };

        try {
            TCSAPIConnector.getInstance().requestWithArgs(requestArgs);
        } catch (Exception e) {
            delegate.done(false, TCSAppInstance.ERROR_REQUEST_FAILED);
        }

        if (beaconEventDelegate != null) {
            beaconEventDelegate.syncAdditionalBeacons();
        }
    }

    private void notifyBeaconPopStatusChanged(TCSTransmitter transmitter) {

        synchronized (closestTransmitters) {
            //If the transmitter is already in the list then it is already updated
            //or requires removal then simple update to delegate
            if (closestTransmitters.contains(transmitter)) {
                if (transmitter.isDepart()) {
                    closestTransmitters.remove(transmitter);
                }
            }
            //Otherwise if we are below our limit for beacon pops, simply add and update
            else if (closestTransmitters.size() < mMaxBeaconPops) {
                closestTransmitters.add(transmitter);
            } else {
                //have a full complement of close transmitters need to see if it is closer
                //than any existing and replace if necessary

                TCSTransmitter farthest = null;
                TCSTransmitter temp = null;

                boolean newTransmitterIsCloser = false;

                for (int i = 0; i < closestTransmitters.size(); i++) {
                    temp = closestTransmitters.get(i);
                    //is this transmitter the farthest of the closest :)
                    if (farthest == null || temp.getRssi() < farthest.getRssi()) {
                        farthest = temp; //Temp is further away - lower (more negative) signal
                    }

                    newTransmitterIsCloser |= transmitter.getRssi() > temp.getRssi(); //Is the new transmitter closer.
                }

                if (newTransmitterIsCloser) {
                    closestTransmitters.set(closestTransmitters.indexOf(farthest), transmitter);
                }

                //Note transmitters are the closest but not in order.
            }

            if (TCSAppInstance.getInstance().isApplicationIsAwake() && beaconPopDelegate != null && closestTransmitters != null) {
                beaconPopDelegate.beaconPopsUpdate(closestTransmitters);
            }
        }
    }


    private void removeTransmitter(TCSTransmitter transmitter) {
        synchronized (transmitters) {
            transmitters.remove(transmitter.getName());
        }
    }

    public void didDepart(TCSTransmitter transmitter) {

        if (beaconEventDelegate != null) {
            beaconEventDelegate.onDidDepart(transmitter);
        }

        TCSBeaconDwellDefinition dwellDefinition = beaconDwellDefinitions.get(transmitter.getName());
        if (dwellDefinition != null) {
            dwellDefinition.reset();
        }

        if (beaconDefinitions == null) // If we don't know what to do with it then just return
            return;

        //TCSLogDebug(@"############## didDepart: %@", visit);
        //TCSLogDebug(@"############## didDepart: %@", visit.transmitter.name);

        TCSEvent event = new TCSEvent();
        event.setEventType(TCSEventManager.VAL_BEACON_PROXIMITY_EXIT);
        JSONObject values = new JSONObject();

        //[values setObject:[NSString stringWithFormat:@"%f", location.coordinate.latitude]  forKey:@"latitude"];
        //[values setObject:[NSString stringWithFormat:@"%f", location.coordinate.longitude]  forKey:@"longitude"];
        String iso8601String = String.valueOf(android.text.format.DateFormat.format("yyyy-MM-dd'T'hh:mm:ssz", transmitter.getLastSighted()));
        try {
            values.put(TCSEvent.KEYVALUE01, transmitter.getName());
            values.put(TCSEvent.KEYVALUE02, transmitter.getIdentifier());
            values.put("eventTime", iso8601String); // Report the last beacon report datetime
//            values.put(TCSEvent.KEYVALUE03, (int)(transmitter.getDwellTime()*1000)); // Report the dwell time with the fence exit.
            event.values = values;
            TCSEventManager.getInstance().logEvent(event);
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }

        if (transmitter != null) {

            transmitter.setDepart(true);

            if (transmitter.getBeaconEntryBehaviour().equals(BEACONENTRYBEHAVIOUR_BEACONPOP)) {
                notifyBeaconPopStatusChanged(transmitter);
            }

            removeTransmitter(transmitter);
        }

        if (getBeaconCount() == 0 && beaconPopCountDelegate != null) {
            beaconPopCountDelegate.beaconPopCountUpdated();
        }
    }

    public interface BeaconPopDelegate {
        void clearBeaconPops();

        void beaconPopsUpdate(ArrayList<TCSTransmitter> transmitters);
    }

    public interface BeaconPopCountDelegate {
        void beaconPopCountUpdated();
    }

    private class InactiveTransmittersAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            while (true) {
                synchronized (transmitters) {
                    Iterator<String> iterator = transmitters.keySet().iterator();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        TCSTransmitter transmitter = transmitters.get(key);
                        Date now = new Date();
                        boolean inactive = (now.getTime() - transmitter.getLastSighted().getTime()) > inactiveInterval;
                        if (inactive) {
                            didDepart(transmitter);
                        }
                    }
                }
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
