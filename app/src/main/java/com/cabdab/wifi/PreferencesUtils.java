package com.cabdab.wifi;


import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesUtils {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String SHARED_PREF_NAME = "session";
    String PREF_KEY_USERNAME = "username";
    String PREF_KEY_PASSWORD = "password";

    public PreferencesUtils(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveSession(String username, String password){
        editor.putString(PREF_KEY_USERNAME, username);
        editor.putString(PREF_KEY_PASSWORD, password);
        editor.apply();
    }

    public String getUsername(){
        return sharedPreferences.getString(PREF_KEY_USERNAME, "Invalid");
    }

    public String getPassword() {
        return sharedPreferences.getString(PREF_KEY_PASSWORD, "Invalid");
    }

    public void removeSession(){
        editor.putString(PREF_KEY_USERNAME,"Invalid");
        editor.putString(PREF_KEY_PASSWORD, "Invalid");
        editor.apply();
    }

}
