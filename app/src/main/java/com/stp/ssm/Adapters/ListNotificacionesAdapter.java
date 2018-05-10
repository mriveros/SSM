package com.stp.ssm.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stp.ssm.Model.Notificacion;
import com.stp.ssm.R;

import java.util.ArrayList;

import static android.view.LayoutInflater.from;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.item_notificacion;


public class ListNotificacionesAdapter extends RecyclerView.Adapter<ViewHolder> {

    private ArrayList<Notificacion> arrayList;

    public ListNotificacionesAdapter(ArrayList<Notificacion> arrayList) {
        this.arrayList = arrayList;
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = from(parent.getContext()).inflate(item_notificacion, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtTopico.setText(arrayList.get(position).getTopic());
        holder.txtFecha.setText(arrayList.get(position).getFecha());
        holder.txtMensaje.setText(arrayList.get(position).getMensaje());
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtTopico;
        public TextView txtFecha;
        public TextView txtMensaje;

        public ViewHolder(View itemView) {
            super(itemView);

            txtTopico = (TextView) itemView.findViewById(id.txtTopico);
            txtFecha = (TextView) itemView.findViewById(id.txtFecha);
            txtMensaje = (TextView) itemView.findViewById(id.txtMensaje);
        }
    }
}
