package com.pacificfjord.pfapi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aaron Vega on 6/1/15.
 */
public class TCSSegmentationCategory {

    private static final String ID = "id";
    private static final String CATEGORY = "category";
    private static final String OPTIONS = "options";

    private long id;
    private String category;
    private List<TCSProfileOption> options;
    private long selectedOptionId;

    public TCSSegmentationCategory(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getLong(ID);
        this.category = jsonObject.getString(CATEGORY);
        this.options = new ArrayList<>();
        JSONArray optionsJson = jsonObject.getJSONArray(OPTIONS);
        for (int i = 0; i < optionsJson.length(); i++) {
            this.options.add(new TCSProfileOption(optionsJson.getJSONObject(i)));
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<TCSProfileOption> getOptions() {
        return options;
    }

    public void setOptions(List<TCSProfileOption> options) {
        this.options = options;
    }


    public long getSelectedOptionId() {
        return selectedOptionId;
    }

    public void setSelectedOptionId(long selectedOptionId) {
        this.selectedOptionId = selectedOptionId;
    }
}
