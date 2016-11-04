package com.pacificfjord.pfapi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mind-p6 on 1/6/15.
 */
public class TCSQuestionChoice {
    private int thisId;
    private int questionId;
    private String optionText;

    public TCSQuestionChoice(JSONObject choice) throws JSONException{
        thisId = choice.getInt("id");
        questionId = choice.getInt("questionId");
        optionText = choice.getString("optionText");
    }

    public int getThisId() {
        return thisId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public String getOptionText() {
        return optionText;
    }
}
