package com.stp.ssm.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stp.ssm.Interfaces.OnDeleteListener;
import com.stp.ssm.R;
import com.stp.ssm.Model.Beneficiario;

import java.util.ArrayList;

import static android.view.LayoutInflater.from;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.stp.ssm.R.color;
import static com.stp.ssm.R.color.azul1;
import static com.stp.ssm.R.color.rojo1;
import static com.stp.ssm.R.color.verde1;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.id.layout_estado;
import static com.stp.ssm.R.id.lbl_documento;
import static com.stp.ssm.R.id.lbl_tipo_destinatario;
import static com.stp.ssm.R.id.lblitemDoc;
import static com.stp.ssm.R.id.lblitemEstado;
import static com.stp.ssm.R.id.lblitemNombre;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.list_item_bn;
import static com.stp.ssm.R.string;
import static com.stp.ssm.R.string.lbl_comunidad;
import static com.stp.ssm.R.string.lbl_destinatario;
import static com.stp.ssm.R.string.lbl_distrito;
import static com.stp.ssm.R.string.lbl_doc;
import static com.stp.ssm.R.string.lbl_entidad;
import static com.stp.ssm.R.string.lbl_identificador;
import static com.stp.ssm.R.string.lbl_ruc;

public class ListaBeneficiariosAdapter extends BaseAdapter {

    private ArrayList<Beneficiario> beneficiarios;
    private LayoutInflater mInflater;
    private Context context;
    private boolean sw = true;
    private OnDeleteListener onDeleteListener;


    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }


    public ListaBeneficiariosAdapter(ArrayList<Beneficiario> Beneficiarios, Context context) {
        this.beneficiarios = Beneficiarios;
        this.mInflater = from(context);
        this.context = context;
    }


    public ListaBeneficiariosAdapter(ArrayList<Beneficiario> Beneficiarios, Context context, boolean sw) {
        this.beneficiarios = Beneficiarios;
        this.mInflater = from(context);
        this.context = context;
        this.sw = sw;
    }


    @Override
    public int getCount() {
        return beneficiarios.size();
    }


    @Override
    public Object getItem(int position) {
        return beneficiarios.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {/*Inicializar Vista*/
            convertView = mInflater.inflate(list_item_bn, parent, false);
            vh = new ViewHolder();

            vh.lbl_tipo_destinatario = (TextView) convertView.findViewById(lbl_tipo_destinatario);
            vh.lblNombre = (TextView) convertView.findViewById(lblitemNombre);
            vh.lbl_documento = (TextView) convertView.findViewById(lbl_documento);
            vh.lbldocumento = (TextView) convertView.findViewById(lblitemDoc);
            vh.layout_estado = (LinearLayout) convertView.findViewById(layout_estado);
            vh.lblitemEstado = (TextView) convertView.findViewById(lblitemEstado);
            //vh.btnitemdelete = (ImageButton)convertView.findViewById(R.id.btnitemdelete);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        switch (beneficiarios.get(position).getTipo().getCodigo()) {
            case 0:
                vh.lbl_tipo_destinatario.setText(context.getString(lbl_destinatario));
                vh.lbl_documento.setText(context.getString(lbl_doc));
                vh.layout_estado.setVisibility(VISIBLE);
                break;
            case 1:
                vh.lbl_tipo_destinatario.setText(context.getString(lbl_entidad));
                vh.lbl_documento.setText(context.getString(lbl_ruc));
                vh.layout_estado.setVisibility(GONE);
                break;
            case 2:
                vh.lbl_tipo_destinatario.setText(context.getString(lbl_comunidad));
                vh.lbl_documento.setText(context.getString(lbl_identificador));
                vh.layout_estado.setVisibility(GONE);
                break;
            case 3:
                vh.lbl_tipo_destinatario.setText(context.getString(lbl_distrito));
                vh.lbl_documento.setText(context.getString(lbl_ruc));
                vh.layout_estado.setVisibility(GONE);
                break;
        }
        vh.lblNombre.setText(beneficiarios.get(position).getNombre() + " " + beneficiarios.get(position).getApellido());
        vh.lbldocumento.setText(beneficiarios.get(position).getDocumento());
        if (sw) {
            vh.lblitemEstado.setText(beneficiarios.get(position).getEstado().getDescripcion());
            switch (beneficiarios.get(position).getEstado().getCodigo()) {
                case 0:
                    vh.lblitemEstado.setTextColor(context.getResources().getColor(azul1));
                    break;
                case 1:
                    vh.lblitemEstado.setTextColor(context.getResources().getColor(rojo1));
                    break;
                case 2:
                    vh.lblitemEstado.setTextColor(context.getResources().getColor(verde1));
                    break;
            }
        } else {
           /* vh.lblitemEstado.setVisibility(View.GONE);
            vh.btnitemdelete.setVisibility(View.VISIBLE);
            vh.btnitemdelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDeleteListener.OnDelete(position);
                }
            });*/
        }
        return convertView;
    }


    public void actualizarDatos(ArrayList<Beneficiario> Beneficiarios) {
        this.beneficiarios.clear();
        this.beneficiarios.addAll(Beneficiarios);
        this.notifyDataSetChanged();
    }

    class ViewHolder {
        TextView lbl_tipo_destinatario;
        TextView lblNombre;
        TextView lbl_documento;
        TextView lbldocumento;
        LinearLayout layout_estado;
        TextView lblitemEstado;
    }
}