package com.sparkcompass.tobaccodock.common;

import android.content.Context;

import com.sparkcompass.tobaccodock.R;
import com.sparkcompass.tobaccodock.utils.ColorUtils;

/**
 * Created by mind-p6 on 8/28/15.
 */
public class Constants {

    public static final String PRODUCTION_API_URL = "https://demo.pacificfjord.com";
    public static final String PRODUCTION_API_KEY = "659c51cf4a60436392bf91a45351bff9";
    public static final String GIMBAL_KEY_PRODUCTION = "1c7e9e7d-cf71-46b6-a00b-c529bef041aa";

    public static final String STAGING_API_URL = "https://lifetime.pacificfjord.com/development";
    public static final String STAGING_API_KEY = "8178957365c3448c8d70eb5c9bafc7c4";
    public static final String GIMBAL_KEY_STAGING = "6e92a39b-dd89-4ce2-9209-82b4abcd4c31";

    public static final String DEVELOPMENT_API_URL = "https://arkus.pacificfjord.com/development";
    public static final String DEVELOPMENT_API_KEY = "82aa0f04c7a74fd0ae1fdacd4806e36b";
    public static final String GIMBAL_KEY_DEVELOPMENT = "b2b484ba-7110-428e-9c59-6404e69edac5";

    public static final String QA_API_URL = "https://salford.pacificfjord.com/staging";
    public static final String QA_API_KEY = "82aa0f04c7a74fd0ae1fdacd4806e36b";

    public static final String VUFORIA_LICENSE_KEY = "AdWZfj7/////AAAAACagKKlEj0Y7qiTZcocvifAD0Ib04MXAwvgsjwwK2oHecOQTRS8QtWzt+XpVkonNhaKC784EGTyKQejXPH4jXBFInC8EZhyzCgPwCe3gJ+kpLSehoEeQFIDzO0R4K8ezoFRMWIexM/DKYw0i0dv/Jgk5tjPOHQT6Pe638+/Xzjym5NTMk1QV+5lx1tL7rP37zK/Yp/2Y89/ZipwZVILYLSsvt3BP9Yz9abnI0CzhG2jwOOCE5YhNbgzhy6j+4+ZRcRDrcixsfvH69vANjG0ldKVxBE6sjjaw5lcG6NW9n1qdNrN/uPG0YMXAwcqyFugjw6KtnUo1FJ9APSKOsCAMj8zWOok6iYyx3kOK+pkl6UWb";
    public static final String VUFORIA_ACCESS_KEY = "787e54b1e2809cd562cc9e9c0430a7491d96257b";
    public static final String VUFORIA_SECRET_KEY = "ae0eddc0e3ba97e8b040cf027d29100edf8b1000";

    public static final String EULA_URL = "http://pacificfjord.com/content/sparkcompass/common/pages/EndUserLicenceAgreement.html";
    public static final String POLICY_URL = "http://pacificfjord.com/content/sparkcompass/common/pages/PrivacyPolicy.html";
    public static final String SOCIAL_PASSWORD = "ed507dd034344d8aa046b855cf19f9ff";
    public static final String HELP_URL = "http://salford.pacificfjord.com/content/TobaccoHelp/index.html";

    public static final String BASE_APP_UID = "8178957365c3448c8d70eb5c9bafc7c4";

    public static final String GCM_SENDER_ID = "803254111153";
    public static final String LOG_TAG = "ECOS Application";
    public static final String MAIN_MENU = "MainMenu";
    public static final String TWITTER = "Twitter";
    public static final String BANNER_MENU = "BannerMenu";
    public static final String TOOLBAR_MENU = "ToolbarMenu";

    public static final String KEY_RESTART_APP = "RESTART_APP";
    public static final String APP_READY = "app_ready";
    public static final String STATUS = "status";
    public static final String WEB_URL = "web_url";

    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static final String TWITTER_KEY = "JAcIFpsypDMGYT8v6YKZLckC1";
    public static final String TWITTER_SECRET = "EYDd5NKyrNGWflYqn8sboQiYqdztBsi9HjrqFj6oHhTyYVkFP5";


    public final static int REQUEST_ENABLE_BT = 1;

    public static final String IMAGE_TOOLBAR = "image_toolbar";
    public static final String TITLE_TOOLBAR = "title_toolbar";

    public static final String SETTINGS_TITLE = "Settings";
    public static final String IMAGE_RECOGNITION_TITLE = "AR";
    public static final String HOME_TITLE = "Home";
    public static final String MAP_TITLE = "Map";
    public static final String PROFILE_TITLE = "Profile";
    public static final String WEBVIEW_PAGE_TITLE = "WebView";
    public static final String SHARE_TITLE = "SHARE";
    public static final String SWITCHAPP_TITLE = "SWITCH_APP";
    public static final String NOTIFICATIONS_TITLE = "Notifications";
    public static final String BEACON_TRAY_TITLE = "Beacon Tray";
    public static final String SCHEDULED_EVENTS_TITLE = "Events";

