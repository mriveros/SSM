package com.stp.ssm.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.stp.ssm.Model.Beneficiario;
import com.stp.ssm.Model.PosiblesRespuestas;
import com.stp.ssm.Model.Pregunta;
import com.stp.ssm.Model.Secciones;
import com.stp.ssm.R;
import com.stp.ssm.View.CustomCoordenadasView;
import com.stp.ssm.View.CustomImagenView;
import com.stp.ssm.View.CustomListCoordenadasView;
import com.stp.ssm.View.ViewFactory;
import com.stp.ssm.databases.BDFuntions;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class VerFormularioAdaptares extends BaseExpandableListAdapter{

    private ArrayList<Secciones> arrSeccion;
    private Context context;
    private BDFuntions bdFuntions;
    private long idvisita;

    public VerFormularioAdaptares(ArrayList<Secciones> arrSeccion, Context context,long idvisita) {
        this.arrSeccion = arrSeccion;
        this.context = context;
        this.bdFuntions = new BDFuntions(context);
        this.idvisita = idvisita;
    }


    @Override
    public int getGroupCount() {
        return arrSeccion.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int count = 0;
        switch (arrSeccion.get(groupPosition).getTipo()){
            case 1:
            case 4:
                count = arrSeccion.get(groupPosition).getPreguntas().size();
            break;
            case 2:
                count = arrSeccion.get(groupPosition).getSubFormularios().size();
            break;
            case 3:
                count = 1;
            break;
        }
        return count;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_list_header, null);
        }
        TextView lbl_header = (TextView)convertView.findViewById(R.id.lbl_header);
        lbl_header.setTypeface(null, Typeface.BOLD);
        lbl_header.setText("\t"+arrSeccion.get(groupPosition).getDescripSeccion());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_child, null);
        }

        TextView txt_item_pregunta = (TextView)convertView.findViewById(R.id.txt_item_pregunta);
        LinearLayout layout_respuestas = (LinearLayout)convertView.findViewById(R.id.layout_respuestas);
        layout_respuestas.removeAllViews();

        switch (arrSeccion.get(groupPosition).getTipo()){
            case 1:
            case 4:
                txt_item_pregunta.setText("\t\t"+arrSeccion.get(groupPosition).getPreguntas().get(childPosition).getPregunta());
                txt_item_pregunta.setBackgroundResource(R.color.perla);
                txt_item_pregunta.setTextColor(Color.BLACK);
                agregarRespuestas(layout_respuestas,arrSeccion.get(groupPosition).getPreguntas().get(childPosition),0);
            break;
            case 2:
                txt_item_pregunta.setText("\t\t"+"Nro subformulario: " + arrSeccion.get(groupPosition).getSubFormularios().get(childPosition).getNro_subformulario());
                txt_item_pregunta.setBackgroundResource(R.color.gris1);
                txt_item_pregunta.setTextColor(Color.WHITE);
                for(Pregunta pregunta:arrSeccion.get(groupPosition).getSubFormularios().get(childPosition).getPreguntas()){
                    agregarRespuestas(layout_respuestas,pregunta,1);
                }
                //txt_item_pregunta.setVisibility(View.GONE);
                //layout_respuestas.addView(new SubformularioView(context,arrSeccion.get(groupPosition).getSubFormularios().get(childPosition)));
            break;
            case 3:
                txt_item_pregunta.setText("\t\t"+"Integrantes del Hogar");
                txt_item_pregunta.setBackgroundResource(R.color.perla);
                txt_item_pregunta.setTextColor(Color.BLACK);
                listarIntegrantes(layout_respuestas,arrSeccion.get(groupPosition).getCodSeccion(),idvisita);
            break;
        }
        return convertView;
    }

    private void agregarRespuestas(LinearLayout linear, Pregunta pregunta,int swtitulo){
        if(swtitulo == 1){
            TextView titulo = ViewFactory.createTextView("\t\t\t"+pregunta.getPregunta(),22,context, R.color.negro1);
            titulo.setTypeface(Typeface.DEFAULT_BOLD);
            linear.addView(titulo);

        }

        TextView textView;
        switch (pregunta.getTipo().getCodigo()){
            case 2:
                textView = ViewFactory.createTextView("\t\t\t"+buscarRespuesta(pregunta.getTxtrespuesta(),pregunta.getRespuestas()),18,context, R.color.negro1);
                linear.addView(textView);
            break;
            case 3:
            case 4:
            case 5:
            case 6:
            case 8:
            case 9:
            case 10:
            case 11:
                 textView = ViewFactory.createTextView("\t\t\t"+pregunta.getTxtrespuesta(),18,context, R.color.negro1);
                linear.addView(textView);
            break;
            case 1:
                for(String respuesta:pregunta.getSelecresp()){
                    linear.addView(ViewFactory.createTextView("\t\t\t"+buscarRespuesta(respuesta,pregunta.getRespuestas()),15,context, R.color.negro1));
                }
            break;
            case 12:
                for(String respuesta:pregunta.getSelecresp()){
                    linear.addView(ViewFactory.createTextView("\t\t\t"+respuesta,15,context, R.color.negro1));
                }
            break;
            case 14:
                CustomImagenView customImagenView = ViewFactory.crearImagenPregunta(context,pregunta);
                customImagenView.setImagen(pregunta.getTxtrespuesta());
                linear.addView(customImagenView);
            break;
            case 15:
                CustomCoordenadasView customCoordenadasView = ViewFactory.crearCoordenadasView(context,pregunta);
                customCoordenadasView.setCoordenadas(pregunta.getTxtrespuesta());
                customCoordenadasView.setDisableButton();
                linear.addView(customCoordenadasView);
            break;
            case 16:
                CustomListCoordenadasView customListCoordenadasView = ViewFactory.crearListaCoordenadasView(context,pregunta);
                try {
                    JSONObject jsonObject = new JSONObject(pregunta.getTxtrespuesta());
                    customListCoordenadasView.setAgregarInvisible();
                    customListCoordenadasView.setListPuntos(jsonObject,false);
                    customListCoordenadasView.setTipoObjeto(jsonObject.getString("figura"),false);
                    linear.addView(customListCoordenadasView);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            break;
        }
    }

    private void listarIntegrantes(LinearLayout linear,int codseccion,long idvisita){
        ArrayList<Beneficiario>miembros = bdFuntions.getListaHogarResp(codseccion,idvisita);
        for(Beneficiario beneficiario:miembros){
            linear.addView(ViewFactory.createTextView("\t\t\t"+beneficiario.getNombre().replace("#"," ")+" "+beneficiario.getApellido().replace("#"," "),15,context, R.color.negro1));
            linear.addView(ViewFactory.createTextView("\t\t\t"+"Documento :" + beneficiario.getDocumento(),15,context, R.color.negro1));
            linear.addView(ViewFactory.crearLinea(context));
        }
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private String buscarRespuesta(String respuesta, ArrayList<PosiblesRespuestas>respuestas){
        String resp = "";
        for(PosiblesRespuestas posiblesRespuestas:respuestas){
            if(!respuesta.equals("") && Integer.parseInt(respuesta) == posiblesRespuestas.getCodigo()){
                resp = posiblesRespuestas.getTexto();
                break;
            }
        }
        return resp;
    }
}
