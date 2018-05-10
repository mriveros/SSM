package com.stp.ssm.View;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.stp.ssm.Evt.ValidarSeccion;
import com.stp.ssm.Evt.ValidatePreguntaEvt;
import com.stp.ssm.Interfaces.OnAddItemListener;
import com.stp.ssm.Interfaces.OnClickDialogInputListener;
import com.stp.ssm.Interfaces.OnDataSelectListener;
import com.stp.ssm.Interfaces.OnDeleteListener;
import com.stp.ssm.Interfaces.OnReadCodeListener;
import com.stp.ssm.Model.PosiblesRespuestas;
import com.stp.ssm.Model.Pregunta;
import com.stp.ssm.Model.PreguntaCondicion;
import com.stp.ssm.Model.SeccionCondicion;
import com.stp.ssm.R;
import com.stp.ssm.Util.ValidUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.greenrobot.event.EventBus;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.os.Build.VERSION;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES;
import static android.os.Build.VERSION_CODES.JELLY_BEAN;
import static android.support.v4.content.ContextCompat.getColor;
import static android.view.View.OnFocusChangeListener;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.widget.AdapterView.OnItemSelectedListener;
import static android.widget.CompoundButton.OnCheckedChangeListener;
import static android.widget.LinearLayout.LayoutParams;
import static com.stp.ssm.Model.Pregunta.TIPO;
import static com.stp.ssm.Model.Pregunta.TIPO.CUADRO_TEXTO_NUMERICO;
import static com.stp.ssm.Model.Pregunta.TIPO.FECHA_CALCULO;
import static com.stp.ssm.R.color;
import static com.stp.ssm.R.color.blanco2;
import static com.stp.ssm.R.color.gris1;
import static com.stp.ssm.R.color.negro1;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.layout.seccion_view;
import static com.stp.ssm.R.string;
import static com.stp.ssm.R.string.lbl_requerido;
import static com.stp.ssm.Util.ValidUtil.isValidEmail;
import static com.stp.ssm.View.ViewFactory.crearCheckBox;
import static com.stp.ssm.View.ViewFactory.crearCoordenadasView;
import static com.stp.ssm.View.ViewFactory.crearCuadroDeTexto;
import static com.stp.ssm.View.ViewFactory.crearCuadroDeTextoEmail;
import static com.stp.ssm.View.ViewFactory.crearCuadroDeTextoFecha;
import static com.stp.ssm.View.ViewFactory.crearCuadroDeTextoNumerico;
import static com.stp.ssm.View.ViewFactory.crearCuadroDeTextoTel;
import static com.stp.ssm.View.ViewFactory.crearImagenPregunta;
import static com.stp.ssm.View.ViewFactory.crearLectorQr;
import static com.stp.ssm.View.ViewFactory.crearListDinamica;
import static com.stp.ssm.View.ViewFactory.crearListaCoordenadasView;
import static com.stp.ssm.View.ViewFactory.crearSpinner;
import static com.stp.ssm.View.ViewFactory.createTextView;
import static de.greenrobot.event.EventBus.getDefault;
import static java.lang.Integer.parseInt;
import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static java.util.Calendar.getInstance;

public class SeccionView extends LinearLayout {

    private LinearLayout content;
    private Context context;
    private FragmentManager fragmentManager;
    private EventBus bus;
    private ScrollView scrollView;

    public SeccionView(Context context, FragmentManager fragmentManager) {
        super(context);
        this.context = context;
        this.fragmentManager = fragmentManager;
        inicializar();
    }

    private void inicializar() {
        String infService = LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        li.inflate(seccion_view, this, true);
        bus = getDefault();
        scrollView = (ScrollView) findViewById(id.scrollView);

        content = (LinearLayout) findViewById(id.content);
    }

