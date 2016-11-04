package com.pacificfjord.pfapi.views;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Aaron Vega on 1/28/15.
 */
public class TCSSkin {
    public static final String NAME = "name";
    public static final String COLOR = "color";
    public static final String SWITCH_APP = "switch_app";
    public static final String INPUT_TEXT = "input_text";
    public static final String BUTTON = "button";
    public static final String LABEL = "label";
    public static final String TITLE_BAR = "title_bar";
    public static final String LOGIN = "login";
    public static final String HOME_PAGE = "home_page";
    public static final String NOTIFICATION = "notification";
    public static final String MENU = "menu";

    public static final String BG_COLOR = "bg_color";
    public static final String FONT_SIZE = "font_size";
    public static final String TEXT_COLOR = "text_color";
    public static final String TITLE_COLOR = "title_color";
    public static final String OPACITY = "opacity";
    public static final String PRIMARY_COLOR = "primary_color";
    public static final String PRIMARY_COLOR_DARK = "primary_color_dark";
    public static final String ACCENT_COLOR = "accent_color";
    public static final String BG_IMAGE = "bg_image";
    public static final String ENTER_CODE_BG = "enter_code_bg";
    public static final String LOGO_SMALL = "logo_small";
    public static final String LOGO_MEDIUM = "logo_medium";
    public static final String CLIENT_LOGO = "client_logo";
    public static final String LOGO = "logo";
    public static final String SEPARATOR = "separator";
    public static final String TOOLBAR = "menu_";
    public static final String NOTIFICATIONS = "notifications_";
    public static final String HEADER_BG_COLOR = "header_bg_color";
    public static final String HEADER_TEXT_COLOR = "header_text_color";
    public static final String PLACEHOLDER_COLOR = "placeholder_color";
    public static final String DIVIDER_COLOR = "divider_colog";
    public static final String HEADER_COLOR = "header_color";

    public static final String FONT = "OpenSans-Light.ttf";

    private String name;
    private String color;
    private String primaryColor;
    private String primaryColorDark;
    private String accentColor;
    private String opacity;
    private String bgImage;
    private String logoSmall;
    private String logoMedium;
    private String clientLogo;
    private String separator;
    private String bgColor;
    private Map<String, String> switchApp;
    private Map<String, String> inputText;
    private Map<String, String> button;
    private Map<String, String> label;
    private Map<String, String> titleBar;
    private Map<String, String> login;
    private Map<String, String> homePage;
    private Map<String, String> notification;
    private Map<String, String> menu;

    public Map<String, String> getInputText() {
        return inputText;
    }

