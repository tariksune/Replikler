package com.tarxsoft.replikler;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class CopyrightActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copyright);
        getSupportActionBar().hide();
    }
}