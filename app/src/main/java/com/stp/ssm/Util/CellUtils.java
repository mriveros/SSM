package com.stp.ssm.Util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;

import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;
import com.stp.ssm.BuildConfig;
import com.stp.ssm.Model.Capturas;
import com.stp.ssm.Model.Operadora;
import com.stp.ssm.R;
import com.stp.ssm.SelectActivity;
import com.stp.ssm.SelectMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import android.provider.Settings.Secure;

import static android.Manifest.permission;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.ActivityManager.RunningServiceInfo;
import static android.app.AppOpsManager.MODE_ALLOWED;
import static android.app.AppOpsManager.OPSTR_MOCK_LOCATION;
import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.APP_OPS_SERVICE;
import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.TELEPHONY_SERVICE;
import static android.content.Intent.ACTION_BATTERY_CHANGED;
import static android.content.pm.PackageManager.GET_META_DATA;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;
import static android.media.RingtoneManager.TYPE_NOTIFICATION;
import static android.media.RingtoneManager.getDefaultUri;
import static android.media.RingtoneManager.getRingtone;
import static android.net.Uri.fromFile;
import static android.os.BatteryManager.EXTRA_LEVEL;
import static android.os.BatteryManager.EXTRA_SCALE;
import static android.os.Build.VERSION;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES;
import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR1;
import static android.os.Build.VERSION_CODES.M;
import static android.os.Environment.getExternalStorageDirectory;
import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;
import static android.provider.MediaStore.EXTRA_OUTPUT;
import static android.provider.Settings.Global;
import static android.provider.Settings.Global.AUTO_TIME;
import static android.provider.Settings.Global.DEVELOPMENT_SETTINGS_ENABLED;
import static android.provider.Settings.Secure.ALLOW_MOCK_LOCATION;
import static android.provider.Settings.Secure.ANDROID_ID;
import static android.provider.Settings.Secure.getInt;
import static android.provider.Settings.Secure.getString;
import static android.provider.Settings.System;
import static android.support.v4.app.ActivityCompat.requestPermissions;
import static android.support.v4.content.ContextCompat.checkSelfPermission;
import static android.support.v4.content.FileProvider.getUriForFile;
import static android.telephony.TelephonyManager.SIM_STATE_ABSENT;
import static android.telephony.TelephonyManager.SIM_STATE_UNKNOWN;
import static com.scanlibrary.ScanConstants.IMAGE_PATH;
import static com.scanlibrary.ScanConstants.OPEN_CAMERA;
import static com.scanlibrary.ScanConstants.OPEN_INTENT_PREFERENCE;
import static com.scanlibrary.ScanConstants.OPEN_MEDIA;
import static com.scanlibrary.ScanConstants.SCANNED_RESULT;
import static com.stp.ssm.BuildConfig.APPLICATION_ID;
import static com.stp.ssm.Model.Capturas.TIPO_ORIGEN;
import static com.stp.ssm.Model.Capturas.TIPO_ORIGEN.CAMARA;
import static com.stp.ssm.R.raw;
import static com.stp.ssm.R.raw.localidades;
import static com.stp.ssm.SelectActivity.EX_PATH;
import static com.stp.ssm.SelectActivity.EX_STYLE;
import static com.stp.ssm.SelectMode.SELECT_FILE;
import static com.stp.ssm.Util.FechaUtil.getFechaCadena2;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.System.currentTimeMillis;

public class CellUtils {


    private static final String PATH_GALLERY = "/DCIM/ssm_gallery/";
    private static final String IMG_EXT = ".jpg";
    private static final String PATH_DB = "/data/data/com.stp.ssm/databases/db_stp_ssm";

    public static boolean checkNetworkConnect(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        try {
            return activeNetwork.isConnectedOrConnecting();
        } catch (NullPointerException ex) {
            return false;
        }
    }

    public static boolean checkLocationGPS(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(GPS_PROVIDER);
    }

