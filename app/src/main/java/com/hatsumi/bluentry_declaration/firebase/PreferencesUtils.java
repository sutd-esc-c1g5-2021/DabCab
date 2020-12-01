package com.hatsumi.bluentry_declaration.firebase;


import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesUtils {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String SHARED_PREF_NAME = "session";
    String PREF_KEY = "username";

    public PreferencesUtils(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveSession(String username){
        editor.putString(PREF_KEY, username);
        editor.apply();
    }

    public String getSession(){
        return sharedPreferences.getString(PREF_KEY, null);
    }

    public void removeSession(){
        editor.putString(PREF_KEY,"Invalid");
        editor.apply();
    }

}
