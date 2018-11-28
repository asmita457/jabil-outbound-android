package com.example.inceptive.imageadapter.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.crashlytics.android.Crashlytics;
import com.example.inceptive.imageadapter.R;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT=4000;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(MainActivity.this,ScanSrNumber.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_TIME_OUT);
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
    }
    @Override
    public void onDestroy()
    {
        editor.remove("Box_No");
        editor.remove("Po_No");
        editor.remove("Quantity");
        editor.remove("Part_No");
        editor.clear();
        editor.commit();
        editor.apply();
        this.finish();
        super.onDestroy();
    }
}
