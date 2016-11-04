package com.pacificfjord.pfapi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

//import org.apache.http.client.utils.URLEncodedUtils;


/**
 * Singleton Class for API calls
 *
 * @author Tom Mulder
 * @version 1.0.0
 * @date 16/01/14
 */

/**
 * The Main engine for making API calls with the Platform.
 */
public class TCSAPIConnector {
    private static TCSAPIConnector ourInstance = new TCSAPIConnector();



    /**
     * json key to the cache status on certain api results
     */
    public static final String JKEY_CACHESTATUS = "cacheStatus";
    /**
     * json key to the cache version on certain api results
     */
    public static final String JKEY_CACHEVERSION = "cacheVersion";
    /**
     * response if the cache version is out of sync
     */
    public static final String OUT_OF_SYNC = "OUT_OF_SYNC";

    private ConnectivityManager connMgr;
    private boolean serverReachable;
    private TimerTask requestQueueTimer;
    private ArrayList<TCSURLConnection> requestQueue;

    /**
     * Definitions of the API calls
     */
    public TCSAPIConstants pathDefinitions;
    /**
     * base url
     */
    public String baseURL;
    /**
     * app url for API calls eg. https://api.pacificfjord.com
     */
    public String appURL;

    public static TCSAPIConnector getInstance() {
        return ourInstance;
    }

    private TCSAPIConnector() {
        baseURL = "https://demo.pacificfjord.com";
        appURL = "https://demo.pacificfjord.com";

        pathDefinitions = new TCSAPIConstants();
        pathDefinitions.defaultAPIVersionPath = "/api/1";

        TCSAPICallDefinition definition;

        /****************************/
        definition = new TCSAPICallDefinition("/api/1", "/registerAppInstance", TCSAPIConstants.TCSRequestMethod.POST);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.REGISTERAPPINSTANCE);

