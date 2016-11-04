package com.pacificfjord.pfapi.beacons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.pacificfjord.pfapi.beacons.TCSBeaconDwellTime.DwellTimeComparator;

/**
 * Created by mind-p6 on 9/30/15.
 */
public class TCSBeaconDwellDefinition {
    private String deviceId;
    private List<TCSBeaconDwellTime> dwellTimes;
    private boolean invalid;

    public TCSBeaconDwellDefinition(String deviceId) {
        this.deviceId = deviceId;
        dwellTimes = new ArrayList<TCSBeaconDwellTime>();
        invalid = false;
    }

    public int testDwellTime(int seconds) {
        for (TCSBeaconDwellTime dwell : this.dwellTimes) {
            if (seconds >= dwell.getDwellTime() && !dwell.isTriggered()) {
                dwell.setTriggered(true);
                return dwell.getDwellTime();
            }
        }
        return 0;
    }

    public void addDwellTime(int seconds) {
        for (TCSBeaconDwellTime dwell : this.dwellTimes) {
            if (dwell.getDwellTime() == seconds) {
                dwell.setInvalid(false);
                return;
            }
        }

        TCSBeaconDwellTime dwell = new TCSBeaconDwellTime();
        dwell.setDwellTime(seconds);
        dwellTimes.add(dwell);

        Collections.sort(dwellTimes, DwellTimeComparator);
    }

    public void invalidate() {
        for (TCSBeaconDwellTime dwell : this.dwellTimes) {
            dwell.setInvalid(true);
        }
    }

    public void reset() {
        for (TCSBeaconDwellTime dwell : this.dwellTimes) {
            dwell.setTriggered(false);
        }
    }

    public void clean() {
        List<TCSBeaconDwellTime> removes = new ArrayList<TCSBeaconDwellTime>();

        //Find the invalid dwell times
        for (TCSBeaconDwellTime dwell : this.dwellTimes) {
            if (dwell.isInvalid()) {
                removes.add(dwell);
            }
        }

        //Remove the invalid dwell times
        for (TCSBeaconDwellTime dwell : removes) {
            this.dwellTimes.remove(dwell);
        }
    }

    public boolean isInvalid() {
        return invalid;
    }

    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }
}
