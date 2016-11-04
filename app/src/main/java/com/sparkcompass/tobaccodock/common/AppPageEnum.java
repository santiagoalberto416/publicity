package com.sparkcompass.tobaccodock.common;

/**
 * Created by mind-p6 on 8/29/15.
 */
public enum AppPageEnum {

    HOME("HOME"),
    PROFILE("PROFILE"),
    SETTINGS("SETTINGS"),
    CONTACT("CONTACT"),
    IMAGE_RECOGNITION("IMAGE_RECOGNITION"),
    MAP("MAP"),
    SHARE("SHARE"),
    EVENTS("EVENTS"),
    SWITCH_APP("SWITCH_APP"),
    WEB_VIEW("WEBVIEW"),
    NOTIFICATIONS("NOTIFICATIONS");

    private final String name;

    AppPageEnum(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return (otherName != null) && name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}
