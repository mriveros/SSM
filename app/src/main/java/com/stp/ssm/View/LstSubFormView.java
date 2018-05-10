package com.stp.ssm.View;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.stp.ssm.Interfaces.OnAddPersonaListener;
import com.stp.ssm.Model.Pregunta;
import com.stp.ssm.Model.Secciones;
import com.stp.ssm.Model.SubFormulario;
import com.stp.ssm.Model.TotalResult;
import com.stp.ssm.R;

import java.util.ArrayList;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.view.LayoutInflater.from;
import static android.view.View.OnClickListener;
import static android.widget.AdapterView.OnItemSelectedListener;
import static com.stp.ssm.R.color;
import static com.stp.ssm.R.color.negro1;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.id.btnitemdelete;
import static com.stp.ssm.R.id.lbl_spinne_item_2;
import static com.stp.ssm.R.id.lblitemlist;
import static com.stp.ssm.R.id.spCondicion;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.list_item_adj;
import static com.stp.ssm.R.layout.spinner_item_2;
import static com.stp.ssm.R.layout.view_list_subformulario;
import static com.stp.ssm.View.ViewFactory.crearCuadroDeTextoNumerico;
import static com.stp.ssm.View.ViewFactory.createTextView;
import static com.stp.ssm.View.ViewFactory.notificacionToast;
import static java.lang.Integer.parseInt;

public class LstSubFormView extends LinearLayout {

    private ListView lstPersonas;
    private Button btnAgregar;
    private Spinner spinner;
    private ToggleButton tgbtnSinFam;
    private OnAddPersonaListener onAddPersonaListener;
    private int codigoSeccion;
    private ArrayList<SubFormulario> subFormularios;
    private int position;
    private ListaAdapter adapter;
    private LinearLayout lineartotales;
    private ArrayList<TotalResult> totales;
    private int totalizable = 0;

    public LstSubFormView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inicializar();
    }

    public LstSubFormView(Context context) {
        super(context);
        inicializar();
    }

    public void setCodigoSeccion(int codigoSeccion) {
        this.codigoSeccion = codigoSeccion;
    }

    public void setSubFormularios(ArrayList<SubFormulario> subFormularios) {
        this.subFormularios = subFormularios;
        adapter = new ListaAdapter();
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setTotalizable(Secciones secciones) {
        ArrayList<Pregunta> preguntas = secciones.getPreguntas();
        if (subFormularios.isEmpty()) {
            totalizable = 1;
            int posicion = 0;
            for (Pregunta pregunta : preguntas) {
                if (pregunta.getTotalizable() == 1) {
                    if (totales == null) {
                        totales = new ArrayList<>();
                    }
                    totales.add(new TotalResult(pregunta.getPregunta(), 0, posicion));
                }
                posicion++;
            }

            secciones.setTotales(totales);
            lineartotales.setVisibility(VISIBLE);
            lineartotales.removeAllViews();
            int i = 0;
            for (TotalResult totalResult : totales) {
                lineartotales.addView(createTextView("Total " + totalResult.getDescripcion(), 20, getContext(), negro1));
                EditText editTextNumeric = crearCuadroDeTextoNumerico(getContext());
                editTextNumeric.addTextChangedListener(new EditarEdditex(i));
                lineartotales.addView(editTextNumeric);
                i++;
            }
        }
    }

    public void cargarDatos() {
        //lstPersonas.setAdapter(new ArrayAdapter<SubFormulario>(getContext(),R.layout.item_secciones_1,R.id.lbl_item_seccion,subFormularios));
        lstPersonas.setAdapter(adapter);
    }

    private void inicializar() {
        String infService = LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        li.inflate(view_list_subformulario, this, true);
        //eventBus = EventBus.getDefault();
        //eventBus.register(this);

        spinner = (Spinner) findViewById(spCondicion);
        spinner.setAdapter(new ArrayAdapter<String>(getContext(),
                spinner_item_2,
                lbl_spinne_item_2,
                new String[]{"SI", "NO"}));
        subFormularios = new ArrayList<>();
        lstPersonas = (ListView) findViewById(id.lstPersonas);
        btnAgregar = (Button) findViewById(id.btnAgregar);
        lineartotales = (LinearLayout) findViewById(id.lineartotales);
        tgbtnSinFam = (ToggleButton) findViewById(id.tgbtnSinFam);
        tgbtnSinFam.setVisibility(GONE);
        asignarEventos();
    }

    public void condicionalVisible() {
        spinner.setVisibility(VISIBLE);
    }

    private void asignarEventos() {
        btnAgregar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalizable == 1) {
                    if (validar()) {
                        onAddPersonaListener.OnAddPersona();
                    } else {
                        notificacionToast(getContext(), "Debe cargar Valores distintos a 0");
                    }
                } else {
                    onAddPersonaListener.OnAddPersona();
                }
            }
        });

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == 0) {
                    btnAgregar.setEnabled(true);
                    if (totalizable == 1) {
                        lineartotales.setVisibility(VISIBLE);
                    }
                } else {
                    btnAgregar.setEnabled(false);
                    lineartotales.setVisibility(GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public void swagregar() {
        lineartotales.setVisibility(GONE);
    }

    public boolean validar() {
        for (TotalResult totalResult : totales) {
            if (totalResult.getTotal() == 0) {
                return false;
            }
        }
        return true;
    }

    public void setOnAddPersonaListener(OnAddPersonaListener onAddPersonaListener) {
        this.onAddPersonaListener = onAddPersonaListener;
    }

    public ArrayList<TotalResult> getTotales() {
        return totales;
    }

    private class ListaAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public ListaAdapter() {
            mInflater = from(getContext());
        }

        @Override
        public int getCount() {
            return subFormularios.size();
        }

        @Override
        public Object getItem(int position) {
            return subFormularios.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder vh;
            if (convertView == null) {
                convertView = mInflater.inflate(list_item_adj, parent, false);
                vh = new ViewHolder();

                vh.lblitemlist = (TextView) convertView.findViewById(lblitemlist);
                vh.btnitemdelete = (ImageButton) convertView.findViewById(btnitemdelete);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            vh.lblitemlist.setText(subFormularios.get(position).toString());
            vh.btnitemdelete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    subFormularios.remove(position);
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }

        class ViewHolder {
            TextView lblitemlist;
            ImageButton btnitemdelete;
        }
    }

    private class EditarEdditex implements TextWatcher {

        private int posicion;


        public EditarEdditex(int posicion) {
            this.posicion = posicion;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().equals("")) {
                totales.get(posicion).setTotal(0);
            } else {
                totales.get(posicion).setTotal(parseInt(s.toString()));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }
}
