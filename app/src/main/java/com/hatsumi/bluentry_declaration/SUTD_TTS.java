package com.hatsumi.bluentry_declaration;


import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SUTD_TTS {
    private static SUTD_TTS sutd_tts = null;

    public static SUTD_TTS getSutd_tts() {
        if (sutd_tts == null) {
            sutd_tts = new SUTD_TTS();
        }
        return sutd_tts;
    }

    String user_id, user_password;
    Map<String, String> cookies;

    private static String TAG = com.hatsumi.bluentry_declaration.SUTD_TTS.class.toString();

    public void setCredentials(String user_id, String user_password) {
        this.user_id = user_id;
        this.user_password = user_password;

    }

    public boolean attemptTemperatureDeclaration(String temperature) {
        try {


            Connection.Response response = Jsoup.connect("https://tts.sutd.edu.sg/tt_temperature_taking_user.aspx")
                    .cookies(this.cookies).sslSocketFactory(socketFactory()).method(Connection.Method.GET).execute();

            Log.d(TAG, response.body());

            Document temp_declaration_doc = response.parse();

            Elements form_inputs = temp_declaration_doc.select("#frm input");
            HashMap<String, String> formData = new HashMap<>();
            for (Element input : form_inputs) {
                Log.d(TAG, "temperature declaration form " + input.attr("name") + ": " + input.attr("value"));
                formData.put(input.attr("name"), input.attr("value"));
            }

            formData.put("ctl00$pgContent1$uiTemperature", "Less than or equal to 37.6°C");
            formData.put("ctl00$pgContent1$uiRemarks", temperature + "°C");

            for (Map.Entry<String, String> entry : cookies.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                Log.d(TAG, "Cookies " + key + " : " + value);
            }
            for (Map.Entry<String, String> entry : this.cookies.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                Log.d(TAG, "Cookies " + key + " : " + value);
            }
            Log.d(TAG, "Output");
            Log.d(TAG, formData.toString());

            response = Jsoup.connect("https://tts.sutd.edu.sg/tt_temperature_taking_user.aspx")
                    .data(formData)
                    .cookies(this.cookies)
                    .sslSocketFactory(socketFactory())
                    .method(Connection.Method.POST)
                    .execute();

            Log.d(TAG, response.body());
            return true;
        }
        catch (Exception e) {
            Log.d(TAG, "Error: Got unexpected error " + e.toString());
            return false;
        }
    }
    private SSLSocketFactory socketFactory() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("Failed to create a SSL socket factory", e);
        }
    }

    public String getCookieString() {
        String output = "";
        for (Map.Entry<String, String> entry: this.cookies.entrySet()) {
            output += entry.getKey() + "=" + entry.getValue() + "; ";
        }
        output += "path=/";

        return output;
    }

    public int completedTempDeclarationCount() {
        try {
            Connection.Response response = Jsoup.connect("https://tts.sutd.edu.sg/tt_temperature_taking_user.aspx")
                    .cookies(this.cookies).sslSocketFactory(socketFactory()).method(Connection.Method.GET).execute();

            Log.d(TAG, response.body());

            Document temp_history = response.parse();
            return temp_history.select("a[href^=\"tt_temperature_taking_user.aspx\"]").size();

        }
        catch (Exception e) {
            Log.d(TAG, "Encountered unexpected error " + e.toString());
            return 0;
        }


    }

    public boolean attemptFetchProfile() {
        return true;
    }

    public boolean attemptLogin() {
        try {

            Connection.Response response = Jsoup.connect("https://tts.sutd.edu.sg/tt_login.aspx?formmode=expire").sslSocketFactory(socketFactory()).execute();

            Document login_document = response.parse();

            Elements form_inputs = login_document.select("#frm input");
            HashMap<String, String> formData = new HashMap<>();
            for (Element input : form_inputs) {
                Log.d(TAG, input.attr("name") + ": " + input.attr("value"));
                formData.put(input.attr("name"), input.attr("value"));
            }
            formData.put("ctl00$pgContent1$uiLoginid", user_id);
            formData.put("ctl00$pgContent1$uiPassword", user_password);
            response = Jsoup.connect("https://tts.sutd.edu.sg/tt_login.aspx")
                    .data(formData)
                    .cookies(response.cookies())
                    .sslSocketFactory(socketFactory())
                    .method(Connection.Method.POST)
                    .execute();

            Document responseDocument = response.parse();
            Element passwordErrorElement = responseDocument.select("#pgContent1_valPassword").first();
            if (passwordErrorElement != null) {
                Log.d(TAG, "There is a password error message " + passwordErrorElement.val());
                return false;
            }
            else {
                // Update the cookies
                this.cookies = response.cookies();
                Log.d(TAG, "Login succeeded");
                Log.d(TAG, this.cookies.toString());
                return true;

            }
        }
        catch (Exception e) {
            Log.d(TAG, "Got exception");
            Log.d(TAG, e.toString());
            return false;
        }

    }
}

