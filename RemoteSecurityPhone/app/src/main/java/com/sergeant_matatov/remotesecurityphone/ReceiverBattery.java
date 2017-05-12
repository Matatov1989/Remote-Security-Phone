package com.sergeant_matatov.remotesecurityphone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.BatteryManager;

/**
 * Created by Yurka on 12.03.2016.
 */
public class ReceiverBattery extends BroadcastReceiver {
    Context context;
    String phone;

    String type;
    String flagSend;

    SharedPreferences temporaryPhone;    //для номера
    final String TEMPORARY_PHONE = "temporary_phone";

    SharedPreferences typePref;
    final String TYPE_SEND = "type_sens";

    SharedPreferences sendPref;
    final String FLAG_SEND = "flag_sens";

    SendSMS sms;

    @Override
    public void onReceive(Context context, Intent intent) {

        phone = intent.getStringExtra("phone");
        type = intent.getStringExtra("type");

        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

        //сохраняем номер SharedPreferences так как Service работает в цикле и intent теряет данные
        if (level == 0) {
            //сохраняем номер телефона
            temporaryPhone = context.getSharedPreferences("Temporary Phone", context.MODE_PRIVATE);
            SharedPreferences.Editor ed = temporaryPhone.edit();
            ed.putString(TEMPORARY_PHONE, phone);
            ed.commit();

            //сохранили тип батареи notification/command
            typePref = context.getSharedPreferences("Type send", context.MODE_PRIVATE);
            ed = typePref.edit();
            ed.putString(TYPE_SEND, type);
            ed.commit();
        } else {
            //загружаем флаг для того что бы sms о состоянии батареи отправилась 1 раз
            sendPref = context.getSharedPreferences("Flag send", context.MODE_PRIVATE);
            flagSend = sendPref.getString(FLAG_SEND, "");

            //загружаем type command/notification
            typePref = context.getSharedPreferences("Type send", context.MODE_PRIVATE);
            type = typePref.getString(TYPE_SEND, "");

            //просто отправиться состояниее батареи на полученный запрос и закроется
            if (type.equals("command")) {
                //загружаем номер телефона
                temporaryPhone = context.getSharedPreferences("Temporary Phone", context.MODE_PRIVATE);
                phone = temporaryPhone.getString(TEMPORARY_PHONE, "");

                String msg = context.getString(R.string.smsBattery) + " " + level + "%";

                //отправляем sms
                sms = new SendSMS();
                sms.sendSMS(context, phone, msg);

                context.stopService(new Intent(context, ServiceBattery.class));
            } else if (type.equals("notification") && level == 15 && flagSend.equals("not send"))   //если 15% батареи то отправится уведомление
            {
                //для того что бы sms отправилась 1 раз
                sendPref = context.getSharedPreferences("Flag send", context.MODE_PRIVATE);
                SharedPreferences.Editor ed = sendPref.edit();
                ed.putString(FLAG_SEND, "send");
                ed.commit();

                //загружаем номер телефона
                temporaryPhone = context.getSharedPreferences("Temporary Phone", context.MODE_PRIVATE);
                phone = temporaryPhone.getString(TEMPORARY_PHONE, "");

                String msg = context.getString(R.string.smsBatt15);

                //отправка sms
                sms = new SendSMS();
                sms.sendSMS(context, phone, msg);
            } else if (type.equals("notification") && level == 10)        //если 10% отключится сервис notification и отправится уведомление и gps координаты
            {
                //загружаем номер телефона
                temporaryPhone = context.getSharedPreferences("Temporary Phone", context.MODE_PRIVATE);
                phone = temporaryPhone.getString(TEMPORARY_PHONE, "");

                String msg = context.getString(R.string.smsBatt10);

                //отправка sms
                sms = new SendSMS();
                sms.sendSMS(context, phone, msg);
                //стоп ServiceBattery
                context.stopService(new Intent(context, ServiceBattery.class));
                //стоп ServiceNotif
                context.stopService(new Intent(context, ServiceNotif.class));
                //для отключения диалога
                intent = new Intent(context, DialogNotif.class);
                intent.putExtra("finish", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
    }
}
