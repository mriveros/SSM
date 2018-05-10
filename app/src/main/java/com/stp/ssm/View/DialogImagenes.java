package com.stp.ssm.View;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.stp.ssm.Adapters.GaleriaAdapter;;
import com.stp.ssm.R;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

import static android.support.v7.app.AlertDialog.Builder;
import static android.util.Log.i;
import static android.view.View.OnClickListener;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.id.btnGalleryAnt;
import static com.stp.ssm.R.id.btnGallerySgt;
import static com.stp.ssm.R.id.indicator;
import static com.stp.ssm.R.id.viewpager1;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.view_imagen;
import static com.stp.ssm.R.string;
import static com.stp.ssm.R.string.dialog_title_img;

@SuppressLint("ValidFragment")
public class DialogImagenes extends DialogFragment {

    private ArrayList<String> imagenes;

    public DialogImagenes(ArrayList<String> imagenes) {
        this.imagenes = imagenes;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(view_imagen, null);

        final ViewPager pager = (ViewPager) view.findViewById(viewpager1);
        final CircleIndicator indicator = (CircleIndicator) view.findViewById(R.id.indicator);
        final Button btnGalleryAnt = (Button) view.findViewById(R.id.btnGalleryAnt);
        final Button btnGallerySgt = (Button) view.findViewById(R.id.btnGallerySgt);
        i("Imagenes", imagenes.toString());

        final GaleriaAdapter galeria = new GaleriaAdapter(imagenes, getActivity().getApplicationContext());
        pager.setAdapter(galeria);
        indicator.setViewPager(pager);
        galeria.registerDataSetObserver(indicator.getDataSetObserver());

        btnGalleryAnt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pager.getCurrentItem() > 0) {
                    pager.setCurrentItem(pager.getCurrentItem() - 1);
                }
            }
        });
        btnGallerySgt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pager.getCurrentItem() < (imagenes.size() - 1)) {
                    pager.setCurrentItem(pager.getCurrentItem() + 1);
                }
            }
        });

        Builder builder = new Builder(getActivity());
        builder.setTitle(getResources().getString(dialog_title_img));
        builder.setView(view);
        return builder.create();
    }
}
