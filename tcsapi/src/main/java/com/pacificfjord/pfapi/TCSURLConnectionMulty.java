package com.pacificfjord.pfapi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by tom on 16/01/14.
 */
public class TCSURLConnectionMulty extends AsyncTask<HttpsURLConnection, String, String> {
    int response;
    IOException exception;
    String urlPAth;

    private static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private byte[] payload;
    private TCSURLConnectionMulty callback;
    private InputStream is;

    private static SSLContext context;

    private static void generateSSLContext() throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException {
        // Load CAs from an InputStream
        // (could be from a resource or ByteArrayInputStream or ...)
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        // From https://www.washington.edu/itconnect/security/ca/load-der.crt
        InputStream caInput = new BufferedInputStream(new FileInputStream("load-der.crt"));
        Certificate ca;
        try {
            ca = cf.generateCertificate(caInput);
            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
        } finally {
            caInput.close();
        }

        // Create a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Create an SSLContext that uses our TrustManager
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, tmf.getTrustManagers(), null);
    }

    public TCSURLConnectionMulty(byte[] payload, TCSURLConnectionDelegate callback, String urlPath)
    {
        this.urlPAth = urlPath;
        this.payload = payload;
    }

    @Override
    protected String doInBackground(HttpsURLConnection... httpsURLConnections) {
        InputStream is = null;
        HttpsURLConnection conn = httpsURLConnections[0];
        String result = null;

            try {
                String fileUrl = urlPAth;
                File logFileToUpload = new File(fileUrl);
                    TCSAPIMultyPart multipart = new TCSAPIMultyPart("UTF-8", conn);
                    multipart.addFilePart("image", logFileToUpload);
                    multipart.addFormField("image", fileUrl);
                    multipart.addHeaderField("image", fileUrl);
                    List<String> response = multipart.finish();
                    for(String i : response){
                        result = result + i;
                    }
            this.response = conn.getResponseCode();
            Log.d("PFUrlConnection", "The response of send a picture: " + this.response);
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
            this.response = conn.getResponseCode();
            Log.d("PFUrlConnection", "The response is: " + response);

            // Convert the InputStream into a string
            }catch (IOException e){
                e.printStackTrace();
            }



        /*
            result = uploadFile(urlPAth, conn);
    */

        return result;
    }

    @Override
    protected void onPostExecute(String result)
    {
    }


}
