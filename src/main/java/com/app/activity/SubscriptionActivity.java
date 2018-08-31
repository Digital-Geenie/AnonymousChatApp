package com.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.app.AlertDialog;
import com.app.constants.GeenieConstants;
import com.app.geenie.R;
import com.app.helper.MultiSelectionSpinner;
import com.app.services.MQTTservice;

import java.util.Arrays;

public class SubscriptionActivity extends Activity {

    Button subscribe;
    MultiSelectionSpinner topic;
    String topic1;
    private String[] choices = {"Travel", "Shopping", "Food"};
    private Messenger svc = null;
    Location location;
    private static final long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 1;
    private static final long MINIMUM_TIME_BETWEEN_UPDATE = 1000;
    private static final String POINT_LATITUDE_KEY = "POINT_LATITUDE_KEY";
    private static final String POINT_LONGITUDE_KEY = "POINT_LONGITUDE_KEY";
    String[] subscribedCategories;
    private  ProgressDialog pd;
    private LocationManager locationManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        Bundle svcBundle = getIntent().getExtras();
        svc = new Messenger(svcBundle.getBinder("binder"));

        subscribe = (Button) findViewById(R.id.subscribeButton);
        topic = (MultiSelectionSpinner)findViewById(R.id.subscribedCategories);
        topic.setItems(choices);

        subscribe.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    topic1 = topic.getSelectedItem().toString();
                    String delimiter = ", ";
                    subscribedCategories = topic1.split(delimiter);
                    Toast.makeText(SubscriptionActivity.this, "You have subscribed to " + Arrays.toString(subscribedCategories), Toast.LENGTH_LONG).show();
                    try {
                        pd = new ProgressDialog(SubscriptionActivity.this);
                        pd.setTitle("Processing...");
                        pd.setMessage("Please wait.");
                        pd.setCancelable(false);
                        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pd.show();

                        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                        if(isLocationEnabled()) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MINIMUM_TIME_BETWEEN_UPDATE,
                                    MINIMUM_DISTANCECHANGE_FOR_UPDATE,
                                    new MyLocationListener()
                            );
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location == null) {
                                return;
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    for (String subscribedCategory : subscribedCategories) {
                        addSubscribeButtonListener(subscribedCategory);
                    }
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    for (String subscribedCategory : subscribedCategories) {
                        sharedPreferences.edit().putBoolean(subscribedCategory, true).apply();
                    }

                    Intent intent = new Intent(SubscriptionActivity.this, UserActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(SubscriptionActivity.this, GeenieConstants.INTERNET_UNAVAILABLE, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(SubscriptionActivity.this, UserActivity.class);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }
    private void addSubscribeButtonListener(String topic1)
    {
        if (topic1 != null && !topic1.isEmpty()) {

            Bundle data = new Bundle();
            data.putCharSequence(MQTTservice.TOPIC, topic1);
            data.putDouble("subscriberLatitude", location.getLatitude());
            data.putDouble("subscriberLongitude", location.getLongitude());
            Message msg = Message.obtain(null, MQTTservice.SUBSCRIBE);
            msg.setData(data);
            try {
                svc.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();

            }
        }
    }
    public class MyLocationListener implements LocationListener {
        public void onLocationChanged(Location location) {
            Location pointLocation = retrievelocationFromPreferences();
        }

        public void onStatusChanged(String s, int i, Bundle b) {
        }

        public void onProviderDisabled(String s) {
        }

        public void onProviderEnabled(String s) {
        }
    }

    private Location retrievelocationFromPreferences() {
        SharedPreferences prefs = this.getSharedPreferences(getClass().getSimpleName(), Context.MODE_PRIVATE);
        Location location = new Location("POINT_LOCATION");
        location.setLatitude(prefs.getFloat(POINT_LATITUDE_KEY, 0));
        location.setLongitude(prefs.getFloat(POINT_LONGITUDE_KEY, 0));
        return location;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return (wifi.isConnected() || mobile.isConnected());
    }

    public boolean isLocationEnabled(){
        if( !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SubscriptionActivity.this);
            builder.setTitle("Location is not enabled");  // GPS not found
            builder.setMessage("Do you want to enable location ?"); // Want to enable?
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
            builder.setNegativeButton("No", null);
            builder.create().show();
            return false;
        }
        else{
            return true;
        }
    }
}
