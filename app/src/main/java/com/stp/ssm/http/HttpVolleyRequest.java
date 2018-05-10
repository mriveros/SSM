package com.stp.ssm.http;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.stp.ssm.Evt.CerrarSessionEvt;
import com.stp.ssm.Evt.DescargaBeneficiarioEvt;
import com.stp.ssm.Evt.DescargaMotivosEvt;
import com.stp.ssm.Evt.LoginResult;
import com.stp.ssm.Evt.TokenExpirado;
import com.stp.ssm.Evt.ValidIdentificacionesEvt;
import com.stp.ssm.Interfaces.OnHoraServidorListener;
import com.stp.ssm.R;
import com.stp.ssm.Util.ParseJson;
import com.stp.ssm.Util.SessionData;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

import static com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
import static com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES;
import static com.android.volley.Request.Method;
import static com.android.volley.Request.Method.GET;
import static com.android.volley.Request.Method.POST;
import static com.android.volley.Request.Method.PUT;
import static com.android.volley.Response.ErrorListener;
import static com.android.volley.toolbox.Volley.newRequestQueue;
import static com.stp.ssm.Evt.CerrarSessionEvt.RESULTADO;
import static com.stp.ssm.Evt.CerrarSessionEvt.RESULTADO.ERROR;
import static com.stp.ssm.Evt.LoginResult.TipoRespuesta;
import static com.stp.ssm.Evt.LoginResult.TipoRespuesta.ERROR_CONEXION;
import static com.stp.ssm.Evt.LoginResult.TipoRespuesta.ERROR_SERVIDOR;
import static com.stp.ssm.Util.ParseJson.parseCerrarSessionResp;
import static com.stp.ssm.Util.ParseJson.parseHoraServidor;
import static com.stp.ssm.Util.ParseJson.parseIdentificaciones;
import static com.stp.ssm.Util.ParseJson.parseLogin;
import static com.stp.ssm.Util.SessionData.getInstance;
import static com.stp.ssm.http.HttpStatus.GENERIC;
import static com.stp.ssm.http.URLs.URL_BENEFICIARIOS;
import static com.stp.ssm.http.URLs.URL_ENCUESTA;
import static de.greenrobot.event.EventBus.getDefault;

public class HttpVolleyRequest {


    private static HttpVolleyRequest mInstance;
    private RequestQueue mRequestQueue;
    protected EventBus eventBus;
    private final int TIME_OUT_LOGIN = 60000;
    private Context context;
    private OnHoraServidorListener onHoraServidorListener;

    public HttpVolleyRequest(Context context) {
        this.eventBus = getDefault();
        this.context = context;
        mRequestQueue = newRequestQueue(context);
    }

