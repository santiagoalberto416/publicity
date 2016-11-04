package com.sparkcompass.tobaccodock.welcome;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.pacificfjord.pfapi.TCSAppInstance;
import com.pacificfjord.pfapi.TCSAppInstanceBeginDelegate;
import com.pacificfjord.pfapi.TCSDataReturnedDelegate;
import com.pacificfjord.pfapi.TCSMenu;
import com.pacificfjord.pfapi.TCSMenuItem;
import com.pacificfjord.pfapi.TCSSuccessDelegate;
import com.sparkcompass.tobaccodock.R;
import com.sparkcompass.tobaccodock.common.Constants;
import com.sparkcompass.tobaccodock.common.WebViewActivity;
import com.sparkcompass.tobaccodock.home.MainActivity;
import com.sparkcompass.tobaccodock.utils.ImageUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tom on 16/11/14.
 */
public class SwitchAppActivity extends Activity implements View.OnClickListener {

    public static final String FEATURED_APPS = "FeaturedApps";


    @Bind(R.id.list)
    protected LinearLayout list;
    @Bind(R.id.remember_option)
    protected CheckBox rememberOption;
    @Bind(R.id.enter_code)
    protected RelativeLayout enterCode;
    @Bind(R.id.lock)
    protected RelativeLayout lock;
    @Bind(R.id.get_started)
    protected ImageView getStartedButton;
    @Bind(R.id.go_button)
    protected Button go;
    @Bind(R.id.code_input)
    protected EditText code;
    @Bind(R.id.dummy)
    protected View view;
    @Bind(R.id.no_notifications_text_view)
    protected TextView noFeatured;

    private String appKey;
    private boolean syncedFeaturedEvents;
    private boolean syncedTemplate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_app);
        ButterKnife.bind(this);

        view.requestFocus();


        getStartedButton.setOnClickListener(this);
        go.setOnClickListener(this);

        lock.getBackground().setAlpha(100);
        lock.setVisibility(View.VISIBLE);

        appKey = Constants.PRODUCTION_API_KEY;

        if (!TCSAppInstance.getInstance().getAppUID().equals(appKey)) {
            TCSAppInstance.getInstance().reset();
            TCSAppInstance.getInstance()
                    .setupApp(appKey, this);

            TCSAppInstance.getInstance().beginWithCallback(new TCSAppInstanceBeginDelegate() {
                @Override
                public void done(boolean success, String reply) {
                    if (success) {
                        syncMenuData();
                        TCSAppInstance.getInstance().updateGCMRegistrationKey();
                    } else {
                        Log.d("SwitchAppFragment", "AppInstance Did not begin successfully");
                    }
                }
            });
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    syncMenuData();
                }
            }, 2000);
        }
        code.clearFocus();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(broadcastReceiver, new IntentFilter(Constants.APP_READY));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go_button:
                enterCode();
                break;
            case R.id.get_started:
                switchDemoKey(appKey);
                break;
        }
    }

    private void validateDataSync() {
        if (syncedTemplate && syncedFeaturedEvents) {
            lock.setVisibility(View.GONE);
        }
    }

    private void syncMenuData() {
        TCSAppInstance.getInstance().syncMenu(FEATURED_APPS, new TCSSuccessDelegate() {
            @Override
            public void done(boolean success) {
                if (success) {
                    TCSMenu menu = TCSAppInstance.getInstance().menuWithName(FEATURED_APPS);
                    if (menu.menuItems.size() > 0) {
                        fillList(menu, LayoutInflater.from(SwitchAppActivity.this));
                    } else {
                        noFeatured.setVisibility(View.VISIBLE);
                    }
                } else {
                    noFeatured.setVisibility(View.VISIBLE);
                    lock.setVisibility(View.GONE);
                }
                syncedFeaturedEvents = true;
                validateDataSync();
            }
        });
        try {
            TCSAppInstance.getInstance().getTemplates(new TCSSuccessDelegate() {
                @Override
                public void done(boolean success) {
                    syncedTemplate = true;
                    validateDataSync();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enterCode() {
        String stringCode = code.getText().toString();

        if (!stringCode.isEmpty()) {
            sendAppCode(stringCode);
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Event code error")
                    .setMessage("Please provide a valid event code")
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        }

        InputMethodManager imm = (InputMethodManager) this.
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(code.getWindowToken(), 0);
    }

    private void sendAppCode(String appCode) {
        enterCode.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Submitting your event code...");
        progressDialog.show();

        TCSAppInstance.getInstance().getAppUIDByCode(appCode, new TCSDataReturnedDelegate() {
            @Override
            public void done(JSONObject data, Error error) {
                enterCode.setEnabled(true);
                progressDialog.dismiss();

                if (error == null) {
                    try {
                        String uid = data.getString("uid");
                        switchDemoKey(uid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    new AlertDialog.Builder(SwitchAppActivity.this)
                            .setTitle("Alternate App")
                            .setMessage("Error with app code")
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
            }
        });
    }

    private void fillList(final TCSMenu featuredMenu, LayoutInflater inflater) {
        for (final TCSMenuItem item : featuredMenu.menuItems) {
            RelativeLayout frameItem =
                    (RelativeLayout) inflater.inflate(R.layout.featured_item, list, false);
            TextView title = (TextView) frameItem.findViewById(R.id.title);
            title.setText(item.getName());

            ImageView image = (ImageView) frameItem.findViewById(R.id.image);
            TextView initial = (TextView) frameItem.findViewById(R.id.event_initial);

            if (!item.getIconUrl().equals("")) {
                ImageUtils.replaceImage(item.getIconUrl(), image, this);
            } else {
                initial.setVisibility(View.VISIBLE);
                initial.setText(item.getName().substring(0, 1));
            }

            frameItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchDemoKey(item.getAction().getParamValueForKey("Url"));
                }
            });
            frameItem.setBackgroundResource(R.drawable.rounded_corners);

            list.addView(frameItem);
        }
    }

    private void switchDemoKey(String uid) {
        if (uid.startsWith("http")) {
            Intent i = new Intent(this, WebViewActivity.class);
            i.putExtra(Constants.WEB_URL, uid);
            startActivity(i);
            overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
        } else {
            lock.getBackground().setAlpha(100);
            lock.setVisibility(View.VISIBLE);

            TCSAppInstance.getInstance().reset();
            TCSAppInstance.getInstance().setAppUID(uid);

            if (rememberOption.isChecked()) {
                TCSAppInstance.getInstance().setRememberedAppUID(uid);
            } else {
                TCSAppInstance.getInstance().setRememberedAppUID("");
            }

            Intent intent = new Intent(Constants.KEY_RESTART_APP);
            sendBroadcast(intent);
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(Constants.APP_READY)) {
                Intent mainActivity = new Intent(SwitchAppActivity.this, MainActivity.class);
                mainActivity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(mainActivity);
                overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
                finish();
            }
            lock.setVisibility(View.GONE);
        }
    };
}
