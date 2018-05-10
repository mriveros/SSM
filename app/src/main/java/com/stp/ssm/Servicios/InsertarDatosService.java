package com.stp.ssm.Servicios;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.stp.ssm.Evt.FinalizoInsert;
import com.stp.ssm.Util.ParseJson;
import com.stp.ssm.databases.BDFuntions;

import de.greenrobot.event.EventBus;

import static com.stp.ssm.Util.ParseJson.parseBeneficiariosBD;
import static com.stp.ssm.Util.ParseJson.parseProyectoDB;
import static de.greenrobot.event.EventBus.getDefault;


public class InsertarDatosService extends IntentService {

    private BDFuntions dbFuntions;
    private EventBus eventBus;


    public InsertarDatosService() {
        super("InsertarDatosService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        dbFuntions = new BDFuntions(getApplicationContext());
        eventBus = getDefault();
        Bundle bundle = intent.getExtras();
        String usuario = bundle.getString("usuario");

        if (!bundle.getString("jsonBeneficiario").equals("")) {
            parseBeneficiariosBD(bundle.getString("jsonBeneficiario"), usuario, dbFuntions);
        }
        if (!bundle.getString("jsonEncuesta").equals("")) {
            dbFuntions.limpiarProyectos();
            dbFuntions.desuscribirTopics();
            parseProyectoDB(bundle.getString("jsonEncuesta"), dbFuntions);
        }
        eventBus.post(new FinalizoInsert());
    }
}
