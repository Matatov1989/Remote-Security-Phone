package com.sergeant_matatov.remotesecurityphone.Adapters;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sergeant_matatov.remotesecurityphone.Activitys.MainActivity;
import com.sergeant_matatov.remotesecurityphone.Activitys.PrivacyPolicy;
import com.sergeant_matatov.remotesecurityphone.Database.ContactData;
import com.sergeant_matatov.remotesecurityphone.R;
import com.sergeant_matatov.remotesecurityphone.Services.ServiceSendMessage;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class ContactRecyclerAdapter extends RecyclerView.Adapter<ContactRecyclerAdapter.ContactHolder> {

    Context context;
    ArrayList<ContactData> arrayListContact;

    public ContactRecyclerAdapter(Context context) {
        this.context = context;
        this.arrayListContact = getContacts();
    }

    @Override
    public ContactRecyclerAdapter.ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater myInflator = LayoutInflater.from(context);
        View view = myInflator.inflate(R.layout.element_list_contact, parent, false);
        return new ContactHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactRecyclerAdapter.ContactHolder holder, int position) {
        holder.textNameContact.setText(arrayListContact.get(position).getNameContact());
        holder.textPhoneContact.setText(arrayListContact.get(position).getPhoneContact());
    }

    @Override
    public int getItemCount() {
        return arrayListContact.size();
    }

    public class ContactHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textNameContact;
        TextView textPhoneContact;

        public ContactHolder(View view) {
            super(view);
            textNameContact = (TextView) view.findViewById(R.id.textNameContact);
            textPhoneContact = (TextView) view.findViewById(R.id.textPhoneContact);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            saveContact(arrayListContact.get(getAdapterPosition()).getNameContact(), arrayListContact.get(getAdapterPosition()).getPhoneContact());
        }

        //save a selected contact and send to selected contact message about programm and operation
        public void saveContact(String contactName, String contactPhone) {

            final String CALLER_NAME = "contact_name";
            final String CALLER_NUMBER = "contact_number";
            SharedPreferences personPref = context.getSharedPreferences("rsp_contact", context.MODE_PRIVATE);
            SharedPreferences.Editor edit = personPref.edit();
            edit.putString(CALLER_NAME, contactName);
            edit.putString(CALLER_NUMBER, contactPhone);
            edit.commit();

            Intent intent = new Intent(context, ServiceSendMessage.class).putExtra("flagSecuritySMS", false);
            context.startService(intent);

            context.startActivity(new Intent(context, MainActivity.class));
        }
    }

    //get contacts from a phone book
    public ArrayList<ContactData> getContacts() {
        ArrayList<ContactData> contactListData = new ArrayList<ContactData>();
        //connect with contacts data, get id, name and number
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        ContentResolver contentResolver = context.getContentResolver();
        //sorting
        Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, ContactsContract.Contacts.SORT_KEY_PRIMARY + " ASC");
        //start searchig for every contact
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                String phoneNumber = "";
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
                //get name
                if (hasPhoneNumber > 0) {
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                    //get his numbers
                    while (phoneCursor.moveToNext()) {
                        //           pers = name;
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        phoneNumber = phoneNumber.replace("-", "");
                        phoneNumber = phoneNumber.replace(" ", "");
                        int len = phoneNumber.length();

                        if (len >= 7)
                            contactListData.add(new ContactData(name, phoneNumber));
                    }
                }
            }
        }

        //dubbing check in due WhatsApp
        for (int i = 0; i < contactListData.size(); i++) {
            String contactName = contactListData.get(i).getNameContact();
            String contactPhone = contactListData.get(i).getPhoneContact();
            for (int j = i + 1; j < contactListData.size(); j++)
                if (contactName.equals(contactListData.get(j).getNameContact()) && contactPhone.equals(contactListData.get(j).getPhoneContact()))
                    contactListData.remove(j);     //remove
        }

        for (int i = 0; i < contactListData.size(); i++) {
            String contactName = contactListData.get(i).getNameContact();
            String contactPhone = contactListData.get(i).getPhoneContact();
            for (int j = i + 1; j < contactListData.size(); j++)
                if (contactName.equals(contactListData.get(j).getNameContact()) && contactPhone.equals(contactListData.get(j).getPhoneContact()))
                    contactListData.remove(j);     //remove
        }
        return contactListData;
    }
}