package com.example.awadeshkumar.chatapplication.Utilities;

/**
 * Created by Awadesh Kumar on 11/25/2015.
 */
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class Logger {
    static File logFile;
    static String currentDateTimeString = DateFormat.getDateTimeInstance()
            .format(new Date());

    private static boolean createLogFile() {
        boolean retVal = true;
        if (!Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            Log.e("Log file", "SD Card not mounted.");
            retVal = false;
        } else {
            logFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/ChatApplication.txt");
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    Log.e("Log file", "Error while creating log file");
                    e.printStackTrace();
                    retVal = false;
                }
            }
        }
        return retVal;
    }

    public static void appendErrorLog(String text, String tag) {
        try {
            boolean createLogFileStatus = createLogFile();
            if (createLogFileStatus) {
                // BufferedWriter for performance, true to set append to file
                // flag
                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile,
                        true));

                buf.append("At " + currentDateTimeString + ": Error in " + tag + " : " + text);
                buf.newLine();
                buf.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void appendInfoLog(String text, String tag) {
        try {
            boolean createLogFileStatus = createLogFile();
            if (createLogFileStatus) {
                // BufferedWriter for performance, true to set append to file
                // flag
                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile,
                        true));

                buf.append("At " + currentDateTimeString + ": Info " + tag + " : " + text);
                buf.newLine();
                buf.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


}