    public static final String UID = "uid";
    public static final String EMAIL_ADDRESS = "emailAddress";
    public static final String USER_NAME = "userName";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String UPDATES = "uoffer";
    public static final String ADDRESS = "address";
    public static final String ATTRIBUTES = "attributes";
    public static final String CITY = "city";
    public static final String STATE = "state";
    public static final String ZIP = "zip";
    public static final String CELL = "cell";

    public static final int RR_REQUEST_FINE_LOCATION = 1;
    public static final int RR_CAMERA = 2;

    public static final String VR_PREFIX = "vr_video";
    public static final String VR_URL = "https://salford.pacificfjord.com/content/video/";
    public static final String VIDEO_TAG = "video_name";

    public static final String yellowColor = "#ffd204";

    public static String getTemplate(Context context) {
        return "{\n" +
                "  \"name\": \"JuicyOrange\",\n" +
                "  \"primary_color\": \"" + ColorUtils.getColor(context, R.color.primary) + "\",\n" +
                "  \"primary_color_dark\": \"" + ColorUtils.getColor(context, R.color.primary_dark) + "\",\n" +
                "  \"accent_color\": \"" + ColorUtils.getColor(context, R.color.accent) + "\",\n" +
                "  \"bg_color\": \"#303030\",\n" +
                "  \"separator\": \"#E9E9E9\",\n" +
                "  \"opacity\": \"200\",\n" +
                "  \"logo_small\": \"file://ecos_logo.png\",\n" +
                "  \"logo_medium\": \"file://ecos_logo.png\",\n" +
                "  \"menu\": {\n" +
                "    \"bg_color\": \"" + ColorUtils.getColor(context, R.color.primary)  + "\",\n" +
                "    \"divider_color\": \"#E9E9E9\",\n" +
                "    \"header_color\": \"#FAFAFA\"\n" +
                "  },\n" +
                "  \"input_text\": {\n" +
                "    \"bg_color\": \"#9ca0ad\",\n" +
                "    \"text_color\": \"#ffffff\",\n" +
                "    \"placeholder_color\": \"#e7e7ea\"\n" +
                "  },\n" +
                "  \"button\": {\n" +
                "    \"bg_color\": \"#fb8200\",\n" +
                "    \"text_color\": \"#ffffff\"\n" +
                "  },\n" +
                "  \"label\": {\n" +
                "    \"text_color\": \"" + ColorUtils.getColor(context, android.R.color.white) + "\"\n" +
                "  },\n" +
                "  \"home_page\": {\n" +
                "    \"menu_opacity\": \"255\",\n" +
                "    \"menu_bg_color\": \"" + ColorUtils.getColor(context, R.color.primary) + "\",\n" +
                "    \"notifications_bg_color\": \"" + ColorUtils.getColor(context, R.color.primary) + "\"\n" +
                "  },\n" +
                "  \"notification\": {\n" +
                "    \"text_color\": \"#404040\",\n" +
                "    \"date_color\": \"#979797\",\n" +
                "    \"bg_color\": \"#FFFFFF\",\n" +
                "    \"header_bg_color\": \"#FFFFFF\",\n" +
                "    \"header_text_color\": \"#404040\"\n" +
                "  },\n" +
                "  \"title_bar\": {\n" +
                "    \"logo\": \"\",\n" +
                "    \"text_color\": \"#FFFFFF\",\n" +
                "    \"opacity\": \"0\"\n" +
                "  },\n" +
                "  \"beacons\": {\n" +
                "    \"input_text_bg\": \"#3f90b4\",\n" +
                "    \"input_text_placeholder\": \"#7fc0d6\",\n" +
                "    \"input_text_color\": \"#dedede\",\n" +
                "    \"button_text_color\": \"#00d6f7\",\n" +
                "    \"bg_color\": \"#00b3f0\",\n" +
                "    \"title_color\": \"#404040\"\n" +
                "  },\n" +
                "  \"login\": {\n" +
                "    \"bg_image\": \"T2W_Sunset-Cove.png\",\n" +
                "    \"bg_color\": \"#303030\",\n" +
                "    \"opacity\": \"10\",\n" +
                "    \"text_color\": \"#FFFFFF\"\n" +
                "  },\n" +
                "  \"switch_app\": {\n" +
                "    \"bg_image\": \"T2W_Sunset-Cove.png\",\n" +
                "    \"bg_color\": \"#303030\",\n" +
                "    \"opacity\": \"10\",\n" +
                "    \"text_color\": \"#FFFFFF\"\n" +
                "  }\n" +
                "}";
    }

}
