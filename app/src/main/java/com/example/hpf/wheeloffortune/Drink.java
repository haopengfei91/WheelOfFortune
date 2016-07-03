package com.example.hpf.wheeloffortune;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class Drink extends AppCompatActivity {

    private LuckyPan luckyPan;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.id_choose);

        Intent intent = getIntent();
        List<String> nameList = new ArrayList<String>();
        nameList = (List<String>) intent.getSerializableExtra("names");
        luckyPan = new LuckyPan(Drink.this, null, nameList);
        imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.run);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        luckyPan.setLayoutParams(lp);
        luckyPan.setPadding(30, 30, 30, 30);
        imageView.setLayoutParams(lp);
        relativeLayout.addView(luckyPan);
        relativeLayout.addView(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!luckyPan.isStart()) {
                    luckyPan.luckyStart();;
                }
            }
        });
    }
}
