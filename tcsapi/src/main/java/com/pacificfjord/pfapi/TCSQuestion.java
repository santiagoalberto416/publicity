package com.pacificfjord.pfapi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mind-p6 on 1/6/15.
 */
public class TCSQuestion {
    private int thisId;
    private int questionnaireId;
    private String questionText;
    private String description;
    private String answerTip;
    private TCSQuestionType type;
    private int minValue;
    private int maxValue;
    private List<TCSQuestionChoice> choices;
    private int questionNumber;
    private int totalQuestions;

    public enum TCSQuestionType {
        UNSPECIFIED,
        TYPE_BOOLEAN,
        TYPE_RANGE,
        TYPE_MULTICHOISE,
        TYPE_NUMERIC
    }


    public TCSQuestion(JSONObject question) throws JSONException {
        thisId = question.getInt("id");
        questionnaireId = question.getInt("questionnaireId");
        questionText = question.getString("questionText");
        description = question.getString("description");
        answerTip = question.getString("answerTip");
        type = TCSQuestionType.values()[question.getInt("type")];
        minValue = question.getInt("mnValue");
        maxValue = question.getInt("mxValue");
    }

    public void createChoices(JSONArray choices) throws JSONException {
        if (this.choices == null) {
            this.choices = new ArrayList<TCSQuestionChoice>();
        } else {
            this.choices.clear();
        }

        for (int i = 0; i < choices.length(); i++) {
            TCSQuestionChoice questionChoice = new TCSQuestionChoice(choices.getJSONObject(i));
            if(questionChoice.getQuestionId() == thisId)
                this.choices.add(questionChoice);
        }
    }

    public int getThisId() {
        return thisId;
    }

    public int getQuestionnaireId() {
        return questionnaireId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getDescription() {
        return description;
    }

    public String getAnswerTip() {
        return answerTip;
    }

    public TCSQuestionType getType() {
        return type;
    }

    public int getMinValue() {
        return minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public List<TCSQuestionChoice> getChoices() {
        return choices;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }
}
