package com.example.hpf.wheeloffortune;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Create extends AppCompatActivity implements PickerView.OnSelectListener{

    private PickerView mPick;
    private int number;
    private LinearLayout mName;
    private EditText[] names;
    private List<String> nameList;
    private int layoutHeight;
    private int editHeight;
    private Button btnStart;
    private ImageView imgBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        mPick = (PickerView) findViewById(R.id.id_pick);
        mPick.setOnSelectListener(this);
        mName = (LinearLayout) findViewById(R.id.id_name);
        nameList = new ArrayList<String>();
        btnStart = (Button) findViewById(R.id.id_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameList = getName();
                Intent intent = new Intent(Create.this, Drink.class);
                intent.putExtra("names", (Serializable)nameList);
                startActivity(intent);
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

    @Override
    protected void onResume() {
        super.onResume();
        layoutHeight = mName.getLayoutParams().height;
        Log.i("layoutHeight", layoutHeight+"");
    }

    @Override
    public void onSelect(String text) {

        mName.removeAllViews();

        number = Integer.parseInt(text);
        names = new EditText[number];
        Log.i("number", number+"");
        editHeight = layoutHeight/number;
        Log.i("height", editHeight+"");
        for (int i=0; i<number; i++) {
            names[i] = new EditText(this);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
            names[i].setLayoutParams(lp);
            mName.addView(names[i]);
        }

    }

    private List<String> getName(){

        for (int i=0; i<names.length; i++) {
            String name = names[i].getText().toString();
            nameList.add(name);
        }
        return nameList;
    }


}
