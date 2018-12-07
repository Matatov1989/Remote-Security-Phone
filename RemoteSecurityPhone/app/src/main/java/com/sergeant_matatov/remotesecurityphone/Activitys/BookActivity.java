package com.sergeant_matatov.remotesecurityphone.Activitys;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.sergeant_matatov.remotesecurityphone.Adapters.ContactRecyclerAdapter;
import com.sergeant_matatov.remotesecurityphone.R;

/**
 * Created by Yurka on 12.02.2016.
 */
public class BookActivity extends AppCompatActivity {

    ContactRecyclerAdapter contactRecyclerAdapter;
    RecyclerView recyclerContact;

    public static final int CODE_READ_BOOK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_book);

        recyclerContact = (RecyclerView) findViewById(R.id.recyclerContact);
        recyclerContact.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerContact.setLayoutManager(new LinearLayoutManager(this));
        recyclerContact.setClickable(true);

        checkPermissionReadBook();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(BookActivity.this, MainActivity.class));
    }

    //set list contacts from a phone book
    private void setListBook() {
        contactRecyclerAdapter = new ContactRecyclerAdapter(this);
        recyclerContact.setAdapter(contactRecyclerAdapter);
    }

    //check permission on read a phone book
    private void checkPermissionReadBook() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
                dialogPermissionReadBook();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS}, CODE_READ_BOOK);
            }
        } else {
            setListBook();
        }
    }

    //dialog if user do not set permission on read a phone book
    private void dialogPermissionReadBook() {
        android.app.AlertDialog.Builder adb = new android.app.AlertDialog.Builder(this);
        adb.setCancelable(false);
        adb.setMessage(R.string.dialogPermissionContact);
        adb.setPositiveButton(R.string.btnOK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(BookActivity.this, new String[]{android.Manifest.permission.READ_CONTACTS}, CODE_READ_BOOK);
                dialog.dismiss();
            }
        });
        adb.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CODE_READ_BOOK: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setListBook();
                } else {
                    checkPermissionReadBook();
                }
                return;
            }
        }
    }
}