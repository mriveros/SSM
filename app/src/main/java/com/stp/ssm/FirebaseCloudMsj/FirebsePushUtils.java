package com.stp.ssm.FirebaseCloudMsj;

import android.content.Context;
import com.google.firebase.iid.FirebaseInstanceId;
import com.stp.ssm.http.HttpVolleyRequest;
import com.stp.ssm.Util.SessionData;
import com.stp.ssm.http.URLs;
import java.util.HashMap;
import java.util.Map;
import static com.google.firebase.iid.FirebaseInstanceId.getInstance;
import static com.stp.ssm.http.URLs.URL_FIREBASE_TOKEN_REGISTER;

public class FirebsePushUtils {

    public static void registarToken(Context context, String cedusuario) {
        String token = getInstance().getToken();
        Map<String, String> parametros = new HashMap<>();
        parametros.put("cedusuario", cedusuario);
        parametros.put("firebasetoken", token);

        HttpVolleyRequest.getInstance(context).registrarTokenPush(URL_FIREBASE_TOKEN_REGISTER,
                parametros,
                SessionData.getInstance(context).getToken());
        SessionData.getInstance(context).saveTokenFirebase(token);
    }
}