        /****************************/
        definition = new TCSAPICallDefinition("/api/1", "/appInstance/update", TCSAPIConstants.TCSRequestMethod.PUT);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.UPDATEAPPINSTANCE);

        /****************************/
        definition = new TCSAPICallDefinition("/api/1", "/enduser/register", TCSAPIConstants.TCSRequestMethod.POST);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.REGISTERUSER);

        /****************************/
        definition = new TCSAPICallDefinition("/api/1", "/enduser/login", TCSAPIConstants.TCSRequestMethod.POST);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.LOGINUSER);

        /****************************/
        definition = new TCSAPICallDefinition("/api/1", "/enduser", TCSAPIConstants.TCSRequestMethod.GET);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.GETUSERPROFILE);

        /****************************/
        definition = new TCSAPICallDefinition("/api/1", "/enduser/update", TCSAPIConstants.TCSRequestMethod.PUT);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.UPDATEUSERPROFILE);

        /****************************/
        definition = new TCSAPICallDefinition("/api/1", "/enduser/changePassword", TCSAPIConstants.TCSRequestMethod.POST);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.CHANGEPASSWORD);

        /****************************/
        definition = new TCSAPICallDefinition("/api/1", "/enduser/changeEmail", TCSAPIConstants.TCSRequestMethod.POST);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.CHANGEEMAIL);

        /****************************/
        definition = new TCSAPICallDefinition("/api/1", "/enduser/forgotPassword", TCSAPIConstants.TCSRequestMethod.POST);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.FORGOTPASSWORD);

        /****************************/
        definition = new TCSAPICallDefinition("/api/1", "/menu", TCSAPIConstants.TCSRequestMethod.GET);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.SYNCMENU);

        /****************************/
        definition = new TCSAPICallDefinition("/api/1", "/appEvent", TCSAPIConstants.TCSRequestMethod.POST);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.LOGEVENT);

        /****************************/
        definition = new TCSAPICallDefinition("/api/1", "/beacons", TCSAPIConstants.TCSRequestMethod.GET);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.GETBEACONS);

        /****************************/
        definition = new TCSAPICallDefinition("/api/1", "/geolocations", TCSAPIConstants.TCSRequestMethod.GET);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.GETGEOLOCATIONS);

        /****************************/
        definition = new TCSAPICallDefinition("/api/1", "/imageTargets", TCSAPIConstants.TCSRequestMethod.GET);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.GETIMAGETARGETS);

        /****************************/
        definition = new TCSAPICallDefinition("/api/1", "/notifyActions", TCSAPIConstants.TCSRequestMethod.GET);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.GETNOTIFYACTIONS);

        /****************************/
        definition = new TCSAPICallDefinition("/api/1", "/android/gcm/registerGcmDevice", TCSAPIConstants.TCSRequestMethod.POST);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.REGISTERGCSDEVICE);

        /****************************/
        definition = new TCSAPICallDefinition("/api/1", "/app/getUID", TCSAPIConstants.TCSRequestMethod.GET);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.GETAPPUID);

        /****************************/
        definition = new TCSAPICallDefinition("/api/1", "/questionnaire/getlist", TCSAPIConstants.TCSRequestMethod.GET);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.GETQUESTIONS);

        /****************************/
        definition = new TCSAPICallDefinition("/api/1", "/questionnaire/answers", TCSAPIConstants.TCSRequestMethod.POST);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.SENDANSWERS);

        definition = new TCSAPICallDefinition("/api/1", "/schedules/", TCSAPIConstants.TCSRequestMethod.GET);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.GETSCHEDULES);

        definition = new TCSAPICallDefinition("/api/1", "/schedule/items", TCSAPIConstants.TCSRequestMethod.GET);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.GETSCHEDULEDITEMS);

        definition = new TCSAPICallDefinition("/api/1", "/schedule/item", TCSAPIConstants.TCSRequestMethod.GET);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.GETSCHEDULEITEM);

        definition = new TCSAPICallDefinition("/api/1", "/schedule/rsvp", TCSAPIConstants.TCSRequestMethod.POST);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.SENDRSVP);

        definition = new TCSAPICallDefinition("/api/1", "/templates", TCSAPIConstants.TCSRequestMethod.GET);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.GETTEMPLATES);

        definition = new TCSAPICallDefinition("/api/1", "/segmentation/load", TCSAPIConstants.TCSRequestMethod.GET);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.LOADSEGMENTATIONS);

        definition = new TCSAPICallDefinition("/api/1", "/enduser/profileOptions", TCSAPIConstants.TCSRequestMethod.GET);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.GETENDUSERPROFILEOPTIONS);

        definition = new TCSAPICallDefinition("/api/1", "/enduser/profileOptions", TCSAPIConstants.TCSRequestMethod.POST);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.UPDATEPROFILEOPTIONS);

        definition = new TCSAPICallDefinition("/api/1", "/accountablebeacon", TCSAPIConstants.TCSRequestMethod.GET);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.GETACCOUNTABLEBEACON);

        definition = new TCSAPICallDefinition("/api/1", "/auditoryoptions", TCSAPIConstants.TCSRequestMethod.GET);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.GETAUDITORYOPTIONS);

        definition = new TCSAPICallDefinition("/api/1", "/beacon/audit", TCSAPIConstants.TCSRequestMethod.POST);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.POSTAUDITORYOPTIONS);

        definition = new TCSAPICallDefinition("/api/1", "/beacons/history", TCSAPIConstants.TCSRequestMethod.GET);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.GETAUDITORYHISTORY);

        definition = new TCSAPICallDefinition("/api/1", "/app", TCSAPIConstants.TCSRequestMethod.POST);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.POSTADVERTISINGID);

        definition = new TCSAPICallDefinition("/api/1", "/principals", TCSAPIConstants.TCSRequestMethod.GET);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.GETPRINCIPALS);

        definition = new TCSAPICallDefinition("/api/1", "/principal", TCSAPIConstants.TCSRequestMethod.GET);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.GETPRINCIPALDETAILS);

        definition = new TCSAPICallDefinition("/api/1", "/principal/link", TCSAPIConstants.TCSRequestMethod.POST);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.LINKTOPRINCIPAL);

        definition = new TCSAPICallDefinition("/api/1", "/principal/unlink", TCSAPIConstants.TCSRequestMethod.POST);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.UNLINKFROMPRINCIPAL);

        definition = new TCSAPICallDefinition("/api/1", "/meds/pending", TCSAPIConstants.TCSRequestMethod.GET);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.GETTIMEFORMEDS);

        definition = new TCSAPICallDefinition("/api/1", "/meds/take", TCSAPIConstants.TCSRequestMethod.POST);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.POSTMEDSTAKEN);

        definition = new TCSAPICallDefinition("/api/1", "/notifyDoctor", TCSAPIConstants.TCSRequestMethod.POST);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.NOTIFYDOCTOR);

        definition = new TCSAPICallDefinition("/api/1", "/principals/linked", TCSAPIConstants.TCSRequestMethod.GET);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.GETLINKEDPRINCIPALS);

        definition = new TCSAPICallDefinition("/api/1", "/facilities", TCSAPIConstants.TCSRequestMethod.GET);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.GETFACILITIES);

        definition = new TCSAPICallDefinition("/api/1", "/flight", TCSAPIConstants.TCSRequestMethod.GET);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.GETSUBCRIBE);

        definition = new TCSAPICallDefinition("/api/1", "/concessions/categories", TCSAPIConstants.TCSRequestMethod.GET);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.GETCONCESSIONCATEGORIES);

        definition = new TCSAPICallDefinition("/api/1", "/concessions", TCSAPIConstants.TCSRequestMethod.GET);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.GETCONCESSIONLIST);

        /****************************/
        definition = new TCSAPICallDefinition("/api/1", "/terms", TCSAPIConstants.TCSRequestMethod.GET);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.GETTERMS);

        /****************************/
        definition = new TCSAPICallDefinition("/api/1", "/terms/accept", TCSAPIConstants.TCSRequestMethod.POST);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.ACCEPTTERMS);

        definition = new TCSAPICallDefinition("/api/1", "/enduser/profileImage", TCSAPIConstants.TCSRequestMethod.POST);
        pathDefinitions.AddAPICall(definition, TCSAPIConstants.TCSRequestType.PROFILEIMAGE);

    }

    public void extendAPI(TCSAPIExtender extender) {
        for (Map.Entry<String, TCSAPICallDefinition> entry : extender.getTCSAPICallDefinitions().entrySet()) {
            if(!pathDefinitions.AddAPICall(entry.getValue(), entry.getKey()))
                Log.d("APIConnector", "There was a problem adding ApiCallDefinition with key " + entry.getKey());
        }
    }

    public void MonitorReachability(Context context) {
        connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public boolean internetIsReachable() {
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public boolean serverIsReachable() //TODO - Is the server reachable
    {
        return true;
    }

    public boolean serverIsReachableByWifi() {
        return true; //TODO - is the server reachable by Wifi
    }

    public boolean serverIsReachableByWWAN() {
        return true; //TODO - is the server reachable by WWAN
    }

    /**
     * Trust every server - dont check for any certificate
     * TODO - Don't do this - only for development
     */
//    private static void trustAllHosts() throws Exception {
//        if(!BuildConfig.DEBUG)
//            throw new Exception("Don't use this in production!");
////        Create a trust manager that does not validate certificate chains
//        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
//            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                return new java.security.cert.X509Certificate[] {};
//            }
//
//            public void checkClientTrusted(X509Certificate[] chain,
//                                           String authType) throws CertificateException {
//            }
//
//            public void checkServerTrusted(X509Certificate[] chain,
//                                           String authType) throws CertificateException {
//            }
//        } };
//
//        // Install the all-trusting trust manager
//        try {
//            SSLContext sc = SSLContext.getInstance("TLS");
//            sc.init(null, trustAllCerts, new java.security.SecureRandom());
//            HttpsURLConnection
//                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    /**
     * Method to make an api call to the backend
     *
     * @param args - The arguments to pass to the request - see TCSRequestArgs class
     */
    public void requestWithArgs(TCSRequestArgs args) throws Exception {
        if (connMgr == null)
            throw new Exception("You need to call MonitorReachability() first");


        if (internetIsReachable()
                ) {
            String urlString = pathDefinitions.APIPathForCallType(args.requestType, appURL);

            if (args.additionalPath != null)
                urlString += args.additionalPath;

            TCSAPIConstants.TCSRequestMethod t = pathDefinitions.MethodForCallType(args.requestType);

            if (t == TCSAPIConstants.TCSRequestMethod.GET && args.getParams != null) {
//                String params = URLEncoder.encode(args.getParams.toString(), "utf-8");
                String params = args.getParams.toString();
                if (params != null) {
                    urlString += "?" + params;
                }
            }

//            if(BuildConfig.DEBUG)
//            {
//                //TODO - Get Mike to fix intermediate certificate for Android then remove this
//                try
//                {
//                    trustAllHosts();
//                }catch (Exception e){}
//            }

            URL url = new URL(urlString);
            HttpsURLConnection request = (HttpsURLConnection) url.openConnection();

//            if(BuildConfig.DEBUG)
//            {
//                //TODO - Get Mike to fix intermediate certificate for Android then remove this
//                request.setHostnameVerifier(new HostnameVerifier() {
//                    @Override
//                    public boolean verify(String s, SSLSession sslSession) {
//                        return true;
//                    }
//                });
//            }

            request.setUseCaches(false);
            request.setConnectTimeout(20000);
            request.setReadTimeout(20000);

            request.setRequestProperty("Accept", "application/json");







            request.setRequestProperty("X-PF-AppInstanceUID", TCSAppInstance.getInstance().getAppInstanceUID());

            String userAccessToken = TCSAppInstance.getInstance().getEndUserAccessToken();
            if (userAccessToken != null)
                request.setRequestProperty("X-PF-EndUserAccessToken", userAccessToken);

            Date now = new Date();
            String iso8601String = String.valueOf(android.text.format.DateFormat.format("yyyy-MM-dd'T'hh:mm:ssz", new Date()));

            request.setRequestProperty("X-PF-LocalTime", iso8601String);

            byte[] data = null;

            //switch (args.type) {
            switch (t) {
                case GET: {
                    request.setRequestMethod("GET");
                    break;
                }
                case PUT: {
                    request.setRequestMethod("PUT");
                    request.setDoOutput(true);
                    String json = args.payload.toString();
                    data = json.getBytes("UTF-8");

                    break;
                }

                case POST: {
                    request.setRequestMethod("POST");
                    request.setDoOutput(true);
                    String json = args.payload.toString();
                    data = json.getBytes("UTF-8");

                    break;
                }
                case METHOD_ERROR:
                    break;
                case DELETE:
                    break;
                default:
                    request.setRequestMethod("GET");
                    break;
            }
            //TODO - Earlier versions of Android seem not to recognise GoDaddy - need to check this.
            //new TCSURLConnection(data, args.callback).execute(request);
            if(args.requestType.equals(TCSAPIConstants.TCSRequestType.PROFILEIMAGE)){
               String KEYPROFILEIMAGE = "image";
               String fileUrl =  args.payload.getString(KEYPROFILEIMAGE);
                TCSURLConnection connection = new TCSURLConnection(data, args.callback);
                connection.AddArchive("image", fileUrl);
                connection.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
            }else {
                request.setRequestProperty("Content-Type", "application/json");
            new TCSURLConnection(data, args.callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
            }
        } else {
            Log.d("APIConnector", "No network connection available.");
        }
    }

    /*

    request.addRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundaryString);
                // Indicate that we want to write some data as the HTTP request body
                request.setDoOutput(true);

                OutputStream outputStreamToRequestBody = request.getOutputStream();
                BufferedWriter httpRequestBodyWriter =
                        new BufferedWriter(new OutputStreamWriter(outputStreamToRequestBody));

                // Include value from the myFileDescription text area in the post data
                httpRequestBodyWriter.write("\n\n--" + boundaryString + "\n");
                httpRequestBodyWriter.write("Content-Disposition: form-data; name=\"image\"");
                httpRequestBodyWriter.write("\n\n");
                httpRequestBodyWriter.write("Log file for 20150208");

                // Include the section to describe the file
                httpRequestBodyWriter.write("\n--" + boundaryString + "\n");
                httpRequestBodyWriter.write("Content-Disposition: form-data;"
                        + "name=\"myFile\";"
                        + "filename=\""+ logFileToUpload.getName() +"\""
                        + "\nContent-Type: text/plain\n\n");
                httpRequestBodyWriter.flush();

                // Write the actual file contents
                FileInputStream inputStreamToLogFile = new FileInputStream(logFileToUpload);

                int bytesRead;
                byte[] dataBuffer = new byte[1024];
                while((bytesRead = inputStreamToLogFile.read(dataBuffer)) != -1) {
                    outputStreamToRequestBody.write(dataBuffer, 0, bytesRead);
                }

                outputStreamToRequestBody.flush();

                // Mark the end of the multipart http request
                httpRequestBodyWriter.write("\n--" + boundaryString + "--\n");
                httpRequestBodyWriter.flush();

                // Close the streams
                outputStreamToRequestBody.close();
                httpRequestBodyWriter.close();

                BufferedReader httpResponseReader =
                        new BufferedReader(new InputStreamReader(request.getInputStream()));
                String lineRead;
                while((lineRead = httpResponseReader.readLine()) != null) {
                    Log.d("responce", lineRead);
                }

     */



}
