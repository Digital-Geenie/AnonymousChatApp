package com.app.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.content.Intent;
import com.app.constants.QuickstartPreferences;
import com.app.geenie.R;
import com.app.services.MQTTservice;
import com.parse.Parse;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        Thread thread = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                    Intent intent;
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    /*intent = new Intent(SplashActivity.this, StartServicesActivity.class);
                    startActivity(intent);*/

                    if(!sharedPreferences.getBoolean(QuickstartPreferences.DEVICE_REGISTERED,false)) {
                        sharedPreferences.edit().putBoolean("travel", false).apply();
                        sharedPreferences.edit().putBoolean("shopping", false).apply();
                        sharedPreferences.edit().putBoolean("food", false).apply();

                        intent = new Intent(SplashActivity.this, SignUpActivity.class);
                        startActivity(intent);
                    }
                    else{
                        intent = new Intent(SplashActivity.this, UserActivity.class);
                        startActivity(intent);
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        thread.start();
       /* new LoadingAsyncTask().execute();*/
    }

    /*private class LoadingAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            startService(new Intent(SplashActivity.this, MQTTservice.class));
            return null;
        }
    }*/

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }
}
