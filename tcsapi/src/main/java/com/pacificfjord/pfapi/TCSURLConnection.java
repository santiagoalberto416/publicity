package com.pacificfjord.pfapi;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
public class TCSURLConnection extends AsyncTask<HttpsURLConnection, String, String> {
    int response;
    IOException exception;

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
    private TCSURLConnectionDelegate callback;
    private InputStream is;
    private String keyNameArchive;
    private String urlPAth;

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

    public TCSURLConnection(byte[] payload, TCSURLConnectionDelegate callback)
    {
        this.payload = payload;
        this.callback = callback;

    }

    public void AddArchive(String keyNameArchive, String urlPath){
        this.keyNameArchive = keyNameArchive;
        this.urlPAth = urlPath;
    }

    @Override
    protected String doInBackground(HttpsURLConnection... httpsURLConnections) {
        InputStream is = null;
        HttpsURLConnection conn = httpsURLConnections[0];
        String result = null;
        if(keyNameArchive!=null){
            try {
                //if exist a archive
                String fileUrl = urlPAth;
                File logFileToUpload = new File(fileUrl);
                TCSAPIMultyPart multipart = new TCSAPIMultyPart("UTF-8", conn);
                multipart.addFilePart(keyNameArchive, logFileToUpload);
                multipart.addFormField(keyNameArchive, fileUrl);
                multipart.addHeaderField(keyNameArchive, fileUrl);
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

        }else {
            //this case if there no archives
            try {
                // Starts the query
                conn.connect();


                if (payload != null) {
                    OutputStream os = conn.getOutputStream();
                    os.write(payload);
                    os.close();
                }

                response = conn.getResponseCode();
                Log.d("PFUrlConnection", "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                result = convertStreamToString(is);

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } catch (java.io.IOException e) {
                exception = e;
                Log.d("PFUrlConnection", e.getMessage());
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result)
    {

        if(exception!=null) {
            if (result != null)
                Log.d("results", result);
            callback.done(this, -1, null, exception);
        }
        else
            callback.done(this,response,result,null);
    }
}
