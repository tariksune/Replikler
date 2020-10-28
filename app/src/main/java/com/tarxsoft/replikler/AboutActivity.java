package com.tarxsoft.replikler;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().hide();
    }

    public void tarxsoftLink(View view) {
        Intent openLink = new Intent(Intent.ACTION_VIEW);
        openLink.setData(Uri.parse("https://play.google.com/store/apps/dev?id=8951161100903318890"));
        startActivity(openLink);
    }

    public void backImageButton(View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}