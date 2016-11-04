package com.sparkcompass.tobaccodock.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.pacificfjord.pfapi.TCSAppInstance;
import com.pacificfjord.pfapi.TCSDataReturnedDelegate;
import com.pacificfjord.pfapi.TCSRequestSuccessDelegate;
import com.pacificfjord.pfapi.views.TCSSkin;
import com.sparkcompass.tobaccodock.R;
import com.sparkcompass.tobaccodock.common.Constants;
import com.sparkcompass.tobaccodock.common.TCSDialog;
import com.sparkcompass.tobaccodock.profile.ProvisionalUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

//import com.linkedin.platform.APIHelper;
//import com.linkedin.platform.LISession;
//import com.linkedin.platform.LISessionManager;
//import com.linkedin.platform.errors.LIApiError;
//import com.linkedin.platform.errors.LIAuthError;
//import com.linkedin.platform.listeners.ApiListener;
//import com.linkedin.platform.listeners.ApiResponse;
//import com.linkedin.platform.listeners.AuthListener;
//import com.linkedin.platform.utils.Scope;

/**
 * Created by mind-p6 on 8/31/15.
 */
public class SignupLoginDialog extends TCSDialog implements View.OnClickListener {


    @Bind(R.id.linkedin_login)
    protected ImageView linkedinLogin;
    @Bind(R.id.login_button)
    protected Button loginButton;
    @Bind(R.id.close_button)
    protected ImageView closeButton;
    @Bind(R.id.header_title)
    protected TextView headerTitle;
    @Bind(R.id.header_toolbar)
    protected RelativeLayout headerToolbar;
    @Bind(R.id.sign_up)
    protected Button signUpButton;
    @Bind(R.id.progress_container)
    protected RelativeLayout progressContainer;

    @Bind(R.id.welcome_container)
    protected RelativeLayout welcomeContainer;
    @Bind(R.id.user_name)
    protected TextView userName;
    @Bind(R.id.continue_button)
    protected Button continueButton;
    @Bind(R.id.welcome_message)
    protected TextView welcomeMessage;


    private Context context;
    private CallbackManager callbackManager;
    private OnEmailRegisterButtonsListener loginButtonListener;
    private GoogleSignInListener googleSignInListener;
    private LoginStateListener loginStateListener;
    private String messageToUser;

    public SignupLoginDialog(Context context, CallbackManager callbackManager) {
        super(context);
        this.context = context;
        this.callbackManager = callbackManager;
        try {
            googleSignInListener = (GoogleSignInListener) context;
            loginButtonListener = (OnEmailRegisterButtonsListener) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_signup_login);
        ButterKnife.bind(this);

        customizeViews(TCSAppInstance.getInstance().getSelectedSkin());

        linkedinLogin.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
        continueButton.setOnClickListener(this);
        closeButton.setVisibility(View.VISIBLE);
        headerTitle.setText(R.string.profile);
        headerTitle.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linkedin_login:
//                LISessionManager.getInstance(context).init((Activity) context, buildScope(), new AuthListener() {
//                    @Override
//                    public void onAuthSuccess() {
//                        setUpdateState();
//                    }
//
//                    @Override
//                    public void onAuthError(LIAuthError error) {
//                        setUpdateState();
//                    }
//                }, true);
                break;
            case R.id.login_button:
                dismiss();
                if (loginButtonListener != null) {
                    loginButtonListener.onAppLoginButtonClick();
                }
                break;
            case R.id.close_button:
                dismiss();
                if (loginStateListener != null) {
                    loginStateListener.onCancelLogin();
                }
                break;
            case R.id.sign_up:
                dismiss();
                if (loginButtonListener != null) {
                    loginButtonListener.onAppSignUpButtonClick();
                }
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

        loginButton.setBackgroundColor(Color.parseColor(Constants.yellowColor));
        signUpButton.setBackgroundColor(Color.parseColor(skinTemplate.getColor()));
    }

    private void registerUser(final ProvisionalUser user) {
        if (user != null) {
            progressContainer.setVisibility(View.VISIBLE);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("userName", user.getUserName());
            params.put("emailAddress", user.getEmailAddress());
            params.put("password", Constants.SOCIAL_PASSWORD);
            params.put("firstName", user.getFirstName());
            params.put("lastName", user.getLastName());
            Map<String, String> attrs = new HashMap<>();
            params.put("attributes", attrs);
            TCSAppInstance.getInstance().registerUserParameters(params, new TCSDataReturnedDelegate() {
                @Override
                public void done(JSONObject data, Error error) {
                    if (error == null) {
                        messageToUser = context.getString(R.string.thanks_for_signing);
                    } else {
                        messageToUser = context.getString(R.string.welcome_back_message);
                    }

                    login(user);
                    progressContainer.setVisibility(View.GONE);
                }
            });
        }
    }

    private void login(final ProvisionalUser user) {
        progressContainer.setVisibility(View.VISIBLE);
        TCSAppInstance.getInstance().loginUser(user.getEmailAddress(), Constants.SOCIAL_PASSWORD, new TCSRequestSuccessDelegate() {
            @Override
            public void done(boolean success, String reply) {
                if (success) {
                    welcomeMessage.setText(messageToUser);
                    userName.setText(user.getFirstName() + " " + user.getLastName() + "!");
                    welcomeContainer.setVisibility(View.VISIBLE);
                } else {
                    final AlertDialog dialog = new AlertDialog.Builder(context)
                            .setTitle("Login Error")
                            .setMessage("Please try again")
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
                            dialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
                        }
                    });
                    dialog.show();
                }
                progressContainer.setVisibility(View.GONE);
            }
        });
    }

    private void setUpdateState() {
//        LISessionManager sessionManager = LISessionManager.getInstance(context);
//        LISession session = sessionManager.getSession();
//
//        if (session.isValid()) {
//            progress.show();
//            APIHelper apiHelper = APIHelper.getInstance(context);
//            apiHelper.getRequest(context, Constants.LINKED_IN_TOP_CARD_URL, new ApiListener() {
//                @Override
//                public void onApiSuccess(ApiResponse s) {
//                    progress.hide();
//                    ProvisionalUser user = new ProvisionalUser();
//                    JSONObject userObject = s.getResponseDataAsJson();
//                    try {
//                        user.setUserName(userObject.getString("emailAddress"));
//                        user.setEmailAddress(userObject.getString("emailAddress"));
//                        user.setLastName(userObject.getString("lastName"));
//                        user.setFirstName(userObject.getString("firstName"));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    registerUser(user);
//
//                }
//
//                @Override
//                public void onApiError(LIApiError error) {
//                    progress.hide();
//                    Toast.makeText(context, "Unable to connect with LinkedIn service", Toast.LENGTH_LONG).show();
//                }
//            });
//        }
    }

//    private static Scope buildScope() {
//        return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE, Scope.R_EMAILADDRESS);
//    }


    public void setLoginStateListener(LoginStateListener loginStateListener) {
        this.loginStateListener = loginStateListener;
    }
}
