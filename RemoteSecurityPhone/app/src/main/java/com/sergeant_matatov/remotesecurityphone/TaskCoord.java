package com.sergeant_matatov.remotesecurityphone;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

/**
 * Created by Yurka on 12.03.2016.
 */
public class TaskCoord extends AsyncTask<Void, Void, Void> {

    String coordinates;
    String lat;
    String lon;
    Context context;

    SharedPreferences latPref;    //сохраняет lat
    SharedPreferences lonPref;    //сохраняет lon
    final String SAVED_LAT = "saved_lat";
    final String SAVED_LON = "saved_lon";

    SharedPreferences flagLocalPref;    //сохраняет flag local (new/old)
    final String SAVED_FLAG_LOCAL = "saved_local_new";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        coordinates += ",";
        String[] strTempArr = coordinates.split(",");
        lat = strTempArr[0].trim();
        lon = strTempArr[1].trim();


        //сохраняет Latitude
        latPref = context.getSharedPreferences("Latitude", context.MODE_PRIVATE);
        SharedPreferences.Editor ed = latPref.edit();
        ed.putString(SAVED_LAT, lat);
        ed.commit();

        //сохраняет Longitude
        lonPref = context.getSharedPreferences("Longitude", context.MODE_PRIVATE);
        SharedPreferences.Editor ed1 = lonPref.edit();
        ed1.putString(SAVED_LON, lon);
        ed1.commit();

        flagLocalPref = context.getSharedPreferences(SAVED_FLAG_LOCAL, 0);
        SharedPreferences.Editor editor = flagLocalPref.edit();
        editor.putBoolean("localFlag", true);
        editor.commit();

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }
}