    public static boolean checkLocationNetwork(Context context) {
        if (deviceHasSimcard(context)) {
            LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            return locationManager.isProviderEnabled(NETWORK_PROVIDER);
        } else {
            return true;
        }
    }

    public static boolean checkMockLocation(Context context) {
        boolean isMockLocation = false;
        if (SDK_INT >= M) {
            PackageManager pm = context.getPackageManager();
            List<ApplicationInfo> packages = pm.getInstalledApplications(GET_META_DATA);
            AppOpsManager opsManager = (AppOpsManager) context.getSystemService(APP_OPS_SERVICE);
            for (ApplicationInfo applicationInfo : packages) {
                try {
                    isMockLocation = (opsManager.checkOp(OPSTR_MOCK_LOCATION, applicationInfo.uid, applicationInfo.packageName) == MODE_ALLOWED);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
                if (isMockLocation) {
                    break;
                }
            }
        } else {
            isMockLocation = !getString(context.getContentResolver(), ALLOW_MOCK_LOCATION).equals("0");
        }
        return isMockLocation;
    }

    public static boolean checkDeveloperOption(Context context) {
        int adb = getInt(context.getContentResolver(), DEVELOPMENT_SETTINGS_ENABLED, 0);
        return adb == 0;
    }

    public static float getPorcBaterria(Context context) {
        IntentFilter ifilter = new IntentFilter(ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(EXTRA_SCALE, -1);

        float batteryPct = level / (float) scale;
        return batteryPct;
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    public static Operadora getDataOperadora(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        if ((mTelephonyMgr.getSimState() == SIM_STATE_ABSENT) || (mTelephonyMgr.getSimState() == SIM_STATE_UNKNOWN)) {
            return new Operadora("", "", "", "", getAndroidId(context));

        } else {
            return new Operadora(mTelephonyMgr.getSimOperatorName(),
                    mTelephonyMgr.getSimSerialNumber(),
                    mTelephonyMgr.getSimCountryIso(),
                    mTelephonyMgr.getSimOperator(),
                    getAndroidId(context));
        }
    }

    @SuppressLint("MissingPermission")
    public static String getImeiCell(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        return mTelephonyMgr.getDeviceId();
    }

    public static void adjuntarFichero(Activity activity) {
        Intent i = new Intent(activity, SelectActivity.class);
        i.putExtra(EX_PATH, getExternalStorageDirectory().getAbsolutePath());
        i.putExtra(EX_STYLE, SELECT_FILE);
        activity.startActivityForResult(i, 100);
    }

    public static void takePhoto(Activity activity, ArrayList<String> img) {
        File folder = new File(getExternalStorageDirectory() + PATH_GALLERY);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File imagen = new File(folder, currentTimeMillis() + IMG_EXT);
        img.add(imagen.getAbsolutePath());
        Intent takePictureIntent = new Intent(ACTION_IMAGE_CAPTURE);
        if (SDK_INT >= 24) {
            Uri photoURI = getUriForFile(activity.getApplicationContext(), APPLICATION_ID + ".provider", imagen);
            takePictureIntent.putExtra(EXTRA_OUTPUT, photoURI);
        } else {
            takePictureIntent.putExtra(EXTRA_OUTPUT, fromFile(imagen));
        }
        activity.startActivityForResult(takePictureIntent, 200);
    }

    public static void takePhoto2(Activity activity, ArrayList<Capturas> img) {
        File folder = new File(getExternalStorageDirectory() + PATH_GALLERY);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File imagen = new File(folder, currentTimeMillis() + IMG_EXT);
        img.add(new Capturas(imagen.getAbsolutePath(), CAMARA.getCodigo()));
        Intent takePictureIntent = new Intent(ACTION_IMAGE_CAPTURE);
        if (SDK_INT >= 24) {
            Uri photoURI = getUriForFile(activity.getApplicationContext(), APPLICATION_ID + ".provider", imagen);
            takePictureIntent.putExtra(EXTRA_OUTPUT, photoURI);
        } else {
            takePictureIntent.putExtra(EXTRA_OUTPUT, fromFile(imagen));
        }
        activity.startActivityForResult(takePictureIntent, 200);
    }

    public static void takeScan(Activity activity) {
        int REQUEST_CODE = 700;
        Intent intent = new Intent(activity.getApplicationContext(), ScanActivity.class);
        intent.putExtra(OPEN_INTENT_PREFERENCE, OPEN_CAMERA);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    public static void scanDocumentAndroid(Activity activity, String pathimagen) {
        int REQUEST_CODE = 700;
        int preference = OPEN_MEDIA;
        Intent intent = new Intent(activity.getApplicationContext(), ScanActivity.class);
        intent.putExtra(SCANNED_RESULT, preference);
        intent.putExtra(IMAGE_PATH, pathimagen);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    public static void BorrarFileCapturas2(ArrayList<Capturas> capturas) {
        File file;
        for (Capturas captura : capturas) {
            file = new File(captura.getPath());
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public static boolean hasConnectionInternet(Context context) {
        /*if(isNetworkAvailable(context)){
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://clients3.google.com/generate_204").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 204 && urlc.getContentLength() == 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  false;
        }*/
        return isNetworkAvailable(context);
    }

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public static void copyDatabaseToFolder(String folder) {
        File dir = new File(folder);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File dbdata = new File(PATH_DB);
        File dbcopia = new File(folder, "stpcopia_" + getFechaCadena2());
        if (!dbcopia.exists()) {
            try {
                dbcopia.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            inChannel = new FileInputStream(dbdata).getChannel();
            outChannel = new FileOutputStream(dbcopia).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inChannel.close();
            outChannel.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copyDatabaseLocalidad(Context context) {
        File folder = new File("/data/data/com.stp.ssm/databases/");
        File destino = new File(folder, "localidades");
        try {
            FileOutputStream fileOutput = new FileOutputStream(destino);
            InputStream inputStream = context.getResources().openRawResource(localidades);
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
            }
            fileOutput.close();
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkAutomaticTime(Context context) {
        if (SDK_INT >= JELLY_BEAN_MR1) {
            return Global.getInt(context.getContentResolver(), AUTO_TIME, 0) == 1;
        } else {
            return System.getInt(context.getContentResolver(), System.AUTO_TIME, 0) == 1;
        }
    }


    public static String getAndroidId(Context context) {
        return getString(context.getContentResolver(), ANDROID_ID);
    }

    public static boolean validarPermisos(Activity activity) {
        if (checkSelfPermission(activity, READ_PHONE_STATE) != PERMISSION_GRANTED) {
            requestPermissions(activity, new String[]{READ_PHONE_STATE}, 100);
            return false;
        }

        if (checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            requestPermissions(activity, new String[]{WRITE_EXTERNAL_STORAGE}, 100);
            return false;
        }

        if (checkSelfPermission(activity, READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            requestPermissions(activity, new String[]{READ_EXTERNAL_STORAGE}, 100);
            return false;
        }

        if (checkSelfPermission(activity, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            requestPermissions(activity, new String[]{ACCESS_FINE_LOCATION}, 100);
            return false;
        }

        if (checkSelfPermission(activity, ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            requestPermissions(activity, new String[]{ACCESS_COARSE_LOCATION}, 100);
            return false;
        }

        if (checkSelfPermission(activity, CAMERA) != PERMISSION_GRANTED) {
            requestPermissions(activity, new String[]{CAMERA}, 100);
            return false;
        }
        return true;
    }

    public static boolean deviceHasSimcard(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        if (tm.getSimState() != SIM_STATE_ABSENT) {
            return true;
        }
        return false;
    }

    public static void playAlert(Context context) {
        Uri notification = getDefaultUri(TYPE_NOTIFICATION);
        Ringtone ringtone = getRingtone(context, notification);
        ringtone.play();
    }
}
