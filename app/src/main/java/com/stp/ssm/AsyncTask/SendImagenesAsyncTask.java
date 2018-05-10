package com.stp.ssm.AsyncTask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.stp.ssm.Evt.TokenExpirado;
import com.stp.ssm.Interfaces.OnSendImgFinishListener;
import com.stp.ssm.Model.Capturas;
import com.stp.ssm.Util.SessionData;
import com.stp.ssm.http.ApiResponse;
import com.stp.ssm.http.HttpCliente;
import com.stp.ssm.Util.ImageFileUtil;
import com.stp.ssm.databases.BDFuntions;
import com.stp.ssm.http.HttpStatus;
import com.stp.ssm.http.URLs;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.greenrobot.event.EventBus;

public class SendImagenesAsyncTask extends AsyncTask<List<String>,Void,Void> {

    private OnSendImgFinishListener onSendImgFinishListener;
    private Capturas capturas;
    private BDFuntions bdFuntions;
    private Context context;
    private HttpCliente httpCliente;
    private EventBus eventBus;

    public SendImagenesAsyncTask(Context context) {
        this.context = context;
        this.httpCliente = HttpCliente.getInstance();
        this.eventBus = EventBus.getDefault();
    }

    public void setOnSendImgFinishListener(OnSendImgFinishListener onSendImgFinishListener){
        this.onSendImgFinishListener = onSendImgFinishListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.bdFuntions = new BDFuntions(context);
    }

    @Override
    protected Void doInBackground(List<String>... params) {
        for(String cadena:params[0]){
            capturas = bdFuntions.getCapturaByName(cadena);
            Map<String,String> parametros = new HashMap<>();
            File file;
            String result = "";
            ApiResponse response = null;
            if(capturas == null || (capturas.getEstado() == 2 && capturas.getSync() == 1)){
                file = new File(cadena);
                parametros.put("hash", ImageFileUtil.getHashOfFile(file));
                parametros.put("fecha", ImageFileUtil.getDateOfFile(file));
                Log.i("parametros",parametros.toString());

                SessionData sessionData = SessionData.getInstance(context);
                String token = sessionData.getToken();
                if(!token.equals("")){
                    response = httpCliente.httpUpFile(parametros, URLs.URL_SEND_FILE_IMG,file,sessionData.getToken());
                    if(response.getHttpStatus() == HttpStatus.OK.getCodigo()){
                        interpretarResult(response.getTextReponse(),cadena);
                    }else if(response.getHttpStatus() == HttpStatus.UNAUTHORIZED.getCodigo()){
                        eventBus.post(new TokenExpirado());
                    }
                }else{
                    eventBus.post(new TokenExpirado());
                }
                parametros.clear();
            }
        }
        return null;
    }

    private void interpretarResult(String result,String pathimg){
        try {
            JSONObject jsonObject = new JSONObject(result);
            Log.i("Respuesta",result);
            switch (jsonObject.getInt("respuesta")){
                case 0:
                    bdFuntions.imagenSincronizada(pathimg);
                break;
                case 1:
                    File file = new File(pathimg);
                    if (file.exists()){
                        file.delete();
                    }
                break;
                case 2:
                break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        onSendImgFinishListener.OnSendImgFinish();
    }
}
