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
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pacificfjord.pfapi.TCSAPIConnector;
import com.pacificfjord.pfapi.TCSAppInstance;
import com.pacificfjord.pfapi.TCSRequestSuccessDelegate;
import com.pacificfjord.pfapi.TCSUserProfileDelegate;
import com.pacificfjord.pfapi.views.TCSSkin;
import com.sparkcompass.tobaccodock.R;
import com.sparkcompass.tobaccodock.common.Constants;
import com.sparkcompass.tobaccodock.common.TCSDialog;
import com.sparkcompass.tobaccodock.profile.ProvisionalUser;
import com.sparkcompass.tobaccodock.utils.ImageUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mind-p6 on 8/31/15.
 */
public class LoginDialog extends TCSDialog implements View.OnClickListener {

    @Bind(R.id.email_address)
    protected EditText emailAddress;
    @Bind(R.id.password)
    protected EditText password;
    @Bind(R.id.login_button)
    protected Button loginButton;
    @Bind(R.id.progress_container)
    protected RelativeLayout progressContainer;
    @Bind(R.id.close_button)
    protected ImageView closeButton;
    @Bind(R.id.header_toolbar)
    protected RelativeLayout headerToolbar;
    @Bind(R.id.header_title)
    protected TextView headerTitle;
    @Bind(R.id.logo)
    protected ImageView logo;
    @Bind(R.id.forgot_password)
    protected TextView forgotPassword;
    @Bind(R.id.coordinator_layout)
    protected CoordinatorLayout coordinatorLayout;

    @Bind(R.id.main_container)
    protected RelativeLayout mainContainer;
    @Bind(R.id.welcome_container)
    protected RelativeLayout welcomeContainer;
    @Bind(R.id.user_name)
    protected TextView userName;
    @Bind(R.id.continue_button)
    protected Button continueButton;

    private int editTextFocusColor;
    private LoginStateListener loginStateListener;
    private ProgressDialog progress;


    public LoginDialog(Context context) {
        super(context);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_login);
        ButterKnife.bind(this);
        closeButton.setVisibility(View.VISIBLE);
        closeButton.setOnClickListener(this);
        headerTitle.setText(R.string.login_with_email);
        headerTitle.setVisibility(View.VISIBLE);
        loginButton.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        continueButton.setOnClickListener(this);
        customizeViews(TCSAppInstance.getInstance().getSelectedSkin());

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

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    password.getBackground().setColorFilter(editTextFocusColor, PorterDuff.Mode.SRC_ATOP);
                    password.setHintTextColor(editTextFocusColor);
                } else {
                    password.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.sdia_edit_text_gray), PorterDuff.Mode.SRC_ATOP);
                    password.setHintTextColor(ContextCompat.getColor(getContext(), R.color.sdia_edit_text_gray));
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

    }

    private void validateCredentials() {
        if (emailAddress.getText().toString().isEmpty()) {
            createWarningDialog(getContext().getString(R.string.login_warning), getContext().getString(R.string.email_address_error));
        } else if (password.getText().toString().isEmpty()) {
            createWarningDialog(getContext().getString(R.string.login_warning), getContext().getString(R.string.empty_password));
        } else {
            loginUserCredentials();
        }
    }

    private void loginUserCredentials() {
        progressContainer.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);
        TCSAppInstance.getInstance().loginUser(emailAddress.getText().toString(),
                password.getText().toString(), new TCSRequestSuccessDelegate() {
                    @Override
                    public void done(boolean success, String reply) {
                        loginButton.setEnabled(true);
                        if (success) {
                            TCSAppInstance.getInstance().getUserProfile(new TCSUserProfileDelegate() {
                                @Override
                                public void done(boolean success, JSONObject profile) {
                                    if (success) {
                                        try {
                                            showWelcomeMessage(new ProvisionalUser(profile));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                    progressContainer.setVisibility(View.GONE);
                                }
                            });
                        } else {
                            createWarningDialog(getContext().getString(R.string.login), getContext().getString(R.string.incorrect_credentials));
                            progressContainer.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void showWelcomeMessage(ProvisionalUser user) {
        userName.setText(user.getFirstName() + " " + user.getLastName());
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
                mainContainer.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        welcomeContainer.startAnimation(anim);
    }

    private void createWarningDialog(String title, String message) {
        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(title)
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
            case R.id.login_button:
                validateCredentials();
                break;
            case R.id.close_button:
                dismiss();
                if (loginStateListener != null) {
                    loginStateListener.onCancelLogin();
                }
                break;
            case R.id.forgot_password:
                showForgotPasswordAlert();
                break;
            case R.id.continue_button:
                dismiss();
                if (loginStateListener != null) {
                    loginStateListener.onUserLogin();
                }
                break;
        }
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
        }

        if (skinTemplate.getClientLogo() != null) {
            ImageUtils.replaceImage(skinTemplate.getClientLogo(), logo, getContext());
            // i dont need this because the logo has multiple colors
            //logo.setColorFilter(Color.parseColor(skinTemplate.getColor()));
        }

        if (skinTemplate.getColor() != null) {
            loginButton.setBackgroundColor(Color.parseColor(Constants.yellowColor));
            continueButton.setBackgroundColor(Color.parseColor(Constants.yellowColor));
        }
    }

    private void showForgotPasswordAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter your login email");
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = input.getText().toString();
                forgotPasswordAsyncTask(email);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
                dialog.getButton(BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
            }
        });

        dialog.show();
    }

    protected void forgotPasswordAsyncTask(String email) {
        if (!TCSAPIConnector.getInstance().internetIsReachable()) {
            createWarningDialog(getContext().getString(R.string.no_internet_connection_title),
                    getContext().getString(R.string.no_internet_connection_message));
        } else {
            progress = new ProgressDialog(getContext());
            progress.setTitle("Password Reset");
            progress.setMessage("Attempting to send password reset link");
            progress.show();
            TCSAppInstance.getInstance().forgotUserPassword(email, new TCSRequestSuccessDelegate() {
                @Override
                public void done(boolean success, String reply) {
                    progress.dismiss();
                    if (success) {
                        createWarningDialog(getContext().getString(R.string.forgot_password_title),
                                getContext().getString(R.string.forgot_password_message));
                    } else {
                        createWarningDialog(getContext().getString(R.string.forgot_password_title),
                                getContext().getString(R.string.wrong_email));

                    }
                }
            });
        }
    }

    public void setLoginStateListener(LoginStateListener loginStateListener) {
        this.loginStateListener = loginStateListener;
    }
}
