package com.stp.ssm.View;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.stp.ssm.Evt.ReadCodigoEvt;
import com.stp.ssm.Interfaces.OnReadCodeListener;
import com.stp.ssm.R;
import com.stp.ssm.ReadBarcoderActivity;
import de.greenrobot.event.EventBus;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Intent.CATEGORY_DEFAULT;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.layout.view_lector_qr;
import static com.stp.ssm.ReadBarcoderActivity.ACTION_READ_CODE;

public class LectorCodigosView extends LinearLayout {

    private EditText edtViewLector;
    private Button btnViewLector;
    private Context context;
    private MyReceiver myReceiver;
    private OnReadCodeListener onReadCodeListener;

    public LectorCodigosView(Context context) {
        super(context);
        this.context = context;
        inicializar();
    }

    private void inicializar() {
        String infService = LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        li.inflate(view_lector_qr, this, true);

        edtViewLector = (EditText) findViewById(R.id.edtViewLector);
        btnViewLector = (Button) findViewById(id.btnViewLector);

        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_READ_CODE);
        intentFilter.addCategory(CATEGORY_DEFAULT);
        getContext().registerReceiver(myReceiver, intentFilter);

        asignarEventos();
    }

    public void setOnReadCodeListener(OnReadCodeListener onReadCodeListener) {
        this.onReadCodeListener = onReadCodeListener;
    }

    private void asignarEventos() {
        btnViewLector.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ReadBarcoderActivity.class));
            }
        });

        edtViewLector.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                onReadCodeListener.OnReadCode(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            edtViewLector.setText(intent.getExtras().getString("readcode"));
            onReadCodeListener.OnReadCode(intent.getExtras().getString("readcode"));
        }
    }
}
