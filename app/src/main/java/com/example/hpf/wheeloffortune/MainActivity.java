package com.example.hpf.wheeloffortune;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnCreate;
    private Button btnImport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCreate = (Button) findViewById(R.id.id_create);
        btnImport = (Button) findViewById(R.id.id_import);
        btnCreate.setOnClickListener(this);
        btnImport.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.id_create:
                Intent intentCreate = new Intent(this, Create.class);
                startActivity(intentCreate);
                break;
            case R.id.id_import:
                Intent intentImport = new Intent(this, Import.class);
                startActivity(intentImport);
        }
    }

}
