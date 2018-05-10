package com.stp.ssm;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.support.multidex.MultiDex;
import com.stp.ssm.Util.FechaUtil;
import java.io.File;
import java.io.IOException;

import static android.os.Environment.getExternalStorageDirectory;
import static android.support.multidex.MultiDex.install;
import static com.stp.ssm.Util.FechaUtil.getFechaActual;
import static java.lang.Runtime.getRuntime;

public class AplicacionSSM extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //escribirLog();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        install(this);
    }

    private void escribirLog() {

        File appDirectory = new File(getExternalStorageDirectory() + "/ssm");
        File logDirectory = new File(appDirectory + "/log");
        File logFile = new File(logDirectory, "logcat" + getFechaActual() + ".txt");

        if (!appDirectory.exists()) {
            appDirectory.mkdir();
        }

        if (!logDirectory.exists()) {
            logDirectory.mkdir();
        }

        try {
            Process process = getRuntime().exec("logcat -c");
            process = getRuntime().exec("logcat -f " + logFile + " com.stp.ssm:E");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
