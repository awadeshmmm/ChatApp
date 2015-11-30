package com.example.awadeshkumar.chatapplication.helpers;

import android.app.Application;
import android.content.Intent;

import com.example.awadeshkumar.chatapplication.UsersListActivity;
import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by Awadesh Kumar on 11/22/2015.
 */
public class ChatApplication extends Application {
    public static final String YOUR_APPLICATION_ID = "LARQeMe9rHmzjuVlmFs37oEqyst9oEkv5tPhrTtG";
    public static final String YOUR_CLIENT_KEY = "T1CPvdSt2ECr6fU7nsQ21dCuASJHZArS1rgKxgDE";

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, YOUR_APPLICATION_ID, YOUR_CLIENT_KEY);
        ParseInstallation.getCurrentInstallation().saveInBackground();

    }
}
