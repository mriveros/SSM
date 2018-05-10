package com.stp.ssm.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.location.Location;
import android.os.Build;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessaging;
import com.stp.ssm.Excepciones.ExcepcionReadBD;
import com.stp.ssm.Model.Beneficiario;
import com.stp.ssm.Model.Capturas;
import com.stp.ssm.Model.Coordenadas;
import com.stp.ssm.Model.Departamento;
import com.stp.ssm.Model.Distrito;
import com.stp.ssm.Model.Evento;
import com.stp.ssm.Model.Institucion;
import com.stp.ssm.Model.Marcacion;
import com.stp.ssm.Model.Motivos;
import com.stp.ssm.Model.Notificacion;
import com.stp.ssm.Model.Operadora;
import com.stp.ssm.Model.PosiblesRespuestas;
import com.stp.ssm.Model.Pregunta;
import com.stp.ssm.Model.PreguntaCondicion;
import com.stp.ssm.Model.Proyecto;
import com.stp.ssm.Model.Reporte;
import com.stp.ssm.Model.SeccionCondicion;
import com.stp.ssm.Model.Secciones;
import com.stp.ssm.Model.SendData;
import com.stp.ssm.Model.SubFormulario;
import com.stp.ssm.Model.TipoUbicacion;
import com.stp.ssm.Model.TotalRelevado;
import com.stp.ssm.Model.Visita;
import com.stp.ssm.R;
import com.stp.ssm.Util.CellUtils;
import com.stp.ssm.Util.FechaUtil;
import com.stp.ssm.Util.ImageFileUtil;
import com.stp.ssm.Util.SessionData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BDFuntions{

    private dbAdapter data;
    private Context context;
    private SessionData sessionData;
    private ContentValues registro;


    public BDFuntions(Context context){
        data  = new dbAdapter(context);
        this.context = context;
        sessionData = SessionData.getInstance(context);
    }


    private void inicializarContent(){
        if(registro == null){
            registro = new ContentValues();
        }else{
            registro.clear();
        }
    }


    public long addBeneficiario(Beneficiario beneficiario){
        inicializarContent();
        registro.put(DataBaseMaestro.TABLE_BENEFICIARIO_NOMBRE, beneficiario.getRawNombre());
        registro.put(DataBaseMaestro.TABLE_BENEFICIARIO_APELLIDO, beneficiario.getRawApellido());
        registro.put(DataBaseMaestro.TABLE_BENEFICIARIO_DOCUMENTO, beneficiario.getDocumento());
        registro.put(DataBaseMaestro.TABLE_BENEFICIARIO_TIPO, beneficiario.getTipo().getCodigo());
        registro.put(DataBaseMaestro.TABLE_BENEFICIARIO_USUARIO, beneficiario.getUsuario());
        registro.put(DataBaseMaestro.TABLE_BENEFICIARIO_PROYECTO, beneficiario.getProyecto());
        registro.put(DataBaseMaestro.TABLE_BENEFICIARIO_DISTRITO, beneficiario.getDistrito());
        registro.put(DataBaseMaestro.TABLE_BENEFICIARIO_DPTO, beneficiario.getDepartamento());
        registro.put(DataBaseMaestro.TABLE_BENEFICIARIO_LOCALIDAD, beneficiario.getLocalidad());
        if(beneficiario.getCoordenadas() != null){
            registro.put(DataBaseMaestro.TABLE_BENEFICIARIO_LONG,beneficiario.getCoordenadas().getLongitud());
            registro.put(DataBaseMaestro.TABLE_BENEFICIARIO_LAT,beneficiario.getCoordenadas().getLatitud());
            registro.put(DataBaseMaestro.TABLE_BENEFICIARIO_PRESC,beneficiario.getCoordenadas().getPrecision());
        }
        registro.put(DataBaseMaestro.TABLE_BENEFICIARIO_EST, beneficiario.getEstado().getCodigo());
        if(beneficiario.isJefe()){
            registro.put(DataBaseMaestro.TABLE_BENEFICIARIO_JEFE,1);
        }
        long insert = data.insertarResgistro(DataBaseMaestro.TABLE_BENEFICIARIO, registro);
        if(insert == -1){
            data.updateRegistros(DataBaseMaestro.TABLE_BENEFICIARIO,registro,
                                 DataBaseMaestro.TABLE_BENEFICIARIO_DOCUMENTO+"='"+beneficiario.getDocumento()+"'",null);
            return 0;
        }
        return insert;
    }

    public ArrayList<Beneficiario> getListaBeneficiarios(String proyecto,String usuario,String busqueda){
        ArrayList<Beneficiario> listBeneficiarios = new ArrayList<>();
        String campos[] = new String[]{DataBaseMaestro.TABLE_BENEFICIARIO_NOMBRE,   //0
                                       DataBaseMaestro.TABLE_BENEFICIARIO_APELLIDO, //1
                                       DataBaseMaestro.TABLE_BENEFICIARIO_DOCUMENTO,//2
                                       DataBaseMaestro.TABLE_BENEFICIARIO_TIPO,     //3
                                       DataBaseMaestro.TABLE_BENEFICIARIO_USUARIO,  //4
                                       DataBaseMaestro.TABLE_BENEFICIARIO_PROYECTO, //5
                                       DataBaseMaestro.TABLE_BENEFICIARIO_EST,      //6
                                       DataBaseMaestro.TABLE_BENEFICIARIO_DISTRITO, //7
                                       DataBaseMaestro.TABLE_BENEFICIARIO_DPTO,     //8
                                       DataBaseMaestro.TABLE_BENEFICIARIO_JEFE};    //9

        String where = DataBaseMaestro.TABLE_BENEFICIARIO_PROYECTO + "='"+proyecto+"' AND "+
                       DataBaseMaestro.TABLE_BENEFICIARIO_USUARIO + "='"+usuario+"' ";

        if(!busqueda.equals("")){
            where = where+" AND ("+DataBaseMaestro.TABLE_BENEFICIARIO_NOMBRE+"||' '||"+
                                  DataBaseMaestro.TABLE_BENEFICIARIO_APELLIDO+" LIKE '%"+busqueda+"%'" +
                                  " OR "+DataBaseMaestro.TABLE_BENEFICIARIO_DOCUMENTO+" LIKE '%"+busqueda+"%')";
        }
        Cursor cursor = data.consultar(DataBaseMaestro.TABLE_BENEFICIARIO, campos, where, null, null, null, DataBaseMaestro.TABLE_BENEFICIARIO_ID + " DESC");
        if(cursor.moveToFirst()){
            do{
                listBeneficiarios.add(new Beneficiario(cursor.getString(0),
                                                       cursor.getString(1),
                                                       cursor.getString(2),
                                                       cursor.getString(4),
                                                       cursor.getString(5),
                                                       Beneficiario.TipoBeneficiario.findByCodigo(cursor.getInt(3)),
                                                       cursor.getInt(7),
                                                       cursor.getInt(8),
                                                       Beneficiario.Estado.findByCodigo(cursor.getInt(6)),
                                                       (cursor.getInt(9)==1)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return listBeneficiarios;
    }


    public ArrayList<Beneficiario> getListaBeneficiarios(String usuario,String busqueda,ArrayList<String> list,int proyecto){
        ArrayList<Beneficiario> listBeneficiarios = new ArrayList<>();
        String campos[] = new String[]{DataBaseMaestro.TABLE_BENEFICIARIO_NOMBRE,
                                       DataBaseMaestro.TABLE_BENEFICIARIO_APELLIDO,
                                       DataBaseMaestro.TABLE_BENEFICIARIO_DOCUMENTO,
                                       DataBaseMaestro.TABLE_BENEFICIARIO_TIPO,
                                       DataBaseMaestro.TABLE_BENEFICIARIO_USUARIO,
                                       DataBaseMaestro.TABLE_BENEFICIARIO_PROYECTO,
                                       DataBaseMaestro.TABLE_BENEFICIARIO_EST,
                                       DataBaseMaestro.TABLE_BENEFICIARIO_DISTRITO,
                                       DataBaseMaestro.TABLE_BENEFICIARIO_DPTO,
                                       DataBaseMaestro.TABLE_BENEFICIARIO_JEFE};

        String where = DataBaseMaestro.TABLE_BENEFICIARIO_USUARIO + "='"+usuario+"' AND "
                     + DataBaseMaestro.TABLE_BENEFICIARIO_JEFE + "=0  AND "
                     + DataBaseMaestro.TABLE_BENEFICIARIO_FAMILY + "=0 AND "
                     + DataBaseMaestro.TABLE_BENEFICIARIO_PROYECTO + "='"+proyecto+"' ";

        if(!busqueda.equals("")){
            where = where+" AND ("+DataBaseMaestro.TABLE_BENEFICIARIO_NOMBRE+"||' '||"+
                                   DataBaseMaestro.TABLE_BENEFICIARIO_APELLIDO+" LIKE '%"+busqueda+"%'" +
                          " OR " + DataBaseMaestro.TABLE_BENEFICIARIO_DOCUMENTO+" LIKE '%"+busqueda+"%') ";
        }

        String  listced = "('";
        for(int i=0;i<list.size();i++){
            if(i==(list.size()-1)){
                listced=listced+list.get(i)+"')";
            }else{
                listced=listced+list.get(i)+"','";
            }
        }

        where = where + " AND " + DataBaseMaestro.TABLE_BENEFICIARIO_DOCUMENTO + " NOT IN " + listced;

        Cursor cursor = data.consultar(DataBaseMaestro.TABLE_BENEFICIARIO, campos, where, null, null, null, DataBaseMaestro.TABLE_BENEFICIARIO_ID + " DESC");
        if(cursor.moveToFirst()){
            do{
                listBeneficiarios.add(new Beneficiario(cursor.getString(0),
                                                       cursor.getString(1),
                                                       cursor.getString(2),
                                                       cursor.getString(4),
                                                       cursor.getString(5),
                                                       Beneficiario.TipoBeneficiario.findByCodigo(cursor.getInt(3)),
                                                       cursor.getInt(7),
                                                       cursor.getInt(8),
                                                       Beneficiario.Estado.findByCodigo(cursor.getInt(6)),
                                                       (cursor.getInt(9)==1)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return listBeneficiarios;
    }

    public Beneficiario getBeneficiarioByDoc(String documento){
        Beneficiario beneficiario = null;
        String campos[] = new String[]{DataBaseMaestro.TABLE_BENEFICIARIO_ID,           //0
                                       DataBaseMaestro.TABLE_BENEFICIARIO_REGISTRO_ID,  //1
                                       DataBaseMaestro.TABLE_BENEFICIARIO_NOMBRE,       //2
                                       DataBaseMaestro.TABLE_BENEFICIARIO_APELLIDO,     //3
                                       DataBaseMaestro.TABLE_BENEFICIARIO_DOCUMENTO,    //4
                                       DataBaseMaestro.TABLE_BENEFICIARIO_TIPO,         //5
                                       DataBaseMaestro.TABLE_BENEFICIARIO_PROYECTO,     //6
                                       DataBaseMaestro.TABLE_BENEFICIARIO_LONG,         //7
                                       DataBaseMaestro.TABLE_BENEFICIARIO_LAT,          //8
                                       DataBaseMaestro.TABLE_BENEFICIARIO_PRESC,        //9
                                       DataBaseMaestro.TABLE_BENEFICIARIO_DPTO,         //10
                                       DataBaseMaestro.TABLE_BENEFICIARIO_DISTRITO,     //11
                                       DataBaseMaestro.TABLE_BENEFICIARIO_LOCALIDAD,    //12
                                       DataBaseMaestro.TABLE_BENEFICIARIO_EST,          //13
                                       DataBaseMaestro.TABLE_BENEFICIARIO_JEFE,         //14
                                       DataBaseMaestro.TABLE_BENEFICIARIO_FAMILY        //15
                                      };
        String WHERE = DataBaseMaestro.TABLE_BENEFICIARIO_DOCUMENTO + "='" + documento +"'";
        Cursor cursor = data.consultar(DataBaseMaestro.TABLE_BENEFICIARIO, campos, WHERE, null, null, null, DataBaseMaestro.TABLE_BENEFICIARIO_ID + " DESC");
        if(cursor.moveToFirst()){
            beneficiario = new Beneficiario();
            beneficiario.setIdbeneficiario(cursor.getLong(0));
            beneficiario.setIdregistro(cursor.getInt(1));
            beneficiario.setNombre(cursor.getString(2));
            beneficiario.setApellido(cursor.getString(3));
            beneficiario.setDocumento(cursor.getString(4));
            beneficiario.setTipo(Beneficiario.TipoBeneficiario.findByCodigo(cursor.getInt(5)));
            beneficiario.setProyecto(cursor.getString(6));
            beneficiario.setCoordenadas(new Coordenadas(cursor.getString(7),
                                                        cursor.getString(8),
                                                        cursor.getFloat(9)));
            beneficiario.setDepartamento(cursor.getInt(10));
            beneficiario.setDistrito(cursor.getInt(11));
            beneficiario.setLocalidad(cursor.getInt(12));
            beneficiario.setEstado(Beneficiario.Estado.findByCodigo(cursor.getInt(13)));
            beneficiario.setJefe((cursor.getInt(14)==1));
            cursor.close();
        }
        return beneficiario;
    }


    public long addTelefonia(Operadora operadora,String usuario){
        if(operadora != null){
            if(!operadora.getSerialSim().equals(lastSIMSave()) || !checkAndroidID(CellUtils.getAndroidId(context))){
                inicializarContent();
                registro.put(DataBaseMaestro.TABLE_OPERADORA_NOM, operadora.getNombre());
                registro.put(DataBaseMaestro.TABLE_OPERADORA_SERIAL_SIM, operadora.getSerialSim());
                registro.put(DataBaseMaestro.TABLE_OPERADORA_COUNTRYISO, operadora.getCountryISO());
                registro.put(DataBaseMaestro.TABLE_OPERADORA_FECHA, FechaUtil.getFechaActual());
                registro.put(DataBaseMaestro.TABLE_OPERADORA_CODOPERADORA, operadora.getOperador());
                registro.put(DataBaseMaestro.TABLE_OPERADORA_USUARIO, usuario);
                registro.put(DataBaseMaestro.TABLE_OPERADORA_ANDROIDID, CellUtils.getAndroidId(context));
                return data.insertarResgistro(DataBaseMaestro.TABLE_OPERADORA, registro);
            }
        }
        return 0;
    }

    public boolean checkAndroidID(String androidid){
        String CONSULTA = "SELECT _id FROM operadora WHERE "+DataBaseMaestro.TABLE_OPERADORA_ANDROIDID+"='"+androidid+"'";
        Cursor cursor = data.consultaSql(CONSULTA,null);
        Boolean result = false;
        if(cursor.moveToFirst()){
            result = true;
        }
        return result;
    }


    public int updateOperadoraUser(String usuario){
        inicializarContent();
        registro.put(DataBaseMaestro.TABLE_OPERADORA_USUARIO, usuario);
        return data.updateRegistros(DataBaseMaestro.TABLE_OPERADORA, registro,
                DataBaseMaestro.TABLE_OPERADORA_USUARIO + "=''", null);
    }


    public String lastSIMSave(){
        String campos[] = new String[]{DataBaseMaestro.TABLE_OPERADORA_SERIAL_SIM};
        Cursor cursor = data.consultar(DataBaseMaestro.TABLE_OPERADORA, campos, null, null, null, null, null);
        String result = "";
        if(cursor.moveToLast()){
            result = cursor.getString(0);
        }
        cursor.close();
        return result;
    }


    public long guardarUbicacion(Coordenadas coordenadas,TipoUbicacion tipoUbicacion){
        inicializarContent();
        registro.put(DataBaseMaestro.TABLE_UBICACIONES_LONG, coordenadas.getLongitud());
        registro.put(DataBaseMaestro.TABLE_UBICACIONES_LAT,coordenadas.getLatitud());
        registro.put(DataBaseMaestro.TABLE_UBICACIONES_HORA_OBT,coordenadas.getHora());
        registro.put(DataBaseMaestro.TABLE_UBICACIONES_HORA_GUARD, FechaUtil.getFechaActual());
        registro.put(DataBaseMaestro.TABLE_UBICACIONES_PROVEDOR, coordenadas.getProveedor());
        registro.put(DataBaseMaestro.TABLE_UBICACIONES_ALTITUD, coordenadas.getAltitud());
        registro.put(DataBaseMaestro.TABLE_UBICACIONES_PRECS, coordenadas.getPrecision());
        registro.put(DataBaseMaestro.TABLE_UBICACIONES_TIPO, tipoUbicacion.getCodigo());
        registro.put(DataBaseMaestro.TABLE_UBICACIONES_BATERIA, CellUtils.getPorcBaterria(context));
        registro.put(DataBaseMaestro.TABLE_UBICACIONES_USER, sessionData.getUsuario());
        return data.insertarResgistro(DataBaseMaestro.TABLE_UBICACIONES, registro);
    }


    public ArrayList<Motivos> getMotivos(int proyecto_id){
        ArrayList<Motivos>motivos = new ArrayList<>();
        String campos[] = new String[]{DataBaseMaestro.TABLE_MOTIVOS_ID,
                DataBaseMaestro.TABLE_MOTIVOS_CODIGO,
                DataBaseMaestro.TABLE_MOTIVOS_DESCRIPCION,
                DataBaseMaestro.TABLE_MOTIVOS_COD_FORMULARIO,
                DataBaseMaestro.TABLE_MOTIVOS_FORMULARIO_DESCRIP,
                DataBaseMaestro.TABLE_MOTIVOS_HAS_FORMULARIO};
        Cursor cursor = data.consultar(DataBaseMaestro.TABLE_MOTIVOS, campos, DataBaseMaestro.TABLE_MOTIVOS_ID_PROYECTO+"="+proyecto_id, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                motivos.add(new Motivos(cursor.getInt(1),cursor.getString(2),cursor.getInt(3),cursor.getString(4),(cursor.getInt(5)==1)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return motivos;
    }


    public ArrayList<Motivos> getMotivosForm(){
        ArrayList<Motivos>motivos = new ArrayList<>();
        String consulta ="SELECT m."+DataBaseMaestro.TABLE_MOTIVOS_ID+"," +                   //0
                                "m."+DataBaseMaestro.TABLE_MOTIVOS_CODIGO+"," +               //1
                                "m."+DataBaseMaestro.TABLE_MOTIVOS_DESCRIPCION+"," +          //2
                                "m."+DataBaseMaestro.TABLE_MOTIVOS_COD_FORMULARIO+"," +       //3
                                "m."+DataBaseMaestro.TABLE_MOTIVOS_FORMULARIO_DESCRIP+"," +   //4
                                "m."+DataBaseMaestro.TABLE_MOTIVOS_HAS_FORMULARIO+"," +       //5
                                "p."+DataBaseMaestro.TABLE_PROYECTOS_COD+" " +                //6
                          "FROM "+DataBaseMaestro.TABLE_MOTIVOS+" m,"+DataBaseMaestro.TABLE_PROYECTOS+" p "+
                          "WHERE p."+DataBaseMaestro.TABLE_PROYECTOS_COD+"=m."+DataBaseMaestro.TABLE_MOTIVOS_ID_PROYECTO+" AND " +
                                "m."+DataBaseMaestro.TABLE_MOTIVOS_HAS_FORMULARIO+"=1";
        Cursor cursor = data.consultaSql(consulta,null);
        if(cursor.moveToFirst()){
            do{
                motivos.add(new Motivos(cursor.getInt(1),
                                        cursor.getString(2),
                                        cursor.getInt(3),
                                        cursor.getString(4),
                                        (cursor.getInt(5)==1),
                                        cursor.getInt(6)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return motivos;
    }


    public Motivos getMotivo(int codigomotivo){
        String campos[] = new String[]{DataBaseMaestro.TABLE_MOTIVOS_ID,                //0
                                       DataBaseMaestro.TABLE_MOTIVOS_CODIGO,            //1
                                       DataBaseMaestro.TABLE_MOTIVOS_DESCRIPCION,       //2
                                       DataBaseMaestro.TABLE_MOTIVOS_COD_FORMULARIO,    //3
                                       DataBaseMaestro.TABLE_MOTIVOS_FORMULARIO_DESCRIP,//4
                                       DataBaseMaestro.TABLE_MOTIVOS_HAS_FORMULARIO,    //5
                                       DataBaseMaestro.TABLE_MOTIVOS_ID_PROYECTO};      //6
        String where = DataBaseMaestro.TABLE_MOTIVOS_CODIGO+"="+codigomotivo;
        Cursor cursor = data.consultar(DataBaseMaestro.TABLE_MOTIVOS, campos, where, null, null, null, null);
        Motivos motivos = null;
        if(cursor.moveToFirst()){
            motivos = new Motivos(cursor.getInt(1),cursor.getString(2),cursor.getInt(3),cursor.getString(4),(cursor.getInt(5)==1),cursor.getInt(6));
        }
        cursor.close();
        return motivos;
    }


    public boolean motivoHasFormulario(int codigomotivo){
        String campos[] = new String[]{DataBaseMaestro.TABLE_MOTIVOS_HAS_FORMULARIO};
        String where = DataBaseMaestro.TABLE_MOTIVOS_CODIGO+"="+codigomotivo;
        Cursor cursor = data.consultar(DataBaseMaestro.TABLE_MOTIVOS, campos, where, null, null, null, null);
        boolean result = false;
        if(cursor.moveToFirst()){
            result = (cursor.getInt(0)==1);
        }
        cursor.close();
        return result;
    }


    public String getFormularioMotivo(int codigomotivo){
        String campos[] = new String[]{DataBaseMaestro.TABLE_MOTIVOS_FORMULARIO_DESCRIP};
        String where = DataBaseMaestro.TABLE_MOTIVOS_CODIGO+"="+codigomotivo;
        Cursor cursor = data.consultar(DataBaseMaestro.TABLE_MOTIVOS, campos, where, null, null, null, null);
        String result = "";
        if(cursor.moveToFirst()){
            result = cursor.getString(0);
        }
        cursor.close();
        return result;
    }


    public boolean relHasCapturas(long idvisita){
        String CONSULTA = "SELECT COUNT(_id) FROM Capturas WHERE idVisita="+idvisita;
        Cursor cursor = data.consultaSql(CONSULTA,null);
        Boolean result = false;
        if(cursor.moveToNext()){
            result = (cursor.getInt(0)>0);
        }
        cursor.close();
        return result;
    }


    public void addSeccion(ArrayList<Secciones> secciones,int codmotivo){
        inicializarContent();
        /*Inserta Secciones en la base de dato segun cuantos registros haya en el ArrayList*/
        for(Secciones seccion:secciones){
            registro.put(DataBaseMaestro.TABLE_SECCIONES_MOTIVO,codmotivo);
            registro.put(DataBaseMaestro.TABLE_SECCIONES_COD,seccion.getCodSeccion());
            registro.put(DataBaseMaestro.TABLE_SECCIONES_DESCRIP, seccion.getDescripSeccion());
            registro.put(DataBaseMaestro.TABLE_SECCIONES_TIPO,seccion.getTipo());
            registro.put(DataBaseMaestro.TABLE_SECCIONES_TOTALIZABLE,seccion.getTotalizable());
            registro.put(DataBaseMaestro.TABLE_SECCIONES_CONDICIONABLE,seccion.getCondicionable());
            if(seccion.getTipo()==4){
                registro.put(DataBaseMaestro.TABLE_SECCIONES_MULTIMEDIA,seccion.getMultimedia());
            }
            data.insertarResgistro(DataBaseMaestro.TABLE_SECCIONES, registro);
            registro.clear();
            addPreguntas(seccion.getPreguntas(), seccion.getCodSeccion());
            if(seccion.getCondicionsSiguiente()!=null && seccion.getCondicionsSiguiente().size()>0){
                addCondicionSeccion(seccion.getCodSeccion(),seccion.getCondicionsSiguiente());
            }
        }
    }


    private void addCondicionSeccion(int codseccion, ArrayList<SeccionCondicion> arrSeccionCondicion){
        inicializarContent();
        for(SeccionCondicion seccionCondicion:arrSeccionCondicion){
            registro.put(DataBaseMaestro.TABLE_SECCION_CONDICION_SEC_CONDICIONADA,codseccion);
            registro.put(DataBaseMaestro.TABLE_SECCION_CONDICION_SEC_SIGUIENTE,seccionCondicion.getIdseccionsiguiente());
            registro.put(DataBaseMaestro.TABLE_SECCION_CONDICION_PREG_CONDICIONANTE,seccionCondicion.getIdpreguntacondicionante());
            registro.put(DataBaseMaestro.TABLE_SECCION_CONDICION_COND,seccionCondicion.getCondicion());
            registro.put(DataBaseMaestro.TABLE_SECCION_CONDICION_VALOR,seccionCondicion.getValor());
            data.insertarResgistro(DataBaseMaestro.TABLE_SECCION_CONDICION, registro);
            registro.clear();
        }
    }



    public ArrayList<Secciones> getSecciones(int codmotivo,long idvisita){
        ArrayList<Secciones>secciones = new ArrayList<>();
        String campos[] = new String[]{DataBaseMaestro.TABLE_SECCIONES_ID,              //0
                                       DataBaseMaestro.TABLE_SECCIONES_COD,             //1
                                       DataBaseMaestro.TABLE_SECCIONES_DESCRIP,         //2
                                       DataBaseMaestro.TABLE_SECCIONES_TIPO,            //3
                                       DataBaseMaestro.TABLE_SECCIONES_TOTALIZABLE,     //4
                                       DataBaseMaestro.TABLE_SECCIONES_CONDICIONABLE,   //5
                                       DataBaseMaestro.TABLE_SECCIONES_MULTIMEDIA};     //6
        String where = DataBaseMaestro.TABLE_SECCIONES_MOTIVO+"="+codmotivo;
        Cursor cursor = data.consultar(DataBaseMaestro.TABLE_SECCIONES, campos, where, null, null, null, null);
        Secciones seccion;
        if(cursor.moveToFirst()){
            int indice = 1;
            do{
                seccion = new Secciones(cursor.getInt(1),
                                        indice+"-"+cursor.getString(2),
                                        getPreguntas(cursor.getInt(1),idvisita,indice,cursor.getInt(3)),
                                        cursor.getInt(3),
                                        cursor.getInt(4),
                                        cursor.getInt(5),
                                        getSeccionCondicion(cursor.getInt(1)));

                switch (cursor.getInt(3)){
                    case 4:
                        seccion.setMultimedia(cursor.getString(6));
                    break;
                    case 2:
                        seccion.setSubFormularios(getArrSubFormularios(cursor.getInt(1),idvisita));
                    break;
                }

                secciones.add(seccion);
                indice++;
            }while (cursor.moveToNext());
        }
        cursor.close();
        return secciones;
    }


    public ArrayList<SubFormulario> getArrSubFormularios(int idseccion,long idvisita){
        ArrayList<SubFormulario> arr_subformuarios = new ArrayList<>();
        String CONSULTA = "SELECT pr." + DataBaseMaestro.TABLE_PREGUNTAS_ID_SECCION + ",res." + DataBaseMaestro.TABLE_RESPUESTAS_NRO_SUBFORM + " " +
                          "FROM " + DataBaseMaestro.TABLE_PREGUNTAS + " pr," + DataBaseMaestro.TABLE_RESPUESTAS + " res " +
                          "WHERE pr." + DataBaseMaestro.TABLE_PREGUNTAS_ID_PREG + "=res." + DataBaseMaestro.TABLE_RESPUESTAS_ID_PREG + " AND " +
                                        DataBaseMaestro.TABLE_PREGUNTAS_ID_SECCION + "=" + idseccion + " AND res." +
                                        DataBaseMaestro.TABLE_RESPUESTAS_ID_VIST + "=" +idvisita + " " +
                          "GROUP BY res." + DataBaseMaestro.TABLE_RESPUESTAS_NRO_SUBFORM;
        Cursor cursor = data.consultaSql(CONSULTA,null);
        SubFormulario subFormulario;
        if(cursor.moveToFirst()){
            do{
                subFormulario = new SubFormulario();
                subFormulario.setIdseccion(Integer.toString(idseccion));
                subFormulario.setNro_subformulario(cursor.getInt(1));
                subFormulario.setPreguntas(getPreguntas(idseccion,idvisita,cursor.getInt(1)));
                arr_subformuarios.add(subFormulario);
            }while (cursor.moveToNext());
        }
        return arr_subformuarios;
    }


    public ArrayList<SeccionCondicion>getSeccionCondicion(int codSeccion){
        ArrayList<SeccionCondicion>arrSeccionCondicion = new ArrayList<>();
        String campos[] = new String[]{DataBaseMaestro.TABLE_SECCION_CONDICION_PREG_CONDICIONANTE,
                                       DataBaseMaestro.TABLE_SECCION_CONDICION_SEC_SIGUIENTE,
                                       DataBaseMaestro.TABLE_SECCION_CONDICION_COND,
                                       DataBaseMaestro.TABLE_SECCION_CONDICION_VALOR};
        String where = DataBaseMaestro.TABLE_SECCION_CONDICION_SEC_CONDICIONADA+"="+codSeccion;
        Cursor cursor = data.consultar(DataBaseMaestro.TABLE_SECCION_CONDICION,campos,where,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                arrSeccionCondicion.add(new SeccionCondicion(cursor.getLong(0),
                                                             cursor.getLong(1),
                                                             cursor.getInt(2),
                                                             cursor.getString(3)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return arrSeccionCondicion;
    }


    public void addPreguntas(ArrayList<Pregunta>preguntas,int codseccion){
        inicializarContent();
        if(preguntas!=null && !preguntas.isEmpty()){
            for(Pregunta pregunta:preguntas){
                registro.put(DataBaseMaestro.TABLE_PREGUNTAS_ID_SECCION,codseccion);
                registro.put(DataBaseMaestro.TABLE_PREGUNTAS_ID_PREG,pregunta.getIdpregunta());
                registro.put(DataBaseMaestro.TABLE_PREGUNTAS_TXT,pregunta.getPregunta());
                registro.put(DataBaseMaestro.TABLE_PREGUNTAS_TIPO, pregunta.getTipo().getCodigo());
                registro.put(DataBaseMaestro.TABLE_PREGUNTAS_requerido, pregunta.getRequerido().getCodigo());
                registro.put(DataBaseMaestro.TABLE_PREGUNTAS_TOTALIZABLE, pregunta.getTotalizable());
                registro.put(DataBaseMaestro.TABLE_PREGUNTAS_VISIBLE, pregunta.getListable());
                data.insertarResgistro(DataBaseMaestro.TABLE_PREGUNTAS, registro);
                registro.clear();
                if(pregunta.getRespuestas()!=null){
                    addPosiblesRespuestas(pregunta.getRespuestas(),pregunta.getIdpregunta());
                }
                if(pregunta.getPreguntaCondicions()!=null){
                    addPreguntaCondicions(pregunta.getPreguntaCondicions(),pregunta.getIdpregunta());
                }
            }
        }
    }


    public ArrayList<Pregunta>getPreguntas(int codseccion,long idvisita,int nroseccion,int tiposeccion){
        ArrayList<Pregunta>preguntas = new ArrayList<>();
        String campos[] = new String[]{DataBaseMaestro.TABLE_PREGUNTAS_ID,         //0
                                       DataBaseMaestro.TABLE_PREGUNTAS_ID_PREG,    //1
                                       DataBaseMaestro.TABLE_PREGUNTAS_TXT,        //2
                                       DataBaseMaestro.TABLE_PREGUNTAS_TIPO,       //3
                                       DataBaseMaestro.TABLE_PREGUNTAS_condicion,  //4
                                       DataBaseMaestro.TABLE_PREGUNTAS_requerido,  //5
                                       DataBaseMaestro.TABLE_PREGUNTAS_TOTALIZABLE,//6
                                       DataBaseMaestro.TABLE_PREGUNTAS_VISIBLE};   //7
        String where = DataBaseMaestro.TABLE_PREGUNTAS_ID_SECCION+"="+codseccion+" AND "+
                       DataBaseMaestro.TABLE_PREGUNTAS_estado+"=1";
        Cursor cursor = data.consultar(DataBaseMaestro.TABLE_PREGUNTAS, campos, where, null, null, null, null);
        if(cursor.moveToFirst()){
            int indice = 1;
            String cadenapregunta = "";
            do{
                cadenapregunta = cursor.getString(2);
                if(!cadenapregunta.equals("ESPECIFICAR")){
                    cadenapregunta = nroseccion+"."+indice + "-" + cadenapregunta;
                    indice++;
                }
                Pregunta pregunta = new Pregunta(cursor.getInt(1),
                                                 cadenapregunta,
                                                 Pregunta.TIPO.findByCodigo(cursor.getInt(3)),
                                                 cursor.getInt(5),null,
                                                 cursor.getInt(6),
                                                 cursor.getInt(7));
                if(cursor.getInt(3) != Pregunta.TIPO.CUADRO_TEXTO.getCodigo()){
                    pregunta.setRespuestas(getPosiblesResp(cursor.getInt(1)));
                }
                if(tiposeccion!=2){
                    pregunta.cargarRespuesta(recuperarRespuesta(idvisita,cursor.getInt(1),0));
                }
                pregunta.setPreguntaCondicions(getPreguntaCondicions(cursor.getInt(1)));
                pregunta.setVisible(!isPregCondicionada(cursor.getInt(1)));
                preguntas.add(pregunta);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return preguntas;
    }


    public ArrayList<Pregunta>getPreguntas(int codseccion,long idvisita,int nrosubformulario){
        ArrayList<Pregunta>preguntas = new ArrayList<>();

        String CAMPOS[] = new String[]{DataBaseMaestro.TABLE_PREGUNTAS_ID,         //0
                                       DataBaseMaestro.TABLE_PREGUNTAS_ID_PREG,    //1
                                       DataBaseMaestro.TABLE_PREGUNTAS_TXT,        //2
                                       DataBaseMaestro.TABLE_PREGUNTAS_TIPO,       //3
                                       DataBaseMaestro.TABLE_PREGUNTAS_condicion,  //4
                                       DataBaseMaestro.TABLE_PREGUNTAS_requerido,  //5
                                       DataBaseMaestro.TABLE_PREGUNTAS_TOTALIZABLE,//6
                                       DataBaseMaestro.TABLE_PREGUNTAS_VISIBLE};   //7

        String WHERE = DataBaseMaestro.TABLE_PREGUNTAS_ID_SECCION + "=" + codseccion + " AND " +
                       DataBaseMaestro.TABLE_PREGUNTAS_estado + "=1";

        Cursor cursor = data.consultar(DataBaseMaestro.TABLE_PREGUNTAS,CAMPOS,WHERE, null, null, null, null);

        if(cursor.moveToFirst()){
            String cadenapregunta = "";
            do{
                cadenapregunta = cursor.getString(2);
                Pregunta pregunta = new Pregunta(cursor.getInt(1),
                                                 cadenapregunta,
                                                 Pregunta.TIPO.findByCodigo(cursor.getInt(3)),
                                                 cursor.getInt(5),null,
                                                 cursor.getInt(6),
                                                 cursor.getInt(7));
                if(cursor.getInt(3) != Pregunta.TIPO.CUADRO_TEXTO.getCodigo()){
                    pregunta.setRespuestas(getPosiblesResp(cursor.getInt(1)));
                }
                pregunta.cargarRespuesta(recuperarRespuesta(idvisita,cursor.getInt(1),nrosubformulario));
                pregunta.setPreguntaCondicions(getPreguntaCondicions(cursor.getInt(1)));
                pregunta.setVisible(!isPregCondicionada(cursor.getInt(1)));
                preguntas.add(pregunta);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return preguntas;
    }


    public boolean isPregCondicionada(int idpregunta){
        String campos[] = new String[]{DataBaseMaestro.TABLE_PREGUNTAS_CONDICION_PREG_CONDICIONADA};
        String where = DataBaseMaestro.TABLE_PREGUNTAS_CONDICION_PREG_CONDICIONADA+"="+idpregunta;
        Cursor cursor = data.consultar(DataBaseMaestro.TABLE_PREGUNTAS_CONDICION, campos, where, null, null, null, null);
        boolean result = false;
        if(cursor.moveToFirst()){
            result = true;
        }
        cursor.close();
        return result;
    }


    public ArrayList<PosiblesRespuestas> getPosiblesResp(int idpregunta){
        ArrayList<PosiblesRespuestas>respuestas = new ArrayList<>();
        String campos[] = new String[]{DataBaseMaestro.TABLE_POSRESPUESTAS_ID,
                                       DataBaseMaestro.TABLE_POSRESPUESTAS_IDRESPUESTA,
                                       DataBaseMaestro.TABLE_POSRESPUESTAS_OPCION};
        String where = DataBaseMaestro.TABLE_POSRESPUESTAS_IDPREGUNTA+"="+idpregunta;
        Cursor cursor = data.consultar(DataBaseMaestro.TABLE_POSRESPUESTAS, campos, where, null, null, null, null);
        if(cursor.moveToFirst()){

            do{
                respuestas.add(new PosiblesRespuestas(cursor.getInt(1), cursor.getString(2)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return respuestas;
    }

    public ArrayList<PreguntaCondicion> getPreguntaCondicions(int idpregunta){
        ArrayList<PreguntaCondicion>arr_preguntaCondicions = new ArrayList<>();
        String campos[] = new String[]{DataBaseMaestro.TABLE_PREGUNTAS_CONDICION_PREG_CONDICIONADA,
                                       DataBaseMaestro.TABLE_PREGUNTAS_CONDICION_COND,
                                       DataBaseMaestro.TABLE_PREGUNTAS_CONDICION_VALOR};
        String where = DataBaseMaestro.TABLE_PREGUNTAS_CONDICION_PREG_CONDICIONANTE + "=" + idpregunta;
        Cursor cursor = data.consultar(DataBaseMaestro.TABLE_PREGUNTAS_CONDICION, campos, where, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                arr_preguntaCondicions.add(new PreguntaCondicion(cursor.getLong(0),
                                                                 cursor.getInt(1),
                                                                 cursor.getString(2)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return arr_preguntaCondicions;
    }

    public void addPosiblesRespuestas(ArrayList<PosiblesRespuestas> posiblesRespuestas,int idpregunta){
        inicializarContent();
        for(PosiblesRespuestas respuestas:posiblesRespuestas){
            registro.put(DataBaseMaestro.TABLE_POSRESPUESTAS_IDPREGUNTA,idpregunta);
            registro.put(DataBaseMaestro.TABLE_POSRESPUESTAS_IDRESPUESTA,respuestas.getCodigo());
            registro.put(DataBaseMaestro.TABLE_POSRESPUESTAS_OPCION, respuestas.getTexto());
            data.insertarResgistro(DataBaseMaestro.TABLE_POSRESPUESTAS, registro);
            registro.clear();
        }
    }


    public void addPreguntaCondicions(ArrayList<PreguntaCondicion>preguntaCondicions,int idpregunta){
        inicializarContent();
        for(PreguntaCondicion condicion:preguntaCondicions){
            registro.put(DataBaseMaestro.TABLE_PREGUNTAS_CONDICION_PREG_CONDICIONANTE,idpregunta);
            registro.put(DataBaseMaestro.TABLE_PREGUNTAS_CONDICION_PREG_CONDICIONADA,condicion.getId_pregunta_condicionada());
            registro.put(DataBaseMaestro.TABLE_PREGUNTAS_CONDICION_COND,condicion.getCondicion());
            registro.put(DataBaseMaestro.TABLE_PREGUNTAS_CONDICION_VALOR,condicion.getValor());
            data.insertarResgistro(DataBaseMaestro.TABLE_PREGUNTAS_CONDICION,registro);
            registro.clear();
        }
    }

    public long addVisita(Visita visita,String usuario,Beneficiario beneficiario){
        inicializarContent();
        if(visita.getBeneficiario()!=null){
            registro.put(DataBaseMaestro.TABLE_VISITA_DOCBENEFICIARIO, visita.getBeneficiario().getDocumento());
        }
        registro.put(DataBaseMaestro.TABLE_VISITA_HORAINICIO,visita.getHorainicio());
        registro.put(DataBaseMaestro.TABLE_VISITA_CODMOTIVO,visita.getCodmotivo());

        if(visita.getCoordenadas() != null){
            registro.put(DataBaseMaestro.TABLE_VISITA_LONG,visita.getCoordenadas().getLongitud());
            registro.put(DataBaseMaestro.TABLE_VISITA_LAT,visita.getCoordenadas().getLatitud());
            registro.put(DataBaseMaestro.TABLE_VISITA_PRES,visita.getCoordenadas().getPrecision());
        }

        registro.put(DataBaseMaestro.TABLE_VISITA_OBS,visita.getObservacion());
        registro.put(DataBaseMaestro.TABLE_VISITA_PROYECTO,visita.getProyecto());
        registro.put(DataBaseMaestro.TABLE_VISITA_USER,usuario);
        registro.put(DataBaseMaestro.TABLE_VISITA_TIME,visita.getTiempo());

        registro.put(DataBaseMaestro.TABLE_VISITA_DIST, beneficiario.getDistrito());
        registro.put(DataBaseMaestro.TABLE_VISITA_DPTO, beneficiario.getDepartamento());
        registro.put(DataBaseMaestro.TABLE_VISITA_LOC, beneficiario.getLocalidad());

        if(!visita.hasFormulario()){
            registro.put(DataBaseMaestro.TABLE_VISITA_EST,1);
            registro.put(DataBaseMaestro.TABLE_VISITA_HORAFIN,visita.getHorafin());
        }

        registro.put(DataBaseMaestro.TABLE_VISITA_ID_KEY,visita.getId_key());
        registro.put(DataBaseMaestro.TABLE_VISITA_ORIGINAL,visita.getOriginal());

        return data.insertarResgistro(DataBaseMaestro.TABLE_VISITA,registro);
    }


    public void guardarRespuestas(ArrayList<Pregunta> preguntas,long idvisita,String beneficiarioDoc,int nrosubform){
        inicializarContent();
        for(Pregunta pregunta:preguntas){
            if(pregunta.getTipo().getCodigo() == Pregunta.TIPO.CHECKBOX.getCodigo()){
                if(pregunta.getSelecresp() != null && !pregunta.getSelecresp().isEmpty()){
                    for(String cadena:pregunta.getSelecresp()){
                        registro.put(DataBaseMaestro.TABLE_RESPUESTAS_ID_PREG,pregunta.getIdpregunta());
                        registro.put(DataBaseMaestro.TABLE_RESPUESTAS_ID_VIST,idvisita);
                        registro.put(DataBaseMaestro.TABLE_RESPUESTAS_ID_POSIBLE_RESP,cadena);
                        registro.put(DataBaseMaestro.TABLE_RESPUESTAS_PREG_TIPO,pregunta.getTipo().getCodigo());
                        registro.put(DataBaseMaestro.TABLE_RESPUESTAS_BENEFICIARIODOC,beneficiarioDoc);
                        registro.put(DataBaseMaestro.TABLE_RESPUESTAS_NRO_SUBFORM,nrosubform);
                        data.insertarResgistro(DataBaseMaestro.TABLE_RESPUESTAS, registro);
                        registro.clear();
                    }
                }
            }else{
                if(pregunta.getTxtrespuesta() != null && !pregunta.getTxtrespuesta().equals("")){
                    registro.put(DataBaseMaestro.TABLE_RESPUESTAS_ID_PREG,pregunta.getIdpregunta());
                    registro.put(DataBaseMaestro.TABLE_RESPUESTAS_ID_VIST,idvisita);
                    registro.put(DataBaseMaestro.TABLE_RESPUESTAS_TEXT_RESP,pregunta.getTxtrespuesta());
                    registro.put(DataBaseMaestro.TABLE_RESPUESTAS_PREG_TIPO,pregunta.getTipo().getCodigo());
                    registro.put(DataBaseMaestro.TABLE_RESPUESTAS_BENEFICIARIODOC,beneficiarioDoc);
                    registro.put(DataBaseMaestro.TABLE_RESPUESTAS_NRO_SUBFORM,nrosubform);
                    data.insertarResgistro(DataBaseMaestro.TABLE_RESPUESTAS, registro);
                    registro.clear();
                    if(pregunta.getTipo().getCodigo() == Pregunta.TIPO.IMAGEN.getCodigo()){
                        String hash = ImageFileUtil.getHashOfFile(new File(pregunta.getTxtrespuesta()));
                        if(!existImagen(hash)){
                            registro.put(DataBaseMaestro.TABLE_CAPTURAS_IDVISITA,idvisita);
                            registro.put(DataBaseMaestro.TABLE_CAPTURAS_DOCBENEFICIARIO,beneficiarioDoc);
                            registro.put(DataBaseMaestro.TABLE_CAPTURAS_PATH,pregunta.getTxtrespuesta());
                            registro.put(DataBaseMaestro.TABLE_CAPTURAS_FECHA,FechaUtil.getFechaActual());
                            registro.put(DataBaseMaestro.TABLE_CAPTURAS_NUMERO,0);
                            registro.put(DataBaseMaestro.TABLE_CAPTURAS_ORIGEN,Capturas.TIPO_ORIGEN.CAMARA.getCodigo());
                            registro.put(DataBaseMaestro.TABLE_CAPTURAS_EST,0);
                            //registro.put(DataBaseMaestro.TABLE_CAPTURAS_HASH,ImageFileUtil.getHashOfFile(new File(pregunta.getTxtrespuesta())));
                            registro.put(DataBaseMaestro.TABLE_CAPTURAS_HASH,hash);
                            data.insertarResgistro(DataBaseMaestro.TABLE_CAPTURAS, registro);
                            registro.clear();
                        }
                    }
                }else if(pregunta.getTipo().getCodigo() == Pregunta.TIPO.LISTA_DINAMICA.getCodigo()){
                    if(pregunta.getSelecresp() != null && !pregunta.getSelecresp().isEmpty()){
                        for(String cadena:pregunta.getSelecresp()){
                            registro.put(DataBaseMaestro.TABLE_RESPUESTAS_ID_PREG,pregunta.getIdpregunta());
                            registro.put(DataBaseMaestro.TABLE_RESPUESTAS_ID_VIST,idvisita);
                            registro.put(DataBaseMaestro.TABLE_RESPUESTAS_TEXT_RESP,cadena);
                            registro.put(DataBaseMaestro.TABLE_RESPUESTAS_PREG_TIPO,pregunta.getTipo().getCodigo());
                            registro.put(DataBaseMaestro.TABLE_RESPUESTAS_BENEFICIARIODOC,beneficiarioDoc);
                            registro.put(DataBaseMaestro.TABLE_RESPUESTAS_NRO_SUBFORM,nrosubform);
                            data.insertarResgistro(DataBaseMaestro.TABLE_RESPUESTAS, registro);
                            registro.clear();
                        }
                    }
                }
            }
        }
    }

    public void guardarRespuestasSubform(ArrayList<SubFormulario> subFormularios, long idvisita,String beneficiarioDoc){
        int contador = 1;
        for(SubFormulario subform:subFormularios){
            guardarRespuestas(subform.getPreguntas(),idvisita,beneficiarioDoc,contador);
            contador++;
        }
    }


    public void guardarRespuestasHogar(ArrayList<Beneficiario> beneficiarios, long idvisita,String beneficiarioDoc,long codSeccion){
        inicializarContent();
        for(Beneficiario beneficiario:beneficiarios){
            registro.put(DataBaseMaestro.TABLE_RESPUESTAS_ID_PREG,codSeccion);
            registro.put(DataBaseMaestro.TABLE_RESPUESTAS_ID_VIST,idvisita);
            registro.put(DataBaseMaestro.TABLE_RESPUESTAS_TEXT_RESP,beneficiario.getDocumento());
            registro.put(DataBaseMaestro.TABLE_RESPUESTAS_PREG_TIPO,Pregunta.TIPO.LISTA_HOGAR.getCodigo());
            registro.put(DataBaseMaestro.TABLE_RESPUESTAS_BENEFICIARIODOC,beneficiarioDoc);
            data.insertarResgistro(DataBaseMaestro.TABLE_RESPUESTAS, registro);
            registro.clear();
            registro.put(DataBaseMaestro.TABLE_BENEFICIARIO_FAMILY,1);
            data.updateRegistros(DataBaseMaestro.TABLE_BENEFICIARIO, registro, DataBaseMaestro.TABLE_BENEFICIARIO_DOCUMENTO + "='"+beneficiario.getDocumento()+"'",null);

            registro.clear();
        }
    }


    public ArrayList<Beneficiario> getListaHogarResp(int codseccion,long idvisita){
        ArrayList<Beneficiario>miembros = new ArrayList<>();
        String CONSULTA = "SELECT b." + DataBaseMaestro.TABLE_BENEFICIARIO_DOCUMENTO + "," +
                                 "b." + DataBaseMaestro.TABLE_BENEFICIARIO_NOMBRE + "," +
                                 "b." + DataBaseMaestro.TABLE_BENEFICIARIO_APELLIDO + " " +
                          "FROM " + DataBaseMaestro.TABLE_RESPUESTAS + " r," +
                                    DataBaseMaestro.TABLE_BENEFICIARIO + " b " +
                          "WHERE r." + DataBaseMaestro.TABLE_RESPUESTAS_TEXT_RESP + "="+
                                "b." + DataBaseMaestro.TABLE_BENEFICIARIO_DOCUMENTO+" AND " +
                                "r." + DataBaseMaestro.TABLE_RESPUESTAS_ID_PREG + "=" + codseccion +" AND " +
                                "r." + DataBaseMaestro.TABLE_RESPUESTAS_ID_VIST + "=" + idvisita + ";";
        Cursor cursor = data.consultaSql(CONSULTA,null);
        if(cursor.moveToFirst()){
            do{
                miembros.add(new Beneficiario(cursor.getString(1),cursor.getString(2),cursor.getString(0),"","",null,0,0,null,false));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return miembros;
    }



    public void borrarRespuestas(ArrayList<Pregunta> preguntas,long idvisita){
        for(Pregunta pregunta:preguntas){
            data.eliminarRegistros(DataBaseMaestro.TABLE_RESPUESTAS,
                                   DataBaseMaestro.TABLE_RESPUESTAS_ID_VIST+"="+idvisita+" AND "+
                                   DataBaseMaestro.TABLE_RESPUESTAS_ID_PREG+"="+pregunta.getIdpregunta(),null);
           // borrarCapturasByVisita(idvisita);
        }
    }


    public void borrarVisitaById(long idvisita){
        data.eliminarRegistros(DataBaseMaestro.TABLE_VISITA, DataBaseMaestro.TABLE_VISITA_ID + "=" + idvisita, null);
    }


    public void borrarBeneficiarioByCed(String documento){
        data.eliminarRegistros(DataBaseMaestro.TABLE_BENEFICIARIO, DataBaseMaestro.TABLE_BENEFICIARIO_DOCUMENTO + "='" + documento + "'", null);
    }


    public void borrarCapturasByVisita(long idvisita){
        data.eliminarRegistros(DataBaseMaestro.TABLE_CAPTURAS, DataBaseMaestro.TABLE_CAPTURAS_IDVISITA + "=" + idvisita, null);
    }


    public void borrarCapturasByName(String name){
        data.eliminarRegistros(DataBaseMaestro.TABLE_CAPTURAS, DataBaseMaestro.TABLE_CAPTURAS_PATH + "='" + name +"'", null);
    }


    public void borrarAdjuntosByVisita(long idvisita){
        data.eliminarRegistros(DataBaseMaestro.TABLE_ADJ, DataBaseMaestro.TABLE_ADJ_IDVISITA + "=" + idvisita, null);
    }


    public void borrarAdjuntosByName(String name){
        data.eliminarRegistros(DataBaseMaestro.TABLE_ADJ, DataBaseMaestro.TABLE_ADJ_PATH + "='" + name +"'", null);
    }


    public void borrarAdjuntosById(long id){
        data.eliminarRegistros(DataBaseMaestro.TABLE_ADJ, DataBaseMaestro.TABLE_ADJ_ID + "=" + id, null);
    }


    public void finalizarVisita(long idvisita,long tiempo){
        inicializarContent();
        registro.put(DataBaseMaestro.TABLE_VISITA_EST, 1);
        registro.put(DataBaseMaestro.TABLE_VISITA_TIME,tiempo);
        registro.put(DataBaseMaestro.TABLE_VISITA_HORAFIN,FechaUtil.getFechaActual());
        String where = DataBaseMaestro.TABLE_VISITA_ID+"="+idvisita;
        data.actualizarRegistro(DataBaseMaestro.TABLE_VISITA, registro, where, null);
    }


    public void addCoordenadasVisita(long idvisita,Coordenadas coordenadas){
        inicializarContent();
        registro.put(DataBaseMaestro.TABLE_VISITA_LONG,coordenadas.getLongitud());
        registro.put(DataBaseMaestro.TABLE_VISITA_LAT,coordenadas.getLatitud());
        registro.put(DataBaseMaestro.TABLE_VISITA_PRES,coordenadas.getPrecision());
        String where = DataBaseMaestro.TABLE_VISITA_ID+"="+idvisita;
        data.actualizarRegistro(DataBaseMaestro.TABLE_VISITA, registro, where, null);
    }


    public void addAdjuntos(ArrayList<String> adjuntos,long idvisita,int estado){
        inicializarContent();
        /*eEcorre el arrayList de adjuntos y los inserta en la base de datos*/
        for(String adjunto:adjuntos){
            registro.put(DataBaseMaestro.TABLE_ADJ_IDVISITA,idvisita);
            registro.put(DataBaseMaestro.TABLE_ADJ_PATH,adjunto);
            registro.put(DataBaseMaestro.TABLE_ADJ_FECHA, FechaUtil.getFechaActual());
            registro.put(DataBaseMaestro.TABLE_ADJ_EST, estado);
            data.insertarResgistro(DataBaseMaestro.TABLE_ADJ, registro);
            registro.clear();
        }
    }


    public void addCapturas2(ArrayList<Capturas> capturas, long idvisita, String docbeneficiario, int estado){
        inicializarContent();
        int numero = 1;
        /*Recorre el arrayList de capturas insertandolas en la BD*/
        for(Capturas captura:capturas){
            registro.put(DataBaseMaestro.TABLE_CAPTURAS_IDVISITA,idvisita);
            registro.put(DataBaseMaestro.TABLE_CAPTURAS_DOCBENEFICIARIO,docbeneficiario);
            registro.put(DataBaseMaestro.TABLE_CAPTURAS_PATH,captura.getPath());
            registro.put(DataBaseMaestro.TABLE_CAPTURAS_FECHA,FechaUtil.getFechaActual());
            registro.put(DataBaseMaestro.TABLE_CAPTURAS_NUMERO,numero);
            registro.put(DataBaseMaestro.TABLE_CAPTURAS_ORIGEN,captura.getOrigen());
            registro.put(DataBaseMaestro.TABLE_CAPTURAS_EST,estado);
            registro.put(DataBaseMaestro.TABLE_CAPTURAS_HASH,ImageFileUtil.getHashOfFile(new File(captura.getPath())));
            data.insertarResgistro(DataBaseMaestro.TABLE_CAPTURAS, registro);
            registro.clear();
            numero++;
        }
    }


    public ArrayList<Capturas> getCapturas(long idvisita){
        ArrayList<Capturas> capturas = new ArrayList<>();
        String campos[] = new String[]{DataBaseMaestro.TABLE_CAPTURAS_PATH,
                                       DataBaseMaestro.TABLE_CAPTURAS_ORIGEN};
        String where = DataBaseMaestro.TABLE_CAPTURAS_IDVISITA+"="+idvisita;
        Cursor cursor = data.consultar(DataBaseMaestro.TABLE_CAPTURAS, campos, where, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                capturas.add(new Capturas(cursor.getString(0),cursor.getInt(1)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return capturas;
    }

    public ArrayList<Capturas> getCapturas(){
        ArrayList<Capturas> capturas = new ArrayList<>();
        String campos[] = new String[]{DataBaseMaestro.TABLE_CAPTURAS_PATH,
                                       DataBaseMaestro.TABLE_CAPTURAS_ORIGEN};
        Cursor cursor = data.consultar(DataBaseMaestro.TABLE_CAPTURAS, campos,null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                capturas.add(new Capturas(cursor.getString(0),cursor.getInt(1)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return capturas;
    }

    public Capturas getCapturaByName(String nombre){

        String campos[] = new String[]{DataBaseMaestro.TABLE_CAPTURAS_PATH,
                                       DataBaseMaestro.TABLE_CAPTURAS_ORIGEN,
                                       DataBaseMaestro.TABLE_CAPTURAS_HASH,
                                       DataBaseMaestro.TABLE_CAPTURAS_EST,
                                       DataBaseMaestro.TABLE_CAPTURAS_SYNC};

        String where = DataBaseMaestro.TABLE_CAPTURAS_PATH + "='" + nombre + "'";
        Cursor cursor = data.consultar(DataBaseMaestro.TABLE_CAPTURAS, campos, where, null, null, null, null);
        Capturas capturas = null;
        if(cursor.moveToFirst()){
            capturas = new Capturas(cursor.getString(0),
                                    cursor.getInt(1),
                                    cursor.getString(2),
                                    cursor.getInt(3),
                                    cursor.getInt(4));
        }
        cursor.close();
        return capturas;
    }


    public ArrayList<String> getAdjuntos(long idvisita){
        ArrayList<String> adjuntos = new ArrayList<>();
        String campos[] = new String[]{DataBaseMaestro.TABLE_ADJ_PATH};
        String where = DataBaseMaestro.TABLE_ADJ_IDVISITA+"="+idvisita;
        Cursor cursor = data.consultar(DataBaseMaestro.TABLE_ADJ, campos, where, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                adjuntos.add(cursor.getString(0));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return adjuntos;
    }


    public ArrayList<Reporte> getReporte(){
        String CONSULTA = "SELECT * FROM " + DataBaseMaestro.VIEW_REPORTE_RELEVADO;
        Cursor cursor = data.consultaSql(CONSULTA, null);
        ArrayList<Reporte>listReporte=new ArrayList<>();
        if(cursor.moveToFirst()){
            Reporte reporte;
            do{
                reporte = new Reporte(cursor.getLong(10),
                                      cursor.getString(0),
                                      cursor.getString(11),
                                      cursor.getString(2),
                                      cursor.getInt(3),
                                      cursor.getString(4),
                                      new Coordenadas(cursor.getString(5),cursor.getString(6),0),
                                      Reporte.Estado.findByCodigo(cursor.getInt(8)),
                                      Reporte.Envio.findByCodigo(cursor.getInt(9)),
                                      cursor.getString(1),
                                      Beneficiario.Estado.findByCodigo(cursor.getInt(7)),
                                      cursor.getString(12),
                                      cursor.getString(13));
                reporte.setTipo_proyecto(getTipoProyecto(cursor.getInt(3)));
                listReporte.add(reporte);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return listReporte;
    }

    private int getTipoProyecto(int cod_motivo){
        String CONSULTA = "SELECT p." + DataBaseMaestro.TABLE_PROYECTOS_TIPO + " " +
                          "FROM " + DataBaseMaestro.TABLE_PROYECTOS + " p," +
                                    DataBaseMaestro.TABLE_MOTIVOS + " m " +
                          "WHERE p." + DataBaseMaestro.TABLE_PROYECTOS_COD + "=" +
                                "m." + DataBaseMaestro.TABLE_MOTIVOS_ID_PROYECTO + " AND " +
                                "m." + DataBaseMaestro.TABLE_MOTIVOS_CODIGO  + "=" + cod_motivo ;
        Cursor cursor = data.consultaSql(CONSULTA,null);
        int result = 0;
        if(cursor.moveToFirst()){
            result = cursor.getInt(0);
        }
        cursor.close();
        return result;
    }

    public TotalRelevado getTotalAdjuntos(){
        int relevado = 0;
        int enviado = 0;

        String SELECT = "SELECT _id ";
        String FROM = "FROM Adjuntos ";
        String WHERE = "WHERE estado=1 ";
        Cursor cursor = data.consultaSql(SELECT + FROM, null);
        if(cursor.moveToFirst()){
            relevado = cursor.getCount();
        }
        cursor.close();
        cursor = data.consultaSql(SELECT + FROM + WHERE, null);
        if(cursor.moveToFirst()){
            enviado = cursor.getCount();
        }
        cursor.close();
        return new TotalRelevado(relevado,enviado);
    }


    public TotalRelevado getTotalCapturas(){

        int relevado = 0;
        int enviado = 0;

        String SELECT = "SELECT _id ";
        String FROM = "FROM Capturas ";
        String WHERE = "WHERE estado=1 OR estado=2 ";
        Cursor cursor = data.consultaSql(SELECT + FROM, null);
        if(cursor.moveToFirst()){
            relevado = cursor.getCount();
        }
        cursor.close();
        cursor = data.consultaSql(SELECT + FROM + WHERE, null);
        if(cursor.moveToFirst()){
            enviado = cursor.getCount();
        }
        cursor.close();
        return new TotalRelevado(relevado,enviado);
    }


   private ArrayList<String> recuperarRespuesta(long idvisita,int idpregunta,int nrosubform){
       ArrayList<String> respuestas = new ArrayList<>();
       String campos[] = new String[]{DataBaseMaestro.TABLE_RESPUESTAS_ID_POSIBLE_RESP,//0
                                      DataBaseMaestro.TABLE_RESPUESTAS_TEXT_RESP,      //1
                                      DataBaseMaestro.TABLE_RESPUESTAS_PREG_TIPO};     //2
       String where = DataBaseMaestro.TABLE_RESPUESTAS_ID_VIST+"="+idvisita+" AND "+
                      DataBaseMaestro.TABLE_RESPUESTAS_ID_PREG+"="+idpregunta + " AND " +
                      DataBaseMaestro.TABLE_RESPUESTAS_NRO_SUBFORM + "=" + nrosubform;
       Cursor cursor = data.consultar(DataBaseMaestro.TABLE_RESPUESTAS, campos, where, null, null, null, null);
       if(cursor.moveToFirst()){
           do{
              if(cursor.getInt(0) == 0){
                  if(cursor.getInt(2) == 14){
                      respuestas.add(ImageFileUtil.getNameImagen(cursor.getString(1)));
                  }else{
                      respuestas.add(cursor.getString(1));
                  }
              }else{
                respuestas.add(cursor.getString(0));
              }
           }while (cursor.moveToNext());
       }
       cursor.close();
       return respuestas;
   }


   public ArrayList<SendData> getArrayPendienteVisita(){
       ArrayList<SendData> arrayList = new ArrayList<>();
       String consulta = "SELECT * FROM "+DataBaseMaestro.VIEW_VISITAS_PENDIENTES_ARR;
       Cursor cursor = data.consultaSql(consulta, null);
       if(cursor.moveToFirst()){
           do{
               Map<String,String> parametros = new HashMap<>();
               parametros.put("documento",cursor.getString(0));
               parametros.put("nombre",cursor.getString(1));
               parametros.put("apellido",cursor.getString(2));
               parametros.put("tipobeneficiario",cursor.getString(16));
               parametros.put("esjefe",cursor.getString(17));
               parametros.put("longitud",cursor.getString(3));
               parametros.put("latitud",cursor.getString(4));
               parametros.put("presicion",cursor.getString(5));
               parametros.put("horaini",cursor.getString(6));
               parametros.put("horafin",cursor.getString(7));
               parametros.put("duracion",cursor.getString(8));
               parametros.put("observacion",cursor.getString(9));
               parametros.put("motivo",cursor.getString(10));
               parametros.put("proyecto",cursor.getString(11));
               parametros.put("usuario",cursor.getString(13));
               parametros.put("distrito",cursor.getString(14));
               parametros.put("departamento",cursor.getString(15));
               parametros.put("localidad",cursor.getString(18));
               parametros.put("capturas", getImgenesJson(cursor.getString(12)));
               parametros.put("adjuntos", getAdjuntosPathSend(cursor.getString(12)));
               parametros.put("id_key", cursor.getString(19));
               parametros.put("original", cursor.getString(20));
               parametros.put("resultado", getResultadoSend(cursor.getLong(12)));
               arrayList.add(new SendData(cursor.getString(12), parametros, SendData.TIPO.VISITA));
           }while (cursor.moveToNext());
       }
       cursor.close();
       return arrayList;
   }

   private String getImgenesJson(String idvisita){
       String campos[] = new String[]{DataBaseMaestro.TABLE_CAPTURAS_PATH,
                                      DataBaseMaestro.TABLE_CAPTURAS_ORIGEN,
                                      DataBaseMaestro.TABLE_CAPTURAS_HASH};
       String where = DataBaseMaestro.TABLE_CAPTURAS_IDVISITA + "=" + idvisita;
       Cursor cursor = data.consultar(DataBaseMaestro.TABLE_CAPTURAS, campos, where, null, null, null, null);
       JSONArray jsonArray = new JSONArray();
       JSONObject jsonObject;
       if(cursor.moveToFirst()){
           do{
               jsonObject = new JSONObject();
               try {
                   jsonObject.put("imagen",ImageFileUtil.getNameImagen(cursor.getString(0)));
                   jsonObject.put("origen",cursor.getInt(1));
                   jsonObject.put("hash",cursor.getString(2));
                   jsonArray.put(jsonObject);
               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }while (cursor.moveToNext());
       }
       return jsonArray.toString();
   }

    private String getAdjuntosPathSend(String idvisita){
        String adjuntos = "";
        String campos[] = new String[]{DataBaseMaestro.TABLE_ADJ_PATH};//0
        String where = DataBaseMaestro.TABLE_ADJ_IDVISITA+"=" + idvisita;
        Cursor cursor = data.consultar(DataBaseMaestro.TABLE_ADJ, campos, where, null, null, null, null);
        if(cursor.moveToFirst()){
            /*Recorre el cursor concatenando los nombre de archivos adjuntos */
            do{
                adjuntos=adjuntos+ImageFileUtil.getNameImagen(cursor.getString(0))+"|";
            }while (cursor.moveToNext());
        }
        cursor.close();
        return adjuntos;
    }

    private String getResultadoSend(long idvisita){
        JSONArray jsonArray = new JSONArray();
        String select = "SELECT DISTINCT "+DataBaseMaestro.TABLE_RESPUESTAS_ID_PREG+"," +  //0
                                           DataBaseMaestro.TABLE_RESPUESTAS_ID_VIST+","+   //1
                                           DataBaseMaestro.TABLE_RESPUESTAS_PREG_TIPO+","+ //2
                                           DataBaseMaestro.TABLE_RESPUESTAS_BENEFICIARIODOC+"," +//3
                                           DataBaseMaestro.TABLE_RESPUESTAS_NRO_SUBFORM + " ";   //4
        String from = "FROM "+DataBaseMaestro.TABLE_RESPUESTAS+" ";
        String where = "WHERE "+DataBaseMaestro.TABLE_RESPUESTAS_ID_VIST+"="+idvisita+" ";
        String group = "GROUP BY "+DataBaseMaestro.TABLE_RESPUESTAS_PREG_TIPO+","
                                  +DataBaseMaestro.TABLE_RESPUESTAS_ID_PREG+","
                                  +DataBaseMaestro.TABLE_RESPUESTAS_BENEFICIARIODOC + ","
                                  +DataBaseMaestro.TABLE_RESPUESTAS_NRO_SUBFORM;
        Log.i("Consulta",select + from + where + group);
        Log.i("Consulta",select + from + where + group);
        Cursor cursor = data.consultaSql(select + from + where + group, null);
        if(cursor.moveToFirst()){
            do{
                try {
                    JSONObject jsonObject =new JSONObject();
                    jsonObject.put("idpegunta",cursor.getString(0));
                    jsonObject.put("tipo",cursor.getString(2));
                    jsonObject.put("respuesta",getResultResp(cursor.getLong(1),cursor.getInt(0),cursor.getInt(4)));
                    jsonObject.put("beneficiario",cursor.getString(3));
                    jsonObject.put("nro_subform",cursor.getInt(4));
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        return jsonArray.toString();
    }

    private String getResultResp(long idvisita,int idpregunta,int nrosubform){
        ArrayList<String>respuestas = recuperarRespuesta(idvisita,idpregunta,nrosubform);
        String cadena = "";
        if(respuestas != null && !respuestas.isEmpty()){
            for(String result:respuestas){
                cadena = cadena+result +"|";
            }
        }
        return cadena;
    }

   public void changeEstadoEnvioVisita(int estado,String idvisita){
       ContentValues values=new ContentValues();
       values.put(DataBaseMaestro.TABLE_VISITA_ENV,estado);
       data.actualizarRegistro(DataBaseMaestro.TABLE_VISITA,values,DataBaseMaestro.TABLE_VISITA_ID+"="+idvisita,null);
   }

   public void imagenNoValida(long idregistro,SendData.TIPO tipo){
       String campos[];
       String where;
       Cursor cursor = null;
       switch (tipo.getCodigo()){
           case 1:
               campos = new String[]{DataBaseMaestro.TABLE_CAPTURAS_NRO_ENVIOS};
               where = DataBaseMaestro.TABLE_CAPTURAS_ID + "=" + idregistro;
               cursor = data.consultar(DataBaseMaestro.TABLE_CAPTURAS, campos,where, null, null, null, null);
           break;
           case 7:
               campos = new String[]{DataBaseMaestro.TABLE_CAPTURA_EVENTOS_NRO_ENVIOS};
               where = DataBaseMaestro.TABLE_CAPTURA_EVENTOS_ID + "=" + idregistro;
               cursor = data.consultar(DataBaseMaestro.TABLE_CAPTURA_EVENTOS, campos,where, null, null, null, null);
           break;
       }

       if(cursor.moveToFirst()){
            if(cursor.getInt(0) < 10){
                int cont = cursor.getInt(0) + 1;
                ContentValues contentValues = new ContentValues();
                contentValues.put("nro_envios",cont);
                if(tipo.getCodigo() == 1){
                    data.actualizarRegistro(DataBaseMaestro.TABLE_CAPTURAS,contentValues, DataBaseMaestro.TABLE_CAPTURAS_ID + "=" + idregistro,null);
                }else{
                    data.actualizarRegistro(DataBaseMaestro.TABLE_CAPTURA_EVENTOS,contentValues, DataBaseMaestro.TABLE_CAPTURA_EVENTOS_ID + "=" + idregistro,null);
                }
            }else{
                if(tipo.getCodigo() == 1){
                    changeEstadoEnvio(2,Long.toString(idregistro), DataBaseMaestro.TABLE_CAPTURAS);
                }else{
                    changeEstadoEnvio(2,Long.toString(idregistro), DataBaseMaestro.TABLE_CAPTURA_EVENTOS);
                }
            }
       }
   }

   public ArrayList<SendData> getPendienteArrCapturas(){
       ArrayList<SendData> arrayList = new ArrayList<>();
       String CONSULTA = "SELECT * FROM "+DataBaseMaestro.VIEW_CAPTURAS_PENDIENTES_ARR;
       Cursor cursor = data.consultaSql(CONSULTA, null);
       if(cursor.moveToFirst()){
           do {
               File file = new File(cursor.getString(2));
               if(file.exists()){
                   Map<String,String> parametros = new HashMap<>();
                   parametros.put("encuestado",cursor.getString(1));
                   //parametros.put("archivo", ImageFileUtil.getStringImage(cursor.getString(2)));
                   parametros.put("archivo",cursor.getString(2));
                   parametros.put("nombre", ImageFileUtil.getNameImagen(cursor.getString(2)));
                   parametros.put("fecha", cursor.getString(3));
                   parametros.put("numero", cursor.getString(4));
                   arrayList.add(new SendData(cursor.getString(0), parametros, SendData.TIPO.CAPTURAS));
               }else{
                   //data.eliminarRegistros(DataBaseMaestro.TABLE_CAPTURAS,
                   //        DataBaseMaestro.TABLE_CAPTURAS_ID+"="+cursor.getString(0),null);
                   changeEstadoEnvio(2,cursor.getString(0), DataBaseMaestro.TABLE_CAPTURAS);
               }
           }while (cursor.moveToNext());
       }
       cursor.close();
       return arrayList;
   }

    public ArrayList<SendData> getPendienteArrCapturasEvt(){
        ArrayList<SendData> arrayList = new ArrayList<>();
        String CONSULTA = "SELECT * FROM "+DataBaseMaestro.VIEW_CAPTURAS_EVENTO_ARR;
        Cursor cursor = data.consultaSql(CONSULTA, null);
        if(cursor.moveToFirst()){
            do {
                File file = new File(cursor.getString(2));
                if(file.exists()){
                    Map<String,String> parametros = new HashMap<>();
                    parametros.put("encuestado",cursor.getString(1));
                    //parametros.put("archivo", ImageFileUtil.getStringImage(cursor.getString(2)));
                    parametros.put("archivo", cursor.getString(2));
                    parametros.put("nombre", ImageFileUtil.getNameImagen(cursor.getString(2)));
                    parametros.put("fecha", cursor.getString(3));
                    arrayList.add(new SendData(cursor.getString(0), parametros, SendData.TIPO.EVENTO_CAPTURAS));
                }else{
                    data.eliminarRegistros(DataBaseMaestro.TABLE_CAPTURA_EVENTOS,
                                           DataBaseMaestro.TABLE_CAPTURA_EVENTOS_ID+"="+cursor.getString(0),null);
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }

    public void changeEstadoEnvio(int estado,String id,String table){
        ContentValues values=new ContentValues();
        values.put("estado", estado);
        data.actualizarRegistro(table,values,"_id="+id,null);
    }

    public void changeSync(int estado,String id,String table){
        ContentValues values=new ContentValues();
        values.put("sync", estado);
        data.actualizarRegistro(table,values,"_id="+id,null);
    }

    public void eliminarImagen(String id,String tabla){
        String campos[] = new String[]{DataBaseMaestro.TABLE_CAPTURAS_PATH};//0
        String where = DataBaseMaestro.TABLE_CAPTURAS_ID+"="+id;
        Cursor cursor = data.consultar(tabla, campos, where, null, null, null, null);
        if(cursor.moveToFirst()){
            File file = new File(cursor.getString(0));
            if(file.exists()){
                file.delete();
            }
        }
        cursor.close();
    }

    public void eliminarZip(String id){
        String campos[] = new String[]{DataBaseMaestro.TABLE_ADJ_PATH};//0
        String where = DataBaseMaestro.TABLE_ADJ_ID+"="+id;
        Cursor cursor = data.consultar(DataBaseMaestro.TABLE_ADJ, campos, where, null, null, null, null);
        if(cursor.moveToFirst()){
            File file = new File(cursor.getString(0)+".zip");
            if(file.exists()){
                file.delete();
            }
        }
        cursor.close();
    }

    public ArrayList<SendData> getArrayUbicacionesPendientes(){
        ArrayList<SendData>arrayList = new ArrayList<>();
        String consulta = "SELECT * FROM "+DataBaseMaestro.VIEW_UBICACIONES_PENDIENTES_ARR;
        Cursor cursor = data.consultaSql(consulta, null);
        if (cursor.moveToFirst()){
            do {
                Map<String,String> parametros = new HashMap<>();
                parametros.put("longitud",cursor.getString(1));
                parametros.put("latitud",cursor.getString(2));
                parametros.put("altitud",cursor.getString(3));
                parametros.put("precision",cursor.getString(4));
                parametros.put("proveedor",cursor.getString(5));
                parametros.put("horaobtenido",cursor.getString(6));
                parametros.put("horaguardado",cursor.getString(7));
                parametros.put("bateria",cursor.getString(8));
                parametros.put("tipo",cursor.getString(9));
                parametros.put("usuario", cursor.getString(10));
                arrayList.add(new SendData(cursor.getString(0),parametros, SendData.TIPO.UBICACION));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }

    public ArrayList<SendData> getArrayDataTelefonoPendiente(){
        ArrayList<SendData> arrayList = new ArrayList<>();
        String consulta = "SELECT * FROM "+DataBaseMaestro.VIEW_OPERADORA_PENDIENTES_ARR;
        Cursor cursor = data.consultaSql(consulta, null);
        if(cursor.moveToFirst()){
            do{
                Map<String,String> parametros = new HashMap<>();
                parametros.put("serialsim",cursor.getString(2));
                parametros.put("iso",cursor.getString(3));
                parametros.put("operadora",cursor.getString(4));
                parametros.put("fecha",cursor.getString(5));
                parametros.put("usuario",cursor.getString(6));
                parametros.put("imei",CellUtils.getImeiCell(context));
                parametros.put("fabricante", Build.MANUFACTURER);
                parametros.put("modelo", Build.MODEL);
                parametros.put("androidid",CellUtils.getAndroidId(context));
                parametros.put("osversion",Build.VERSION.RELEASE);
                arrayList.add(new SendData(cursor.getString(0),parametros, SendData.TIPO.TELEFONIA));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }

    public ArrayList<SendData>getArrrayAdjuntosPendientes(){
        ArrayList<SendData>arrayList = new ArrayList<>();
        String consulta = "SELECT * FROM "+DataBaseMaestro.VIEW_ADJUNTOS_PENDIENTES_ARR;
        Cursor cursor = data.consultaSql(consulta, null);
        if(cursor.moveToFirst()){

            do{
                String pathzip = "";
                if(ImageFileUtil.fileExist(cursor.getString(1))){
                    pathzip=cursor.getString(1)+".zip";
                    ImageFileUtil.comprimir(new String[]{cursor.getString(1)}, pathzip);
                    Map<String,String> parametros = new HashMap<>();
                    parametros.put("archivo",ImageFileUtil.encodeFileToBase64Binary(pathzip));
                    parametros.put("nombre",ImageFileUtil.getNameImagen(pathzip));
                    parametros.put("encuestado", cursor.getString(3));
                    arrayList.add(new SendData(cursor.getString(0),parametros, SendData.TIPO.ADJUNTOS));
                }else{
                    borrarAdjuntosById(cursor.getLong(0));
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }


    public ArrayList<SendData>getArrayRecorridoPendiente(){
        ArrayList<SendData>arrayList = new ArrayList<>();
        String consulta = "SELECT * FROM "+DataBaseMaestro.TABLE_RECORRIDO+" WHERE estado=0 AND fin IS NOT NULL";
        Cursor cursor = data.consultaSql(consulta, null);
        if(cursor.moveToFirst()){

            do{
                Map<String,String> parametros = new HashMap<>();
                parametros.put("usuario",cursor.getString(4));
                parametros.put("inicio",cursor.getString(1));
                parametros.put("fin", cursor.getString(2));
                parametros.put("duracion", cursor.getString(3));
                arrayList.add(new SendData(cursor.getString(0), parametros, SendData.TIPO.RECORRIDO));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }


    private String getImgenesEvtJson(String idevento){
        String campos[] = new String[]{DataBaseMaestro.TABLE_CAPTURA_EVENTOS_PATH,
                                       DataBaseMaestro.TABLE_CAPTURA_EVENTOS_HASH};
        String where = DataBaseMaestro.TABLE_CAPTURA_EVENTOS_IDEVENTO + "=" + idevento;
        Cursor cursor = data.consultar(DataBaseMaestro.TABLE_CAPTURA_EVENTOS, campos, where, null, null, null, null);
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;
        if (cursor.moveToFirst()){
            do{
                jsonObject = new JSONObject();
                try {
                    jsonObject.put("imagen",ImageFileUtil.getNameImagen(cursor.getString(0)));
                    jsonObject.put("hash",cursor.getString(1));
                    jsonArray.put(jsonObject);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        return jsonArray.toString();
    }


    public ArrayList<SendData>getArrayEventosPendiente(){
        ArrayList<SendData>arrayList = new ArrayList<>();
        String consulta = "SELECT * FROM "+DataBaseMaestro.TABLE_EVENTOS+" WHERE estado=0 ";
        Cursor cursor = data.consultaSql(consulta, null);
        if (cursor.moveToFirst()){

            do{
                Map<String,String> parametros = new HashMap<>();
                parametros.put("usuario",cursor.getString(6));
                parametros.put("longitud",cursor.getString(2));
                parametros.put("latitud", cursor.getString(3));
                parametros.put("presicion", cursor.getString(4));
                parametros.put("descripcion", cursor.getString(5));
                parametros.put("capturas", getImgenesEvtJson(cursor.getString(0)));
                parametros.put("fecha", cursor.getString(1));
                arrayList.add(new SendData(cursor.getString(0), parametros, SendData.TIPO.EVENTO));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }


    public ArrayList<SendData> getArrayEventosDevice(){
        ArrayList<SendData>arrayList = new ArrayList<>();
        String consulta = "SELECT * FROM "+DataBaseMaestro.TABLE_EVTCELL+" WHERE estado=0 ";
        Cursor cursor = data.consultaSql(consulta, null);
        if(cursor.moveToFirst()){
            do {
                Map<String,String> parametros = new HashMap<>();
                parametros.put("usuario", cursor.getString(3));
                parametros.put("descripcion", cursor.getString(1));
                parametros.put("deviceid", CellUtils.getAndroidId(context));
                parametros.put("fecha", cursor.getString(2));
                arrayList.add(new SendData(cursor.getString(0),parametros, SendData.TIPO.EVENTO_DEV));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }

    public ArrayList<SendData>getArrPendientesVerificacion(){
        ArrayList<SendData>arrCedulas = new ArrayList<>();
        String SELECT = "SELECT "+DataBaseMaestro.TABLE_BENEFICIARIO_ID+"," +
                                  DataBaseMaestro.TABLE_BENEFICIARIO_DOCUMENTO;
        String FROM = " FROM "+DataBaseMaestro.TABLE_BENEFICIARIO;
        String WHERE = " WHERE "+DataBaseMaestro.TABLE_BENEFICIARIO_EST+"="+Beneficiario.Estado.NUEVO_NO_VALIDADO.getCodigo();
        Cursor cursor = data.consultaSql(SELECT+FROM+WHERE,null);
        if(cursor.moveToFirst()){
            do {
                Map<String,String> parametros = new HashMap<>();
                parametros.put("ci", cursor.getString(1));
                arrCedulas.add(new SendData(cursor.getString(0),parametros, SendData.TIPO.INDENTIFICACIONES));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return arrCedulas;
    }


    public ArrayList<SendData>getArrCorreccionesSyncPendientes(){
        ArrayList<SendData>arr_syn_correciones = new ArrayList<>();
        String campos[] = new String[]{DataBaseMaestro.TABLE_CORRECCIONES_ID,
                                       DataBaseMaestro.TABLE_CORRECCIONES_REL_ORIGIN,
                                       DataBaseMaestro.TABLE_CORRECCIONES_REL_FINAL};
        String WHERE = DataBaseMaestro.TABLE_CORRECCIONES_SYNC + "=0";
        Cursor cursor = data.consultar(DataBaseMaestro.TABLE_CORRECCIONES,campos,WHERE,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                Map<String,String> parametros = new HashMap<>();
                parametros.put("relevamiento_origen", cursor.getString(1));
                parametros.put("relevamiento_final", cursor.getString(2));
                arr_syn_correciones.add(new SendData(cursor.getString(0),parametros,SendData.TIPO.CORRECCION));
            }while (cursor.moveToNext());
            cursor.close();
        }
        return arr_syn_correciones;
    }


    public ArrayList<SendData>getArrCapturasInvalidas(){
        ArrayList<SendData>arr_capturas_invalidas = new ArrayList<>();
        String campos[] = new String[]{DataBaseMaestro.TABLE_CAPTURAS_ID,
                                       DataBaseMaestro.TABLE_CAPTURAS_PATH};
        String WHERE = DataBaseMaestro.TABLE_CAPTURAS_EST + "=2 AND " +
                       DataBaseMaestro.TABLE_CAPTURAS_SYNC + "=0";
        Cursor cursor = data.consultar(DataBaseMaestro.TABLE_CAPTURAS,campos,WHERE,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                Map<String,String> parametros = new HashMap<>();
                parametros.put("captura", ImageFileUtil.getNameImagen(cursor.getString(1)));
                parametros.put("tipo", "0");
                arr_capturas_invalidas.add(new SendData(cursor.getString(0),parametros,SendData.TIPO.CAPTURA_INVALIDA));
            }while (cursor.moveToNext());
            cursor.close();
        }
        return arr_capturas_invalidas;
    }


    public ArrayList<SendData>getArrCapturasEvtInvalidas(){
        ArrayList<SendData>arr_capturas_evt_invalidas = new ArrayList<>();
        String campos[] = new String[]{DataBaseMaestro.TABLE_CAPTURA_EVENTOS_ID,
                                       DataBaseMaestro.TABLE_CAPTURA_EVENTOS_PATH};
        String WHERE = DataBaseMaestro.TABLE_CAPTURA_EVENTOS_envio + "=2 AND " +
                       DataBaseMaestro.TABLE_CAPTURA_EVENTOS_SYNC + "=0";
        Cursor cursor = data.consultar(DataBaseMaestro.TABLE_CAPTURAS,campos,WHERE,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                Map<String,String> parametros = new HashMap<>();
                parametros.put("captura", ImageFileUtil.getNameImagen(cursor.getString(1)));
                parametros.put("tipo", "0");
                arr_capturas_evt_invalidas.add(new SendData(cursor.getString(0),parametros,SendData.TIPO.EVENTO_CAPTURA_INVALIDA));
            }while (cursor.moveToNext());
            cursor.close();
        }
        return arr_capturas_evt_invalidas;
    }


    public void limpiarEnviados(){
        data.eliminarRegistros(DataBaseMaestro.TABLE_VISITA,DataBaseMaestro.TABLE_VISITA_ENV+"=1",null);
        data.eliminarRegistros(DataBaseMaestro.TABLE_CAPTURAS,DataBaseMaestro.TABLE_CAPTURAS_EST+"=1",null);
        data.eliminarRegistros(DataBaseMaestro.TABLE_ADJ,DataBaseMaestro.TABLE_ADJ_EST+"=1",null);
    }


    public void limpiarEnviadosEvent(){
        data.eliminarRegistros(DataBaseMaestro.TABLE_EVENTOS,DataBaseMaestro.TABLE_EVENTOS_envio+"=1",null);
        data.eliminarRegistros(DataBaseMaestro.TABLE_CAPTURA_EVENTOS,DataBaseMaestro.TABLE_CAPTURA_EVENTOS_envio+"=1",null);
    }


    public void limpiarProyectos(){
        data.eliminarRegistros(DataBaseMaestro.TABLE_PROYECTOS,null,null);
        data.eliminarRegistros(DataBaseMaestro.TABLE_MOTIVOS,null,null);
        data.eliminarRegistros(DataBaseMaestro.TABLE_SECCIONES,null,null);
        data.eliminarRegistros(DataBaseMaestro.TABLE_PREGUNTAS,null,null);
        data.eliminarRegistros(DataBaseMaestro.TABLE_POSRESPUESTAS,null,null);
        data.eliminarRegistros(DataBaseMaestro.TABLE_PREGUNTAS_CONDICION,null,null);
        data.eliminarRegistros(DataBaseMaestro.TABLE_SECCION_CONDICION,null,null);
        data.eliminarRegistros(DataBaseMaestro.TABLE_UBICACIONES,DataBaseMaestro.TABLE_UBICACIONES_EST+"=1 AND " +
                                                                 DataBaseMaestro.TABLE_UBICACIONES_TIPO + "<3 ",null);
    }


    public void limpiarBD() {
        Cursor cursor = data.consultaSql("SELECT name FROM sqlite_master WHERE type='table'", null);
        if(cursor.moveToFirst()){
            do {
                data.eliminarRegistros(cursor.getString(0),null,null);
            }while (cursor.moveToNext());
        }
        cursor.close();
    }


    public void importarBD(String pathimportar) throws ExcepcionReadBD {
        SQLiteDatabase dbimportar;
        try{
            dbimportar = SQLiteDatabase.openDatabase(pathimportar, null, SQLiteDatabase.OPEN_READWRITE);
            dbimportar.beginTransaction();
            Cursor cursor = dbimportar.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
            if(cursor.moveToFirst()){
                limpiarBD();
                Cursor registros;
                /*Recorre la estructura de una base de datos importando sus registros en la base de datos local*/
                do{
                    registros = dbimportar.rawQuery("SELECT * FROM "+cursor.getString(0),null);
                    insertarRegistroTabla(registros,cursor.getString(0));
                }while (cursor.moveToNext());
            }else{
                throw new ExcepcionReadBD(context.getString(R.string.toast_err_read_bd));
            }
            cursor.close();
        }catch (SQLiteException ex) {
            throw new ExcepcionReadBD(context.getString(R.string.toast_err_read_bd));
        }
    }

    private void insertarRegistroTabla(Cursor registros,String tabla){
        if(registros.moveToFirst()){
            ContentValues values = new ContentValues();
            /*Recorre el cursor para insertar sus datos en la base de datos local*/
            do{
                /*Recorre las columnas de un registro para generar el ContenValue de insercion*/
                for(int i=0;i<registros.getColumnCount();i++){
                    if(!registros.getColumnName(i).equals("_id")){
                        values.put(registros.getColumnName(i), registros.getString(i));
                    }
                }
                data.insertarResgistro(tabla,values);
                values.clear();
            }while(registros.moveToNext());
        }
    }


    public boolean hasPendientes(){
        String consulta = "SELECT * FROM "+DataBaseMaestro.VIEW_VISITAS_PENDIENTES_ARR;
        Cursor cursor = data.consultaSql(consulta, null);
        if (cursor.moveToFirst()){
            return true;
        }

        cursor.close();
        consulta = "SELECT * FROM "+DataBaseMaestro.VIEW_CAPTURAS_PENDIENTES_ARR;
        cursor = data.consultaSql(consulta, null);
        if (cursor.moveToFirst()){
            return true;
        }

        cursor.close();
        consulta = "SELECT * FROM "+DataBaseMaestro.VIEW_ADJUNTOS_PENDIENTES_ARR;
        cursor = data.consultaSql(consulta, null);
        if (cursor.moveToFirst()){
            return true;
        }

        cursor.close();
        return false;
    }


    public void addEvento(Evento evento){
        inicializarContent();
        registro.put(DataBaseMaestro.TABLE_EVENTOS_FECHA, evento.getFecha());
        registro.put(DataBaseMaestro.TABLE_EVENTOS_LON, evento.getCoordenadas().getLongitud());
        registro.put(DataBaseMaestro.TABLE_EVENTOS_LAT, evento.getCoordenadas().getLatitud());
        registro.put(DataBaseMaestro.TABLE_EVENTOS_PRES, evento.getCoordenadas().getPrecision());
        registro.put(DataBaseMaestro.TABLE_EVENTOS_descripcion, evento.getDescripcion());
        registro.put(DataBaseMaestro.TABLE_EVENTOS_USER, sessionData.getUsuario());
        long idevento = data.insertarResgistro(DataBaseMaestro.TABLE_EVENTOS,registro);
        inicializarContent();
        if(!evento.getCapturas().isEmpty()){
            addCapturaEvento(idevento,evento.getCapturas(),0);
        }
    }


    public void addCapturaEvento(long idevento,ArrayList<String>eventos,int estado){
        inicializarContent();
        for(String path:eventos){
            registro.put(DataBaseMaestro.TABLE_CAPTURA_EVENTOS_IDEVENTO,idevento);
            registro.put(DataBaseMaestro.TABLE_CAPTURA_EVENTOS_PATH,path);
            registro.put(DataBaseMaestro.TABLE_CAPTURA_EVENTOS_envio,estado);
            registro.put(DataBaseMaestro.TABLE_CAPTURA_EVENTOS_FECHA,FechaUtil.getFechaActual());
            registro.put(DataBaseMaestro.TABLE_CAPTURA_EVENTOS_HASH,ImageFileUtil.getHashOfFile(new File(path)));
            data.insertarResgistro(DataBaseMaestro.TABLE_CAPTURA_EVENTOS,registro);
            registro.clear();
        }
    }


    public ArrayList<String> getCapturasEventos(long idevento){
        ArrayList<String> capturas = new ArrayList<>();
        String campos[] = new String[]{DataBaseMaestro.TABLE_CAPTURA_EVENTOS_PATH};
        String where = DataBaseMaestro.TABLE_CAPTURA_EVENTOS_IDEVENTO+"="+idevento;
        Cursor cursor = data.consultar(DataBaseMaestro.TABLE_CAPTURA_EVENTOS, campos, where, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                capturas.add(cursor.getString(0));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return capturas;
    }


    public void borrarCapturasByEventos(long idevento){
        data.eliminarRegistros(DataBaseMaestro.TABLE_CAPTURA_EVENTOS, DataBaseMaestro.TABLE_CAPTURA_EVENTOS_IDEVENTO + "=" + idevento, null);
    }


    public ArrayList<Evento>getArrayEventos(){
        ArrayList<Evento>listeventos = new ArrayList<>();
        String campos[] = new String[]{DataBaseMaestro.TABLE_EVENTOS_descripcion,   //0
                                       DataBaseMaestro.TABLE_EVENTOS_LON,           //1
                                       DataBaseMaestro.TABLE_EVENTOS_LAT,           //2
                                       DataBaseMaestro.TABLE_EVENTOS_PRES,          //3
                                       DataBaseMaestro.TABLE_EVENTOS_FECHA,         //4
                                       DataBaseMaestro.TABLE_EVENTOS_ID,            //5
                                       DataBaseMaestro.TABLE_EVENTOS_envio};        //6
        Cursor cursor = data.consultar(DataBaseMaestro.TABLE_EVENTOS, campos, null, null, null, null, null);
        if(cursor.moveToNext()){
            do {
                Coordenadas coordenadas = new Coordenadas(cursor.getString(1),cursor.getString(2),cursor.getFloat(3));
                listeventos.add(new Evento(coordenadas,
                                           cursor.getString(0),
                                           getCapturasEventos(cursor.getLong(5)),
                                           cursor.getString(4),
                                           cursor.getInt(6)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return listeventos;
    }

    public ArrayList<Departamento>getArrayDepartametos(){
        ArrayList<Departamento>departamentos = new ArrayList<Departamento>();
        String campos[] = new String[]{DataBaseMaestro.TABLE_DEPARTAMENTOS_ID,
                                       DataBaseMaestro.TABLE_DEPARTAMENTOS_NOMBRE};
        Cursor cursor = data.consultar(DataBaseMaestro.TABLE_DEPARTAMENTOS,campos,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                departamentos.add(new Departamento(cursor.getString(0),cursor.getString(1),getArrayDistritos(cursor.getString(0))));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return departamentos;
    }


    public ArrayList<Distrito>getArrayDistritos(String iddpto){
        ArrayList<Distrito>distritos = new ArrayList<Distrito>();
        String campos[] = new String[]{DataBaseMaestro.TABLE_DISTRITOS_ID,
                                       DataBaseMaestro.TABLE_DISTRITOS_NOMBRE};
        String where = DataBaseMaestro.TABLE_DISTRITOS_DPTO + "="+iddpto;
        Cursor cursor = data.consultar(DataBaseMaestro.TABLE_DISTRITOS,campos,where,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                distritos.add(new Distrito(cursor.getString(0),cursor.getString(1)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return distritos;
    }

    public void insertarDpto(){
        data.sqlQuery("INSERT INTO departamentos VALUES(0,'CAPITAL');");
        data.sqlQuery("INSERT INTO departamentos VALUES(1,'CONCEPCION');");
        data.sqlQuery("INSERT INTO departamentos VALUES(2,'SAN PEDRO');");
        data.sqlQuery("INSERT INTO departamentos VALUES(3,'CORDILLERA');");
        data.sqlQuery("INSERT INTO departamentos VALUES(4,'GUAIRA');");
        data.sqlQuery("INSERT INTO departamentos VALUES(5,'CAAGUAZU');");
        data.sqlQuery("INSERT INTO departamentos VALUES(6,'CAAZAPA');");
        data.sqlQuery("INSERT INTO departamentos VALUES(7,'ITAPUA');");
        data.sqlQuery("INSERT INTO departamentos VALUES(8,'MISIONES');");
        data.sqlQuery("INSERT INTO departamentos VALUES(9,'PARAGUARI');");
        data.sqlQuery("INSERT INTO departamentos VALUES(10,'ALTO PARANA');");
        data.sqlQuery("INSERT INTO departamentos VALUES(11,'CENTRAL');");
        data.sqlQuery("INSERT INTO departamentos VALUES(12,'ÑEEMBUCU');");
        data.sqlQuery("INSERT INTO departamentos VALUES(13,'AMAMBAY');");
        data.sqlQuery("INSERT INTO departamentos VALUES(14,'CANINDEYU');");
        data.sqlQuery("INSERT INTO departamentos VALUES(15,'PRESIDENTE HAYES');");
        data.sqlQuery("INSERT INTO departamentos VALUES(16,'BOQUERON');");
        data.sqlQuery("INSERT INTO departamentos VALUES(17,'ALTO PARAGUAY');");
    }


    public void insertarDistritos(Context context){
        try {
            InputStream fraw = context.getResources().openRawResource(R.raw.distritos);
            BufferedReader brin = new BufferedReader(new InputStreamReader(fraw));
            String linea;
            while((linea = brin.readLine())!=null) {
                data.sqlQuery(linea);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void insertarInstituciones( ArrayList<Institucion> instituciones){
        data.eliminarRegistros(DataBaseMaestro.TABLE_INSTITUCIONES,null,null);
        inicializarContent();
        for(Institucion institucion:instituciones){
            registro.put(DataBaseMaestro.TABLE_INSTITUCIONES_COD,institucion.getCodigo());
            registro.put(DataBaseMaestro.TABLE_INSTITUCIONES_NOM,institucion.getDescripcion());
            data.insertarResgistro(DataBaseMaestro.TABLE_INSTITUCIONES,registro);
            registro.clear();
        }
    }


    public void insertarProyecto(Proyecto proyecto){
        inicializarContent();
        registro.put(DataBaseMaestro.TABLE_PROYECTOS_COD, proyecto.getCodigo());
        registro.put(DataBaseMaestro.TABLE_PROYECTOS_NOM,proyecto.getDescripcion());
        registro.put(DataBaseMaestro.TABLE_PROYECTOS_INST, proyecto.getInstitucion());
        registro.put(DataBaseMaestro.TABLE_PROYECTOS_TIPO, proyecto.getTipo());
        registro.put(DataBaseMaestro.TABLE_PROYECTOS_CANT_MIN_IMG, proyecto.getCant_min_img());
        registro.put(DataBaseMaestro.TABLE_PROYECTOS_ALTA_DESTINATARIO, proyecto.getAlta_destinatarios());
        if(proyecto.getTipo() == 3){
            registro.put(DataBaseMaestro.TABLE_PROYECTOS_ENTIDAD_RELEVAR, proyecto.getEntidad_relevar());
        }
        data.insertarResgistro(DataBaseMaestro.TABLE_PROYECTOS, registro);
        for(Motivos motivos:proyecto.getMotivos()){
            addMotivo(motivos, proyecto.getCodigo());
        }


        inicializarContent();
        registro.put(DataBaseMaestro.TABLE_SUSCRIPCIONES_TOPIC,proyecto.getDescripcion().toLowerCase().replaceAll(" ",""));
        data.insertarResgistro(DataBaseMaestro.TABLE_SUSCRIPCIONES,registro);
    }

    public void desuscribirTopics(){
        String CONSULTA = "SELECT * FROM " + DataBaseMaestro.TABLE_SUSCRIPCIONES;
        Cursor cursor = data.consultaSql(CONSULTA,null);
        if(cursor.moveToFirst()){
            do{
                FirebaseMessaging.getInstance().unsubscribeFromTopic(cursor.getString(1).toLowerCase().replaceAll(" ",""));
            }while (cursor.moveToNext());
            data.eliminarRegistros(DataBaseMaestro.TABLE_SUSCRIPCIONES,null,null);
        }
        cursor.close();
    }


    public long addMotivo(Motivos motivos,String codproyecto){
        inicializarContent();
        registro.put(DataBaseMaestro.TABLE_MOTIVOS_ID_PROYECTO, codproyecto);
        registro.put(DataBaseMaestro.TABLE_MOTIVOS_CODIGO, motivos.getCodmotivo());
        registro.put(DataBaseMaestro.TABLE_MOTIVOS_DESCRIPCION,motivos.getDescripcionMotivo());
        registro.put(DataBaseMaestro.TABLE_MOTIVOS_COD_FORMULARIO, motivos.getCodformulario());
        registro.put(DataBaseMaestro.TABLE_MOTIVOS_FORMULARIO_DESCRIP, motivos.getDescripcionForm());
        registro.put(DataBaseMaestro.TABLE_MOTIVOS_HAS_FORMULARIO, (motivos.getSecciones() != null) ? 1 : 0);
        long result = data.insertarResgistro(DataBaseMaestro.TABLE_MOTIVOS, registro);
        if(motivos.getSecciones()!=null){
            addSeccion(motivos.getSecciones(), motivos.getCodmotivo());
        }
        return result;
    }

    public ArrayList<Proyecto> arrayProyectos(){
        ArrayList<Proyecto>proyectos = new ArrayList<>();
        String CONSULTA = "SELECT * FROM "+DataBaseMaestro.VIEW_PROYECTOS;
        Cursor cursor = data.consultaSql(CONSULTA, null);
        if(cursor.moveToFirst()){
            do {
                proyectos.add(new Proyecto(cursor.getString(1),
                                           cursor.getString(0),
                                           cursor.getString(2),
                                           cursor.getInt(3),
                                           cursor.getInt(4),
                                           cursor.getInt(5),
                                           cursor.getString(6)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return proyectos;
    }

    public Proyecto getProyectoByCodMotivo(int codmotivo){
        Proyecto proyecto = null;
        String CONSULTA = "SELECT p."+DataBaseMaestro.TABLE_PROYECTOS_COD+","+//0
                                 "p."+DataBaseMaestro.TABLE_PROYECTOS_NOM+","+//1
                                 "i."+DataBaseMaestro.TABLE_INSTITUCIONES_NOM+","+//2
                                 "p."+DataBaseMaestro.TABLE_PROYECTOS_TIPO+","+//3
                                 "p."+DataBaseMaestro.TABLE_PROYECTOS_CANT_MIN_IMG+","+//4
                                 "p."+DataBaseMaestro.TABLE_PROYECTOS_ALTA_DESTINATARIO+","+//5
                                 "p."+DataBaseMaestro.TABLE_PROYECTOS_ENTIDAD_RELEVAR+" "+//6
                          "FROM " + DataBaseMaestro.TABLE_PROYECTOS+" p," +
                                    DataBaseMaestro.TABLE_MOTIVOS+" m," +
                                    DataBaseMaestro.TABLE_INSTITUCIONES+" i " +
                          "WHERE p."+DataBaseMaestro.TABLE_PROYECTOS_COD+"=" +
                                "m."+DataBaseMaestro.TABLE_MOTIVOS_ID_PROYECTO+" AND " +
                                "p."+DataBaseMaestro.TABLE_PROYECTOS_INST+"=" +
                                "i."+DataBaseMaestro.TABLE_INSTITUCIONES_COD + " AND " +
                                "m."+DataBaseMaestro.TABLE_MOTIVOS_CODIGO+"="+codmotivo;
        Cursor cursor = data.consultaSql(CONSULTA, null);
        if(cursor.moveToFirst()){
            proyecto = new Proyecto(cursor.getString(1),
                                    cursor.getString(0),
                                    cursor.getString(2),
                                    cursor.getInt(3),
                                    cursor.getInt(4),
                                    cursor.getInt(5),
                                    cursor.getString(6));
        }
        return proyecto;
    }


    public boolean isBeneficiarioExists(String documento){
        String CONSULTA = "SELECT " + DataBaseMaestro.TABLE_BENEFICIARIO_ID +
                         " FROM " + DataBaseMaestro.TABLE_BENEFICIARIO +
                         " WHERE " + DataBaseMaestro.TABLE_BENEFICIARIO_DOCUMENTO + " ='"+documento+"'";
        Cursor cursor = data.consultaSql(CONSULTA,null);
        boolean result = false;
        if(cursor.moveToFirst()){
            result = true;
        }
        return result;
    }


    public long guardarInicioRecorrido(){
        inicializarContent();
        registro.put(DataBaseMaestro.TABLE_RECORRIDO_INI, FechaUtil.getFechaActual());
        return data.insertarResgistro(DataBaseMaestro.TABLE_RECORRIDO, registro);
    }

    public void guardarFinRecorrido(long idrecorrido,long tiempo){
        inicializarContent();
        registro.put(DataBaseMaestro.TABLE_RECORRIDO_FIN, FechaUtil.getFechaActual());
        registro.put(DataBaseMaestro.TABLE_RECORRIDO_TIEMPO, tiempo);
        registro.put(DataBaseMaestro.TABLE_RECORRIDO_USUARIO, sessionData.getUsuario());
        data.actualizarRegistro(DataBaseMaestro.TABLE_RECORRIDO, registro, DataBaseMaestro.TABLE_RECORRIDO_ID + "=" + idrecorrido, null);
    }

    public long addEventoCell(String evento){
        inicializarContent();
        registro.put(DataBaseMaestro.TABLE_EVTCELL_DESCRIP, evento);
        registro.put(DataBaseMaestro.TABLE_EVTCELL_FECHA,FechaUtil.getFechaActual());
        registro.put(DataBaseMaestro.TABLE_EVTCELL_USER,sessionData.getUsuario());
        return data.insertarResgistro(DataBaseMaestro.TABLE_EVTCELL,registro);
    }

    public void guardarMarcacionEntradaSalida(Location location,int tipo){
        inicializarContent();
        registro.put(DataBaseMaestro.TABLE_UBICACIONES_LONG,location.getLongitude());
        registro.put(DataBaseMaestro.TABLE_UBICACIONES_LAT,location.getLatitude());
        registro.put(DataBaseMaestro.TABLE_UBICACIONES_HORA_OBT,FechaUtil.milisegunToDate(location.getTime()));
        registro.put(DataBaseMaestro.TABLE_UBICACIONES_HORA_GUARD,FechaUtil.getFechaActual());
        registro.put(DataBaseMaestro.TABLE_UBICACIONES_PROVEDOR,location.getProvider());
        registro.put(DataBaseMaestro.TABLE_UBICACIONES_BATERIA,CellUtils.getPorcBaterria(context));
        registro.put(DataBaseMaestro.TABLE_UBICACIONES_ALTITUD,location.getAltitude());
        registro.put(DataBaseMaestro.TABLE_UBICACIONES_PRECS,location.getAccuracy());
        registro.put(DataBaseMaestro.TABLE_UBICACIONES_TIPO,tipo);
        registro.put(DataBaseMaestro.TABLE_UBICACIONES_USER,sessionData.getUsuario());
        data.insertarResgistro(DataBaseMaestro.TABLE_UBICACIONES,registro);
    }


    public int isEntradaSalida(){
        String CONSULTA = "SELECT " + DataBaseMaestro.TABLE_UBICACIONES_TIPO +","+ DataBaseMaestro.TABLE_UBICACIONES_HORA_GUARD+" "+
                          "FROM " +DataBaseMaestro.TABLE_UBICACIONES +" "+
                          "WHERE " + DataBaseMaestro.TABLE_UBICACIONES_TIPO+">2 AND "+
                                     DataBaseMaestro.TABLE_UBICACIONES_USER+"='"+sessionData.getUsuario()+"'";
        Cursor cursor = data.consultaSql(CONSULTA,null);
        if(cursor.moveToLast()){
            String cadenafecha = cursor.getString(1).substring(0,10);
            if(cadenafecha.equals(FechaUtil.getFechaDia())){
                if(cursor.getInt(0)==3){
                    return 4;
                }else{
                    return 0;
                }
            }else{
                return 3;
            }
        }
        cursor.close();
        return 3;
    }


    public long guardarNotificacion(Notificacion notificacion){
        inicializarContent();
        registro.put(DataBaseMaestro.TABLE_NOTIFICACIONES_TOPIC,notificacion.getTopic());
        registro.put(DataBaseMaestro.TABLE_NOTIFICACIONES_MSJ_ID,notificacion.getMensajeid());
        registro.put(DataBaseMaestro.TABLE_NOTIFICACIONES_MSJ,notificacion.getMensaje());
        registro.put(DataBaseMaestro.TABLE_NOTIFICACIONES_FECHA,FechaUtil.getFechaActual());
        return data.insertarResgistro(DataBaseMaestro.TABLE_NOTIFICACIONES,registro);
    }


    public ArrayList<Notificacion> getArrayNotificaciones(){
        ArrayList<Notificacion>notificaciones = new ArrayList<>();
        String CONSULTA = "SELECT * FROM " + DataBaseMaestro.TABLE_NOTIFICACIONES;
        Cursor cursor = data.consultaSql(CONSULTA,null);
        if(cursor.moveToFirst()){
            do{
               notificaciones.add(new Notificacion(cursor.getString(1),
                                                   cursor.getString(2),
                                                   cursor.getString(3),
                                                   cursor.getString(4),
                                                   cursor.getInt(5)==1));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return notificaciones;
    }


    public void updateRelavamientoCorrecto(long idvisita,int activo){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseMaestro.TABLE_VISITA_CORRECTO,activo);
        data.actualizarRegistro(DataBaseMaestro.TABLE_VISITA,contentValues,DataBaseMaestro.TABLE_VISITA_ID+"="+idvisita,null);
    }


    public void insertarCorreccion(String rel_origen,String rel_final){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseMaestro.TABLE_CORRECCIONES_REL_ORIGIN,rel_origen);
        contentValues.put(DataBaseMaestro.TABLE_CORRECCIONES_REL_FINAL,rel_final);
        Log.i("Correccion",contentValues.toString());
        data.insertarResgistro(DataBaseMaestro.TABLE_CORRECCIONES,contentValues);
    }


    public String getHashImagen(long idregistro,SendData.TIPO tipo){
        String hash = null;
        String campos[];
        String where;
        Cursor cursor = null;
        switch (tipo.getCodigo()){
            case 1:
                campos = new String[]{DataBaseMaestro.TABLE_CAPTURAS_HASH};
                where = DataBaseMaestro.TABLE_CAPTURAS_ID + "=" + idregistro;
                cursor = data.consultar(DataBaseMaestro.TABLE_CAPTURAS, campos,where, null, null, null, null);
            break;
            case 7:
                campos = new String[]{DataBaseMaestro.TABLE_CAPTURA_EVENTOS_HASH};
                where = DataBaseMaestro.TABLE_CAPTURA_EVENTOS_ID + "=" + idregistro;
                cursor = data.consultar(DataBaseMaestro.TABLE_CAPTURA_EVENTOS, campos,where, null, null, null, null);
            break;
        }

        if(cursor!=null && cursor.moveToFirst()){
            hash = cursor.getString(0);
        }
        cursor.close();

        return hash;
    }


    public  int cantidadRegistro(String tabla,String condicion){
        int  result = 0;
        String campos[] = new String[]{"_id"};
        Cursor cursor = data.consultar(tabla, campos,condicion, null, null, null, null);
        if(cursor.moveToFirst()){
            result = cursor.getCount();
        }
        return result;
    }

    public void imagenSincronizada(String pathImage){
        ContentValues values=new ContentValues();
        values.put("estado", 1);
        data.actualizarRegistro(DataBaseMaestro.TABLE_CAPTURAS,values,"path='" + pathImage + "'",null);
        data.actualizarRegistro(DataBaseMaestro.TABLE_CAPTURA_EVENTOS,values,"path='" + pathImage + "'",null);

        File file = new File(pathImage);
        if(file.exists()){
            file.delete();
        }
    }

    public boolean existImagen(String hash){
        boolean result = false;
        String campos[] = new String[]{DataBaseMaestro.TABLE_CAPTURAS_ID};//0
        String where = DataBaseMaestro.TABLE_CAPTURAS_HASH + "='" + hash +"'";
        Cursor cursor = data.consultar(DataBaseMaestro.TABLE_CAPTURAS, campos, where, null, null, null, null);
        if(cursor.moveToFirst()){
           result = true;
        }
        cursor.close();
        return result;
    }

    public List<Marcacion> getMarcaciones(int tipo){
        List<Marcacion> arr_marcacion = new ArrayList<>();
        String campos[] = new String[]{DataBaseMaestro.TABLE_UBICACIONES_LONG,      //0
                                       DataBaseMaestro.TABLE_UBICACIONES_LAT,       //1
                                       DataBaseMaestro.TABLE_UBICACIONES_PRECS,     //2
                                       DataBaseMaestro.TABLE_UBICACIONES_HORA_GUARD,//3
                                       DataBaseMaestro.TABLE_UBICACIONES_TIPO,      //4
                                       DataBaseMaestro.TABLE_UBICACIONES_EST};      //5
        String where = "";
        switch (tipo){
            case 0:
                where = DataBaseMaestro.TABLE_UBICACIONES_TIPO + " IN(3,4);";
            break;
            case 1:
                where = DataBaseMaestro.TABLE_UBICACIONES_TIPO + "=3;";
            break;
            case 2:
                where = DataBaseMaestro.TABLE_UBICACIONES_TIPO + "=4;";
            break;
        }
        Cursor cursor = data.consultar(DataBaseMaestro.TABLE_UBICACIONES, campos, where, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                arr_marcacion.add(new Marcacion(cursor.getString(3),
                                                new Coordenadas(cursor.getString(0),
                                                                cursor.getString(1),
                                                                cursor.getFloat(2)),
                                                Marcacion.TipoMarcacion.findByCodigo(cursor.getInt(4)),
                                                cursor.getInt(5)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return  arr_marcacion;
    }
}