    public void cargarPreguntas(final ArrayList<Pregunta> preguntas, ArrayList<SeccionCondicion> arrSeccionCondicion, int idseccion) {
        for (final Pregunta pregunta : preguntas) {
            if (pregunta.isVisible()) {
                if (pregunta.isRequerido()) {
                    content.addView(createTextView("*" + pregunta.getPregunta(), 20, getContext(), negro1));
                } else {
                    content.addView(createTextView(pregunta.getPregunta(), 20, getContext(), negro1));
                }

                if (SDK_INT < JELLY_BEAN) {
                    content.setBackgroundColor(context.getResources().getColor(blanco2));
                } else {
                    content.setBackgroundColor(getColor(context, blanco2));
                }

                switch (pregunta.getTipo().getCodigo()) {
                    case 1:
                        int cont = 0;
                        for (PosiblesRespuestas respuestas : pregunta.getRespuestas()) {
                            CheckBox checkBox = crearCheckBox(respuestas.getTexto(), getContext());
                            if (pregunta.getSelecresp() != null && !pregunta.getSelecresp().isEmpty()) {
                                for (String cadena : pregunta.getSelecresp()) {
                                    if (cadena.equals(Integer.toString(respuestas.getCodigo()))) {
                                        checkBox.setChecked(true);
                                        break;
                                    }
                                }
                            }
                            checkBox.setOnCheckedChangeListener(new MarcarCheckBox(pregunta, cont, arrSeccionCondicion, idseccion, checkBox.getId()));
                            cont++;
                            content.addView(checkBox);
                        }
                        break;
                    case 2:
                        Spinner spinner = crearSpinner(pregunta.getRespuestas(), getContext());
                        cont = 0;
                        for (PosiblesRespuestas respuestas : pregunta.getRespuestas()) {
                            if (pregunta.getTxtrespuesta() != null) {
                                if (pregunta.getTxtrespuesta().equals(Integer.toString(respuestas.getCodigo()))) {
                                    spinner.setSelection(cont);
                                    break;
                                }
                            }
                            cont++;
                        }
                        spinner.setOnItemSelectedListener(new SeleccionarItem(pregunta, arrSeccionCondicion, idseccion, spinner.getId()));
                        content.addView(spinner);
                        break;
                    case 3:
                        EditText editText = crearCuadroDeTexto(getContext());
                        if (pregunta.getTxtrespuesta() != null) {
                            editText.setText(pregunta.getTxtrespuesta());
                        }

                        if (pregunta.isRequerido()) {
                            editText.setHint(getResources().getString(lbl_requerido));
                            editText.setHintTextColor(getColor(context, gris1));
                        }

                        editText.addTextChangedListener(new ListenerEditText(pregunta));
                        editText.setPadding(5, 5, 5, 5);

                        content.addView(editText);
                        break;
                    case 4:
                        final EditText editTextNumeric = crearCuadroDeTextoNumerico(getContext());
                        if (pregunta.getTxtrespuesta() != null) {
                            editTextNumeric.setText(pregunta.getTxtrespuesta());
                        }

                        if (pregunta.isRequerido()) {
                            editTextNumeric.setHint(getResources().getString(lbl_requerido));
                            editTextNumeric.setHintTextColor(getColor(context, gris1));
                        }

                        editTextNumeric.setOnFocusChangeListener(new OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                //limpiarFocus(preguntas);
                                if (hasFocus) {
                                    pregunta.setHasfoco(true);
                                }
                            }
                        });

                        if (pregunta.isHasfoco()) {
                            editTextNumeric.requestFocus();
                        }

                        editTextNumeric.addTextChangedListener(new ListenerEditText(pregunta, arrSeccionCondicion, idseccion, editTextNumeric.getId()));

                        content.addView(editTextNumeric);
                        break;
                    case 5:
                        final EditText editTextEmail = crearCuadroDeTextoEmail(getContext());
                        if (pregunta.getTxtrespuesta() != null) {
                            editTextEmail.setText(pregunta.getTxtrespuesta());
                        }

                        if (pregunta.isRequerido()) {
                            editTextEmail.setHint(getResources().getString(lbl_requerido));
                            editTextEmail.setHintTextColor(getColor(context, gris1));
                        }

                        editTextEmail.setOnFocusChangeListener(new OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (!hasFocus) {
                                    if (!isValidEmail(editTextEmail.getText().toString())) {
                                        editTextEmail.setText("");
                                    }
                                }
                            }
                        });
                        editTextEmail.addTextChangedListener(new ListenerEditText(pregunta));

