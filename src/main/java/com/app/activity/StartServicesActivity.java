package com.app.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.app.geenie.R;
import com.app.services.MQTTservice;

public class StartServicesActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent startMQTTIntent = new Intent(StartServicesActivity.this, MQTTservice.class);
        startService(startMQTTIntent);

    }

    @Override
    protected  void onStart(){
        finish();
    }
}
