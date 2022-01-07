package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;



public class SplashScreen extends AppCompatActivity {


    private Thread mThread;
    private boolean isFinish = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);
        mThread = new Thread(mRunnable);
        mThread.start();


    }

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            SystemClock.sleep(2000);
            mHandler.sendEmptyMessage(0);
        }
    };

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0 && (!isFinish)) {
                stop();
            }
            super.handleMessage(msg);
        }

    };

    @Override
    protected void onDestroy() {
        try {
            mThread.interrupt();
            mThread = null;
        } catch (Exception e) {
        }
        super.onDestroy();
    }

    private void stop() {
        isFinish = true;

        Intent intent = new Intent(SplashScreen.this, WeatherActivity.class);
        startActivity(intent);


        finish();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isFinish = true;
        try {
            mThread.interrupt();
            mThread = null;
        } catch (Exception e) {
        }
        finish();
    }

    @Override
    public void finish() {
        super.finish();

    }
}