package com.stp.ssm.FirebaseCloudMsj;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.stp.ssm.Util.SessionData;

import static com.stp.ssm.Util.SessionData.getInstance;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private SessionData sessionData;

    @Override
    public void onCreate() {
        super.onCreate();
        sessionData = getInstance(getApplicationContext());
    }

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        sessionData.saveTokenFirebase(refreshedToken);
    }
}
