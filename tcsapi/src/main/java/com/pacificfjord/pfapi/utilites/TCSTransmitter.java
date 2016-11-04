package com.pacificfjord.pfapi.utilites;

import com.pacificfjord.pfapi.TCSFYXManager;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TCSTransmitter {

    private String name = "";
    private String identifier = "";
    private int rssi = -100;
    private int previousRSSI = -100;
    private Date lastSighted = new GregorianCalendar(1970,1,1).getTime();
    private Integer battery = 0;
    private Integer temperature = 0;
    private String beaconEntryBehaviour = TCSFYXManager.BEACONENTRYBEHAVIOUR_REQUESTACTION;
    private String ownerId = "";
    private String iconUrl = "";
    private String displayName = "";
    private String displayDescription = "";
    private long timeInMillis;

    private boolean departed;

    public TCSTransmitter(){
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public Integer getBattery() {
        return battery;
    }

    public void setBattery(Integer battery) {
        this.battery = battery;
    }

    public Integer getTemperature() {
        return temperature;
    }

    public void setTemperature(Integer temperature) {
        this.temperature = temperature;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public boolean isDepart() {
        return departed;
    }

    public void setDepart(boolean depart) {
        this.departed = depart;
    }

    public int getPreviousRSSI() {
        return previousRSSI;
    }

    public void setPreviousRSSI(int previousRSSI) {
        this.previousRSSI = previousRSSI;
    }

    public Date getLastSighted() {
        return lastSighted;
    }

    public void setLastSighted(Date lastSighted) {
        this.lastSighted = lastSighted;
    }

    public String getBeaconEntryBehaviour() {
        return beaconEntryBehaviour;
    }

    public void setBeaconEntryBehaviour(String beaconEntryBehaviour) {
        this.beaconEntryBehaviour = beaconEntryBehaviour;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayDescription() {
        return displayDescription;
    }

    public void setDisplayDescription(String displayDescription) {
        this.displayDescription = displayDescription;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }
}
