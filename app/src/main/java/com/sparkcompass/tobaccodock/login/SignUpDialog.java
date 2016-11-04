package com.sparkcompass.tobaccodock.login;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.pacificfjord.pfapi.TCSAppInstance;
import com.pacificfjord.pfapi.TCSDataReturnedDelegate;
import com.pacificfjord.pfapi.TCSRequestSuccessDelegate;
import com.pacificfjord.pfapi.views.TCSSkin;
import com.sparkcompass.tobaccodock.R;
import com.sparkcompass.tobaccodock.common.Constants;
import com.sparkcompass.tobaccodock.common.TCSDialog;
import com.sparkcompass.tobaccodock.profile.ProvisionalUser;
import com.sparkcompass.tobaccodock.utils.StringUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by danielalcantara on 11/9/15.
 */
public class SignUpDialog extends TCSDialog implements View.OnClickListener {

    @Bind(R.id.first_name)
    protected EditText firstName;
    @Bind(R.id.last_name)
    protected EditText lastName;
    @Bind(R.id.email_address)
    protected EditText emailAddress;
    @Bind(R.id.password)
    protected EditText password;
    @Bind(R.id.create_account)
    protected Button createAccountButton;
    @Bind(R.id.close_button)
    protected ImageView closeButton;
    @Bind(R.id.header_toolbar)
    protected RelativeLayout headerToolbar;
    @Bind(R.id.header_title)
    protected TextView headerTitle;
    @Bind(R.id.coordinator_layout)
    protected CoordinatorLayout coordinatorLayout;
    @Bind(R.id.bmi)
    protected EditText bmi;
    @Bind(R.id.gender)
    protected Spinner gender;
    @Bind(R.id.membership_type)
    protected Spinner membershipType;

    @Bind(R.id.welcome_container)
    protected RelativeLayout welcomeContainer;
    @Bind(R.id.user_name)
    protected TextView userName;
    @Bind(R.id.continue_button)
    protected Button continueButton;

    private int editTextFocusColor;
    private ProgressDialog progress;
    private LoginStateListener loginStateListener;

    public SignUpDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_sign_up);
        ButterKnife.bind(this);

        continueButton.setOnClickListener(this);
        closeButton.setVisibility(View.VISIBLE);
        closeButton.setOnClickListener(this);
        headerTitle.setText(R.string.signup_with_email);
        headerTitle.setVisibility(View.VISIBLE);
        createAccountButton.setOnClickListener(this);

        customizeViews(TCSAppInstance.getInstance().getSelectedSkin());

        progress = new ProgressDialog(getContext());
        progress.setTitle("In progress");
        progress.setMessage("Registering profile...");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        gender.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getContext(), R.array.membership_type, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        membershipType.setAdapter(adapter1);

    }

    @Override
    public void customizeViews(TCSSkin skinTemplate) {
        headerToolbar.getBackground().setColorFilter(Color.parseColor(skinTemplate.getColor()), PorterDuff.Mode.SRC_ATOP);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor(skinTemplate.getPrimaryColorDark()));
        }

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

            emailAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        emailAddress.getBackground().setColorFilter(editTextFocusColor, PorterDuff.Mode.SRC_ATOP);
                        emailAddress.setHintTextColor(editTextFocusColor);
                    } else {
                        emailAddress.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.sdia_edit_text_gray), PorterDuff.Mode.SRC_ATOP);
                        emailAddress.setHintTextColor(ContextCompat.getColor(getContext(), R.color.sdia_edit_text_gray));
                    }
                }
            });

            password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        password.clearFocus();
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(password.getWindowToken(), 0);

                    }
                    return false;
                }
            });
            createAccountButton.setBackgroundColor(Color.parseColor(Constants.yellowColor));
            continueButton.setBackgroundColor(editTextFocusColor);
        }
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
            user.setPassword(password.getText().toString());
            registerUser(user);
        }
    }

    private void registerUser(final ProvisionalUser user) {
        if (user != null) {
            progress.show();
            Map<String, Object> params = new HashMap<>();
            params.put("userName", user.getUserName());
            params.put("emailAddress", user.getEmailAddress());
            params.put("password", user.getPassword());
            params.put("firstName", user.getFirstName());
            params.put("lastName", user.getLastName());
            Map<String, String> attrs = new HashMap<>();
            /*
            attrs.put("membershipType", membershipType.getSelectedItem().toString());
            attrs.put("bmi", bmi.getText().toString());
            attrs.put("gender", gender.getSelectedItem().toString());
            */
            params.put("attributes", attrs);
            TCSAppInstance.getInstance().registerUserParameters(params, new TCSDataReturnedDelegate() {
                @Override
                public void done(JSONObject data, Error error) {
                    if (error != null) {
                        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                                .setTitle("Registration Error")
                                .setMessage("The email you provided is already in use please login")
                                .setPositiveButton(android.R.string.ok,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int which) {

                                            }
                                        })
                                .setCancelable(false).create();

                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface arg0) {
                                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
                            }
                        });
                        dialog.show();
                    } else {
                        login(user);
                    }
                    progress.dismiss();

                }
            });
        }
    }

    private void login(final ProvisionalUser user) {
        progress.show();
        TCSAppInstance.getInstance().loginUser(user.getEmailAddress(), user.getPassword(), new TCSRequestSuccessDelegate() {
            @Override
            public void done(boolean success, String reply) {
                if (success) {
                    showWelcomeMessage(user);
                } else {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Login Error")
                            .setMessage("Please go to the login screen ad provide your credentials")
                            .setPositiveButton(android.R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int which) {

                                        }
                                    })
                            .setCancelable(false).show();
                }
                progress.dismiss();
            }
        });
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
                dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
            }
        });

        dialog.show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_button:
                dismiss();
                if (loginStateListener != null) {
                    loginStateListener.onCancelLogin();
                }
                break;
            case R.id.create_account:
                validateUserDataForRegistration();
                break;
            case R.id.continue_button:
                dismiss();
                if (loginStateListener != null) {
                    loginStateListener.onUserLogin();
                }
                break;
        }
    }

    private void showWelcomeMessage(ProvisionalUser user) {
        userName.setText(user.getFirstName() + " " + user.getLastName() + "!");
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(600);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                welcomeContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        welcomeContainer.startAnimation(anim);
    }

    public void setLoginStateListener(LoginStateListener loginStateListener) {
        this.loginStateListener = loginStateListener;
    }

}
