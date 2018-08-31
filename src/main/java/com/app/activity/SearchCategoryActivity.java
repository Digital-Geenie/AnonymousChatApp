package com.app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.constants.GeenieConstants;
import com.app.geenie.R;
import com.google.api.client.testing.util.TestableByteArrayOutputStream;

public class SearchCategoryActivity extends Activity {

    private Spinner categories;
    private String[] choices = {"Select A Topic", "Travel", "Shopping", "Food"};
    private Integer[] choices_icons = {0, R.drawable.travel, R.drawable.shopping, R.drawable.food};
    private String [] choices_desc = {" ", "Let's carpool", "Let's share the apparels", "Let's have Cuisines"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_category);


        categories = (Spinner) findViewById(R.id.categories);
        Button searchCategories = (Button) findViewById(R.id.search);

        categories.setAdapter(new MyAdapter(this, R.layout.custom_spinner, choices));

        searchCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    Intent intent = new Intent(SearchCategoryActivity.this, SearchResultActivity.class);
                    intent.putExtra("selectedCategory", categories.getSelectedItem().toString());
                    startActivity(intent);
                }
                else{
                    Toast.makeText(SearchCategoryActivity.this, GeenieConstants.INTERNET_UNAVAILABLE, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(SearchCategoryActivity.this, UserActivity.class);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
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
            main_text.setText(choices[position]);/*

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
}