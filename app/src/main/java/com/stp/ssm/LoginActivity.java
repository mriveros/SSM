package com.stp.ssm;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.dd.processbutton.iml.ActionProcessButton;
import com.google.firebase.crash.FirebaseCrash;
import com.stp.ssm.Evt.LoginResult;
import com.stp.ssm.FirebaseCloudMsj.FirebsePushUtils;
import com.stp.ssm.Interfaces.OnClickDialogListener;
import com.stp.ssm.Interfaces.OnHoraServidorListener;
import com.stp.ssm.Model.HelpObject;
import com.stp.ssm.Model.HoraServidor;
import com.stp.ssm.Util.CellUtils;
import com.stp.ssm.Util.Encriptacion;
import com.stp.ssm.Util.FechaUtil;
import com.stp.ssm.http.HttpVolleyRequest;
import com.stp.ssm.View.DialogAcpView;
import com.stp.ssm.View.ViewFactory;
import com.stp.ssm.http.URLs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.provider.Settings.ACTION_DATE_SETTINGS;
import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;
import static android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
import static android.util.Log.i;
import static android.view.View.OnClickListener;
import static android.widget.CompoundButton.OnCheckedChangeListener;
import static com.dd.processbutton.iml.ActionProcessButton.Mode;
import static com.dd.processbutton.iml.ActionProcessButton.Mode.ENDLESS;
import static com.google.firebase.crash.FirebaseCrash.report;
import static com.stp.ssm.FirebaseCloudMsj.FirebsePushUtils.registarToken;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.activity_login;
import static com.stp.ssm.R.string;
import static com.stp.ssm.R.string.dialog_msg_hora_servidor;
import static com.stp.ssm.R.string.dialog_title_hora_servidor;
import static com.stp.ssm.R.string.help_edt_signin_button;
import static com.stp.ssm.R.string.help_edt_signin_password;
import static com.stp.ssm.R.string.help_edt_signin_usuario;
import static com.stp.ssm.R.string.help_edt_signin_ver;
import static com.stp.ssm.R.string.lbl_ajustar;
import static com.stp.ssm.R.string.lbl_err_sin_password;
import static com.stp.ssm.R.string.lbl_err_sin_usuario;
import static com.stp.ssm.R.string.lbl_salir;
import static com.stp.ssm.R.string.title_login;
import static com.stp.ssm.R.string.toast_msj_err_con_serv;
import static com.stp.ssm.R.string.toast_msj_err_sin_institucion;
import static com.stp.ssm.R.string.toast_msj_login_err_login_sinconexion;
import static com.stp.ssm.Util.CellUtils.hasConnectionInternet;
import static com.stp.ssm.Util.Encriptacion.getStringMessageDigest;
import static com.stp.ssm.Util.FechaUtil.getFechaActual;
import static com.stp.ssm.Util.FechaUtil.validFechaServidor;
import static com.stp.ssm.View.ViewFactory.notificacionToast;
import static com.stp.ssm.http.HttpVolleyRequest.getInstance;
import static com.stp.ssm.http.URLs.URL_FECHA_HORA_SERVIDOR;
import static com.stp.ssm.http.URLs.URL_LOGIN;

public class LoginActivity extends BaseActivity {

