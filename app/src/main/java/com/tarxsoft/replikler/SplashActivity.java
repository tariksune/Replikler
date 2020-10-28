package com.tarxsoft.replikler;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2000;
    SharedPreferences onboardingScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Remove title bar
        getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                onboardingScreen = getSharedPreferences("onboardingScreen",MODE_PRIVATE);
                boolean isNewUser = onboardingScreen.getBoolean("newUser", true);
                if (isNewUser){
                    SharedPreferences.Editor editor = onboardingScreen.edit();
                    editor.putBoolean("newUser",false);
                    editor.commit();
                    Intent loginIntent = new Intent(SplashActivity.this,OnboardingActivity.class);
                    startActivity(loginIntent);
                    finish();
                }else{
                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }

            }
        },SPLASH_TIME_OUT);
    }
}