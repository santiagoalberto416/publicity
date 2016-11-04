package com.sparkcompass.tobaccodock.questionnaires;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.pacificfjord.pfapi.TCSQuestion;
import com.pacificfjord.pfapi.TCSQuestionChoice;
import com.sparkcompass.tobaccodock.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by danielalcantara on 7/18/16.
 */
public class QuestionAlert extends Dialog {

    private TCSQuestion question;

    @Bind(R.id.title)
    public TextView title;
    @Bind(R.id.question_text)
    public TextView questionDescription;
    private SendAnswerListener listener;
    public int progressChanged;
    public String multiChoiseAnswer = "";

    public QuestionAlert(Context context, TCSQuestion question, SendAnswerListener listener) {
        super(context, R.style.TranslucentDialogAnimation);
        this.question = question;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (question != null) {
            switch (question.getType()) {
                case TYPE_BOOLEAN:
                    setBinaryQuestion();
                    break;
                case TYPE_RANGE:
                    setRangeQuestion();
                    break;
                case TYPE_MULTICHOISE:
                    setMultiChoiseQuestion();
                    break;
                case TYPE_NUMERIC:
                    setNumericQuestion();
                    break;
            }
            ButterKnife.bind(this);
            title.setText("Question " + (question.getQuestionNumber() + 1) + " of " + question.getTotalQuestions());
            questionDescription.setText(question.getQuestionText());
        } else {
            setFinishMessage();
            ButterKnife.bind(this);
            questionDescription.setText("Thank you for your feedback");
        }


    }

    private void setBinaryQuestion() {
        setContentView(R.layout.dialog_binary_question);
        Button buttonYes = (Button) findViewById(R.id.answer_yes);
        Button buttonNo = (Button) findViewById(R.id.answer_no);

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                sendAnswer("YES");
            }
        });

        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                sendAnswer("NO");
            }
        });
    }

    private void setRangeQuestion() {
        setContentView(R.layout.dialog_range_question);
        Button continueButton = (Button) findViewById(R.id.continue_button);
        if (question.getQuestionNumber() + 1 == question.getTotalQuestions()) {
            continueButton.setText("Finish");
        }
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                sendAnswer(progressChanged);
            }
        });

        final TextView answer = (TextView) findViewById(R.id.answer);
        answer.setText("Answer: " + question.getMinValue());
        SeekBar seekBar = (SeekBar) findViewById(R.id.range_answer);
        progressChanged = question.getMinValue();
        seekBar.setMax(question.getMaxValue());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = question.getMinValue() + progress;
                if (progressChanged > question.getMaxValue()) {
                    progressChanged = question.getMaxValue();
                }
                if (fromUser) {
                    answer.setText("Answer: " + progressChanged);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setMultiChoiseQuestion() {
        setContentView(R.layout.dialog_multichoise_question);
        Button continueButton = (Button) findViewById(R.id.continue_button);
        if (question.getQuestionNumber() + 1 == question.getTotalQuestions()) {
            continueButton.setText("Finish");
        }
        Spinner spinner = (Spinner) findViewById(R.id.spinner_answers);

        final ArrayList<String> list = new ArrayList<>();
        for (TCSQuestionChoice choice : question.getChoices()) {
            list.add(choice.getOptionText());
        }

        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                multiChoiseAnswer = list.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                sendAnswer(multiChoiseAnswer);
            }
        });
    }

    private void setNumericQuestion() {
        setContentView(R.layout.dialog_numeric_question);
        Button continueButton = (Button) findViewById(R.id.continue_button);
        if (question.getQuestionNumber() + 1 == question.getTotalQuestions()) {
            continueButton.setText("Finish");
        }
        final TextView numberAnswer = (TextView) findViewById(R.id.numeric_answer);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                try {
                    sendAnswer(Integer.parseInt(numberAnswer.getText().toString()));
                } catch (NumberFormatException e) {
                    sendAnswer(0);
                }
            }
        });
    }

    private void setFinishMessage(){
        setContentView(R.layout.dialog_dismiss_questionnaire);
        Button continueButton = (Button) findViewById(R.id.continue_button);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(listener != null){
                    listener.finishQuestionnaire();
                }
            }
        });
    }

    private void sendAnswer(Object answer) {
        if (listener != null) {
            listener.sendAnswer(answer);
        }
    }
}

