package com.sparkcompass.tobaccodock.questionnaires;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import com.pacificfjord.pfapi.TCSAppInstance;
import com.pacificfjord.pfapi.TCSQuestion;
import com.pacificfjord.pfapi.TCSQuestionnaire;
import com.pacificfjord.pfapi.TCSSuccessDelegate;
import com.sparkcompass.tobaccodock.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class QuestionnaireActivity extends AppCompatActivity implements SendAnswerListener {

    public static final String QUESTIONNAIRE_ID = "questionnaire_id";
    private int questionnaireId;
    private int currentQuestion = 0;
    private int totalQuestions;
    private TCSQuestionnaire questionnaire;
    private JSONArray answers;
    private TCSQuestion question;
    @Bind(R.id.progress_container)
    public RelativeLayout progressContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_questionnaire);
        ButterKnife.bind(this);
        if (savedInstanceState != null) {
            questionnaireId = savedInstanceState.getInt(QUESTIONNAIRE_ID);
        } else {
            questionnaireId = getIntent().getExtras().getInt(QUESTIONNAIRE_ID);
        }

        questionnaire = TCSAppInstance.getInstance().questionnaireWithId(questionnaireId);

        showNextQuestion();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(QUESTIONNAIRE_ID, questionnaireId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void sendAnswer(Object answer) {
        if (answers == null) {
            answers = new JSONArray();
        }

        JSONObject jsonAnswer = new JSONObject();
        try {
            jsonAnswer.put("answerValue", answer);
            jsonAnswer.put("questionId", question.getThisId());
            answers.put(answer);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (currentQuestion >= totalQuestions) {
            progressContainer.setVisibility(View.VISIBLE);
            TCSAppInstance.getInstance().sendAnswersForQuestionnaireId(questionnaireId, answers, new TCSSuccessDelegate() {
                @Override
                public void done(boolean success) {
                    progressContainer.setVisibility(View.GONE);
                    if(success){
                        Log.d("XXXXXX questionnaire", "SUCCESS");
                    } else {
                        Log.d("XXXXXX questionnaire", "FAILED");
                    }
                    showFinishDialog();
                }
            });

        } else {
            showNextQuestion();
        }
    }

    @Override
    public void finishQuestionnaire() {
        finish();
    }

    private void showFinishDialog(){
        QuestionAlert questionAlert = new QuestionAlert(this, null, this);
        questionAlert.getWindow().getAttributes().windowAnimations = R.style.TranslucentDialogAnimation;
        questionAlert.show();
    }

    private void showNextQuestion(){
        question = questionnaire.getQuestions().get(currentQuestion);
        question.setQuestionNumber(currentQuestion);
        totalQuestions = questionnaire.getQuestions().size();
        question.setTotalQuestions(totalQuestions);

        QuestionAlert questionAlert = new QuestionAlert(this, question, this);
        questionAlert.getWindow().getAttributes().windowAnimations = R.style.TranslucentDialogAnimation;
        questionAlert.show();

        currentQuestion++;
    }

}
