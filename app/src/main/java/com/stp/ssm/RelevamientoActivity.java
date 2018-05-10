package com.stp.ssm;
//Created by desarrollo on 24/06/16.
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import com.stp.ssm.Evt.FinalizoFormEvt;
import com.stp.ssm.Interfaces.OnClickDialogListener;
import com.stp.ssm.Model.Coordenadas;
import com.stp.ssm.Model.Motivos;
import com.stp.ssm.Model.Visita;
import com.stp.ssm.Servicios.SendDataService2;
import com.stp.ssm.Util.CellUtils;
import com.stp.ssm.Util.FechaUtil;
import com.stp.ssm.View.DialogAcpView;

import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;
import static com.stp.ssm.Model.Visita.TipoVisita;
import static com.stp.ssm.Model.Visita.TipoVisita.VISITA_SIN_BENEFICIARIO;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.activity_fragment;
import static com.stp.ssm.R.string;
import static com.stp.ssm.R.string.dialog_msj_alert_confir_out;
import static com.stp.ssm.R.string.dialog_title_alert_confir;
import static com.stp.ssm.R.string.title_formulario;
import static com.stp.ssm.Util.CellUtils.hasConnectionInternet;
import static com.stp.ssm.Util.CellUtils.isMyServiceRunning;
import static com.stp.ssm.Util.FechaUtil.getFechaActual;

public class RelevamientoActivity extends BaseActivity {

