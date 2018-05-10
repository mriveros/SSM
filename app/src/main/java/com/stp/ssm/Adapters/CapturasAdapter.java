package com.stp.ssm.Adapters;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.stp.ssm.Model.Capturas;
import com.stp.ssm.R;
import com.stp.ssm.Util.ImageFileUtil;
import com.stp.ssm.Util.SessionData;
import com.stp.ssm.databases.BDFuntions;
import com.stp.ssm.http.URLs;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;

import static android.os.Build.VERSION;
import static android.os.Build.VERSION.SDK_INT;
import static android.support.v4.content.ContextCompat.getColor;
import static android.view.LayoutInflater.from;
import static com.bumptech.glide.Glide.with;
import static com.bumptech.glide.load.model.LazyHeaders.Builder;
import static com.stp.ssm.R.color;
import static com.stp.ssm.R.color.rojo1;
import static com.stp.ssm.R.color.rojo2;
import static com.stp.ssm.R.color.verde2;
import static com.stp.ssm.R.drawable;
import static com.stp.ssm.R.drawable.broken;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.grid_item;
import static com.stp.ssm.R.raw;
import static com.stp.ssm.R.raw.loading;
import static com.stp.ssm.Util.ImageFileUtil.getNameImagen;
import static com.stp.ssm.Util.SessionData.getInstance;
import static com.stp.ssm.http.URLs.URL_IMAGENES;

public class CapturasAdapter extends BaseAdapter {

    private Context context;
    private JSONArray arr_capturas;
    private LayoutInflater mInflater;
    private Capturas capturas;
    private BDFuntions bdFuntions;

    public CapturasAdapter(Context context, JSONArray arr_capturas) {
        this.context = context;
        this.arr_capturas = arr_capturas;
        this.mInflater = from(context);
        this.bdFuntions = new BDFuntions(context);
    }

    @Override
    public int getCount() {
        return arr_capturas.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return arr_capturas.get(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(grid_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        try {

            File file = new File(arr_capturas.getString(position));
            if (file.exists()) {
                with(context)
                        .load(file)
                        .into(holder.img_captura);
            } else {
                SessionData sessionData = getInstance(context);
                GlideUrl glideUrl = new GlideUrl(URL_IMAGENES + getNameImagen(arr_capturas.getString(position)),
                        new Builder().addHeader("Authorization", sessionData.getToken()).build());
                with(context)
                        .load(glideUrl)
                        .centerCrop()
                        .placeholder(loading)
                        .crossFade()
                        .error(broken)
                        .into(holder.img_captura);
            }

            capturas = bdFuntions.getCapturaByName(arr_capturas.getString(position));
            if (capturas == null) {
                changeColorText(holder.imglblestado, rojo1);
                holder.imglblestado.setText("no enviado");
            } else {
                switch (capturas.getEstado()) {
                    case 0:
                        changeColorText(holder.imglblestado, rojo2);
                        holder.imglblestado.setText("Pendiente");
                        break;
                    case 1:
                        changeColorText(holder.imglblestado, verde2);
                        holder.imglblestado.setText("Enviado");
                        break;
                    case 2:
                        changeColorText(holder.imglblestado, rojo1);
                        holder.imglblestado.setText("no enviado");
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }


    static class ViewHolder {
        ImageView img_captura;
        TextView imglblestado;

        public ViewHolder(View view) {
            img_captura = (ImageView) view.findViewById(id.img_captura);
            imglblestado = (TextView) view.findViewById(id.imglblestado);
        }
    }

    private void changeColorText(TextView textView, int color) {
        if (SDK_INT >= 23) {
            textView.setTextColor(getColor(context, color));
        } else {
            textView.setTextColor(context.getResources().getColor(color));
        }
    }
}
