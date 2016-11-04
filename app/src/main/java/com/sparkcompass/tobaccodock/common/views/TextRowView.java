package com.sparkcompass.tobaccodock.common.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sparkcompass.tobaccodock.R;

/**
 * Created by aaronmaiden on 8/8/14.
 */
public class TextRowView extends RelativeLayout {

    private TextView label;
    private EditText text;
    private Typeface typeface;

    public TextRowView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextRowView);
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/SofiaProBold.otf");
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.text_row_view, this, true);

        String label = a.getString(R.styleable.TextRowView_the_label);

        boolean password = a.getBoolean(R.styleable.TextRowView_password, false);
        boolean email = a.getBoolean(R.styleable.TextRowView_email, false);

        if (password) {
            getTextField().setTransformationMethod(PasswordTransformationMethod.getInstance());
        }

        if(email) {
            getTextField().setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        }

        getLabel().setText(label);
    }

    private TextView getLabel() {
        if (label == null) {
            label = (TextView) findViewById(R.id.label);
            label.setTypeface(typeface);
        }

        return label;
    }

    private EditText getTextField() {
        if (text == null) {
            text = (EditText) findViewById(R.id.text);
            text.setTypeface(typeface);
        }

        return text;
    }

    public String getText() {
        return getTextField().getText().toString();
    }

    public void setText(String text) {
        getTextField().setText(text);
    }
}
