package com.stp.ssm.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.stp.ssm.Model.Evento;
import com.stp.ssm.R;
import com.stp.ssm.View.DialogImagenes;

import java.util.ArrayList;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;
import static android.view.LayoutInflater.from;
import static android.view.View.OnClickListener;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.id.btnCapturas;
import static com.stp.ssm.R.id.lbl_hist_descrip;
import static com.stp.ssm.R.id.lbl_hist_fecha;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.item_history;

public class ListHistoryAdapter extends RecyclerView.Adapter<ViewHolder> {

    private ArrayList<Evento> arrayList;
    private FragmentManager fragmentManager;
    private Context context;


    public ListHistoryAdapter(ArrayList<Evento> arrayList, FragmentManager fragmentManager, Context context) {
        this.arrayList = arrayList;
        this.fragmentManager = fragmentManager;
        this.context = context;
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = from(parent.getContext()).inflate(item_history, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.lblfecha.setText(arrayList.get(position).getFecha());
        holder.lbldescripcion.setText(arrayList.get(position).getDescripcion());
        if (arrayList.get(position).getEstado() == 0) {
            holder.lblEstadoEvt.setTextColor(RED);
            holder.lblEstadoEvt.setText("PENDIENTE");
        } else {
            holder.lblEstadoEvt.setTextColor(GREEN);
            holder.lblEstadoEvt.setText("ENVIADO");
        }
        holder.btnVerCapturas.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogImagenes(arrayList.get(position).getCapturas()).show(fragmentManager, "");
            }
        });
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView lblfecha;
        public TextView lbldescripcion;
        public TextView lblEstadoEvt;
        public Button btnVerCapturas;

        public ViewHolder(View v) {
            super(v);
            lblfecha = (TextView) v.findViewById(lbl_hist_fecha);
            lbldescripcion = (TextView) v.findViewById(lbl_hist_descrip);
            lblEstadoEvt = (TextView) v.findViewById(id.lblEstadoEvt);
            btnVerCapturas = (Button) v.findViewById(btnCapturas);
        }
    }

    public void notifyData() {
        this.notifyDataSetChanged();
    }
}
