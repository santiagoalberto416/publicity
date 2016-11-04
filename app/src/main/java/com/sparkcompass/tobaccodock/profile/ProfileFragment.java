package com.sparkcompass.tobaccodock.profile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pacificfjord.pfapi.TCSAPIConnector;
import com.pacificfjord.pfapi.TCSAppInstance;
import com.pacificfjord.pfapi.TCSRequestSuccessDelegate;
import com.pacificfjord.pfapi.TCSUserProfileDelegate;
import com.pacificfjord.pfapi.views.TCSSkin;
import com.sparkcompass.tobaccodock.R;
import com.sparkcompass.tobaccodock.common.AppPageEnum;
import com.sparkcompass.tobaccodock.common.Constants;
import com.sparkcompass.tobaccodock.common.TCSFragment;
import com.sparkcompass.tobaccodock.home.MainActivity;
import com.sparkcompass.tobaccodock.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by mind-p6 on 8/31/15.
 */
public class ProfileFragment extends TCSFragment implements View.OnClickListener {

    @Bind(R.id.first_name)
    protected EditText firstName;
    @Bind(R.id.last_name)
    protected EditText lastName;
    @Bind(R.id.email_address)
    protected EditText emailAddress;
    @Bind(R.id.password)
    protected EditText password;
    @Bind(R.id.save_button)
    protected Button saveButton;
    @Bind(R.id.progress_container)
    protected RelativeLayout progressContainer;
    @Bind(R.id.coordinator_layout)
    protected CoordinatorLayout coordinatorLayout;
    @Bind(R.id.read_eula_button)
    protected Button readEulaButton;
    @Bind(R.id.read_privacy_button)
    protected Button readPrivacyButton;

    private ProvisionalUser user;
    private int editTextFocusColor;
    private ProgressDialog progress;


    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, rootView);
        customizeViews(TCSAppInstance.getInstance().getSelectedSkin());
        saveButton.setOnClickListener(this);
        readEulaButton.setOnClickListener(this);
        readPrivacyButton.setOnClickListener(this);
        loadProfile();
        progress = new ProgressDialog(getContext());
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void loadProfile() {
        progressContainer.setVisibility(View.VISIBLE);
        TCSAppInstance.getInstance().getUserProfile(new TCSUserProfileDelegate() {
            @Override
            public void done(boolean success, JSONObject profile) {
                try {
                    user = new ProvisionalUser(profile);
                    firstName.setText(user.getFirstName());
                    lastName.setText(user.getLastName());
                    emailAddress.setText(user.getEmailAddress());
                    password.setText(Constants.SOCIAL_PASSWORD);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(coordinatorLayout, "Error getting profile information", Snackbar.LENGTH_SHORT)
                            .setActionTextColor(ContextCompat.getColor(getContext(), R.color.primary))
                            .show();
                    ((MainActivity) getActivity()).showAppPageFragment(AppPageEnum.HOME);
                }
                progressContainer.setVisibility(View.GONE);

            }
        });
    }

    private void validateUserDataForRegistration() {
        if (firstName.getText().toString().isEmpty()) {
            createWarningDialog(getContext().getString(R.string.first_name_warning));
        } else if (lastName.getText().toString().isEmpty()) {
            createWarningDialog(getContext().getString(R.string.last_name_warning));
        } else if (!StringUtils.isValidEmail(emailAddress.getText().toString())) {
            createWarningDialog(getContext().getString(R.string.email_address_warning));
        } else if (password.getText().toString().isEmpty()) {
            createWarningDialog(getContext().getString(R.string.password_warning));
        } else {
            ProvisionalUser user = new ProvisionalUser();
            user.setEmailAddress(emailAddress.getText().toString());
            user.setUserName(emailAddress.getText().toString());
            user.setFirstName(firstName.getText().toString());
            user.setLastName(lastName.getText().toString());
            saveUserProfile(user);
        }
    }

    private void saveUserProfile(ProvisionalUser user) {
        if (!TCSAPIConnector.getInstance().internetIsReachable()) {
            final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setTitle("No Internet Connection")
                    .setMessage("An internet connection is required to update your profile")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg0) {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
                }
            });
            dialog.show();
        } else {
            JSONObject profile = new JSONObject();
            JSONObject attributes = new JSONObject();

            try {
                profile.put("firstName", user.getFirstName());
                profile.put("lastName", user.getLastName());
                profile.put("attributes", attributes);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            progress.setTitle("Saving Profile");
            progress.setMessage("Please Wait while saving...");
            progress.show();
            TCSAppInstance.getInstance().updateUserProfile(profile, new TCSRequestSuccessDelegate() {
                @Override
                public void done(boolean success, String reply) {
                    progress.dismiss();
                    if (success) {
                        Snackbar
                                .make(coordinatorLayout, R.string.profile_update_success, Snackbar.LENGTH_LONG)
                                .show();
                    } else {
                        Snackbar
                                .make(coordinatorLayout, R.string.problem_updating_profile, Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.re_try, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        validateUserDataForRegistration();
                                    }
                                })
                                .show();
                    }


                }
            });

        }
    }

    private void createWarningDialog(String message) {
        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.sig_up_warning)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
            }
        });

        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_button:
                validateUserDataForRegistration();
                break;
            case R.id.read_eula_button:
                createDialog(Constants.EULA_URL);
                break;
            case R.id.read_privacy_button:
                createDialog(Constants.POLICY_URL);
                break;
        }
    }

    @Override
    public void customizeViews(TCSSkin skinTemplate) {
        if (skinTemplate.getColor() != null) {
            editTextFocusColor = Color.parseColor(skinTemplate.getColor());
            firstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        firstName.getBackground().setColorFilter(editTextFocusColor, PorterDuff.Mode.SRC_ATOP);
                        firstName.setHintTextColor(editTextFocusColor);
                    } else {
                        firstName.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.sdia_edit_text_gray), PorterDuff.Mode.SRC_ATOP);
                        firstName.setHintTextColor(ContextCompat.getColor(getContext(), R.color.sdia_edit_text_gray));
                    }
                }
            });

            lastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        lastName.getBackground().setColorFilter(editTextFocusColor, PorterDuff.Mode.SRC_ATOP);
                        lastName.setHintTextColor(editTextFocusColor);
                    } else {
                        lastName.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.sdia_edit_text_gray), PorterDuff.Mode.SRC_ATOP);
                        lastName.setHintTextColor(ContextCompat.getColor(getContext(), R.color.sdia_edit_text_gray));
                    }
                }
            });


            lastName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        lastName.clearFocus();
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(password.getWindowToken(), 0);

                    }
                    return false;
                }
            });

        }
    }

    public void createDialog(String file) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

        WebView wv = new WebView(getContext());
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
        alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        alert.setCancelable(true);
        alert.show();
    }
}
