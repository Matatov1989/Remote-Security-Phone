package com.sergeant_matatov.remotesecurityphone.Activitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.sergeant_matatov.remotesecurityphone.Adapters.ContactRecyclerAdapter;
import com.sergeant_matatov.remotesecurityphone.R;

/**
 * Created by Yurka on 12.02.2016.
 */
public class BookActivity extends AppCompatActivity {

    ContactRecyclerAdapter contactRecyclerAdapter;
    RecyclerView recyclerContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_book);

        recyclerContact = (RecyclerView) findViewById(R.id.recyclerContact);
        recyclerContact.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerContact.setLayoutManager(new LinearLayoutManager(this));
        recyclerContact.setClickable(true);

        contactRecyclerAdapter = new ContactRecyclerAdapter(this);
        recyclerContact.setAdapter(contactRecyclerAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(BookActivity.this, MainActivity.class));
    }
}