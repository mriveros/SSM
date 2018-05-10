package com.stp.ssm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.stp.ssm.Evt.ProviderEnbledEvt;
import com.stp.ssm.Util.CellUtils;

import de.greenrobot.event.EventBus;

import static android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS;
import static android.view.View.OnClickListener;
import static android.view.Window.FEATURE_NO_TITLE;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.activity_alert_location;
import static com.stp.ssm.R.string;
import static com.stp.ssm.R.string.dialog_msj_provider_disable;
import static com.stp.ssm.Util.CellUtils.checkLocationGPS;
import static com.stp.ssm.Util.CellUtils.checkLocationNetwork;
import static com.stp.ssm.Util.CellUtils.checkNetworkConnect;
import static de.greenrobot.event.EventBus.getDefault;

public class AlertProviderActivity extends AppCompatActivity {

    private TextView lblproviderAlert;
    private Button btnActivarProvider;
    private Handler handler = new Handler();
    protected EventBus eventBus;
    private final int TIME_SLEEP_RETURN = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(FEATURE_NO_TITLE);
        setContentView(activity_alert_location);
        this.setFinishOnTouchOutside(false);

        eventBus = getDefault();
        eventBus.register(this);
        Bundle bundle = getIntent().getExtras();

        lblproviderAlert = (TextView) findViewById(id.lblproviderAlert);
        lblproviderAlert.setText(getString(dialog_msj_provider_disable) + bundle.getString("provider"));
        btnActivarProvider = (Button) findViewById(id.btnActivarProvider);

        asignarEventos();
    }

    private void asignarEventos() {
        btnActivarProvider.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, 100);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        handler.postDelayed(new Runnable() {
            public void run() {
                if (checkLocationGPS(getApplicationContext()) && checkLocationNetwork(getApplicationContext())) {
                    finish();
                }
            }
        }, TIME_SLEEP_RETURN);
    }

    public void onEvent(ProviderEnbledEvt evt) {
        if (checkLocationGPS(getApplicationContext()) && checkNetworkConnect(getApplicationContext())) {
            finish();
        }
    }
}