    public void setInputText(Map<String, String> inputText) {
        this.inputText = inputText;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getButton() {
        return button;
    }

    public void setButton(Map<String, String> button) {
        this.button = button;
    }

    public String getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(String color) {
        this.primaryColor = color;
    }

    public String getAccentColor() {
        return accentColor;
    }

    public void setAccentColor(String accentColor) {
        this.accentColor = accentColor;
    }

    public String getPrimaryColorDark() {
        return primaryColorDark;
    }

    public void setPrimaryColorDark(String primaryColorDark) {
        this.primaryColorDark = primaryColorDark;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Map<String, String> getSwitchApp() {
        return switchApp;
    }

    public void setSwitchApp(Map<String, String> switchApp) {
        this.switchApp = switchApp;
    }

    public String getOpacity() {
        return opacity;
    }

    public void setOpacity(String opacity) {
        this.opacity = opacity;
    }

    public Map<String, String> getLabel() {
        return label;
    }

    public void setLabel(Map<String, String> label) {
        this.label = label;
    }

    public Map<String, String> getTitleBar() {
        return titleBar;
    }

    public void setTitleBar(Map<String, String> titleBar) {
        this.titleBar = titleBar;
    }

    public Map<String, String> getLogin() {
        return login;
    }

    public void setLogin(Map<String, String> login) {
        this.login = login;
    }

    public Map<String, String> getHomePage() {
        return homePage;
    }

    public void setHomePage(Map<String, String> homePage) {
        this.homePage = homePage;
    }

    public String getBgImage() {
        return bgImage;
    }

    public void setBgImage(String bgImage) {
        this.bgImage = bgImage;
    }

    public String getLogoSmall() {
        return logoSmall;
    }

    public void setLogoSmall(String clientLogo) {
        this.logoSmall = clientLogo;
    }

    public String getClientLogo() {
        return clientLogo;
    }

    public void setClientLogo(String clientLogo) {
        this.clientLogo = clientLogo;
    }

    public String getLogoMedium() {
        return logoMedium;
    }

    public void setLogoMedium(String logoMedium) {
        this.logoMedium = logoMedium;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public Map<String, String> getNotification() {
        return notification;
    }

    public void setNotification(Map<String, String> notification) {
        this.notification = notification;
    }

    public Map<String, String> getMenu() {
        return menu;
    }

    public void setMenu(Map<String, String> menu) {
        this.menu = menu;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public TCSSkin(JSONObject json) throws JSONException {
        name = json.getString(NAME);

        if(json.has(COLOR) && !json.isNull(COLOR)){
            color = json.getString(COLOR);
        }
        if (json.has(PRIMARY_COLOR) && !json.isNull(PRIMARY_COLOR)) {
            primaryColor = json.getString(PRIMARY_COLOR);
        } else {
            primaryColor = color;
        }

        if(json.has(PRIMARY_COLOR_DARK) && !json.isNull(PRIMARY_COLOR_DARK)){
            primaryColorDark = json.getString(PRIMARY_COLOR_DARK);
        } else {
            primaryColorDark = color.substring(0, 5) + "00";
        }

        if(json.has(ACCENT_COLOR) && !json.isNull(ACCENT_COLOR)){
            accentColor = json.getString(ACCENT_COLOR);
        } else {
            accentColor = color.substring(0, 5) + "ff";
        }

        opacity = json.getString(OPACITY);

        if(json.has(LOGO_SMALL) && !json.isNull(LOGO_SMALL)) {
            logoSmall = json.getString(LOGO_SMALL);
        }

        if(json.has(LOGO_MEDIUM) && !json.isNull(LOGO_MEDIUM)) {
            logoMedium = json.getString(LOGO_MEDIUM);
        }

        if(json.has(CLIENT_LOGO) && !json.isNull(CLIENT_LOGO)) {
            clientLogo = json.getString(CLIENT_LOGO);
        }

        if (json.has(BG_IMAGE)) {
            bgImage = json.getString(BG_IMAGE);
        } else {
            bgImage = null;
        }
        separator = json.getString(SEPARATOR);
        bgColor = json.getString(BG_COLOR);
        inputText = toMap((JSONObject) json.get(INPUT_TEXT));
        button = toMap((JSONObject) json.get(BUTTON));

        if(json.has(SWITCH_APP) && !json.isNull(SWITCH_APP)) {
            switchApp = toMap((JSONObject) json.get(SWITCH_APP));
        }

        label = toMap((JSONObject) json.get(LABEL));
        titleBar = toMap((JSONObject) json.get(TITLE_BAR));
        if (json.has(LOGIN)) {
            login = toMap((JSONObject) json.get(LOGIN));
        } else {
            login = null;
        }
        homePage = toMap((JSONObject) json.get(HOME_PAGE));
        notification = toMap((JSONObject) json.get(NOTIFICATION));
        menu = toMap((JSONObject) json.get(MENU));
    }

    private Map toMap(JSONObject json) throws JSONException {
        Map<String, String> map = new HashMap<String, String>();

        Iterator<String> keyItr = json.keys();

        while (keyItr.hasNext()) {
            String key = keyItr.next();
            String value = json.getString(key);

            map.put(key, value);
        }

        return map;
    }
}
