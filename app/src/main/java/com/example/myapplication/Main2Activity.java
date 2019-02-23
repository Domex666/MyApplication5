package com.example.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class Main2Activity extends AppCompatActivity implements View.OnClickListener{
    Button b1;
    EditText e1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        e1 = (EditText) findViewById(R.id.e1);
        Button b1 = (Button) findViewById(R.id.b1);
        b1.setOnClickListener(this);

    }




    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, Main3Activity.class);
        intent.putExtra("grp", e1.getText().toString());
        startActivity(intent);

    }
}