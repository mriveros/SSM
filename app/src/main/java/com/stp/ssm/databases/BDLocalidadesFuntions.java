package com.stp.ssm.databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import com.stp.ssm.Model.Localidad;
import com.stp.ssm.Util.CellUtils;
import java.io.File;
import java.util.ArrayList;

public class BDLocalidadesFuntions {

    private static BDLocalidadesFuntions INSTANCE;
    private Context context;
    private SQLiteDatabase myDataBase;

    private BDLocalidadesFuntions(Context context){
        this.context = context;
    }


    private synchronized static void createInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new BDLocalidadesFuntions(context);
        }
    }


    public static BDLocalidadesFuntions getInstance(Context context) {
        if (INSTANCE == null) createInstance(context);
        return INSTANCE;
    }


    public void openDataBase() {
        String myPath = "/data/data/com.stp.ssm/databases/localidades";
        try{
            myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        }catch (SQLiteCantOpenDatabaseException ex){
            ex.printStackTrace();
            File dbFile = new File("/data/data/com.stp.ssm/databases/localidades");
            if(!dbFile.exists()){
                CellUtils.copyDatabaseLocalidad(context);
                myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
            }
        }

    }


    public ArrayList<Localidad> getLocalidadByDistrito(int codDpto, int codDistrito){
        ArrayList<Localidad >localidades = new ArrayList<>();
        openDataBase();
        String Consulta = "SELECT localidad_id,descripcion FROM localidad WHERE departamento_id="+codDpto+" AND " + "distrito_id="+codDistrito;
        Cursor cursor = myDataBase.rawQuery(Consulta,null);
        if(cursor.moveToFirst()){
            do{
                localidades.add(new Localidad(cursor.getInt(0),cursor.getString(1)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        myDataBase.close();
        return localidades;
    }
}
