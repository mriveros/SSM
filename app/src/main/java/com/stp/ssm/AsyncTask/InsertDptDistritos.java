package com.stp.ssm.AsyncTask;

import android.content.Context;
import android.os.AsyncTask;
import com.stp.ssm.databases.BDFuntions;

public class InsertDptDistritos extends AsyncTask<Void,Void,Void> {

    private BDFuntions dbFuntions;
    private Context context;

    public InsertDptDistritos(BDFuntions dbFuntions, Context context) {
        this.dbFuntions = dbFuntions;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        dbFuntions.insertarDpto();
        dbFuntions.insertarDistritos(context);
        return null;
    }
}
