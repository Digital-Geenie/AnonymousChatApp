package com.app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.app.constants.GeenieConstants;
import com.app.geenie.R;

public class SearchActivity extends Activity implements OnItemSelectedListener{

    Spinner categories;

    ImageView travelImage, shoppingImage, foodImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        travelImage = (ImageView)findViewById(R.id.travelImage);
        shoppingImage = (ImageView)findViewById(R.id.shoppingImage);
        foodImage = (ImageView)findViewById(R.id.foodImage);

        travelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()) {
                    Intent intent = new Intent(getBaseContext(), SearchResultActivity.class);
                    intent.putExtra("selectedCategory", "Travel");
                    startActivity(intent);
                }
                else{
                    Toast.makeText(SearchActivity.this, GeenieConstants.INTERNET_UNAVAILABLE, Toast.LENGTH_SHORT).show();
                }

            }
        });

        shoppingImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()) {
                    Intent intent = new Intent(getBaseContext(), SearchResultActivity.class);
                    intent.putExtra("selectedCategory", "Shopping");
                    startActivity(intent);
                }
                else{
                    Toast.makeText(SearchActivity.this, GeenieConstants.INTERNET_UNAVAILABLE, Toast.LENGTH_SHORT).show();
                }
            }
        });

        foodImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    Intent intent = new Intent(getBaseContext(), SearchResultActivity.class);
                    intent.putExtra("selectedCategory", "Food");
                    startActivity(intent);
                }
                else{
                    Toast.makeText(SearchActivity.this, GeenieConstants.INTERNET_UNAVAILABLE, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }
    @Override
    public void onStop(){
        super.onStop();
    }

    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(SearchActivity.this, UserActivity.class);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        categories.setSelection(position);
        String selState = (String) categories.getSelectedItem();

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_actionbar, menu);
        return true;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return (wifi.isConnected() || mobile.isConnected());
    }
}
