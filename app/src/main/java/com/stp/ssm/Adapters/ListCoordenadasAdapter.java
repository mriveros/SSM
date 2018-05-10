package com.stp.ssm.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.stp.ssm.Interfaces.OnDeleteListener;
import com.stp.ssm.Model.Coordenadas;
import com.stp.ssm.R;
import java.util.List;

import static android.view.LayoutInflater.from;
import static android.view.View.GONE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.custom_coordenadas_view;

public class ListCoordenadasAdapter extends RecyclerView.Adapter<ViewHolder> {

    private List<Coordenadas> arr_coordenadas;
    private OnDeleteListener onDeleteListener;
    private boolean enableDelete = true;

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }

    public ListCoordenadasAdapter(List<Coordenadas> arr_coordenadas) {
        this.arr_coordenadas = arr_coordenadas;
    }

    public ListCoordenadasAdapter(List<Coordenadas> arr_coordenadas, boolean enableDelete) {
        this.arr_coordenadas = arr_coordenadas;
        this.enableDelete = false;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = from(parent.getContext()).inflate(custom_coordenadas_view, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (enableDelete) {
            holder.btncustomdelete.setVisibility(VISIBLE);
        } else {
            holder.btncustomdelete.setVisibility(GONE);
        }
        holder.txt_coordenadas_lon.setText(arr_coordenadas.get(position).convertLongitud());
        holder.txt_coordenadas_lat.setText(arr_coordenadas.get(position).convertLatitud());
        holder.txt_coordenadas_alt.setText(Double.toString(arr_coordenadas.get(position).getAltitud()));
        holder.txt_coordenadas_prec.setText(Float.toString(arr_coordenadas.get(position).getPrecision()));
        holder.txt_coordenadas_prov.setText(arr_coordenadas.get(position).getProveedor());
        holder.btncustomdelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                arr_coordenadas.remove(arr_coordenadas.get(position));
                notifyData();
                onDeleteListener.OnDelete(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arr_coordenadas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_coordenadas_lon;
        public TextView txt_coordenadas_lat;
        public TextView txt_coordenadas_alt;
        public TextView txt_coordenadas_prec;
        public TextView txt_coordenadas_prov;
        public Button btnObtCoordenadas;
        public ImageButton btncustomdelete;

        public ViewHolder(View v) {
            super(v);
            txt_coordenadas_lon = (TextView) v.findViewById(id.txt_coordenadas_lon);
            txt_coordenadas_lat = (TextView) v.findViewById(id.txt_coordenadas_lat);
            txt_coordenadas_alt = (TextView) v.findViewById(id.txt_coordenadas_alt);
            txt_coordenadas_prec = (TextView) v.findViewById(id.txt_coordenadas_prec);
            txt_coordenadas_prov = (TextView) v.findViewById(id.txt_coordenadas_prov);
            btnObtCoordenadas = (Button) v.findViewById(id.btnObtCoordenadas);
            btncustomdelete = (ImageButton) v.findViewById(id.btncustomdelete);
            btnObtCoordenadas.setVisibility(GONE);
            btncustomdelete.setVisibility(VISIBLE);
        }
    }

    public void notifyData() {
        this.notifyDataSetChanged();
    }
}
