package com.stp.ssm.Servicios;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import com.stp.ssm.Model.Capturas;
import com.stp.ssm.Util.ImageFileUtil;
import com.stp.ssm.databases.BDFuntions;
import java.util.ArrayList;
import java.util.List;

import static android.os.Environment.getExternalStorageDirectory;
import static android.util.Log.i;
import static com.stp.ssm.Util.ImageFileUtil.PATH_GALLERY;
import static com.stp.ssm.Util.ImageFileUtil.getListOfFile;

public class VerifcGelleryService extends IntentService {

    private BDFuntions dbFuntions;
    private ArrayList<String> arr_imagenes_capturas;

    public VerifcGelleryService() {
        super("VerifcGelleryService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        i("SERVICIO", "Inicializado");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        dbFuntions = new BDFuntions(getApplicationContext());
        validImagenes();
        if (!arr_imagenes_capturas.isEmpty()) {
            startService(new Intent(getApplicationContext(), NotificacionService.class));
        }
    }

    private void validImagenes() {
        List<String> arr_imagenes = getListOfFile(getExternalStorageDirectory() + PATH_GALLERY);
        arr_imagenes_capturas = new ArrayList<>();

        for (int i = 0; i < arr_imagenes.size(); i++) {
            Capturas capturas = dbFuntions.getCapturaByName(arr_imagenes.get(i));
            if (capturas == null) {
                arr_imagenes_capturas.add(arr_imagenes.get(i));
            } else if (capturas.getEstado() == 1 || (capturas.getEstado() == 2 && capturas.getSync() == 1)) {
                arr_imagenes_capturas.add(capturas.getPath());
            }
        }
    }
}
