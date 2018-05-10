package com.stp.ssm.View;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import com.stp.ssm.Adapters.ListCoordenadasAdapter;
import com.stp.ssm.CustomGetCoordenadasActivity;
import com.stp.ssm.Interfaces.OnDeleteListener;
import com.stp.ssm.Model.Coordenadas;
import com.stp.ssm.Model.Pregunta;
import com.stp.ssm.R;
import com.stp.ssm.Util.ObjectToJson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Intent.CATEGORY_DEFAULT;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.os.Build.VERSION;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES;
import static android.os.Build.VERSION_CODES.KITKAT;
import static android.support.v7.widget.RecyclerView.LayoutManager;
import static android.widget.AdapterView.OnItemSelectedListener;
import static com.stp.ssm.CustomGetCoordenadasActivity.ACTION_GET_COORDENADAS;
import static com.stp.ssm.R.id.lbl_spinne_item;
import static com.stp.ssm.R.layout.custom_list_coordinates_view;
import static com.stp.ssm.R.layout.spinner_item_1;
import static com.stp.ssm.Util.ObjectToJson.getJsonFromObject;
import static java.lang.Double.parseDouble;
import static java.lang.Float.parseFloat;
import static java.lang.String.valueOf;
import static java.util.UUID.randomUUID;

public class CustomListCoordenadasView extends LinearLayout {

    private Context context;
    private Pregunta pregunta;
    private Spinner spTipoLista;
    private RecyclerView lst_puntos;
    private LayoutManager mLayoutManager;
    private Button btnAgregarCoordenadas;
    private MyReceiver myReceiver;
    private List<Coordenadas> arr_coordenadas;
    private ListCoordenadasAdapter adapter;
    private UUID id;
    private JSONArray jsonArray;
    private JSONObject jrespuesta;

    private final String TIPO_LISTA[] = new String[]{"LINEA", "POLIGONO"};

    public CustomListCoordenadasView(Context context, Pregunta pregunta) {
        super(context);
        this.context = context;
        this.pregunta = pregunta;
        this.id = randomUUID();
        this.jsonArray = new JSONArray();
        inicializar();
    }

    public CustomListCoordenadasView(Context context, @Nullable AttributeSet attrs, Pregunta pregunta) {
        super(context, attrs);
        this.context = context;
        this.pregunta = pregunta;
        this.id = randomUUID();
        this.jsonArray = new JSONArray();
        inicializar();
    }

    private void inicializar() {
        String infService = LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        li.inflate(custom_list_coordinates_view, this, true);

        arr_coordenadas = new ArrayList<>();
        jrespuesta = new JSONObject();
        try {
            jrespuesta.put("figura", TIPO_LISTA[0]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        spTipoLista = (Spinner) findViewById(R.id.spTipoLista);
        spTipoLista.setAdapter(new ArrayAdapter<String>(context, spinner_item_1, lbl_spinne_item, TIPO_LISTA));
        lst_puntos = (RecyclerView) findViewById(R.id.lst_puntos);

        lst_puntos.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(context);
        lst_puntos.setLayoutManager(mLayoutManager);
        adapter = new ListCoordenadasAdapter(arr_coordenadas);
        lst_puntos.setAdapter(adapter);
        adapter.setOnDeleteListener(new OnDeleteListener() {
            @Override
            public void OnDelete(int position) {
                if (SDK_INT >= KITKAT) {
                    jsonArray.remove(position);
                } else {
                    JSONObject jsonObject;
                    jsonArray = new JSONArray();
                    for (Coordenadas coordenadas : arr_coordenadas) {
                        try {
                            jsonObject = new JSONObject(getJsonFromObject(coordenadas));
                            jsonArray.put(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                try {
                    jrespuesta.remove("puntos");
                    jrespuesta.put("puntos", jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pregunta.responder(jrespuesta.toString());
            }
        });

        btnAgregarCoordenadas = (Button) findViewById(R.id.btnAgregarCoordenadas);

        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GET_COORDENADAS);
        intentFilter.addCategory(CATEGORY_DEFAULT);
        getContext().registerReceiver(myReceiver, intentFilter);

        asignarEventos();
    }

    public void setAgregarInvisible() {
        btnAgregarCoordenadas.setVisibility(GONE);
    }

    public void setTipoObjeto(String figura, boolean enable) {
        if (figura.equals(TIPO_LISTA[0])) {
            spTipoLista.setSelection(0);
        } else {
            spTipoLista.setSelection(1);
        }
        spTipoLista.setEnabled(enable);
    }

    public void setListPuntos(JSONObject jsonObject, boolean deleteEnable) {
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("puntos");
            Coordenadas coordenadas;
            for (int i = 0; i < jsonArray.length(); i++) {
                coordenadas = new Coordenadas(jsonArray.getJSONObject(i).getString("longitud"),
                        jsonArray.getJSONObject(i).getString("latitud"),
                        parseFloat(jsonArray.getJSONObject(i).getString("precision")),
                        parseDouble(jsonArray.getJSONObject(i).getString("altitud")),
                        jsonArray.getJSONObject(i).getString("proveedor"),
                        jsonArray.getJSONObject(i).getString("hora"),
                        jsonArray.getJSONObject(i).getString("descripcion"));
                arr_coordenadas.add(coordenadas);
            }
            if (arr_coordenadas.size() > 0) {
                adapter = new ListCoordenadasAdapter(arr_coordenadas, deleteEnable);
                lst_puntos.setAdapter(adapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void asignarEventos() {
        spTipoLista.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    jrespuesta.remove("figura");
                    jrespuesta.put("figura", TIPO_LISTA[position]);
                    pregunta.responder(jrespuesta.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnAgregarCoordenadas.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CustomGetCoordenadasActivity.class);
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("uuid", valueOf(id));
                context.startActivity(intent);
            }
        });
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras().getString("uuid").equals(valueOf(id))) {
                Coordenadas coordenadas = (Coordenadas) intent.getExtras().getSerializable("coordenadas");
                arr_coordenadas.add(coordenadas);
                adapter.notifyData();

                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(getJsonFromObject(coordenadas));
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    jrespuesta.remove("puntos");
                    jrespuesta.put("puntos", jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pregunta.responder(jrespuesta.toString());
            }
        }
    }
}
