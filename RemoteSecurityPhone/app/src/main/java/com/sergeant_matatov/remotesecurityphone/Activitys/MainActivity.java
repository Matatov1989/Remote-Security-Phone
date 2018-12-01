package com.sergeant_matatov.remotesecurityphone.Activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.sergeant_matatov.remotesecurityphone.Manifest;
import com.sergeant_matatov.remotesecurityphone.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final String LOG_TAG = "myLogs";
    TextView textPolicy;
    Switch switchStartProgramm;


    public static final int MULTIPLE_PERMISSIONS = 2; // code you want.

    String[] permissions = new String[]{
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.CALL_PHONE,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        switchStartProgramm = (Switch) findViewById(R.id.switchStartProgramm);
        switchStartProgramm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (switchStartProgramm.isChecked()) {
                    Log.d(LOG_TAG, "switchStartProgramm " + isChecked);
                    dialogStartProgramm();
                } else {
                    Log.d(LOG_TAG, "switchStartProgramm " + isChecked);
                }
            }
        });

        textPolicy = (TextView) findViewById(R.id.textPolicy);
        textPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textPolicy.setTextColor(getResources().getColor(R.color.colorRed));
                startActivity(new Intent(MainActivity.this, PrivacyPolicy.class));
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });
    }

    //open a book with contacts
    private void onClickBook(View view) {
        startActivity(new Intent(this, BookActivity.class));
    }

    //dialogfor start programm
    private void dialogStartProgramm() {

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setCancelable(true);
        adb.setTitle(getString(R.string.dialogTitleInst));
        adb.setIcon(R.drawable.ic_instr);
        adb.setPositiveButton(R.string.btnOK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //get serial number of sim card

                checkPermissionPhoneState();

                dialog.dismiss();
            }
        });
        adb.setNegativeButton(R.string.btnCancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        adb.show();

    }

    private void checkPermissionPhoneState(){

        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.READ_PHONE_STATE)) {
                Log.d(LOG_TAG, "dialog ");
                dialogPermissionPhoneState();
            } else {
                Log.d(LOG_TAG, "request permission ");
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, 1);
            }
        }
        else {
            Log.d(LOG_TAG, "save  ");
            TelephonyManager telephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            String numSIM = telephonyMgr.getSimSerialNumber();

            SharedPreferences sinSIMPref = getSharedPreferences("rsp_contact", MODE_PRIVATE);
            SharedPreferences.Editor ed = sinSIMPref.edit();
            ed.putString("save_serial_sim_card", numSIM);
            ed.commit();
        }
    }



    private void dialogPermissionPhoneState(){
        android.app.AlertDialog.Builder adb = new android.app.AlertDialog.Builder(this);
        adb.setCancelable(false);
        adb.setMessage(R.string.dialogPermissionCall);
        adb.setPositiveButton(R.string.btnOK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, 1);
                dialog.dismiss();
            }
        });
        adb.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        Log.d(LOG_TAG, "onRequestPermissionsResult  ");

        switch (requestCode) {
            case 1: {
                checkPermissionPhoneState();
     /*           if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          //          getCalendarsToList();
                } else {
                    checkPermissionPhoneState();
                }*/
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
/*
        if (loadCheckLocal()) {
            localNew = menu.findItem(R.id.action_local);
            localNew.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            saveCheckLocal();
        }*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_local) {
            //          startActivity(new Intent(MainActivity11.this, MapsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}