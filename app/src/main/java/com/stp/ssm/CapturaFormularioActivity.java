package com.stp.ssm;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.widget.LinearLayout;
import com.stp.ssm.Model.Pregunta;
import java.io.File;

import static android.content.Intent.CATEGORY_DEFAULT;
import static android.net.Uri.fromFile;
import static android.os.Build.VERSION;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Environment.getExternalStorageDirectory;
import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;
import static android.provider.MediaStore.EXTRA_OUTPUT;
import static android.support.v4.content.FileProvider.getUriForFile;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.widget.LinearLayout.LayoutParams;
import static com.stp.ssm.BuildConfig.APPLICATION_ID;
import static java.lang.System.currentTimeMillis;

public class CapturaFormularioActivity extends BaseActivity {

    private final String PATH_GALLERY = "/DCIM/ssm_gallery/";
    private final String IMG_EXT = ".jpg";
    private Pregunta pregunta;
    private String nom_imagen;
    public static final String ACTION_GETIMG_FORMS = "com.stp.ssm.GETIMGFORMS";
    private String uuid;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        LayoutParams params = new LayoutParams(MATCH_PARENT,
                MATCH_PARENT);
        pregunta = (Pregunta) getIntent().getExtras().getSerializable("pregunta");
        uuid = getIntent().getExtras().getString("uuid");
        linearLayout.setLayoutParams(params);
        setContentView(linearLayout);
        capturarImg();
    }

    private void capturarImg() {
        File folder = new File(getExternalStorageDirectory() + PATH_GALLERY);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File imagen = new File(folder, "FORMS_" + currentTimeMillis() + IMG_EXT);
        nom_imagen = imagen.getAbsolutePath();
        Intent takePictureIntent = new Intent(ACTION_IMAGE_CAPTURE);
        if (SDK_INT >= 24) {
            Uri photoURI = getUriForFile(getApplicationContext(), APPLICATION_ID + ".provider", imagen);
            takePictureIntent.putExtra(EXTRA_OUTPUT, photoURI);
        } else {
            takePictureIntent.putExtra(EXTRA_OUTPUT, fromFile(imagen));
        }
        startActivityForResult(takePictureIntent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            pregunta.responder(nom_imagen);
            Bundle bundle = new Bundle();
            bundle.putString("imagen", nom_imagen);
            bundle.putString("uuid", uuid);
            enviarBroadcast(bundle);
        }
        finish();
    }

    private void enviarBroadcast(Bundle bundle) {
        Intent intentResponse = new Intent();
        intentResponse.setAction(ACTION_GETIMG_FORMS);
        intentResponse.addCategory(CATEGORY_DEFAULT);
        intentResponse.putExtras(bundle);
        sendBroadcast(intentResponse);
    }
}