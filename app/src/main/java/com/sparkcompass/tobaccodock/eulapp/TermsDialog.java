package com.sparkcompass.tobaccodock.eulapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;


import com.sparkcompass.tobaccodock.R;
import com.sparkcompass.tobaccodock.common.Constants;
import com.sparkcompass.tobaccodock.common.UserPreferences;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Daniel Alcantara on 8/28/15.
 */
public class TermsDialog extends Dialog implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Context context;
    @Bind(R.id.read_eula_button)
    protected Button readEulaButton;
    @Bind(R.id.read_privacy_button)
    protected Button readPrivacyButton;
    @Bind(R.id.continue_button)
    protected Button continueButton;
    @Bind(R.id.agreed_check_box)
    protected CompoundButton agreedCompound;
    public TermsDialog(Context context) {
        super(context, R.style.DialogSlideAnim);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_terms);
        ButterKnife.bind(this);

        readEulaButton.setOnClickListener(this);
        readPrivacyButton.setOnClickListener(this);
        continueButton.setOnClickListener(this);
        agreedCompound.setOnCheckedChangeListener(this);

        validateContinueButton();
    }

    private void validateContinueButton() {
        if (agreedCompound.isChecked()) {
            continueButton.setEnabled(true);
        } else {
            continueButton.setEnabled(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.read_eula_button:
                createDialog(Constants.EULA_URL);
                break;
            case R.id.read_privacy_button:
                createDialog(Constants.POLICY_URL);
                break;
            case R.id.continue_button:
                if(agreedCompound.isChecked()){
                    UserPreferences.setTermsAccepted(context, true);
                } else {
                    UserPreferences.setTermsAccepted(context, false);
                }
                dismiss();
                break;

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        validateContinueButton();
    }

    public void createDialog(String file) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        WebView wv = new WebView(context);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setBuiltInZoomControls(true);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);

                return true;
            }
        });
        wv.loadUrl(file);

        alert.setView(wv);
        alert.setNegativeButton("Close", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        alert.setCancelable(true);
        alert.show();
    }
}
