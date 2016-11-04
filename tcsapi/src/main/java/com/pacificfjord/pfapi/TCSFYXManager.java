package com.pacificfjord.pfapi;

import com.pacificfjord.pfapi.utilites.TCSTransmitter;

import java.util.ArrayList;

/**
 * Created by tom on 16/01/14.
 */
public class TCSFYXManager /*implements ProximityListener, VisitListener */{
//    private static TCSFYXManager instance = new TCSFYXManager();
//    public static TCSFYXManager getInstance() {
//        return instance;
//    }
//
//    private final LinkedHashMap<String, TCSTransmitter> transmitters = new LinkedHashMap<String, TCSTransmitter>();
//    private VisitManager visitManager;
//
//    private static final String TAG = "TCSFYXManager";
//    private static final String JKEY_BEACONS = "beacons";
//
    public static final String BEACONENTRYBEHAVIOUR_REQUESTACTION  = "REQUEST_ACTION";
    public static final String BEACONENTRYBEHAVIOUR_BEACONPOP      = "DISPLAY";
//
//    protected int mMaxBeaconPops = 5;
//    protected float mBackgroundResightingInterval = 6 * 60 * 60 * 1000; //6 hours in milliseconds
//    protected ProximityOptions mOptions = new ProximityOptions();
//    protected boolean enabled = false;
//    protected boolean isRunning = false;
//    protected String beaconCacheVersion = "";
//    protected Map<String,TCSBeacon> beaconDefinitions = new HashMap<String, TCSBeacon>();
//    protected ArrayList<TCSTransmitter> closestTransmitters = new ArrayList<TCSTransmitter>();
//
//    protected VisitListener forwardingDelegate = null;
//    protected BeaconPopDelegate beaconPopDelegate = null;
//
//    private TCSFYXManager() {
//        GimbalLogConfig.setLogLevel(GimbalLogLevel.INFO);
//        mOptions.setOption(ProximityOptions.VisitOptionArrivalRSSIKey, -75);
//        mOptions.setOption(ProximityOptions.VisitOptionArrivalRSSIKey, -90);
//        mOptions.setOption(ProximityOptions.BluetoothScanModeKey, ProximityOptions.BluetoothScanForegroundAndBackground);
//        mOptions.setOption(ProximityOptions.VisitOptionSignalStrengthWindowKey, ProximityOptions.VisitOptionSignalStrengthWindowMedium);
//        mOptions.setOption(ProximityOptions.VisitOptionBackgroundDepartureIntervalInSecondsKey, 10);
//        mOptions.setOption(ProximityOptions.VisitOptionForegroundDepartureIntervalInSecondsKey, 5);
//    }
//
//    public LinkedHashMap<String, TCSTransmitter> getTransmitters() {
//        return transmitters;
//    }
//
//    public boolean isRunning() {
//        return isRunning;
//    }
//
//    public boolean isEnabled() {
//        return enabled;
//    }
//
//    public void setForwardingDelegate(VisitListener forwardingDelegate) {
//        this.forwardingDelegate = forwardingDelegate;
//    }
//
//    public void setBeaconPopDelegate(BeaconPopDelegate beaconPopDelegate) {
//        this.beaconPopDelegate = beaconPopDelegate;
//    }
//
//    public void setEntryRSSI(int entryRSSI) {
//        mOptions.setOption(ProximityOptions.VisitOptionArrivalRSSIKey, entryRSSI);
//    }
//
//    public void setExitRSSI(int exitRSSI) {
//        mOptions.setOption(ProximityOptions.VisitOptionArrivalRSSIKey, exitRSSI);
//    }
//
//    public void setMaxBeaconPops(int maxBeaconPops) {
//        this.mMaxBeaconPops = maxBeaconPops;
//    }
//
//    public void setWindow(int window)
//    {
//        switch (window)
//        {
//            case ProximityOptions.VisitOptionSignalStrengthWindowNone:break;
//            case ProximityOptions.VisitOptionSignalStrengthWindowSmall:break;
//            case ProximityOptions.VisitOptionSignalStrengthWindowMedium:break;
//            case ProximityOptions.VisitOptionSignalStrengthWindowLarge:break;
//            default:
//                Log.i(TAG, "not a valid window");
//                return;
//        }
//        mOptions.setOption(ProximityOptions.VisitOptionSignalStrengthWindowKey, window);
//    }
//
//    public void setAppId(String appId, String secret, String callbackUrl)
//    {
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
//            Proximity.initialize(TCSAppInstance.getInstance().getApplicationContext(), appId, secret);
//    }
//
//    public void syncBeacons(final TCSRequestSuccessDelegate delegate)
//    {
//        TCSRequestArgs requestArgs = new TCSRequestArgs();
//        requestArgs.requestType = TCSAPIConstants.TCSRequestType.GETBEACONS;
//
//        JSONObject values = new JSONObject();
//        try{
//            values.put(TCSAPIConnector.JKEY_CACHEVERSION, beaconCacheVersion);
//        }catch (JSONException e){}
//
//        requestArgs.payload = values;
//
//        requestArgs.callback = new TCSURLConnectionDelegate() {
//            @Override
//            public void done(TCSURLConnection connection, int responseCode, String reply, Exception e) {
//                boolean result = false;
//                String resultDesc = "ERROR";
//
//                if(e!=null)
//                {
//                    result = false;
//                    resultDesc = TCSAppInstance.ERROR_CONNECTION_FAILED;
//                }
//                else
//                {
//                    if(responseCode==200)
//                    {
//                        try{
//                            JSONObject jobj = new JSONObject(reply);
//                            newBeaconDefinitions(jobj);
//                            result = true;
//                            resultDesc = TCSAppInstance.SUCCESS;
//                        }catch (JSONException e2)
//                        {
//                            result = false;
//                            resultDesc = TCSAppInstance.ERROR_UNKNOWN;
//                        }
//
//                    }
//                }
//                if(delegate!=null)
//                    delegate.done(result, resultDesc);
//            }
//        };
//
//        try {
//            TCSAPIConnector.getInstance().requestWithArgs(requestArgs);
//        } catch (Exception e) {
//            delegate.done(false, TCSAppInstance.ERROR_REQUEST_FAILED);
//        }
//    }
//
//    public void startFYX(){
//        if(enabled && !isRunning)
//             Proximity.startService(this);
//    }
//
//    public void stopFYX(){
//        stopScanning();
//        Proximity.stopService();
//        isRunning = false;
//    }
//
//    public void clearBeacons(){
//        beaconDefinitions.clear();
//        clearTransmitters();
//    }
//
//    public void clearTransmitters(){
//        synchronized (transmitters) {
//            transmitters.clear();
//        }
//        if(beaconPopDelegate!=null)
//            beaconPopDelegate.clearBeaconPops();
//    }
//
//    public int getBeaconCount()
//    {
//        return transmitters.size();
//    }
//
//    public void setEnabled(boolean enabled) {
//        this.enabled = enabled;
//
//        if(!enabled)
//            stopFYX();
//    }
//
//    private void newBeaconDefinitions(JSONObject definitions)
//    {
//        Log.d(TAG, "Received Beacons");
//
//        try {
//            String syncStatus = definitions.getString(TCSAPIConnector.JKEY_CACHESTATUS);
//
//            //Check to see that if we are out of sync with the server definitions
//            if (syncStatus.equals(TCSAPIConnector.OUT_OF_SYNC)){
//                JSONArray beacons = definitions.getJSONArray(JKEY_BEACONS);
//                if (beaconDefinitions == null)
//                    beaconDefinitions = new HashMap<String, TCSBeacon>();
//                else
//                    beaconDefinitions.clear();
//
//                //BOOL signalStrenthDefaultDone = NO;
//                for (int i=0; i< beacons.length(); i++){
//                    TCSBeacon tcsBeacon = new TCSBeacon(beacons.getJSONObject(i));
//                    beaconDefinitions.put(tcsBeacon.getDeviceId(), tcsBeacon);
//                }
//                beaconCacheVersion = definitions.getString(TCSAPIConnector.JKEY_CACHEVERSION);
//            }
//        }catch(JSONException e){}
//    }
//
//    public TCSBeacon getBeaconWithDeviceId(String deviceId){
//        return beaconDefinitions.get(deviceId);
//    }
//
//    private void stopScanning() {
//        if(visitManager!=null)
//            visitManager.stop();
//    }
//
//    private void startScanning() {
//        startScanningWithOptions();
//    }
//
//    private void startScanningWithOptions() {
//        visitManager.setVisitListener(this);
//        visitManager.startWithOptions(mOptions);
//    }
//
//    private void addTransmitter(TCSTransmitter transmitter)
//    {
//        synchronized (transmitters){
//            transmitters.put(transmitter.getName(), transmitter);
//        }
//    }
//
//    private void removeTransmitter(TCSTransmitter transmitter)
//    {
//        synchronized (transmitters){
//            transmitters.remove(transmitter.getName());
//        }
//    }
//
//    private void notifyBeaconPopStatusChanged(TCSTransmitter transmitter){
//
//        synchronized (closestTransmitters) {
//            //If the transmitter is already in the list then it is already updated
//            //or requires removal then simple update to delegate
//            if (closestTransmitters.contains(transmitter)) {
//                if (transmitter.isDepart())
//                    closestTransmitters.remove(transmitter);
//            }
//            //Otherwise if we are below our limit for beacon pops, simply add and update
//            else if (closestTransmitters.size() < mMaxBeaconPops) {
//                closestTransmitters.add(transmitter);
//            } else {
//                //have a full complement of close transmitters need to see if it is closer
//                //than any existing and replace if necessary
//
//                TCSTransmitter farthest = null;
//                TCSTransmitter temp = null;
//
//                boolean newTransmitterIsCloser = false;
//
//                for (int i = 0; i < closestTransmitters.size(); i++) {
//                    temp = closestTransmitters.get(i);
//                    //is this transmitter the farthest of the closest :)
//                    if (farthest == null || temp.getRssi() < farthest.getRssi())
//                        farthest = temp; //Temp is further away - lower (more negative) signal
//
//                    newTransmitterIsCloser |= transmitter.getRssi() > temp.getRssi(); //Is the new transmitter closer.
//                }
//
//                if (newTransmitterIsCloser)
//                    closestTransmitters.set(closestTransmitters.indexOf(farthest), transmitter);
//
//                //Note transmitters are the closest but not in order.
//            }
//        }
//
//        if(TCSAppInstance.getInstance().isApplicationIsAwake() && beaconPopDelegate!=null && closestTransmitters!=null)
//            beaconPopDelegate.beaconPopsUpdate(closestTransmitters);
//    }
//
//    @Override
//    public void serviceStarted() {
//        Log.d(TAG, "serviceStarted");
//
//        //if(visitManager==null)
//            visitManager = ProximityFactory.getInstance().createVisitManager();
//
//        startScanning();
//
//        isRunning = true;
//    }
//
//    @Override
//    public void startServiceFailed(int i, String s) {
//        Log.d(TAG, "Service start failed " + s);
//    }
//
//    @Override
//    public void didArrive(Visit visit) {
//        if(forwardingDelegate!=null)
//            forwardingDelegate.didArrive(visit);
//    }
//
//    @Override
//    public void receivedSighting(Visit visit, Date date, Integer rssi) {
//        if(forwardingDelegate!=null)
//            forwardingDelegate.receivedSighting(visit, date, rssi);
//
//        if(beaconDefinitions == null)
//            return;
//
//        //TCSLogDebug(@"############## receivedSighting: %@", visit);
//        //TCSLogDebug(@"############## didReceiveSighting: %@", visit.transmitter.name);
//        TCSBeacon beacon = beaconDefinitions.get(visit.getTransmitter().getName());
//        TCSTransmitter transmitter = transmitters.get(visit.getTransmitter().getName());
//
//        if (transmitter==null) {
//            transmitter = new TCSTransmitter();
//            transmitter.setIdentifier(visit.getTransmitter().getIdentifier());
//            transmitter.setName(visit.getTransmitter().getName());
//            transmitter.setOwnerId(visit.getTransmitter().getOwnerId());
//
//            addTransmitter(transmitter);
//        };
//
//        if(beacon!=null)
//        {
//            transmitter.setBeaconEntryBehaviour(beacon.getBeaconEntryBehaviour());
//            transmitter.setIconUrl(beacon.getIconUrl());
//            transmitter.setDisplayName(beacon.getDisplayName());
//            transmitter.setDisplayDescription(beacon.getDisplayDescription());
//        }
//
//        transmitter.setPreviousRSSI(transmitter.getRssi());
//        transmitter.setRssi(rssi);
//        transmitter.setBattery(visit.getTransmitter().getBattery());
//        transmitter.setTemperature(visit.getTransmitter().getTemperature());
//
//        Date now = new Date();
//
//        //We only want an event when we first see this or if a significant enough time period has gone past that we can assume that a
//        //sighting for a beacon has not departed beacuse the phone went to the lockscreen.
//        if((now.getTime() - transmitter.getLastSighted().getTime()) > mBackgroundResightingInterval)
//        {
//            //the first sighting is guaranteed to fire this as the time is set to 1/1/1970
//            //Following fires will occur if the elapsed time is greater than backGroundResightingInterval or if the beacon departs and then arrives again.
//
//            //Also if we have an exclusive beacon count, check that this beacon is not a member
//            //If it is then don't fire the entry.
//
//            TCSEvent event = new TCSEvent();
//            event.setEventType(TCSEvent.TCSEventType.BEACON_PROXIMITY_ENTRY);
//            JSONObject values = new JSONObject();
//
//            String iso8601String = String.valueOf(android.text.format.DateFormat.format("yyyy-MM-dd'T'hh:mm:ssz", visit.getStartTime()));
//
//            try{
//                values.put(TCSEvent.KEYVALUE01, transmitter.getName());
//                values.put(TCSEvent.KEYVALUE02, transmitter.getIdentifier());
//                values.put("eventTime", iso8601String); // Send the visit start time
//                event.values = values;
//                TCSEventManager.getInstance().logEvent(event);
//            }catch (JSONException e){
//                Log.d(TAG, e.getMessage());
//            }
//        }
//
//        transmitter.setLastSighted(date);
//
//        if(transmitter.getBeaconEntryBehaviour().equals(BEACONENTRYBEHAVIOUR_BEACONPOP))
//        {
//            //Show this beacon as a beacon pop - update its state when in foreground
//            notifyBeaconPopStatusChanged(transmitter);
//        }
//    }
//
//    @Override
//    public void didDepart(Visit visit) {
//        if(forwardingDelegate!=null)
//            forwardingDelegate.didDepart(visit);
//
//        if(beaconDefinitions==null) // If we don't know what to do with it then just return
//            return;
//
//        //TCSLogDebug(@"############## didDepart: %@", visit);
//        //TCSLogDebug(@"############## didDepart: %@", visit.transmitter.name);
//        TCSTransmitter transmitter = transmitters.get(visit.getTransmitter().getName());
//
//        TCSEvent event = new TCSEvent();
//        event.setEventType(TCSEvent.TCSEventType.BEACON_PROXIMITY_EXIT);
//        JSONObject values = new JSONObject();
//
//        //[values setObject:[NSString stringWithFormat:@"%f", location.coordinate.latitude]  forKey:@"latitude"];
//        //[values setObject:[NSString stringWithFormat:@"%f", location.coordinate.longitude]  forKey:@"longitude"];
//        String iso8601String = String.valueOf(android.text.format.DateFormat.format("yyyy-MM-dd'T'hh:mm:ssz", visit.getLastUpdateTime()));
//        try{
//            values.put(TCSEvent.KEYVALUE01, visit.getTransmitter().getName());
//            values.put(TCSEvent.KEYVALUE02, visit.getTransmitter().getIdentifier());
//            values.put("eventTime", iso8601String ); // Report the last beacon report datetime
//            values.put(TCSEvent.KEYVALUE03, (int)(visit.getDwellTime()*1000)); // Report the dwell time with the fence exit.
//            event.values = values;
//            TCSEventManager.getInstance().logEvent(event);
//        }catch(JSONException e){
//            Log.d(TAG, e.getMessage());
//        }
//
//        if(transmitter!=null) {
//
//            transmitter.setDepart(true);
//
//            if(transmitter.getBeaconEntryBehaviour().equals(BEACONENTRYBEHAVIOUR_BEACONPOP))
//            {
//                notifyBeaconPopStatusChanged(transmitter);
//            }
//
//            removeTransmitter(transmitter);
//        }
//    }
//
//    public interface BeaconPopDelegate{
//        public void clearBeaconPops();
//        public void beaconPopsUpdate(ArrayList<TCSTransmitter> transmitters);
//    }
}