                        content.addView(editTextEmail);
                        break;
                    case 6:
                        final EditText editTextFecha = crearCuadroDeTextoFecha(getContext());
                        if (pregunta.getTxtrespuesta() != null) {
                            editTextFecha.setText(pregunta.getTxtrespuesta());
                        }

                        if (pregunta.isRequerido()) {
                            editTextFecha.setHint(getResources().getString(lbl_requerido));
                            editTextFecha.setHintTextColor(getColor(context, gris1));
                        }

                        editTextFecha.setOnFocusChangeListener(new OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (hasFocus) {
                                    DialogDatePicker dialog = new DialogDatePicker();
                                    dialog.setOnDataSelectListener(new OnDataSelectListener() {
                                        @Override
                                        public void OnDataSelect(String fech) {
                                            editTextFecha.setText(fech);
                                        }
                                    });
                                    dialog.show(fragmentManager, "");
                                }
                            }
                        });

                        editTextFecha.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (editTextFecha.hasFocus()) {
                                    DialogDatePicker dialog = new DialogDatePicker();
                                    dialog.setOnDataSelectListener(new OnDataSelectListener() {
                                        @Override
                                        public void OnDataSelect(String fech) {
                                            editTextFecha.setText(fech);
                                        }
                                    });
                                    dialog.show(fragmentManager, "");
                                }
                            }
                        });

                        editTextFecha.addTextChangedListener(new ListenerEditText(pregunta));

                        content.addView(editTextFecha);
                        break;
                    case 8:
                        final EditText editTextEdad = crearCuadroDeTextoNumerico(getContext());
                        if (pregunta.getTxtrespuesta() != null) {
                            editTextEdad.setText(pregunta.getTxtrespuesta());
                        }

                        if (pregunta.isRequerido()) {
                            editTextEdad.setHint(getResources().getString(lbl_requerido));
                            editTextEdad.setHintTextColor(getColor(context, gris1));
                        }

                        editTextEdad.setOnFocusChangeListener(new OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (hasFocus) {
                                    pregunta.setHasfoco(true);
                                }
                            }
                        });

                        if (pregunta.isHasfoco()) {
                            editTextEdad.requestFocus();
                        }

                        editTextEdad.setOnFocusChangeListener(new OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (hasFocus) {
                                    calcularFecha(editTextEdad);
                                }
                            }
                        });

                        editTextEdad.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                calcularFecha(editTextEdad);
                            }
                        });

                        editTextEdad.addTextChangedListener(new ListenerEditText(pregunta, arrSeccionCondicion, idseccion, editTextEdad.getId()));
                        content.addView(editTextEdad);
                        break;
                    case 9:
                        final EditText editTextTel = crearCuadroDeTextoTel(getContext());
                        if (pregunta.getTxtrespuesta() != null) {
                            editTextTel.setText(pregunta.getTxtrespuesta());
                        }

                        if (pregunta.isRequerido()) {
                            editTextTel.setHint(getResources().getString(lbl_requerido));
                            editTextTel.setHintTextColor(getColor(context, gris1));
                        }
                        editTextTel.addTextChangedListener(new ListenerEditText(pregunta));
                        content.addView(editTextTel);
                        break;
                    case 11:
                        final EditText editTextHora = crearCuadroDeTextoFecha(getContext());
                        if (pregunta.getTxtrespuesta() != null) {
                            editTextHora.setText(pregunta.getTxtrespuesta());
                        }

                        if (pregunta.isRequerido()) {
                            editTextHora.setHint(getResources().getString(lbl_requerido));
                            editTextHora.setHintTextColor(getColor(context, gris1));
                        }

                        final DialogTimerPicker dialog = new DialogTimerPicker();

                        editTextHora.setOnFocusChangeListener(new OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (hasFocus) {
                                    if (!dialog.isVisible()) {
                                        dialog.setOnDataSelectListener(new OnDataSelectListener() {
                                            @Override
                                            public void OnDataSelect(String fech) {
                                                editTextHora.setText(fech);
                                            }
                                        });
                                        dialog.show(fragmentManager, "");
                                    }
                                }
                            }
                        });

                        editTextHora.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!dialog.isVisible()) {
                                    dialog.setOnDataSelectListener(new OnDataSelectListener() {
                                        @Override
                                        public void OnDataSelect(String fech) {
                                            editTextHora.setText(fech);
                                        }
                                    });
                                    dialog.show(fragmentManager, "");
                                }
                            }
                        });
                        editTextHora.addTextChangedListener(new ListenerEditText(pregunta));

                        content.addView(editTextHora);
                        break;
                    case 12:
                        final ListaDinamicaView lstDinamica = crearListDinamica(getContext());
                        final DialogInputView dialogInputView = new DialogInputView();
                        lstDinamica.setOnAddItemListener(new OnAddItemListener() {
                            @Override
                            public void OnAddItem() {
                                dialogInputView.setOnClickDialogInputListener(new OnClickDialogInputListener() {
                                    @Override
                                    public void OnPositiveClick(DialogInterface dialog, String tag, String inputText) {
                                        pregunta.responder(inputText);
                                        lstDinamica.setListItems(pregunta.getSelecresp());

                                        lstDinamica.setLayoutsize(lstDinamica.getLayoutsize() + 100);
                                        LayoutParams newViewParams = new LayoutParams(MATCH_PARENT, lstDinamica.getLayoutsize());
                                        lstDinamica.setLayoutParams(newViewParams);

                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void OnNegativeClick(DialogInterface dialog, String tag) {
                                        dialog.dismiss();
                                    }
                                });
                                dialogInputView.show(fragmentManager, "");
                            }
                        });
                        lstDinamica.setOnDeleteListener(new OnDeleteListener() {
                            @Override
                            public void OnDelete(int position) {
                                lstDinamica.setLayoutsize(lstDinamica.getLayoutsize() - 100);
                                LayoutParams newViewParams = new LayoutParams(MATCH_PARENT, lstDinamica.getLayoutsize());
                                lstDinamica.setLayoutParams(newViewParams);
                            }
                        });
                        content.addView(lstDinamica);
                        break;
                    case 13:
                        final LectorCodigosView lectorCodigosView = crearLectorQr(context);
                        content.addView(lectorCodigosView);
                        lectorCodigosView.setOnReadCodeListener(new OnReadCodeListener() {
                            @Override
                            public void OnReadCode(String codigo) {
                                pregunta.responder(codigo);
                            }
                        });
                        break;
                }
            }
        }
    }

    private void calcularFecha(final EditText editText) {
        DialogDatePicker dialog = new DialogDatePicker();
        dialog.setOnDataSelectListener(new OnDataSelectListener() {
            @Override
            public void OnDataSelect(String fech) {
                try {
                    Date fechaNac = new SimpleDateFormat("yyyy-MM-dd").parse(fech);
                    Calendar fechaNacimiento = getInstance();
                    Calendar fechaActual = getInstance();
                    fechaNacimiento.setTime(fechaNac);
                    int year = fechaActual.get(YEAR) - fechaNacimiento.get(YEAR);
                    int mes = fechaActual.get(MONTH) - fechaNacimiento.get(MONTH);
                    int dia = fechaActual.get(DATE) - fechaNacimiento.get(DATE);
                    if (mes < 0 || (mes == 0 && dia < 0)) {
                        year--;
                    }
                    editText.setText(year + "");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.show(fragmentManager, "");
    }

    private class MarcarCheckBox implements OnCheckedChangeListener {

        private Pregunta pregunta;
        private int posicion;
        private ArrayList<SeccionCondicion> arrseccionCondicion;
        private int idseccion;
        private int idview;


        public MarcarCheckBox(Pregunta pregunta, int posicion, ArrayList<SeccionCondicion> arrseccionCondicion, int idseccion, int idview) {
            this.pregunta = pregunta;
            this.posicion = posicion;
            this.arrseccionCondicion = arrseccionCondicion;
            this.idseccion = idseccion;
            this.idview = idview;
        }


        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                pregunta.responder(Integer.toString(pregunta.getRespuestas().get(posicion).getCodigo()));
            } else {
                pregunta.removeRespuesta(Integer.toString(pregunta.getRespuestas().get(posicion).getCodigo()));
            }

            if (pregunta.getPreguntaCondicions().size() > 0) {
                for (PreguntaCondicion condicion : pregunta.getPreguntaCondicions()) {
                    switch (condicion.getCondicion()) {
                        case 1:
                            if (pregunta.getRespuestas().get(posicion).getCodigo() == parseInt(condicion.getValor())) {
                                if (isChecked) {
                                    bus.post(new ValidatePreguntaEvt(true, condicion.getId_pregunta_condicionada(), idview));
                                } else {
                                    bus.post(new ValidatePreguntaEvt(false, condicion.getId_pregunta_condicionada(), idview));
                                }
                            }
                            break;
                    }
                }
            }

            if (arrseccionCondicion != null && arrseccionCondicion.size() > 0) {
                for (SeccionCondicion seccionCondicion : arrseccionCondicion) {
                    if (pregunta.getIdpregunta() == seccionCondicion.getIdpreguntacondicionante()) {
                        switch (seccionCondicion.getCondicion()) {
                            case 1:
                                if (pregunta.getRespuestas().get(posicion).getCodigo() == parseInt(seccionCondicion.getValor())) {
                                    if (isChecked) {
                                        bus.post(new ValidarSeccion(true, idseccion, seccionCondicion.getIdseccionsiguiente()));
                                    } else {
                                        bus.post(new ValidarSeccion(false, idseccion, seccionCondicion.getIdseccionsiguiente()));
                                    }
                                }
                                break;
                        }
                        break;
                    }
                }
            }
        }
    }

    private class SeleccionarItem implements OnItemSelectedListener {

        private Pregunta pregunta;
        private ArrayList<SeccionCondicion> arrseccionCondicion;
        private int idseccion;
        private int idview;


        public SeleccionarItem(Pregunta pregunta) {
            this.pregunta = pregunta;
        }

        public SeleccionarItem(Pregunta pregunta, ArrayList<SeccionCondicion> arrseccionCondicion, int idseccion, int idview) {
            this.pregunta = pregunta;
            this.arrseccionCondicion = arrseccionCondicion;
            this.idseccion = idseccion;
            this.idview = idview;
        }


        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            pregunta.responder(Integer.toString(pregunta.getRespuestas().get(position).getCodigo()));
            if (pregunta.getPreguntaCondicions().size() > 0) {
                for (PreguntaCondicion condicion : pregunta.getPreguntaCondicions()) {
                    switch (condicion.getCondicion()) {
                        case 1:
                            if (position == parseInt(condicion.getValor())) {
                                bus.post(new ValidatePreguntaEvt(true, condicion.getId_pregunta_condicionada(), idview));
                            } else {
                                bus.post(new ValidatePreguntaEvt(false, condicion.getId_pregunta_condicionada(), idview));
                            }
                            break;
                        case 6:
                            if (position != parseInt(condicion.getValor())) {
                                bus.post(new ValidatePreguntaEvt(true, condicion.getId_pregunta_condicionada(), idview));
                            } else {
                                bus.post(new ValidatePreguntaEvt(false, condicion.getId_pregunta_condicionada(), idview));
                            }
                            break;
                    }
                }
            }

            if (arrseccionCondicion != null && arrseccionCondicion.size() > 0) {
                for (SeccionCondicion seccionCondicion : arrseccionCondicion) {
                    if (pregunta.getIdpregunta() == seccionCondicion.getIdpreguntacondicionante()) {
                        switch (seccionCondicion.getCondicion()) {
                            case 1:
                                if (pregunta.getTxtrespuesta().equals(seccionCondicion.getValor())) {
                                    bus.post(new ValidarSeccion(true, idseccion, seccionCondicion.getIdseccionsiguiente()));
                                } else {
                                    bus.post(new ValidarSeccion(false, idseccion, seccionCondicion.getIdseccionsiguiente()));
                                }
                                break;
                            case 6:
                                if (!pregunta.getTxtrespuesta().equals(seccionCondicion.getValor())) {
                                    bus.post(new ValidarSeccion(true, idseccion, seccionCondicion.getIdseccionsiguiente()));
                                } else {
                                    bus.post(new ValidarSeccion(false, idseccion, seccionCondicion.getIdseccionsiguiente()));
                                }
                                break;
                        }
                        break;
                    }
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    private class ListenerEditText implements TextWatcher {

        private Pregunta pregunta;
        private ArrayList<SeccionCondicion> arrseccionCondicion;
        private int idseccion;
        private int idview;

        public ListenerEditText(Pregunta pregunta) {
            this.pregunta = pregunta;
        }

        public ListenerEditText(Pregunta pregunta, ArrayList<SeccionCondicion> arrseccionCondicion, int idseccion, int idview) {
            this.pregunta = pregunta;
            this.arrseccionCondicion = arrseccionCondicion;
            this.idseccion = idseccion;
            this.idview = idview;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }


        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            this.pregunta.responder(s.toString());

            if (this.pregunta.getTipo().equals(CUADRO_TEXTO_NUMERICO) ||
                    this.pregunta.getTipo().equals(FECHA_CALCULO)) {
                if (this.pregunta.getPreguntaCondicions().size() > 0) {

                    int valor = 0;
                    if (s.toString().equals("")) {
                        valor = 0;
                    } else {
                        valor = parseInt(s.toString());
                    }

                    for (PreguntaCondicion condicion : pregunta.getPreguntaCondicions()) {
                        switch (condicion.getCondicion()) {
                            case 2:
                                if (valor > parseInt(condicion.getValor())) {
                                    bus.post(new ValidatePreguntaEvt(true, condicion.getId_pregunta_condicionada(), idview));
                                } else {
                                    bus.post(new ValidatePreguntaEvt(false, condicion.getId_pregunta_condicionada(), idview));
                                }
                                break;
                            case 3:
                                if (valor < parseInt(condicion.getValor())) {
                                    bus.post(new ValidatePreguntaEvt(true, condicion.getId_pregunta_condicionada(), idview));
                                } else {
                                    bus.post(new ValidatePreguntaEvt(false, condicion.getId_pregunta_condicionada(), idview));
                                }
                                break;
                            case 4:
                                if (valor >= parseInt(condicion.getValor())) {
                                    bus.post(new ValidatePreguntaEvt(true, condicion.getId_pregunta_condicionada(), idview));
                                } else {
                                    bus.post(new ValidatePreguntaEvt(false, condicion.getId_pregunta_condicionada(), idview));
                                }
                                break;
                            case 5:
                                if (valor <= parseInt(condicion.getValor())) {
                                    bus.post(new ValidatePreguntaEvt(true, condicion.getId_pregunta_condicionada(), idview));
                                } else {
                                    bus.post(new ValidatePreguntaEvt(false, condicion.getId_pregunta_condicionada(), idview));
                                }
                                break;
                        }
                    }

                    if (arrseccionCondicion != null && arrseccionCondicion.size() > 0) {
                        for (SeccionCondicion seccionCondicion : arrseccionCondicion) {
                            if (pregunta.getIdpregunta() == seccionCondicion.getIdpreguntacondicionante()) {
                                switch (seccionCondicion.getCondicion()) {
                                    case 2:
                                        if (valor > parseInt(seccionCondicion.getValor())) {
                                            bus.post(new ValidarSeccion(true, idseccion, seccionCondicion.getIdseccionsiguiente()));
                                        } else {
                                            bus.post(new ValidarSeccion(false, idseccion, seccionCondicion.getIdseccionsiguiente()));
                                        }
                                        break;
                                    case 3:
                                        if (valor < parseInt(seccionCondicion.getValor())) {
                                            bus.post(new ValidarSeccion(true, idseccion, seccionCondicion.getIdseccionsiguiente()));
                                        } else {
                                            bus.post(new ValidarSeccion(false, idseccion, seccionCondicion.getIdseccionsiguiente()));
                                        }
                                        break;
                                    case 4:
                                        if (valor >= parseInt(seccionCondicion.getValor())) {
                                            bus.post(new ValidarSeccion(true, idseccion, seccionCondicion.getIdseccionsiguiente()));
                                        } else {
                                            bus.post(new ValidarSeccion(false, idseccion, seccionCondicion.getIdseccionsiguiente()));
                                        }
                                        break;
                                    case 5:
                                        if (valor <= parseInt(seccionCondicion.getValor())) {
                                            bus.post(new ValidarSeccion(true, idseccion, seccionCondicion.getIdseccionsiguiente()));
                                        } else {
                                            bus.post(new ValidarSeccion(false, idseccion, seccionCondicion.getIdseccionsiguiente()));
                                        }
                                        break;
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

}
