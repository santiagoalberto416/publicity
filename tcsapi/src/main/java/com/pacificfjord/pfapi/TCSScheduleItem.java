package com.pacificfjord.pfapi;

import android.os.Parcel;
import android.os.Parcelable;

import net.danlew.android.joda.DateUtils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Aaron Vega on 3/23/15.
 */
public class TCSScheduleItem implements Parcelable{

    private static final String UID = "uid";
    private static final String SCHEDULEID = "scheduleUid";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String STARTDATE = "startdate";
    private static final String ENDDATE = "enddate";
    private static final String TIMEZONE = "timezone";
    private static final String SCHEDULEITEMCATEGORYID = "scheduleitemcategoryid";
    private static final String URL = "url";
    private static final String FACILITYID = "facilityId";
    private static final String SCHEDULEITEMPARENTUID = "scheduleItemParentUid";
    private static final String PARAMS = "params";
    private static final String PRINCIPALS = "principals";
    private static final String FACILITY = "facility";
    private static final String PRACTICE = "practice";
    private static final String SPEAKER = "speaker";
    private static final String FACILITY_STRING = "facilityString";

    private String uid;
    private String scheduleUid;
    private String name;
    private String description;
    private DateTime startDate;
    private DateTime endDate;
    private int timeZone;
    private long scheduleItemCategoryId;
    private String url;
    private long facilityId;
    private String scheduleItemParentUid;
    private TCSFacility facility;
    private Map<String, String> params;
    private Map<String, String> principals;
    private String speaker = "";
    private String facilityString = "";

    public TCSScheduleItem() {
    }

    public TCSScheduleItem(JSONObject jsonObject) throws JSONException, ParseException {
        this.uid = jsonObject.getString(UID);
        this.scheduleUid = jsonObject.getString(SCHEDULEID);
        this.name = jsonObject.getString(NAME);
        this.description = jsonObject.getString(DESCRIPTION);
        DateTimeZone.setDefault(DateTimeZone.UTC);
        this.startDate = new DateTime(jsonObject.getString(STARTDATE));
        this.endDate = new DateTime(jsonObject.getString(ENDDATE));
        this.timeZone = jsonObject.getInt(TIMEZONE);
        this.scheduleItemCategoryId = jsonObject.getLong(SCHEDULEITEMCATEGORYID);
        this.url = jsonObject.getString(URL);
        this.facilityId = jsonObject.getInt(FACILITYID);
        this.scheduleItemParentUid = jsonObject.getString(SCHEDULEITEMPARENTUID);
        if (facilityId > 0 && !jsonObject.isNull(FACILITY)) {
            this.facility = new TCSFacility(jsonObject.getJSONObject(FACILITY));
        }

        if (jsonObject.has(SPEAKER)) {
            this.speaker = jsonObject.getString(SPEAKER);
        }

        if (jsonObject.has(FACILITY_STRING)) {
            this.facilityString = jsonObject.getString(FACILITY_STRING);
        }

    }

    protected TCSScheduleItem(Parcel in) {
        uid = in.readString();
        scheduleUid = in.readString();
        name = in.readString();
        description = in.readString();
        timeZone = in.readInt();
        scheduleItemCategoryId = in.readLong();
        url = in.readString();
        facilityId = in.readLong();
        scheduleItemParentUid = in.readString();
        startDate = (DateTime) in.readValue(DateTime.class.getClassLoader());
        endDate = (DateTime) in.readValue(DateTime.class.getClassLoader());
        facility = in.readParcelable(TCSFacility.class.getClassLoader());

    }

    public static final Creator<TCSScheduleItem> CREATOR = new Creator<TCSScheduleItem>() {
        @Override
        public TCSScheduleItem createFromParcel(Parcel in) {
            return new TCSScheduleItem(in);
        }

        @Override
        public TCSScheduleItem[] newArray(int size) {
            return new TCSScheduleItem[size];
        }
    };

    public String getDuration() {
        int days = Days.daysBetween(this.startDate, this.endDate).getDays();
        if (days >= 1) {
            return String.valueOf(days) + (days == 1 ? " day" : " days");
        } else {
            int hours = Hours.hoursBetween(this.startDate, this.endDate).getHours();
            if (hours >= 1) {
                return String.valueOf(hours) + (hours == 1 ? " hr" : " hrs");
            } else {
                int minutes = Minutes.minutesBetween(this.startDate, this.endDate).getMinutes();
                return String.valueOf(minutes) + " minutes";
            }
        }
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getScheduleUid() {
        return scheduleUid;
    }

    public void setScheduleUid(String scheduleUid) {
        this.scheduleUid = scheduleUid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public String getStartDateString() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MMM d 'at' h:mm a");
        DateTimeFormatter todayFormatter = DateTimeFormat.forPattern("'Today at' h:mm a");
        String dateStr = formatter.print(getStartDate());
        if (DateUtils.isToday(getStartDate())) {
            dateStr = todayFormatter.print(getStartDate());
        }

        return dateStr;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    public int getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(int timeZone) {
        this.timeZone = timeZone;
    }

    public long getScheduleItemCategoryId() {
        return scheduleItemCategoryId;
    }

    public void setScheduleItemCategoryId(long scheduleItemCategoryId) {
        this.scheduleItemCategoryId = scheduleItemCategoryId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(long facilityId) {
        this.facilityId = facilityId;
    }

    public String getScheduleItemParentUid() {
        return scheduleItemParentUid;
    }

    public void setScheduleItemParentUid(String scheduleItemParentUid) {
        this.scheduleItemParentUid = scheduleItemParentUid;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public Map<String, String> getPrincipals() {
        return principals;
    }

    public void setPrincipals(Map<String, String> principals) {
        this.principals = principals;
    }

    public TCSFacility getFacility() {
        return facility;
    }

    public void setFacility(TCSFacility facility) {
        this.facility = facility;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(scheduleUid);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(timeZone);
        dest.writeLong(scheduleItemCategoryId);
        dest.writeString(url);
        dest.writeLong(facilityId);
        dest.writeString(scheduleItemParentUid);
        dest.writeValue(startDate);
        dest.writeValue(endDate);
        dest.writeParcelable(facility, flags);
    }

    public static class TCSScheduleItemStartDateComparator implements Comparator<TCSScheduleItem> {

        @Override
        public int compare(TCSScheduleItem scheduleItem, TCSScheduleItem scheduleItem2) {
            if(scheduleItem.startDate.isAfter(scheduleItem2.startDate)) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    public String getFacilityString() {
        return facilityString;
    }

    public void setFacilityString(String facilityString) {
        this.facilityString = facilityString;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }
}
