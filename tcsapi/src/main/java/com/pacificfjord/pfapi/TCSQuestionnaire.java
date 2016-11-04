package com.pacificfjord.pfapi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mind-p6 on 1/6/15.
 */
public class TCSQuestionnaire {
    private int thisId;
    private int appId;
    private String status;
    private Date creationTime;
    private Date updateTime;
    private String name;
    private List<TCSQuestion> questions;

    public TCSQuestionnaire(JSONObject questionnaire) throws JSONException {
        thisId = questionnaire.getInt("id");
        appId = questionnaire.getInt("appId");
        status = questionnaire.getString("status");
        name = questionnaire.getString("name");
    }

    public void createQuestions(JSONArray questions, JSONArray choices) throws JSONException {
        if (this.questions == null) {
            this.questions = new ArrayList<TCSQuestion>();
        } else {
            this.questions.clear();
        }

        for (int i = 0; i < questions.length(); i++) {
            TCSQuestion question = new TCSQuestion(questions.getJSONObject(i));
            if (question.getQuestionnaireId() == this.thisId) {
                question.createChoices(choices);
                this.questions.add(question);
            }
        }
    }

    public int getThisId() {
        return thisId;
    }

    public int getAppId() {
        return appId;
    }

    public String getStatus() {
        return status;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public String getName() {
        return name;
    }

    public List<TCSQuestion> getQuestions() {
        return questions;
    }
}
