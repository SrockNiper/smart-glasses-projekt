package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class Nastavovaci_stranka extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nastavovaci_stranka);
        Intent intent = getIntent();
        String jmeno  = intent.getExtras().getString("jmeno");
        Toast.makeText(this,jmeno,Toast.LENGTH_LONG).show();
    }
}