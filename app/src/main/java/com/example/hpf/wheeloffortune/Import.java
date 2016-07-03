package com.example.hpf.wheeloffortune;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Import extends AppCompatActivity {

    private List<String> phoneNameList;
    private List<String> nameList;
    private ContentResolver resolver;
    private Cursor cursor;
    private ListView listView;
    private Button btnStart;
    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);
        nameList = new ArrayList<String>();
        phoneNameList = new ArrayList<String>();
        listView = (ListView) findViewById(R.id.id_listview);
        getNameList();
        showNameList();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckedTextView ctv = (CheckedTextView) view.findViewById(android.R.id.text1);
                String name = phoneNameList.get(i);
                if (ctv.isChecked()) {
                    nameList.add(name);
                } else {
                    nameList.remove(name);
                }
                Log.i("nameList", nameList+"");
            }
        });
        btnStart = (Button) findViewById(R.id.id_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nameList != null) {
                    Intent intent = new Intent(Import.this, Drink.class);
                    intent.putExtra("names", (Serializable)nameList);
                    startActivity(intent);
                }
            }
        });
        imgBack = (ImageView) findViewById(R.id.id_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    private void showNameList() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, getNameList());
        listView.setAdapter(adapter);
        listView.setChoiceMode(listView.CHOICE_MODE_MULTIPLE);
        listView.setFocusable(false);
    }

    public List<String> getNameList() {

        resolver = getContentResolver();
        cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);
        if (cursor != null) {

            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                phoneNameList.add(name);
            }

        }
        return phoneNameList;
    }

}
