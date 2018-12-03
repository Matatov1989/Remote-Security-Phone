package com.sergeant_matatov.remotesecurityphone.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.gsm.SmsMessage;

import com.sergeant_matatov.remotesecurityphone.R;


/**
 * Created by Yurka on 12.03.2016.
 */
public class ServiceInterceptSMS extends BroadcastReceiver {
    String[] strCom;    //для команды
    Context context;

    SharedPreferences passPref;    //для пароля
    final String SAVED_PASS = "saved_pass";

    SharedPreferences questionPref;    //для вопроса
    final String SAVED_QUESTION = "saved_question";

    SharedPreferences answerPref;    //для ответа
    final String SAVED_ANSWER = "saved_answer";

    @Override
    public void onReceive(Context context, Intent intent) {


        //вытаскиваем пароль
        passPref = context.getSharedPreferences("My Password", context.MODE_PRIVATE);
        String myPass = passPref.getString(SAVED_PASS, "");

        if (!myPass.toString().isEmpty()) {
            //перехватываем входящее SMS сообщение
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            String str = "";
            if (bundle != null) {
                //извлечь полученное SMS
                Object[] pdus = (Object[]) bundle.get("pdus");
                msgs = new SmsMessage[pdus.length];
                for (int i = 0; i < msgs.length; i++) {
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    str += msgs[i].getOriginatingAddress();
                    str += "-";
                    str += msgs[i].getMessageBody().toString();
                }
                if (str.charAt(str.length() - 1) != '-') {
                    str += '-';
                }
                //разбиваем на массивы: номер, пароль и команду (номер, команду и координаты)
                strCom = str.split("-");
                //вытаскиваем ответ контрольного вопроса
                answerPref = context.getSharedPreferences("My Answer", context.MODE_PRIVATE);
                String myAnswer = answerPref.getString(SAVED_ANSWER, "");
                //для того что бы если установили программу и не поставили пароль
                //или если решил обновить и не обновил
                if (myPass.equals(""))
                    myPass = "not password";

                if (strCom.length == 2) {
                    //условие для получения контрольного вопроса
                    if ("My Question".equals(strCom[1])) {
                        abortBroadcast();             //не сохраняет sms во входящих
                        //вытаскиваем вопрос
                        questionPref = context.getSharedPreferences("My Question", context.MODE_PRIVATE);
                        String question = questionPref.getString(SAVED_QUESTION, "");
                        question = question.substring(0, question.length() - 1);
                        String msg = context.getString(R.string.smsQuestion) + " №" + question + " " + context.getString(R.string.smsQuestionInst);

                    }
                    //условие для получения пароля на ответ контрольного вопроса
                    else if (myAnswer.equals(strCom[1])) {
                        abortBroadcast();             //не сохраняет sms во входящих
                        String msg = context.getString(R.string.smsAnswer) + " " + myPass;
                        //          sendSMS(strCom[0], msg, context);

                    }
                } else if (strCom.length == 3) {
                    //условие для включения звука
                    if (myPass.equals(strCom[1]) && "onSound".equals(strCom[2])) {
                        abortBroadcast();           //не сохраняет sms во входящих

                    }
                    //условие для получения координат
                    else if (myPass.equals(strCom[1]) && "onGPS".equals(strCom[2])) {
                        abortBroadcast();             //не сохраняет sms во входящих

                    }
                    //условие для включения notification
                    else if (myPass.equals(strCom[1]) && "onNotif".equals(strCom[2])) {
                        abortBroadcast();           //не сохраняет sms во входящих
                        //вывод диалога
                 /*       intent = new Intent("android.intent.action.MAIN");
                        intent.setClass(context, DialogNotif.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("phone", strCom[0]);
                        intent.putExtra("finish", false);
                        context.startActivity(intent);*/
                    }

                  /*  //условие для выключения notification
                    else if (myPass.equals(strCom[1]) && "offNotif".equals(strCom[2])) {
                        abortBroadcast();           //не сохраняет sms во входящих
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
*/
                    //условие для получения состояния батареи
                  /*  else if (myPass.equals(strCom[1]) && "onBattery".equals(strCom[2])) {
                        abortBroadcast();             //не сохраняет sms во входящих
                        context.startService(new Intent(context, ServiceBattery.class).putExtra("phone", strCom[0]).putExtra("type", "command"));
                    }*/
                }
                else if (strCom.length == 5 && "Coordinates".equals(strCom[1]))   //условие для обработки координат
                {
                   /* TaskCoord tc = new TaskCoord();
                    tc.context = context;
                    tc.coordinates = strCom[2];
                    tc.execute();*/
                } else if (strCom.length == 6 && "Coordinates".equals(strCom[1])) {
                 /*   TaskCoord tc = new TaskCoord();
                    tc.context = context;
                    tc.coordinates = strCom[3];
                    tc.execute();*/
                }
            }
        }
    }
}