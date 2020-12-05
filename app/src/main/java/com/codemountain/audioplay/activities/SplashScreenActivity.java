package com.codemountain.audioplay.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.codemountain.audioplay.MainActivity;
import com.codemountain.audioplay.R;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(2000);
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                    finish();
                }
                catch (Exception e){

                }
            }
        };
        thread.start();
    }
}