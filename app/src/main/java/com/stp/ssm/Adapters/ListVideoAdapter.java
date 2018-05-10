package com.stp.ssm.Adapters;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.stp.ssm.Model.Secciones;
import com.stp.ssm.R;
import java.io.File;
import java.util.ArrayList;

public class ListVideoAdapter extends BaseAdapter {

    private ArrayList<Secciones> arr_secciones;
    private LayoutInflater mInflater;
    private Context context;

    public ListVideoAdapter(ArrayList<Secciones> arr_secciones, Context context) {
        this.arr_secciones = arr_secciones;
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getCount() {
        return arr_secciones.size();
    }

    @Override
    public Object getItem(int position) {
        return arr_secciones.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_list_video, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.txt_item_seccion = (TextView)convertView.findViewById(R.id.txt_item_seccion);
            viewHolder.img_preview_video = (ImageView)convertView.findViewById(R.id.img_preview_video);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.txt_item_seccion.setText(arr_secciones.get(position).getDescripSeccion());
        String video = Environment.getExternalStorageDirectory() + "/DCIM/videos/" + arr_secciones.get(position).getMultimedia();
        Glide.with(context)
             .load(new File(video))
             .override(800, 600)
             .fitCenter()
             .into(viewHolder.img_preview_video);
        return convertView;
    }

    class ViewHolder {
        TextView txt_item_seccion;
        ImageView img_preview_video;
    }
}
