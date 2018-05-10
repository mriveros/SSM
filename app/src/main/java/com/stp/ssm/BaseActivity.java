package com.stp.ssm;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;

import com.stp.ssm.Util.SessionData;
import com.stp.ssm.databases.BDFuntions;

import de.greenrobot.event.EventBus;

import static android.app.ProgressDialog.STYLE_HORIZONTAL;
import static android.content.DialogInterface.BUTTON_POSITIVE;
import static android.content.DialogInterface.OnClickListener;
import static com.stp.ssm.R.string.lbl_segundoplano;
import static com.stp.ssm.Util.SessionData.getInstance;
import static de.greenrobot.event.EventBus.getDefault;

public abstract class BaseActivity extends AppCompatActivity {

    protected SessionData sessionData;
    protected EventBus eventBus;
    protected BDFuntions dbFuntions;
    protected ProgressDialog barProgressDialog;

    //private final String SHOWCASE_ID = "secuence_showcase";
    //private ShowcaseConfig showcaseConfig;
    //private MaterialShowcaseSequence sequence;


    protected void inicializar() {
        sessionData = getInstance(getApplicationContext());
        eventBus = getDefault();
        dbFuntions = new BDFuntions(getApplicationContext());
    }

    public void showProgressDialog(String titulo, String mensaje) {
        if (barProgressDialog == null) {
            barProgressDialog = new ProgressDialog(BaseActivity.this);
        }
        barProgressDialog.setTitle(titulo);
        barProgressDialog.setMessage(mensaje);
        barProgressDialog.setIndeterminate(true);
        barProgressDialog.setCancelable(false);
        barProgressDialog.setCanceledOnTouchOutside(false);
        barProgressDialog.show();
    }


    public void showProgressBarDialog(String mensaje, int total) {
        if (barProgressDialog == null) {
            barProgressDialog = new ProgressDialog(BaseActivity.this);
        }
        barProgressDialog.setCancelable(true);
        barProgressDialog.setMessage(mensaje);
        barProgressDialog.setProgressStyle(STYLE_HORIZONTAL);
        barProgressDialog.setProgress(0);
        barProgressDialog.setMax(total);
        barProgressDialog.setButton(BUTTON_POSITIVE, getString(lbl_segundoplano), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                barProgressDialog.dismiss();
            }
        });
        barProgressDialog.show();
    }

    public void offProgressDialog() {
        if (barProgressDialog != null && barProgressDialog.isShowing()) {
            barProgressDialog.dismiss();
        }
    }

    /*protected void iniciarHelp(ArrayList<HelpObject> arr_help){
        if(showcaseConfig == null){
            showCaseCreate(arr_help);
        }else{
            MaterialShowcaseView.resetSingleUse(this, SHOWCASE_ID);
            showCaseCreate(arr_help);
        }
        sequence.start();
    }

    private void showCaseCreate(ArrayList<HelpObject> arr_help){
        showcaseConfig = new ShowcaseConfig();
        showcaseConfig.setDelay(500);
        sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);
        sequence.setConfig(showcaseConfig);

        for(HelpObject helpObject:arr_help){
            sequence.addSequenceItem(
                    new MaterialShowcaseView.Builder(this)
                            .setTarget(helpObject.getView())
                            .setDismissText(getString(R.string.lbl_hecho))
                            .setContentText(helpObject.getTexto())
                            .withRectangleShape(true)
                            .build()
            );
        }
    }*/

    @Override
    protected void onDestroy() {
        getDefault().unregister(this);
        super.onDestroy();
    }
}