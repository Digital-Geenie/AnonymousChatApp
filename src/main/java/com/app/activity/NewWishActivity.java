package com.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.constants.GeenieConstants;
import com.app.geenie.R;
import com.app.services.MQTTservice;
import com.parse.Parse;
import com.parse.ParseObject;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import  static  com.app.constants.GeenieConstants.PARSE_USERNAME;
import  static  com.app.constants.GeenieConstants.PARSE_PASSWORD;

public class NewWishActivity extends Activity {

    private Messenger svc = null;
    Spinner newCategories;
    EditText shortDescription, longDescription;
    TextView tv1, tv2;
    Button newCategory;
    String newCat, shortDes, longDes, userName;
    private String[] choices = {"Travel", "Shopping", "Food"};
    private Integer[] choices_icons = {R.drawable.travel, R.drawable.shopping, R.drawable.food};
    private String [] choices_desc = {" ", "Let's carpool", "Let's share the apparels", "Let's have Cuisines"};
    private static final NumberFormat nf = new DecimalFormat("##.########");
    private LocationManager locationManager;
    Location location;
    private static final long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 1;
    private static final long MINIMUM_TIME_BETWEEN_UPDATE = 1000;
    private static final String POINT_LATITUDE_KEY = "POINT_LATITUDE_KEY";
    private static final String POINT_LONGITUDE_KEY = "POINT_LONGITUDE_KEY";
    private ProgressDialog pd = null;

    SharedPreferences sharedPreferences = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_wish);

        Parse.initialize(this, PARSE_USERNAME, PARSE_PASSWORD);

        Bundle svcBundle = getIntent().getExtras();
        svc = new Messenger(svcBundle.getBinder("binder"));

        newCategories = (Spinner)findViewById(R.id.newcategories);
        newCategory = (Button) findViewById(R.id.newCategory);
        shortDescription = (EditText)findViewById(R.id.shortDescription);
        longDescription = (EditText)findViewById(R.id.detailedDescription);
        tv1 = (TextView)findViewById(R.id.textView1);
        tv2 = (TextView)findViewById(R.id.textView2);

        newCategories.setAdapter(new MyAdapter(this, R.layout.custom_spinner, choices));

        shortDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                tv1.setText("Characters Remaining:" + (50-s.toString().length()) + "/50");
            }
        });

        longDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                tv2.setText("Characters Remaining:" + (150-s.toString().length()) + "/150");
            }
        });

        newCategory.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkAvailable()) {
                    String latitude = "";
                    String longitude = "";
                    String userID = UUID.randomUUID().toString();

                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");

                    String timestamp = sdf.format(new Date());
                    try {
                        pd = new ProgressDialog(NewWishActivity.this);
                        pd.setTitle("Processing...");
                        pd.setMessage("Please wait.");
                        pd.setCancelable(false);
                        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pd.show();

                        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                        if(isNetworkAvailable()) {
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
                            if (location != null) {

                                latitude = nf.format(location.getLatitude());
                                longitude = nf.format(location.getLongitude());
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    userName = sharedPreferences.getString("userName", "default");

                    newCat = newCategories.getSelectedItem().toString();
                    shortDes = shortDescription.getText().toString();
                    longDes = longDescription.getText().toString();
                    ParseObject genieUser = new ParseObject("GenieUser");
                    genieUser.put("USER_NAME", userName);
                    genieUser.put("USER_ID", userID);
                    genieUser.put("TOPIC", newCat);
                    genieUser.put("SHORT_DESCRIPTION", shortDes);
                    genieUser.put("LONG_DESCRIPTION", longDes);
                    genieUser.put("CREATE_DTTM", timestamp);
                    genieUser.put("LOCATION_LATITUDE", latitude);
                    genieUser.put("LOCATION_LONGITUDE", longitude);
                    genieUser.saveInBackground();

                    addPublishButtonListener();

                    Toast.makeText(NewWishActivity.this, "You have published " + newCat,
                            Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(NewWishActivity.this, UserActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(NewWishActivity.this, GeenieConstants.INTERNET_UNAVAILABLE, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }

    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(NewWishActivity.this, UserActivity.class);
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

    public class MyLocationListener implements LocationListener {
        public void onLocationChanged(Location location) {
            Location pointLocation = retrievelocationFromPreferences();
            float distance = location.distanceTo(pointLocation);
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
    private void addPublishButtonListener()
    {
       // sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (newCat != null && !newCat.isEmpty() && shortDes != null && !shortDes.isEmpty()) {
         //String userName = sharedPreferences.getString("userName","default");

            Bundle data = new Bundle();
            data.putCharSequence(MQTTservice.TOPIC, newCat);
            data.putCharSequence(MQTTservice.SHORTDESC, shortDes);
            data.putCharSequence(MQTTservice.LONGDESC, longDes);
            data.putDouble("publisherLatitude", location.getLatitude());
            data.putDouble("publisherLongitude",location.getLongitude());
            data.putCharSequence(MQTTservice.USERNAME, userName);
            Message msg = Message.obtain(null, MQTTservice.PUBLISH);
            msg.setData(data);
            //  msg.replyTo = serviceHandler;
            try {
                svc.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();

            }
        }
    }
    public class MyAdapter extends ArrayAdapter<String> {

        public MyAdapter(Context ctx, int txtViewResourceId, String[] objects) {
            super(ctx, txtViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
            return getCustomView(position, cnvtView, prnt);
        }
        @Override
        public View getView(int pos, View cnvtView, ViewGroup prnt) {
            return getCustomView(pos, cnvtView, prnt);
        }
        public View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View mySpinner = inflater.inflate(R.layout.custom_spinner, parent, false);
            TextView main_text = (TextView) mySpinner.findViewById(R.id.text_main_seen);
            main_text.setText(choices[position]);
/*
            TextView subSpinner = (TextView) mySpinner.findViewById(R.id.sub_text_seen);
            subSpinner.setText(choices_desc[position]);

            ImageView left_icon = (ImageView) mySpinner.findViewById(R.id.left_pic);
            left_icon.setImageResource(choices_icons[position]);*/

            return mySpinner;
        }
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
            AlertDialog.Builder builder = new AlertDialog.Builder(NewWishActivity.this);
            builder.setTitle("Location is not enabled");  // GPS not found
            builder.setMessage("Do you want to enable location ?"); // Want to enable?
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
            builder.setNegativeButton("No", null);
            builder.create().show();
            return true;
        }
        else{
            return false;
        }
    }

}
