package com.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.app.constants.GeenieConstants;
import com.app.geenie.R;

public class CurrentSubscriptionsActivity extends Activity {

    private String[] options = {"Travel", "Shopping", "Food"};
    SharedPreferences sharedPreferences;
    private ProgressDialog pd = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_subscriptions);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        ListView listView = (ListView)findViewById(R.id.list);
        MyAdapter adapter = new MyAdapter(this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(isNetworkAvailable()){
                    pd = new ProgressDialog(CurrentSubscriptionsActivity.this);
                    pd.setTitle("Processing...");
                    pd.setMessage("Please wait.");
                    pd.setCancelable(false);
                    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pd.show();

                    RelativeLayout row = (RelativeLayout) view;
                ToggleButton toggleButton = (ToggleButton) row.getChildAt(1);
                String topic = ((TextView)view.findViewById(R.id.textViewName)).getText().toString();

                if (toggleButton.isChecked()) {
                    toggleButton.setChecked(false);
                    sharedPreferences.edit().putBoolean(topic, false).apply();
                } else {
                    toggleButton.setChecked(true);
                    sharedPreferences.edit().putBoolean(topic, true).apply();
                }
            }
                else {
                    Toast.makeText(CurrentSubscriptionsActivity.this, GeenieConstants.INTERNET_UNAVAILABLE, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_current_subscriptions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return id == R.id.action_settings || super.onOptionsItemSelected(item);

    }

    class MyAdapter extends BaseAdapter{

        private LayoutInflater layoutInflater;

        public MyAdapter(CurrentSubscriptionsActivity activity) {
            layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return options.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView==null) {
                convertView = layoutInflater.inflate(R.layout.listview_item_row_subscriptions, null);
            }

                TextView topic = (TextView) convertView.findViewById(R.id.textViewName);
                ToggleButton status = (ToggleButton) convertView.findViewById(R.id.toggleButton);

            topic.setText(options[position]);
            status.setChecked(sharedPreferences.getBoolean(options[position], false));

            return convertView;
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
