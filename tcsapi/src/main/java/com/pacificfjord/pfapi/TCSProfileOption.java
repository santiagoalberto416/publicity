package com.pacificfjord.pfapi;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Aaron Vega on 6/1/15.
 */
public class TCSProfileOption {

    private static final String ID = "id";
    private static final String SEGMENTATION_CATEGORY = "segmentationCategory";
    private static final String VALUE = "value";
    private static final String RANGE_MIN = "rangeMin";
    private static final String RANGE_MAX = "rangeMax";

    private long id;
    private int segmentationCategory;
    private String value;
    private double rangeMin = Double.NaN;
    private double rangeMax = Double.NaN;

    public TCSProfileOption(String value) {
        this.id = -1;
        this.value = value;
        this.segmentationCategory = -1;
    }

    public TCSProfileOption(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getLong(ID);
        this.segmentationCategory = jsonObject.getInt(SEGMENTATION_CATEGORY);
        this.value = jsonObject.getString(VALUE);
        this.rangeMin = jsonObject.getDouble(RANGE_MIN);
        this.rangeMax = jsonObject.getDouble(RANGE_MAX);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getSegmentationCategory() {
        return segmentationCategory;
    }

    public void setSegmentationCategory(int segmentationCategory) {
        this.segmentationCategory = segmentationCategory;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public double getRangeMin() {
        return rangeMin;
    }

    public void setRangeMin(double rangeMin) {
        this.rangeMin = rangeMin;
    }

    public double getRangeMax() {
        return rangeMax;
    }

    public void setRangeMax(double rangeMax) {
        this.rangeMax = rangeMax;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        return id == ((TCSProfileOption) o).getId();
    }
}
