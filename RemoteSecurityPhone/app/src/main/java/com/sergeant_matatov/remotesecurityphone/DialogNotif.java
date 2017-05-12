package com.sergeant_matatov.remotesecurityphone;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import static android.os.SystemClock.sleep;
import static java.lang.System.exit;

/**
 * Created by Yurka on 12.03.2016.
 */
public class DialogNotif extends Activity {
    String phone;
    Context context;
    Intent intent;

    Dialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //для автоматического отключения
        if (getIntent().getBooleanExtra("finish", false)) {
            finish();
            moveTaskToBack(true);
            exit(0);
        }
        sleep(10000);
        intent = getIntent();
        if (intent.hasExtra("phone"))
            phone = intent.getStringExtra("phone");

        //снять бдлкировку экрана
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        //     getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER);
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        lock.disableKeyguard();

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setCancelable(false);
        adb.setTitle(getString(R.string.titleDialogUrgently));
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setMessage(getString(R.string.dialogUrgentlyCall) + " " + phone);
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //остановили notification
                stopService(new Intent(DialogNotif.this, ServiceNotif.class));
                //остановили состояние батареи
                stopService(new Intent(DialogNotif.this, ServiceBattery.class));
                //отправили номер в звонилку
                intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                startActivity(intent);

                DialogNotif.this.finish();
                dialog.cancel();
            }
        });

        dialog = adb.show();

        //запускаем notification
        startService(new Intent(DialogNotif.this, ServiceNotif.class).putExtra("phone", phone));
        //запускаем состояние батарейки
        startService(new Intent(DialogNotif.this, ServiceBattery.class).putExtra("phone", phone).putExtra("type", "notification"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        finishAffinity();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}