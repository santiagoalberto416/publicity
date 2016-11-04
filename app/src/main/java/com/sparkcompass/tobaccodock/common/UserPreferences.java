package com.sparkcompass.tobaccodock.common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by mind-p6 on 8/28/15.
 */
public class UserPreferences {

    public static final String USER_PREFERENCES = "user_preferences";
    public static final String TERMS_ACCEPTED = "terms_accepted";
    public static final String BLUETOOTH_DIALOG = "bluetooth_dialog";
    public static final String STEPS_GOAL = "steps_goal";

    public static SharedPreferences getSettings(Context context) {
        return context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
    }

    public static SharedPreferences.Editor getSettingsEditor(Context context) {
        return getSettings(context).edit();
    }

    public static String getSafeSetting(Context ctx, String settingName,
                                        String defaultValue) {
        return getSettings(ctx).getString(settingName, defaultValue);
    }

    public static void setTermsAccepted(Context context, boolean termsAccepted) {
        getSettingsEditor(context).putBoolean(TERMS_ACCEPTED, termsAccepted).commit();
    }

    public static boolean getTermsAccepted(Context context) {
        return getSettings(context).getBoolean(TERMS_ACCEPTED, false);
    }

    public static void setTurnBluetoothMessage(Context context, boolean option) {
        getSettingsEditor(context).putBoolean(BLUETOOTH_DIALOG, option).commit();
    }

    public static boolean getTurnBluetoothMessage(Context context) {
        return getSettings(context).getBoolean(BLUETOOTH_DIALOG, false);
    }

}
