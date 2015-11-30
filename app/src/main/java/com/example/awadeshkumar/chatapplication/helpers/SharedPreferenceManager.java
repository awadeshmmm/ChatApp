package com.example.awadeshkumar.chatapplication.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

/**
 * Created by Awadesh Kumar on 11/29/2015.
 * To perform one time login in a device and storing data in  SharedPreferences
 */
public class SharedPreferenceManager {

    SharedPreferences mPrefs;

    public SharedPreferenceManager(Context context, String name) {
        mPrefs = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public void storeStringPreference(String preferenceName, String value) {
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString(preferenceName, value);
        prefsEditor.commit();
    }

    public void storeObjectPreference(String preferenceName, Object value) {
        Gson gson = new Gson();
        String json = gson.toJson(value);
        storeStringPreference(preferenceName, json);
    }

    public String getStringPreference(String preferenceName) {
        return mPrefs.getString(preferenceName, "");
    }

    public String getStringPreference(String preferenceName, String defaultValue) {
        return mPrefs.getString(preferenceName, defaultValue);
    }

    public void clearSharedPreference(String preferenceName) {
        mPrefs.edit().remove(preferenceName).commit();
    }
}
