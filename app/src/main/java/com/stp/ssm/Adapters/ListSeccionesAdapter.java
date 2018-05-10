package com.stp.ssm.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.stp.ssm.Model.Secciones;
import com.stp.ssm.R;

import java.util.ArrayList;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.GREEN;
import static android.view.LayoutInflater.from;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.id.lbl_item_seccion;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.item_secciones_1;

public class ListSeccionesAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<Secciones> secciones;


    public ListSeccionesAdapter(Context context, ArrayList<Secciones> secciones) {
        this.secciones = secciones;
        this.mInflater = from(context);
    }


    @Override
    public int getCount() {
        return secciones.size();
    }


    @Override
    public Object getItem(int position) {
        return secciones.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            convertView = mInflater.inflate(item_secciones_1, parent, false);
            vh = new ViewHolder();

            vh.lblSeccion = (TextView) convertView.findViewById(lbl_item_seccion);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.lblSeccion.setText(secciones.get(position).toString());
        if (secciones.get(position).isHasResponse()) {
            vh.lblSeccion.setTextColor(GREEN);
        } else {
            vh.lblSeccion.setTextColor(BLACK);
        }
        return convertView;
    }

    class ViewHolder {
        TextView lblSeccion;
    }
}
