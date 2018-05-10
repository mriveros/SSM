package com.stp.ssm.View;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.bumptech.glide.Glide;
import com.stp.ssm.CapturaFormularioActivity;
import com.stp.ssm.Model.Pregunta;
import com.stp.ssm.R;
import com.stp.ssm.Util.ImageFileUtil;
import com.stp.ssm.http.URLs;

import java.io.File;
import java.util.UUID;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Intent.CATEGORY_DEFAULT;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.bumptech.glide.Glide.with;
import static com.stp.ssm.CapturaFormularioActivity.ACTION_GETIMG_FORMS;
import static com.stp.ssm.R.drawable;
import static com.stp.ssm.R.drawable.broken;
import static com.stp.ssm.R.layout.custom_image_view;
import static com.stp.ssm.R.raw;
import static com.stp.ssm.R.raw.loading;
import static com.stp.ssm.Util.ImageFileUtil.getNameImagen;
import static com.stp.ssm.http.URLs.URL_IMAGENES;
import static java.lang.String.valueOf;
import static java.util.UUID.randomUUID;

public class CustomImagenView extends LinearLayout {

    private ImageButton btn_customImg;
    private Context context;
    private Pregunta pregunta;
    private MyReceiver myReceiver;
    private UUID id = randomUUID();


    public CustomImagenView(Context context, Pregunta pregunta) {
        super(context);
        this.context = context;
        this.pregunta = pregunta;
        inicializar();
    }

    public CustomImagenView(Context context, @Nullable AttributeSet attrs, Pregunta pregunta) {
        super(context, attrs);
        this.context = context;
        this.pregunta = pregunta;
        inicializar();
    }

    private void inicializar() {
        String infService = LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        li.inflate(custom_image_view, this, true);

        btn_customImg = (ImageButton) findViewById(R.id.btn_customImg);

        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GETIMG_FORMS);
        intentFilter.addCategory(CATEGORY_DEFAULT);
        getContext().registerReceiver(myReceiver, intentFilter);

        asignarEventos();
    }

    private void asignarEventos() {
        btn_customImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, CapturaFormularioActivity.class);
                intent.putExtra("pregunta", pregunta);
                intent.putExtra("uuid", valueOf(id));
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    public void setImagen(String pathImagen) {
        File file = new File(pathImagen);
        if (file.exists()) {
            with(context)
                    .load(file)
                    .thumbnail(0.1f)
                    .into(btn_customImg);
        } else {
            with(context)
                    .load(URL_IMAGENES + getNameImagen(pathImagen))
                    .centerCrop()
                    .placeholder(loading)
                    .thumbnail(0.1f)
                    .error(broken)
                    .into(btn_customImg);
        }

    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras().getString("uuid").equals(valueOf(id))) {
                File file = null;
                if (pregunta.getTxtrespuesta() != null && !pregunta.getTxtrespuesta().equals("")) {
                    file = new File(pregunta.getTxtrespuesta());
                    if (file.exists()) {
                        file.delete();
                    }
                }

                file = new File(intent.getExtras().getString("imagen"));
                pregunta.responder(intent.getExtras().getString("imagen"));
                with(context)
                        .load(file)
                        .fitCenter()
                        .centerCrop()
                        .into(btn_customImg);
            }
        }
    }
}
