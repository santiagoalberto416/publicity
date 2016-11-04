package com.sparkcompass.tobaccodock.utils;

import android.text.TextUtils;

import java.util.regex.Pattern;

/**
 * Created by mind-p6 on 8/31/15.
 */
public class StringUtils {
    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    public static Pattern Password_Pattern = Pattern.compile(
            "((?=.*\\d)(?=.*[a-zA-Z]).{6,20})");
}
