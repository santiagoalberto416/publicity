package com.pacificfjord.pfapi;

import java.util.HashMap;

/**
 * Created by tom on 16/01/14.
 */
public class TCSAPIConstants {

    /**
     * The API Request Method
     */
    public enum TCSRequestMethod {
        METHOD_ERROR,
        POST,
        GET,
        PUT,
        DELETE
    };

    /**
     * The API Request Type
     */
    public static class TCSRequestType{
        public static final String REGISTERAPPINSTANCE = "RegisterAppInstance";
        public static final String UPDATEAPPINSTANCE = "UpdateAppInstance";
        public static final String REGISTERUSER = "RegisterUser";
        public static final String LOGINUSER = "LoginUser";
        public static final String GETUSERPROFILE = "GetUserProfile";
        public static final String UPDATEUSERPROFILE = "UpdateUserProfile";
        public static final String CHANGEEMAIL = "ChangeEmail";
        public static final String CHANGEPASSWORD = "ChangePassword";
        public static final String FORGOTPASSWORD = "ForgotPassword";
        public static final String GETGEOLOCATIONS = "GetGeolocations";
        public static final String GETBEACONS = "GetBeacons";
        public static final String GETIMAGETARGETS = "GetImageTargets";
        public static final String GETNOTIFYACTIONS = "GetNotifyActions";
        public static final String GETFILE = "GetFile";
        public static final String SYNCMENU = "SyncMenu";
        public static final String REGISTERGCSDEVICE = "RegisterGCDevice";
        public static final String LOGEVENT ="LogEvent";
        public static final String GETAPPUID = "GetAppUId";
        public static final String GETQUESTIONS = "GetQuestions";
        public static final String SENDANSWERS = "SendAnswers";
        public static final String GETSCHEDULES = "GetSchedules";
        public static final String GETSCHEDULEDITEMS = "GetScheduledItems";
        public static final String GETSCHEDULEITEM = "GetScheduleItem";
        public static final String SENDRSVP = "SendRSVP";
        public static final String GETTEMPLATES = "GetTemplates";
        public static final String LOADSEGMENTATIONS = "LoadSegmentations";
        public static final String GETENDUSERPROFILEOPTIONS = "GetUserProfileOptions";
        public static final String UPDATEPROFILEOPTIONS = "UpdateProfileOptions";
        public static final String GETACCOUNTABLEBEACON = "GetAccountableBeacon";
        public static final String GETAUDITORYOPTIONS = "GetAuditoryOptions";
        public static final String GETAUDITORYHISTORY = "GetAuditoryHistory";
        public static final String POSTAUDITORYOPTIONS = "PostAuditoryOptions";
        public static final String POSTADVERTISINGID = "PostAdvertisingId";
        public static final String GETPRINCIPALS = "GetPrincipals";
        public static final String GETPRINCIPALDETAILS = "GetPrincipalDetails";
        public static final String LINKTOPRINCIPAL = "LinkToPrincipal";
        public static final String UNLINKFROMPRINCIPAL = "UnlinkFromPrincipal";
        public static final String GETTIMEFORMEDS = "GetTimeForMeds";
        public static final String POSTMEDSTAKEN = "PostMedsTaken";
        public static final String NOTIFYDOCTOR = "NotifyDoctor";
        public static final String GETLINKEDPRINCIPALS = "GetLinkedPrincipals";
        public static final String GETFACILITIES = "GetFacilities";
        public static final String GETSUBCRIBE = "GetSubscribe";
        public static final String GETCONCESSIONCATEGORIES = "GetConcessionCategories";
        public static final String GETCONCESSIONLIST = "GetConcessionList";
        public static final String GETTERMS = "GetTerms";
        public static final String ACCEPTTERMS = "AcceptTerms";
        public static final String PROFILEIMAGE = "ProfileImage";
    }

    /**
     * The default API Version Path if none is defined on the APICallDefinition
     */
    public String defaultAPIVersionPath = "/api/1";

    /**
     * The API Call definitions
     */
    private HashMap<String,TCSAPICallDefinition> apiCallDefinitions;

    /**
     * constructor
     */
    public TCSAPIConstants()
    {

    }

    /**
     * Method to add an API call definition
     *
     * @param call the Call Definition
     * @param callType the RequestType
     * @return Whether the call has been added successfully
     */
    public boolean AddAPICall(TCSAPICallDefinition call, String callType)
    {
        if(apiCallDefinitions==null)
            apiCallDefinitions = new HashMap<>();

        if(apiCallDefinitions.containsKey(callType))
            return false;

        apiCallDefinitions.put(callType, call);

        return true;
    }

    /**
     * Method to get the API path from the cally type and appURL
     *
     * @param callType The type of the call
     * @param appUrl The app URL
     * @return The api call path
     */
    public String APIPathForCallType(String callType, String appUrl)
    {
        if(!CheckCallDefinitions())
            return null;

        TCSAPICallDefinition definition = apiCallDefinitions.get(callType);

        if(definition!=null)
        {
            String path = appUrl;
            if(definition.versionPath!=null)
                path += definition.versionPath;
            else
                path += defaultAPIVersionPath;
            path += definition.apiPath;
            return path;
        }
        return null;
    }

    /**
     * Return the Request Method for this call
     *
     * @param callType the type of the call to get the RequestMethod for
     * @return the Request Method for the call
     */
    public TCSRequestMethod MethodForCallType(String callType)
    {
        if(!CheckCallDefinitions())
            return TCSRequestMethod.METHOD_ERROR;

        TCSAPICallDefinition definition = apiCallDefinitions.get(callType);

        if(definition!=null)
            return definition.method;
        else
            return TCSRequestMethod.METHOD_ERROR;
    }

    /** Check the call definitions have been defined
     *
     * @return whether or not the definitions are defined
     */
    private boolean CheckCallDefinitions()
    {
        if(apiCallDefinitions==null || apiCallDefinitions.size()==0)
            return false;

        if(defaultAPIVersionPath==null)
            return false;

        return true;
    }
}
