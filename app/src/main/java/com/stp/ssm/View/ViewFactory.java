package com.stp.ssm.View;

import android.content.Context;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.stp.ssm.Model.PosiblesRespuestas;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

import com.stp.ssm.Model.Pregunta;
import com.stp.ssm.R;
import com.stp.ssm.Util.ImageFileUtil;
import com.stp.ssm.Util.SessionData;
import com.stp.ssm.http.URLs;

import static android.os.Build.VERSION;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES;
import static android.os.Build.VERSION_CODES.JELLY_BEAN;
import static android.os.Build.VERSION_CODES.M;
import static android.support.v4.content.ContextCompat.getColor;
import static android.support.v4.content.ContextCompat.getDrawable;
import static android.text.InputFilter.LengthFilter;
import static android.text.InputType.TYPE_CLASS_DATETIME;
import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_CLASS_PHONE;
import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_DATETIME_VARIATION_DATE;
import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;
import static android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
import static android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
import static android.view.Gravity.CENTER_HORIZONTAL;
import static android.view.Gravity.TOP;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.view.inputmethod.EditorInfo.IME_FLAG_NO_ENTER_ACTION;
import static android.widget.LinearLayout.LayoutParams;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static com.bumptech.glide.Glide.with;
import static com.bumptech.glide.load.model.LazyHeaders.Builder;
import static com.stp.ssm.R.color;
import static com.stp.ssm.R.color.azul1;
import static com.stp.ssm.R.color.negro1;
import static com.stp.ssm.R.drawable;
import static com.stp.ssm.R.drawable.broken;
import static com.stp.ssm.R.drawable.btn_dropdown;
import static com.stp.ssm.R.drawable.custom_edt_cursor;
import static com.stp.ssm.R.drawable.selector_edittext;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.id.lbl_spinne_item_2;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.spinner_item_2;
import static com.stp.ssm.R.raw;
import static com.stp.ssm.R.raw.loading;
import static com.stp.ssm.R.style;
import static com.stp.ssm.R.style.MyCheckBox;
import static com.stp.ssm.Util.ImageFileUtil.getNameImagen;
import static com.stp.ssm.Util.SessionData.getInstance;
import static com.stp.ssm.http.URLs.URL_IMAGENES;

public class ViewFactory {

    public static TextView createTextView(String content, int size, Context context, int color) {
        TextView tv = new TextView(context);
        tv.setText(content);
        tv.setTextColor(getColor(context, color));
        tv.setTextSize(size);
        LayoutParams params = new LayoutParams(MATCH_PARENT,
                WRAP_CONTENT);
        tv.setLayoutParams(params);
        return tv;
    }

    public static Button createButton(String label, Context context) {
        Button btn = new Button(context);
        btn.setText(label);
        LayoutParams params = new LayoutParams(MATCH_PARENT,
                MATCH_PARENT);
        btn.setLayoutParams(params);
        return btn;
    }

    public static CheckBox crearCheckBox(String cadena, Context context) {
        CheckBox checkBox = new CheckBox(context);
        checkBox.setText(cadena);
        checkBox.setTextColor(getColor(context, negro1));
        LayoutParams params = new LayoutParams(WRAP_CONTENT,
                WRAP_CONTENT);
        checkBox.setLayoutParams(params);

        if (SDK_INT > JELLY_BEAN) {
            if (SDK_INT >= M) {
                checkBox.setTextAppearance(MyCheckBox);
            } else {
                checkBox.setTextAppearance(context, MyCheckBox);
            }
        }

        return checkBox;
    }


    public static Spinner crearSpinner(ArrayList<PosiblesRespuestas> respuestas, Context context) {
        Spinner spinner = new Spinner(context);
        spinner.setAdapter(new ArrayAdapter<PosiblesRespuestas>(context, spinner_item_2, lbl_spinne_item_2, respuestas));
        LayoutParams params = new LayoutParams(MATCH_PARENT,
                WRAP_CONTENT);
        spinner.setLayoutParams(params);

        if (SDK_INT < JELLY_BEAN) {
            spinner.setBackgroundDrawable(context.getResources().getDrawable(btn_dropdown));
        } else {
            spinner.setBackground(getDrawable(context, btn_dropdown));
        }
        return spinner;
    }


    public static EditText crearCuadroDeTexto(Context context) {
        EditText editText = new EditText(context);
        float desity = context.getResources().getDisplayMetrics().density;
        int alto = 200;
        if (desity >= 3.0) {
            alto = 400;
        }

        LayoutParams params = new LayoutParams(MATCH_PARENT, alto);
        editText.setLayoutParams(params);

        editText.setSingleLine(false);
        editText.setImeOptions(IME_FLAG_NO_ENTER_ACTION);
        editText.setGravity(TOP);

        if (SDK_INT < JELLY_BEAN) {
            editText.setBackgroundDrawable(context.getResources().getDrawable(selector_edittext));
        } else {
            editText.setBackground(getDrawable(context, selector_edittext));
        }

        try {
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(editText, custom_edt_cursor);
        } catch (Exception ignored) {
        }
        editText.setTextColor(getColor(context, negro1));
        return editText;
    }


    public static EditText crearCuadroDeTextoNumerico(Context context) {
        EditText editText = new EditText(context);
        LayoutParams params = new LayoutParams(MATCH_PARENT,
                WRAP_CONTENT);
        editText.setLayoutParams(params);
        editText.setImeOptions(IME_FLAG_NO_ENTER_ACTION);
        editText.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_DECIMAL);

