package com.stp.ssm.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.stp.ssm.CapturaGaleriaActivity;
import com.stp.ssm.Evt.ContinuarFormEvt;
import com.stp.ssm.Evt.CorregirRelevamientoEvt;
import com.stp.ssm.Model.Beneficiario;
import com.stp.ssm.Model.Reporte;
import com.stp.ssm.R;
import com.stp.ssm.VerRespFormActivity;
import com.stp.ssm.databases.BDFuntions;
import java.util.ArrayList;
import de.greenrobot.event.EventBus;

public class ListaReporteAdapter extends BaseAdapter{

    private ArrayList<Reporte> listReporte;
    private LayoutInflater mInflater;
    private Context context;
    private BDFuntions data;
    private EventBus eventBus;


    public  ListaReporteAdapter(ArrayList<Reporte> listReporte,Context context){
        this.listReporte = listReporte;
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.data = new BDFuntions(context);
        this.eventBus = EventBus.getDefault();
    }


    @Override
    public int getCount() {
        return listReporte.size();
    }


    @Override
    public Object getItem(int position) {
        return listReporte.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {/*Inicializar Vista*/
            convertView = mInflater.inflate(R.layout.list_item_report, parent, false);
            vh = new ViewHolder();

            vh.txt_destinatario = (TextView)convertView.findViewById(R.id.txt_destinatario);
            vh.txt_documento = (TextView)convertView.findViewById(R.id.txt_documento);
            vh.lblBeneficiario = (TextView)convertView.findViewById(R.id.lblBeneficiario);
            vh.lblDestinatarioVerf = (TextView)convertView.findViewById(R.id.lblDestinatarioVerf);
            vh.lblDocumento = (TextView) convertView.findViewById(R.id.lblDocumento);
            vh.lblFechaVisita = (TextView)convertView.findViewById(R.id.lblFechaVisita);
            vh.lblMotivo = (TextView)convertView.findViewById(R.id.lblMotivo);
            vh.lblCoordenadas = (TextView)convertView.findViewById(R.id.lblCoordenadas);
            vh.lblValidado = (TextView)convertView.findViewById(R.id.lblValidado);
            vh.lblEstado = (TextView)convertView.findViewById(R.id.lblEstado);
            vh.lblEnvio = (TextView)convertView.findViewById(R.id.lblEnvio);
            vh.btnInforme = (Button)convertView.findViewById(R.id.btnInforme);
            vh.btnVerCapturas = (Button)convertView.findViewById(R.id.btnVerCapturas);
            vh.btnCorregir = (Button)convertView.findViewById(R.id.btnCorregir);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }

        if(listReporte.get(position).getTipo_proyecto() == 3){
            vh.txt_destinatario.setVisibility(View.GONE);
            vh.lblBeneficiario.setVisibility(View.GONE);
            vh.txt_documento.setText(context.getString(R.string.lbl_identificador));
        }else{
            vh.lblBeneficiario.setText(listReporte.get(position).getBeneficiario().replaceAll("#"," "));
        }
        vh.lblDocumento.setText(listReporte.get(position).getDocumento());
        vh.lblFechaVisita.setText(listReporte.get(position).getFechavisita());
        vh.lblMotivo.setText(listReporte.get(position).getMotivo());
        vh.lblCoordenadas.setText(listReporte.get(position).getCoordenadas().getLatitud() + "," + listReporte.get(position).getCoordenadas().getLongitud());
        String styledText;
        if(listReporte.get(position).getEstado().equals(Reporte.Estado.NO_FINALIZADO)){
            styledText = "<font color='#3B0B0B'>"+listReporte.get(position).getEstado().toString()+"</font>.";
            vh.btnCorregir.setText(context.getString(R.string.lbl_btn_continuar));
        }else{
            styledText = "<font color='#298A08'>"+listReporte.get(position).getEstado().toString()+"</font>.";
            vh.btnCorregir.setText(context.getString(R.string.lbl_btn_corregir));
        }
        vh.lblEstado.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);

        if(listReporte.get(position).getEnvio().equals(Reporte.Envio.PENDIENTE)){
            styledText = "<font color='#3B0B0B'>"+listReporte.get(position).getEnvio().toString()+"</font>.";
        }else{
            styledText = "<font color='#298A08'>"+listReporte.get(position).getEnvio().toString()+"</font>.";
        }
        vh.lblEnvio.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);

        if(listReporte.get(position).getBenficiario_estado().equals(Beneficiario.Estado.ASIGNADO)){
            vh.lblValidado.setTextColor(Color.BLUE);
        }else if(listReporte.get(position).getBenficiario_estado().equals(Beneficiario.Estado.NUEVO_NO_VALIDADO)){
            vh.lblValidado.setTextColor(Color.GREEN);
        }else{
            vh.lblValidado.setTextColor(Color.RED);
        }
        vh.lblValidado.setText(listReporte.get(position).getBenficiario_estado().getDescripcion());

        if(!data.motivoHasFormulario(listReporte.get(position).getCodmotivo()) ||
           listReporte.get(position).getEstado().equals(Reporte.Estado.NO_FINALIZADO)){
            vh.btnInforme.setVisibility(View.GONE);
        }else{
            vh.btnInforme.setVisibility(View.VISIBLE);
        }
        vh.btnInforme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,VerRespFormActivity.class);
                intent.putExtra("codmotivo",listReporte.get(position).getCodmotivo());
                intent.putExtra("idvisita",listReporte.get(position).getIdvisita());
                intent.putExtra("formulario",data.getFormularioMotivo(listReporte.get(position).getCodmotivo()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        if(!data.relHasCapturas(listReporte.get(position).getIdvisita())){
            vh.btnVerCapturas.setVisibility(View.GONE);
        }else{
            vh.btnVerCapturas.setVisibility(View.VISIBLE);
        }

        vh.btnVerCapturas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,CapturaGaleriaActivity.class);
                intent.putExtra("idvisita",listReporte.get(position).getIdvisita());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        vh.btnCorregir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listReporte.get(position).getEstado().equals(Reporte.Estado.NO_FINALIZADO)){
                    eventBus.post(new ContinuarFormEvt(position));
                }else{
                    eventBus.post(new CorregirRelevamientoEvt(position));
                }
            }
        });
        return convertView;
    }


    class ViewHolder {
        TextView txt_destinatario;
        TextView txt_documento;
        TextView lblBeneficiario;     //1
        TextView lblDestinatarioVerf; //2
        TextView lblDocumento;        //3
        TextView lblFechaVisita;      //4
        TextView lblMotivo;           //5
        TextView lblCoordenadas;      //6
        TextView lblValidado;         //7
        TextView lblEstado;           //8
        TextView lblEnvio;            //9
        Button btnInforme;            //11
        Button btnVerCapturas;        //12
        Button btnCorregir;           //13
    }

    public void notifyData(){
        this.notifyDataSetChanged();
    }
}
