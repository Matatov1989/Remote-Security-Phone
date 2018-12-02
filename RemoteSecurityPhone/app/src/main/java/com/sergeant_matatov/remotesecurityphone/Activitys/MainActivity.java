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

import static java.lang.System.exit;

public class MainActivity extends AppCompatActivity {
    final String LOG_TAG = "myLogs";
    TextView textPolicy;
    Switch switchStartProgramm;


    public static final int CODE_READ_PHONE_STATE = 1; // code you want.

    public static final int MULTIPLE_PERMISSIONS = 2; // code you want.

    String[] PERMISSIONS = new String[]{
            android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        checkPermissions();

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
    public void onClickBook(View view) {
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

    private void checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : PERMISSIONS) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            //         return false;
        }
    }
    private void checkPermissionPhoneState() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_PHONE_STATE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                Log.d(LOG_TAG, "dialog ");
                dialogPermissionPhoneState();

            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, CODE_READ_PHONE_STATE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted

            Log.d(LOG_TAG, "save  ");
            TelephonyManager telephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            String numSIM = telephonyMgr.getSimSerialNumber();

            SharedPreferences sinSIMPref = getSharedPreferences("rsp_contact", MODE_PRIVATE);
            SharedPreferences.Editor ed = sinSIMPref.edit();
            ed.putString("save_serial_sim_card", numSIM);
            ed.commit();
        }
    }

    private void dialogPermissionPhoneState() {
        android.app.AlertDialog.Builder adb = new android.app.AlertDialog.Builder(this);
        adb.setCancelable(false);
        adb.setMessage(R.string.dialogPermissionCall);
        adb.setPositiveButton(R.string.btnOK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, CODE_READ_PHONE_STATE);
                dialog.dismiss();
            }
        });
        adb.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CODE_READ_PHONE_STATE: {
                checkPermissionPhoneState();
                return;
            }

            case MULTIPLE_PERMISSIONS: {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.SEND_SMS)) {
                    showDialogOK(getString(R.string.dialogPermissionSMS), new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            switch (which)
                            {
                                case DialogInterface.BUTTON_POSITIVE:
                                    checkPermissions();
                                    break;
                            }
                        }
                    });
                }

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showDialogOK(getString(R.string.dialogPermissionLocation), new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            switch (which)
                            {
                                case DialogInterface.BUTTON_POSITIVE:
                                    checkPermissions();
                                    break;
                            }
                        }
                    });
                }
                return;
            }

        }
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(getString(R.string.btnOK), okListener)
                .create()
                .show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        exit(0);
    }
}