        if (SDK_INT < JELLY_BEAN) {
            editText.setBackgroundDrawable(context.getResources().getDrawable(selector_edittext));
        } else {
            editText.setBackground(getDrawable(context, selector_edittext));
        }
        try {

            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(editText, custom_edt_cursor);
        } catch (Exception ignored) {
        }
        editText.setTextColor(getColor(context, negro1));
        return editText;
    }


    public static EditText crearCuadroDeTextoEmail(Context context) {
        EditText editText = new EditText(context);
        LayoutParams params = new LayoutParams(MATCH_PARENT,
                WRAP_CONTENT);
        editText.setLayoutParams(params);
        editText.setImeOptions(IME_FLAG_NO_ENTER_ACTION);
        editText.setInputType(TYPE_CLASS_TEXT | TYPE_TEXT_FLAG_NO_SUGGESTIONS | TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        if (SDK_INT < JELLY_BEAN) {
            editText.setBackgroundDrawable(context.getResources().getDrawable(selector_edittext));
        } else {
            editText.setBackground(getDrawable(context, selector_edittext));
        }

        try {
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(editText, custom_edt_cursor);
        } catch (Exception ignored) {
        }

        editText.setTextColor(getColor(context, negro1));
        return editText;
    }


    public static EditText crearCuadroDeTextoFecha(Context context) {
        final EditText editText = new EditText(context);
        LayoutParams params = new LayoutParams(MATCH_PARENT,
                WRAP_CONTENT);
        editText.setLayoutParams(params);
        editText.setImeOptions(IME_FLAG_NO_ENTER_ACTION);
        editText.setInputType(TYPE_CLASS_DATETIME | TYPE_DATETIME_VARIATION_DATE);
        //editText.setFocusable(false);
        editText.setClickable(true);

        if (SDK_INT < JELLY_BEAN) {
            editText.setBackgroundDrawable(context.getResources().getDrawable(selector_edittext));
        } else {
            editText.setBackground(getDrawable(context, selector_edittext));
        }
        editText.setTextColor(getColor(context, negro1));
        return editText;
    }

    public static EditText crearCuadroDeTextoTel(Context context) {
        final EditText editText = new EditText(context);
        LayoutParams params = new LayoutParams(MATCH_PARENT,
                WRAP_CONTENT);
        editText.setLayoutParams(params);
        editText.setImeOptions(IME_FLAG_NO_ENTER_ACTION);
        editText.setInputType(TYPE_CLASS_PHONE);
        editText.setFilters(new InputFilter[]{new LengthFilter(10)});

        if (SDK_INT < JELLY_BEAN) {
            editText.setBackgroundDrawable(context.getResources().getDrawable(selector_edittext));
        } else {
            editText.setBackground(getDrawable(context, selector_edittext));
        }
        editText.setTextColor(getColor(context, negro1));
        return editText;
    }


    public static ImageView crearImagenView(String image, Context context) {
        ImageView iv = new ImageView(context);
        iv.setLayoutParams(new ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        File file = new File(image);
        if (file.exists()) {
            with(context).load(new File(image)).into(iv);
        } else {

            SessionData sessionData = getInstance(context);
            GlideUrl glideUrl = new GlideUrl(URL_IMAGENES + getNameImagen(image),
                    new Builder().addHeader("Authorization", sessionData.getToken()).build());

            with(context)
                    .load(glideUrl)
                    .centerCrop()
                    .placeholder(loading)
                    .crossFade()
                    .error(broken)
                    .into(iv);
        }

        return iv;
    }


    public static LstSubFormView crearListSubFormularios(Context context) {
        LstSubFormView lst = new LstSubFormView(context);
        lst.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        return lst;
    }


    public static LstAddPersonaView crearListAddPersona(Context context, FragmentManager fragmentManager) {
        LstAddPersonaView lst = new LstAddPersonaView(context, fragmentManager);
        lst.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        return lst;
    }

    public static ListaDinamicaView crearListDinamica(Context context) {
        ListaDinamicaView lst = new ListaDinamicaView(context);
        lst.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, 200));
        return lst;
    }

    public static LinearLayout crearLinea(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, 3));
        linearLayout.setBackgroundColor(getColor(context, azul1));
        return linearLayout;
    }

    public static LectorCodigosView crearLectorQr(Context context) {
        LectorCodigosView lectorQr = new LectorCodigosView(context);
        LayoutParams params = new LayoutParams(MATCH_PARENT,
                WRAP_CONTENT);
        lectorQr.setLayoutParams(params);
        return lectorQr;
    }

    public static CustomImagenView crearImagenPregunta(Context context, Pregunta pregunta) {
        CustomImagenView customImagenView = new CustomImagenView(context, pregunta);
        LayoutParams params = new LayoutParams(MATCH_PARENT,
                WRAP_CONTENT);
        customImagenView.setLayoutParams(params);
        return customImagenView;
    }

    public static CustomCoordenadasView crearCoordenadasView(Context context, Pregunta pregunta) {
        CustomCoordenadasView customCoordenadasView = new CustomCoordenadasView(context, pregunta);
        LayoutParams params = new LayoutParams(MATCH_PARENT,
                WRAP_CONTENT);
        customCoordenadasView.setLayoutParams(params);
        return customCoordenadasView;
    }

    public static CustomListCoordenadasView crearListaCoordenadasView(Context context, Pregunta pregunta) {
        CustomListCoordenadasView customListCoordenadasView = new CustomListCoordenadasView(context, pregunta);
        LayoutParams params = new LayoutParams(MATCH_PARENT,
                WRAP_CONTENT);
        customListCoordenadasView.setLayoutParams(params);
        return customListCoordenadasView;
    }

    public static void notificacionToast(Context context, String mensaje) {
        Toast toast = makeText(context, mensaje, LENGTH_LONG);
        toast.setGravity(CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }
}