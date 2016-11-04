package com.pacificfjord.pfapi.utilites;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by danielalcantara on 5/16/16.
 */
public class TCSTimeUtilities {

    static final String DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public static Date GetUTCdatetimeAsDate(Date date)
    {
        //note: doesn't check for null
        return StringDateToDate(GetUTCdatetimeAsString(date));
    }

    public static String GetUTCdatetimeAsString(Date date)
    {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(date);

        return utcTime;
    }

    public static Date StringDateToDate(String StrDate)
    {
        Date dateToReturn = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);

        try
        {
            dateToReturn = (Date)dateFormat.parse(StrDate);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return dateToReturn;
    }
}
