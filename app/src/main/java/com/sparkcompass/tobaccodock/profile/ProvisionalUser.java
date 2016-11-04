package com.sparkcompass.tobaccodock.profile;


import com.sparkcompass.tobaccodock.common.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mind-p6 on 9/3/15.
 */
public class ProvisionalUser {
    private String userId = "";
    private String emailAddress = "";
    private String userName = "";
    private String firstName = "";
    private String lastName = "";
    private String password = "";

    public ProvisionalUser() {

    }

    public ProvisionalUser(JSONObject jsonObject) throws JSONException {
        this.userId = jsonObject.getString(Constants.UID);
        this.emailAddress = jsonObject.getString(Constants.EMAIL_ADDRESS);
        this.userName = jsonObject.getString(Constants.USER_NAME);
        this.firstName = jsonObject.getString(Constants.FIRST_NAME);
        this.lastName = jsonObject.getString(Constants.LAST_NAME);
        JSONObject attributes = jsonObject.getJSONObject(Constants.ATTRIBUTES);
    }


    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
