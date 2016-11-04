package com.sparkcompass.tobaccodock.common;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pacificfjord.pfapi.views.TCSSkin;
import com.sparkcompass.tobaccodock.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WebViewActivity extends TCSActivity implements View.OnClickListener {

    @Bind(R.id.web_view)
    protected WebView webView;
    @Bind(R.id.close_button)
    protected ImageView closeButton;
    @Bind(R.id.back_button)
    protected ImageView backButton;
    @Bind(R.id.header_title)
    protected TextView titleView;
    @Bind(R.id.progress_container)
    protected RelativeLayout progressContainer;

    protected String url;

    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            titleView.setText(view.getTitle());
            progressContainer.setVisibility(View.GONE);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        ButterKnife.bind(this);

        closeButton.setVisibility(View.VISIBLE);
        closeButton.setOnClickListener(this);
        backButton.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(this);
        titleView.setVisibility(View.VISIBLE);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(true);
        webView.setWebViewClient(webViewClient);


        if (savedInstanceState != null) {
            url = savedInstanceState.getString(Constants.WEB_URL, "");
        } else {
            url = getIntent().getExtras().getString(Constants.WEB_URL, "");
        }

        progressContainer.setVisibility(View.VISIBLE);
        webView.loadUrl(url);

    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            finishWebViewActivity();
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(Constants.WEB_URL, url);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_button:
                finishWebViewActivity();
                break;
            case R.id.back_button:
                onBackPressed();
                break;
        }
    }

    private void finishWebViewActivity() {
        finish();
        overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
    }

    @Override
    public void customizeViews(TCSSkin skinTemplate) {
        super.customizeViews(skinTemplate);

    }
}
