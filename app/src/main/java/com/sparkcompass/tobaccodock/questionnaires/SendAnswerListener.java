package com.sparkcompass.tobaccodock.questionnaires;

/**
 * Created by danielalcantara on 7/18/16.
 */
public interface SendAnswerListener {
    void sendAnswer(Object answer);
    void finishQuestionnaire();
}
