package com.stp.ssm;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import static android.media.RingtoneManager.TYPE_NOTIFICATION;
import static android.media.RingtoneManager.getDefaultUri;
import static android.media.RingtoneManager.getRingtone;
import static android.view.View.OnClickListener;
import static android.view.Window.FEATURE_NO_TITLE;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.activity_alert_session;

public class AlertSessionActivity extends AppCompatActivity {

    private Button btnAlertSession;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(FEATURE_NO_TITLE);
        setContentView(activity_alert_session);
        setFinishOnTouchOutside(false);

        btnAlertSession = (Button) findViewById(id.btnAlertSession);

        Uri notification = getDefaultUri(TYPE_NOTIFICATION);
        Ringtone r = getRingtone(getApplicationContext(), notification);
        r.play();

        Vibrator vi = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vi.vibrate(500);

        asignarEventos();
    }

    private void asignarEventos() {
        btnAlertSession.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}