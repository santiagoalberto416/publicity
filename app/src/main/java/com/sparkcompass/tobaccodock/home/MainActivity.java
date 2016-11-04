package com.sparkcompass.tobaccodock.home;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.pacificfjord.pfapi.TCSAppAction;
import com.pacificfjord.pfapi.TCSAppInstance;
import com.pacificfjord.pfapi.TCSMenu;
import com.pacificfjord.pfapi.TCSSuccessDelegate;
import com.pacificfjord.pfapi.beacons.GimbalManager;
import com.pacificfjord.pfapi.utilites.TCSAppActionDelegate;
import com.pacificfjord.pfapi.utilites.TCSNotificationService;
import com.pacificfjord.pfapi.utilites.TCSTwitter;
import com.pacificfjord.pfapi.views.TCSSkin;
import com.sparkcompass.tobaccodock.R;
import com.sparkcompass.tobaccodock.common.AppPageEnum;
import com.sparkcompass.tobaccodock.common.Constants;
import com.sparkcompass.tobaccodock.common.TCSActivity;
import com.sparkcompass.tobaccodock.common.TCSService;
import com.sparkcompass.tobaccodock.common.UserPreferences;
import com.sparkcompass.tobaccodock.common.WebViewActivity;
import com.sparkcompass.tobaccodock.events.ScheduledEventsActivity;
import com.sparkcompass.tobaccodock.home.menu.MainMenuAdapter;
import com.sparkcompass.tobaccodock.login.LoginDialog;
import com.sparkcompass.tobaccodock.login.OnEmailRegisterButtonsListener;
import com.sparkcompass.tobaccodock.login.SignUpDialog;
import com.sparkcompass.tobaccodock.login.SignupLoginDialog;
import com.sparkcompass.tobaccodock.map.MapFragment;
import com.sparkcompass.tobaccodock.notifications.NotificationsFragment;
import com.sparkcompass.tobaccodock.profile.ProfileFragment;
import com.sparkcompass.tobaccodock.questionnaires.QuestionnaireActivity;
import com.sparkcompass.tobaccodock.settings.SettingsFragment;
import com.sparkcompass.tobaccodock.utils.ImageUtils;
import com.sparkcompass.tobaccodock.welcome.SwitchAppActivity;

