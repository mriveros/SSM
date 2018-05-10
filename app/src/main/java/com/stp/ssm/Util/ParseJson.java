package com.stp.ssm.Util;

import com.google.firebase.messaging.FirebaseMessaging;
import com.stp.ssm.Evt.LoginResult;
import com.stp.ssm.Model.Beneficiario;
import com.stp.ssm.Model.Coordenadas;
import com.stp.ssm.Model.HoraServidor;
import com.stp.ssm.Model.Institucion;
import com.stp.ssm.Model.Motivos;
import com.stp.ssm.Model.PosiblesRespuestas;
import com.stp.ssm.Model.Pregunta;
import com.stp.ssm.Model.PreguntaCondicion;
import com.stp.ssm.Model.Proyecto;
import com.stp.ssm.Model.SeccionCondicion;
import com.stp.ssm.Model.Secciones;
import com.stp.ssm.databases.BDFuntions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.stp.ssm.Evt.LoginResult.TipoRespuesta;
import static com.stp.ssm.Evt.LoginResult.TipoRespuesta.ERROR;
import static com.stp.ssm.Evt.LoginResult.TipoRespuesta.ERROR_PARSEO;
import static com.stp.ssm.Evt.LoginResult.TipoRespuesta.ERROR_SERVIDOR;
import static com.stp.ssm.Evt.LoginResult.TipoRespuesta.USUARIO_DESHABILITADO;
import static com.stp.ssm.Evt.LoginResult.TipoRespuesta.USUARIO_ENUSO;
import static com.stp.ssm.Model.Beneficiario.Estado;
import static com.stp.ssm.Model.Beneficiario.Estado.NUEVO_NO_VALIDADO;
import static com.stp.ssm.Model.Beneficiario.TipoBeneficiario;
import static com.stp.ssm.Model.Beneficiario.TipoBeneficiario.findByCodigoBD;
import static com.stp.ssm.Model.Pregunta.TIPO;
import static com.stp.ssm.Model.Pregunta.TIPO.findByCodigo;

public class ParseJson {

    public static LoginResult parseLogin(String json) {
        try {
            JSONObject jobjeto = new JSONObject(json);
            LoginResult loginResult = null;
            switch (jobjeto.getInt("respuesta")) {
                case 0:
                    String usuario = jobjeto.getString("usuario");
                    String departamento = jobjeto.getString("departamento");
                    String distrito = jobjeto.getString("distrito");
                    JSONArray jsonArray = jobjeto.getJSONArray("instituciones");
                    ArrayList<Institucion> institucions = new ArrayList<>();
                    int nivel = jobjeto.getInt("nivel");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        institucions.add(new Institucion(jsonArray.getJSONObject(i).getString("descripcion"),
                                jsonArray.getJSONObject(i).getString("codigo")));
                    }
                    loginResult = new LoginResult(usuario, institucions, distrito, departamento, nivel);
                    break;
                case 1:
                    loginResult = new LoginResult(ERROR.getDescripcion());
                    break;
                case 2:
                    loginResult = new LoginResult(USUARIO_DESHABILITADO.getDescripcion());
                    break;
                case 3:
                    loginResult = new LoginResult(USUARIO_ENUSO.getDescripcion());
                    break;
                case 4:
                    loginResult = new LoginResult(ERROR_SERVIDOR.getDescripcion());
                    break;
            }
            return loginResult;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new LoginResult(ERROR_PARSEO.getDescripcion());
    }


