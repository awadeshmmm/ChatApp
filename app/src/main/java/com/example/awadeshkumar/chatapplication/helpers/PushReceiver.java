package com.example.awadeshkumar.chatapplication.helpers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.example.awadeshkumar.chatapplication.UsersListActivity;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Awadesh Kumar on 11/24/2015.
 */

/**
 * BroadCastReceiver to receive push notifications
 */
public class PushReceiver extends ParsePushBroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        String message = extras != null ? extras.getString("com.parse.Data")
                : "";
        JSONObject jObject;
        try {
            jObject = new JSONObject(message);
            Toast toast = Toast.makeText(context, jObject.getString("alert")
                    + jObject.getString("title"), Toast.LENGTH_SHORT);
            toast.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);
        Intent newIntent = new Intent(context, UsersListActivity.class);
        context.startActivity(newIntent);
    }

    @Override
    public IBinder peekService(Context myContext, Intent service) {
        return super.peekService(myContext, service);
    }
}
