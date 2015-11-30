package com.example.awadeshkumar.chatapplication.Utilities;

/**
 * Created by Awadesh Kumar on 11/25/2015.
 */

import android.content.Context;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

public class UncaughtExceptionHandler implements
        java.lang.Thread.UncaughtExceptionHandler {

    Context myContext;

    public UncaughtExceptionHandler(Context context) {
        myContext = context;
    }

    public void uncaughtException(Thread thread, Throwable exception) {
        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        Log.e("UnCaughtExceptionHandle", sw.toString());
        Logger.appendErrorLog(sw.toString(), "");
        // myContext.startActivity(new Intent(myContext, LeadsActivity.class));
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }
}