package com.example.controldetiempo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class LauncherActivity extends AppCompatActivity {

    //SharedPreference
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Id = "idKey";

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        if(sharedpreferences.getString(Id,"")==""){
            startActivity(new Intent(LauncherActivity.this, LoginActivity.class));

        }else {

            startActivity(new Intent(LauncherActivity.this, MainActivity.class));

        }


        finish();
    }
}