import java.util.Stack;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends TCSActivity implements TCSAppActionDelegate,
        View.OnClickListener, MenuItemClickListener, DialogInterface.OnDismissListener,
        OnEmailRegisterButtonsListener, ChangeFragmentListener{

    @Bind(R.id.menu_button)
    protected ImageView menuButton;
    @Bind(R.id.back_button)
    protected ImageView backButton;
    @Bind(R.id.drawer_layout)
    protected DrawerLayout drawerLayout;
    @Bind(R.id.main_menu_list)
    protected RecyclerView mainMenuList;
    @Bind(R.id.content_frame)
    protected FrameLayout contentFrame;
    @Bind(R.id.header_logo)
    protected ImageView headerLogo;
    @Bind(R.id.header_title)
    protected TextView titleView;
    @Bind(R.id.logout_button)
    protected RelativeLayout logoutButton;

    @Bind(R.id.navigation_view)
    protected NavigationView navigationView;

    private FragmentManager fragmentManager;
    private Stack<AppPageEnum> fragmentAppPageStack;
    private AppPageEnum currentAppPageFromBack;
    private boolean firstPressBackButton = true;


    private static final String MANUFACTURER_NOT_SUPPORTED = "samsung";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FOR UNITYAR VIEW
        getWindow().takeSurface(null);
        getWindow().setFormat(PixelFormat.RGBX_8888); // <--- This makes xperia play happy
        //FOR UNITYAR VIEW
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        headerLogo.setVisibility(View.VISIBLE);
        menuButton.setVisibility(View.VISIBLE);
        menuButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);

        chargeMainMenu();


        MainMenuAdapter adapter = new MainMenuAdapter(this,
                TCSAppInstance.getInstance().menuWithName(Constants.MAIN_MENU), this);
        mainMenuList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mainMenuList.setLayoutManager(llm);
        mainMenuList.setAdapter(adapter);

        TCSAppInstance.getInstance().setAppActionDelegate(this);

        TCSTwitter.setupTwitter(MainActivity.this, Constants.TWITTER_KEY,
                Constants.TWITTER_SECRET, "oauth://sparkcompass");
        TCSTwitter.oathCallback(getIntent());

        fragmentAppPageStack = new Stack<>();

        if (contentFrame != null) {
            fragmentManager = getSupportFragmentManager();

            if (savedInstanceState != null) {
                return;
            }

            fragmentAppPageStack.push(AppPageEnum.valueOf("HOME"));
            showAppPageFragment(AppPageEnum.HOME);
        }
        startBeaconsService();
        callbackManager = CallbackManager.Factory.create();

        // Build GoogleApiClient with access to basic profile
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.EMAIL))
                .build();


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String uid = extras.getString(TCSNotificationService.KEYNOTIFICATIONACTIONUID, "");
            for (TCSAppAction action : TCSAppInstance.getInstance().getAppActionList()) {
                if (action.getUid().equals(uid)) {
                    didReceiveAppAction(action);
                }
            }
        }
    }

    private void chargeMainMenu(){
        TCSMenu menu = TCSAppInstance.getInstance().menuWithName(Constants.MAIN_MENU);
        if(menu!=null){
            putIntoMenu();
        }else {
            TCSAppInstance.getInstance().syncMenu(Constants.MAIN_MENU, new TCSSuccessDelegate() {
                @Override
                public void done(boolean success) {
                    if(success){
                        putIntoMenu();
                    }
                }
            });
        }
    }

    private void putIntoMenu(){
        MainMenuAdapter adapter = new MainMenuAdapter(this,
                TCSAppInstance.getInstance().menuWithName(Constants.MAIN_MENU), this);
        mainMenuList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mainMenuList.setLayoutManager(llm);
        mainMenuList.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        GimbalManager.getInstance().setBeaconPopCountDelegate(null);
        if (!isServiceRunning(TCSService.class)) {
            Intent intent = new Intent(getApplicationContext(), TCSService.class);
            startService(intent);
        }
        super.onDestroy();
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void showAppPageFragment(AppPageEnum appPage) {
        Fragment fragment = null;
        drawerLayout.closeDrawer(GravityCompat.START);
        boolean isActivity = false;

        setTranslucentBar(false);

        if (appPage.equalsName("Notifications") || appPage == AppPageEnum.IMAGE_RECOGNITION) {
            backButton.setVisibility(View.VISIBLE);
            menuButton.setVisibility(View.GONE);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        } else {
            backButton.setVisibility(View.GONE);
            menuButton.setVisibility(View.VISIBLE);
        }

        switch (appPage) {
            case HOME:
                updateHeader(Constants.IMAGE_TOOLBAR, null);
                fragment = HomeFragment.newInstance(this);
                ((HomeFragment) fragment).setMenuItemListener(this);
                break;
            case PROFILE:
                if (TCSAppInstance.getInstance().userIsLoggedIn()) {
                    updateHeader(Constants.TITLE_TOOLBAR, Constants.PROFILE_TITLE);
                    fragment = ProfileFragment.newInstance();
                } else {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            goToLoginDialog();
                            fragmentAppPageStack.pop();
                        }
                    }, 250);
                }
                break;
            case SETTINGS:
                updateHeader(Constants.TITLE_TOOLBAR, Constants.SETTINGS_TITLE);
                fragment = SettingsFragment.newInstance();
                break;
            case MAP:
                updateHeader(Constants.TITLE_TOOLBAR, Constants.MAP_TITLE);
                fragment = MapFragment.newInstance();
                break;
            case CONTACT:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", getString(R.string.email_contact), null)); //TODO: Set correct email address
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_contact_subject)); //TODO: Set correct email subject
                emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_contact_text));
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
                break;
            case NOTIFICATIONS:
                updateHeader(Constants.TITLE_TOOLBAR, Constants.NOTIFICATIONS_TITLE);
                fragment = NotificationsFragment.newInstance();
                ((NotificationsFragment) fragment).setMenuItemListener(this);
                if (!isFragmentOnManager(appPage.toString())) {
                    firstPressBackButton = true;
                    if (currentAppPageFromBack != null) {
                        fragmentAppPageStack.push(currentAppPageFromBack);
                        currentAppPageFromBack = null;
                    }
                    fragmentAppPageStack.push(AppPageEnum.valueOf(appPage.toString()));
                }

                break;
        }

        if (!isActivity) {
            replaceFragmentTransaction(fragment, appPage);
        }
    }

    private void setToolbarElevation(boolean enabled) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        if (enabled) {
            headerToolbar.setElevation(4);
        } else {
            headerToolbar.setElevation(0);
        }
    }

    private void setTranslucentBar(boolean enabled) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }

        Window w = getWindow();
        int translucentStatusFlag = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;

        if (enabled) {
            w.setFlags(translucentStatusFlag, translucentStatusFlag);
        } else {
            w.clearFlags(translucentStatusFlag);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            goBack();
        }
    }

    public void goBack() {
        setToolbarElevation(true);

        if (fragmentAppPageStack.size() >= 1) {
            if (firstPressBackButton) {
                firstPressBackButton = false;
                fragmentAppPageStack.pop();
            }
            if (fragmentAppPageStack.size() == 0) {
                super.onBackPressed();
            } else {
                currentAppPageFromBack = fragmentAppPageStack.peek();
                showAppPageFragment(fragmentAppPageStack.pop());
            }
            backButton.setVisibility(View.GONE);
            menuButton.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    private boolean isFragmentOnManager(String tag) {
        Fragment existingFragment = fragmentManager.findFragmentByTag(tag);

        if (existingFragment != null) {
            if (existingFragment.isVisible() && drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, existingFragment)
                        .addToBackStack(null)
                        .commit();
            }
            return true;
        }

        return false;
    }

    private void replaceFragmentTransaction(final Fragment fragment, final AppPageEnum appPage) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (fragment != null) {
                    if (drawerLayout.isDrawerOpen(navigationView)) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                    }

                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out)
                            .replace(R.id.content_frame, fragment, appPage.toString())
                            .commit();
                }
            }
        }, 250);
    }

    private void updateHeader(String type, String title) {
        if (type.equals(Constants.TITLE_TOOLBAR) && !title.isEmpty()) {
            headerLogo.setVisibility(View.GONE);
            titleView.setVisibility(View.VISIBLE);
            titleView.setText(title);
            if (title.equalsIgnoreCase(Constants.PROFILE_TITLE)) {
                logoutButton.setVisibility(View.VISIBLE);
            } else {
                logoutButton.setVisibility(View.GONE);
            }
        } else {
            headerLogo.setVisibility(View.VISIBLE);
            TCSSkin skin = TCSAppInstance.getInstance().getSelectedSkin();
            if (skin != null && skin.getClientLogo() != null &&
                    TCSAppInstance.getInstance().getSelectedSkin().getClientLogo().startsWith("http")) {
                ImageUtils.replaceImage(TCSAppInstance.getInstance().getSelectedSkin().getClientLogo(), headerLogo, this);
            }
            titleView.setVisibility(View.GONE);
            logoutButton.setVisibility(View.GONE);
        }


    }

    @Override
    public void didReceiveAppAction(final TCSAppAction action) {
        switch (action.getActionType()) {
            case ATYPE_APPAGE: {
                String appPage = action.getParamValueForKey("AppPage");

                Intent i;
                switch (AppPageEnum.valueOf(appPage)) {
                    case EVENTS:
                        i = new Intent(this, ScheduledEventsActivity.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
                        break;
                    case SWITCH_APP:
                        i = new Intent(this, SwitchAppActivity.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
                        break;
                    default:
                        try {
                            if (!isFragmentOnManager(appPage)) {
                                firstPressBackButton = true;
                                if (currentAppPageFromBack != null) {
                                    fragmentAppPageStack.push(currentAppPageFromBack);
                                    currentAppPageFromBack = null;
                                }
                                fragmentAppPageStack.push(AppPageEnum.valueOf(appPage));
                                showAppPageFragment(AppPageEnum.valueOf(appPage));
                            }
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                            Toast.makeText(this, R.string.tcs_app_page_not_supported, Toast.LENGTH_SHORT).show();
                            drawerLayout.closeDrawer(GravityCompat.START);
                        }
                        break;
                }
                break;
            }
            case ATYPE_WEBURL:
                drawerLayout.closeDrawer(GravityCompat.START);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String url = action.getParamValueForKey("Url");
                        Intent i = null;

                        if (!url.toLowerCase().contains(Constants.VR_PREFIX)) {
                            i = new Intent(MainActivity.this, WebViewActivity.class);
                            i.putExtra(Constants.WEB_URL, action.getParamValueForKey("Url"));
                            startActivity(i);
                            overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
                        } else {
                            //Google VR
                            String[] content = url.split("//");

                            if (content.length <= 1) {
                                Toast.makeText(MainActivity.this, R.string.tcs_app_page_not_supported, Toast.LENGTH_SHORT).show();
                                return;
                            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                if (Build.MANUFACTURER.toLowerCase().equals(MANUFACTURER_NOT_SUPPORTED)
                                        && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                                    String packageName = "com.samsung.android.video360";
                                    PackageManager packageManager = getPackageManager();
                                    ApplicationInfo applicationInfo = null;
                                    try {
                                        applicationInfo = packageManager.getApplicationInfo(packageName, 0);
                                    } catch (PackageManager.NameNotFoundException e) {
                                        searchOnStore();
                                    }
                                    if (applicationInfo != null) {
                                        i = new Intent();
                                        i.setData(Uri.parse("samsungvrmobile://sideload/?url=https://salford.pacificfjord.com/content/video/" + content[1] + "&video_type=_mono360"));
                                        startActivity(i);
                                        overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
                                    } else {
                                        searchOnStore();
                                    }
                                } else {
                                }
                            }
                        }
                    }
                }, 250);
                break;
            case ATYPE_QUESTIONAIRE:
                if (TCSAppInstance.getInstance().userIsLoggedIn()) {
                    Intent i = new Intent(this, QuestionnaireActivity.class);
                    i.putExtra(QuestionnaireActivity.QUESTIONNAIRE_ID,
                            Integer.parseInt(action.getParamValueForKey("Questionnaire")));
                    startActivity(i);
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("Questionnaire")
                            .setMessage("You must be logged in as a user to answer questionnaires")
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
                break;
        }
    }


    private void searchOnStore() {
        new AlertDialog.Builder(this)
                .setTitle("Install Samsung VR")
                .setMessage("To see this content you need to install Samsung VR app. Do you want to continue and install Samsung VR app from Galaxy Apps store?")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent();
//                        intent.setData(Uri.parse("samsungapps://ProductDetail/com.samsung.android.video360"));
                        intent.setData(Uri.parse("market://details?id=com.samsung.android.video360"));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }

    private void startBeaconsService() {
        if (TCSAppInstance.getInstance().supportsBeacons() && !UserPreferences.getTurnBluetoothMessage(this)) {
            final AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Proximity Information")
                    .setMessage(getString(R.string.bluetooth_message))
                    .setPositiveButton("Enable",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                                    if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
                                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                        startActivityForResult(enableBtIntent, Constants.REQUEST_ENABLE_BT);
                                    }

                                    int fineLocationPermissionsCheck =
                                            ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

                                    if (fineLocationPermissionsCheck == PackageManager.PERMISSION_GRANTED) {
                                        TCSAppInstance.getInstance().setBluetoothEnabled(true, new TCSSuccessDelegate() {
                                            @Override
                                            public void done(boolean enabled) {

                                            }
                                        });
                                    } else {
                                        if (ActivityCompat
                                                .shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                                            new AlertDialog.Builder(MainActivity.this)
                                                    .setTitle("Proximity Information")
                                                    .setMessage(
                                                            "This feature makes use of location services in order to check you in automatcally into Healty places. Would you like to review your permissions for this app.")
                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            ActivityCompat.requestPermissions(MainActivity.this,
                                                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                                    Constants.RR_REQUEST_FINE_LOCATION);
                                                        }
                                                    }).setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Toast.makeText(MainActivity.this, "Bluetooth service was not activated.", Toast.LENGTH_LONG)
                                                            .show();
                                                }
                                            }).show();
                                        } else {
                                            ActivityCompat
                                                    .requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                            Constants.RR_REQUEST_FINE_LOCATION);
                                        }
                                    }

                                    dialog.dismiss();
                                }
                            })
                    .setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).setCancelable(false).create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg0) {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(MainActivity.this, R.color.primary));
                    dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(MainActivity.this, R.color.primary));
                }
            });

            UserPreferences.setTurnBluetoothMessage(MainActivity.this, true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.RR_REQUEST_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    TCSAppInstance.getInstance().setLocationsEnabled(true, new TCSSuccessDelegate() {
                        @Override
                        public void done(boolean success) {

                        }
                    });
                    TCSAppInstance.getInstance().setBluetoothEnabled(true, new TCSSuccessDelegate() {
                        @Override
                        public void done(boolean enabled) {

                        }
                    });
                }
                break;
            case Constants.RR_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    onBackPressed();
                }

                break;

        }
    }

    @Override
    public Context getContextForMessage() {
        return this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_button:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.logout_button:
                TCSAppInstance.getInstance().logOutEndUser();
                onBackPressed();
                break;
        }
    }

    @Override
    public void itemClickedWithAction(TCSAppAction tcsAppAction) {
        didReceiveAppAction(tcsAppAction);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (TCSAppInstance.getInstance().userIsLoggedIn()) {
            showAppPageFragment(AppPageEnum.PROFILE);
        }
    }

    private void goToLoginDialog() {
        SignupLoginDialog loginDialog = new SignupLoginDialog(MainActivity.this, callbackManager);
        loginDialog.setOnDismissListener(MainActivity.this);
        loginDialog.show();
        loginDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                googleListener = null;
            }
        });
    }

    @Override
    public void customizeViews(TCSSkin skinTemplate) {
        super.customizeViews(skinTemplate);
        ImageUtils.replaceImage(skinTemplate.getClientLogo(), headerLogo, this);
        if (skinTemplate.getMenu().containsKey(TCSSkin.BG_COLOR) && !skinTemplate.getMenu().get(TCSSkin.BG_COLOR).isEmpty()) {
            mainMenuList.setBackgroundColor(Color.parseColor(skinTemplate.getMenu().get(TCSSkin.BG_COLOR)));
            navigationView.setBackgroundColor(Color.parseColor(skinTemplate.getMenu().get(TCSSkin.BG_COLOR)));
        }
    }

    @Override
    public void onAppLoginButtonClick() {
        LoginDialog loginDialog = new LoginDialog(this);
        loginDialog.show();
    }

    @Override
    public void onAppSignUpButtonClick() {
        SignUpDialog signUpDialog = new SignUpDialog(this);
        signUpDialog.show();
    }

    @Override
    public void changeFragment(AppPageEnum page) {
        showAppPageFragment(page);
    }



}
