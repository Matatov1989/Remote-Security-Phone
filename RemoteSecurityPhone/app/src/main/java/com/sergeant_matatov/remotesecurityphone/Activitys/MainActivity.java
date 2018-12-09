package com.sergeant_matatov.remotesecurityphone.Activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
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

import com.sergeant_matatov.remotesecurityphone.R;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.exit;

public class MainActivity extends AppCompatActivity {
    //   final String LOG_TAG = "myLogs";
    TextView textPolicy;
    TextView textChooseContact;
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

        //     Log.d(LOG_TAG, "onCreate");

        checkPermissions();

        SharedPreferences sharedPrefs = getSharedPreferences("rsp_contact", MODE_PRIVATE);

        textChooseContact = (TextView) findViewById(R.id.textChooseContact);

        String strCooseName = getString(R.string.textChooseContact, sharedPrefs.getString("contact_name", getString(R.string.textNotChooseContact)));

        textChooseContact.setText(strCooseName);

        switchStartProgramm = (Switch) findViewById(R.id.switchStartProgramm);
        switchStartProgramm.setChecked(sharedPrefs.getBoolean("switch_button_job", false));
        switchStartProgramm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (switchStartProgramm.isChecked()) {
                    SharedPreferences sharedPrefs = getSharedPreferences("rsp_contact", MODE_PRIVATE);
                    if (sharedPrefs.getString("contact_name", "").isEmpty()) {
                        dialogChooseContact();
                        switchStartProgramm.setChecked(false);
                    }
                    else {
                        SharedPreferences.Editor editor = getSharedPreferences("rsp_contact", MODE_PRIVATE).edit();
                        editor.putBoolean("switch_button_job", true);
                        editor.commit();
                        checkPermissionPhoneState();
                    }
                } else {
                    SharedPreferences.Editor editor = getSharedPreferences("rsp_contact", MODE_PRIVATE).edit();
                    editor.putBoolean("switch_button_job", false);
                    editor.commit();
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
    private void dialogChooseContact() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setCancelable(true);
        adb.setTitle(getString(R.string.actionBtnInstructions));
        adb.setMessage(getString(R.string.textNeedChooseContact));
        adb.setIcon(R.drawable.ic_error);
        adb.setPositiveButton(R.string.btnOK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        adb.show();
    }

    //multy check permissioms SEND_SMS and LOCATION_DEVICE
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
                dialogPermissionPhoneState();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, CODE_READ_PHONE_STATE);
            }
        } else {
            //get and save a sim serial number
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
        adb.setMessage(R.string.dialogPermissionPhoneState);
        adb.setPositiveButton(R.string.btnOK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, CODE_READ_PHONE_STATE);
                dialog.dismiss();
            }
        });
        adb.show();
    }

    //dialog with list workers (I am and my brathers)
    //user can to write message on worker
    private void dialogDevelopers() {
        final String[] developers = getResources().getStringArray(R.array.arrWorkers);

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(getString(R.string.actionBtnDevelopers));
        adb.setItems(developers, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                switch (item) {
                    case 0:
                        emailIntent.setType("plain/text");
                        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"Matatov1989@gmail.com"});
                        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Remote Secyrity Phone");
                        startActivity(Intent.createChooser(emailIntent, getString(R.string.toastSendMail)));
                        dialog.dismiss();
                        break;
                    case 1:
                        emailIntent.setType("plain/text");
                        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"Docmat63@gmail.com"});
                        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Remote Secyrity Phone");
                        startActivity(Intent.createChooser(emailIntent, getString(R.string.toastSendMail)));
                        dialog.dismiss();
                        break;
                }
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
                    showDialogOK(getString(R.string.dialogPermissionSMS), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    checkPermissions();
                                    break;
                            }
                        }
                    });
                }

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showDialogOK(getString(R.string.dialogPermissionLocation), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_developers:        //dialog developers
                dialogDevelopers();
                break;

            case R.id.action_from_developer:    //from developers
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/developer?id=Yury%20Matatov&hl"));
                startActivity(intent);
                break;

            case R.id.action_advise_friend:     //advise a program to friend
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.sergeant_matatov.remotesecurityphone&hl");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;

            case R.id.action_feedback:      //feedback a programm
                Intent intentFeedback = new Intent(Intent.ACTION_VIEW);
                intentFeedback.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.sergeant_matatov.remotesecurityphone&hl"));
                startActivity(intentFeedback);
                break;

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