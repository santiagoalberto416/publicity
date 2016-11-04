package com.pacificfjord.pfapi.utilites;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by tom on 25/03/14.
 */
public class TCSTwitter {

    static Context context;
    private static SharedPreferences mSharedPreferences;
    private static Twitter twitter;
    private static RequestToken requestToken;
    private static AccessToken accessToken;
    static String TWITTER_CONSUMER_KEY = null;
    static String TWITTER_CONSUMER_SECRET = null;

    // Preference Constants
    static String PREFERENCE_NAME = "twitter_oauth";
    static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";

    static String TWITTER_CALLBACK_URL = null; //"oauth://t4jsample";

    // Twitter oauth urls
    static final String URL_TWITTER_AUTH = "auth_url";
    static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";
    private static String twitterMessage;

    public static void setupTwitter(Activity applicationActivity, String consumerKey,
                                    String consumerSecret, String callbackUrl) {
        TCSTwitter.context = applicationActivity;
        mSharedPreferences = context.getSharedPreferences("MyPref", 0);
        TCSTwitter.TWITTER_CONSUMER_KEY = consumerKey;
        TCSTwitter.TWITTER_CONSUMER_SECRET = consumerSecret;
        TCSTwitter.TWITTER_CALLBACK_URL = callbackUrl;
    }

    public static void oathCallback(Intent intent) {
        /** This if conditions is tested once is
         * redirected from twitter page. Parse the uri to get oAuth
         * Verifier
         * */
        if (!isLoggedIn()) {
            Uri uri = intent.getData();
            if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
                // oAuth verifier
                final String verifier = uri
                        .getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);

                try {

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                // Get the access token
                                TCSTwitter.accessToken =
                                        twitter.getOAuthAccessToken(requestToken, verifier);

                                // Shared Preferences
                                SharedPreferences.Editor e = mSharedPreferences.edit();

                                // After getting access token, access token secret
                                // store them in application preferences
                                e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
                                e.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
                                // Store login status - true
                                e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
                                e.commit(); // save changes


                                // Getting user details from twitter
                                // For now i am getting his name only
                                long userID = accessToken.getUserId();
                                User user = twitter.showUser(userID);
                                String username = user.getName();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    thread.start();


                    // Displaying in xml ui
                    // lblUserName.setText(Html.fromHtml("<b>Welcome " + username + "</b>"));
                } catch (Exception e) {
                    // Check log for login errors
                    Log.e("Twitter Login Error", "> " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Function to logout from twitter
     * It will just clear the application shared preferences
     */
    private static void logout() {
        // Clear the shared preferences
        SharedPreferences.Editor e = mSharedPreferences.edit();
        e.remove(PREF_KEY_OAUTH_TOKEN);
        e.remove(PREF_KEY_OAUTH_SECRET);
        e.remove(PREF_KEY_TWITTER_LOGIN);
        e.commit();
    }

    public static void login(final Activity activity) {
        // Check if already logged in
        if (!isLoggedIn()) {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
            builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
            Configuration configuration = builder.build();

            TwitterFactory factory = new TwitterFactory(configuration);
            twitter = factory.getInstance();


            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        requestToken = twitter
                                .getOAuthRequestToken(TWITTER_CALLBACK_URL);
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                .parse(requestToken.getAuthenticationURL())));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        } else {
            // user already logged into twitter
            //Toast.makeText(context,
            //        "Already Logged into twitter", Toast.LENGTH_LONG).show();
        }
    }

    public static boolean isLoggedIn() {
        // return twitter login status from Shared Preferences
        return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
    }

    public static void share(String message) {
        new updateTwitterStatus().execute(message);
        TCSTwitter.twitterMessage = null;
    }

    public static void getUserTimeline(String username, UserTimelineCallback callback) {
        new retrieveUserTimeline(username, callback).execute();
    }

    public static void setTwitterMessage(String twitterMessage) {
        TCSTwitter.twitterMessage = twitterMessage;
    }

    public static String getTwitterMessage() {
        return twitterMessage;
    }

    public static class retrieveUserTimeline extends AsyncTask<String, String, String> {

        UserTimelineCallback callback;
        String username;
        List<twitter4j.Status> statuses;

        private retrieveUserTimeline() {
        }

        public retrieveUserTimeline(String username, UserTimelineCallback callback) {
            this.callback = callback;
            this.username = username;
        }

        /**
         * getting Places JSON
         */
        protected String doInBackground(String... args) {
            ConfigurationBuilder cb = new ConfigurationBuilder();
            // Access Token
            String access_token = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
            // Access Token Secret
            String access_token_secret = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey(TWITTER_CONSUMER_KEY)
                    .setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET)
                    .setOAuthAccessToken(access_token)
                    .setOAuthAccessTokenSecret(access_token_secret);
            try {
                TwitterFactory factory = new TwitterFactory(cb.build());
                Twitter twitter = factory.getInstance();
                Paging paging = new Paging(1, 5);
                statuses = twitter.getUserTimeline(username, paging);
            } catch (TwitterException e) {

            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog and show
         * the data in UI Always use runOnUiThread(new Runnable()) to update UI
         * from background thread, otherwise you will get error
         * *
         */
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            //Toast.makeText(context,
            //        "User Timeline success", Toast.LENGTH_SHORT)
            //        .show();

            callback.done(statuses != null, statuses);
        }
    }

    public static class updateTwitterStatus extends AsyncTask<String, String, String> {
        ProgressDialog pDialog;

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Updating to twitter...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting Places JSON
         */
        protected String doInBackground(String... args) {
            Log.d("Tweet Text", "> " + args[0]);
            String status = args[0];
            try {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
                builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);

                // Access Token
                String access_token = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
                // Access Token Secret
                String access_token_secret =
                        mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");

                AccessToken accessToken = new AccessToken(access_token, access_token_secret);
                Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

                // Update status
                twitter4j.Status response = twitter.updateStatus(status);

                Log.d("Status", "> " + response.getText());
            } catch (TwitterException e) {
                // Error in updating status
                Log.d("Twitter Update Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog and show
         * the data in UI Always use runOnUiThread(new Runnable()) to update UI
         * from background thread, otherwise you will get error
         * *
         */
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();

            Toast.makeText(context,
                    "Status tweeted successfully", Toast.LENGTH_SHORT)
                    .show();

        }
    }

    public interface UserTimelineCallback {
        public void done(boolean success, List<Status> statuses);
    }
}
