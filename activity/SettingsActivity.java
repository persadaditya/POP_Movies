package com.app.phedev.popmovie.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.app.phedev.popmovie.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingFragment()).commit();

        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        getSupportActionBar().setTitle("Setting");


    }

   // public static class SettingFragment extends PreferenceFragment{
   //     @Override
   //     public void onCreate(final Bundle savedInstanceState){
   //         super.onCreate(savedInstanceState);
   //         addPreferencesFromResource(R.xml.preference);
   //     }
   // }

    }