    public static HttpVolleyRequest getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new HttpVolleyRequest(context);
        }
        return mInstance;
    }

    public void setOnHoraServidorListener(OnHoraServidorListener onHoraServidorListener) {
        this.onHoraServidorListener = onHoraServidorListener;
    }


    public void simplePostRequestLogin(String url, final Map<String, String> parametros) {
        StringRequest request = new StringRequest(POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                LoginResult loginResult = parseLogin(s.toString());
                eventBus.post(loginResult);
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                NetworkResponse networkResponse = volleyError.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode) {
                        case 401:
                            if (volleyError.networkResponse.data != null) {
                                try {
                                    String body = new String(volleyError.networkResponse.data, "UTF-8");
                                    LoginResult loginResult = parseLogin(body);
                                    eventBus.post(loginResult);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                            //eventBus.post(new LoginResult(LoginResult.TipoRespuesta.ERROR.getDescripcion()));
                            break;
                        case 409:
                            eventBus.post(new LoginResult(ERROR_SERVIDOR.getDescripcion()));
                            break;
                        default:
                            eventBus.post(new LoginResult(volleyError.toString()));
                            break;
                    }
                } else {
                    eventBus.post(new LoginResult(volleyError.toString()));
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param = parametros;
                return param;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String token = response.headers.get("authorization");
                SessionData.getInstance(context).setToken(token);
                return super.parseNetworkResponse(response);
            }
        };

        ///deficion del timeout
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(30000,
                DEFAULT_MAX_RETRIES,
                DEFAULT_BACKOFF_MULT));
        mRequestQueue.add(request);
    }

    public void RequestGetBase(String usuario, final String token) {
        /*Request Beneficiarios*/
        String url = URL_BENEFICIARIOS + usuario;
        StringRequest requestBeneficiarios = new StringRequest(GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                eventBus.post(new DescargaBeneficiarioEvt(s.toString()));
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                NetworkResponse networkResponse = volleyError.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode) {
                        case 401:
                            eventBus.post(new TokenExpirado());
                            break;
                        default:
                            eventBus.post(new DescargaBeneficiarioEvt(null, volleyError.toString(), GENERIC));
                            break;
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", token);
                return params;
            }
        };
        requestBeneficiarios.setShouldCache(false);
        requestBeneficiarios.setRetryPolicy(new DefaultRetryPolicy(120000,
                DEFAULT_MAX_RETRIES,
                DEFAULT_BACKOFF_MULT));

        /*Request de los Motivo con sus respectivos Formularios*/
        url = URL_ENCUESTA + usuario;
        StringRequest requestMotivos = new StringRequest(GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                eventBus.post(new DescargaMotivosEvt(s.toString()));
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                NetworkResponse networkResponse = volleyError.networkResponse;
                if (networkResponse != null) {
                    switch (networkResponse.statusCode) {
                        case 401:
                            eventBus.post(new TokenExpirado());
                            break;
                        default:
                            eventBus.post(new DescargaMotivosEvt(null, volleyError.toString(), GENERIC));
                            break;
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", token);
                return params;
            }
        };
        requestMotivos.setShouldCache(false);
        requestMotivos.setRetryPolicy(new DefaultRetryPolicy(120000,
                DEFAULT_MAX_RETRIES,
                DEFAULT_BACKOFF_MULT));

        /*Cola request*/
        mRequestQueue.add(requestBeneficiarios);
        mRequestQueue.add(requestMotivos);
    }

    public void cerrarSession(String url, final Map<String, String> parametros, final String token) {
        StringRequest requestLiberarUser = new StringRequest(PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                eventBus.post(new CerrarSessionEvt(parseCerrarSessionResp(s.toString())));
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                eventBus.post(new CerrarSessionEvt(ERROR.getCodigo()));
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param = parametros;
                return param;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", token);
                return params;
            }
        };
        requestLiberarUser.setShouldCache(false);
        mRequestQueue.add(requestLiberarUser);
    }

    public void validarCedula(String urlrequest) {
        StringRequest requestCedula = new StringRequest(GET, urlrequest, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                String cadenas[] = parseIdentificaciones(s.toString());
                eventBus.post(new ValidIdentificacionesEvt(cadenas[0], cadenas[1], cadenas[2], cadenas[3]));
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                eventBus.post(new ValidIdentificacionesEvt(null, null, null, null));
            }
        });
        requestCedula.setShouldCache(false);
        mRequestQueue.add(requestCedula);
    }

    public void registrarTokenPush(String url, final Map<String, String> parametros, final String token) {
        StringRequest request = new StringRequest(POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                eventBus.post(new LoginResult(volleyError.toString()));
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param = parametros;
                return param;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", token);
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    public void requestHoraServidor(String urlrequest) {
        StringRequest requestCedula = new StringRequest(GET, urlrequest, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                onHoraServidorListener.OnHoraServidor(parseHoraServidor(s));
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                eventBus.post(new LoginResult(ERROR_CONEXION.getDescripcion()));
            }
        });
        requestCedula.setShouldCache(false);
        mRequestQueue.add(requestCedula);
    }
}
