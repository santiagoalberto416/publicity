package com.pacificfjord.pfapi.beacons;

import java.util.Comparator;

/**
 * Created by mind-p6 on 9/30/15.
 */
public class TCSBeaconDwellTime {
    private int dwellTime;
    private boolean triggered;
    private boolean invalid;

    public TCSBeaconDwellTime() {
        dwellTime = 0;
        triggered = false;
        invalid = false;
    }

    public int getDwellTime() {
        return dwellTime;
    }

    public void setDwellTime(int dwellTime) {
        this.dwellTime = dwellTime;
    }

    public boolean isTriggered() {
        return triggered;
    }

    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }

    public boolean isInvalid() {
        return invalid;
    }

    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }

    public static Comparator<TCSBeaconDwellTime> DwellTimeComparator = new Comparator<TCSBeaconDwellTime>() {

        @Override
        public int compare(TCSBeaconDwellTime lhs, TCSBeaconDwellTime rhs) {
            if (lhs.getDwellTime() > rhs.getDwellTime()) {
                return 1;
            } else if (lhs.getDwellTime() < rhs.getDwellTime()) {
                return -1;
            } else {
                return 0;
            }
        }
    };

}
