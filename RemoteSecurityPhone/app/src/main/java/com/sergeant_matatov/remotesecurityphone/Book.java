package com.sergeant_matatov.remotesecurityphone;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Yurka on 12.02.2016.
 */
public class Book extends Activity {
    Context context;

    SharedPreferences contactPref;    //для контакта
    final String SAVED_CONTACT = "saved_contact";

    ListView listView;
    String pers;
    String flagTab;

    SendSMS sms;

    ArrayAdapter myArrayAdapter;
    private ArrayList<String> personList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_book);

        Intent intent = getIntent();
        if (intent.hasExtra("flagTab"))
            flagTab = intent.getStringExtra("flagTab");

        listView = (ListView) findViewById(R.id.listView);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);       // устанавливаем режим выбора пунктов списка

        getContacts();      //функция загружающая контакты с телефоной книги

        myArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, android.R.id.text1, personList);

        listView.setAdapter(myArrayAdapter);
    }

    //выбранный номер отправляем в textPhone
    public void onClickOK(View v) {
        int num = listView.getCheckedItemPosition();    //вытаскиваем выбранный контакт

        if (flagTab.toString().equals("security")) {
            saveContact(personList.get(num));
            //переходим в MainActivity
            startActivity(new Intent(Book.this, MainActivity.class));
        } else if (flagTab.toString().equals("send"))
            startActivity(new Intent(Book.this, MainActivity.class).putExtra("contact", personList.get(num)));
    }

    //загружаем контакты с телефонной книги в arraylist
    public void getContacts() {
        String phoneNumber = null;
        //Связываемся с контактными данными и берем с них значения id контакта, имени контакта и его номера:
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
        pers = "";
        ContentResolver contentResolver = getContentResolver();
        //тут прописывается сортировка по алфавиту
        Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, ContactsContract.Contacts.SORT_KEY_PRIMARY + " ASC");
        //Запускаем цикл обработчик для каждого контакта:
        if (cursor.getCount() > 0) {
            //Если значение имени и номера контакта больше 0 (то есть они существуют) выбираем
            //их значения в приложение привязываем с соответствующие поля "Имя" и "Номер":
            while (cursor.moveToNext()) {
                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
                //Получаем имя:
                if (hasPhoneNumber > 0) {
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                    //и соответствующий ему номер:
                    while (phoneCursor.moveToNext()) {
                        pers = name;
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        phoneNumber = phoneNumber.replace("-", "");
                        phoneNumber = phoneNumber.replace(" ", "");
                        int len = phoneNumber.length();

                        if (len >= 7) {
                            pers += " " + phoneNumber;
                            personList.add(pers);
                            personList.add(pers);
                        }
                    }
                }
            }
        }

        //проверка на дубляж в основном из-за WhatsApp
        for (int i = 0; i < personList.size(); i++) {
            pers = personList.get(i);
            for (int j = i + 1; j < personList.size(); j++)
                if (pers.equals(personList.get(j)))
                    personList.remove(j);   //удаляем повторяющийся контакт
        }

        //удаляем повторяющийся контакт
        for (int i = 0; i < personList.size(); i++) {
            pers = personList.get(i);
            for (int j = i + 1; j < personList.size(); j++)
                if (pers.equals(personList.get(j)))
                    personList.remove(j);   //удаляем повторяющийся контакт
        }
    }

    //сохраняет контакт
    public void saveContact(String contact) {
        contactPref = getSharedPreferences("My Contact", MODE_PRIVATE);
        SharedPreferences.Editor ed = contactPref.edit();
        ed.putString(SAVED_CONTACT, contact);
        ed.commit();
        //отправляем sms об оповещении о том что этот контакт выбрали как охраника
        contact += " ";
        String[] arrTemp = contact.split(" ");

        sms = new SendSMS();
        sms.sendSMS(getBaseContext(), arrTemp[1], getString(R.string.smsFirst));
    }
}