    private EditText edtUsuario;
    private EditText edtPassword;
    private CheckBox chVerPass;
    private ActionProcessButton btnSignIn;
    private ArrayList<HelpObject> arr_help_object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_login);
        setTitle(getString(title_login));

        inicializar();
        sessionData.enabledlocationMonitor();
        eventBus.register(this);

        edtUsuario = (EditText) findViewById(id.edtUsuario);
        edtPassword = (EditText) findViewById(id.edtPassword);
        chVerPass = (CheckBox) findViewById(id.chVerPass);
        btnSignIn = (ActionProcessButton) findViewById(id.btnSignIn);

        arr_help_object = new ArrayList<>();
        arr_help_object.add(new HelpObject(edtUsuario, getString(help_edt_signin_usuario), true));
        arr_help_object.add(new HelpObject(edtPassword, getString(help_edt_signin_password), true));
        arr_help_object.add(new HelpObject(chVerPass, getString(help_edt_signin_ver), true));
        arr_help_object.add(new HelpObject(btnSignIn, getString(help_edt_signin_button), true));

        asignarEventos();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mn_help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.act_help){
            iniciarHelp(arr_help_object);
        }
        return true;
    }*/

    private void asignarEventos() {
        chVerPass.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edtPassword.setInputType(TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    edtPassword.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        btnSignIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarInput()) {
                    if (hasConnectionInternet(getApplicationContext())) {
                        ButtonInProcess();
                        HttpVolleyRequest httpVolleyRequest = getInstance(getApplicationContext());
                        httpVolleyRequest.requestHoraServidor(URL_FECHA_HORA_SERVIDOR);
                        httpVolleyRequest.setOnHoraServidorListener(new OnHoraServidorListener() {
                            @Override
                            public void OnHoraServidor(HoraServidor horaServidor) {
                                if (horaServidor != null) {
                                    if (validFechaServidor(horaServidor)) {
                                        requetLogin();
                                    } else {
                                        report(new Exception("Fecha Incorrecto"));
                                        enableView();
                                        btnSignIn.setProgress(0);
                                        DialogAcpView dialogAcpView = new DialogAcpView(getString(dialog_title_hora_servidor),
                                                getString(dialog_msg_hora_servidor) + "\n" +
                                                        "Hora Servidor :" + horaServidor.getAnho() + "-" +
                                                        horaServidor.getMes() + "-" +
                                                        horaServidor.getDia() + " " +
                                                        horaServidor.getHora() + ":" +
                                                        horaServidor.getMinuto() + "\n" +
                                                        "Hora Dispositivo :" + getFechaActual(),
                                                getString(lbl_ajustar),
                                                getString(lbl_salir));
                                        dialogAcpView.setOnClickDialogListener(new OnClickDialogListener() {
                                            @Override
                                            public void OnPositiveClick(DialogInterface dialog, String tag) {
                                                startActivity(new Intent(ACTION_DATE_SETTINGS));
                                            }

                                            @Override
                                            public void OnNegativeClick(DialogInterface dialog, String tag) {
                                                finish();
                                            }
                                        });
                                        dialogAcpView.show(getSupportFragmentManager(), "");
                                    }
                                } else {
                                    report(new Exception("Error al conectar al servidor"));
                                    enableView();
                                    btnSignIn.setProgress(0);
                                    notificacionToast(getApplicationContext(), getString(toast_msj_err_con_serv));
                                }
                            }
                        });
                    } else {
                        if (sessionData.checkLastPassword(edtUsuario.getText().toString(), edtPassword.getText().toString())) {
                            sessionData.salvarUsuarioOffline(edtUsuario.getText().toString());
                            startActivity(new Intent(LoginActivity.this, MenuPrincipalAcitivity.class));
                            finish();
                        } else {
                            notificacionToast(getApplicationContext(), getString(toast_msj_login_err_login_sinconexion));
                        }
                    }
                }
            }
        });
    }

    private void requetLogin() {
        final Map<String, String> parametros = new HashMap<>();
        parametros.put("username", edtUsuario.getText().toString());
        parametros.put("password", getStringMessageDigest(edtPassword.getText().toString()));
        i("Parametros", parametros.toString());
        getInstance(getApplicationContext()).simplePostRequestLogin(URL_LOGIN, parametros);
    }


    private boolean validarInput() {
        if (edtUsuario.getText().toString().equals("")) {
            edtUsuario.setError(getString(lbl_err_sin_usuario));
            return false;
        }

        if (edtPassword.getText().toString().equals("")) {
            edtPassword.setError(getString(lbl_err_sin_password));
            return false;
        }
        return true;
    }

    public void ButtonInProcess() {
        btnSignIn.setMode(ENDLESS);
        btnSignIn.setProgress(1);
        disableView();
    }

    private void disableView() {
        edtUsuario.setEnabled(false);
        edtPassword.setEnabled(false);
        chVerPass.setEnabled(false);
    }

    private void enableView() {
        edtUsuario.setEnabled(true);
        edtPassword.setEnabled(true);
        chVerPass.setEnabled(true);
        edtUsuario.setText("");
        edtPassword.setText("");
    }


    public void errorLogin(final String error) {
        btnSignIn.setProgress(-1);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                notificacionToast(getApplicationContext(), error);
                enableView();
                btnSignIn.setProgress(0);
            }
        }, 500);
    }


    private void correctLogin(final LoginResult loginResult) {
        btnSignIn.setProgress(100);
        if (loginResult.getInstituciones().size() > 0) {
            registarToken(getApplicationContext(), loginResult.getUsuario());
            saltarActivitad(loginResult);
        } else {
            notificacionToast(getApplicationContext(), getString(toast_msj_err_sin_institucion));
        }
    }


    private void saltarActivitad(LoginResult loginResult) {
        sessionData.salvarUsuarioOnline(loginResult);
        sessionData.salvarCredenciales(loginResult.getUsuario(), edtPassword.getText().toString());
        sessionData.setNivel(loginResult.getNivel());
        dbFuntions.insertarInstituciones(loginResult.getInstituciones());
        dbFuntions.updateOperadoraUser(loginResult.getUsuario());
        startActivity(new Intent(LoginActivity.this, MenuPrincipalAcitivity.class));
        finish();
    }


    public void onEvent(LoginResult loginResult) {
        if (loginResult.isOK()) {
            correctLogin(loginResult);
        } else {
            errorLogin(loginResult.getErrorRazon());
        }
    }

    @Override
    public void onBackPressed() {
        sessionData.disablelocationMonitor();
        super.onBackPressed();
    }
}