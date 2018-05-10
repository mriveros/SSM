package com.stp.ssm;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.stp.ssm.AsyncTask.InsertDptDistritos;
import com.stp.ssm.Interfaces.OnClickDialogListener;
import com.stp.ssm.Util.CellUtils;
import com.stp.ssm.Util.SessionData;
import com.stp.ssm.View.DialogAcpView;
import com.stp.ssm.databases.BDFuntions;

import static android.content.Intent.CATEGORY_DEFAULT;
import static android.content.Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NO_HISTORY;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.net.Uri.parse;
import static android.os.Build.VERSION;
import static android.os.Build.VERSION.SDK_INT;
import static android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS;
import static android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS;
import static android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.activity_main;
import static com.stp.ssm.R.string;
import static com.stp.ssm.R.string.dialog_msj_err_geo;
import static com.stp.ssm.R.string.dialog_msj_err_mock_location;
import static com.stp.ssm.R.string.dialog_msj_permisos;
import static com.stp.ssm.R.string.dialog_title_err_geo;
import static com.stp.ssm.R.string.dialog_title_err_mock_location;
import static com.stp.ssm.R.string.dialog_title_permisos;
import static com.stp.ssm.Util.CellUtils.checkLocationGPS;
import static com.stp.ssm.Util.CellUtils.checkLocationNetwork;
import static com.stp.ssm.Util.CellUtils.checkMockLocation;
import static com.stp.ssm.Util.CellUtils.copyDatabaseLocalidad;
import static com.stp.ssm.Util.CellUtils.deviceHasSimcard;
import static com.stp.ssm.Util.CellUtils.getDataOperadora;
import static com.stp.ssm.Util.SessionData.getInstance;
import static com.stp.ssm.View.DialogAcpView.SIMPLE;


public class MainActivity extends AppCompatActivity {

    private final int TIME_SPLASH = 1000;
    private final int TIME_SLEEP_RETURN = 100;
    private SessionData sessionData;
    private BDFuntions dbFuntions;
    private Handler handler = new Handler();
    private DialogAcpView dialogLocation;
    private DialogAcpView dialogMockLoc;
    private DialogAcpView dialogPermisos;
    private boolean checkedPermission = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);

        //FirebaseInstanceId.getInstance().getToken();
        if (SDK_INT >= 23) {
            validarPermisos();
        } else {
            inicializar();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkedPermission) {
            checkedPermission = false;
            validarPermisos();
        }
    }

    private void validarPermisos() {
        if (CellUtils.validarPermisos(this)) {
            inicializar();
        }
    }

    private void inicializar() {
        sessionData = getInstance(getApplicationContext());
        dbFuntions = new BDFuntions(getApplicationContext());
        sessionData.checkUpdate();
        if (deviceHasSimcard(getApplicationContext())) {
            if (dbFuntions.addTelefonia(getDataOperadora(getApplicationContext()), sessionData.getUsuario()) > 0) {
                sessionData.nuevoSim(true);
            }
        }

        if (sessionData.isfirsRunning()) {
            new InsertDptDistritos(dbFuntions, getApplicationContext()).execute();
            copyDatabaseLocalidad(getApplicationContext());
            sessionData.changeIsFisrtRun();
        }

        handler.postDelayed(new Runnable() {
            public void run() {
                saltarActivity();
            }
        }, TIME_SPLASH);
    }

    private void saltarActivity() {
        if (checkLocationGPS(getApplicationContext()) && checkLocationNetwork(getApplicationContext())) {
            if (!checkMockLocation(getApplicationContext())) {
                if (sessionData.isSessionStart()) {
                    startActivity(new Intent(MainActivity.this, MenuPrincipalAcitivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                finish();
            } else {
                if (dialogMockLoc == null) {
                    dialogMockLoc = new DialogAcpView(getString(dialog_title_err_mock_location),
                            getString(dialog_msj_err_mock_location));
                    dialogMockLoc.setOnClickDialogListener(new OnClickDialogListener() {
                        @Override
                        public void OnPositiveClick(DialogInterface dialog, String tag) {
                            Intent intent = new Intent(ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                            startActivityForResult(intent, 100);
                        }

                        @Override
                        public void OnNegativeClick(DialogInterface dialog, String tag) {
                            dialog.dismiss();
                            finish();
                        }
                    });
                }
                dialogMockLoc.show(getSupportFragmentManager(), "");
            }

        } else {
            if (dialogLocation == null) {
                dialogLocation = new DialogAcpView(getString(dialog_title_err_geo),
                        getString(dialog_msj_err_geo));

                dialogLocation.setOnClickDialogListener(new OnClickDialogListener() {
                    @Override
                    public void OnPositiveClick(DialogInterface dialog, String tag) {
                        Intent intent = new Intent(ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, 100);
                    }

                    @Override
                    public void OnNegativeClick(DialogInterface dialog, String tag) {
                        dialog.dismiss();
                        finish();
                    }
                });
            }
            dialogLocation.show(getSupportFragmentManager(), "");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 100:
                handler.postDelayed(new Runnable() {
                    public void run() {
                        saltarActivity();
                    }
                }, TIME_SLEEP_RETURN);
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] != PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                validarPermisos();
            } else {
                handler.postDelayed(new Runnable() {
                    public void run() {
                        alertaPermisos();
                    }
                }, TIME_SLEEP_RETURN);
            }
        } else {
            validarPermisos();
        }
    }

    private void alertaPermisos() {
        dialogPermisos = new DialogAcpView(getString(dialog_title_permisos), getString(dialog_msj_permisos), SIMPLE);
        dialogPermisos.setOnClickDialogListener(new OnClickDialogListener() {
            @Override
            public void OnPositiveClick(DialogInterface dialog, String tag) {
                Intent i = new Intent();
                i.setAction(ACTION_APPLICATION_DETAILS_SETTINGS);
                i.addCategory(CATEGORY_DEFAULT);
                i.setData(parse("package:" + getPackageName()));
                i.addFlags(FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(FLAG_ACTIVITY_NO_HISTORY);
                i.addFlags(FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                checkedPermission = true;
                startActivity(i);
            }

            @Override
            public void OnNegativeClick(DialogInterface dialog, String tag) {
            }
        });
        dialogPermisos.show(getSupportFragmentManager(), "");
    }
}
