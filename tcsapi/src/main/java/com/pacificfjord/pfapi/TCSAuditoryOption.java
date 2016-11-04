package com.pacificfjord.pfapi;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Aaron Vega on 6/26/15.
 */
public class TCSAuditoryOption {

    private static final String ID = "id";
    private static final String QUESTION_ID = "questionId";
    private static final String OPTION_TEXT = "optionText";

    private int id;
    private long questionId;
    private String optionText;

    public TCSAuditoryOption(JSONObject json) throws JSONException {
        this.id = json.getInt(ID);
        this.questionId = json.getLong(QUESTION_ID);
        this.optionText = json.getString(OPTION_TEXT);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }
}