    private Motivos motivos;
    private Long idvisita;
    private Bundle bundle;
    private LocationManager locationManager;
    private FragmentTransaction transaction;
    private long segundos = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_fragment);
        setTitle(title_formulario);

        inicializar();
        eventBus.register(this);
        bundle = getIntent().getExtras();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        transaction = getSupportFragmentManager().beginTransaction();

        if (savedInstanceState != null) {
            motivos = (Motivos) savedInstanceState.getSerializable("motivos");
            idvisita = savedInstanceState.getLong("idvisita");
            long segundos = savedInstanceState.getLong("segundos");
            /*formularioFragment =(FormularioFragment) getSupportFragmentManager().getFragment(savedInstanceState, "formularioFragment");
            formularioFragment.setSegundos(savedInstanceState.getLong("segundos"));
            formularioFragment.setMotivos(motivos);
            formularioFragment.setIdvisita(idvisita);*/

            /*formularioFragment = FormularioFragment.newInstance(motivos,idvisita,segundos,"",sessionData);

            transaction.replace(R.id.fragment_container, formularioFragment);
            transaction.addToBackStack(null);*/

        } else {

            motivos = (Motivos) bundle.getSerializable("motivo");
            idvisita = crearVisita();
            /*formularioFragment = FormularioFragment.newInstance(motivos,idvisita,0,"",sessionData);
            transaction.add(R.id.fragment_container, formularioFragment);*/
        }
        transaction.commit();
    }

    public void onEvent(FinalizoFormEvt evt) {
        Coordenadas coordenadas = capturarCoodenadas();
        if (validarDatos(coordenadas)) {
            guardar(coordenadas);
        }
    }


    private void guardar(Coordenadas coordenadas) {
        /*visita.setBeneficiario(null);
        visita.setHorafin(FechaUtil.getFechaActual());
        visita.setCodmotivo(motivos.getCodmotivo());
        visita.setCoordenadas(coordenadas);
        visita.setProyecto(proyecto);
        visita.setObservacion("");
        visita.setTiempo(0);
        visita.setHasFormulario();
        dbFuntions.addVisita(visita,sessionData.getUsuario());*/
        dbFuntions.addCoordenadasVisita(idvisita, coordenadas);
        // formularioFragment.finalizar();
        finalizar();
    }

    private long crearVisita() {
        Visita visita = new Visita(getFechaActual(), VISITA_SIN_BENEFICIARIO);
        visita.setBeneficiario(null);
        visita.setCodmotivo(motivos.getCodmotivo());
        visita.setCoordenadas(null);
        visita.setProyecto(motivos.getProyecto());
        visita.setObservacion("");
        visita.setTiempo(0);
        visita.setHasFormulario();
        // return dbFuntions.addVisita(visita,sessionData.getUsuario());
        return 0;
    }

    private Coordenadas capturarCoodenadas() {
        Location locationgps;
        Location locationnetwork;
        try {
            locationgps = locationManager.getLastKnownLocation(GPS_PROVIDER);
            locationnetwork = locationManager.getLastKnownLocation(NETWORK_PROVIDER);
            if (locationgps != null && locationnetwork != null) {
                if (locationgps.getAccuracy() <= locationnetwork.getAccuracy()) {
                    return generateCoordenadas(locationgps);
                } else {
                    return generateCoordenadas(locationnetwork);
                }
            } else if (locationgps != null) {
                return generateCoordenadas(locationgps);
            } else if (locationnetwork != null) {
                return generateCoordenadas(locationnetwork);
            } else {

            }
        } catch (SecurityException e) {
        }
        return null;
    }


    private Coordenadas generateCoordenadas(Location location) {
        return new Coordenadas(Double.toString(location.getLongitude()), Double.toString(location.getLatitude()), location.getAccuracy());
    }


    private boolean validarDatos(Coordenadas coordenadas) {
        if (coordenadas == null || coordenadas.getPrecision() > 30) {
            startActivityForResult(new Intent(getApplicationContext(), CalibrarLocationActivity.class), 300);
            return false;
        }
        return true;
    }


    /*@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("motivos",formularioFragment.getMotivos());
        outState.putString("beneficiario",formularioFragment.getDocumento());
        outState.putLong("segundos",formularioFragment.getSegundos());
        outState.putLong("idvisita",idvisita);
        getSupportFragmentManager().putFragment(outState, "formularioFragment", formularioFragment);
    }*/

    @Override
    public void onBackPressed() {
        DialogAcpView dialog = new DialogAcpView(getString(dialog_title_alert_confir),
                getString(dialog_msj_alert_confir_out));
        dialog.setOnClickDialogListener(new OnClickDialogListener() {
            @Override
            public void OnPositiveClick(DialogInterface dialog, String tag) {

                //formularioFragment.limpiarBase();
               /* dbFuntions.borrarVisitaById(idvisita);
                dbFuntions.borrarAdjuntosByVisita(idvisita);
                dbFuntions.borrarCapturasByVisita(idvisita);
                dbFuntions.borrarAdjuntosByVisita(0);
                dbFuntions.borrarCapturasByVisita(0);
                sessionData.setLastIdVisita(0);
                sessionData.setLastTimeVisita(0);*/
                dialog.dismiss();
                finish();
            }

            @Override
            public void OnNegativeClick(DialogInterface dialog, String tag) {
                dialog.dismiss();
            }
        });
        dialog.show(getSupportFragmentManager(), "");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*switch (requestCode){
            case 100:
                if(resultCode==RESULT_OK) {
                    formularioFragment.addAdjunto(data.getStringExtra(SelectActivity.EX_PATH_RESULT));
                }
                formularioFragment.contadorAdjuntos();
            break;
            case 200:
                if(resultCode == RESULT_CANCELED){
                    formularioFragment.removerCaptura();
                }
                formularioFragment.contadorCapturas();
            break;
            case 300:
                if(resultCode==RESULT_OK){
                    Bundle bundle = data.getExtras();
                    Coordenadas coordenadas = new Coordenadas(Double.toString(bundle.getDouble("longitud")),
                                                              Double.toString(bundle.getDouble("latitud")),
                                                              bundle.getFloat("presicion"));
                    guardar(coordenadas);
                }
            break;
            case 500:
                if(resultCode == RESULT_OK){
                    String path = formularioFragment.getCapturas().get(formularioFragment.getCapturas().size()-1);
                    CellUtils.scanDocumentAndroid(this,path);
                }else{
                    formularioFragment.getCapturas().remove(formularioFragment.getCapturas().size()-1);
                }
            break;
            case 700:
                if(resultCode == RESULT_OK){
                    Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        getContentResolver().delete(uri, null, null);
                        String path = ImageFileUtil.bitmapToFile(bitmap,formularioFragment.getDocumento(),formularioFragment.getCapturas().size());
                        formularioFragment.getCapturas().remove(formularioFragment.getCapturas().size()-1);
                        formularioFragment.addAdjunto(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }*/
    }


    private void finalizar() {
        sessionData.setLastIdVisita(0);
        sessionData.setLastTimeVisita(0);
        enviarDatos();
        finish();
    }

    private void enviarDatos() {
        if (hasConnectionInternet(getApplicationContext()) && !isMyServiceRunning(SendDataService2.class, getApplicationContext())) {
            startService(new Intent(getApplicationContext(), SendDataService2.class));
        }
    }
}