    public static boolean parseBeneficiariosBD(String json, String usuario, BDFuntions bdFuntions) {
        try {
            JSONArray jsonArray = new JSONArray(json);
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    Beneficiario beneficiario = new Beneficiario();
                    beneficiario.setIdregistro(jsonObject.getInt("iddestinatario"));
                    beneficiario.setDocumento(jsonObject.getString("documento"));
                    beneficiario.setNombre(jsonObject.getString("nombre"));
                    beneficiario.setApellido(jsonObject.getString("apellido"));
                    beneficiario.setTipo(findByCodigoBD(jsonObject.getInt("tipo_destinatario")));
                    beneficiario.setJefe((jsonObject.getInt("esjefe") == 1));
                    beneficiario.setProyecto(Integer.toString(jsonObject.getInt("proyecto")));
                    beneficiario.setCoordenadas(new Coordenadas(jsonObject.getString("longitud"),
                            jsonObject.getString("latitud"),
                            (float) jsonObject.getDouble("presicion")));
                    beneficiario.setDepartamento(jsonObject.getInt("codigo_departamento"));
                    beneficiario.setDistrito(jsonObject.getInt("codigo_distrito"));
                    beneficiario.setLocalidad(jsonObject.getInt("codigo_localidad"));
                    beneficiario.setUsuario(usuario);
                    beneficiario.setEstado(NUEVO_NO_VALIDADO);
                    bdFuntions.addBeneficiario(beneficiario);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static ArrayList<Motivos> parserMotivos(JSONArray jsonArray) {
        ArrayList<Motivos> arrayList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                JSONObject formulario = jsonObject.getJSONObject("formulario");
                int codformulario = 0;
                String descripfomulario = "";
                ArrayList<Secciones> secciones = null;
                boolean hasformulario = false;

                if (formulario.getInt("codigo_form") > 0) {
                    hasformulario = true;
                    codformulario = formulario.getInt("codigo_form");
                    descripfomulario = formulario.getString("descrip_form");
                    secciones = parseSeccion(formulario.getJSONArray("secciones"));
                }

                Motivos motivos = new Motivos(jsonObject.getInt("codigo_motivo"),
                        jsonObject.getString("motivo"),
                        codformulario,
                        descripfomulario,
                        hasformulario);
                if (hasformulario) {
                    motivos.setSecciones(secciones);
                }
                arrayList.add(motivos);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arrayList;
    }

    public static boolean parseProyectoDB(String json, BDFuntions bdFuntions) {
        try {
            JSONArray jsonArray = new JSONArray(json);
            if (jsonArray.length() > 0) {
                bdFuntions.limpiarProyectos();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Proyecto proyecto = new Proyecto(jsonObject.getString("proyecto"),
                            jsonObject.getString("proyectoid"),
                            jsonObject.getString("institucionid"),
                            jsonObject.getString("institucion"),
                            parserMotivos(jsonObject.getJSONArray("motivos")),
                            jsonObject.getInt("tipo"),
                            jsonObject.getJSONObject("configProyect").getInt("min_imagenes"),
                            jsonObject.getJSONObject("configProyect").getInt("altas_destinatarios"),
                            (jsonObject.getInt("tipo") == 3) ? jsonObject.getJSONObject("configProyect").getString("entidad_relevar") : "");

                    bdFuntions.insertarProyecto(proyecto);
                    ////////////////////////////////////////////////////////////////////////////////
                    //FirebaseMessaging.getInstance().subscribeToTopic(jsonObject.getString("proyecto").toLowerCase().replaceAll(" ",""));
                    ///////////////////////////////////////////////////////////////////////////////
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static ArrayList<Secciones> parseSeccion(JSONArray jarray) {
        if (jarray.length() > 0) {
            JSONObject jobject;
            ArrayList<Secciones> arraySecciones = new ArrayList<>();
            for (int i = 0; i < jarray.length(); i++) {
                try {
                    jobject = jarray.getJSONObject(i);
                    Secciones secciones = new Secciones(jobject.getInt("codSeccion"),
                            jobject.getString("seccion"),
                            parsePreguntas(jobject.getJSONArray("preguntas")),
                            jobject.getInt("tipo"),
                            jobject.getInt("totalizable"),
                            jobject.getInt("condicionada"));
                    secciones.setCondicionsSiguiente(parseSeccionCondicion(jobject.getJSONArray("siguiente_seccion")));
                    if (jobject.getInt("tipo") == 4) {
                        secciones.setMultimedia(jobject.getString("multimedia"));
                    }
                    arraySecciones.add(secciones);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return arraySecciones;
        }
        return null;
    }

    private static ArrayList<SeccionCondicion> parseSeccionCondicion(JSONArray jarray) {
        ArrayList<SeccionCondicion> arrseccionCondicion = new ArrayList<>();
        if (jarray.length() > 0) {
            JSONObject jobject;
            SeccionCondicion seccionCondicion;
            for (int i = 0; i < jarray.length(); i++) {
                try {
                    jobject = jarray.getJSONObject(i);
                    seccionCondicion = new SeccionCondicion(jobject.getLong("idpreguntacondicionante"),
                            jobject.getLong("idseccionsiguiente"),
                            jobject.getInt("condicion"),
                            jobject.getString("valor"));
                    arrseccionCondicion.add(seccionCondicion);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return arrseccionCondicion;
    }

    private static ArrayList<Pregunta> parsePreguntas(JSONArray jarray) {
        if (jarray.length() > 0) {
            JSONObject jobject;
            ArrayList<Pregunta> preguntas = new ArrayList<>();
            for (int i = 0; i < jarray.length(); i++) {
                try {
                    jobject = jarray.getJSONObject(i);
                    Pregunta pregunta = new Pregunta(jobject.getInt("idpreg"),
                            jobject.getString("pregunta"),
                            findByCodigo(jobject.getInt("tipo")),
                            jobject.getInt("requerido"), null,
                            jobject.getInt("totalizable"),
                            jobject.getInt("visible"));
                    if (jobject.getJSONArray("posibles_respuestas").length() > 0) {
                        pregunta.setRespuestas(parsePosiblesResp(jobject.getJSONArray("posibles_respuestas")));
                    }
                    if (jobject.getJSONArray("preguntas_condicionadas").length() > 0) {
                        pregunta.setPreguntaCondicions(parsePreguntaCondicion(jobject.getJSONArray("preguntas_condicionadas")));
                    }
                    preguntas.add(pregunta);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return preguntas;
        }
        return null;
    }

    private static ArrayList<PosiblesRespuestas> parsePosiblesResp(JSONArray jarray) {
        if (jarray.length() > 0) {
            JSONObject jobject;
            ArrayList<PosiblesRespuestas> respuestas = new ArrayList<>();
            for (int i = 0; i < jarray.length(); i++) {
                try {
                    jobject = jarray.getJSONObject(i);
                    respuestas.add(new PosiblesRespuestas(jobject.getInt("id"), jobject.getString("texto")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return respuestas;
        }
        return null;
    }


    private static ArrayList<PreguntaCondicion> parsePreguntaCondicion(JSONArray jarray) {
        if (jarray.length() > 0) {
            JSONObject jobject;
            ArrayList<PreguntaCondicion> condiciones = new ArrayList<>();
            for (int i = 0; i < jarray.length(); i++) {
                try {
                    jobject = jarray.getJSONObject(i);
                    condiciones.add(new PreguntaCondicion(jobject.getLong("idpreguntacondicionada"),
                            jobject.getInt("condicion"),
                            jobject.getString("valor")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return condiciones;
        }
        return null;
    }


    public static int parseCerrarSessionResp(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getInt("respuesta");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public static String[] parseIdentificaciones(String json) {
        String nomape[] = new String[]{"", "", "", ""};
        try {
            JSONObject jobjeto = new JSONObject(json);
            int pos = jobjeto.getString("nombre").indexOf(" ");
            if (pos > 0) {
                nomape[0] = jobjeto.getString("nombre").substring(0, pos);
                nomape[1] = jobjeto.getString("nombre").substring(pos);
            } else {
                nomape[0] = jobjeto.getString("nombre");
            }
            pos = jobjeto.getString("apellidos").indexOf(" ");
            if (pos > 0) {
                nomape[2] = jobjeto.getString("apellidos").substring(0, pos);
                nomape[3] = jobjeto.getString("apellidos").substring(pos);
            } else {
                nomape[2] = jobjeto.getString("apellidos");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return nomape;
    }

    public static HoraServidor parseHoraServidor(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            return new HoraServidor(jsonObject.getInt("anho"),
                    jsonObject.getInt("mes"),
                    jsonObject.getInt("dia"),
                    jsonObject.getInt("hora"),
                    jsonObject.getInt("minuto"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
